package com.netcetera.trema.core.importing;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IDatabase;
import com.netcetera.trema.core.api.IImportSource;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;

import java.util.ArrayList;
import java.util.List;



/**
 * Determines the changes between an imported source and a database
 * and classifies them as conflicting or non-conflicting.
 */
public class ChangesAnalyzer {

  /** The import source. */
  private IImportSource importSource = null;

  /** The database. */
  private IDatabase db = null;

  /** The conflicting changes. */
  private final List<Change> conflictingChanges = new ArrayList<>();

  /** The non-conflicting changes. */
  private final List<Change> nonConflictingChanges = new ArrayList<>();

  private boolean useMasterValueFromFile = true;





  /**
   * Creates a new instance.
   * @param importSource the import source
   * @param db the database
   * @throws IllegalArgumentException if there is a mismatch between
   * the master languages
   */
  public ChangesAnalyzer(IImportSource importSource, IDatabase db) throws IllegalArgumentException {
    // if the import source has not got a master language, its language has to be the same as the db's
    // master language
    if (!importSource.hasMasterLanguage() && !importSource.getLanguage().equals(db.getMasterLanguage())) {
      throw new IllegalArgumentException("The imported language does not match the database master language.");
    }

    // if the import source has got a master language it has to be the same as the db's
    if (importSource.hasMasterLanguage() && !importSource.getMasterLanguage().equals(db.getMasterLanguage())) {
      throw new IllegalArgumentException("The imported master language does not match the database master language. "
                                         + "Imported master language: " + importSource.getMasterLanguage()
                                         + ", database master language: " + db.getMasterLanguage() + ".");
    }

    this.importSource = importSource;
    this.db = db;
  }


  /**
   * Determines the changes between the import source and the database
   * and classifies them as conflicting and non-conflicting.
   */
  public void analyze() {
    for (String key : importSource.getKeys()) {
      Change change = createChange(key);
      setConflictAttributes(change);

      if (change.getType() != Change.TYPE_NO_CHANGE) {
        if (change.isConflicting()) {
          conflictingChanges.add(change);
        } else {
          nonConflictingChanges.add(change);
        }
      }
    }
  }

  /**
   * Creates a <code>Change</code> object for a given key. Note that
   * only the conflict attributes of the change (i.e.
   * conflicting/non-conflicting, acceptable/not acceptable,
   * to be accepted/not to be accepted and the values to be accepted)
   * are <b>not</b> set. Use
   * {@link ChangesAnalyzer#setConflictAttributes(Change)} for that
   * purpose.
   * @param key the key
   * @return a <code>Change</code> object
   */
  protected Change createChange(String key) {
    String language = importSource.getLanguage();
    String importedValue = importSource.getValue(key);
    Status importedStatus = importSource.getStatus(key);

    // basic change
    Change change = new Change(Change.TYPE_NO_CHANGE, language, key, importedValue, importedStatus);

    ITextNode textNode = db.getTextNode(key);
    if (importSource.hasMasterLanguage()) {
      String masterLanguage = importSource.getMasterLanguage();
      change.setMasterLanguage(masterLanguage);

      // imported master value
      change.setImportedMasterValue(importSource.getMasterValue(key));

      // db master value
      if (textNode != null) {
        IValueNode masterValueNode = textNode.getValueNode(masterLanguage);
        if (masterValueNode != null) {
          change.setDbMasterValue(masterValueNode.getValue());
        }
      }
    }

    if (textNode != null) {
      change.setContext(textNode.getContext());

      IValueNode valueNode = textNode.getValueNode(language);
      if (valueNode != null) {
        change.setDbValue(valueNode.getValue());
        change.setDbStatus(valueNode.getStatus());
      }
    }

    setChangeType(change);

    return change;
  }

