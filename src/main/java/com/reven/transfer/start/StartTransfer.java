package com.reven.transfer.start;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.reven.transfer.bo.ImportTable;
import com.reven.transfer.file.ReadTableUtil;
import com.reven.transfer.service.TransferService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartTransfer implements ApplicationRunner {
    @Autowired
    private TransferService transferService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 参数以空格分隔，“--”开头的参数会被解析成Option类型的参数，便于取值
        Set<String> optionNames = args.getOptionNames();
        for (String optionName : optionNames) {
            log.info("这是传过来的参数[{}]={}", optionName, args.getOptionValues(optionName));
        }
        if (args.getOptionValues("file") == null) {
            log.error("需要迁移的表清单文件参数file必须设置！");
        }
        String filePath = args.getOptionValues("file").get(0);
        log.info(filePath);
//        if (filePath.startsWith("\\.\\/")) {
//        filePath = System.getProperty("user.dir") + filePath.replace("\\.\\/", "");
//        }
        filePath = System.getProperty("user.dir") + File.separator + filePath;
        log.info("表清单文件参数file={}", filePath);
        File file = new File(filePath);
        List<ImportTable> tables = ReadTableUtil.readTxt(file);
        log.info("表清单数量={}", tables.size());
        for (ImportTable importTable : tables) {
            transferService.transfer(importTable.getTableName(), importTable.getPageOrderBy());
        }
    }
}