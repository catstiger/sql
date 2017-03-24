package com.github.catstiger.sql.mapper.rsi;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.catstiger.sql.mapper.ResultSetInvoker;

public class BigDecimalRSInvoker implements ResultSetInvoker<BigDecimal> {

  @Override
  public BigDecimal get(ResultSet rs, int index) throws SQLException {
    return rs.getBigDecimal(index);
  }
}
