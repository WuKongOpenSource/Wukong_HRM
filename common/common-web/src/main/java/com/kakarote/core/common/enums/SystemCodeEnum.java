package com.kakarote.core.common.enums;

import com.kakarote.core.common.ResultCode;

/**
 * @author zhangzhiwei
 * 系统响应错误代码枚举类
 */

public enum SystemCodeEnum implements ResultCode {
    //系统响应成功
    SYSTEM_OK(0, "success"),
    //未捕获的错误
    SYSTEM_ERROR(500, "网络错误，请稍候再试"),

    SYSTEM_NOT_LOGIN(302, "请先登录！"),
    //拒绝访问
    SYSTEM_BAD_REQUEST(403, "请求频率过快,请稍后再试"),
    //无权访问
    SYSTEM_NO_AUTH(401, "无权操作"),
    //资源未找到
    SYSTEM_NO_FOUND(404, "资源未找到"),
    //资源未找到
    SYSTEM_NO_VALID(400, "参数验证错误"),
    //请求方式错误
    SYSTEM_METHOD_ERROR(405, "请求方式错误"),
    //请求超时
    SYSTEM_REQUEST_TIMEOUT(408, "请求超时"),
    //服务调用异常
    SYSTEM_SERVER_ERROR(409, "服务调用异常"),
    //企业信息已到期
    SYSTEM_SERVER_EXPIRE_ERROR(410, "企业用户已到期!"),
    //企业信息已到期
    SYSTEM_NO_SUCH_PARAMENT_ERROR(411, "参数不存在!"),
    //上传文件失败
    SYSTEM_UPLOAD_FILE_ERROR(412, "文件上传失败!"),
    //企业信息不完善
    COMPANY_INFO_ERROR(413, "企业信息不完善!"),

    //发送消息失败
    SEND_MSG_FAILED(414, "发送消息失败"),

    //企业用户不存在
    COMPANY_USER_INFO_ERROR(415, "企业用户不存在!"),

    WORK_NO_TIME_VALID(1008, "开始时间结束时间至少要有一个有值"),

    BOARD_ERROR(1009, "初始看板不能删除"),
    PASSWORD_CHECK_ERROR(1330, "密码规则校验失败，请按要求修改后重试")


    ;

    SystemCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public static SystemCodeEnum parse(Integer status) {
        for (SystemCodeEnum value : values()) {
            if (value.getCode() == status) {
                return value;
            }
        }
        return SystemCodeEnum.SYSTEM_ERROR;
    }
}
