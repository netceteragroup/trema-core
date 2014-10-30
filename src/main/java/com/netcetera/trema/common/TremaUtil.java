package com.netcetera.trema.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/** A collection of utility methods for the whole Trema package. */
public final class TremaUtil {

  /**
   * Private constructor.
   */
  private TremaUtil() {
  }

  /**
   * Gets the languages of all value nodes whose parents are a given
   * array of text nodes.
   * @param textNodes the text nodes to extract the languages from
   * @return a sorted set containing all languages or an empty set if
   * none
   */
  public static Set<String> getLanguages(final ITextNode[] textNodes) {
    SortedSet<String> languages = new TreeSet<String>();
    for (ITextNode textNode : textNodes) {
      IValueNode[] valueNodes = textNode.getValueNodes();
      for (int j = 0; j < valueNodes.length; j++) {
        languages.add(valueNodes[j].getLanguage());
      }
    }
    return languages;
  }

  /**
   * Checks whether a given status is contained in an array of status.
   * @param status the status to be looked for
   * @param statusArray the status array.
   * @return true if the given status is contained in the array.
   */
  public static boolean containsStatus(final Status status, final Status[] statusArray) {
    if (status == null) {
      return false;
    }
    
    for (int i = 0; i < statusArray.length; i++) {
      if (statusArray[i] == status) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Gets the default system encoding (system property
   * <code>"file.encoding"</code>).
   * @return the default system encoding or <code>null</code> if none.
   */
  public static String getSystemDefaultEncoding() {
    String systemDefaultEncoding = null;
    try {
      systemDefaultEncoding = System.getProperty("file.encoding");
    } catch (RuntimeException e) {
      // ignore
    }
    return systemDefaultEncoding;
  }
  
 
  /**
   * Strips the extension of a given file name. All characters after
   * the last dot (<code>.</code>) are removed. If there is no dot or
   * just a single one at the beginning, the given file name will be
   * returned.
   * @param fileName the file name
   * @return the file name without extension.
   */
  public static String stripExtension(String fileName) {
    if (fileName == null) {
      return null;
    }
    
    int index = fileName.lastIndexOf('.');    
    if (index == -1 || index == 0) {
      return fileName;
    }
    
    return fileName.substring(0, index);
  }
  
  /**
   * Returns an empty string if a given string is <code>null</code>.
   * @param s the string to be checked
   * @return an empty string if the given string is
   * <code>null</code>. Otherwise, the given string ist just returned.
   */
  public static String emptyStringIfNull(String s) {
    return (s == null) ? "" : s;
  }
  
  /**
   * Gets the default system line separator (system property
   * <code>"line.separator"</code>).
   * @return the default system line separator or <code>"\n"</code> if
   * anything goes wrong
   */
  public static String getDefaultLineSeparator() {
    try {
      String lineSeparator = System.getProperty("line.separator");
      if (lineSeparator != null) {
        return lineSeparator;
      }
    } catch (RuntimeException e) {
      // ignore
    }
    return "\n";
  }

  /**
   * Compares 2 <code>String</code>s.
   * @param string1 the first string
   * @param string2 the second string
   * @return true if both strings are null or
   * <code>string1.equals(string2)</code> holds true
   */
  public static boolean equalsOrNull(String string1, String string2) {
    if (string1 == null) {
      return string2 == null;
    }
    return string1.equals(string2);
  }
  
  /**
   * Concatenates the elements in an array to a string using a given
   * delimiter string.
   * <p>
   * Returns <code>"[null]"</code> if the array is <code>null</code> or
   * <code>"[none]"</code> if the array is empty.
   * @param array the elements
   * @param delimiter the delimiter to be inserted between elements
   * @return concatenated string with elements separated by the delimiter
   * string
   */
  public static String arrayToString(Object[] array, String delimiter) {
    if (array == null) {
      return "[null]";
    }
    if (array.length == 0) {
      return "[none]";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        sb.append(delimiter);
      }
      sb.append(array[i].toString());
    }
    return sb.toString();
  }
  
  /**
   * Concatenates the elements in a list to a string using a given
   * delimiter string.
   * <p>
   * Returns <code>"[null]"</code> if the list is <code>null</code> or
   * <code>"[none]"</code> if the list is empty.
   * @param list the list
   * @param delimiter the delimiter to be inserted between elements
   * @return concatenated string with elements separated by the delimiter
   * string
   */
  //TODO tzueblin 20081114: Commons for this?
  public static String listToString(List<?> list, String delimiter) {
    if (list == null) {
      return "[null]";
    }
    if (list.size() == 0) {
      return "[none]";
    }
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Iterator<?> iterator = list.iterator(); iterator.hasNext(); i++) {
      if (i > 0) {
        sb.append(delimiter);
      }
      sb.append(iterator.next().toString());
    }
    return sb.toString();
  }
  
  /**
   * Converts a given array containing <code>String</code>s to a
   * <code>String[]</code> array. This method should only be called if
   * you know <b>for sure</b> that the type of the elements in the
   * given array is <code>String</code>.
   * @param strings the array to be converted. Must only contain elements
   * of type <code>String</code>.
   * @return the converted array.
   */
  public static String[] toStringArray(Object[] strings) {
    if (strings == null) {
      return null;
    }
    
    String[] stringArray = new String[strings.length];
    System.arraycopy(strings, 0, stringArray, 0, strings.length);
    return stringArray;
  }
  
  /**
   * Inserts a <code>String</code> element at the beginning of a given
   * <code>String</code> array and shifts the subsequent elements
   * towards the back. If thereby the array size gets greater than a
   * given limit, the last element is dropped.
   * @param pool the string array to rotate, may be <code>null</code>.
   * The size of this array must <b>not</b> be greater than
   * <code>size</code>.
   * @param element the element to insert
   * @param size the rotation size
   * @return a rotated array
   */
  public static String[] rotate(String[] pool, String element, int size) {
    if (pool == null) {
      return new String[] {element};
    }
    
    List<String> poolList = new ArrayList<String>(Arrays.asList(pool));
    if (!poolList.contains(element)) {
      poolList.add(0, element);
      if (poolList.size() > size) {
        poolList.remove(poolList.size() - 1);
      }
    }
    return poolList.toArray(new String[poolList.size()]);
  }

  
}
