package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class GroupDmChannelCreateRequestBody {
    private @NotNull String name;
    private @NotEmpty Set<String> recipientIds;
}
