package org.cody;


import Printer.V2Printer;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.cody.V2.Item;
import org.cody.V2.OrderLine;
import org.cody.V2.Pallet;
import org.cody.V2.V2Document;


public class App {

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Type in an option, then press Enter:\n");
      System.out.println("1. Print missing BIBO from V2.");
      System.out.println("2. Print pallets and cases.");
      String option = scanner.next();
      //int optionInt = Integer.parseInt(option);

      switch (option) {
        case "1":
          System.out.println(
              "\nPlease run an \"ALL\" V2 using the \"Incomplete Items\" option to get missing BIBO items.");
          watchV2(1);
          break;
        case "2":
          System.out.println(
              "\nPlease run an \"ALL\" V2 using the \"All Items\" option to get pallets and cases.");
          watchV2(2);
          //runApp("C:\\Users\\cody.jewell\\Documents\\Programs\\v2-pallet-case-count\\v2-pallet" +
          //    "-case-count\\PRMSAA_2175717159236.pdf", 2);
          break;
        default:
          System.out.println("Please enter an available option.\n");
          break;
      }
      System.out.println("\n");
    }
  }

  static void runApp(String fileName, int runNo) {

    ArrayList<OrderLine> orderLines = new ArrayList<>();
    ArrayList<String> palletsStr = new ArrayList<>();
    ArrayList<Pallet> pallets = new ArrayList<>();

    try {
      PDDocument doc;
      FileInputStream inputStream = new FileInputStream(fileName);
      doc = PDDocument.load(inputStream);
      String pdfText = stripPDF(doc);
      ArrayList<String> lines = V2Document.getLines(pdfText);
      orderLines = V2Document.getInvoiceLines(lines);
      doc.close();
    } catch (Exception e) {
      System.out.println("Caught exception " + e);
      System.out.println(e.getStackTrace());
    }

    for (OrderLine line : orderLines) {
      for (org.cody.V2.Box box : line.getBoxes()) {
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

    for (OrderLine line : orderLines) {
      for (org.cody.V2.Box box : line.getBoxes()) {
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

    if (runNo == 1) {
      ArrayList<OrderLine> missingLines = V2Document.getMissingBIBO(orderLines);
      V2Printer.printMissingLines(missingLines);
    }

    if (runNo == 2) {
      Map<String, ArrayList<Item>> items = V2Document.getItemsSummary(pallets);

      // TODO: sort Map by pallet tag
      TreeMap<String, ArrayList<Item>> sorted = new TreeMap<>(items);
//      for (String str : sorted.keySet()){
//        System.out.println(str);
//      }
      V2Printer.printSummary(sorted);
    }
    //printDetailedPalletInfo(items);
  }

  static void watchV2(int runNo) {
    String fileName = "";
    try {
      WatchService watchService = FileSystems.getDefault().newWatchService();
      String pathStr = "C:\\Users\\" + System.getProperty("user.name") +
          "\\AppData\\Local\\Temp";
      Path path = Paths.get(pathStr);

      path.register(
          watchService,
          StandardWatchEventKinds.ENTRY_CREATE);

      WatchKey key;

      while ((key = watchService.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
          if (pathStr.concat("\\" + event.context()).endsWith(".pdf")) {
            fileName = pathStr.concat("\\" + event.context());
            //fileName = fileName.substring(0, fileName.length() - 7);
            runApp(fileName, runNo);
          }
        }
        key.reset();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  static String stripPDF(PDDocument document) throws IOException {
    PDFTextStripper stripper = new PDFTextStripper();
    return stripper.getText(document);
  }
}
