package org.cody;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) {
    String pdfText = pdfToString();
    int currentStartIndex = 0;
    int prevStartIndex = 0;
    // FIND INVOICE NUMBERS
    String invoiceNumber = "[0-9]{2}-[0-9]{8}";
    Pattern invoiceNumPattern = Pattern.compile(invoiceNumber);
    Matcher invoiceNumMatcher = invoiceNumPattern.matcher(pdfText);
    ArrayList<String> lines = new ArrayList<>();
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
        System.out.println(strings);

        InvoiceLine line = new InvoiceLine(strings[0], strings[2],
                strings[4]);
        // strings HOLDS AN ARRAY OF ALL THE VALUES IN BETWEEN TWO
        // INVOICE NUMBERS

        // ITEM NAME STARTS AT INDEX 15.


      }
      prevStartIndex = invoiceNumMatcher.start();


    }
    System.out.println(lines);

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

