package com.reven.transfer.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.reven.transfer.bo.ImportTable;

public class ReadTableUtil {
    /**
     *
     * @param file file需要传入文件的全路径
     */
    public static List<ImportTable> readTxt(File file) throws IOException {
        String s;
        List<ImportTable> listEntities = new ArrayList<>();
        /*
         * 以文件流的形式逐行读取txt文件(文件存储编码需要utf-8格式，不然会乱码)
         */
        try (InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br = new BufferedReader(in);) {
            // 忽略第一行
            br.readLine();
            while ((s = br.readLine()) != null) {
                String[] arr = s.split("\\|");
                ImportTable table = new ImportTable();
                if (arr.length >= 1 && !StringUtils.isEmpty(arr[0].trim())) {
                    table.setTableName(arr[0].trim());
                    if (arr.length >= 2) {
                        table.setPageOrderBy(arr[1].trim());
                    }

                    listEntities.add(table);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listEntities;
    }
}
