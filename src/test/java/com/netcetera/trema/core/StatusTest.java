package com.netcetera.trema.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for {@link Status}.
 */
public class StatusTest {

  @Test
  void testCompareTo() {
    assertTrue(0 == Status.INITIAL.compareTo(Status.INITIAL));
    assertTrue(0 > Status.INITIAL.compareTo(Status.TRANSLATED));
    assertTrue(0 > Status.INITIAL.compareTo(Status.VERIFIED));
    assertTrue(0 > Status.INITIAL.compareTo(Status.SPECIAL));

    assertTrue(0 < Status.TRANSLATED.compareTo(Status.INITIAL));
    assertTrue(0 == Status.TRANSLATED.compareTo(Status.TRANSLATED));
    assertTrue(0 > Status.TRANSLATED.compareTo(Status.VERIFIED));
    assertTrue(0 > Status.TRANSLATED.compareTo(Status.SPECIAL));

    assertTrue(0 < Status.VERIFIED.compareTo(Status.INITIAL));
    assertTrue(0 < Status.VERIFIED.compareTo(Status.TRANSLATED));
    assertTrue(0 == Status.VERIFIED.compareTo(Status.VERIFIED));
    assertTrue(0 > Status.VERIFIED.compareTo(Status.SPECIAL));

    assertTrue(0 < Status.SPECIAL.compareTo(Status.INITIAL));
    assertTrue(0 < Status.SPECIAL.compareTo(Status.TRANSLATED));
    assertTrue(0 < Status.SPECIAL.compareTo(Status.VERIFIED));
    assertTrue(0 == Status.SPECIAL.compareTo(Status.SPECIAL));
  }

  @Test
  void testToString() {
    assertThat(Status.INITIAL.toString(), equalTo("initial"));
    assertThat(Status.TRANSLATED.toString(), equalTo("translated"));
    assertThat(Status.VERIFIED.toString(), equalTo("verified"));
    assertThat(Status.SPECIAL.toString(), equalTo("special"));
  }

  @Test
  void testValueOf() {
    assertThat(Status.valueOf("initial"), equalTo(Status.INITIAL));
    assertThat(Status.valueOf("translated"), equalTo(Status.TRANSLATED));
    assertThat(Status.valueOf("verified"), equalTo(Status.VERIFIED));
    assertThat(Status.valueOf("special"), equalTo(Status.SPECIAL));
    assertThat(Status.valueOf("blah"), equalTo(Status.UNDEFINED));
  }
}
