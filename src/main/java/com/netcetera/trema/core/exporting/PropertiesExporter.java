package com.netcetera.trema.core.exporting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import com.netcetera.trema.common.TremaUtil;
import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IExporter;
import com.netcetera.trema.core.api.IKeyValuePair;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/**
 * Exports an <code>IDatabase</code> to a Java ".properties" file.
 */
public class PropertiesExporter implements IExporter {

  private File outputFile;
  private OutputStreamFactory outputStreamFactory;
  private IExportFilter [] iExportFilters;


  /**
   * Constructor.
   * @param outputFile the file used to write the output into
   * @param outputStreamFactory the factory used to create the outputstream to the file
   */
  public PropertiesExporter(File outputFile, OutputStreamFactory outputStreamFactory) {
    this.outputStreamFactory = outputStreamFactory;
    this.outputFile = outputFile;
  }

  /**
   * Constructs a {@link SortedProperties} map form the database with
   * the given language and status to export.
   * @param nodes the nodes
   * @param language the language
   * @param status the states
   * @return the {@link SortedProperties}
   */
  protected SortedProperties getProperties(ITextNode[]nodes, String language, Status[] status) {
    SortedProperties properties = new SortedProperties();
    for (ITextNode node : nodes) {
      IValueNode valueNode = node.getValueNode(language);
      if (valueNode != null) {
        if (status == null || TremaUtil.containsStatus(valueNode.getStatus(), status)) {
          IKeyValuePair keyValuePair = new KeyValuePair(node.getKey(), valueNode.getValue());
          if (iExportFilters != null) {
            for (IExportFilter filter : iExportFilters) {
              filter.filter(keyValuePair);
            }
          }
          properties.setProperty(keyValuePair.getKey(), keyValuePair.getValue());
        }
      }
    }
    return properties;
  }

  /**
   * Sets a filter chain that is used to process each value that is exported into a property file.
   * @param iExportFilters the filters to use
   */
  public void setExportFilter(IExportFilter [] iExportFilters) {
    this.iExportFilters = iExportFilters;
  }

   /** {@inheritDoc} */
  @Override
  public void export(ITextNode[] nodes, String masterlanguage, String language, Status[] states)
      throws ExportException {
    OutputStream outputStream = null;
    try {
      outputStream = outputStreamFactory.createOutputStream(outputFile);
      String header = "Generated file - do not edit";
      SortedProperties props = getProperties(nodes, language, states);
      props.store(outputStream, header);
    } catch (IOException e) {
      throw new ExportException("Could not store properties:" + e.getMessage());
    } finally {
      if (outputStream != null) {
          try {
            outputStream.close();
          } catch (IOException e) {
            throw new ExportException("Could not store properties:" + e.getMessage());
          }
      }
    }
  }

  /**
   * Sorted properties.
   */
  static class SortedProperties extends Properties {
    /**
     * Overrides, called by the store method.
     * @return sorted property keys
     */
    @Override
    public synchronized Enumeration<Object> keys() {
      return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }
  }
}

