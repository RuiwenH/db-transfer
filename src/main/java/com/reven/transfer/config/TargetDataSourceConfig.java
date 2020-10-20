package com.reven.transfer.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages = "com.reven.transfer.dao.target", sqlSessionFactoryRef = "targetSqlSessionFactory")
public class TargetDataSourceConfig {

    @Bean("targetDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.target-db") // 读取application.yml中的配置参数映射成为一个对象
    public DataSource getDb1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("targetSqlSessionFactory")
    public SqlSessionFactory targetSqlSessionFactory(@Qualifier("targetDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // mapper的xml形式文件位置必须要配置，不然将报错：no statement
        // （这种错误也可能是mapper的xml中，namespace与项目的路径不一致导致）
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/target/*.xml"));
        sessionFactory.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config-target.xml"));
        return sessionFactory.getObject();
    }

    @Bean("targetSqlSessionTemplate")
    public SqlSessionTemplate targetSqlSessionTemplate(
            @Qualifier("targetSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        // BATCH模式并没有增加效率，可能是插入的sql语句的预编译sql是一样的，所以没区别。
        return new SqlSessionTemplate(sqlSessionFactory,ExecutorType.BATCH);
    }
}