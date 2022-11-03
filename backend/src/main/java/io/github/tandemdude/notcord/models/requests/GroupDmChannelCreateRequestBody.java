package io.github.tandemdude.notcord.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class GroupDmChannelCreateRequestBody {
    private @NotNull String name;
    private @NotEmpty @Valid Set<@Pattern(regexp = "[1-9][0-9]+", message = "'${validatedValue}' is not a valid snowflake") String> recipientIds;
}
