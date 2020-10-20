package com.reven.transfer.dao.source;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SourceMapper {

    List<Map<String, Object>> list(@Param("tableName") String tableName, @Param("orderBy") String orderBy);

    String getPrimary(@Param("tableName") String tableName);
}