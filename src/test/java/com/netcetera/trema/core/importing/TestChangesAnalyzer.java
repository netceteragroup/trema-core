package com.netcetera.trema.core.importing;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.XMLTextNode;
import com.netcetera.trema.core.XMLValueNode;
import com.netcetera.trema.core.api.ITextNode;



/**
 * Unit test for the <code>ChangesAnalyzer</code> class.
 */
public class TestChangesAnalyzer {

  /**
   * Import source without master language.
   * @throws Exception in case the test fails
   */
  @Test
  public void testAnalyze1() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.setMasterLanguage("de");
    ITextNode textNode = new XMLTextNode("key1", "context1");
    textNode.addValueNode(new XMLValueNode("de", Status.INITIAL, "oldValue1"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key2", "context2");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "oldValue2"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key3", "context3");
    textNode.addValueNode(new XMLValueNode("de", Status.INITIAL, "sameValue3"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key4", "context4");
    textNode.addValueNode(new XMLValueNode("de", Status.VERIFIED, "sameValue4"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key5", "no master value");
    textNode.addValueNode(new XMLValueNode("jp", Status.TRANSLATED, "value"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key6", "nochange");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "value6"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key7", "context7");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "oldValue7"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key8", "context8");
    textNode.addValueNode(new XMLValueNode("de", Status.SPECIAL, "oldValue8"));
    db.addTextNode(textNode);
    
    CSVFile csvFile = new CSVFile(null, "de");
    csvFile.add("key1", Status.TRANSLATED, null, "newValue1");
    csvFile.add("key2", Status.INITIAL, null, "newValue2");
    csvFile.add("key3", Status.TRANSLATED, null, "sameValue3");
    csvFile.add("key4", Status.TRANSLATED, null, "sameValue4");
    csvFile.add("key5", Status.SPECIAL, null, "language addition");
    csvFile.add("key6", Status.TRANSLATED, null, "value6");
    csvFile.add("key7", Status.TRANSLATED, null, "newValue7");
    csvFile.add("key8", Status.TRANSLATED, null, "newValue8");
    csvFile.add("newKey", Status.TRANSLATED, null, "addition");
    
    ChangesAnalyzer analyzer = new ChangesAnalyzer(csvFile, db);
    analyzer.analyze();
    Change[] conflictingChanges = analyzer.getConflictingChanges();
    Change[] nonConflictingChanges = analyzer.getNonConflictingChanges();
  
    Assert.assertEquals(5, conflictingChanges.length);
    Assert.assertEquals(3, nonConflictingChanges.length);
    
    checkChangeTypes(conflictingChanges, nonConflictingChanges,
        1, 1, 1, 1, 0, 0, 3, 1);    
  }
  
  /**
   * Import source with master language.
   * @throws Exception in case the test fails
   */
  @Test
  public void testAnalyzeMasterLanguageImportSource() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.setMasterLanguage("de");
    ITextNode textNode = new XMLTextNode("key1", "context1");
    textNode.addValueNode(new XMLValueNode("de", Status.INITIAL, "masterValue1"));
    textNode.addValueNode(new XMLValueNode("fr", Status.INITIAL, "oldValue1"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key2", "context2");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "masterValue2"));
    textNode.addValueNode(new XMLValueNode("fr", Status.TRANSLATED, "oldValue2"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key3", "context3");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "oldMasterValue3"));
    textNode.addValueNode(new XMLValueNode("fr", Status.INITIAL, "oldValue3"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key4", "context4");
    textNode.addValueNode(new XMLValueNode("de", Status.VERIFIED, "masterValue4"));
    textNode.addValueNode(new XMLValueNode("fr", Status.INITIAL, "sameValue4"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key5", "no master value");
    textNode.addValueNode(new XMLValueNode("jp", Status.TRANSLATED, "value"));
    textNode.addValueNode(new XMLValueNode("fr", Status.INITIAL, "oldValue1"));
    db.addTextNode(textNode);
    textNode = new XMLTextNode("key6", "no value");
    textNode.addValueNode(new XMLValueNode("de", Status.TRANSLATED, "masterValue6"));
    textNode.addValueNode(new XMLValueNode("jp", Status.INITIAL, "value"));
    db.addTextNode(textNode);
    
    CSVFile csvFile = new CSVFile("de", "fr");
    csvFile.add("key1", Status.TRANSLATED, "masterValue1", "newValue1");
    csvFile.add("key2", Status.INITIAL, "masterValue2", "newValue2");
    csvFile.add("key3", Status.TRANSLATED, "newMasterValue3", "newValue3");
    csvFile.add("key4", Status.TRANSLATED, "masterValue4", "sameValue4");
    csvFile.add("key5", Status.SPECIAL, "masterValue5", "master language addition");
    csvFile.add("key6", Status.TRANSLATED, "masterValue6", "value6");
    csvFile.add("newKey", Status.TRANSLATED, "masterValue9", "addition");
    
    ChangesAnalyzer analyzer = new ChangesAnalyzer(csvFile, db);
    analyzer.analyze();
    Change[] conflictingChanges = analyzer.getConflictingChanges();
    Change[] nonConflictingChanges = analyzer.getNonConflictingChanges();
  
    Assert.assertEquals(5, conflictingChanges.length);
    Assert.assertEquals(2, nonConflictingChanges.length);
    
    checkChangeTypes(conflictingChanges, nonConflictingChanges,
        1, 1, 0, 1, 1, 1, 2, 0);    
  }

  /**
   * Checks the types of the changes and compares them with an expected
   * number.
   * 
   *  
   */
  // tzueblin Dec 1, 2008: Suppress checkstyle, this method has more than 7 args but its ok here, 
  // however its a unittest   
  
  // CHECKSTYLE:OFF
  private void checkChangeTypes(Change[] conflictingChanges, Change[] nonConflictingChanges, 
      int additionCount,
      int statusNewerCount,
      int statusOlderCount,
      int languageAdditionCount,
      int masterLanguageAdditionCount,
      int masterValueChangedCount,
      int valueAndStatusChangedCount,
      int valueChangedCount) {
    // CHECKSTYLE:ON
    
    int addition = 0;
    int statusNewer = 0;
    int statusOlder = 0;
    int languageAddition = 0;
    int masterLanguageAddition = 0;
    int masterValueChanged = 0;
    int valueAndStatusChanged = 0;
    int valueChanged = 0;
    
    Change[] allChanges = new Change[conflictingChanges.length + nonConflictingChanges.length];
    
    System.arraycopy(conflictingChanges, 0, allChanges, 0, conflictingChanges.length);
    System.arraycopy(nonConflictingChanges, 0, allChanges, conflictingChanges.length, nonConflictingChanges.length);
    
    for (int i = 0; i < allChanges.length; i++) {
      switch (allChanges[i].getType()) {
        case Change.TYPE_KEY_ADDITION: addition++; break;
        case Change.TYPE_IMPORTED_STATUS_NEWER: statusNewer++; break;
        case Change.TYPE_IMPORTED_STATUS_OLDER: statusOlder++; break;
        case Change.TYPE_LANGUAGE_ADDITION: languageAddition++; break;
        case Change.TYPE_MASTER_LANGUAGE_ADDITION: masterLanguageAddition++; break;
        case Change.TYPE_MASTER_VALUE_CHANGED: masterValueChanged++; break;
        case Change.TYPE_VALUE_AND_STATUS_CHANGED: valueAndStatusChanged++; break;
        case Change.TYPE_VALUE_CHANGED: valueChanged++; break;
        default: // do nothing
      }
    }
    
    Assert.assertEquals(additionCount, addition);
    Assert.assertEquals(statusNewerCount, statusNewer);
    Assert.assertEquals(statusOlderCount, statusOlder);
    Assert.assertEquals(languageAdditionCount, languageAddition);
    Assert.assertEquals(masterLanguageAdditionCount, masterLanguageAddition);
    Assert.assertEquals(masterValueChangedCount, masterValueChanged);    
    Assert.assertEquals(valueAndStatusChangedCount, valueAndStatusChanged);
    Assert.assertEquals(valueChangedCount, valueChanged);
  }
  
}
