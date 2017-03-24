package com.github.catstiger.sql.mapper.rsi;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.catstiger.sql.mapper.ResultSetInvoker;

public class ObjectRSInvoker implements ResultSetInvoker<Object> {

  @Override
  public Object get(ResultSet rs, int index) throws SQLException {
    return rs.getObject(index);
  }
}
