package com.github.catstiger.sql.sync.mysql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.github.catstiger.sql.NamingStrategy;
import com.github.catstiger.sql.ORMHelper;
import com.github.catstiger.sql.sync.ColumnCreator;
import com.github.catstiger.sql.sync.DatabaseInfo;
import com.github.catstiger.sql.sync.TableCreator;
import com.github.catstiger.utils.ReflectUtils;
import com.github.catstiger.utils.StringUtils;
import com.google.common.base.Joiner;

@Component
public class MySqlTableCreator implements TableCreator {
  private Logger logger = LoggerFactory.getLogger(MySqlTableCreator.class);
  
  @Resource
  private DatabaseInfo databaseInfo;
  @Resource
  private ColumnCreator columnCreator;
  @Resource
  private JdbcTemplate jdbcTemplate;
  
  private NamingStrategy namingStrategy;
  
  
  @Override
  public void createTableIfNotExists(Class<?> entityClass) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    String table = ormHelper.tableNameByEntity(entityClass);
    Field[] fields = ReflectUtils.getFields(entityClass);
    
    if(!this.isTableExists(entityClass)) {
      StringBuilder sqlBuf = new StringBuilder(500)
          .append("create table ")
          .append(table)
          .append("(");
      List<String> sqls = new ArrayList<String>(fields.length); //SQL片段
      for(Field field : fields) {
        if(ormHelper.isFieldIgnore(field)) {
          continue;
        }
        String sqlFregment = columnCreator.getColumnSqlFragment(entityClass, field.getName());
        logger.debug("SQL Fregment {}", sqlFregment);
        if(StringUtils.isBlank(sqlFregment)) {
          continue;
        }
        sqls.add(sqlFregment);
      }
      sqlBuf.append(Joiner.on(",\n").join(sqls))
      .append(")");
      logger.debug("创建表{}, {}", entityClass.getName(), sqlBuf);
      jdbcTemplate.execute(sqlBuf.toString());
    } 
  }

  @Override
  public Boolean isTableExists(Class<?> entityClass) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    String table = ormHelper.tableNameByEntity(entityClass);
    return databaseInfo.isTableExists(table);
  }

  @Override
  public void updateTable(Class<?> entityClass) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    Field[] fields = ReflectUtils.getFields(entityClass);
    if(this.isTableExists(entityClass)) {
      for(Field field : fields) {
        if(ormHelper.isFieldIgnore(field)) {
          continue;
        }
        columnCreator.addColumnIfNotExists(entityClass, field.getName());
      }
    }

  }

  public void setNamingStrategy(NamingStrategy namingStrategy) {
    this.namingStrategy = namingStrategy;
  }

}
