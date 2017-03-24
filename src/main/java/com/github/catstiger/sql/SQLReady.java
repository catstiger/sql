package com.github.catstiger.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.github.catstiger.sql.limit.LimitSql;
import com.github.catstiger.utils.StringUtils;
import com.google.common.base.Joiner;

/**
 * 用于存放生成的SQL，以及对应的参数
 */
public final class SQLReady {
  //private String sql;
  private List<Object> args = new ArrayList<>(10);
  private Map<String, Object> namedParameters = new HashMap<>(0);
  private LimitSql limitSql = SQLRequest.DEFAULT_LIMIT_SQL;
  private List<String> appended = new ArrayList<>(10);
  private static final String SQL_SPLITTER = " ";
  private static final String ORDER_BY = " ORDER BY ";
  /**
   * 排序方向常量
   */
  public static final String DESC = "desc";
  /**
   * 排序方向常量
   */
  public static final String ASC = "asc";
  
  
  
    
  public SQLReady(String sql, Object... args) {
    appended.add(sql);
    if(args != null) {
      for(Object arg : args) {
        this.args.add(arg);
      }
    }
  }
  
  public SQLReady(String sql, Map<String, Object> namedParameters) {
    if(sql == null) {
      throw new IllegalArgumentException("SQL must not be null.");
    }
    appended.add(sql);
    this.namedParameters = namedParameters;
  }
  
  public SQLReady(String sql, Object[] args, LimitSql limitSql) {
    if(sql == null) {
      throw new IllegalArgumentException("SQL must not be null.");
    }
    
    appended.add(sql);
    if(args != null) {
      for(Object arg : args) {
        this.args.add(arg);
      }
    }
    this.limitSql = limitSql;
  }
  
  public SQLReady(String sql, Map<String, Object> namedParameters, LimitSql limitSql) {
    appended.add(sql);
    this.namedParameters = namedParameters;
    this.limitSql = limitSql;
  }
  
  public String getSql() {
    return Joiner.on(SQL_SPLITTER).join(appended);
  }
  
  public Object[] getArgs() {
    Object[] objs = new Object[args.size()];
    return args.toArray(objs);
  }
  
  public void setArgs(Object[] args) {
    if(args != null) {
      for(Object arg : args) {
        this.args.add(arg);
      }
    }
  }
  
  public SQLReady addArg(Object arg) {
    this.args.add(arg);
    return this;
  }

  public Map<String, Object> getNamedParameters() {
    return namedParameters;
  }

  public void setNamedParameters(Map<String, Object> namedParameters) {
    this.namedParameters = namedParameters;
  }
  
  /**
   * 追加一段SQL，及其参数
   * @param sqlSegment SQL片段
   * @param args 参数
   */
  public SQLReady append(String sqlSegment, Object...args) {
    if(sqlSegment == null) {
      throw new IllegalArgumentException("Sql Segment must not be null.");
    }
    appended.add(sqlSegment);
    if(args != null && args.length > 0) {
      for(Object arg : args) {
        this.args.add(arg);
      }
    }
    return this;
  }
  
  /**
   * 根据表达式的结果，判断是否追加SQL和参数
   * @param sqlSegment SQL片段
   * @param expression 表达式，可以是一个boolean类型的语句，如果为true, 则追加，否则，直接返回SQLReady对象
   * @param args SQL对应的参数。
   * @return
   */
  public SQLReady append(String sqlSegment, Boolean expression, Object...args) {
    return this.append(sqlSegment, () -> expression, args);
  }
  
  /**
   * 根据booleanSupplier的返值，决定是否追加一段SQL
   * @param sqlSegment 要追加的SQL
   * @param booleanSupplier Instance of {@link java.util.function.BooleanSupplier}, 可以是一个lambda
   * @param args 涉及的参数
   * @return
   */
  public SQLReady append(String sqlSegment, BooleanSupplier action, Object...args) {
    if(action == null || !action.getAsBoolean()) {
      return this;
    }
    return append(sqlSegment, args);
  }
  
