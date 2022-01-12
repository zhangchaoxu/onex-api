package com.nb6868.onex.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.nb6868.onex.api.modules.sys.dao.TableSchemaDao;
import com.nb6868.onex.coder.entity.CodeGenerateConfig;
import com.nb6868.onex.coder.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CoderTest {

    @Resource
    TableSchemaDao tableSchemaDao;

    @Test
    @DisplayName("加载所有表")
    void loadAllTables() {
        String tableNameSearch = "cms";
        tableSchemaDao.queryTable(tableNameSearch).forEach(tableMap -> {
            // 打印出所有表
            log.error("table={},json={}", MapUtil.getStr(tableMap, "table_name"), JSONUtil.toJsonStr(tableMap));
        });
    }

    @Test
    @DisplayName("生成代码")
    void generateCode() throws Exception {
        DynamicDataSourceContextHolder.push("master");//手动切换
        TimeInterval timeInterval = DateUtil.timer();
        String tableNames = "uc_user_param";
        String path = "C:\\Workspaces\\coderTest\\";
        String zipFile = path + tableNames + "-" + DateUtil.current() + ".zip";
        CodeGenerateConfig config = new CodeGenerateConfig();
        config.setAuthorEmail("zhangchaoxu@gmail.com");
        config.setAuthorName("Charles");
        config.setModuleName("uc");
        config.setPackageName("com.nb6868.onex.api");
        config.setTablePrefix("uc");
        config.setVersion("1.0.0");
        // 获取表列表
        DataSource ds = new SimpleDataSource("jdbc:mysql://hw3.nb6868.com:3306/onex", "onex", "CWCPxhpp67yaErnn");
        List<Entity> tableList = Db.use(ds).query("SELECT table_name as table_name, engine as engine, table_comment as table_comment, create_time as create_time FROM information_schema.tables WHERE table_schema = (select database()) and table_name like ?", "%" + tableNames + "%");
        FileUtil.touch(zipFile);
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
        for (Map<String, Object> tableMap : tableList) {
            String tableName = MapUtil.getStr(tableMap, "table_name");
            // 查询列信息
            List<Entity> columns = Db.use(ds).query("SELECT column_name as column_name, data_type as data_type, column_comment as column_comment, column_key as column_key, extra as extra FROM information_schema.columns WHERE table_schema = (select database()) and table_name = ? order by ordinal_position", tableName);
            // 生成代码
            GenUtils.generatorCode(tableMap, columns, config, zip);
        }
        zip.close();
        log.debug("代码生成执行完成,timeInterval={}", timeInterval.intervalSecond());
        RuntimeUtil.exec("cmd /c start explorer " + path);
    }

}

