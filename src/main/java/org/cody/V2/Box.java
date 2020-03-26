package org.cody.V2;

public class Box {

  private String gfsItemNum;
  private String itemName;
  private String itemNum;
  private String boxNum;
  private String boxWeight;
  private String shipped;
  private String brand;
  private String scannedDate;
  private String scannedBy;
  private String timeScanned;
  private String palletID;

  public String getItemName() {
    return itemName;
  }

  public String getPalletID() {
    return palletID;
  }

  public String getBoxNum() {
    return boxNum;
  }

  public String getBoxWeight() {
    return boxWeight;
  }

  public String getShipped() {
    return shipped;
  }

  public String getBrand() {
    return brand;
  }

  public String getGfsItemNum() {
    return gfsItemNum;
  }

  public String getItemNum() {
    return itemNum;
  }

  public Box(String gfsItemNum, String itemName, String itemNum, String boxNum, String boxWeight,
             String shipped,
             String brand,
             String scannedDate, String scannedBy,
             String timeScanned, String palletID) {
    this.gfsItemNum = gfsItemNum;
    this.itemName = itemName;
    this.itemNum = itemNum;
    this.boxNum = boxNum;
    this.boxWeight = boxWeight;
    this.shipped = shipped;
    this.brand = brand;
    this.scannedDate = scannedDate;
    this.scannedBy = scannedBy;
    this.timeScanned = timeScanned;
    this.palletID = palletID;
  }

  @Override
  public String toString() {
    return "Box{" +
            "boxNum='" + boxNum + '\'' +
            ", boxWeight='" + boxWeight + '\'' +
            ", shipped='" + shipped + '\'' +
            ", brand='" + brand + '\'' +
            ", scannedDate='" + scannedDate + '\'' +
            ", scannedBy='" + scannedBy + '\'' +
            ", timeScanned='" + timeScanned + '\'' +
            ", palletID='" + palletID + '\'' +
            '}';
  }
}