  /**
   * Sets the change type of a given basic change (i.e. a change with
   * the following attributes present: key, language, imported value,
   * imported status).
   * @param change the change
   */
  protected void setChangeType(Change change) {
    if (db.getTextNode(change.getKey()) == null) {
      // addition (key appears in the import source but not in the db)
      change.setType(Change.TYPE_KEY_ADDITION);
      return;
    }

    if (change.hasMasterLanguage()) {
      String dbMasterValue = change.getDbMasterValue();
      if (dbMasterValue == null) {
        // master language value does not exist in the db
        change.setType(Change.TYPE_MASTER_LANGUAGE_ADDITION);
        return;
      }
      if (!dbMasterValue.equals(change.getImportedMasterValue())) {
        // master language value has changed
        change.setType(Change.TYPE_MASTER_VALUE_CHANGED);
        return;
      }
    }

    // check the database value
    String dbValue = change.getDbValue();
    if (dbValue == null) {
      // value does not exist in the database
      change.setType(Change.TYPE_LANGUAGE_ADDITION);
      return;
    }

    String importedValue = change.getImportedValue();
    Status importedStatus = change.getImportedStatus();

    Status dbStatus = change.getDbStatus();

    if (dbValue.equals(importedValue)) {
      // values are the same, check status now
      if (importedStatus.compareTo(dbStatus) < 0) {
        change.setType(Change.TYPE_IMPORTED_STATUS_OLDER);
      } else if (importedStatus.compareTo(dbStatus) > 0) {
        change.setType(Change.TYPE_IMPORTED_STATUS_NEWER);
      }
    } else {
      // values have changed
      if (dbStatus == importedStatus) {
        change.setType(Change.TYPE_VALUE_CHANGED);
      } else {
        change.setType(Change.TYPE_VALUE_AND_STATUS_CHANGED);
      }
    }
  }

  /**
   * Determines whether a change is conflicting, acceptable and to be
   * accepted and sets the values to be accepted. This routine is
   * the only one dealing with the mentioned conflict attributes. It
   * may be overridden to change the conflict specification.
   * @param change the change to be analyzed
   * @throws IllegalArgumentException if an unknown change type is
   * encountered.
   */
  protected void setConflictAttributes(Change change) throws IllegalArgumentException {
    // default values
    change.setAcceptStatus(change.getImportedStatus());
    change.setAcceptValue(change.getImportedValue());
    if (useMasterValueFromFile) {
      change.setAcceptMasterValue(change.getImportedMasterValue());
    } else {
      change.setAcceptMasterValue(change.getDbMasterValue());
    }

    switch (change.getType()) {
      case Change.TYPE_NO_CHANGE:
        break;
      case Change.TYPE_MASTER_VALUE_CHANGED:
        change.setConflicting(true);
        change.setAcceptable(true); // ...in contrast to the original specifiaction by JH
        change.setAccept(false);
        break;
      case Change.TYPE_IMPORTED_STATUS_OLDER:
        change.setConflicting(true);
        change.setAcceptable(true);
        change.setAccept(true);
        break;
      case Change.TYPE_IMPORTED_STATUS_NEWER:
        change.setConflicting(false);
        change.setAcceptable(true);
        change.setAccept(true);
        break;
      case Change.TYPE_LANGUAGE_ADDITION:
        change.setConflicting(true);
        change.setAcceptable(false); // only the developer should be able to add languages
        break;
      case Change.TYPE_MASTER_LANGUAGE_ADDITION:
        change.setConflicting(true);
        change.setAcceptable(false); // only the developer should be able to add languages
        break;
      case Change.TYPE_VALUE_CHANGED:
      case Change.TYPE_VALUE_AND_STATUS_CHANGED:
        // default values
        change.setConflicting(false);
        change.setAcceptable(true);
        change.setAccept(true);

        // proceed according to the "old status" / "new status" matrix
        Status importedStatus = change.getImportedStatus();
        Status dbStatus = change.getDbStatus();

        if (dbStatus == Status.SPECIAL) {
          change.setConflicting(true);
          change.setAcceptable(false);
        } else if (dbStatus == Status.INITIAL) {
          if (importedStatus == Status.INITIAL) {
            change.setAcceptStatus(Status.TRANSLATED);
          } else if (importedStatus == Status.SPECIAL) {
            change.setConflicting(true);
            change.setAcceptable(false);
            change.setAcceptStatus(Status.TRANSLATED);
          }
        } else if (dbStatus == Status.TRANSLATED) {
          if (importedStatus == Status.INITIAL || importedStatus == Status.SPECIAL) {
            change.setConflicting(true);
            change.setAcceptStatus(Status.TRANSLATED);
          }
        } else if (dbStatus == Status.VERIFIED) {
          change.setConflicting(true);
          if (importedStatus == Status.VERIFIED) {
            change.setAccept(false);
          }
        }
        break;
      case Change.TYPE_KEY_ADDITION:
        change.setConflicting(true);
        change.setAcceptable(false); // only the developer should be able to add keys
        break;
      default:
        throw new IllegalArgumentException("Unknown change type: " + change.getType());
    }
  }

  /**
   * Gets the conflicting changes between the import source and the
   * database. Make sure to call <code>analyze()</code> before invoking
   * this method.
   * @return the conflicting changes or an empty array if there are
   * none.
   */
  public Change[] getConflictingChanges() {
    return conflictingChanges.toArray(new Change[conflictingChanges.size()]);
  }

