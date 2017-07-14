package com.github.catstiger.sql.id;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.util.Assert;

import com.github.catstiger.sql.BaseTestCase;

public class TableHiloGenTest extends BaseTestCase {

  @Test
  public void testNextId() {
    TableHiloGen thg = new TableHiloGen();
    thg.setDataSource(ds);
    thg.init();
    Set<Long> ids = new HashSet<>(50000);
    for (int i = 0; i < 50000; i++) {
      Long id = thg.nextId();
      if (i == 0)
        System.out.println(id);
      ids.add(id);
    }
    Assert.isTrue(ids.size() == 50000);
  }

  @Test
  public void testMultiThread() throws Exception {
    TableHiloGen thg = new TableHiloGen();
    thg.setDataSource(ds);
    thg.init();
    AtomicInteger a = new AtomicInteger(0);
    AtomicInteger b = new AtomicInteger(0);
    Runnable r = new Runnable() {
      @Override
      public void run() {
        System.out.println("==========AAA=========" + a.getAndIncrement());
        Connection conn = null;
        try {
          conn = ds.getConnection();
          for (int i = 0; i < 5000; i++) {
            Long id = thg.nextId();
            insertId(conn, id);
            Thread.sleep(2);
            b.incrementAndGet();
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
        }
      }
    };
    for (int i = 0; i < 100; i++) {
      Thread t = new Thread(r);
      t.start();
      
    }
    
    while(b.get() < (5000 * 100)) {
      Thread.sleep(10);
      if(b.get() % 1000 == 0) {
        System.out.println("+++++++++++++++++BBB+++++++++++++++++" + b.getAndIncrement());
      }
    }
    Thread.sleep(100);
  }

  private void insertId(Connection conn, Long id) {
    try {
      PreparedStatement ps = conn.prepareStatement("insert into test_table(val) values(?)");
      ps.setLong(1, id);
      ps.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
