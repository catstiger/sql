package com.github.catstiger.sql;

import java.io.Serializable;

import javax.persistence.Id;

public abstract class BaseEntity implements Serializable {
  protected Long id;

  @Id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
  
  
}
