package com.github.catstiger.sql.sync.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.JoinColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;

import com.github.catstiger.sql.NamingStrategy;
import com.github.catstiger.sql.ORMHelper;
import com.github.catstiger.sql.annotation.Index;
import com.github.catstiger.sql.sync.DatabaseInfo;
import com.github.catstiger.sql.sync.IndexCreator;
import com.github.catstiger.utils.StringUtils;
import com.google.common.base.Joiner;

public class MySqlIndexCreator implements IndexCreator {
  private static Logger logger = LoggerFactory.getLogger(MySqlIndexCreator.class);

  private Boolean strongReferences = false;
  private JdbcTemplate jdbcTemplate;
  private DatabaseInfo databaseInfo;
  private NamingStrategy namingStrategy;
  
  public MySqlIndexCreator() {
    
  }

  @Override
  public void addIndexIfNotExists(Class<?> entityClass, String fieldName) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    if(ormHelper.isFieldIgnore(entityClass, fieldName)) {
      return;
    }
    
    Field field = ReflectionUtils.findField(entityClass, fieldName);
    Method getter = ReflectionUtils.findMethod(entityClass, "get" + StringUtils.upperFirst(field.getName()));
    Index index = getter.getAnnotation(Index.class);
    if(index == null) {
      index = field.getAnnotation(Index.class);
    }
    String table = ormHelper.tableNameByEntity(entityClass);
    String column = ormHelper.columnNameByField(entityClass, field.getName());
    
    if(index == null) {
      if(!strongReferences) {
        //如果不创建外键约束，则引用字段建立索引
        if(getter.getAnnotation(JoinColumn.class) != null || field.getAnnotation(JoinColumn.class) != null) {
          String name = "idx_fk_" + table + "_" + column.toLowerCase();
          if(!databaseInfo.isIndexExists(table, name, false)) { //当索引不存在的时候创建
            String sql = new StringBuilder(100)
                .append("create index ").append(name)
                .append(" on ").append(table)
                .append("(").append(column.toLowerCase())
                .append(")").toString();
            logger.info("创建索引 {}, {} on {}", name, table, column);
            try {
              jdbcTemplate.execute(sql);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
          }
          
          return;
        }
      }
    } else {
      String[] columns = index.columnNames();
      if(columns == null || columns.length == 0) {
        columns = new String[]{column};
      }
      String name = index.name();
      if(StringUtils.isBlank(name)) {
        name = new StringBuilder(200).append("indx_").append(table).append("_")
            .append(Joiner.on("_").join(columns)).toString();
        //当索引不存在的时候创建
        if(!databaseInfo.isIndexExists(table, name, index.unique())) {
          String sql = new StringBuilder(200)
              .append("create index ").append(name)
              .append(" on ").append(table)
              .append("(").append(Joiner.on(",").join(columns))
              .append(")").toString();
          logger.info("创建索引 {}, {} on {}", name, table, Joiner.on(",").join(columns));
          try {
            jdbcTemplate.execute(sql);
          } catch (Exception e) {
            logger.error(e.getMessage());
          }
        }
      }
    }
    
  }

  public void setNamingStrategy(NamingStrategy namingStrategy) {
    this.namingStrategy = namingStrategy;
  }

  public void setStrongReferences(Boolean strongReferences) {
    this.strongReferences = strongReferences;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void setDatabaseInfo(DatabaseInfo databaseInfo) {
    this.databaseInfo = databaseInfo;
  }

}
