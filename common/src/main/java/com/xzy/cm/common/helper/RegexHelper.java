package com.xzy.cm.common.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
    /**
     * 判断账号是否为手机号,根据是否带国家号做判断
     * @param account
     * @return
     */
    public static boolean isPhone(String account) {
        Pattern p = Pattern.compile("\\+[1-9]\\d*\\ [0-9]\\d*");
        Matcher matcher = p.matcher(account);
        return matcher.matches();
    }

    /**
     * 判断账号是否为邮箱
     * @param account
     * @return
     */
    public static boolean isEmail(String account) {

        Pattern p = Pattern.compile("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
        Matcher m = p.matcher(account);
        return m.matches();
    }

    /**
     * 判断是否为国内手机号
     * @param phoneNum
     * @return
     */
    public static boolean isChinaNum(String phoneNum) {
        boolean result = Boolean.FALSE;
        if (phoneNum.startsWith("+86 ")) {
            result = Boolean.TRUE;
        }
        return result;
    }
}
