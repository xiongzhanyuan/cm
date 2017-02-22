package com.xzy.cm.mybatis.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.helper.JOHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanda on 4/14/16.
 * 将下划线转换为驼峰规则
 */
public class CameHumpHelper {

    public static final char UNDERLINE = '_';

    /**
     * 将查询出来的数据list进行驼峰规则转换
     *
     * @param list
     * @param clazz
     * @param <T>
     * @return
     * @throws Throwable
     */
    public static <T> List<T> process(List<Map<String, Object>> list, Class<T> clazz) {
        //先执行，后处理
        List<T> lists = new ArrayList<>();
        T t = null;
        for (Object object : list) {
            if (object instanceof Map) {
                processMap((Map) object);
            } else {
                continue;
            }
            t = JSON.parseObject(JSON.toJSONString(object), clazz);
            lists.add(t);
        }
        return lists;
    }

    /**
     * 将查询出来的数据list进行驼峰规则转换
     *
     * @param list
     * @param clazz
     * @param <T>
     * @return
     * @throws Throwable
     */
    public static <T> List<T> processJO(List<JSONObject> list, Class<T> clazz) {
        //先执行，后处理
        List<T> lists = new ArrayList<>();
        T t = null;
        for (JSONObject object : list) {
            Map map = JSON.parseObject(object.toJSONString(), Map.class);
            processMap(map);
            t = JSON.parseObject(JSON.toJSONString(map), clazz);
            lists.add(t);
        }
        return lists;
    }

    /**
     * 处理单条数据
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     * @throws Throwable
     */
    public static <T> T process(Map<String, Object> obj, Class<T> clazz) {
        //先执行，后处理
        processMap((Map) obj);
        T t = JSON.parseObject(JSON.toJSONString(obj), clazz);
        return t;
    }

    /**
     * 处理简单对象
     *
     * @param map
     */
    private static void processMap(Map map) {
        if (ObjectUtils.isEmpty(map)) {
            return;
        }
        Map cameHumpMap = new HashMap();
        Iterator<Map.Entry> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String cameHumpKey = null;
            if (key.contains("_")) {
                cameHumpKey = underlineToCamelhump(key.toLowerCase());
            } else {
                cameHumpKey = key;
            }
            if (!key.equals(cameHumpKey)) {
                cameHumpMap.put(cameHumpKey, entry.getValue());
                iterator.remove();
            }
        }
        map.putAll(cameHumpMap);
    }

    /**
     * 将下划线风格替换为驼峰风格
     *
     * @param str
     * @return
     */
    public static String underlineToCamelhump(String str) {
        Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param jo
     */
    public static JSONObject processCamelToUnderline(JSONObject jo) {
        if (ObjectUtils.isEmpty(jo)) {
            return null;
        }
        JSONObject cameHumpMap = new JSONObject();
        Iterator<Map.Entry<String, Object>> iterator = jo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            String cameHumpKey = camelToUnderline(key);
            JSONObject result = null;
            if (entry.getValue() instanceof JSONObject) {
                result = processCamelToUnderline(jo.getJSONObject(key));
            }
            List<JSONObject> listJo = null;
            if (entry.getValue() instanceof JSONArray) {
                listJo = new ArrayList<>();
                List<JSONObject> list = JOHelper.jo2list(jo.getJSONArray(key), JSONObject.class);
                for (JSONObject value : list) {
                    JSONObject jsonObject = processCamelToUnderline(value);
                    listJo.add(jsonObject);
                }
            }
            if (!ObjectUtils.isEmpty(result)) {
                cameHumpMap.put(cameHumpKey, result);
            }
            if (CollectionUtils.isNotEmpty(listJo)) {
                cameHumpMap.put(cameHumpKey, listJo);
            }
            if (ObjectUtils.isEmpty(result) && CollectionUtils.isEmpty(listJo)) {
                cameHumpMap.put(cameHumpKey, entry.getValue());
            }
            iterator.remove();
        }
        jo.clear();
        jo.putAll(cameHumpMap);
        return jo;
    }

    /**
     * 驼峰转下划线
     *
     * @param map
     */
    public static void processCamelToUnderline(Map map) {
        if (ObjectUtils.isEmpty(map)) {
            return;
        }
        Map cameHumpMap = new HashMap();
        Iterator<Map.Entry> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String cameHumpKey = camelToUnderline(key);
            cameHumpMap.put(cameHumpKey, entry.getValue());
            iterator.remove();
        }
        map.putAll(cameHumpMap);
    }

    /**
     * 驼峰命名规则转换成下划线
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
