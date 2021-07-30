package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.importing.XLSFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;


/**
 * Exporter for the xls format.
 */
public class XLSExporter extends AbstractSpreadSheetExporter {

  public static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  private File outputfile;
  private static final int COLUMNWIDTH = 256 * 20;

  /**
   * Constructor.
   *
   * @param outputfile the output file
   */
  public XLSExporter(File outputfile) {
    this.outputfile = outputfile;
  }

  /**
   * For unittests.
   */
  public XLSExporter() {

  }

  /** {@inheritDoc} */
  @Override
  public void export(ITextNode [] nodes, String masterlanguage, String language, Status[] states)
  throws ExportException {
    LOG.info("Exporting XLS file...");
    Workbook wb = new HSSFWorkbook();
    Map<String, CellStyle> styles = createStyles(wb);
    Sheet sheet = wb.createSheet(XLSFile.SHEET_NAME);
    //turn off gridlines
    sheet.setDisplayGridlines(true);
    sheet.setPrintGridlines(false);
    sheet.setFitToPage(true);
    sheet.setHorizontallyCenter(true);
    PrintSetup printSetup = sheet.getPrintSetup();
    printSetup.setLandscape(true);

    //the following three statements are required only for HSSF
    sheet.setAutobreaks(true);
    printSetup.setFitHeight((short) 1);
    printSetup.setFitWidth((short) 1);

    String [][] values = getValues(nodes, masterlanguage, language, states);

    Row headerRow = sheet.createRow(0);
    String [] header = values[0];
    for (int i = 0; i < header.length; i++) {
      Cell c = headerRow.createCell(i);
      c.setCellValue(header[i]);
      c.setCellStyle(styles.get("header"));
      sheet.setColumnWidth(i, COLUMNWIDTH);
    }
    sheet.createFreezePane(0, 1);

    Row textRow;
    Cell cell;
    int rownum = 1;
    for (int i = 1; i < values.length; i++, rownum++) {
      String[] row = values[i];
      textRow = sheet.createRow(rownum);
      for (int j = 0; j < row.length; j++) {
        cell = textRow.createCell(j);
        cell.setCellValue(row[j]);
        cell.setCellStyle(styles.get("cell_b"));
      }
    }

    // Write the output to a file
    try (FileOutputStream out = new FileOutputStream(outputfile)) {
      wb.write(out);
    } catch (FileNotFoundException e) {
      throw new ExportException("Could not create output file", e);
    } catch (IOException e) {
      throw new ExportException("Could not write to output file", e);
    }
    LOG.info("Exporting of XLS file finished.");
  }


  /**
   * Cell styles used.
   */
  private static Map<String, CellStyle> createStyles(Workbook wb) {
    Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

    CellStyle style;
    Font headerFont = wb.createFont();
    style = createBorderedStyle(wb);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setFont(headerFont);
    styles.put("header", style);


    Font font1 = wb.createFont();
    style = createBorderedStyle(wb);
    style.setAlignment(HorizontalAlignment.LEFT);
    style.setFont(font1);
    styles.put("cell_b", style);

    return styles;

  }

  private static CellStyle createBorderedStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    style.setBorderRight(BorderStyle.THIN);
    style.setRightBorderColor(HSSFColorPredefined.BLACK.getIndex());
    style.setBorderBottom(BorderStyle.THIN);
    style.setBottomBorderColor(HSSFColorPredefined.BLACK.getIndex());
    style.setBorderLeft(BorderStyle.THIN);
    style.setLeftBorderColor(HSSFColorPredefined.BLACK.getIndex());
    style.setBorderTop(BorderStyle.THIN);
    style.setTopBorderColor(HSSFColorPredefined.BLACK.getIndex());
    return style;
  }

}
