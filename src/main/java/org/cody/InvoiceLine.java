package org.cody;

import java.util.ArrayList;

public class InvoiceLine {
  private String invNum;
  private String lineNum;
  private String itemNum;
  private String itemName;
  private int orderedQty;
  private int madeQty;
  private int scannedQty;
  private String gfsItem;
  private ArrayList<Box> boxes;

  public InvoiceLine(String inv, String line, String itemNum, String itemName, int ordered,
                     int made, int scanned, String gfsItem, ArrayList<Box> boxes) {
    this.invNum = inv;
    this.lineNum = line;
    this.itemNum = itemNum;
    this.itemName = itemName;
    this.orderedQty = ordered;
    this.madeQty = made;
    this.scannedQty = scanned;
    this.gfsItem = gfsItem;
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
