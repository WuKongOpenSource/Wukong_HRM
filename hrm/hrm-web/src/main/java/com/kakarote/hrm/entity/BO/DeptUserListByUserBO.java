package com.kakarote.hrm.entity.BO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeptUserListByUserBO {

    public List<Long> employeeIdList;

    private List<Long> deptIdList;

    @Override
    public String toString() {
        return "DeptUserListByUserBO{" +
                "employeeIdList=" + employeeIdList +
                ", deptIdList=" + deptIdList +
                '}';
    }
}
