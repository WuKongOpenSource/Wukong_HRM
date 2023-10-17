package com.kakarote.core.common;

import java.io.Serializable;

/**
 * 默认配置常量信息
 *
 * @author zhangzhiwei
 */
public class Const implements Serializable {

    /**
     * 项目版本
     */
    public static final String PROJECT_VERSION = "12.3.6";

    /**
     * 默认分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 查询数据权限递归次数,可以通过继承这个类修改
     */
    public static final int AUTH_DATA_RECURSION_NUM = 20;

    /**
     * 业务token在header中的名称
     */
    public static final String TOKEN_NAME = "Admin-Token";

    /**
     * 默认的token名称
     */
    public static final String DEFAULT_TOKEN_NAME = "AUTH-TOKEN";

    /**
     * 默认编码
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * sql最大查询条数限制,以及字段数量限制
     */
    public static final Long QUERY_MAX_SIZE = 100L;

    /**
     * PC端登录的userKey
     */
    public static final String USER_TOKEN = "WK:USER:TOKEN:";

    /**
     * 临时token前缀
     */
    public static final String TEMP_TOKEN_PREFIX = "TEMP:";

    /**
     * 用户token的最长过期时间
     */
    public static final Integer MAX_USER_EXIST_TIME = 3600 * 24 * 7;

    /**
     * 批量保存的条数
     */
    public static final int BATCH_SAVE_SIZE = 200;


    /**
     * 默认的企业人数
     */
    public static final Integer DEFAULT_COMPANY_NUMBER = 999;

    public static final String DEFAULT_DOMAIN = "www.72crm.com";

    /**
     * 用户名称缓存key
     */
    public static final String ADMIN_USER_NAME_CACHE_NAME = "ADMIN:USER:CACHE:";

    /**
     * 部门名称缓存key
     */
    public static final String ADMIN_DEPT_NAME_CACHE_NAME = "ADMIN:DEPT:CACHE:";

    /**
     * 企业安全配置缓存key 前缀
     */
    public static final String COMPANY_SECURITY_CONFIG_KEY="COMPANY:SECURITY:CONFIG:";

    /**
     * PC端登录的userKey
     */
    public static final String MULT_DEVICE_USER_TOKEN = "MULT:DEVICE:USER:TOKEN:";

    /**
     * 用户上次登录时间缓存key
     */
    public static final String USER_LOGIN_LASTED_TIME = "USER:LOGIN:LASTED:TIME:";

    /**
     * 移动端上次登录信息缓存
     */
    public static final String USER_LOGIN_LASTED_TIME_MOBILE = "USER:LOGIN:LASTED:TIME:MOBILE:";

    /**
     * 密码强度正则  大写字母、小写字母、数字、特殊符号
     */
    public static final String PASS_PASSWORD_WITH_NUMBER_UPPER_LETTER_CHAR="^(?![0-9A-Za-z]+$)(?![0-9A-Z\\W]+$)(?![0-9a-z\\W]+$)(?![A-Za-z\\W]+$)[0-9A-Za-z~!@#$%^&*()_+`\\-={}|\\[\\]\\\\:\";'<>?,./]{";
    /* "/*8,16}$";*/

    /**
     *密码强度正则   字母加数字
     */
    public static final String PASS_PASSWORD_WITH_NUMBER_LETTER="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{";

    /**
     * 密码强度正则  大小写字母加数字
     */
    public static final String PASS_PASSWORD_WITH_NUMBER_UPPER_LETTER="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{";
    /**
     *密码强度正则   字母 数字 特殊字符
     */
    public static final String PASS_PASSWORD_WITH_NUMBER_LETTER_CHAR="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])(?=.{8,})[A-Za-z\\d\\W]{";

    /**
     * 密码强度正则后缀
     */
    public static final String PASSWORD_REG_SUFFIX=",20}$";

    public static final String NO_AUTH_MENU_CACHE_KEY = "USER_NO_AUTH_URL:";

    public static final String TOKEN_CACHE_NAME = "_token";

}

