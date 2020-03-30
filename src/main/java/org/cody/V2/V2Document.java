package org.cody.V2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

public class V2Document {

  public static ArrayList<String> getLines(String pdf) {
    ArrayList<String> lines = new ArrayList<>();

    String invoiceNumber = "[0-9]{2}-[0-9]{8}";
    Pattern invoiceNumPattern = Pattern.compile(invoiceNumber);
    Matcher invoiceNumMatcher = invoiceNumPattern.matcher(pdf);
    int oldStart = 0;

    while (invoiceNumMatcher.find()) {
      String line = "";
      if (oldStart != 0) {
        line = pdf.substring(oldStart, invoiceNumMatcher.start());
        lines.add(line);
      }
      oldStart = invoiceNumMatcher.start();
    }
    return lines;
  }

  public static ArrayList<OrderLine> getInvoiceLines(ArrayList<String> invoiceLines) {
    String[] lines = invoiceLines.toArray(new String[0]);
    ArrayList<OrderLine> invLines = new ArrayList<>();
    for (String line : lines) {
      String invoiceNum = line.substring(0, 11).trim();
      String lineNum = line.substring(12, 16).trim();
      String itemNum = line.substring(17, 26).trim();
      String itemName = line.substring(26, 72).trim();
      if (itemName.contains("FREIGHT") || itemName.contains("SAMPLE")) {
        continue;
      }
      try {
        int ordered = Integer.parseInt(line.substring(71, 74).trim());
        int made = Integer.parseInt(line.substring(81, 84).trim());
        int scanned = Integer.parseInt(line.substring(89, 92).trim());
        String status = line.substring(96, 108).trim();
        String gfsItemNum = line.substring(109, 150).trim();
        String warehouse = line.substring(242, 245);
        ArrayList<org.cody.V2.Box> boxes = getBoxes(line);
        OrderLine invLine = new OrderLine(invoiceNum, lineNum, itemNum, itemName, ordered, made,
            scanned, gfsItemNum, warehouse, boxes);
        invLines.add(invLine);
      } catch (Exception e) {
      }

    }
    return invLines;

  }

  public static ArrayList<org.cody.V2.Box> getBoxes(String line) {

    ArrayList<org.cody.V2.Box> boxes = new ArrayList<>();
    String boxesStart = "\\.{75}";
    Pattern boxesStartPattern = Pattern.compile(boxesStart);
    Matcher boxesStartMatcher = boxesStartPattern.matcher(line);
    //String boxToEnd;
    boxesStartMatcher.find();
    //boxToEnd = line.substring(boxesStartMatcher.start(), line.length() - 1);

    String boxStart = "( \\. |\\.[0-9]{2,3}\\w)";
    Pattern boxStartPattern = Pattern.compile(boxStart);
    Matcher boxStartMatcher = boxStartPattern.matcher(line);

    ArrayList<String> boxesStrings = new ArrayList<>();

    int oldStart = 0;

    while (boxStartMatcher.find()) {
      String boxLine = "";
      if (oldStart != 0) {
        boxLine = line.substring(oldStart, boxStartMatcher.start());
        if (boxLine.contains("____")) {
          boxesStrings.add(boxLine);
        }
      }
      oldStart = boxStartMatcher.start();
      //System.out.println("Start index: " + invoiceNumMatcher.start());
      //System.out.println("End index: " + invoiceNumMatcher.end());
    }

    // System.out.println(boxesStrings);

    for (String box : boxesStrings) {
      String[] boxArr = box.split(" ");
      ArrayList<String> boxSplit = new ArrayList<>(Arrays.asList(boxArr));
      boxSplit.removeAll(Collections.singleton(""));
      String itemName = line.substring(26, 72).trim();
      String itemNum = line.substring(17, 26).trim();
      String gfsItemNum = line.substring(109, 150).trim();
      String boxNum = "";
      String weight = "";
      String brand = "";
      String loadDate = "";
      String scannedBy = "";
      String scannedTime = "";
      String palletID = "";

      if (boxSplit.size() == 10) {
        boxNum = boxSplit.get(2);
        weight = boxSplit.get(3);
        brand = "No Brand Listed";
        loadDate = boxSplit.get(5);
        scannedBy = boxSplit.get(6);
        scannedTime = boxSplit.get(7) + boxSplit.get(8);
        palletID = boxSplit.get(9);
      } else if (boxSplit.size() == 11) {
        boxNum = boxSplit.get(2);
        weight = boxSplit.get(3);
        brand = boxSplit.get(5);
        loadDate = boxSplit.get(6);
        scannedBy = boxSplit.get(7);
        scannedTime = boxSplit.get(8) + boxSplit.get(9);
        palletID = boxSplit.get(10);
      } else {
        boxNum = boxSplit.get(2);
        weight = boxSplit.get(3);
        brand = boxSplit.get(5);
        loadDate = "Not Load Scanned";
        scannedBy = "Not Load Scanned";
        scannedTime = "Not Load Scanned";
        palletID = "No Pallet ID";
      }

      org.cody.V2.Box boxFinal = new org.cody.V2.Box(gfsItemNum, itemName, itemNum, boxNum, weight,
          "___",
          brand, loadDate, scannedBy, scannedTime, palletID);

      boxes.add(boxFinal);
    }

    return boxes;
  }

