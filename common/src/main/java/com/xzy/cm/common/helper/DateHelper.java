package com.xzy.cm.common.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;

public class DateHelper {
    private static final SimpleDateFormat sdf_ss = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private static final SimpleDateFormat sdf_dd = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat sdf_mm = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private static final SimpleDateFormat sdf_hh = new SimpleDateFormat("yyyy.MM.dd HH");
    private static final SimpleDateFormat sdf_hh_mm_ss = new SimpleDateFormat("hh:mm:ss");
    private static SimpleDateFormat format;

    /**
     * 精确到秒
     *
     * @param time
     * @return
     */
    public static String convertSecond(Long time) {
        if (ObjectUtils.isEmpty(time)) {
            return null;
        }
        return sdf_ss.format(time);
    }

    /**
     * 精确到分
     *
     * @param time
     * @return
     */
    public static String convertMinute(Long time) {
        if (ObjectUtils.isEmpty(time)) {
            return null;
        }
        return sdf_mm.format(time);
    }

    /**
     * 精确到天
     *
     * @param time
     * @return
     */
    public static String convertDay(Long time) {
        if (ObjectUtils.isEmpty(time)) {
            return null;
        }
        return sdf_dd.format(time);
    }

    /**
     * 精确到小时
     *
     * @param time
     * @return
     */
    public static String convertHour(Long time) {
        if (ObjectUtils.isEmpty(time)) {
            return null;
        }
        return sdf_hh.format(time);
    }

    /**
     * 任意格式的format
     *
     * @param time
     * @return
     */
    public static String convert(Long time, String format) {
        if (ObjectUtils.isEmpty(time) || StringUtils.isBlank(format)) {
            return null;
        }
        DateHelper.format = new SimpleDateFormat(format);
        return DateHelper.format.format(time);
    }
}
