package org.cody.V2;

import java.util.ArrayList;

public class Pallet {
  private String palletID;
  private ArrayList<Box> boxes = new ArrayList<>();

  public Pallet(String id) {
    this.palletID = id;
  }

  public void addBox(Box box) {
    this.boxes.add(box);
  }

  public String getPalletID() {
    return palletID;
  }

  @Override
  public String toString() {
    return "Pallet{" +
            "palletID='" + palletID + '\'' +
            ", boxes=" + boxes +
            '}';
  }

  public ArrayList<Box> getBoxes() {
    return boxes;
  }
}
