package com.kakarote.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kakarote.core.common.Const.SEPARATOR;

/**
 * 字符串分割
 */
public class SeparatorUtil {

    public static Set<Integer> toSet(String tagStr){
        Set<Integer> tag=new HashSet<>();
        if(null==tagStr){
            return tag;
        }
        for (String str : tagStr.split(SEPARATOR)) {
            if(StrUtil.isEmpty(str)){
                continue;
            }
            tag.add(Integer.valueOf(str));
        }
        return tag;
    }

    public static Set<Long> toSetL(String tagStr){
        Set<Long> tag=new HashSet<>();
        if(null==tagStr){
            return tag;
        }
        for (String str : tagStr.split(SEPARATOR)) {
            if(StrUtil.isEmpty(str)){
                continue;
            }
            tag.add(Long.valueOf(str));
        }
        return tag;
    }

    public static Set<Long> toLongSet(String tagStr){
        Set<Long> tag=new HashSet<>();
        if(StrUtil.isEmpty(tagStr)){
            return tag;
        }
        for (String str : tagStr.split(SEPARATOR)) {
            if(StrUtil.isEmpty(str)){
                continue;
            }
            tag.add(Long.valueOf(str));
        }
        return tag;
    }



    public static String fromSet(Collection<Integer> tag){
        if(CollectionUtil.isEmpty(tag)){
            return "";
        }
        StringBuilder sb=new StringBuilder(SEPARATOR);
        for (Integer integer : tag) {
            if(integer==null){
                continue;
            }
            sb.append(integer).append(SEPARATOR);
        }
        return sb.toString();
    }

    public static String fromLongSet(Collection<Long> tag){
        if(CollectionUtil.isEmpty(tag)){
            return "";
        }
        StringBuilder sb=new StringBuilder(SEPARATOR);
        for (Long integer : tag) {
            if(integer==null){
                continue;
            }
            sb.append(integer).append(SEPARATOR);
        }
        return sb.toString();
    }

    public static String fromString(String tagStr){
        if(StrUtil.isEmpty(tagStr)){
            return "";
        }
        StringBuilder sb=new StringBuilder();
        if(!tagStr.substring(0,1).equals(SEPARATOR)){
            sb.append(SEPARATOR);
        }
        sb.append(tagStr);
        if(!tagStr.substring(tagStr.length()-1).equals(SEPARATOR)){
            sb.append(SEPARATOR);
        }
        return sb.toString();
    }

    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";

    public static String replaceChinese(String str, String replacement){
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        return str.replaceAll(REGEX_CHINESE, replacement);
    }

    public static String REGEX_OPERATOR = "[+|-]";

    public static String FINANCE_CASH_FLOW_STATEMENT_REGEX_EXPRESSION = "([A-Z]{2}\\[.*?\\])|([A-Z]{2}[0-9]{0,2})";

    public static String GET_STR_BETWEEN_BRACKET_REGEX_EXPRESSION = "(?<=\\[)(\\S+)(?=\\])";

    public static List<String> parseParams(String formula) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(FINANCE_CASH_FLOW_STATEMENT_REGEX_EXPRESSION);
        Matcher matcher = pattern.matcher(formula);
        while (matcher.find()) {
            String s = matcher.group(0);
            if (!CollUtil.contains(result, s)) {
                result.add(s);
            }
        }
        return result;
    }

    public static List<String> parseStrBetweenBracket(String exp) {
		List<String> result = new ArrayList<>();
		Pattern pattern = Pattern.compile(GET_STR_BETWEEN_BRACKET_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(exp);
		while (matcher.find()) {
			result.add(matcher.group(0));
		}
		return result;
	}
}
