package com.netcetera.trema.common;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/** A collection of utility methods for the whole Trema package. */
public final class TremaCoreUtil {

  /**
   * Private constructor.
   */
  private TremaCoreUtil() {
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
      for (IValueNode valueNode : valueNodes) {
        languages.add(valueNode.getLanguage());
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

    for (Status currentStatus : statusArray) {
      if (currentStatus == status) {
        return true;
      }
    }

    return false;
  }


}
