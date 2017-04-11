package com.github.catstiger.sql;

import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.sql.mapper.BeanRowMapper;
import com.github.catstiger.sql.sync.model.TestDbModel;

public class SqlTestA extends BaseTest {
  /**
   * 测试主表关联两个子表
   */
  @org.junit.Test
  public void testA() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    String sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "refTiger")
        .usingAlias(true).select(false).getSql();
    
    List<TestDbModel> models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefTiger());
    Assert.notNull(models.get(0).getRefTiger().getId());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName",
        "refModel", "title", "refTiger", "tigerName")
        .usingAlias(true).select(true).getSql();
    
    models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefTiger());
    Assert.notNull(models.get(0).getRefTiger().getId());
  }
  
  /**
   * 测试主表两次关联
   */
  @Test
  public void testB() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    String sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "refTiger", "refTigerSam")
        .usingAlias(true).select(false).getSql();
    List<TestDbModel> models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefTiger());
    Assert.notNull(models.get(0).getRefTiger().getId());
    Assert.notNull(models.get(0).getRefTigerSam());
    Assert.notNull(models.get(0).getRefTigerSam().getId());
    
    sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "title",
        "refTiger", "refTigerSam", "tigerName")
        .usingAlias(true).select(true).getSql();
    System.out.println(sql);
    models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefTiger());
    Assert.notNull(models.get(0).getRefTiger().getId());
    Assert.notNull(models.get(0).getRefTigerSam());
    Assert.notNull(models.get(0).getRefTigerSam().getId());
  }
  
  @Test
  public void testC() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    String sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "title", "refCate", "funcName")
        .usingAlias(true).select(true).getSql();
    List<TestDbModel>models = jdbcTemplate.query(sql, new BeanRowMapper<TestDbModel>(TestDbModel.class));
    System.out.println(sql + "\n" + JSON.toJSONString(models, true));
    Assert.notEmpty(models);
    Assert.notNull(models.get(0));
    Assert.notNull(models.get(0).getRefModel());
    Assert.notNull(models.get(0).getRefModel().getId());
    Assert.notNull(models.get(0).getRefModel().getTitle());
  }
}
