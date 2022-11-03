package io.github.tandemdude.notcord.models.utility;

import lombok.Data;

@Data
public class ResultContainer<T> {
    private final String error;
    private final T result;

    public boolean isError() {
        return error != null;
    }

    public boolean isSuccess() {
        return !isError();
    }
}
