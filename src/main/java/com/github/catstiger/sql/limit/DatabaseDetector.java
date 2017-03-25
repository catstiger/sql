package com.github.catstiger.sql.limit;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.catstiger.utils.StringUtils;



/**
 * 用于探测数据库类型
 *
 */
public class DatabaseDetector {
  private DataSource ds;
  private String dbName;
  
  public DatabaseDetector() {
    
  }
  
  public DatabaseDetector(DataSource ds) {
    this.ds = ds;
  }
  
  void init() {
    if(StringUtils.isBlank(dbName)) {
      Connection conn = null;
      try {
        conn = ds.getConnection();
        dbName = conn.getMetaData().getDatabaseProductName();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      } finally {
        try {
          conn.close();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
      
      if(StringUtils.isBlank(dbName)) {
        throw new RuntimeException("Do not find database name!");
      }
    }
  }
  
  public String getDatabaseName() {
    init();
    return dbName;
  }
  
  public Boolean isH2() {
    init();
    return dbName.toUpperCase().indexOf("H2") >= 0;
  }
  
  public Boolean isMySql() {
    init();
    return dbName.toUpperCase().indexOf("MYSQL") >= 0;
  }
  
  public Boolean isOracle() {
    init();
    return dbName.toUpperCase().indexOf("ORACLE") >= 0;
  }

  public void setDs(DataSource ds) {
    this.ds = ds;
  }
}
