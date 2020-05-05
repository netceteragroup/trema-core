package com.netcetera.trema.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test for the XSD validation in {@link XMLDatabase}.
 */
class XSDValidationTest {

  private static final String XSD_LOCATION = "xsi:noNamespaceSchemaLocation="
    + "\"http://software.group.nca/trema/schema/trema-1.0.xsd\"";
  private static final String SCHEMA_NAMESPACE = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";


  @Test
  void shouldThrowForDuplicateLanguage() {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "  <value lang='de' status='initial'>fr-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when / then
    assertThrows(ParseException.class,
      () -> db.build(xmlContents, true));
  }

  @Test
  void shouldThrowForDuplicateKey() {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when / then
    assertThrows(ParseException.class,
      () -> db.build(xmlContents, true));
  }

  @Test
  void shouldThrowForUnknownStatus() {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='somethingwrong'>de-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when / then
    assertThrows(ParseException.class,
      () -> db.build(xmlContents, true));
  }

  @Test
  void shouldThrowForMissingContext() {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de' " + SCHEMA_NAMESPACE + " " + XSD_LOCATION + ">"
      + "<text key='key1'>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when / then
    assertThrows(ParseException.class,
      () -> db.build(xmlContents, false));
  }

  /**
   * Key1 is used twice in the file which is not allowed by the xsd. However a wrong xsd location is
   * provided and the parser will therefore not validate.
   *
   * @throws Exception in case of errors
   */
  @Test
  void shouldTreatDuplicateKeyAsParseWarningDueToUnknownXsd() throws Exception {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de' " + SCHEMA_NAMESPACE
      + " xsi:noNamespaceSchemaLocation='http://localhost/doesntexist.xsd'>"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when
    db.build(xmlContents, false);

    // then - no exception
    assertThat(db.getParseWarnings(), arrayWithSize(1));
  }

  /**
   * Key1 is used twice in the file which is not allowed. No xsd is provided, so the parser will not find a problem.
   * However Trema does check for duplicated keys and will produce a warning.
   *
   * @throws Exception in case of an error
   */
  @Test
  void shouldTreatDuplicateKeyAsParseWarningDueToMissingXsd() throws Exception {
    // given
    String xmlContents = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<trema masterLang='de'>"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "<text key='key1'> <context>context1</context>"
      + "  <value lang='de' status='initial'>de-value</value>"
      + "</text>"
      + "</trema>";
    XMLDatabase db = new XMLDatabase();

    // when
    db.build(xmlContents, false);

    // then - no exception
    assertThat(db.getParseWarnings(), arrayWithSize(1));
  }
}
