package com.xzy.cm.common.helper;

import com.alibaba.fastjson.JSONObject;
import com.xzy.cm.common.exception.BussinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.ObjectUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by xiongzhanyuan on 2016/8/18.
 */
public class CheckExcelHelper {
    public static final String EXCEL_2003_POSTFIX = ".xls";

    public static final String EXCEL_2007_POSTFIX = ".xlsx";

    public static List<JSONObject> checkExcel(String templatePath, String needCheckPath) throws IOException {

        if (StringUtils.isBlank(templatePath) || StringUtils.isBlank(needCheckPath)) {
            throw new BussinessException("path error");
        }
        //// TODO: 2016/8/18 模板和 上传的文件格式是否需要一致（xls、xlsx）
        String temType = templatePath.substring(templatePath.lastIndexOf("."));
        String cheType = needCheckPath.substring(needCheckPath.lastIndexOf("."));
        //获取 上传文件的  数据  list
        List<JSONObject> cheList = null;
        if (EXCEL_2003_POSTFIX.equals(cheType)) {
            cheList = readXls(needCheckPath);
        } else if (EXCEL_2007_POSTFIX.equals(cheType)) {
            cheList = readXlsx(needCheckPath);
        }

        //获取 模板文件的数据 表头list  模板文件必须是 xlsx格式的
        if (!(EXCEL_2007_POSTFIX.equals(temType))) {
            throw new BussinessException("模板文件必须是xlsx格式");
        }
        List<JSONObject> temList = readXlsx(templatePath);

        //表头校验  上传的表头  必须 >= 模板的表头（模板的表头在上传的文件中都得有）
        if (!ObjectUtils.isEmpty(cheList) && !ObjectUtils.isEmpty(temList)) {
            if (!compare2Set(temList.get(0).keySet(), cheList.get(0).keySet())) {
                throw new BussinessException("文件校验失败");
            }
        }
        return cheList;
    }

    /**
     * 2003-2007
     * @param path
     * @return
     * @throws IOException
     */
    public static List<JSONObject> readXls(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new BussinessException("文件路径不能为空");
        }

        InputStream inputStream = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        
        //// TODO: 2016/8/18 现在只要 1个sheet  如果是多个暂不支持校验  hssfWorkbook.getNumberOfSheets()

        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        if (ObjectUtils.isEmpty(hssfSheet)) {
            throw new BussinessException("文件校验失败");
        }
        //row=0 是第一行
        HSSFRow hssfRow = null;
        HSSFCell cell = null;
        String key = "";
        String value = "";
        ArrayList<String> strArray = new ArrayList<String> ();
        List<JSONObject> list = new ArrayList<>();
        for (int rowNum = 0; rowNum < hssfSheet.getPhysicalNumberOfRows(); rowNum++) {
            hssfRow = hssfSheet.getRow(rowNum);
            JSONObject jsonObject = new JSONObject();
            if (!ObjectUtils.isEmpty(hssfRow)) {
                for (int cellNum = 0; cellNum < hssfRow.getPhysicalNumberOfCells(); cellNum++) {
                    cell = hssfRow.getCell(cellNum);
                    //// FIXME: 2016/8/18 将cell都变为 string格式  不知道有没有问题
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    //如果 是第一行 则取出来放到array里  用于json的key
                    if (rowNum == 0) {
                        strArray.add(cellNum, cell.getStringCellValue());
                    } else {
                        jsonObject.put(strArray.get(cellNum), cell.getStringCellValue());
                    }
                }
            }
            if (!ObjectUtils.isEmpty(jsonObject)) {
                list.add(jsonObject);
            }
        }
        return list;
    }

    /**
     * 2007 -
     * @param path
     * @return
     * @throws IOException
     */
    public static List<JSONObject> readXlsx(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            throw new BussinessException("文件路径不能为空");
        }

        InputStream inputStream = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        //// TODO: 2016/8/18 现在只要 1个sheet  如果是多个暂不支持校验  hssfWorkbook.getNumberOfSheets()

        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        if (ObjectUtils.isEmpty(xssfSheet)) {
            throw new BussinessException("文件校验失败");
        }
        //row=0 是第一行
        XSSFRow xssfRow = null;
        XSSFCell xssfCell = null;
        ArrayList<String> strArray = new ArrayList<String> ();
        List<JSONObject> list = new ArrayList<>();
        for (int rowNum = 0; rowNum < xssfSheet.getPhysicalNumberOfRows(); rowNum++) {
            xssfRow = xssfSheet.getRow(rowNum);
            JSONObject jsonObject = new JSONObject();
            if (!ObjectUtils.isEmpty(xssfRow)) {
                for (int cellNum = 0; cellNum < xssfRow.getPhysicalNumberOfCells(); cellNum++) {
                    xssfCell = xssfRow.getCell(cellNum);
                    //// FIXME: 2016/8/18 将cell都变为 string格式  不知道有没有问题
                    xssfCell.setCellType(Cell.CELL_TYPE_STRING);
                    //如果 是第一行 则取出来放到array里  用于json的key
                    if (rowNum == 0) {
                        System.out.print("");
                        strArray.add(cellNum, xssfCell.getStringCellValue());
                    } else {
                        jsonObject.put(strArray.get(cellNum), xssfCell.getStringCellValue());
                    }
                }
            }
            if (!ObjectUtils.isEmpty(jsonObject)) {
                list.add(jsonObject);
            }
        }
        return list;
    }

    public static boolean compare2Set(Set<String> set1, Set<String> set2) {
        boolean result = Boolean.TRUE;
        Iterator<String> it = set1.iterator();
        while (it.hasNext()) {
            String obj = it.next();
            if (!(set2.contains(obj)))
            {
                result = Boolean.FALSE;
                break;
            }
        }
        return result;
    }

}
