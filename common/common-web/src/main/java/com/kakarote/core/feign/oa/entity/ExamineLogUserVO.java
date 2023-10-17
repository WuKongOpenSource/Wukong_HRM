package com.kakarote.core.feign.oa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ExamineLogUserVO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    private Integer examineStatus;

    private String realname;

    private Long userId;

    private String img;

    private ExamineUserVO examineUser;

    @Override
    public String toString() {
        return "ExamineLogUserVO{" +
                "examineTime=" + examineTime +
                ", examineStatus=" + examineStatus +
                ", realname='" + realname + '\'' +
                ", userId=" + userId +
                ", img='" + img + '\'' +
                ", examineUser=" + examineUser +
                '}';
    }

    @Builder
    public static class ExamineUserVO{
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date examineTime;

        private Integer examineStatus;

        private String realname;

        private Long userId;

        private String img;

        @Override
        public String toString() {
            return "ExamineUserVO{" +
                    "examineTime=" + examineTime +
                    ", examineStatus=" + examineStatus +
                    ", realname='" + realname + '\'' +
                    ", userId=" + userId +
                    ", img='" + img + '\'' +
                    '}';
        }
    }
}

