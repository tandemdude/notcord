package io.github.tandemdude.notcord.rest.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class GroupDmChannelCreateRequestBody {
    @NotEmpty
    private String name;
    @Valid
    @NotEmpty
    private Set<@Pattern(regexp = "[1-9][0-9]+", message = "'${validatedValue}' is not a valid snowflake") String> recipientIds;
}
