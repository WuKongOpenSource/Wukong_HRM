package com.kakarote.hrm.entity.BO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QueryNotesStatusBO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    @Override
    public String toString() {
        return "QueryNotesStatusBO{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
