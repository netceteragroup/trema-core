package com.netcetera.trema.core.exporting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Test for {@link AndroidExporter}.
 */
public class AndroidExporterTest {

  private AndroidExporter exporter = new AndroidExporter(null, null);

  /**
   * Test for validation of android keys.
   */
  @Test
  public void shouldProperlyValidateNames() {
    // given
    //same rules as for java variables are valid for android text keys
    List<String> validNames = Arrays.asList("abc", "ABC", "abc123", "abc_abc", "ABC_abc_123", "_ABC_abc_123");
    List<String> invalidNames = Arrays.asList("123", "123!", "1ABC", "abc-abc", "abc.abc", "", null);

    // when / then
    validNames.forEach(validName -> assertThat(validName, exporter.isValidKeyName(validName), equalTo(true)));
    invalidNames.forEach(invalidName -> assertThat(invalidName, exporter.isValidKeyName(invalidName), equalTo(false)));
  }

    /**
     * Test the resolution of the placeholders.
     */
    @Test
    public void shouldResolveIosPlaceholders() {
      // given
      String original =
          "%d %i %o %u %x %X %f %p "
          + "Some text with mixed %e %E %g %G %c %s %@ iOS and %% Android placeholders %15$d";
      String expected =
          "%1$d %2$d %3$o %4$d %5$x %6$X %7$f %p "
          + "Some text with mixed %8$e %9$E %10$g %11$G %12$c %13$s %14$s iOS and %% Android placeholders %15$d";

      // when
      String resolved = exporter.resolveIOSPlaceholders(original);

      // then
      assertThat(resolved, equalTo(expected));
    }
}
