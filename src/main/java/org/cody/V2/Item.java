package org.cody.V2;


public class Item {

  private String gfsNum;
  private String halpNum;
  private int count;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Item item = (Item) o;

    return gfsNum.equals(item.gfsNum);
  }

  @Override
  public int hashCode() {
    return gfsNum.hashCode();
  }

  public String getGfsNum() {
    return gfsNum;
  }

  Item(String gfsNum, String halpNum) {
    this.count = 0;
    this.gfsNum = gfsNum;
    this.halpNum = halpNum;

  }

  public Item(String gfsNum) {
    this.count = 0;
    this.gfsNum = gfsNum;
    this.halpNum = "";
  }

  public void setHalpNum(String halpNum) {
    this.halpNum = halpNum;
  }

  public String getHalpNum() {
    return halpNum;
  }

  public int getCount() {
    return count;
  }

  public void incCount() {
    count++;
  }

}