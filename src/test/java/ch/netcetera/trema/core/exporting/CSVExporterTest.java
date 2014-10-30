package ch.netcetera.trema.core.exporting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.netcetera.trema.common.TremaUtil;
import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.XMLDatabase;



/**
 * Unit test for the <code>CSVExporter</code> class.
 */
public class CSVExporterTest {
  
  private CSVExporter exporter = new CSVExporter();;
  private XMLDatabase db;
  
  /**
   * setUp().
   * @throws Exception in case setUp fails
   */
  @Before
  public void setUp() throws Exception {
    db = new XMLDatabase();
    db.build(
          "<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
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
        + "</trema>", false);
  }

  /**
   * Tests CSVExporter. Export French with all status.
   */
  @Test
  public void testGetValues1() {    
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "fr", null);
    Assert.assertEquals(4, values.length);
    
    // header
    Assert.assertEquals("Key;Status;Master (de);Value (fr);Context", TremaUtil.arrayToString(values[0], ";"));
    
    // first row
    Assert.assertEquals(5, values[1].length);
    Assert.assertEquals("key1", values[1][0]);
    Assert.assertEquals("initial", values[1][1]);
    Assert.assertEquals("masterValue1\u12AB", values[1][2]);
    Assert.assertEquals("value1\u12AB", values[1][3]);
    Assert.assertEquals("context1", values[1][4]);
    
    // second row
    Assert.assertEquals(5, values[2].length);
    Assert.assertEquals("key2", values[2][0]);
    Assert.assertEquals("translated", values[2][1]);
    Assert.assertEquals("masterValue2���", values[2][2]);
    Assert.assertEquals("value2���", values[2][3]);
    Assert.assertEquals("context2", values[2][4]);
    
    // third row
    Assert.assertEquals(5, values[3].length);
    Assert.assertEquals("key3", values[3][0]);
    Assert.assertEquals("special", values[3][1]);
    Assert.assertEquals("masterValue3", values[3][2]);
    Assert.assertEquals("value3", values[3][3]);
    Assert.assertEquals("context3", values[3][4]);
  }
  
  /**
   * Tests CSVExporter. Export French, status "initial" and "translated"
   */
  @Test
  public void testGetValues2() {    
    String[][] values = exporter.getValues(
        db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[] {Status.INITIAL, Status.TRANSLATED});
    Assert.assertEquals(3, values.length);
    
    // header
    Assert.assertEquals("Key;Status;Master (de);Value (fr);Context", TremaUtil.arrayToString(values[0], ";"));
    
    // first row
    Assert.assertEquals(5, values[1].length);
    Assert.assertEquals("key1", values[1][0]);
    Assert.assertEquals("initial", values[1][1]);
    Assert.assertEquals("masterValue1\u12AB", values[1][2]);
    Assert.assertEquals("value1\u12AB", values[1][3]);
    Assert.assertEquals("context1", values[1][4]);
    
    // second row
    Assert.assertEquals(5, values[2].length);
    Assert.assertEquals("key2", values[2][0]);
    Assert.assertEquals("translated", values[2][1]);
    Assert.assertEquals("masterValue2���", values[2][2]);
    Assert.assertEquals("value2���", values[2][3]);
    Assert.assertEquals("context2", values[2][4]);
  }
    
  /**
   * Tests CSVExporter. Export French, only status "initial".
   */
  @Test
  public void testGetValues4() {  
    String[][] values = exporter.getValues(
        db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[] {Status.INITIAL});
    Assert.assertEquals(2, values.length);
    
    // header
    Assert.assertEquals("Key;Status;Master (de);Value (fr);Context", TremaUtil.arrayToString(values[0], ";"));
    
    // first row
    Assert.assertEquals(5, values[1].length);
    Assert.assertEquals("key1", values[1][0]);
    Assert.assertEquals("initial", values[1][1]);
    Assert.assertEquals("masterValue1\u12AB", values[1][2]);
    Assert.assertEquals("value1\u12AB", values[1][3]);
    Assert.assertEquals("context1", values[1][4]);
  }
  
  /**
   * Tests CSVExporter. Export French but no status
   */
  @Test
  public void testGetValues5() { 
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[0]);
    Assert.assertEquals(1, values.length);
  }

  /**
   * Tests CSVExporter. Export German (master language).
   */
  @Test
  public void testGetValues6() {
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "de", null);
    Assert.assertEquals(4, values.length);
    
    // header
    Assert.assertEquals("Key;Status;Value (de);Context", TremaUtil.arrayToString(values[0], ";"));
    
    // first row
    Assert.assertEquals(4, values[1].length);
    Assert.assertEquals("key1", values[1][0]);
    Assert.assertEquals("initial", values[1][1]);
    Assert.assertEquals("masterValue1\u12AB", values[1][2]);
    Assert.assertEquals("context1", values[1][3]);
    
    // second row
    Assert.assertEquals(4, values[2].length);
    Assert.assertEquals("key2", values[2][0]);
    Assert.assertEquals("verified", values[2][1]);
    Assert.assertEquals("masterValue2���", values[2][2]);
    Assert.assertEquals("context2", values[2][3]);
    
    // third row
    Assert.assertEquals(4, values[3].length);
    Assert.assertEquals("key3", values[3][0]);
    Assert.assertEquals("special", values[3][1]);
    Assert.assertEquals("masterValue3", values[3][2]);
    Assert.assertEquals("context3", values[3][3]);
  }
 
}
