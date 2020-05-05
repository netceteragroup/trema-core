package com.netcetera.trema.core.importing;

import com.google.common.collect.ImmutableMap;
import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.XMLTextNode;
import com.netcetera.trema.core.XMLValueNode;
import com.netcetera.trema.core.api.ITextNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;


/**
 * Unit test for {@link ChangesAnalyzer}.
 */
class ChangesAnalyzerTest {

  /**
   * Import source without master language.
   */
  @Test
  void testAnalyze1() {
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
  
    assertThat(conflictingChanges, arrayWithSize(5));
    assertThat(nonConflictingChanges, arrayWithSize(3));

    Map<Integer, Long> expectedChangeCounts = ImmutableMap.<Integer, Long>builder()
      .put(Change.TYPE_KEY_ADDITION, 1L)
      .put(Change.TYPE_IMPORTED_STATUS_NEWER, 1L)
      .put(Change.TYPE_IMPORTED_STATUS_OLDER, 1L)
      .put(Change.TYPE_LANGUAGE_ADDITION, 1L)
      .put(Change.TYPE_VALUE_AND_STATUS_CHANGED, 3L)
      .put(Change.TYPE_VALUE_CHANGED, 1L)
      .build();
    verifyNumberOfChanges(analyzer, expectedChangeCounts);
  }
  
  /**
   * Import source with master language.
   */
  @Test
  void testAnalyzeMasterLanguageImportSource() {
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

    assertThat(conflictingChanges, arrayWithSize(5));
    assertThat(nonConflictingChanges, arrayWithSize(2));

    Map<Integer, Long> expectedChangeCounts = ImmutableMap.<Integer, Long>builder()
      .put(Change.TYPE_KEY_ADDITION, 1L)
      .put(Change.TYPE_IMPORTED_STATUS_NEWER, 1L)
      .put(Change.TYPE_LANGUAGE_ADDITION, 1L)
      .put(Change.TYPE_MASTER_LANGUAGE_ADDITION, 1L)
      .put(Change.TYPE_MASTER_VALUE_CHANGED, 1L)
      .put(Change.TYPE_VALUE_AND_STATUS_CHANGED, 2L)
      .build();
    verifyNumberOfChanges(analyzer, expectedChangeCounts);
  }

  /**
   * Verifies that the given changes analyzer has the expected number of changes by change type.
   *
   * @param analyzer the analyzer whose found changes should be verified
   * @param expectedCountByChangeType expected count by change type (use Change constants)
   */
  private void verifyNumberOfChanges(ChangesAnalyzer analyzer, Map<Integer, Long> expectedCountByChangeType) {
    Map<Integer, Long> actualCountByChangeType =
      Stream.of(analyzer.getConflictingChanges(), analyzer.getNonConflictingChanges())
        .flatMap(Arrays::stream)
        .collect(Collectors.groupingBy(Change::getType, Collectors.counting()));

    assertThat(actualCountByChangeType, equalTo(expectedCountByChangeType));
  }
}
