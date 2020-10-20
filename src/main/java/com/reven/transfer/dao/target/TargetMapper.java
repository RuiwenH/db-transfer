package com.reven.transfer.dao.target;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface TargetMapper {
    public void insert(@Param("tableName") String tableName, @Param("columnMap") Map<String, Object> columnMap,
            @Param("mapList") List<Map<String, Object>> data);

    public void truncate(@Param("tableName") String tableName);
}