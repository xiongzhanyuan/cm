package com.xzy.cm.common.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VOHelper {

    protected static final DecimalFormat df = new DecimalFormat("#.0");
    /**
     * 从data文件中读取信息
     *
     * @param path
     * @param <T>
     * @return
     */
    public static <T> List<T> jo2list(String path, Class<T> classType) throws Exception {
        if (StringUtils.isBlank(path)) {
            throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
        }
        List<String> jsons = IOHelper.readMultiFileByChar(path, "utf-8");
        List<T> list = new ArrayList<>();
        for (String json : jsons) {
            JSONObject jo = JSON.parseObject(json);
            List<T> clist = JOHelper.jo2list(jo.getJSONArray("data"), classType);
            list.addAll(clist);
        }
        return list;
    }

    /**
     * 时间随机
     */
    public static Long random(Long start, Long end) {
        Integer randomInt = 0;

        Integer obj1 = 5;
        Integer obj2 = 1;
        while (randomInt > obj1 || randomInt < obj2) {
            Double random = Math.random();
            String rand = df.format(random);
            random = Double.parseDouble(rand);
            randomInt = (int)(random * 10);
        }
        Double randomDouble = randomInt/100.0 * (end - start);
        Long rtn = start + randomDouble.longValue();
        if (rtn.intValue() == start.intValue() || rtn.intValue() == end.intValue()) {
            return random(start, end);
        }
        return rtn;
    }
}
