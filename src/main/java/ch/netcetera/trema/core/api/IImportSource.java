package ch.netcetera.trema.core.api;

import ch.netcetera.trema.core.Status;



/**
 * Represents a readable text resource for a single language that can
 * be imported into a Trema database, such as a Trema CSV file.
 * <p>
 * An <code>IImportSource</code> has a language, namely the language
 * that previously has been exported. If (and only if) that language
 * was not the master language of the Trema database, the import
 * source also has a master language and a master value for each key.
 */
public interface IImportSource {
  
  /**
   * Gets the number of keys in this import source.
   * @return the number of keys in this import source.
   */
  int getSize();
  
  /**
   * Checks for existence of this key.
   * 
   * @param key the key
   * @return true if the given key exists in this import source.
   */
  boolean existsKey(String key);
  
  /**
   * Gets the language of this import source.
   * @return the language of this import source.
   */
  String getLanguage();
  
  /**
   * Checks if a masterlanguage is defined.
   * 
   * @return true if this import source has a master language (and a
   * master value) for each key
   */
  boolean hasMasterLanguage();
  
  /**
   * Gets the master language of this import source. This will return
   * <code>null</code> if <code>hasMasterLanguage() == false</code>.
   * @return the master language of this import source or
   * <code>null</code> if <code>hasMasterLanguage() == false</code>.
   */
  String getMasterLanguage();
  
  /**
   * Gets the status for a given key.
   * @param key the key
   * @return the status <code>null</code> if the status cannot be found.
   */
  Status getStatus(String key);
  
  /**
   * Gets the value for a given key.
   * @param key the key
   * @return the value or <code>null</code> if the key cannot be found.
   */
  String getValue(String key);
  
  /**
   * Gets the master value for a given key. This will return
   * <code>null</code> if <code>hasMasterLanguage() == false</code>.
   * @param key the key
   * @return the master value for the given key or <code>null</code>
   * if <code>hasMasterLanguage() == false</code>.
   */
  String getMasterValue(String key);
  
  /**
   * Returns all the keys of the import source.
   * @return the keys of the import source.
   */
  String[] getKeys();
  
}
