package io.github.tandemdude.notcord.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class MultiStatusResponse<T> {
    private final List<T> results;
}
