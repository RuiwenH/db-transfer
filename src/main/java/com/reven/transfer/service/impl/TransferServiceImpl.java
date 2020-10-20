package com.reven.transfer.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.NClob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.reven.transfer.dao.source.SourceMapper;
import com.reven.transfer.dao.target.TargetMapper;
import com.reven.transfer.service.TransferService;
import com.reven.transfer.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: TransferServiceImpl
 * @author reven
 * @date 2020年10月16日
 */
@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private TargetMapper targetMapper;

    @Value("${transfer.page.size:3000}")
    private Integer pageSize;

    private long start;

    /*
     * (non-Javadoc)
     * 
     * @see com.reven.transfer.service.TransferService#transfer(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void transfer(String tableName, String pageOrderBy) {
        start = System.currentTimeMillis();
        log.info("开始使用truncate删除{}", tableName);
        targetMapper.truncate(tableName);
        log.info("开始导入{}", tableName);
        if (StringUtils.isEmpty(pageOrderBy)) {
            pageOrderBy = sourceMapper.getPrimary(tableName.toUpperCase());
        }
        // 如果数据量很大，递归调用会栈溢出吧
        insertData(tableName, pageOrderBy, 1);
        long end = System.currentTimeMillis();
        log.info("导入{}结束,start={},end={},耗时={}", tableName, start, end, TimeUtil.ms2DHMS(start, end));
    }

    private void insertData(String tableName, String pageOrderBy, int pageNo) {
        PageHelper.startPage(pageNo, pageSize);
        log.debug("查询数据，tableName={},orderBy={},pageNo={},请求行数=", tableName, pageOrderBy, pageNo, pageSize);
        List<Map<String, Object>> list = sourceMapper.list(tableName, pageOrderBy);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        if (pageInfo.getTotal() > 0 && list != null && list.size() > 0) {
            log.info("tableName={},orderBy={},pageNo={},导入行数={}", tableName, pageOrderBy, pageNo, list.size());
            List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>(list.size());
            for (Map<String, Object> map : list) {
                map.remove("ROW_ID");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (map.get(key) instanceof java.sql.Blob) {
                        if (null != map.get(key)) {
                            map.put(key, blobToByte((Blob) map.get(key)));
                        }
                    }
                    if (map.get(key) instanceof java.sql.NClob || map.get(key) instanceof java.sql.Clob) {
                        if (null != map.get(key)) {
                            map.put(key, clobToString((NClob) map.get(key)));
                        }
                    }
                }
                newList.add(map);
            }
            log.debug("数据转换完成,tableName={},pageNo={},数据大小={}", tableName, pageNo, list.size());
            Map<String, Object> map = list.get(0);
            targetMapper.insert(tableName, map, newList);

            long end = System.currentTimeMillis();
            log.info("导入{},当前耗时={}", tableName, TimeUtil.ms2DHMS(start, end));

            if (pageInfo.getNextPage() >= 1) {
                insertData(tableName, pageOrderBy, pageInfo.getNextPage());
            }
        }
    }

    /**
     * 把Blob类型转换为byte数组类型
     * 
     * @param blob
     * @return
     */
    public static byte[] blobToByte(Blob blob) {
        try (BufferedInputStream is = new BufferedInputStream(blob.getBinaryStream());) {
            byte[] bytes = new byte[(int) blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;
            while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }
            return bytes;
        } catch (Exception e) {
            log.error("java.sql.Blob类型转byte[]类型出错..." + e.getCause());
            return null;
        }
    }

    // NClob或Clob转String类型
    public String clobToString(NClob nclob) {
        try (Reader is = nclob.getCharacterStream(); BufferedReader buff = new BufferedReader(is);) {
            String content = "";
            String line = buff.readLine();
            StringBuffer sb = new StringBuffer();
            while (line != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
                sb.append(line);
                line = buff.readLine();
            }
            content = sb.toString();
            return content;
        } catch (Exception e) {
            log.error("java.sql.NClob类型转java.lang.String类型出错..." + e.getCause());
        }
        return null;
    }
}
