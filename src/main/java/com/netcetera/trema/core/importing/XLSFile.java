package com.netcetera.trema.core.importing;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.Status;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Represents a XLS text resource file.
 */
public class XLSFile extends AbstractFile {

  /** Comment for <code>SHEET_NAME</code>. */
  public static final String SHEET_NAME = "Text Resources";
  /** Comment for <code>LOG</code>. */
  public static final Logger LOG = Logger.getLogger(XLSFile.class.getName());


  private String pathName = null;
  private String masterLanguage = null;
  private String language = null;

  private final Hashtable<String, Integer> cellMap = new Hashtable<>();

  /**
   * Constructs a new CSV file from a path name.
   * @param pathName the path
   * @throws ParseException if any parse errors ocur
   * supported
   */
  public XLSFile(String pathName) throws ParseException {
    this.pathName = pathName;
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(pathName);
      POIFSFileSystem fs = new POIFSFileSystem(fileInputStream);
      Workbook ws = new HSSFWorkbook(fs);
      parse(ws);
    } catch (IOException e) {
      throw new ParseException("Failed to import file: " + e.getMessage());
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (IOException e) {
          // Ignore
        }
      }
    }
  }


  private void analyzeHeader(Row header) throws ParseException {
    cellMap.clear();

    int firstCell = header.getFirstCellNum();
    int lastCell = header.getLastCellNum();

    for (int i = firstCell; i < lastCell; i++) {
      Cell c = header.getCell(i);
      String value = c.getRichStringCellValue().getString();
      if (value == null) {
        continue;
      }
      if (value.contains(AbstractFile.KEY_HEADER)) {
        cellMap.put(AbstractFile.KEY_HEADER, i);
        LOG.info("Key column found in Header, column index is: " + i);
      } else if (value.contains(AbstractFile.STATUS_HEADER)) {
        cellMap.put(AbstractFile.STATUS_HEADER, i);
        LOG.info("Status column found in Header, column index is: " + i);
      } else if (value.contains(AbstractFile.CONTEXT_HEADER)) {
        cellMap.put(AbstractFile.CONTEXT_HEADER, i);
        LOG.info("Context column found in Header, column index is: " + i);
      } else if (value.contains(AbstractFile.MASTER_HEADER)) {
        cellMap.put(AbstractFile.MASTER_HEADER, i);
        LOG.info("Master column found in Header, column index is: " + i);
        String master = StringUtils.substringBetween(value, "(", ")");
        if (master == null) {
          throw new ParseException("Found master column but no masterlanguage");
        } else {
          masterLanguage = master;
        }
      } else if (value.contains(AbstractFile.VALUE_HEADER)) {
        cellMap.put(AbstractFile.VALUE_HEADER, i);
        LOG.info("Value column found in Header, column index is: " + i);
        String lang = StringUtils.substringBetween(value, "(", ")");
        if (lang == null) {
          throw new ParseException("Found value column but no language");
        } else {
          language = lang;
        }
      }
    }
    // four headers are at least needed.
    checkForHeader(KEY_HEADER);
    checkForHeader(STATUS_HEADER);
    checkForHeader(VALUE_HEADER);
    checkForHeader(CONTEXT_HEADER);
  }

  private void checkForHeader(String headerName) throws ParseException {
    if (cellMap.get(headerName) == null) {
      LOG.log(java.util.logging.Level.SEVERE, "Header row check failed, column not found, columnname:" + headerName);
      throw new ParseException("Header not found in file, headername:" + headerName);
    }
  }


  /**
   * Parses a CSV file from a given reader.
   * @throws ParseException if any parse errors ocur
   */
  private void parse(Workbook ws) throws ParseException {
    Sheet sheet = ws.getSheet(SHEET_NAME);
    if (sheet == null) {
      sheet = ws.getSheetAt(0);
    }
    if (sheet == null) {
      throw new ParseException("No sheet found");
    }

    int firstRow = sheet.getFirstRowNum();
    int lastRow = sheet.getLastRowNum();
    LOG.info("first row is:" + firstRow + ", last row is:" + lastRow);

    //first row is expected to be the header.
    analyzeHeader(sheet.getRow(firstRow));

    for (int i = firstRow + 1; i <= lastRow; i++) {
      Row r = sheet.getRow(i);
      extractRowData(r, cellMap);
    }
  }

  private void extractRowData(Row r, Hashtable<String, Integer> cellmap) throws ParseException {
    if (r == null) {
      LOG.info("ignoring row it is null");
      return;
    }
    Cell keyCell = r.getCell(cellmap.get(AbstractFile.KEY_HEADER), Row.RETURN_BLANK_AS_NULL);
    Cell statusCell = r.getCell(cellmap.get(AbstractFile.STATUS_HEADER), Row.RETURN_BLANK_AS_NULL);
    Cell valueCell = r.getCell(cellmap.get(AbstractFile.VALUE_HEADER), Row.RETURN_NULL_AND_BLANK);
    // Master Cell can be null, if the xls is the export of the master language
    Cell masterCell = null;
    if (cellmap.containsKey(AbstractFile.MASTER_HEADER)) {
      masterCell = r.getCell(cellmap.get(AbstractFile.MASTER_HEADER), Row.RETURN_BLANK_AS_NULL);
    }

    // sometimes there are blank lines in the xls file which are ignored
    if (keyCell == null && masterCell == null && statusCell == null
        && (valueCell == null || valueCell.getCellType() == Cell.CELL_TYPE_BLANK)) {
      LOG.info("ignoring row: all cells are null or blank, rownumber:" + r.getRowNum());
      return;
    }
    String keyStr = extractCellValue(keyCell, r.getRowNum(), AbstractFile.KEY_HEADER);
    String masterStr = null;
    // not all xls have a master column (the xls that is the master itself doesn't have a master column)
    if (masterCell != null) {
      masterStr = extractCellValue(masterCell, r.getRowNum(), AbstractFile.MASTER_HEADER);
    }
    String statusStr = extractCellValue(statusCell, r.getRowNum(), AbstractFile.STATUS_HEADER);
    String valueStr = extractCellValue(valueCell, r.getRowNum(), AbstractFile.VALUE_HEADER);
    LOG.info("adding entry, rownumber:" + r.getRowNum() + ", key:" + keyStr
        + ", master:" + masterStr + ", status:" + statusStr + ", value:" + valueStr);
    add(keyStr, Status.valueOf(statusStr), masterStr, valueStr);
  }

  private String extractCellValue(Cell cell, int rowNumber, String columnType) throws ParseException {
    if (cell == null) {
      throw new ParseException(
          "Cell is null, rownumber:" + rowNumber + ", columntype:" + columnType);
    }
    String cellText;
    int cellType = cell.getCellType();
    switch (cellType) {
      case Cell.CELL_TYPE_STRING:
        cellText = cell.getRichStringCellValue() != null ? cell.getRichStringCellValue().getString() : "";
        break;
      case Cell.CELL_TYPE_NUMERIC:
        cellText = Double.valueOf(cell.getNumericCellValue()).toString();
        break;
      case Cell.CELL_TYPE_BLANK:
        cellText = "";
        break;
      case Cell.CELL_TYPE_BOOLEAN:
        throw new ParseException("Unsupported cell type CELL_TYPE_BOOLEAN, rownumber:"
            + rowNumber + ", columntype:" + columnType);
      case Cell.CELL_TYPE_FORMULA:
        throw new ParseException("Unsupported cell type CELL_TYPE_FORMULA, rownumber:"
            + rowNumber + ", columntype:" + columnType);
      case Cell.CELL_TYPE_ERROR:
        throw new ParseException("Unsupported cell type CELL_TYPE_ERROR, rownumber:"
            + rowNumber + ", columntype:" + columnType);
      default:
        throw new ParseException("Unsupported cell type:" + cellType + " , rownumber:"
            + rowNumber + ", columntype:" + columnType);
    }
    return cellText;
  }


  /**
   * Gets the pathname of this CSV file.
   * @return the pathname of this CSV file.
   */
  public String getPathname() {
    return pathName;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasMasterLanguage() {
    return masterLanguage != null;
  }

  /** {@inheritDoc} */
  @Override
  public String getLanguage() {
    return language;
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public String getMasterLanguage() {
    return masterLanguage;
  }

}
