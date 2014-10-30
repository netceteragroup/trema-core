package ch.netcetera.trema.core.importing;

import ch.netcetera.trema.core.Status;



/**
 * Encapsulates a change between the import source and
 * the database for a specific key. A change holds some general data
 * (such as the key and the language the change relates to), the
 * imported values and the database values as well as the values that
 * are to be accepted if this change is applied to a database.
 * <p>
 * Note that a change has no master language and no master value if the
 * involved import source has no master language either.
 * 
 */
public final class Change {
  
  /** Dummy type indicating no change at all. */
  public static final int TYPE_NO_CHANGE = 0;
  
  /**
   * The imported master value is different from the master value in
   * the database.
   */
  public static final int TYPE_MASTER_VALUE_CHANGED = 1;
  
  /** 
   * The status of the imported text is older than the status in the
   * database. The text values, however, are the same.
   */
  public static final int TYPE_IMPORTED_STATUS_OLDER = 2;
  
  /** 
   * The status of the imported text is newer than the status in the
   * database. The text values, however, are the same.
   */  
  public static final int TYPE_IMPORTED_STATUS_NEWER = 3;
  
  /** 
   * The imported language does not exist in the database for the
   * specific key. The key, however, is present in the database.
   */
  public static final int TYPE_LANGUAGE_ADDITION = 4;
  
  /** 
   * The imported master language does not exist in the database for
   * the specific key. The key, however, is present in the database.
   */
  public static final int TYPE_MASTER_LANGUAGE_ADDITION = 5;
  
  /** 
   * The imported text value is different from the text value in the
   * database. The status, however, are the same.
   */
  public static final int TYPE_VALUE_CHANGED = 6;
  
  /** 
   * Both the imported text value and status are different from the
   * text value and status in the database.
   */
  public static final int TYPE_VALUE_AND_STATUS_CHANGED = 7;  
  
  /** 
   * The key appeares in the imported resource, but not in the
   * database.
   */
  public static final int TYPE_KEY_ADDITION = 8;
  
  /** The type of this change. */
  private int type = TYPE_NO_CHANGE;
  
  /** The key this change relates to. */
  private String key = null;
  
  /** The context of the key, only used for display. */
  private String context = null;
  
  /** The language this change relates to. */
  private String language = null;
  
  /** The master language, might be <code>null</code>. */
  private String masterLanguage = null;
  
  /** The database value. */
  private String dbValue = null;
  
  /** The database status. */
  private Status dbStatus = null;
  
  /** The database master value, might be <code>null</code>. */
  private String dbMasterValue = null;
  
  /** The imported value. */
  private String importedValue = null;
  
  /** The imported status. */
  private Status importedStatus = null;  
  
  /** The imported master value, might be <code>null</code>. */
  private String importedMasterValue = null;

  private boolean conflicting = false;
  private boolean acceptable = false;
  private boolean accept = false;
  private String acceptMasterValue = null;
  private Status acceptStatus = null;
  private String acceptValue = null;
  
  /**
   * Constructs a basic change with no master language and no master values.
   * 
   * @param type the type
   * @param language the language
   * @param key the key
   * @param importedValue the imported Value
   * @param importedStatus the imported Status
   */
  public Change(int type, String language, String key, String importedValue, Status importedStatus) {
    this.type = type;
    this.language = language;
    this.key = key;
    
    this.importedValue = importedValue;
    this.importedStatus = importedStatus;
  }

  /**
   * Gets the type of this change.
   * @return the type of this change.
   */
  public int getType() {
    return type;
  }
  
  /**
   * Sets the type of this change.
   * @param type the type to set
   */
  public void setType(int type) {
    this.type = type;
  }
  
  /**
   * Gets the key this change relates to.
   * @return the key this change relates to.
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Gets the context of the key this change relates to.
   * @return the context of the key this change relates to or
   * <code>null</code> if there is none.
   */
  public String getContext() {
    return context;
  }
  
  /**
   * Sets the context of the key this change relates to.
   * @param context the context to set
   */
  public void setContext(String context) {
    this.context = context;
  }
  
  /**
   * A flag indicating if the change is conflicting.
   * 
   * @return true if this change is conflicting.
   */
  public boolean isConflicting() {
    return conflicting;
  }
  
  /**
   * Sets this change conflicting or non-conflicting.
   * @param conflicting true if this change should be set
   * conflicting
   */
  public void setConflicting(boolean conflicting) {
    this.conflicting = conflicting;
  }
  
  /**
   * Gets the language.
   * @return the language.
   */
  public String getLanguage() {
    return language;
  }
  
  /**
   * A flag indicating if this change has a master language.
   * 
   * @return true if this change has a master language.
   */
  public boolean hasMasterLanguage() {
    return masterLanguage != null;
  }
  
  /**
   * Gets the imported master language.
   * @return the imported master language or <code>null</code> if none.
   */
  public String getMasterLanguage() {
    return masterLanguage;
  }
  
  /**
   * Sets the master language.
   * @param masterLanguage the master language to set.
   */
  public void setMasterLanguage(String masterLanguage) {
    this.masterLanguage = masterLanguage;
  }

