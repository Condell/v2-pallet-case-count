package org.cody;

public class InvoiceLine {
  private String invNum;
  private String lineNum;
  private String itemNum;
  private String itemName;
  private int orderedQty;
  private int madeQty;
  private int scannedQty;
  private String gfsItem;

  public InvoiceLine(String inv, String line, String itemNum, String itemName, int ordered,
                     int made, int scanned, String gfsItem) {
    this.invNum = inv;
    this.lineNum = line;
    this.itemNum = itemNum;
    this.itemName = itemName;
    this.orderedQty = ordered;
    this.madeQty = made;
    this.scannedQty = scanned;
    this.gfsItem = gfsItem;
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
            "invNum='" + invNum + '\'' + '\n' +
            ", lineNum='" + lineNum + '\'' + '\n' +
            ", itemNum='" + itemNum + '\'' + '\n' +
            ", itemName='" + itemName + '\'' + '\n' +
            ", orderedQty=" + orderedQty + '\n' +
            ", madeQty=" + madeQty + '\n' +
            ", scannedQty=" + scannedQty + "\n" +
            ", gfsItem=" + gfsItem +
            "}\n\n";
  }
}
