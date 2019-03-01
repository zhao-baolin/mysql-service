package com.mysql.zhaobaolin.entity;


public class AppRegion {

  private long id;
  private long parentId;
  private String regionName;
  private long regionType;
  private long agencyId;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getParentId() {
    return parentId;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }


  public String getRegionName() {
    return regionName;
  }

  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }


  public long getRegionType() {
    return regionType;
  }

  public void setRegionType(long regionType) {
    this.regionType = regionType;
  }


  public long getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(long agencyId) {
    this.agencyId = agencyId;
  }

}
