package com.netcetera.trema.core;

import com.netcetera.trema.TestUtils;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


/**
 * Test for {@link XMLDatabase}.
 */
class XMLDatabaseTest {

  private static final String TEST_KEY = "testKey";
  private static final String TEST_LANGUAGE = "jp";
  private static final String TEST_UTF8_VALUE = "\u30D0\u30CA\u30CAöäü";
  private static final String TEST_ISO_VALUE = "öäü";

  /**
   * Test for building the xml database from an xml String.
   *
   * @throws Exception in case the test fails
   */
  @Test
  void testBuild() throws Exception {
    XMLDatabase db = new XMLDatabase();
    for (int i = 0; i < 3; i++) { // build the database 3 times
      db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
               + "<!-- test comment -->"
               + "<text key='key1'> <context>context1</context>"
               + "  <value lang='de' status='special'>masterValue1\u12AB</value>"
               + "  <value lang='fr' status='initial'>value1äöü</value>"
               + "</text>"
               + "<text key='key2'> <context>context2</context>"
               + "  <value lang='de' status='special'>masterValue2\u12AB</value>"
               + "  <value lang='fr' status='initial'>value2äöü</value>"
               + "</text>"
               + "<text key='key3'> <context>context3</context>"
               + "<!-- test comment -->"
               + "  <value lang='de' status='special'>masterValue3\u12AB</value>"
               + "  <value lang='fr' status='initial'>value3äöü</value>"
               + "</text>"
               + "</trema>", false);

      assertThat(db.getMasterLanguage(), equalTo("de"));
      assertThat(db.getSize(), equalTo(3));

      for (int j = 1; j <= 3; j++) {
        ITextNode textNode = db.getTextNode(j - 1);
        ITextNode textNode2 = db.getTextNode("key" + j);
        assertThat(textNode2, equalTo(textNode));

        // context
        assertThat(textNode.getContext(), equalTo("context" + j));

        // German
        IValueNode valueNode = textNode.getValueNode("de");
        assertThat(valueNode.getValue(), equalTo("masterValue" + j + "\u12AB"));
        assertThat(valueNode.getStatus(), equalTo(Status.SPECIAL));

        // French
        valueNode = textNode.getValueNode("fr");
        assertThat(valueNode.getValue(), equalTo("value" + j + "äöü"));
        assertThat(valueNode.getStatus(), equalTo(Status.INITIAL));

        // inexisting language
        assertThat(textNode.getValueNode("idonotexist"), nullValue());
      }
    }
  }

  @Test
  void shouldReadFromUtf8FileProperly() throws Exception {
    // given
    XMLDatabase db = new XMLDatabase();
    Path utf8TestFile = TestUtils.getFileFromJar("/test-UTF-8.xml");
    String contents = new String(Files.readAllBytes(utf8TestFile), StandardCharsets.UTF_8);

    // when
    db.build(contents, false);

    // then
    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);
    assertThat(valueNode.getValue(), equalTo(TEST_UTF8_VALUE));
  }

  @Test
  void shouldReadFromIso88591FileProperly() throws Exception {
    // given
    XMLDatabase db = new XMLDatabase();
    Path iso88591TestFile = TestUtils.getFileFromJar("/test-ISO.xml");
    String contents = new String(Files.readAllBytes(iso88591TestFile), StandardCharsets.ISO_8859_1);

    // when
    db.build(contents, false);

    // then
    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);
    assertThat(valueNode.getValue(), equalTo(TEST_ISO_VALUE));
  }

  /**
   * Tests the operations available on nodes (move etc.).
   *
   * @throws Exception in case the test fails
   */
  @Test
  void testOperations() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
      + "<!-- test comment -->"
      + "<text key='key1'> <context>context1</context>"
      + "</text>"
      + "<text key='key2'> <context>context2</context>"
      + "</text>"
      + "<text key='key3'> <context>context3</context>"
      + "</text>"
      + "</trema>", false);

    assertThat(db.getMasterLanguage(), equalTo("de"));
    assertThat(db.getSize(), equalTo(3));

    ITextNode textNode1 = db.getTextNode(0);
    ITextNode textNode2 = db.getTextNode(1);
    ITextNode textNode3 = db.getTextNode(2);

    db.moveUpTextNodes(new ITextNode[] {textNode1});
    assertThat(db.getTextNodes(), arrayContaining(textNode1, textNode2, textNode3));

    db.moveDownTextNodes(new ITextNode[] {textNode1});
    assertThat(db.getTextNodes(), arrayContaining(textNode2, textNode1, textNode3));

    db.moveDownTextNodes(new ITextNode[] {textNode2, textNode1});
    assertThat(db.getTextNodes(), arrayContaining(textNode3, textNode2, textNode1));

    db.moveUpTextNodes(new ITextNode[] {textNode2, textNode1});
    assertThat(db.getTextNodes(), arrayContaining(textNode2, textNode1, textNode3));

    db.removeTextNode(textNode2.getKey());
    assertThat(db.getTextNodes(), arrayContaining(textNode1, textNode3));

    db.removeTextNodes(new ITextNode[] {textNode2, textNode1, textNode3});
    assertThat(db.getSize(), equalTo(0));
 }

  /*
   * This tests tries to write a big xml file to disk.
   */
  @Test
  void testCreateLargeDatabase(@TempDir Path tempDirectory) throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.setMasterLanguage("de");

    for (int i = 0; i < 3000; i++) {
      ITextNode textNode = new XMLTextNode("key" + i, "context" + i);
      textNode.addValueNode(new XMLValueNode("de", Status.INITIAL, "value_de" + i));
      textNode.addValueNode(new XMLValueNode("en", Status.INITIAL, "value_en" + i));
      textNode.addValueNode(new XMLValueNode("fr", Status.INITIAL, "value_fr" + i));
      textNode.addValueNode(new XMLValueNode("it", Status.INITIAL, "value_it" + i));
      db.addTextNode(textNode);
    }

    File file = tempDirectory.resolve("largeFile.xml").toFile();
    try (FileOutputStream fis = new FileOutputStream(file)) {
      db.writeXML(fis, "UTF-8", "  ", "\n");
    }
  }
}
