package com.xzy.cm.common.helper;

import com.xzy.cm.common.exception.BussinessException;
import com.xzy.cm.common.exception.ErrorCodeEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于读取系统中的文件
 *
 */
public class IOHelper {
    /**
     * @param location
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String readByChar(String location, String encoding) throws Exception {//single file
        //
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource_list = resolver.getResources(location);
        if (ArrayUtils.isEmpty(resource_list)) {
            throw new Exception(location + "...path error");
        }
        EncodedResource er = new EncodedResource(resource_list[0], encoding);
        String ret = FileCopyUtils.copyToString(er.getReader());
        return ret;
    }

    /**
     * @param location
     * @param encoding
     * @return
     * @throws Exception
     */
    public static List<String> readMultiFileByChar(String location, String encoding) throws Exception {
        //
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource_list = resolver.getResources(location);
        if (ArrayUtils.isEmpty(resource_list)) {
            throw new Exception(location + "...path error");
        }
        List<String> list = new ArrayList<String>();
        for (Resource resource : resource_list) {
            EncodedResource er = new EncodedResource(resource, encoding);
            String content = FileCopyUtils.copyToString(er.getReader());
            list.add(content);
        }
        return list;
    }

    /**
     * 得到resources
     *
     * @param location
     * @return
     * @throws Exception
     */
    public static Resource[] readResource(String location) throws Exception {//single file
        //
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource_list = resolver.getResources(location);
        if (ArrayUtils.isEmpty(resource_list)) {
            throw new Exception(location + "...path error");
        }
        return resource_list;
    }

    /**
     * 向文件系统写入文件
     *
     * @param param
     * @param fileName
     * @param path
     * @throws IOException
     */
    public static String writeFile(String param, String fileName, String path) throws IOException {
        if (StringUtils.isBlank(param) || StringUtils.isBlank(fileName) || StringUtils.isBlank(path)) {
            throw new BussinessException(ErrorCodeEnum.PARAMETERMISSING);
        }
        String pathFile = "";
        if (!path.endsWith("/")) {
            pathFile = path + "/" + fileName;
        } else {
            pathFile = path + fileName;
        }
        File file = new File(pathFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(pathFile);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(param);
        bw.close();
        writer.close();
        return pathFile;
    }

}
