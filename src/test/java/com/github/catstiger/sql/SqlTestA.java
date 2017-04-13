package com.github.catstiger.sql;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.github.catstiger.sql.mapper.BeanRowMapper;
import com.github.catstiger.sql.sync.model.TestDbModel;

public class SqlTestA extends BaseTestCase {
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
    Assert.isTrue(models.get(0).getRefTiger().getId() != models.get(0).getRefTigerSam().getId());
  }
  
  @Test
  public void testTwiceJoin() {
    
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    String sql = new SQLRequest(TestDbModel.class).includes("id", "username", "price", "realName", "refModel", "title",
        "refTiger", "refTigerSam", "tigerName")
        .usingAlias(true).select(true).getSql();
    System.out.println(sql);
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
    Assert.isTrue(models.get(0).getRefTiger().getId() == models.get(0).getRefTigerSam().getId());
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
  
  @Test
  public void testD() {
    String sql = "SELECT testDbModel.id AS id,"
    + "testRefTiger_1.id AS id,"
    + "testRefTiger_0.id AS id,"
    + "testRefModel_0.id AS id,"
    + "testDbModel.price AS price,"
    + "testDbModel.real_name AS realName,"
    + "testRefTiger_0.tiger_name AS tigerName,"
    + "testRefTiger_1.tiger_name AS tigerName,"
    + "testRefModel_0.title AS title,"
    + "testDbModel.username AS username "
    + " FROM test_db_model testDbModel "
    + " JOIN "
    + "test_ref_model testRefModel_0 on (testRefModel_0.id=testDbModel.ref_model_id) "
    + " JOIN "
    + "test_ref_tiger testRefTiger_0 on (testRefTiger_0.id=testDbModel.ref_tiger_id) "
    + " JOIN "
    + "test_ref_tiger testRefTiger_1 on (testRefTiger_1.id=testDbModel.ref_tiger_sam_id) ";
    
    sql = SQLUtils.format(sql, JdbcConstants.MYSQL);
    System.out.println(sql);
    
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
    for(SQLStatement stmt : stmtList) {
      MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
      stmt.accept(visitor);
      Map<String, String> aliasMap = visitor.getAliasMap();
      System.out.println(JSON.toJSONString(aliasMap, true));
    }
  }
}
