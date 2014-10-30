package com.netcetera.trema.core;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.XMLDatabase;



/**
 * Unit test for the <code>XMLDatabase</code> class.
 */
public class TestXSDValidation {

  private static final String XSD_LOCATION = "xsi:noNamespaceSchemaLocation="
    + "\"http://software.group.nca/trema/schema/trema-1.0.xsd\"";
  private static final String SCHEMA_NAMESPACE = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";


  /**
   * key1 contains 2 values for the same language, this is not allowed by the xsd.
   *
   * @throws Exception an expected ParseException
   */
  @Test(expected = ParseException.class)
  public void testXsdValidation1() throws Exception {
    XMLDatabase db = new XMLDatabase();
      db.build("<?xml version='1.0' encoding='UTF-8'?>"
         + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
         + "<text key='key1'> <context>context1</context>"
         + "  <value lang='de' status='initial'>de-value</value>"
         + "  <value lang='de' status='initial'>fr-value</value>"
         + "</text>"
         + "</trema>", true);
  }

  /**
   * Key1 is used twice in the file which is not allowed by the xsd.
   *
   * @throws Exception an expected ParseException
   */
  @Test(expected = ParseException.class)
  public void testXsdValidation2() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?>"
        + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "</trema>", true);
  }

  /**
   * Invalid status is used, will cause validation failure.
   *
   * @throws Exception an expected ParseException
   */
  @Test(expected = ParseException.class)
  public void testXsdValidation3() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?>"
        + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='somethingwrong'>de-value</value>"
        + "</text>"
        + "</trema>", true);
  }

  /**
   * Context is not provided, will cause validation failure.
   *
   * @throws Exception an expected ParseException
   */
  @Test(expected = ParseException.class)
  public void testXsdValidation4() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?>"
        + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
        + "<text key='key1'>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "</trema>", false);
  }

  /**
   * Key1 is used twice in the file which is not allowed by the xsd. However a wrong xsd location is
   * provided and the parser will therefore not validate.
   *
   * @throws Exception an expected ParseException
   */
  @Test
  public void testXSDValidation3() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?>"
        + "<trema masterLang='de' " + SCHEMA_NAMESPACE
        + " xsi:noNamespaceSchemaLocation='http://localhost/doesntexist.xsd'>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "</trema>", false);
  }

  /**
   * Key1 is used twice in the file which is not allowed. No xsd is provided, so the parser will not find a problem.
   * However Trema does check for duplicated keys and will produce a warning.
   *
   * @throws Exception an expected ParseException
   */
  @Test
  public void testNoXsdProvided() throws Exception {
    XMLDatabase db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?>"
        + "<trema masterLang='de'>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>de-value</value>"
        + "</text>"
        + "</trema>", false);
    Assert.assertTrue(db.getParseWarnings().length == 1);
  }
}