  /**
   * Gets the imported master value.
   * @return the imported master value or <code>null</code> if none.
   */
  public String getImportedMasterValue() {
    return importedMasterValue;
  }
  
  /**
   * Sets the imported master value.
   * @param importedMasterValue the imported master value to set
   */
  public void setImportedMasterValue(String importedMasterValue) {
    this.importedMasterValue = importedMasterValue;
  }

  /**
   * Gets the imported status.
   * @return the imported status.
   */
  public Status getImportedStatus() {
    return importedStatus;
  }

  /**
   * Gets the imported value.
   * @return the imported value.
   */
  public String getImportedValue() {
    return importedValue;
  }
  
  /**
   * Gets the database mater value.
   * @return the database master value or <code>null</code> if none.
   */
  public String getDbMasterValue() {
    return dbMasterValue;
  }
  
  /**
   * Sets the database master value.
   * @param dbMasterValue the database master value to set.
   */
  public void setDbMasterValue(String dbMasterValue) {
    this.dbMasterValue = dbMasterValue;
  }
  
  /** 
   * Gets the database status.
   * @return the database status or <code>null</code> if none.
   */
  public Status getDbStatus() {
    return dbStatus;
  }
  
  /**
   * Sets the database status.
   * @param dbStatus the db status
   */
  public void setDbStatus(Status dbStatus) {
    this.dbStatus = dbStatus;
  }
  
  /**
   * Gets the dabase value.
   * @return the database value or <code>null</code> if none.
   */
  public String getDbValue() {
    return dbValue;
  }
  
  /**
   * Sets the database value.
   * @param dbValue the database value to set.
   */
  public void setDbValue(String dbValue) {
    this.dbValue = dbValue;
  }
  
  /** 
   * A flag indicating if this change is acceptable.
   * 
   * @return true if this change is acceptable.
   */
  public boolean isAcceptable() {
    return acceptable;
  }
  
  /**
   * Sets this change acceptable or not.
   * @param acceptable true if this change should be acceptable.
   */
  public void setAcceptable(boolean acceptable) {
    this.acceptable = acceptable;
  }
  
  /**
   * Returns true if this change is to be accepted.
   * If this change is <b>not acceptable</b>, this will return
   * <code>false</code>.
   * @return true if this change is to be accepted.
   */
  public boolean isAccept() {
    return isAcceptable() && accept;
  }
  
  /**
   * Sets this change to be accepted or not. If this change is <b>not
   * acceptable</b>, it cannot be set to be accepted. In that case, you
   * need to set this change acceptable first.
   * @param accept true if this change should be set to be
   * accepted
   */
  public void setAccept(boolean accept) {
    this.accept = isAcceptable() && accept;
  }
  
  /**
   * Gets the master value to be accepted.
   * @return the master value to be accepted.
   */
  public String getAcceptMasterValue() {
    return acceptMasterValue;
  }
  
  /**
   * Sets the master value to be accepted.
   * @param acceptMasterValue the master value to be accepted
   */
  public void setAcceptMasterValue(String acceptMasterValue) {
    this.acceptMasterValue = acceptMasterValue;
  }
  
  /**
   * Gets the status to be accepted.
   * @return the status to be accepted.
   */
  public Status getAcceptStatus() {
    return acceptStatus;
  }
  
  /**
   * Sets the status to be accepted.
   * @param acceptStatus the status to be accepted.
   */
  public void setAcceptStatus(Status acceptStatus) {
    this.acceptStatus = acceptStatus;
  }
  
  /**
   * Gets the value to be accepted.
   * @return the value to be accepted.
   */
  public String getAcceptValue() {
    return acceptValue;
  }
  
  /**
   * Sets the value to be accepted.
   * @param acceptValue the value to be accepted
   */
  public void setAcceptValue(String acceptValue) {
    this.acceptValue = acceptValue;
  }
  
  /**
   * Gets a human readable description for a given change.
   * @param change the change
   * @return a human readable description for the given change.
   */
  public static String getDescription(Change change) {
    switch (change.getType()) {
      case Change.TYPE_MASTER_VALUE_CHANGED:
        return "The master value has changed";
      case Change.TYPE_IMPORTED_STATUS_OLDER:
        return "The imported status is older than the database status";
      case Change.TYPE_IMPORTED_STATUS_NEWER:
        return "The imported status is newer than the database status";
      case Change.TYPE_LANGUAGE_ADDITION:
        return "The language \"" + change.getLanguage() + "\" does not exist in the database";
      case Change.TYPE_MASTER_LANGUAGE_ADDITION:
        return "The master language \"" + change.getMasterLanguage() + "\" does not exist in the database";
      case Change.TYPE_VALUE_CHANGED:
        if (change.isConflicting()) {
          return "The value and the status are conflicting";
        }
        return "The value has changed";
      case Change.TYPE_VALUE_AND_STATUS_CHANGED:
        if (change.isConflicting()) {
          return "The value and the status are conflicting";
        }
        return "The value and the status have changed";
      case Change.TYPE_KEY_ADDITION:
        return "The key does not exist in the database";
      default:
        return "Unknown change";
    }
  }
  
}

