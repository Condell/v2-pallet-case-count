package org.cody;

public class InvoiceLine {
  private String invNum;
  private String lineNum;
  private String itemNum;
  private String itemName;

  public InvoiceLine(String inv, String line, String itemNum, String itemName) {
    this.invNum = inv;
    this.lineNum = line;
    this.itemNum = itemNum;
    this.itemName = itemName;
  }
}
