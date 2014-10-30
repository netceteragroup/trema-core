package com.netcetera.trema.core;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.Status;



/**
 * Unit test for the <code>Status</code> class.
 */
public class TestStatus {
  
  /**
   * Tests the Status.compareTo method.
   */
  @Test
  public void testCompareTo() {
    Assert.assertTrue(0 == Status.INITIAL.compareTo(Status.INITIAL));
    Assert.assertTrue(0 > Status.INITIAL.compareTo(Status.TRANSLATED));
    Assert.assertTrue(0 > Status.INITIAL.compareTo(Status.VERIFIED));
    Assert.assertTrue(0 > Status.INITIAL.compareTo(Status.SPECIAL));
    
    Assert.assertTrue(0 < Status.TRANSLATED.compareTo(Status.INITIAL));
    Assert.assertTrue(0 == Status.TRANSLATED.compareTo(Status.TRANSLATED));
    Assert.assertTrue(0 > Status.TRANSLATED.compareTo(Status.VERIFIED));
    Assert.assertTrue(0 > Status.TRANSLATED.compareTo(Status.SPECIAL));
    
    Assert.assertTrue(0 < Status.VERIFIED.compareTo(Status.INITIAL));
    Assert.assertTrue(0 < Status.VERIFIED.compareTo(Status.TRANSLATED));
    Assert.assertTrue(0 == Status.VERIFIED.compareTo(Status.VERIFIED));
    Assert.assertTrue(0 > Status.VERIFIED.compareTo(Status.SPECIAL));
    
    Assert.assertTrue(0 < Status.SPECIAL.compareTo(Status.INITIAL));
    Assert.assertTrue(0 < Status.SPECIAL.compareTo(Status.TRANSLATED));
    Assert.assertTrue(0 < Status.SPECIAL.compareTo(Status.VERIFIED));
    Assert.assertTrue(0 == Status.SPECIAL.compareTo(Status.SPECIAL));
  }
  
  /**
   * Tests the Status.toString method.
   */
  @Test
  public void testToString() {
    Assert.assertEquals("initial", Status.INITIAL.toString());
    Assert.assertEquals("translated", Status.TRANSLATED.toString());
    Assert.assertEquals("verified", Status.VERIFIED.toString());
    Assert.assertEquals("special", Status.SPECIAL.toString());
  }
  
  /**
   * Tests the Status.valueOf method.
   */
  @Test
  public void testValueOf() {
    Assert.assertTrue(Status.INITIAL == Status.valueOf("initial"));
    Assert.assertTrue(Status.TRANSLATED == Status.valueOf("translated"));
    Assert.assertTrue(Status.VERIFIED == Status.valueOf("verified"));
    Assert.assertTrue(Status.SPECIAL == Status.valueOf("special"));
    Assert.assertTrue(Status.UNDEFINED == Status.valueOf("blah"));
  }

}
