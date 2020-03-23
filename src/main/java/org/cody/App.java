package org.cody;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
  static class Item {
    String gfsNum;
    String halpNum;
    int count;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

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

    Item(String gfsNum) {
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

  public static void main(String[] args) throws IOException {
    PDDocument doc = new PDDocument();
    ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
    ArrayList<String> palletsStr = new ArrayList<>();
    ArrayList<Pallet> pallets = new ArrayList<>();

    //watchForV2();

    try {
      //doc = PDDocument.load(new File("C:\\Users\\cody.jewell\\AppData\\Local\\Temp" +
      //        "\\PRMSAA_67514668335.pdf"));
      doc = PDDocument.load(new File("V2-pal-check.pdf"));
      String pdfText = stripPDF(doc);
      ArrayList<String> lines = getLines(pdfText);
      invoiceLines = getInvoiceLines(lines);
      //System.out.println(pdfText);
      doc.close();
    } catch (Exception e) {
      System.out.println("Caught exception " + e);
      System.out.println(e.getStackTrace());
    } finally {
      doc.close();
    }


    for (InvoiceLine line : invoiceLines) {
      for (Box box : line.getBoxes()) {
        String palletId = box.getPalletID();
        if (!palletsStr.contains(palletId)) {
          palletsStr.add(palletId);
        }
      }
    }

    for (String id : palletsStr) {
      Pallet pallet = new Pallet(id);
      pallets.add(pallet);
    }

    for (InvoiceLine line : invoiceLines) {
      for (Box box : line.getBoxes()) {
        String palletId = box.getPalletID();
        for (Pallet pal : pallets) {
          if (pal.getPalletID().equals(palletId)) {
            try {
              pal.addBox(box);
            } catch (Exception e) {
              System.out.println(e);
            }
          }
        }
      }
    }
    //System.out.println(pallets);
    //writeToExcel(pallets);
    //ArrayList<InvoiceLine> missingLines = getMissingBIBO(invoiceLines);

    // PRINT AN ARRAY OF STRINGS REPRESENTING LINES?
    //printMissingLines(missingLines);

    Map<String, ArrayList<Item>> items = itemsSummary(pallets);

    printSummary(items);

    //printDetailedPalletInfo(items);
  }

  // TODO: Create watcher to watch Temp folder for new V2, then run code.
//  static void watchForV2(){
//    Path dir = Paths.get("C:\\Users\\cody.jewell\\AppData\\Local\\Temp");
//    try{
//      WatchService watcher = FileSystems.getDefault().newWatchService();
//    }catch(Exception e){
//      System.out.println(e);
//    }
//  }

  public static String stripPDF(PDDocument document) throws IOException {
    PDFTextStripper stripper = new PDFTextStripper();
    return stripper.getText(document);
  }


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
      //System.out.println("Start index: " + invoiceNumMatcher.start());
      //System.out.println("End index: " + invoiceNumMatcher.end());
    }
    return lines;
  }


  public static ArrayList<InvoiceLine> getInvoiceLines(ArrayList<String> invoiceLines) {
    String[] lines = invoiceLines.toArray(new String[0]);
    ArrayList<InvoiceLine> invLines = new ArrayList<>();
    for (String line : lines) {
      String invoiceNum = line.substring(0, 11).trim();
      String lineNum = line.substring(12, 16).trim();
      String itemNum = line.substring(17, 26).trim();
      String itemName = line.substring(26, 72).trim();
      if (itemName.contains("FREIGHT") || itemName.contains("SAMPLE")) {
        continue;
      }
      try {
        int ordered = Integer.parseInt(line.substring(72, 74).trim());
        int made = Integer.parseInt(line.substring(82, 84).trim());
        int scanned = Integer.parseInt(line.substring(90, 92).trim());
        String status = line.substring(96, 108).trim();
        String gfsItemNum = line.substring(109, 150).trim();
        ArrayList<Box> boxes = getBoxes(line);
        InvoiceLine invLine = new InvoiceLine(invoiceNum, lineNum, itemNum, itemName, ordered, made,
                scanned, gfsItemNum, boxes);
        invLines.add(invLine);
      } catch (Exception e) {
        continue;
      }

    }
    return invLines;

  }


  public static ArrayList<Box> getBoxes(String line) {


    ArrayList<Box> boxes = new ArrayList<>();
    String boxesStart = "\\.{75}";
    Pattern boxesStartPattern = Pattern.compile(boxesStart);
    Matcher boxesStartMatcher = boxesStartPattern.matcher(line);
    //String boxToEnd;
    boxesStartMatcher.find();
    //boxToEnd = line.substring(boxesStartMatcher.start(), line.length() - 1);

    String boxStart = "( \\. )";
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

      if (boxSplit.size() < 11) {
        boxNum = boxSplit.get(2);
        weight = boxSplit.get(3);
        brand = boxSplit.get(5);
        loadDate = "Not Load Scanned";
        scannedBy = "Not Load Scanned";
        scannedTime = "Not Load Scanned";
        palletID = "Not Load Scanned";
      } else {
        boxNum = boxSplit.get(2);
        weight = boxSplit.get(3);
        brand = boxSplit.get(5);
        loadDate = boxSplit.get(6);
        scannedBy = boxSplit.get(7);
        scannedTime = boxSplit.get(8) + boxSplit.get(9);
        palletID = boxSplit.get(10);
      }


      Box boxFinal = new Box(gfsItemNum, itemName, itemNum, boxNum, weight, "___",
              brand, loadDate, scannedBy, scannedTime, palletID);

      boxes.add(boxFinal);
    }

    return boxes;
  }


  public static void writeToExcel(ArrayList<Pallet> pallets) {
    //Blank workbook
    XSSFWorkbook workbook = new XSSFWorkbook();

    //Create a blank sheet
    XSSFSheet sheet = workbook.createSheet("Pallets");

    //This data needs to be written (Object[])
    Map<String, Object[]> data = new TreeMap<String, Object[]>();


    // TODO: format excel sheet


    data.put("1", new Object[]{"ID", "Boxes"});
    for (int i = 0; i < pallets.size(); i++) {
      Pallet pallet = pallets.get(i);
      for (int y = 0; y < pallet.getBoxes().size(); y++) {
        data.put("" + (i + 2), new Object[]{pallet.getPalletID(), pallet.getBoxes().get(y)});
      }

    }


//    data.put("3", new Object[] {2, "Lokesh", "Gupta"});
//    data.put("4", new Object[] {3, "John", "Adwards"});
//    data.put("5", new Object[] {4, "Brian", "Schultz"});

    //Iterate over data and write to sheet
    Set<String> keyset = data.keySet();
    int rownum = 0;
    for (String key : keyset) {
      Row row = sheet.createRow(rownum++);
      Object[] objArr = data.get(key);
      int cellnum = 0;
      for (Object obj : objArr) {
        Cell cell = row.createCell(cellnum++);
        if (obj instanceof String)
          cell.setCellValue((String) obj);
        else if (obj instanceof Integer)
          cell.setCellValue((Integer) obj);
      }
    }
    try {
      //Write the workbook in file system
      FileOutputStream out = new FileOutputStream(new File("howtodoinjava_demo.xlsx"));
      workbook.write(out);
      out.close();
      System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static ArrayList<InvoiceLine> getMissingBIBO(ArrayList<InvoiceLine> invoiceLines) {
    String itemNumStart = "00-09 00-1 00-2 00-3 00-4 00-5 00-6 00-7 00-8 00-9 01- 02- 03- 04- 05-" +
            " 06- 07- 08- 09- 10- 11- 12- 13- 14- 15- 28-12500 28-12510 28-00010 31- 32- 34- 36- " +
            "37- 38- 43- 44- 45- 46- 47- 48- 49- 50- 51- 52- 53- 54- 57- 58- 70- 71- 72- 73- 74- " +
            "75- 76- 77- 78- 79- 85-12180 88- 95- 96- 97- 98- 99-";

    String[] itemsSplit = itemNumStart.split(" ");

    ArrayList<InvoiceLine> missingLines = new ArrayList<>();

    for (String num : itemsSplit) {
      for (InvoiceLine i : invoiceLines) {
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


  public static void printMissingLines(ArrayList<InvoiceLine> lines) {
    JTextArea hiddenTextArea = new JTextArea();
    int missingNo = 1;
    LocalDateTime dateObj = LocalDateTime.now();
    DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    String formattedDate = dateObj.format(formatObj);
    hiddenTextArea.append("Printed at: " + formattedDate + "\n\n\n");
    for (InvoiceLine line : lines) {
      hiddenTextArea.append("Missing Item Number " + missingNo + ":\n");
      hiddenTextArea.append("Item Name:   " + line.getItemName() + "\n");
      hiddenTextArea.append("Item Number:   " + line.getItemNum() + "\n");
      hiddenTextArea.append("Invoice Number:   " + line.getInvNum() + "\n");
      hiddenTextArea.append("Invoice Line:   " + line.getLineNum() + "\n");
      hiddenTextArea.append("Ordered / Made / Load Scanned:  " + line.getOrderedQty() + "  /  "
              + line.getMadeQty() + "  /  " + line.getScannedQty() + "\n");
      hiddenTextArea.append("\n\n\n");
      missingNo++;
    }
    try {
      hiddenTextArea.print();
    } catch (PrinterException e) {
    }
  }


  public static Map<String, ArrayList<Item>> itemsSummary(ArrayList<Pallet> pallets) {


    JTextArea hiddenTextArea = new JTextArea();

    Map<String, ArrayList<Item>> palletsAndItems = new HashMap<>();


    for (Pallet pallet : pallets) {
      ArrayList<Item> items = new ArrayList<>();
      ArrayList<String> gfsNums = new ArrayList<>();

      for (Box box : pallet.getBoxes()) {
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
        // TODO: Getting items, but NOT by pallet.


      }
//      for (Box box : pallet.getBoxes()){
//        String boxGFS = box.getGfsItemNum();
//        if (!containsName(items, boxGFS)){
//          Item newItem = new Item(gfsNum, box.getItemNum());
//          items.add(newItem);
//        } else {
//          items.get(items.indexOf(new Item(gfsNum, box.getItemNum()))).incCount();
//        }
//      }

//    for (Box box : pallet.getBoxes()) {
//      for (Item item : items) {
//        if (item.getGfsNum().equals(box.getGfsItemNum())) {
//
//        }
//      }
//
//    }
    }
    return palletsAndItems;
    // case count per pallet
    // num cases per item per pallet
  }


  public static boolean containsName(final List<Item> list, final String gfsNum) {
    return list.stream().anyMatch(o -> o.getGfsNum().equals(gfsNum));
  }


  public static void printSummary(Map<String, ArrayList<Item>> palletsWithItems) {
    JTextArea hiddenTextArea = new JTextArea();
    int totalCount = 0;
    for (String key : palletsWithItems.keySet()) {

      for (Item item : palletsWithItems.get(key)) {
        totalCount += item.getCount();
      }
      hiddenTextArea.append("\n" + "PalletID: \t " + key +
              "\n Number of Cases: \t" + totalCount + "\n\n");

      for (Item item : palletsWithItems.get(key)) {
        if (item.getCount() > 0) {
          hiddenTextArea.append("\t" + item.getGfsNum() + " \t " + item.getHalpNum() +
                  " \t " + item.getCount() + "\n");
        }
      }

      hiddenTextArea.append("\n\n\n");
      totalCount = 0;
    }


    try {
      hiddenTextArea.print();
    } catch (PrinterException e) {
    }


  }

//  public static void printDetailedPalletInfo(Map<String, ArrayList<Item>> palletsWithItems) {
//    JTextArea hiddenTextArea = new JTextArea();
//
//    LocalDateTime dateObj = LocalDateTime.now();
//    DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
//    String formattedDate = dateObj.format(formatObj);
//    hiddenTextArea.append("Printed at: " + formattedDate + "\n\n\n");
//
//
//    // LOOP THROUGH BOXES AND ADD THEM?
//
//    Font boldFont = new Font("Arial", Font.BOLD, 20);
//    Font normalFont = new Font("Arial", Font.PLAIN, 10);
//    int count = 1;
//    String prevItem = "";
//
//    for (Pallet pal : pallets) {
//      hiddenTextArea.setFont(boldFont);
//      hiddenTextArea.append("\n" + "PalletID: \t " + pal.getPalletID() +
//              "\n Number of Cases: \t" + pal.getBoxes().size() + "\n");
//      hiddenTextArea.append("Case Number    Weight   \t  GFS Item Number \t Halperns Item " +
//              "Number \n");
//
//      // TODO: KEEP TRACK OF PREV ITEM AND IF ITS NOT THE SAME THEN RESTART BOXCOUNT??
//
//      int boxCount = 1;
//      String prevGFSItemNum = "";
//      for (Box box : pal.getBoxes()) {
//        //hiddenTextArea.append("    " + box.getItemName()+ "\n");
//        hiddenTextArea.setFont(normalFont);
//        if (box.getPalletID().equals(pal.getPalletID())) {
//          hiddenTextArea.append("      " + count + ". (" + boxCount + ".)   " + box.getBoxWeight() +
//                  "  " + box.getGfsItemNum() + "  " + box.getItemNum() +
//                  "\n");
//          if (box.getGfsItemNum() != prevGFSItemNum) {
//            boxCount = 1;
//
//          }
//          ;
//          prevGFSItemNum = box.getGfsItemNum();
//          boxCount++;
//        }
//        count++;
//
//      }
//      count = 1;
//    }
//    try {
//      hiddenTextArea.print();
//    } catch (PrinterException e) {
//    }
//  }
}


//    String invoiceNum = invLine.substring(0, 11).trim();
//    String lineNum = invLine.substring(12, 16).trim();
//    String itemNum = invLine.substring(17, 26).trim();
//    String itemName = invLine.substring(26, 72).trim();
//    int ordered = Integer.parseInt(invLine.substring(72, 74).trim());
//    int made = Integer.parseInt(invLine.substring(82, 84).trim());
//    int scanned = Integer.parseInt(invLine.substring(90, 92).trim());
//    String status = invLine.substring(96, 108).trim();
//    String gfsItemNum = invLine.substring(109, 150).trim();

//  public static void main(String[] args) {
//    String pdfText = pdfToString();
//    String invoiceNumber = "[0-9]{2}-[0-9]{8}";
//    Pattern invoiceNumPattern = Pattern.compile(invoiceNumber);
//    Matcher invoiceNumMatcher = invoiceNumPattern.matcher(pdfText);
//    ArrayList<String> lines = new ArrayList<>();
//    ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
//    ArrayList<Integer> indexs = new ArrayList<>();
//    ArrayList<InvoiceLine> missingLines = new ArrayList<>();
//
//    String itemNumStart = "00-09 00-1 00-2 00-3 00-4 00-5 00-6 00-7 00-8 00-9 01- 02- 03- 04- 05-" +
//            " 06- 07- 08- 09- 10- 11- 12- 13- 14- 15- 28-12500 28-12510 28-00010 31- 32- 34- 36- " +
//            "37- 38- 43- 44- 45- 46- 47- 48- 49- 50- 51- 52- 53- 54- 57- 58- 70- 71- 72- 73- 74- " +
//            "75- 76- 77- 78- 79- 85-12180 88- 95- 96- 97- 98- 99- ";
//    String[] itemsSplit = itemNumStart.split(" ");
//
//
//    while (invoiceNumMatcher.find()) {
//      indexs.add(invoiceNumMatcher.start());
//    }
//
//    for (int i = 0; i <= indexs.toArray().length; i++) {
//      try {
//        if (i == (indexs.toArray().length - 1)) {
//          String object = pdfText.substring(indexs.get(i));
//          InvoiceLine line = getLineObject(object);
//          invoiceLines.add(line);
//        } else {
//          String object = pdfText.substring(indexs.get(i), indexs.get(i + 1) - 1);
//          InvoiceLine line = getLineObject(object);
//          invoiceLines.add(line);
//        }
//      } catch (Exception ignored) {
//        System.out.println(ignored);
//      }
//    }
//
////    for (String num : itemsSplit) {
////      for (InvoiceLine i : invoiceLines) {
////        if (true) {//i.getItemNum().startsWith(num)) {
////          missingLines.add(i);
////        }
////      }
////    }
//
//    Object[] linesArr = missingLines.toArray();
//
//    // UNCOMMENT TO PRINT
//    //JTextArea hiddenTextArea = new JTextArea();
//
//    for (Object o : linesArr) {
//      InvoiceLine line = (InvoiceLine) o;
//      if ((line.getOrderedQty() != line.getMadeQty())) {
//        if (!line.getItemName().contains("SAMPLE")) {
//          if (!line.getItemName().contains("FREIGHT")) {
//            System.out.println(o);
//            //UNCOMMENT TO PRINT
//            //hiddenTextArea.append(o + "\n");
//          }
//        }
//      }
//    }
//
//
//    // UNCOMMENT TO PRINT
//    //try {
//    //  hiddenTextArea.print();
//    //} catch (PrinterException e) {}
//
//
//  }
