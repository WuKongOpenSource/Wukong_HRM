package com.kakarote.core.utils;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.Const;
import com.kakarote.core.common.enums.SystemCodeEnum;
import com.kakarote.core.exception.CrmException;

/**
 * login password *
 * @author yzh
 */
public class PwdUtil {
    /**
     * *
     *
     * @param type          0.不限 ,1.必须包含字母+数字组合 , 2.必须包含大写字母+小写字母+数字组合,3.必须包含字母+特殊字符+数字组合,4.必须包含大写字母+小写字母+特殊字符+数字组合
     * @param password
     * @param minimumPwdLen 密码最小长度
     */
    public static void checkPwdReg(Integer type, String password, Integer minimumPwdLen) {
        //校验强度校验
        if (ObjectUtil.equal(0, type)) {
            if (StrUtil.length(password) < minimumPwdLen) {
                //密码长度不符合要求
                throw new CrmException(SystemCodeEnum.PASSWORD_CHECK_ERROR);
            }
        } else if (ObjectUtil.equal(1, type)) {
            if (!ReUtil.isMatch(Const.PASS_PASSWORD_WITH_NUMBER_LETTER + minimumPwdLen + Const.PASSWORD_REG_SUFFIX,
                    password)) {
                throw new CrmException(SystemCodeEnum.PASSWORD_CHECK_ERROR);
            }

        } else if (ObjectUtil.equal(2, type)) {
            if (!ReUtil.isMatch(Const.PASS_PASSWORD_WITH_NUMBER_UPPER_LETTER + minimumPwdLen + Const.PASSWORD_REG_SUFFIX, password)) {
                throw new CrmException(SystemCodeEnum.PASSWORD_CHECK_ERROR);
            }

        } else if (ObjectUtil.equal(3, type)) {
            if (!ReUtil.isMatch(Const.PASS_PASSWORD_WITH_NUMBER_LETTER_CHAR + minimumPwdLen + Const.PASSWORD_REG_SUFFIX, password)) {
                throw new CrmException(SystemCodeEnum.PASSWORD_CHECK_ERROR);
            }

        } else if (ObjectUtil.equal(4, type)) {
            if (!ReUtil.isMatch(Const.PASS_PASSWORD_WITH_NUMBER_UPPER_LETTER_CHAR + minimumPwdLen + Const.PASSWORD_REG_SUFFIX, password)) {
                throw new CrmException(SystemCodeEnum.PASSWORD_CHECK_ERROR);
            }
        }
    }

}