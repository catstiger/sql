package com.github.catstiger.sql.sync.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.github.catstiger.sql.annotation.AutoId;

@Entity
@Table(name = "test_many_to_many_model")
public class TestManyToManyModel {
  private Long id;
  private String name;
  private List<TestDbModel> testDbs = new ArrayList<>();
  
  @Id @AutoId
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
  @ManyToMany(targetEntity = TestManyToManyModel.class)
  public List<TestDbModel> getTestDbs() {
    return testDbs;
  }
  public void setTestDbs(List<TestDbModel> testDbs) {
    this.testDbs = testDbs;
  } 
}
