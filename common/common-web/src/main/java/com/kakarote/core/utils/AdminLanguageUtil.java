package com.kakarote.core.utils;


import cn.hutool.core.util.StrUtil;

/**
 * 语言包处理工具类
 */
public class AdminLanguageUtil {

    /**
     * 功能描述: <br>
     * 〈信息处理〉
     * @param: msg
     * @return: java.lang.String
     * @throws:
     * @author: zyh
     * @date: 2022/8/23 11:30
     */
    public static String  repalceMsg(String msg){
        if(StrUtil.isBlank(msg)){
            return msg;
        }
        if(msg.contains("【")){
            msg = msg.replace("【","");
        }
        if(msg.contains("】")){
            msg = msg.replaceAll("】","");
        }
        if(msg.contains("%s")){
            msg = msg.replaceAll("%s","");
        }
        return msg;
    }
}
