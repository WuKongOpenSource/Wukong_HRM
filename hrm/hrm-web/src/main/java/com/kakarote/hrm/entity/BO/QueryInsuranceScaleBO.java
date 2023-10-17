package com.kakarote.hrm.entity.BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryInsuranceScaleBO {

    private String cityId;

    private String hujiId;

    @Override
    public String toString() {
        return "QueryInsuranceScaleBO{" +
                "cityId='" + cityId + '\'' +
                ", hujiId='" + hujiId + '\'' +
                '}';
    }
}
