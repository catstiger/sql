package com.github.catstiger.sql.sync.mysql;

import org.junit.Test;

import com.github.catstiger.sql.NamingStrategy;
import com.github.catstiger.sql.ns.SnakeCaseNamingStrategy;
import com.github.catstiger.sql.sync.mysql.MySqlSyncOperator.DbConnInfo;
import com.github.catstiger.sql.TestConstants;

public class MySqlSyncOperatorTest {
  
  @Test
  public void testOpe() {
    DbConnInfo connInfo = new DbConnInfo();
    connInfo.setDriver(TestConstants.DB_DRIVER);
    connInfo.setUrl(TestConstants.DB_URL);
    connInfo.setUser(TestConstants.DB_USER);
    connInfo.setPassword(TestConstants.DB_PWD);
    
    NamingStrategy namingStrategy = new SnakeCaseNamingStrategy();
    MySqlSyncOperator.sync(connInfo, namingStrategy, false, "com.github.catstiger.**.model");
  }
}
