package com.xzy.cm.common.helper;

import org.apache.log4j.Logger;

import java.util.*;

public class GroupHelper {
    /**
     * 日志
     */
    private static final Logger log = Logger.getLogger("SERVICE");

    /**
     *
     * @param <T>
     */
    public interface GroupBy<T> {
        T groupby(Object obj);
    }

    /**
     *
     * @param colls
     * @param gb
     * @param <T>
     * @param <D>
     * @return
     */
    public static final <T extends Comparable<T>, D> Map<T, List<D>> group(Collection<D> colls, GroupBy<T> gb) {
        if (colls == null || colls.isEmpty()) {
            log.error("分组集合不能为空!");
            return null;
        }
        if (gb == null) {
            log.error("分组依据不能为Null!");
            return null;
        }
        Iterator<D> iter = colls.iterator();
        Map<T, List<D>> map = new HashMap<>();
        while (iter.hasNext()) {
            D d = iter.next();
            T t = gb.groupby(d);
            if (map.containsKey(t)) {
                map.get(t).add(d);
            } else {
                List<D> list = new ArrayList<>();
                list.add(d);
                map.put(t, list);
            }
        }
        return map;
    }
}
