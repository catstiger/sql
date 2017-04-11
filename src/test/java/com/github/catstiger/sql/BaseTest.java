package com.github.catstiger.sql;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

public class BaseTest {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  public static DruidDataSource ds;
  
  @BeforeClass
  public static void before() {
    if(ds == null) {
      ds = new DruidDataSource();
      ds.setDriverClassName(TestConstants.DB_DRIVER);
      ds.setUrl(TestConstants.DB_URL);
      ds.setUsername(TestConstants.DB_USER);
      ds.setPassword(TestConstants.DB_PWD);
      try {
        ds.init();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    } 
  }
  
  @AfterClass
  public static void after() {
    if(ds != null) {
      ds.close();
    }
  }
}
