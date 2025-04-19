package com.stdev.team10.domain.chemical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PubchemDescriptionResDto {

    @JsonProperty("InformationList")
    private InformationListDto InformationList;

    @Getter
    @Setter
    public static class InformationListDto {
        @JsonProperty("Information")
        private List<InformationDto> Information;
    }

    @Getter
    @Setter
    public static class InformationDto {
        @JsonProperty("CID")
        private Integer CID;
        @JsonProperty("Description")
        private String Description;
        @JsonProperty("DescriptionSourceName")
        private String DescriptionSourceName;
        @JsonProperty("DescriptionURL")
        private String DescriptionURL;
    }
}
