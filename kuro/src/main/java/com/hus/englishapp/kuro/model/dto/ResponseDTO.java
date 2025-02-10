package com.hus.englishapp.kuro.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ResponseDTO {
    private Integer code;

    private String msg;

    Map<String, String> failureMap;

    private JsonNode data;

    private Meta meta;

    @Data
    @Builder
    public static class Meta {
        long total;

        int page;

        @JsonProperty("page_of_number")
        int pageOfNumber;
    }
}
