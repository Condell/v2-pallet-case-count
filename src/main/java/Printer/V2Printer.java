package Printer;

import java.awt.Font;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JTextArea;
import org.cody.V2.Item;
import org.cody.V2.ItemLine;

public class V2Printer {

  public static void printMissingLines(ArrayList<ItemLine> lines) {
    JTextArea hiddenTextArea = new JTextArea();
    Font font = new Font("Arial", Font.PLAIN, 10);
    hiddenTextArea.setFont(font);
    int missingNo = 1;
    LocalDateTime dateObj = LocalDateTime.now();
    DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    String formattedDate = dateObj.format(formatObj);
    hiddenTextArea.append("Printed at: " + formattedDate + "\n\n\n");
    for (ItemLine line : lines) {
      hiddenTextArea.append("Missing Item Number " + missingNo + ":\n");
      hiddenTextArea.append("Invoice Number:   " + line.getInvNum() + "\n");
      hiddenTextArea.append("Invoice Line:   " + line.getLineNum() + "\n");
      hiddenTextArea.append("Item Number:   " + line.getItemNum() + "\n");
      hiddenTextArea.append("Item Name:   " + line.getItemName() + "\n");
      hiddenTextArea.append("Ordered / Made / Load Scanned:  " + line.getOrderedQty() + "  /  "
          + line.getMadeQty() + "  /  " + line.getScannedQty() + "\n");
      hiddenTextArea.append("Warehouse: " + line.getWarehouse());
      hiddenTextArea.append("\n\n\n");
      missingNo++;
    }
    try {
      hiddenTextArea.print();
    } catch (PrinterException e) {
    }
  }


  public static void printSummary(Map<String, ArrayList<Item>> palletsWithItems) {
    JTextArea hiddenTextArea = new JTextArea();
    Font font = new Font("Arial", Font.PLAIN, 10);
    hiddenTextArea.setFont(font);
    LocalDateTime dateObj = LocalDateTime.now();
    DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    String formattedDate = dateObj.format(formatObj);
    hiddenTextArea.append("Printed at: " + formattedDate + "\n\n\n");
    int itemCount = 0;
    int totalCaseCount = 0;
    int totalPalletCount = 0;
    for (String key : palletsWithItems.keySet()) {
      totalPalletCount += 1;
      for (Item item : palletsWithItems.get(key)) {
        itemCount += item.getCount();
        totalCaseCount += item.getCount();
      }
      hiddenTextArea.append("\n" + "PalletID:  " + key +
          "\n          Number of Cases: \t" + itemCount + "\n"
          + "                      GFS Item #       Halperns Item #        Case Quantity\n");

      for (Item item : palletsWithItems.get(key)) {
        if (item.getCount() > 0) {
          hiddenTextArea.append("\t" + item.getGfsNum() + " \t " + item.getHalpNum() +
              " \t               " + item.getCount() + "\n");
        }
      }

      hiddenTextArea.append("\n\n");
      itemCount = 0;
    }

    hiddenTextArea.append("Total Cases:  " + totalCaseCount + "\n");
    hiddenTextArea.append("Total Pallets:  " + totalPalletCount);

    try {
      hiddenTextArea.print();
    } catch (PrinterException e) {
    }


  }
}
