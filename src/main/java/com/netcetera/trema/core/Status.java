package com.netcetera.trema.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * Represents a status for a text.
 * <p>
 * <b>Notes</b>:
 * <ul>
 * <li>The name of the status has to match the status string in the XML
 * schema.</li>
 * <li>The status are <b>ordered</b> by a position index. The position
 * is not only used as identifier, but also as index into the
 * ascendingly ordered list of the available status (this is useful in
 * a drop down box of the available status). Thus, the positions of the
 * status <b>must</b> be zero-based and gap-less</li>
 * </ul>
 */
public final class Status implements Comparable<Status> {

  /** This map holds the only instances of this class, one per status.
   *  We use a sorted map with the positions as keys to ease getting
   *  all the available status in ascending order and to make
   *  <code>valueOf(Integer)</code> perform in constant time. On the
   *  other hand we need to traverse the map linearly in the
   *  <code>valueOf(String)</code> method, which, however, is not a big
   *  performance penalty since the number of available status is
   *  small. */
  private static SortedMap<Integer, Status> instances = new TreeMap<Integer, Status>();

  /** Status "initial". */
  public static final Status INITIAL = new Status("initial", 0);

  /** Status "translated". */
  public static final Status TRANSLATED = new Status("translated", 1);

  /** Status "verified". */
  public static final Status VERIFIED = new Status("verified", 2);

  /** Status "special". */
  public static final Status SPECIAL = new Status("special", 3);

  /** Status "undefined". */
  public static final Status UNDEFINED = new Status("undefined", 4);

  private final int position;
  private final String name;

  /** Hidden constructor. */
  private Status(final String name, final int position) {
    this.position = position;
    this.name = name;
    instances.put(Integer.valueOf(position), this);
  }

  /**
   * Converts this status to a <code>String</code> by returning its
   * name.
   * @return the name of this status.
   */
  @Override
  public String toString() {
    return getName();
  }

  /**
   * Converts an array of <code>Status</code> to a <code>String</code>
   * array containing the names of the given status.
   * @param status the status to convert
   * @return an array containing the status names or <code>null</code>
   * if the argument is <code>null</code>
   */
  public static String[] getNames(Status[] status) {
    if (status == null) {
      return null;
    }
    String[] statusNames = new String[status.length];
    for (int i = 0; i < statusNames.length; i++) {
      statusNames[i] = status[i].getName();
    }
    return statusNames;
  }

  /**
   * Gets the name of this status.
   * @return the name of this status.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the position of this status in the ascendingly ordered list.
   * @return the position of this status.
   */
  public int getPosition() {
    return position;
  }

  /**
   * Converts a <code>String</code> name to a status.
   * @param name the name of the status
   * @return the status or <code>UNDEFINDED</code> if the conversion fails.
   */
  public static Status valueOf(String name) {
    if (name == null) {
      return UNDEFINED;
    }
    Iterator<Status> iterator = instances.values().iterator();
    while (iterator.hasNext()) {
      Status status = iterator.next();
      if (name.equals(status.getName())) {
        return status;
      }
    }
    return UNDEFINED;
  }

  /**
   * Converts from an <code>Integer</code> specifying the status'
   * position.
   * @param position the position of the status, must not be
   * <code>null</code>
   * @return the status or <code>null</code> if the conversion fails.
   */
  public static Status valueOf(Integer position) {
    return instances.get(position);
  }

  /**
   * Gets the available status in ascending order.
   * @return the available status in ascending order.
   */
  public static Status[] getAvailableStatus() {
    return instances.values().toArray(new Status[instances.size()]);
  }

  /**
   * Gets the available status names in ascending order of their
   * corresponding status.
   * @return the available status names in ascending order of their
   * corresponding status.
   */
  public static String[] getAvailableStatusNames() {
    List<String> nameList = new ArrayList<String>();
    Iterator<Status> iterator = instances.values().iterator();
    while (iterator.hasNext()) {
      nameList.add(iterator.next().getName());
    }
    return nameList.toArray(new String[nameList.size()]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(Status other) throws ClassCastException {

    if (getPosition() < other.getPosition()) {
      return -1;
    } else if (getPosition() > other.getPosition()) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Status)) {
      return false;
    }

    return this == o;
  }

  /**
   *  {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getPosition();
  }

}
