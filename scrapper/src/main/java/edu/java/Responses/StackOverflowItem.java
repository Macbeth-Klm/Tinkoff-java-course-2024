package edu.java.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowItem(
    @JsonProperty("items")
    List<StackOverflowResponse> answers
) {
}
/*
Из-за нестандартной структуры JSON-файла, полученного из StackOverflow API пришлось
создать новый класс, который содержит в себе "items: {}, {}..." иначе я не знаю, как
парсить подобного рода файла с помощью ParameterizedTypeReference<>{} без отдельного
метода с ObjectMapper и обходом по дереву.
 */
