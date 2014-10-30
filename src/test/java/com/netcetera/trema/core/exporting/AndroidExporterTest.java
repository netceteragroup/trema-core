package com.netcetera.trema.core.exporting;

import org.junit.Test;

import com.netcetera.trema.core.exporting.AndroidExporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Testclass for {@link AndroidExporter}.
 */
public class AndroidExporterTest {

  /**
   * Test for validation of android keys.
   */
  @Test
  public void testIsValidKeyName() {
    //same rules as for java variables are valid for android text keys
    AndroidExporter exporter = new AndroidExporter(null, null);
    assertTrue(exporter.isValidKeyName("abc"));
    assertTrue(exporter.isValidKeyName("ABC"));
    assertTrue(exporter.isValidKeyName("abc123"));
    assertTrue(exporter.isValidKeyName("abc_abc"));
    assertTrue(exporter.isValidKeyName("ABC_abc_123"));
    assertTrue(exporter.isValidKeyName("_ABC_abc_123"));

    assertFalse(exporter.isValidKeyName("123"));
    assertFalse(exporter.isValidKeyName("123!"));
    assertFalse(exporter.isValidKeyName("1ABC"));
    assertFalse(exporter.isValidKeyName(""));
    assertFalse(exporter.isValidKeyName("abc-abc"));
    assertFalse(exporter.isValidKeyName("abc.abc"));
    assertFalse(exporter.isValidKeyName(null));

  }

    /**
     * Test the resolution of the placholders.
     */
    @Test
    public void testPlacholderResolution() {
      AndroidExporter exporter = new AndroidExporter(null, null);

      final String original =
          "%d %i %o %u %x %X %f %p "
          + "Some text with mixed %e %E %g %G %c %s %@ iOS and %% Android placeholders %15$d";
      String expected =
          "%1$d %2$d %3$o %4$d %5$x %6$X %7$f %p "
          + "Some text with mixed %8$e %9$E %10$g %11$G %12$c %13$s %14$s iOS and %% Android placeholders %15$d";
      String resolved = exporter.resolveIOSPlaceholders(original);

      assertEquals(expected, resolved);
    }
}
