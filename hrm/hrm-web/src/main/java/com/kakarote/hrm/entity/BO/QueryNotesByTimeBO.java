package com.kakarote.hrm.entity.BO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QueryNotesByTimeBO {

    private LocalDate time;

    @Override
    public String toString() {
        return "QueryNotesByTimeBO{" +
                "time=" + time +
                '}';
    }
}
