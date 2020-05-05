package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IKeyValuePair;
import com.netcetera.trema.core.api.ITextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


/**
 * Test for {@link JsonExporter}.
 */
class JsonExporterTest {

  private XMLDatabase db;
  private JsonExporter exporter;
  private File file;
  private OutputStreamFactory factory;

  @BeforeEach
  void setUp() throws Exception {
    db = new XMLDatabase();
    db.build(
        "<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
            + "<text key=\"com.netcetera.angular.translation.title\">"
            + "  <context/>"
            + "  <value lang=\"en\" status=\"initial\">Hello World</value>"
            + "  <value lang=\"de\" status=\"initial\">Hallo Welt</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.language.en\">"
            + "  <context/>"
            + "  <value lang=\"en\" status=\"initial\">English</value>"
            + "  <value lang=\"de\" status=\"initial\">Englisch</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.language.de\">"
            + "  <context/>"
            + "  <value lang=\"en\" status=\"initial\">German</value>"
            + "  <value lang=\"de\" status=\"initial\">Deutsch</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.test\">"
            + "  <context/>"
            + "  <value lang=\"en\" status=\"initial\">Test</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.multiline\">"
            + "  <context/>"
            + "  <value lang=\"en\" status=\"initial\">\n  first line\n  second line\n  third line\n</value>"
            + "</text>"
            + "</trema>", false
    );
    file = Mockito.mock(File.class);
    factory = Mockito.mock(OutputStreamFactory.class);
    exporter = new JsonExporter(file, factory);
  }

  @Test
  void shouldFilterValuesWhereLanguageIsNotProvided() {
    // given
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.INITIAL};

    // when
    SortedMap<String, String> mapDe = exporter.getProperties(nodes, "de", states);
    SortedMap<String, String> mapEn = exporter.getProperties(nodes, "en", states);

    // then
    assertThat(mapDe.keySet(), contains("com.netcetera.angular.translation.language.de", "com.netcetera.angular.translation.language.en",
      "com.netcetera.angular.translation.title"));
    assertThat(mapEn.keySet(), contains("com.netcetera.angular.translation.language.de", "com.netcetera.angular.translation.language.en",
      "com.netcetera.angular.translation.multiline", "com.netcetera.angular.translation.test", "com.netcetera.angular.translation.title"));
  }

  @Test
  void shouldAddAllEntriesWhenStatuesNull() {
    // given
    ITextNode[] nodes = db.getTextNodes();

    // when
    SortedMap<String, String> mapDe = exporter.getProperties(nodes, "de", null);
    SortedMap<String, String> mapEn = exporter.getProperties(nodes, "en", null);

    // then
    assertThat(mapDe.keySet(), contains("com.netcetera.angular.translation.language.de", "com.netcetera.angular.translation.language.en",
      "com.netcetera.angular.translation.title"));
    assertThat(mapEn.keySet(), contains("com.netcetera.angular.translation.language.de", "com.netcetera.angular.translation.language.en",
      "com.netcetera.angular.translation.multiline", "com.netcetera.angular.translation.test", "com.netcetera.angular.translation.title"));
  }

  @Test
  void shouldNotAddWhenWrongState() {
    // given
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.SPECIAL};

    // when
    SortedMap<String, String> mapDe = exporter.getProperties(nodes, "de", states);
    SortedMap<String, String> mapEn = exporter.getProperties(nodes, "en", states);

    // then
    assertThat(mapDe, anEmptyMap());
    assertThat(mapEn, anEmptyMap());
  }

  @Test
  void shouldRunThroughFilter() {
    // given
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.INITIAL};
    IExportFilter filter = Mockito.mock(IExportFilter.class);
    exporter.setExportFilter(new IExportFilter[]{filter});

    // when
    exporter.getProperties(nodes, "en", states);

    // then
    verify(filter, times(5)).filter(any(IKeyValuePair.class));
    verifyNoMoreInteractions(filter);
  }

  @Test
  void shouldCreateJsonFormattedFile() throws IOException, ExportException {
    // given
    ITextNode[] nodes = db.getTextNodes();
    OutputStream os = Mockito.mock(OutputStream.class);
    given(factory.createOutputStream(file)).willReturn(os);

    // when
    exporter.export(nodes, null, "en", null);

    // then
    verify(factory).createOutputStream(file);
    InOrder inOrder = Mockito.inOrder(os);
    String jsonString = "{"
        + "\n\t\"com.netcetera.angular.translation.language.de\": \"German\","
        + "\n\t\"com.netcetera.angular.translation.language.en\": \"English\","
        + "\n\t\"com.netcetera.angular.translation.multiline\": \"\\n  first line\\n  second line\\n  third line\\n\","
        + "\n\t\"com.netcetera.angular.translation.test\": \"Test\","
        + "\n\t\"com.netcetera.angular.translation.title\": \"Hello World\""
        + "\n}";
    inOrder.verify(os).write(jsonString.getBytes(StandardCharsets.UTF_8));
    inOrder.verify(os).close();
    verifyNoMoreInteractions(os, factory);
  }

  @Test
  void shouldThrowExportExceptionUponIOExceptionOnOpeningStream() throws IOException {
    // given
    given(factory.createOutputStream(file)).willThrow(new IOException());
    ITextNode[] nodes = db.getTextNodes();

    // when / then
    assertThrows(ExportException.class,
      () -> exporter.export(nodes, null, "en", null));
  }

  @Test
  void shouldThrowExportExceptionUponIOExceptionOnClosingStream() throws IOException {
    // given
    OutputStream os = Mockito.mock(OutputStream.class);
    given(factory.createOutputStream(file)).willReturn(os);
    doThrow(IOException.class).when(os).close();
    ITextNode[] nodes = db.getTextNodes();

    // when / then
    assertThrows(ExportException.class,
      () -> exporter.export(nodes, null, "en", null));
  }

  @Test
  void shouldExportValidEmptyJsonForNonExistingLanguage() throws IOException, ExportException {
    // given
    ITextNode[] nodes = db.getTextNodes();
    OutputStream os = Mockito.mock(OutputStream.class);
    given(factory.createOutputStream(file)).willReturn(os);

    // when
    exporter.export(nodes, null, "fr", null);

    // then
    verify(factory).createOutputStream(file);
    String jsonString = "{\n}";
    InOrder inOrder = Mockito.inOrder(os);
    inOrder.verify(os).write(jsonString.getBytes(StandardCharsets.UTF_8));
    inOrder.verify(os).close();
    verifyNoMoreInteractions(os, factory);
  }
}
