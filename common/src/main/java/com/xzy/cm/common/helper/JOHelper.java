package com.xzy.cm.common.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class JOHelper {
    /**
     * json转换为class
     *
     * @param jo
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jo2class(JSONObject jo, Class<T> clazz) {
        T t = JSON.parseObject(jo.toJSONString(), clazz);
        return t;
    }

    /**
     * jsonarray 转换为list
     *
     * @param array
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jo2list(JSONArray array, Class<T> clazz) {
        List<T> list = JSON.parseArray(array.toJSONString(), clazz);
        return list;
    }

    /**
     * obj 2 json
     *
     * @param obj
     * @return
     */
    public static JSONObject obj2Json(Object obj) throws BussinessException {
        if (ObjectUtils.isEmpty(obj)) {
            throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
        }
        return JSON.parseObject(JSON.toJSONString(obj));
    }

    /**
     * @param args
     *         key-value 看起来必须是偶数，对吧
     * @return
     */
    public static JSONObject gen(Object... args) {
        JSONObject ret = new JSONObject();
        for (int i = 0, len = args.length; i < len; i += 2) {
            if (args[i + 1] == null) {
                ret.remove(args[i].toString());
            } else {
                ret.put(args[i].toString(), args[i + 1]);
            }
        }
        return ret;
    }

    /**
     * @param jo
     * @return
     */
    public static boolean isEmpty(JSONObject jo) {
        if (jo == null) {
            return true;
        }
        if (jo.keySet().size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean hasIgnoreCase(String key, JSONObject vo) {
        if (isEmpty(vo)) {
            return false;
        }
        if (StringUtils.isBlank(key)) {
            return false;
        }
        if (vo.get(key) != null || vo.get(key.toLowerCase()) != null || vo.get(key.toUpperCase()) != null) {
            return true;
        }
        return false;
    }

    /**
     * xml string convert to json string
     *
     * @param xml
     * @return
     * @throws BussinessException
     */
    public static String xml2json(String xml) throws BussinessException {
        StringReader input = new StringReader(xml);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().autoArray(true).autoPrimitive(true).prettyPrint(true).build();
        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
            XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception e) {
            throw new BussinessException(e.getMessage());
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                throw new BussinessException(e.getMessage());
            }
        }
        return output.toString();
    }

    /**
     * json string convert to xml string
     *
     * @param json
     * @return
     */
    public static String json2xml(String json) throws BussinessException {
        StringReader input = new StringReader(json);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(false).build();
        try {
            XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception e) {
            throw new BussinessException(e.getMessage());
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                throw new BussinessException(e.getMessage());
            }
        }
        if (output.toString().length() >= 38) {//remove <?xml version="1.0" encoding="UTF-8"?>
            return output.toString().substring(39);
        }
        return output.toString();
    }


}
