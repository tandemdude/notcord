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

package io.github.tandemdude.notcord.gateway;

import io.github.tandemdude.notcord.gateway.services.GrpcHandlerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NotcordGatewayBackendApp implements CommandLineRunner, DisposableBean {
    private final Server server;

    public NotcordGatewayBackendApp(@Value("${server.port}") int port, GrpcHandlerService handlerService) {
        this.server = ServerBuilder.forPort(port)
            .addService(handlerService)
            .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(NotcordGatewayBackendApp.class);
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        server.awaitTermination();
    }

    @Override
    public void destroy() {
        server.shutdown();
    }
}
