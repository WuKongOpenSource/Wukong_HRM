package com.kakarote.core.entity;

import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.entity.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zjj
 * @date: 2021-05-20 18:13
 */
@Data
@ApiModel(value = "消息发送求送", description = "消息发送求送")
public class MsgBodyBO {
	@ApiModelProperty(value = "消息 KEY")
	private String msgKey;

	@ApiModelProperty(value = "消息 TAG")
	private String msgTag;

	@ApiModelProperty(value = "动作")
	private String action;

	@ApiModelProperty(value = "当前用户")
	private UserInfo currentUser;

	@ApiModelProperty(value = "操作对象")
	private List<JSONObject> operateObject;

	@ApiModelProperty(value = "团队成员")
	private List<Long> memberIds = new ArrayList<>();

	@ApiModelProperty(value = "消息标题")
	private String title;

	@ApiModelProperty(value = "服务端发送消息时间")
	private Long delayTime;

	@ApiModelProperty("要推送的用户ID")
	private Set<Long> userIds;
}
