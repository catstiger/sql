package com.github.catstiger.sql.mapper.rsi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.github.catstiger.sql.mapper.ResultSetInvoker;

public class DateRSInvoker implements ResultSetInvoker<Date> {

  @Override
  public Date get(ResultSet rs, int index) throws SQLException {
    return rs.getDate(index);
  }
}
