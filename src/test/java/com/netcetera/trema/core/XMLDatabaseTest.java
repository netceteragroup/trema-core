package com.netcetera.trema.core;

import com.google.common.io.Files;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

  /**
   * Test to read öäü characters from an UTF8 file.
   *
   * @throws Exception incase the test fails
   */
  @Test
  void testUTF8() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build(Files.toString(new File(ConstantsTest.TEST_UTF8_PATHNAME), StandardCharsets.UTF_8), false);

    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);

    assertThat(valueNode.getValue(), equalTo(TEST_UTF8_VALUE));
  }

  /**
   * Test to read äöü characters from an ISO file.
   *
   * @throws Exception incase the test fails
   */
  @Test
  void testISO() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build(Files.toString(new File(ConstantsTest.TEST_ISO_PATHNAME), StandardCharsets.ISO_8859_1), false);

    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);

    assertThat(valueNode.getValue(), equalTo(TEST_ISO_VALUE));
  }

  /**
   * Tests the operations available on nodes (move etc.).
   *
   * @throws Exception incase the test fails
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

  /**
   * This tests tries to write a big xml file to disk.
   *
   * @throws Exception in case the test fails
   */
  @Test
  void testCreateHugeDatabase() throws Exception {
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
    File d = null;
    File f = null;
    FileOutputStream s = null;
    try {
      d = new File("tmp");
      d.mkdir();
      f = new File(ConstantsTest.TEST_HUGE_PATHNAME);
      f.createNewFile();
      s = new FileOutputStream(ConstantsTest.TEST_HUGE_PATHNAME);
      db.writeXML(s, "UTF-8", "  ", "\n");
    } finally {
      f = new File(ConstantsTest.TEST_HUGE_PATHNAME);
      f.delete();
      d = new File("tmp");
      d.delete();
    }
  }
}
