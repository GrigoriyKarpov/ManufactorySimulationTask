package ru.karpov;

import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Getter
public class TaskDataReader {
  private List<ProductionCenter> pcList = new ArrayList<ProductionCenter>();
  private ProductionCenter initPC = null;
  private String fileName;

  public TaskDataReader(String filePath) throws IOException {
    // Read Excel file
    Workbook wb = null;
    File file = new File(filePath);
    fileName = file.getName();

    FileInputStream inputStream = new FileInputStream(file);
    wb = new XSSFWorkbook(inputStream);
    inputStream.close();

    // Read workers count and details count
    final long workersCount = getlong(wb.getSheet("Scenario"), 2, 0);
    final long detailsCount = getlong(wb.getSheet("Scenario"), 2, 1);

    WorkerService ws = new WorkerService(workersCount);

    Sheet sheetProductionCenter = wb.getSheet("ProductionCenter");

    // Read all production centers
    Set<String> setId = new HashSet<String>();
    for (int i = 2; i <= sheetProductionCenter.getLastRowNum(); i++) {
      String id = getString(sheetProductionCenter, i, 0);
      setId.add(id);
      String name = getString(sheetProductionCenter, i, 1);
      BigDecimal performance = BigDecimal.valueOf(getdouble(sheetProductionCenter, i, 2));
      long maxWorkersCount = getlong(sheetProductionCenter, i, 3);
      pcList.add(new ProductionCenter(id, name, maxWorkersCount, performance, ws));
    }

    // Create connections
    Sheet sheetConnection = wb.getSheet("Connection");
    for (int i = 2; i <= sheetConnection.getLastRowNum(); i++) {
      String sourceCenter = getString(sheetConnection, i, 0);
      String destCenter = getString(sheetConnection, i, 1);
      setId.remove(destCenter);

      for (ProductionCenter pc : pcList) {
        if (pc.getId().equals(sourceCenter)) {
          for (ProductionCenter pc2 : pcList) {
            if (pc2.getId().equals(destCenter)) {
              pc.getDependentPC().add(pc2);
              break;
            }
          }
          break;
        }
      }
    }

    // Define first production center and fill it buffer
    for (ProductionCenter pc : pcList) {
      if (setId.contains(pc.getId())) {
        pc.setBuffer(detailsCount);
        initPC = pc;
        break;
      }
    }

    // Set link to first production center in all PC's
    for (ProductionCenter pc : pcList)
      pc.setInitPC(initPC);
  }

  private long getlong(Sheet sheet, int row, int col) {
    Cell cell = sheet.getRow(row).getCell(col);
    if (cell.getCellType().equals(CellType.NUMERIC)) {
      return (long) cell.getNumericCellValue();
    } else {
      return 0;
    }
  }

  private double getdouble(Sheet sheet, int row, int col) {
    Cell cell = sheet.getRow(row).getCell(col);
    if (cell.getCellType().equals(CellType.NUMERIC)) {
      return cell.getNumericCellValue();
    } else {
      return 0.0;
    }
  }

  private String getString(Sheet sheet, int row, int col) {
    Cell cell = sheet.getRow(row).getCell(col);
    if (cell.getCellType().equals(CellType.STRING)) {
      return cell.getStringCellValue();
    } else {
      return "";
    }
  }

  private void readExcelFile(String filePath) {
  }
}
