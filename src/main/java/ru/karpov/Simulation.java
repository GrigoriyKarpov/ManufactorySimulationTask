package ru.karpov;

import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Simulation {
  @Setter
  BigDecimal timeUnit = new BigDecimal(1); // Time unit for simulation
  // Format for outputting decimal numbers
  private final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
  {
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(' ');
  }
  private final DecimalFormat df = new DecimalFormat("0.0", symbols);
  // String constants for output
  private final String tab = ", ";
  private final String newLine = "\r\n";

  // Run simulation for a given dataset in .xlsx file
  public void run(String filePath) {
    TaskDataReader tdr;
    try {
      tdr = new TaskDataReader(filePath);
    } catch (IOException e) {
      System.out.println("ERROR: Cannot find file");
      return;
    }
    List<ProductionCenter> pcList = tdr.getPcList();
    ProductionCenter initPC = tdr.getInitPC();
    // printPCStructure(initPC);

    StringBuilder sbLog = new StringBuilder();
    sbLog.append("Time")
        .append(tab)
        .append("ProductionCenter")
        .append(tab)
        .append("WorkersCount")
        .append(tab)
        .append("BufferCount")
        .append(newLine);

    for (BigDecimal currentTime = new BigDecimal(0); !initPC.isDone(); currentTime = currentTime.add(timeUnit)) {
      try {
        initPC.updateState(timeUnit);
      } catch (Exception e) {
        e.printStackTrace();
      }
      addPCStateToLog(sbLog, currentTime, pcList);
    }

    saveLog(sbLog, tdr.getFileName());
  }

  private void saveLog(StringBuilder sbLog, String fileName) {
    // System.out.println(sbLog.toString());

    String resultFileName;

    try {
      resultFileName = fileName.split("\\.")[0] + " result.csv";
    } catch (Exception e) {
      resultFileName = "result " + new Date() + ".csv";
    }

    try (PrintWriter out = new PrintWriter(resultFileName)) {
      out.println(sbLog);
      System.out.println("Simulation for \"" + fileName + "\" is done. Result in \"" + resultFileName + "\"");
    } catch (Exception e) {
      System.out.println("Error while saving result for \"" + fileName + "\"");
    }
  }

  private void addPCStateToLog(StringBuilder out, BigDecimal time, List<ProductionCenter> pcList) {
    for (ProductionCenter pc : pcList) {
      out.append((df.format(time)))
          .append(tab)
          .append(pc.getName())
          .append(tab)
          .append(pc.getWorkersCount())
          .append(tab)
          .append(pc.getBuffer())
          .append(newLine);
    }
  }

  private void printPCStructure(ProductionCenter pc) {
    System.out.println(" > Structure of Production Centers:");
    printPCStructure(pc, 3);
  }

  private void printPCStructure(ProductionCenter pc, int level) {
    for (int i = 0; i < level; i++)
      System.out.print("  ");
    System.out.println(pc.getName() + " (" + pc.getPerformance() + "; " + pc.getMaxWorkersCount() +
        "; " + pc.getBuffer() + ")");
    for (ProductionCenter pc2 : pc.getDependentPC())
      printPCStructure(pc2, 1 + level);
  }
}
