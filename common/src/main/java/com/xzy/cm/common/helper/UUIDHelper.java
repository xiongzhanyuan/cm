package com.xzy.cm.common.helper;

import java.util.UUID;

public class UUIDHelper {
    /**
     * 获取uuid
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
