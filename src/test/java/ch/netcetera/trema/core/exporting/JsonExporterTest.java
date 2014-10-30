package ch.netcetera.trema.core.exporting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.XMLDatabase;
import ch.netcetera.trema.core.api.IExportFilter;
import ch.netcetera.trema.core.api.IKeyValuePair;
import ch.netcetera.trema.core.api.ITextNode;

/**
 * Test for the JsonExporter.
 */
public class JsonExporterTest {
  private XMLDatabase db;
  private JsonExporter exporter;
  private File file;
  private OutputStreamFactory factory;


  /**
   * setUp().
   *
   * @throws Exception in case setUp fails
   */
  @Before
  public void setUp() throws Exception {
    db = new XMLDatabase();
    db.build(
        "<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
            + "<text key=\"com.netcetera.angular.translation.title\">"
            + "<context/>"
            + "<value lang=\"en\" status=\"initial\">Hello World</value>"
            + "<value lang=\"de\" status=\"initial\">Hallo Welt</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.language.en\">"
            + "<context/>"
            + "<value lang=\"en\" status=\"initial\">English</value>"
            + "<value lang=\"de\" status=\"initial\">Englisch</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.language.de\">"
            + "<context/>"
            + "<value lang=\"en\" status=\"initial\">German</value>"
            + "<value lang=\"de\" status=\"initial\">Deutsch</value>"
            + "</text>"
            + "<text key=\"com.netcetera.angular.translation.test\">"
            + "<context/>"
            + "<value lang=\"en\" status=\"initial\">Test</value>"
            + "</text>"
            + "</trema>", false
    );
    file = Mockito.mock(File.class);
    factory = Mockito.mock(OutputStreamFactory.class);
    exporter = new JsonExporter(file, factory);

  }

  @Test
  public void shouldFilterValuesWhereLanguageIsNotProvided() {
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.INITIAL};
    SortedMap<String, String> mapDE = exporter.getProperties(nodes, "de", states);
    SortedMap<String, String> mapEN = exporter.getProperties(nodes, "en", states);


    Assert.assertEquals(3, mapDE.size());
    Assert.assertEquals(4, mapEN.size());
  }

  @Test
  public void shouldAddAllAddAnythingWhenStatuesNull() {
    ITextNode[] nodes = db.getTextNodes();

    SortedMap<String, String> mapDE = exporter.getProperties(nodes, "de", null);
    SortedMap<String, String> mapEN = exporter.getProperties(nodes, "en", null);

    Assert.assertEquals(3, mapDE.size());
    Assert.assertEquals(4, mapEN.size());
  }

  @Test
  public void shouldNotAddWhenWrongState() {
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.SPECIAL};
    SortedMap<String, String> mapDE = exporter.getProperties(nodes, "de", states);
    SortedMap<String, String> mapEN = exporter.getProperties(nodes, "en", states);

    Assert.assertTrue(mapDE.isEmpty());
    Assert.assertTrue(mapEN.isEmpty());

  }

  @Test
  public void shouldRunThroughFilter() {
    ITextNode[] nodes = db.getTextNodes();
    Status[] states = {Status.INITIAL};
    IExportFilter filter = Mockito.mock(IExportFilter.class);
    exporter.setExportFilter(new IExportFilter[]{filter});

    exporter.getProperties(nodes, "en", states);

    Mockito.verify(filter, Mockito.times(4)).filter(Matchers.any(IKeyValuePair.class));
    Mockito.verifyNoMoreInteractions(filter);
  }

  @Test
  public void shouldCreateJSONFormattedFile() throws IOException, ExportException {

    ITextNode[] nodes = db.getTextNodes();

    OutputStream os = Mockito.mock(OutputStream.class);

    BDDMockito.given(factory.createOutputStream(file)).willReturn(os);

    exporter.export(nodes, null, "en", null);

    InOrder inOrder = Mockito.inOrder(os);
    Mockito.verify(factory).createOutputStream(file);
    String jsonString = "{"
        + "\n\t\"com.netcetera.angular.translation.language.de\": \"German\","
        + "\n\t\"com.netcetera.angular.translation.language.en\": \"English\","
        + "\n\t\"com.netcetera.angular.translation.test\": \"Test\","
        + "\n\t\"com.netcetera.angular.translation.title\": \"Hello World\""
        + "\n}";
    inOrder.verify(os).write(jsonString.getBytes("UTF-8"));
    inOrder.verify(os).close();
    Mockito.verifyNoMoreInteractions(os, factory);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = ExportException.class)
  public void shouldThrowExportOxceptionUponIOExceptionOnOpeningStream() throws IOException,
      ExportException {
    BDDMockito.given(factory.createOutputStream(file)).willThrow(IOException.class);

    ITextNode[] nodes = db.getTextNodes();

    exporter.export(nodes, null, "en", null);

    Assert.fail("Should throw ExportException on IOException when closing the stream");

  }

  @Test(expected = ExportException.class)
  public void shouldThrowExportOxceptionUponIOExceptionOnClosingStream() throws IOException, ExportException {
    OutputStream os = Mockito.mock(OutputStream.class);

    BDDMockito.given(factory.createOutputStream(file)).willReturn(os);
    BDDMockito.doThrow(IOException.class).when(os).close();

    ITextNode[] nodes = db.getTextNodes();

    exporter.export(nodes, null, "en", null);

    Assert.fail("Should throw ExportException on IOException when opening the stream");
  }

  @Test
  public void shouldExportValidEmptyJsonForNonExistingLanguage() throws IOException, ExportException {

    ITextNode[] nodes = db.getTextNodes();

    OutputStream os = Mockito.mock(OutputStream.class);

    BDDMockito.given(factory.createOutputStream(file)).willReturn(os);

    exporter.export(nodes, null, "fr", null);

    InOrder inOrder = Mockito.inOrder(os);
    Mockito.verify(factory).createOutputStream(file);
    String jsonString = "{\n}";
    inOrder.verify(os).write(jsonString.getBytes("UTF-8"));
    inOrder.verify(os).close();
    Mockito.verifyNoMoreInteractions(os, factory);
  }


}
