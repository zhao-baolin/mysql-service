package com.mysql.zhaobaolin.tb;

public class AppRegionTb {

  private static String tb = "app_region";
  private static String id = "id";
  private static String parentId = "parent_id";
  private static String regionName = "region_name";
  private static String regionType = "region_type";
  private static String agencyId = "agency_id";

  public static String getTb() {
    return tb;
  }

  public static void setTb(String tb) {
    AppRegionTb.tb = tb;
  }

  public static String getId() {
    return id;
  }

  public static void setId(String id) {
    AppRegionTb.id = id;
  }

  public static String getParentId() {
    return parentId;
  }

  public static void setParentId(String parentId) {
    AppRegionTb.parentId = parentId;
  }

  public static String getRegionName() {
    return regionName;
  }

  public static void setRegionName(String regionName) {
    AppRegionTb.regionName = regionName;
  }

  public static String getRegionType() {
    return regionType;
  }

  public static void setRegionType(String regionType) {
    AppRegionTb.regionType = regionType;
  }

  public static String getAgencyId() {
    return agencyId;
  }

  public static void setAgencyId(String agencyId) {
    AppRegionTb.agencyId = agencyId;
  }
}
