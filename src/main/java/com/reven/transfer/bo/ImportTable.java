package com.reven.transfer.bo;

import lombok.Data;

/**
 * @Description: 数据导入表信息
 * @author reven
 * @date 2020年10月19日
 */
@Data
public class ImportTable {

    private String tableName;
    /**
     * @Fields pageOrderBy : 指定用于分页排序的字段（非必填，程序会根据表名查询主键列名）
     */
    private String pageOrderBy;
}
