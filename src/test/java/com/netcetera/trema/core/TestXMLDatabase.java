package com.netcetera.trema.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.XMLTextNode;
import com.netcetera.trema.core.XMLValueNode;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/**
 * Unit test for the <code>XMLDatabase</code> class.
 */
public class TestXMLDatabase {

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
  public void testBuild() throws Exception {
    XMLDatabase db = new XMLDatabase();
    for (int i = 0; i < 3; i++) { // build the database 3 times
      db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
               + " !-- test comment -->"
               + "<text key='key1'> <context>context1</context>"
               + "  <value lang='de' status='special'>masterValue1\u12AB</value>"
               + "  <value lang='fr' status='initial'>value1���</value>"
               + "</text>"
               + "<text key='key2'> <context>context2</context>"
               + "  <value lang='de' status='special'>masterValue2\u12AB</value>"
               + "  <value lang='fr' status='initial'>value2���</value>"
               + "</text>"
               + "<text key='key3'> <context>context3</context>"
               + "<!-- test comment -->"
               + "  <value lang='de' status='special'>masterValue3\u12AB</value>"
               + "  <value lang='fr' status='initial'>value3���</value>"
               + "</text>"
               + "</trema>", false);

      Assert.assertEquals("de", db.getMasterLanguage());
      Assert.assertEquals(3, db.getSize());

      for (int j = 1; j <= 3; j++) {
        ITextNode textNode = db.getTextNode(j - 1);
        ITextNode textNode2 = db.getTextNode("key" + j);
        Assert.assertTrue(textNode == textNode2);

        // context
        Assert.assertEquals("context" + j, textNode.getContext());

        // German
        IValueNode valueNode = textNode.getValueNode("de");
        Assert.assertEquals("masterValue" + j + "\u12AB", valueNode.getValue());
        Assert.assertTrue(Status.SPECIAL == valueNode.getStatus());

        // French
        valueNode = textNode.getValueNode("fr");
        Assert.assertEquals("value" + j + "���", valueNode.getValue());
        Assert.assertTrue(Status.INITIAL == valueNode.getStatus());

        // inexisting language
        Assert.assertNull(textNode.getValueNode("idonotexist"));
      }
    }
  }

  /**
   * Test to read ��� characters from an UTF8 file.
   *
   * @throws Exception incase the test fails
   */
  @SuppressWarnings("resource")
  @Test
  public void testUTF8() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build(new FileInputStream(TestConstants.TEST_UTF8_PATHNAME), false);

    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);

    Assert.assertEquals(TEST_UTF8_VALUE, valueNode.getValue());
  }

  /**
   * Test to read ��� characters from an ISO file.
   *
   * @throws Exception incase the test fails
   */
  @SuppressWarnings("resource")
  @Test
  public void testISO() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build(new FileInputStream(TestConstants.TEST_ISO_PATHNAME), false);

    ITextNode textNode = db.getTextNode(TEST_KEY);
    IValueNode valueNode = textNode.getValueNode(TEST_LANGUAGE);

    Assert.assertEquals(TEST_ISO_VALUE, valueNode.getValue());
  }

  /**
   * Tests the operations available on nodes (move etc.).
   *
   * @throws Exception incase the test fails
   */
  @Test
 public void testOperations() throws Exception {
   XMLDatabase db = new XMLDatabase();
   db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
            + " !-- test comment -->"
            + "<text key='key1'> <context>context1</context>"
            + "</text>"
            + "<text key='key2'> <context>context2</context>"
            + "</text>"
            + "<text key='key3'> <context>context3</context>"
            + "</text>"
            + "</trema>", false);

   Assert.assertEquals("de", db.getMasterLanguage());
   Assert.assertEquals(3, db.getSize());

   ITextNode textNode1 = db.getTextNode(0);
   ITextNode textNode2 = db.getTextNode(1);
   ITextNode textNode3 = db.getTextNode(2);

   db.moveUpTextNodes(new ITextNode[] {textNode1});
   Assert.assertArrayEquals(new ITextNode[] {textNode1, textNode2, textNode3}, db.getTextNodes());

   db.moveDownTextNodes(new ITextNode[] {textNode1});
   Assert.assertArrayEquals(new ITextNode[] {textNode2, textNode1, textNode3}, db.getTextNodes());

   db.moveDownTextNodes(new ITextNode[] {textNode2, textNode1});
   Assert.assertArrayEquals(new ITextNode[] {textNode3, textNode2, textNode1}, db.getTextNodes());

   db.moveUpTextNodes(new ITextNode[] {textNode2, textNode1});
   Assert.assertArrayEquals(new ITextNode[] {textNode2, textNode1, textNode3}, db.getTextNodes());

   db.removeTextNode(textNode2.getKey());
   Assert.assertArrayEquals(new ITextNode[] {textNode1, textNode3}, db.getTextNodes());

   db.removeTextNodes(new ITextNode[] {textNode2, textNode1, textNode3});
   Assert.assertEquals(0, db.getSize());
 }

  /**
   * This tests tries to write a big xml file to disk.
   *
   * @throws Exception in case the test fails
   */
  @Test
  public void testCreateHugeDatabase() throws Exception {
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
      f = new File(TestConstants.TEST_HUGE_PATHNAME);
      f.createNewFile();
      s = new FileOutputStream(TestConstants.TEST_HUGE_PATHNAME);
      db.writeXML(s, "UTF-8", "  ", "\n");
    } finally {
      if (s != null) {
        try {
          s.close();
        } catch (IOException e) {
          // nothing to do
        }
      }
      f = new File(TestConstants.TEST_HUGE_PATHNAME);
      f.delete();
      d = new File("tmp");
      d.delete();
    }
  }

  @Test
  public void testInvalidDB() throws Exception {
    XMLDatabase db = new XMLDatabase();
    try {
      db.build(new FileInputStream("src/test/resources/filewitherrors.xml"), false);
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }



}
