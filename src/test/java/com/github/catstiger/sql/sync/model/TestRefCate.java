package com.github.catstiger.sql.sync.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.catstiger.sql.annotation.AutoId;

@Entity
@Table(name = "test_ref_cate")
public class TestRefCate {
  private Long id;
  private Date lastModified;
  private String funcName;
  
  
  @Id @AutoId
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
  public String getFuncName() {
    return funcName;
  }
  
  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }
}
