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
    String invoiceNumber = "[0-9]{2}-[0-9]{8}";
    Pattern invoiceNumPattern = Pattern.compile(invoiceNumber);
    Matcher invoiceNumMatcher = invoiceNumPattern.matcher(pdfText);
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
    ArrayList<Integer> indexs = new ArrayList<>();
    ArrayList<InvoiceLine> missingLines = new ArrayList<>();

    String itemNumStart = "00-09 00-1 00-2 00-3 00-4 00-5 00-6 00-7 00-8 00-9 01- 02- 03- 04- 05-" +
            " 06- 07- 08- 09- 10- 11- 12- 13- 14- 15- 28-12500 28-12510 28-00010 31- 32- 34- 36- " +
            "37- 38- 43- 44- 45- 46- 47- 48- 49- 50- 51- 52- 53- 54- 57- 58- 70- 71- 72- 73- 74- " +
            "75- 76- 77- 78- 79- 85-12180 88- 95- 96- 97- 98- 99- ";
    String[] itemsSplit = itemNumStart.split(" ");


    while (invoiceNumMatcher.find()) {
      indexs.add(invoiceNumMatcher.start());
    }

    for (int i = 0; i < indexs.toArray().length; i++) {
      try {
        if (i == (indexs.toArray().length - 1)) {
          String object = pdfText.substring(indexs.get(i));
          InvoiceLine line = getLineObject(object);
          invoiceLines.add(line);
        } else {
          String object = pdfText.substring(indexs.get(i), indexs.get(i + 1) - 1);
          InvoiceLine line = getLineObject(object);
          invoiceLines.add(line);
        }
      } catch (Exception ignored) {
      }
    }

    for (String num : itemsSplit) {
      for (InvoiceLine i : invoiceLines) {
        if (i.getItemNum().startsWith(num)) {
          missingLines.add(i);
        }
      }
    }

    Object[] linesArr = missingLines.toArray();
    for (Object o : linesArr) {
      InvoiceLine line = (InvoiceLine) o;
      if ((line.getOrderedQty() != line.getMadeQty())) {
        if (!line.getItemName().contains("SAMPLE")) {
          if (!line.getItemName().contains("FREIGHT")) {
            System.out.println(o);
          }
        }
      }
    }

    //System.out.println(pdfText);

  }


  private static String pdfToString() {
    String text = "";
    try {
      PDDocument document = PDDocument.load(new File("C:\\Users\\cody" +
              ".jewell\\AppData\\Local\\Temp\\PRMSAA_591415532277.pdf"));
      PDFTextStripper stripper = new PDFTextStripper();
      text = stripper.getText(document);

      //System.out.println(text);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return text;
  }

  private static InvoiceLine getLineObject(String invLine) {

    String invoiceNum = invLine.substring(0, 11).trim();
    String lineNum = invLine.substring(12, 16).trim();
    String itemNum = invLine.substring(17, 26).trim();
    String itemName = invLine.substring(26, 72).trim();
    int ordered = Integer.parseInt(invLine.substring(72, 74).trim());
    int made = Integer.parseInt(invLine.substring(82, 84).trim());
    int scanned = Integer.parseInt(invLine.substring(90, 92).trim());
    String gfsItemNum = invLine.substring(105, 150).trim();


    return new InvoiceLine(invoiceNum, lineNum, itemNum, itemName, ordered, made,
            scanned, gfsItemNum);

    // NEWEST ITEM NAME REGEX
    //   "( \S+[^ \d{2}\- \d{3} \d{5}].+         (?=\d \QCS\E ))|( \S+[^ \d{2}\- \d{3} \d{5}].*?(?=\d\d \QCS\E ))"
  }
}
