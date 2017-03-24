package com.github.catstiger.sql.mapper.rsi;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.catstiger.sql.mapper.ResultSetInvoker;

public class LongRSInvoker implements ResultSetInvoker<Long> {

  @Override
  public Long get(ResultSet rs, int index) throws SQLException {
    return rs.getLong(index);
  }
}
