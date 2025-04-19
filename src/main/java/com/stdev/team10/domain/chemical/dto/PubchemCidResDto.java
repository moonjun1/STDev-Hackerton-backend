package com.STDev.team10.domain.chemical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubchemCidResDto {

    @JsonProperty("PropertyTable")
    private PropertyTable PropertyTable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyTable {
        @JsonProperty("Properties")
        private List<Property> Properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {

        @JsonProperty("CID")
        private String CID;

        @JsonProperty("MolecularFormula")
        private String MolecularFormula;
    }
}