package com.github.catstiger.sql.sync.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.github.catstiger.sql.NamingStrategy;
import com.github.catstiger.sql.ORMHelper;
import com.github.catstiger.sql.annotation.AutoId;
import com.github.catstiger.sql.sync.ColumnCreator;
import com.github.catstiger.sql.sync.DatabaseInfo;
import com.github.catstiger.sql.sync.IndexCreator;
import com.github.catstiger.utils.StringUtils;

@Service
public class MySqlColumnCreator implements ColumnCreator {
  private static Logger logger = LoggerFactory.getLogger(MySqlColumnCreator.class);
  @Value("${jdbc.strongReferences}")
  private Boolean strongReferences;
  @Resource
  private JdbcTemplate jdbcTemplate;
  @Resource
  private DatabaseInfo databaseInfo;
  @Resource
  private IndexCreator indexCreator;
  
  private NamingStrategy namingStrategy;
  
  
  @Override
  public void addColumnIfNotExists(Class<?> entityClass, String field) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    if(ormHelper.isFieldIgnore(entityClass, field)) {
      return;
    }
    
    if(!isColumnExists(entityClass, field)) {
      StringBuilder sqlBuilder = new StringBuilder(100)
          .append("alter table ")
          .append(ormHelper.tableNameByEntity(entityClass))
          .append(" add column (")
          .append(getColumnSqlFragment(entityClass, field))
          .append(")");
      logger.debug("新增字段 : {}", sqlBuilder);
      jdbcTemplate.execute(sqlBuilder.toString());
    }
  }

  @Override
  public String getColumnSqlFragment(Class<?> entityClass, String fieldName) {
    Objects.requireNonNull(entityClass);
    Objects.requireNonNull(fieldName);
    
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    if(!ormHelper.isEntity(entityClass)) {
      throw new RuntimeException(entityClass.getName() + " 不是实体类！");
    }
    Field field = ReflectionUtils.findField(entityClass, fieldName); //属性
    if(ormHelper.isFieldIgnore(field)) {
      return "";
    }
    
    String name = ormHelper.columnNameByField(entityClass, fieldName); //对应的字段名
    Method getter = ormHelper.getAccessMethod(entityClass, fieldName); //对应的getter方法
    Column colAnn = getter.getAnnotation(Column.class); // Column标注
    JoinColumn joinColAnn = getter.getAnnotation(JoinColumn.class); //外键标注
    Lob lobAnn = getter.getAnnotation(Lob.class); //Lob标注
    AutoId autoId = getter.getAnnotation(AutoId.class); //是否自增
    Id id = getter.getAnnotation(Id.class); //是否主键
    Entity refEntityAnn = field.getType().getAnnotation(Entity.class); //外键
    
    int length = 255, precision = 0, scale = 0;
    boolean nullable = true;
    boolean unique = false;
    String columnDef = "";
    
    if(colAnn != null) {
      length = colAnn.length();
      precision = colAnn.precision();
      scale = colAnn.scale();
      nullable = colAnn.nullable();
      unique = colAnn.unique();
      columnDef = colAnn.columnDefinition();
    } else if (joinColAnn != null) {
      nullable = joinColAnn.nullable();
      unique = joinColAnn.unique();
      columnDef = joinColAnn.columnDefinition();
    } 
    
    StringBuilder sql = new StringBuilder(100).append(name).append(" ");
    Class<?> type = field.getType();
    
    //字符串类型
    if(StringUtils.isNotBlank(columnDef)) {
      sql.append(columnDef);
    }
    else if(type == String.class) {
      if(lobAnn != null) {
        sql.append("text");
      } else {
        sql.append("varchar(").append(length).append(")");
      }
    } 
    //浮点类型
    else if (type == Double.class || type == double.class || type == Float.class || type == float.class) {
      if (precision > 0 && scale == 0) {
        sql.append("numeric(").append(precision).append(")");
      } else if (precision > 0 && scale > 0) {
        sql.append("numeric(").append(precision).append(",").append(scale).append(")");
      } else if (type == double.class || type == Double.class) {
        sql.append("double");
      } else if (type == Float.class || type == float.class) {
        sql.append("float");
      }
    }
    //长整型
    else if (type == Long.class || type == long.class) {
      if(precision > 0) {
        sql.append("numeric(").append(precision).append(")");
      } else {
        sql.append("bigint");
      }
    }
    //整型
    else if (type == Integer.class || type == int.class) {
      if(precision > 0) {
        sql.append("numeric(").append(precision).append(")");
      } else {
        sql.append("int");
      }
    }
    //短整型
    else if (type == Short.class || type == short.class) {
      if(precision > 0) {
        sql.append("numeric(").append(precision).append(")");
      } else {
        sql.append("tinyint");
      }
    }
    //boolean
    else if (type == Boolean.class || type == boolean.class) {
      sql.append("tinyint(1)");
    }
    //日期
    else if (type == Date.class || type == Timestamp.class) {
      sql.append("datetime");
    } 
    //外键
    else if (refEntityAnn != null) {
      sql.append("bigint");
    }
    
    if(!nullable) {
      sql.append(" not null ");
    }
    
    if(unique) {
      sql.append(" unique ");
    }
    
    if(autoId != null) {
      sql.append(" auto_increment ");
    }
    
    if(id != null) {
      sql.append(" primary key ");
    }
    
    return sql.toString();
  }

  @Override
  public void addForeignKeyIfNotExists(Class<?> entityClass, String field, Class<?> refClass, String refField) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    if(ormHelper.isEntityIgnore(entityClass)) {
      return;
    }
    
    if(ormHelper.isFieldIgnore(entityClass, field)) {
      return;
    }
    
    if(!this.strongReferences) { //非强关联，不建立外键
      indexCreator.addIndexIfNotExists(entityClass, field); //外键需要创建索引
      return;
    }
    String table = ormHelper.tableNameByEntity(entityClass);
    String refTable = ormHelper.tableNameByEntity(refClass);
    String column = ormHelper.columnNameByField(entityClass, field);
    String refColumn = ormHelper.columnNameByField(refClass, refField);
    
    if(!databaseInfo.isForeignKeyExists(table, column, refTable, refColumn)) {
      String fkName = new StringBuilder(30).append("fk_").append(table).append("_").append(column).append("_").append(refTable).toString();
      StringBuilder sqlBuilder = new StringBuilder(200)
          .append("ALTER TABLE ")
          .append(table)
          .append(" ADD CONSTRAINT ")
          .append(fkName)
          .append(" FOREIGN KEY (")
          .append(column)
          .append(") REFERENCES ")
          .append(refTable)
          .append("(").append(refColumn).append(")");
      logger.debug("新增外键 {}", sqlBuilder.toString());
      jdbcTemplate.execute(sqlBuilder.toString());
    }
  }

  @Override
  public Boolean isColumnExists(Class<?> entityClass, String field) {
    ORMHelper ormHelper = ORMHelper.getInstance(namingStrategy);
    String table = ormHelper.tableNameByEntity(entityClass);
    String colname = ormHelper.columnNameByField(entityClass, field);
    return databaseInfo.isColumnExists(table, colname);
  }

  public NamingStrategy getNamingStrategy() {
    return namingStrategy;
  }
}
