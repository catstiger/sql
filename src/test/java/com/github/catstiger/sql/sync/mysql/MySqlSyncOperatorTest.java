package com.github.catstiger.sql.sync.mysql;

import org.junit.Test;

import com.github.catstiger.sql.NamingStrategy;
import com.github.catstiger.sql.ns.SnakeCaseNamingStrategy;
import com.github.catstiger.sql.sync.mysql.MySqlSyncOperator.DbConnInfo;

public class MySqlSyncOperatorTest {
  
  @Test
  public void testOpe() {
    DbConnInfo connInfo = new DbConnInfo();
    connInfo.setDriver("com.mysql.jdbc.Driver");
    connInfo.setUrl("jdbc:mysql://127.0.0.1/test");
    connInfo.setUser("root");
    connInfo.setPassword("root");
    
    NamingStrategy namingStrategy = new SnakeCaseNamingStrategy();
    MySqlSyncOperator.sync(connInfo, namingStrategy, false, "com.github.catstiger.**.model");
  }
}
