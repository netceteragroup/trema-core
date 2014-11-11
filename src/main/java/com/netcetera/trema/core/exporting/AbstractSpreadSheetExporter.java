package com.netcetera.trema.core.exporting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.netcetera.trema.common.TremaCoreUtil;
import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IExporter;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;
import com.netcetera.trema.core.importing.AbstractFile;


/**
 * Abstract superclass for spreadsheet based exporters.
 */
public abstract class AbstractSpreadSheetExporter implements IExporter {


  /**
   * Constructs the header row for a spreadsheet/table based file.
   * @param masterlanguage the masterlanguage
   * @param languages the languages to export
   * @return the header row.
   */
  protected String [] getHeaderRow(String masterlanguage, String[] languages) {
    List<String> headerRow = new ArrayList<>();
    headerRow.add(AbstractFile.KEY_HEADER);
    headerRow.add(AbstractFile.STATUS_HEADER);
    if (!ArrayUtils.contains(languages, masterlanguage)) {
      headerRow.add(AbstractFile.MASTER_HEADER + " (" + masterlanguage + ")");
    }
    for (String language : languages) {
      headerRow.add(AbstractFile.VALUE_HEADER + " (" + language + ")");
    }
    headerRow.add(AbstractFile.CONTEXT_HEADER);
    return headerRow.toArray(new String[headerRow.size()]);
  }


  /**
   * Constructs a row for a Trema CSV export file.
   * @param masterLanguage the masterLanguage
   * @param textNode the parent text node
   * @param valueNode the value node to export
   * @return the row.
   */
  protected String[] getRow(String masterLanguage, ITextNode textNode, IValueNode valueNode) {
    List<String> row = new ArrayList<>();

    row.add(textNode.getKey());
    row.add(valueNode.getStatus().getName());
    if (!valueNode.getLanguage().equals(masterLanguage)) {
      IValueNode masterValueNode = textNode.getValueNode(masterLanguage);
      if (masterValueNode == null) {
        row.add("");
      } else {
        row.add(masterValueNode.getValue());
      }
    }
    row.add(valueNode.getValue());
    row.add(textNode.getContext());

    return row.toArray(new String[row.size()]);
  }

  /**
   * Gets a 2 dimensional string array representation of the CSV export
   * data ready to be written to a CSV file.
   *
   * @param textNodes the nodes to get the values for
   * @param masterLanguage the masterLanguage
   * @param language the language
   * @param status the states to get the values for. If <code>null</code>, all
   * status will be exported
   * @return the CSV export values.
   */
  protected String[][] getValues(ITextNode[] textNodes, String masterLanguage, String language, Status[] status) {
    int numberOfColumns = (masterLanguage.equals(language)) ? 4 : 5;
    List<String[]> rows = new ArrayList<>();

    // add the header row
    rows.add(getHeaderRow(masterLanguage, new String[]{language}));

    for (ITextNode textNode : textNodes) {
      IValueNode valueNode = textNode.getValueNode(language);
      if (valueNode != null) {
        if (status == null || TremaCoreUtil.containsStatus(valueNode.getStatus(), status)) {
          rows.add(getRow(masterLanguage, textNode, valueNode)); // add the row
        }
      }
    }

    return rows.toArray(new String[rows.size()][numberOfColumns]);
  }

}
