package edu.java.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class LinkUpdate {
    @Min(1)
    private Long id;
    @NotEmpty
    private String url;
    @NotEmpty
    private String description;
    @NotEmpty
    private List<Long> tgChatIds;
}
