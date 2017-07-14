package com.github.catstiger.sql.id;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.utils.StringUtils;

public class TableHiloGen implements IdGen {
  private static Logger logger = LoggerFactory.getLogger(TableHiloGen.class);
  
  public static final int DEFAULT_STEP = 10000;
  public static final String DEFAULT_TALBE = "catstiger_high";
  
  private AtomicLong max;
  private AtomicLong id;
  private DataSource dataSource;
  private Integer step = DEFAULT_STEP;
  private String tablename = DEFAULT_TALBE;
  
  public TableHiloGen() {
    
  }
  
  public TableHiloGen(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  public TableHiloGen(DataSource dataSource, String tablename, Integer step) {
    
    this.tablename = tablename;
    this.step = step;
  }
  
  public synchronized void init() {
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
      
      ResultSet rs = conn.getMetaData().getTables(StringUtils.EMPTY, StringUtils.EMPTY, tablename, null);
      Statement stmt = conn.createStatement();
      if(!rs.next()) {
        logger.debug("creating hi table {}", tablename);
        stmt.execute("create table " + tablename + " (next_high bigint)");
      }
      rs = stmt.executeQuery("select next_high from " + tablename);
      if(!rs.next()) {
        id = new AtomicLong(Short.MAX_VALUE);
        max = new AtomicLong(Short.MAX_VALUE + step);
        stmt.executeUpdate("insert into " + tablename + " (next_high) values (" + max.get() + ")");
        logger.debug("init hi table {}", max.get());
      } else {
        long hi = rs.getLong(1);
        if(id == null) {
          id = new AtomicLong(hi);
        } else {
          id.set(hi);
        }
        if(max == null) {
          max = new AtomicLong(hi + step);
        } else {
          max.set(hi + step);
        }
        
        stmt.executeUpdate("update " + tablename + " set next_high=" + max.get());
        logger.debug("update hi table {}", max.get());
      }
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  @Override
  public Long nextId() {
    if(id.longValue() < max.longValue()) {
      return id.incrementAndGet();
    } else {
      this.init();
      return id.incrementAndGet();
    }
  }
  
  

  public void setStep(Integer step) {
    this.step = step;
  }

  public void setTablename(String tablename) {
    this.tablename = tablename;
  }
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

}
