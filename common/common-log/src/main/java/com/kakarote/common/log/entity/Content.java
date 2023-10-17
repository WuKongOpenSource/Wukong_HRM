package com.kakarote.common.log.entity;


import com.kakarote.common.log.enums.BehaviorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Content {

    private String subModel;

    private String object;

    private String detail;

    private String transDetail;

    private BehaviorEnum behavior;

    public Content(String object, String detail) {
        this.object = object;
        this.detail = detail;
    }

    public Content(String subModel, String object, String detail) {
        this.subModel = subModel;
        this.object = object;
        this.detail = detail;
    }

    public Content(String object, String detail, BehaviorEnum behavior) {
        this.object = object;
        this.detail = detail;
        this.behavior = behavior;
    }

    public Content(String object, String detail, String transDetail, BehaviorEnum behavior) {
        this.object = object;
        this.detail = detail;
        this.behavior = behavior;
        this.transDetail = transDetail;
    }
}
