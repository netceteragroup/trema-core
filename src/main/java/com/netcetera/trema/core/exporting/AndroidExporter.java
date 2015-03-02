package com.netcetera.trema.core.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Exports an <code>IDatabase</code> to a Android "strings.xml" file.
 */
public class AndroidExporter implements IExporter {

  public static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  private final File outputFile;
  private final OutputStreamFactory outputStreamFactory;
  private final IExportFilter[] iExportFilters;
  private final HashMap<String, String> placeholderMap;

  /**
   * Constructor.
   *
   * @param outputFile the file used to write the output into
   * @param outputStreamFactory factory used to create the outputstream to the file
   */
  public AndroidExporter(File outputFile, OutputStreamFactory outputStreamFactory) {
    this.outputFile = outputFile;
    this.outputStreamFactory = outputStreamFactory;
    iExportFilters = new IExportFilter[1];
    iExportFilters[0] = new AndroidExportFilter();

    placeholderMap = new HashMap<>();
    placeholderMap.put("%d", "%%%d\\$d");
    placeholderMap.put("%i", "%%%d\\$d");
    placeholderMap.put("%o", "%%%d\\$o");
    placeholderMap.put("%u", "%%%d\\$d");
    placeholderMap.put("%x", "%%%d\\$x");
    placeholderMap.put("%X", "%%%d\\$X");
    placeholderMap.put("%f", "%%%d\\$f");
    placeholderMap.put("%e", "%%%d\\$e");
    placeholderMap.put("%E", "%%%d\\$E");
    placeholderMap.put("%g", "%%%d\\$g");
    placeholderMap.put("%G", "%%%d\\$G");
    placeholderMap.put("%c", "%%%d\\$c");
    placeholderMap.put("%s", "%%%d\\$s");
    placeholderMap.put("%@", "%%%d\\$s");
  }

  /** {@inheritDoc} */
  @Override
  public void export(ITextNode[] nodes, String masterlanguage, String language, Status[] states)
      throws ExportException {
    LOG.info("Exporting Android XML file...");
    try (OutputStream outputStream = outputStreamFactory.createOutputStream(outputFile);
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {
      synchronized (this) {
        // write header
        bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        bw.write("\n");
        bw.write("<!-- Generated file - do not edit -->");
        bw.write("\n");
        bw.write("<resources>");
        bw.write("\n");

        for (ITextNode node : nodes) {
          IValueNode valueNode = node.getValueNode(language);

          // get value from value node
          if (valueNode != null) {
            if (states == null || TremaCoreUtil.containsStatus(valueNode.getStatus(), states)) {
              IKeyValuePair keyValuePair = new KeyValuePair(node.getKey(), valueNode.getValue());
              for (IExportFilter filter : iExportFilters) {
                filter.filter(keyValuePair);
              }

              // validate key after it has been filtered
              String key = keyValuePair.getKey();
              if (!isValidKeyName(key)) {
                throw new IllegalArgumentException("Invalid string key name "
                    + (key != null ? "'" + key + "'" : "null"));
              }

              // map the placeholders and write the result
              String value = keyValuePair.getValue();
              if (value != null) {
                String formattedText = resolveIOSPlaceholders(value);
                String rowText = String.format("  <string name=\"%s\">%s</string>", key, formattedText);
                bw.write(rowText);
                bw.write("\n");
              }
            }
          }
        }

        // write footer
        bw.write("</resources>");

      }

      bw.flush();

    } catch (IOException e) {
      throw new ExportException("Could not store properties:" + e.getMessage());
    }
    LOG.info("Exporting of Android XML file finished.");
  }

  /**
   * Returns a string with resolved iOS placeholders.
   * @param original the original text
   * @return The text with resolved iOS placeholders (if there were some).
   */
  protected String resolveIOSPlaceholders(String original) {
    int index = 1;
    String resolved = original;

    final Pattern pattern = Pattern.compile("%[a-zA-Z@]");
    final Matcher matcher = pattern.matcher(original);
    while (matcher.find()) {
      String placeholderIOS = matcher.group();
      String placeholderAndroid = placeholderMap.get(placeholderIOS);
      if (placeholderAndroid != null) {
        placeholderAndroid = String.format(placeholderAndroid, index++);
        resolved = resolved.replaceFirst(placeholderIOS, placeholderAndroid);
      }
    }

    return resolved;
  }


  /**
   * Checks if the key name is valid. Valid key names in android are same as valid Java variables
   * [a-zA-Z][a-zA-Z0-9_]*.
   *
   * @param key to check
   * @return true if the key is valid, false if not
   */
  protected boolean isValidKeyName(String key) {
    // make sure that the key is not null
    if (key == null) {
      return false;
    }

    // check normal variable name
    return key.matches("[a-zA-Z_][a-zA-Z0-9_]*");
  }
}