  public static ArrayList<OrderLine> getMissingBIBO(ArrayList<OrderLine> orderLines) {
    String itemNumStart = "00-09 00-1 00-2 00-3 00-4 00-5 00-6 00-7 00-8 00-9 01- 02- 03- 04- 05-" +
        " 06- 07- 08- 09- 10- 11- 12- 13- 14- 15- 28-12500 28-12510 28-00010 31- 32- 34- 36- " +
        "37- 38- 43- 44- 45- 46- 47- 48- 49- 50- 51- 52- 53- 54- 57- 58- 70- 71- 72- 73- 74- " +
        "75- 76- 77- 78- 79- 85-12180 88- 95- 96- 97- 98- 99-";

    String[] itemsSplit = itemNumStart.split(" ");

    ArrayList<OrderLine> missingLines = new ArrayList<>();

    for (String num : itemsSplit) {
      for (OrderLine i : orderLines) {
        if ((i.getItemNum().startsWith(num)) && (i.getOrderedQty() != i.getMadeQty())) {//i
          // .getItemNum()
          // .startsWith(num)) {
          missingLines.add(i);
        }
      }
    }

//    Object[] linesArr = missingLines.toArray();
    return missingLines;

  }

  public static Map<String, ArrayList<Item>> getItemsSummary(ArrayList<Pallet> pallets) {
    JTextArea hiddenTextArea = new JTextArea();
    Map<String, ArrayList<Item>> palletsAndItems = new HashMap<>();

    for (Pallet pallet : pallets) {
      ArrayList<Item> items = new ArrayList<>();
      ArrayList<String> gfsNums = new ArrayList<>();

      for (org.cody.V2.Box box : pallet.getBoxes()) {
        String gfs = box.getGfsItemNum();
        if (gfsNums.indexOf(gfs) == -1) {
          gfsNums.add(gfs);
        }
      }

      for (String gfsNum : gfsNums) {
        items.add(new Item(gfsNum));
      }

      for (Box box : pallet.getBoxes()) {
        if (containsName(items, box.getGfsItemNum())) {
          Item anItem = items.get(items.indexOf(new Item(box.getGfsItemNum())));
          if (anItem.getHalpNum().equals("")) {
            anItem.setHalpNum(box.getItemNum());
          }
          anItem.incCount();
        }

        palletsAndItems.put(pallet.getPalletID(), items);
      }

    }
    return palletsAndItems;
  }

  private static boolean containsName(final List<Item> list, final String gfsNum) {
    return list.stream().anyMatch(o -> o.getGfsNum().equals(gfsNum));
  }
}
