package com.github.catstiger.sql.ns;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.catstiger.utils.StringUtils;

public class SnakeCaseNamingStrategy  extends AbstractNamingStrategy {

  @Override
  public String columnLabel(PropertyDescriptor propDesc) {
    if(propDesc == null || StringUtils.isBlank(propDesc.getName())) {
      return null;
    }
    return StringUtils.toSnakeCase(propDesc.getName());
  }

  @Override
  public String columnLabel(ResultSet rs, int columnIndex) {
    try {
      ResultSetMetaData metaData = rs.getMetaData();
      String label = metaData.getColumnLabel(columnIndex);
      
      if(StringUtils.isBlank(label)) {
        return null;
      }
      
      return StringUtils.toSnakeCase(label);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public String columnLabel(String column) {
    if(StringUtils.isBlank(column)) {
      return StringUtils.EMPTY;
    }
    
    return StringUtils.toSnakeCase(column);
  }
}