  /**
   * 追加一段SQL和一个命名参数以及参数值
   * @param sqlSegment SQL片段
   * @param name 参数名称
   * @param value 参数值
   */
  public SQLReady append(String sqlSegment, String name, Object value) {
    if(sqlSegment == null) {
      throw new IllegalArgumentException("Sql Segment must not be null.");
    }
    appended.add(sqlSegment);
    if(name != null) {
      this.namedParameters.put(name, value);
    }
    return this;
  }
  
  /**
   * 根据表达式，追加一段SQL和一个命名参数以及参数值
   * @param sqlSegment SQL片段
   * @param expression 如果为true,则追加，否则，直接返回
   * @param name 参数名称
   * @param value 参数值
   */
  public SQLReady append(String sqlSegment, Boolean expression, String name, Object value) {
    return append(sqlSegment, () -> expression, name, value);
  }
  
  /**
   * 根据Lambda表达式，追加一段SQL和一个命名参数以及参数值
   * @param sqlSegment SQL片段
   * @param action 如果返值为TRUE，则追加，否则返回
   * @param name 参数名称
   * @param value 参数值
   */
  public SQLReady append(String sqlSegment, BooleanSupplier action, String name, Object value) {
    if(action == null || !action.getAsBoolean()) {
      return this;
    }
    return append(sqlSegment, name, value);
  }
  
  /**
   * 追加一段SQL，和命名参数
   * @param sqlSegment SQL片段
   * @param namedParams 命名参数，KEY为参数名称，Value为参数值
   */
  public SQLReady append(String sqlSegment, Map<String, Object> namedParams) {
    if(sqlSegment == null) {
      throw new IllegalArgumentException("Sql Segment must not be null.");
    }
    appended.add(sqlSegment);
    if(namedParams != null) {
      this.namedParameters.putAll(namedParams);
    }
    return this;
  }
  
  public SQLReady append(String sqlSegment, Boolean expression, Map<String, Object> namedParams) {
    return this.append(sqlSegment, () -> expression, namedParams);
  }
  
  public SQLReady append(String sqlSegment, BooleanSupplier action, Map<String, Object> namedParams) {
    if(action == null || !action.getAsBoolean()) {
      return this;
    }
    return this.append(sqlSegment, namedParams);
  }  
  
  /**
   * 如果给定的参数不为null，且toString不为空字符串，则追加SQL
   */
  public SQLReady appendIfExists(String sqlSegment, Object arg) {
    if(arg == null) {
      return this;
    }
    if(StringUtils.isBlank(arg.toString())) {
      return this;
    }
    return append(sqlSegment, arg);
  }
  
  /**
   * 新增排序
   * @param column 排序字段名
   * @param direction 排序方向
   * @param expression 表达式，为true才执行添加动作
   * @return
   */
  public SQLReady orderBy(String column, String direction, Boolean expression) {
    if(column == null) {
      throw new IllegalArgumentException("Sql column must not be null.");
    }
    if(direction == null) {
      direction = StringUtils.EMPTY;
    }
    if(expression == null || !expression) {
      return this;
    }
    
    String orderBy = getSql().indexOf(ORDER_BY) > 0 ? " , " : ORDER_BY;
    appended.add(new StringBuilder(20).append(orderBy).append(column).append(" ").append(direction).toString());
    return this;
  }
  /**
   * 新增排序，方向为ASC
   * @param column 字段名
   * @param expression 表达式，决定是否添加排序字段
   * @return
   */
  public SQLReady orderBy(String column, Boolean expression) {
    return orderBy(column, null, expression);
  }
  
  /**
   * 新增排序，
   * @param column 字段名
   * @param direction 排序方向
   */
  public SQLReady orderBy(String column, String direction) {
    return orderBy(column, direction, true);
  }
  
  /**
   * 新增排序，
   * @param column 字段名
   * @param direction 排序方向
   */
  public SQLReady orderBy(String column) {
    return orderBy(column, null, true);
  }
  
  public String countSql() {
    return SQLFactory.getInstance().countSql(getSql());
  }
  
  public String limitSql(int start, int limit) {
    return SQLFactory.getInstance().limitSql(getSql(), start, limit, limitSql);
  }

  public SQLReady withLimitSql(LimitSql limitSql) {
    this.limitSql = limitSql;
    return this;
  }
}

