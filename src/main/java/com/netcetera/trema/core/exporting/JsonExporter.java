package com.netcetera.trema.core.exporting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netcetera.trema.common.TremaCoreUtil;
import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IExporter;
import com.netcetera.trema.core.api.IKeyValuePair;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;

/**
 * Exporter for the JSON format.
 */
public class JsonExporter implements IExporter {

  public static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  private final File outputFile;
  private final OutputStreamFactory outputStreamFactory;
  private IExportFilter[] iExportFilters;

  /**
   * Constructor.
   * @param outputFile the file used to write the output into
   * @param outputStreamFactory the factory used to create the outputstream to the file
   */
  public JsonExporter(File outputFile, OutputStreamFactory outputStreamFactory) {
    this.outputStreamFactory = outputStreamFactory;
    this.outputFile = outputFile;
  }

  /**
   * Sets a filter chain that is used to process each value that is exported into a property file.
   * @param iExportFilters the filters to use
   */
  public void setExportFilter(IExportFilter [] iExportFilters) {
    this.iExportFilters = iExportFilters;
  }

  /**
   * Constructs a {@link SortedMap} form the database with
   * the given language and status to export.
   * @param nodes the nodes
   * @param language the language
   * @param status the states
   * @return the {@link SortedMap} containing the keys and translations
   */
  protected SortedMap<String, String> getProperties(ITextNode[]nodes, String language, Status[] status) {
    TreeMap<String, String> map = new TreeMap<>();
    for (ITextNode node : nodes) {
      IValueNode valueNode = node.getValueNode(language);
      if (valueNode != null) {
        if (status == null || TremaCoreUtil.containsStatus(valueNode.getStatus(), status)) {
          IKeyValuePair keyValuePair = new KeyValuePair(node.getKey(), valueNode.getValue());
          if (iExportFilters != null) {
            for (IExportFilter filter : iExportFilters) {
              filter.filter(keyValuePair);
            }
          }
          map.put(keyValuePair.getKey(), keyValuePair.getValue());
        }
      }
    }
    return map;
  }

  /**
   * {@inheritDoc}
   * Write the translation for the desired language and states into a JSON file in the format:
   * <pre>
   *   {
   *     &lt;key&gt;: '&lt;value&gt;',
   *     ...
   *   }
   * </pre>
   * @param nodes the nodes to export
   * @param masterLanguage the master language
   * @param language the language to export
   * @param states the states to export (all if null)
   * @throws ExportException thrown if export fails
   */
  @Override
  public void export(ITextNode[] nodes, String masterLanguage, String language, Status[] states)
      throws ExportException {
    LOG.info("Exporting JSON file...");
    SortedMap<String, String> props = getProperties(nodes, language, states);
    String jsonString = toJsonString(props);
    try (OutputStream outputStream = outputStreamFactory.createOutputStream(outputFile)) {
      outputStream.write(jsonString.getBytes(UTF_8));
    } catch (IOException e) {
      throw new ExportException("Could not write json:" + e.getMessage());
    }
    LOG.info("Exporting of JSON file finished.");
  }

  private String toJsonString(SortedMap<String, String> props) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\n");
    if (!props.isEmpty()) {
      final String lastKey = props.lastKey();
      for (Entry<String, String> entry : props.entrySet()) {
        sb = sb.append("\t\"").append(entry.getKey()).append('"').append(": \"").append(escape(entry.getValue()));
        if (!entry.getKey().equals(lastKey)) {
          sb = sb.append("\",\n");
        } else {
          sb = sb.append("\"\n");
        }
      }
    }
    sb.append("}");
    return sb.toString();
  }

  private String escape(String in) {
    return StringUtils.replace(in, "\n", "\\n");
  }

}