  /**
   * Gets the non-conflicting changes between the import source and the
   * database. Make sure to call <code>analyze()</code> before invoking
   * this method.
   * @return the non-conflicting changes or an empty array if there are
   * none.
   */
  public Change[] getNonConflictingChanges() {
    return nonConflictingChanges.toArray(new Change[nonConflictingChanges.size()]);
  }

  /**
   * Gets the conflicting changes between the import source and the
   * databaseas <code>java.util.List</code>. Make sure to call
   * <code>analyze()</code> before invoking this method.
   * @return the non-conflicting changes or an empty list if there are
   * none.
   */
  public List<Change> getConflictingChangesAsList() {
    return conflictingChanges;
  }

  /**
   * Gets the non-conflicting changes between the import source and the
   * databaseas <code>java.util.List</code>. Make sure to call
   * <code>analyze()</code> before invoking this method.
   * @return the non-conflicting changes or an empty list if there are
   * none.
   */
  public List<Change> getNonConflictingChangesAsList() {
    return nonConflictingChanges;
  }

  /**
   * Applies a change to a trema database. If the change is not to be
   * accepted (<code>!change.isAccept()</code>, that is) or if the
   * change is an addition (type <code>Change.TYPE_KEY_ADDITION</code>,
   * <code>Change.TYPE_LANGUAGE_ADDITION</code> or
   * <code>Change.TYPE_MASTER_LANGUAGE_ADDITION</code>) this
   * method has <b>no</b> effect.
   * <p>
   * The the master value, the status and the value to be accepted
   * (<code>change.getAcceptMasterValue()</code>,
   * <code>change.getAcceptStatus()</code> and
   * <code>change.getAcceptValue()</code>, that is) will be set for the
   * corresponding key and language.
   * @param db the database to apply the changes to
   * @param change the change to be applied
   * @see ChangesAnalyzer#isApplicable(Change)
   */
  public static void applyChange(IDatabase db, Change change) {
    if (isApplicable(change)) {
      ITextNode textNode = db.getTextNode(change.getKey());

      if (textNode != null) {
        // textNode would be null for TYPE_KEY_ADDITION
        String language = change.getLanguage();
        IValueNode valueNode = textNode.getValueNode(language);
        if (valueNode != null) {
          // valueNode would be null for TYPE_LANGUAGE_ADDITION
          valueNode.setStatus(change.getAcceptStatus());
          valueNode.setValue(change.getAcceptValue());
        }

        if (change.hasMasterLanguage()) {
          String masterLanguage = change.getMasterLanguage();
          IValueNode masterValueNode = textNode.getValueNode(masterLanguage);

          if (masterValueNode != null) {
            // masterValueNode would be null for TYPE_MASTER_LANGUAGE_ADDITION
            masterValueNode.setValue(change.getAcceptMasterValue());
          }
        }
      }
    }
  }

  /**
   * Determines whether a given change gets applied by
   * {@link ChangesAnalyzer#applyChange(IDatabase, Change)}. The
   * mentioned method only applies changes which are to be accepted
   * (<code>change.isAccept() == true</code>, that is) and not of any
   * addition type (type <code>Change.TYPE_KEY_ADDITION</code>,
   * <code>Change.TYPE_LANGUAGE_ADDITION</code> or
   * <code>Change.TYPE_MASTER_LANGUAGE_ADDITION</code>). So even if an
   * addition was not considered a conflicting change, it would still
   * not be applied by <code>applyChange</code>.
   * @param change the change
   * @return true if the given change would get applied by
   * {@link ChangesAnalyzer#applyChange(IDatabase, Change)}
   */
  public static boolean isApplicable(Change change) {
    return change.isAccept() && change.getType() != Change.TYPE_KEY_ADDITION
                             && change.getType() != Change.TYPE_LANGUAGE_ADDITION
                             && change.getType() != Change.TYPE_MASTER_LANGUAGE_ADDITION;
  }


  /**
   * When a master value in an imported file differs from the corresponding
   * master value in the trema database, trema will use the master value from
   * the file by default and overwrite the master value in the db. Setting this
   * boolean to false, will cause trema to use the mastervalue from the db by
   * default and ignore the changed master value in the file.
   *
   *
   * @param useMasterValueFromFile boolean to control the default behaviour
   */
  public void setUseMasterValueFromFile(boolean useMasterValueFromFile) {
    this.useMasterValueFromFile = useMasterValueFromFile;
  }

}
