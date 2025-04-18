package com.stdev.team10.global.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChemicalData2 {
    private List<Chemical> chemicals;

    @Getter
    @Setter
    public static class Chemical {
        private String chemical_description_ko;
        private int chemical_id;
        private String chemical_name_en;
        private String chemical_name_ko;
        private String molecular_formula;
        private String education_level;
    }
}