package com.github.catstiger.sql.mapper.rsi;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.catstiger.sql.mapper.ResultSetInvoker;

public class FloatRSInvoker implements ResultSetInvoker<Float> {

  @Override
  public Float get(ResultSet rs, int index) throws SQLException {
    return rs.getFloat(index);
  }
}
