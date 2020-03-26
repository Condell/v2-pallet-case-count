package Printer;

import java.util.ArrayList;

public interface Printer {

  void printSummary();

  void printDetails();

  <T> void printMissing(ArrayList<T> lines);
}
