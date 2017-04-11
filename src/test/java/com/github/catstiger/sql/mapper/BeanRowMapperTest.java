package com.github.catstiger.sql.mapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.sql.BaseTest;
import com.github.catstiger.sql.SQLRequest;
import com.github.catstiger.sql.sync.model.TestDbModel;

public class BeanRowMapperTest extends BaseTest {
  
  @org.junit.Test
  public void testQ() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    String sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel")
        .usingAlias(true).select(false).getSql();
    
    List<TestDbModel> models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel")
        .usingAlias(false).select(false).getSql();
    models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(sql + "\n" + JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "title")
        .usingAlias(true).select(true).getSql();
    models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(sql + "\n" + JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefModel().getTitle());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "title", "refCate", "funcName")
        .usingAlias(true).select(true).getSql();
    models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(sql + "\n" + JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefModel().getTitle());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName")
        .usingAlias(true).select(false).getSql();
    
    jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    Instant start = Instant.now();
    for(int i = 0; i < 3000; i++) {
       jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    }
    Instant end = Instant.now();
    System.out.println(Duration.between(start, end).toString());
    
    Instant start1 = Instant.now();
    for(int i = 0; i < 3000; i++) {
      jdbcTemplate.query(sql, new BeanPropertyRowMapper<TestDbModel>(TestDbModel.class));
    }
    Instant end1 = Instant.now();
    System.out.println(Duration.between(start1, end1).toString());
  }
  
}
