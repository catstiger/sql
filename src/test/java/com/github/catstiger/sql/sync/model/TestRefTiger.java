package com.github.catstiger.sql.sync.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.catstiger.sql.annotation.AutoId;

@Entity
@Table(name = "test_ref_tiger")
public class TestRefTiger {
  private Long id;
  private Date lastModified;
  private String tigerName;
  
  
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
  
  public String getTigerName() {
    return tigerName;
  }
  
  public void setTigerName(String tigerName) {
    this.tigerName = tigerName;
  }
}
