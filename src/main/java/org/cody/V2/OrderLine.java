package org.cody.V2;

import java.util.ArrayList;

public class OrderLine {

  private String invNum;
  private String lineNum;
  private String itemNum;
  private String itemName;
  private int orderedQty;
  private int madeQty;
  private int scannedQty;
  private String gfsItem;
  private String warehouse;
  private ArrayList<Box> boxes;

  public OrderLine(String inv, String line, String itemNum, String itemName, int ordered,
      int made, int scanned, String gfsItem, String warehouse, ArrayList<Box> boxes) {
    this.invNum = inv;
    this.lineNum = line;
    this.itemNum = itemNum;
    this.itemName = itemName;
    this.orderedQty = ordered;
    this.madeQty = made;
    this.scannedQty = scanned;
    this.gfsItem = gfsItem;
    this.warehouse = warehouse;
    this.boxes = new ArrayList<>(boxes);
  }

  public int getOrderedQty() {
    return orderedQty;
  }

  public int getMadeQty() {
    return madeQty;
  }

  public String getItemName() {
    return itemName;
  }

  public int getScannedQty() {
    return scannedQty;
  }

  public String getItemNum() {
    return itemNum;
  }

  public ArrayList<Box> getBoxes() {
    return new ArrayList<>(boxes);
  }

  public String getInvNum() {
    return invNum;
  }

  public String getLineNum() {
    return lineNum;
  }

  public String getWarehouse() {
    return warehouse;
  }

  public String getGfsItem() {
    return gfsItem;
  }

  @Override
  public String toString() {
    return "InvoiceLine{" +
        "invNum='" + invNum + '\'' +
        ", lineNum='" + lineNum + '\'' +
            ", itemNum='" + itemNum + '\'' +
            ", itemName='" + itemName + '\'' +
            ", orderedQty=" + orderedQty +
            ", madeQty=" + madeQty +
            ", scannedQty=" + scannedQty +
            ", gfsItem='" + gfsItem + '\'' +
            ", boxes=" + boxes +
            '}';
  }
}
