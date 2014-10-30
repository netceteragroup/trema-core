package com.netcetera.trema.core;



/**
 * Constants used in junit tests.
 */
public abstract class TestConstants {
  
  /** Comment for <code>TEST_UTF8_PATHNAME</code>. */
  public static final String TEST_UTF8_PATHNAME = "src/test/resources/test-UTF-8.xml";
  /** Comment for <code>TEST_ISO_PATHNAME</code>. */
  public static final String TEST_ISO_PATHNAME = "src/test/resources/test-ISO.xml";
  /** Comment for <code>TEST_HUGE_PATHNAME</code>. */
  public static final String TEST_HUGE_PATHNAME = "tmp/huge.xml";
  
  
  /** 
   *  Trema XML for testing.
   */
  public static final String TESTXML = ""
    + "<?xml version='1.0' encoding='UTF-8'?>"
    + "<trema masterLang='de'>"
    + "<text key='key1'> <context>context1</context>"
    + "  <value lang='de' status='initial'>masterValue1\u12AB</value>"
    + "  <value lang='fr' status='initial'>value1\u12AB</value>"
    + "</text>"
    + "<text key='key2'> <context>context2</context>"
    + "  <value lang='de' status='verified'>masterValue2���</value>"
    + "  <value lang='fr' status='translated'>value2���</value>"
    + "</text>"
    + "<text key='key3'> <context>context3</context>"
    + "  <value lang='de' status='special'>masterValue3</value>"
    + "  <value lang='fr' status='special'>value3</value>"
    + "</text>"
    + "</trema>";
  
  private TestConstants() {
    
  }
  

}
