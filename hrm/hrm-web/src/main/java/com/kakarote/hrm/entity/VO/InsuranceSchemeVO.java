package com.kakarote.hrm.entity.VO;

import com.kakarote.hrm.entity.BO.AddInsuranceSchemeBO;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InsuranceSchemeVO extends AddInsuranceSchemeBO {

    public Map<String, String> languageKeyMap;

    @Override
    public String toString() {
        return super.toString();
    }
}
