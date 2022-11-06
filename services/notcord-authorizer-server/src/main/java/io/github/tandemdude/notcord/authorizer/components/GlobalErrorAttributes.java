/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tandemdude.notcord.authorizer.components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

/*
Errors to verify are handled correctly:
[x] validation constraints in body
[ ] validation constraints in headers/path
[ ] pass an invalid cookie and see what happens
[x] use the wrong type in the body for a valid field
[x] miss out a required field
[x] add an unrecognised field
[x] omit the body where it is expected
[x] use an unknown encoding in content type
[x] use an unknown encoding in accept
[x] use an unknown content type
[x] use an unknown accept
[x] use an unknown accept-encoding  // TODO - Should this error?
[x] json parsing - parse a value too wide to fit in an int type
[x] malformed json - missing brace
[x] malformed json - missing quote
[x] malformed json - two commas
[x] malformed json - dangling comma
[x] authentication - no authorization header
[x] authentication - authorization header with no prefix
[x] authentication - authorization header with unsupported prefix
[x] authentication - authorization header with unrecognised prefix
[x] authentication - zero length token
[x] authentication - garbage data for the token
[x] authorization - bad authorities or claims
[x] authorization - expired token
[x] method - disallowed method verb
[x] method - unknown method verb  // TODO - returns a 500 - maybe we should change that?
[x] url - http or https when not expected
[x] url - path doesnt exist
[x] url - path traversal - /foo/bar/../../application.yaml
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorAttributes.class);
    private static final ProblemDetail defaultProblemDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(500));

    private static final Map<Class<? extends Annotation>, String> annotationNameMap = Map.of(
        RequestBody.class, "Body",
        RequestHeader.class, "Header",
        RequestParam.class, "Parameter",
        PathVariable.class, "Path"
    );

    private final ObjectMapper objectMapper;

    public GlobalErrorAttributes(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler
    public ProblemDetail onWebExchangeBindException(WebExchangeBindException ex) {
        // Attempt to read the parameter annotations in order to see what type of parameter couldn't be validated
        Optional<String> parameterType = ex.getMethodParameter() == null ? Optional.empty()
            : Arrays.stream(ex.getMethodParameter().getParameterAnnotations())
            .map(Annotation::annotationType)
            .filter(annotationNameMap::containsKey)
            .map(annotationNameMap::get)
            .findFirst();

        var problemDetail = ProblemDetail.forStatus(ex.getStatusCode());
        // Set the detail to specify the parameter type if we could extract it from the parameter
        problemDetail.setDetail(parameterType.isEmpty() ? ex.getBody()
            .getDetail() : parameterType.get() + " validation failure");

        // We use a TreeMap here to ensure that ordering of errors in the ProblemDetails object is consistent
        var errorMap = new TreeMap<String, Set<String>>(String::compareToIgnoreCase);
        ex.getFieldErrors().stream()
            .filter(error -> error.getDefaultMessage() != null)
            .forEach(error -> errorMap.computeIfAbsent(
                    error.getField(),
                    unused -> new TreeSet<>(String::compareToIgnoreCase)
                )
                .add(error.getDefaultMessage()));

        // Add the validation errors as a new property on the ProblemDetails object
        problemDetail.setProperty("errors", errorMap);
        return problemDetail;
    }

    public ProblemDetail onResponseStatusException(ResponseStatusException ex, ServerRequest request) {
        // Make sure we don't show details for 5xx errors as they may contain a stacktrace
        if (!ex.getStatusCode().is5xxServerError()) {
            var details = ProblemDetail.forStatus(ex.getStatusCode());
            if (ex.getReason() != null) {
                details.setDetail(ex.getReason());
            }
            details.setInstance(URI.create(request.path()));
            return details;
        }

        return ProblemDetail.forStatus(ex.getStatusCode());
    }

    public ProblemDetail onUnsupportedMediaTypeException(UnsupportedMediaTypeStatusException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(415),
            ex.getReason() == null ? "Unsupported media type" : ex.getReason()
        );
    }

    public ProblemDetail onException(Throwable ex) {
        // TODO - add exception publishing ?somewhere? and return a reference to identify it?
        LOGGER.error("An exception occurred but was not handled (500 status returned)", ex);
        return defaultProblemDetail;
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions errorAttributeOptions) {
        Throwable exc = getError(request);
        ProblemDetail problemDetail;

        if (exc instanceof UnsupportedMediaTypeStatusException ex) {
            problemDetail = onUnsupportedMediaTypeException(ex);
        } else if (exc instanceof WebExchangeBindException ex) {
            problemDetail = onWebExchangeBindException(ex);
        } else if (exc instanceof ResponseStatusException ex) {
            problemDetail = onResponseStatusException(ex, request);
        } else {
            problemDetail = onException(exc);
        }

        return objectMapper.convertValue(problemDetail, new TypeReference<>() {});
    }
}
