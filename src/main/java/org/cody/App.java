package org.cody;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App {
  public static void main(String[] args) {
    String pdfText = pdfToString();
    int currentStartIndex = 0;
    int prevStartIndex = 0;
    // FIND INVOICE NUMBERS
    String invoiceNumber = "[0-9]{2}-[0-9]{8}";
    // Line Number   \s[0-9]{3}\s

    // Item Number   \s[0-9]{2}-[0-9]{5}\s

    // Item Name   "(   \b([0-9]+\S.*?)|(   \b[a-zA-Z]+\S.*))\s+ "

    // Ordered    (\d CS)   1
    // Made    (\d CS)   2
    // Scanned    (\d CS)   3


    Pattern invoiceNumPattern = Pattern.compile(invoiceNumber);
    Matcher invoiceNumMatcher = invoiceNumPattern.matcher(pdfText);
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();

    while (invoiceNumMatcher.find()) {
      //System.out.println("Found value start: " +
      // invoiceNumMatcher.start() );
      //System.out.println("Found value end: " + invoiceNumMatcher
      // .end());

      // CURRENT START INDEX
      // Previous Start

      if (currentStartIndex != prevStartIndex) {
        currentStartIndex = invoiceNumMatcher.start();
        String sub = pdfText.substring(prevStartIndex,
                currentStartIndex
        );
        String[] strings = sub.split(" ");
        lines.add(sub);


        String invNum = "[0-9]{2}-[0-9]{8}";
        Matcher invNumMatcher = Pattern.compile(invNum).matcher(sub);
        boolean foundInvNum = invNumMatcher.find();
        String invNumStr = invNumMatcher.group().trim();

        String lineNum = "\\s[0-9]{3}\\s";
        Matcher lineNumMatcher = Pattern.compile(lineNum).matcher(sub);
        boolean foundLineNum = lineNumMatcher.find();
        String lineNumStr = lineNumMatcher.group().trim();

        String itemNum = "\\s[0-9]{2}-[0-9]{5}\\s";
        Matcher itemNumMatcher = Pattern.compile(itemNum).matcher(sub);
        boolean foundItemNum = itemNumMatcher.find();
        String itemNumStr = itemNumMatcher.group().trim();

        String itemName = "(   \\b([0-9]+\\S.*?)|(   \\b[a-zA-Z]+\\S.*))\\s+ ";
        Matcher itemNameMatcher = Pattern.compile(itemName).matcher(sub);
        boolean foundItemName = itemNameMatcher.find();
        String itemNameStr = itemNameMatcher.group().trim();

        //   ************* WORKING *************
//        String lineNum = "\\s[0-9]{3}\\s";
//        Pattern lineNumPat = Pattern.compile(lineNum);
//        Matcher lineNumMatcher = lineNumPat.matcher(sub);
//        boolean foundLineNo = lineNumMatcher.find();
//        String matched = lineNumMatcher.group().trim();

        InvoiceLine line = new InvoiceLine(invNumStr, lineNumStr, itemNumStr, itemNameStr);
        invoiceLines.add(line);


      }
      prevStartIndex = invoiceNumMatcher.start();


    }
    System.out.println(invoiceLines);

//    String route = "[0-9]{2}-[0-9]{8}";
//    Pattern routePattern = Pattern.compile(route);
//    Matcher routeNumMatcher = invoiceNumPattern.matcher(routePattern);
//    while (routeNumMatcher.find( )) {
//      System.out.println("Found value: " + invoiceNumMatcher.group(0) );
//    }


    //System.out.println(pdfText);

  }

  private static String pdfToString() {
    String text = "";
    try {
      PDDocument document = PDDocument.load(new File("C:\\Users" +
              "\\cody" +
              ".jewell\\Documents\\Programs\\v2-pallet-case-count\\v2-pallet-case-count\\V2.pdf"));
      PDFTextStripper stripper = new PDFTextStripper();
      text = stripper.getText(document);

      //System.out.println(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return text;
  }
}

