package org.cody;

public class Box {

  private String boxNum;
  private String boxWeight;
  private String shipped;
  private String brand;
  private String scannedDate;
  private String scannedBy;
  private String timeScanned;
  private String palletID;

  public Box(String boxNum, String boxWeight, String shipped, String brand,
             String scannedDate, String scannedBy,
             String timeScanned, String palletID) {
    this.boxNum = boxNum;
    this.boxWeight = boxWeight;
    this.shipped = shipped;
    this.brand = brand;
    this.scannedDate = scannedDate;
    this.scannedBy = scannedBy;
    this.timeScanned = timeScanned;
    this.palletID = palletID;
  }
}
