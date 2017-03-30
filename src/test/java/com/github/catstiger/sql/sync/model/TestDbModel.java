package com.github.catstiger.sql.sync.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.github.catstiger.sql.BaseEntity;
import com.github.catstiger.sql.annotation.FullMatches;
import com.github.catstiger.sql.annotation.FullText;
import com.github.catstiger.sql.annotation.Index;
import com.github.catstiger.sql.annotation.RangeQuery;

@Entity
@Table(name = "test_db_model")
public class TestDbModel extends BaseEntity {

  
  @Index @FullMatches
  private String username;
  
   @RangeQuery(end = "lastModifiedEnd")
  private Date lastModified;
  
  private Date lastModifiedEnd;
  
  @RangeQuery(end = "priceEnd", start = "priceStart")
  private Double price;
  
  @Transient 
  private Double priceStart;
  
  @Transient 
  private Double priceEnd;
  
  private Float radis;
  
  @Column(length = 100)  
  @Index @FullText(relativeColumn = "desc_ft")
  private String descn;
  
  @Column @Lob
  private String body;
  
  @Transient 
  private String descFt;
  
  
  private String realName;
  
  private TestRefModel refModel;
  
  private List<TestManyToManyModel> m2mModel = new ArrayList<>(); 
  
  private Long idc;
  private String pcName;
  
  @JoinColumn
  public TestRefModel getRefModel() {
    return refModel;
  }

  public void setRefModel(TestRefModel refModel) {
    this.refModel = refModel;
  }

  @Column(precision = 7, scale = 3)
  public Float getRadis() {
    return radis;
  }

  public void setRadis(Float radis) {
    this.radis = radis;
  }

  @ManyToMany(targetEntity = TestManyToManyModel.class, mappedBy = "testDbs")
  public List<TestManyToManyModel> getM2mModel() {
    return m2mModel;
  }

  public void setM2mModel(List<TestManyToManyModel> m2mModel) {
    this.m2mModel = m2mModel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getLastModifiedEnd() {
    return lastModifiedEnd;
  }

  public void setLastModifiedEnd(Date lastModifiedEnd) {
    this.lastModifiedEnd = lastModifiedEnd;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getPriceStart() {
    return priceStart;
  }

  public void setPriceStart(Double priceStart) {
    this.priceStart = priceStart;
  }

  public Double getPriceEnd() {
    return priceEnd;
  }

  public void setPriceEnd(Double priceEnd) {
    this.priceEnd = priceEnd;
  }

  public String getDescn() {
    return descn;
  }

  public void setDescn(String descn) {
    this.descn = descn;
  }

  public String getDescFt() {
    return descFt;
  }

  public void setDescFt(String descFt) {
    this.descFt = descFt;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Long getIdc() {
    return idc;
  }

  public void setIdc(Long idc) {
    this.idc = idc;
  }

  @Index
  public String getPcName() {
    return pcName;
  }

  public void setPcName(String pcName) {
    this.pcName = pcName;
  }
}
