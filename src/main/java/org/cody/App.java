package org.cody;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App {
  public static void main(String[] args) throws IOException {
    PDDocument doc = new PDDocument();
    ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();


    try {
      doc = PDDocument.load(new File("C:\\Users\\cody.jewell\\AppData\\Local\\Temp" +
              "\\PRMSAA_1526815567539.pdf"));
      String pdfText = stripPDF(doc);
      ArrayList<String> lines = getLines(pdfText);
      invoiceLines = getInvoiceLines(lines);
      System.out.println(pdfText);

    } catch (Exception e) {
      System.out.println("Caught exception " + e);
      System.out.println(e.getStackTrace());
    } finally {
      doc.close();
    }

//    // TODO: Items on each pallet
//
//    for (InvoiceLine line: invoiceLines){
//
//    }
  }

  static String stripPDF(PDDocument document) throws IOException {
    PDFTextStripper stripper = new PDFTextStripper();
    return stripper.getText(document);
  }

  static ArrayList<String> getLines(String pdf) {
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

  static ArrayList<InvoiceLine> getInvoiceLines(ArrayList<String> invoiceLines) {
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
      int ordered = Integer.parseInt(line.substring(72, 74).trim());
      int made = Integer.parseInt(line.substring(82, 84).trim());
      int scanned = Integer.parseInt(line.substring(90, 92).trim());
      String status = line.substring(96, 108).trim();
      String gfsItemNum = line.substring(109, 150).trim();
      ArrayList<Box> boxes = getBoxes(line);
      InvoiceLine invLine = new InvoiceLine(invoiceNum, lineNum, itemNum, itemName, ordered, made,
              scanned, gfsItemNum, boxes);
      invLines.add(invLine);
    }
    return invLines;

  }

  static ArrayList<Box> getBoxes(String line) {


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

    System.out.println(boxesStrings);

    for (String box : boxesStrings) {
      String[] boxArr = box.split(" ");
      ArrayList<String> boxSplit = new ArrayList<>(Arrays.asList(boxArr));
      boxSplit.removeAll(Collections.singleton(""));
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


      Box boxFinal = new Box(boxNum, weight, "___",
              brand, loadDate, scannedBy, scannedTime, palletID);

      boxes.add(boxFinal);
    }

    return boxes;
  }

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
