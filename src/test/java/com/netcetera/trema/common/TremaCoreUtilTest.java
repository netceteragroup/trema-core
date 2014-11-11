
package com.netcetera.trema.common;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLTextNode;
import com.netcetera.trema.core.XMLValueNode;
import com.netcetera.trema.core.api.ITextNode;



/**
 * Unit test for the <code>TremaCoreUtil</code> class.
 */
public class TremaCoreUtilTest {


  /**
   * Test reading languages used in TextNodes.
   */
  @Test
  public void testGetlanguages() {
    ITextNode textNode1 = new XMLTextNode("key1", "context1");
    textNode1.addValueNode(new XMLValueNode("lang1", Status.INITIAL, "value1"));
    textNode1.addValueNode(new XMLValueNode("lang2", Status.INITIAL, "value1"));
    textNode1.addValueNode(new XMLValueNode("lang2", Status.INITIAL, "value1"));

    ITextNode textNode2 = new XMLTextNode("key2", "context1");
    textNode2.addValueNode(new XMLValueNode("lang5", Status.INITIAL, "value1"));
    textNode2.addValueNode(new XMLValueNode("lang4", Status.INITIAL, "value1"));
    textNode2.addValueNode(new XMLValueNode("lang3", Status.INITIAL, "value1"));

    ITextNode textNode3 = new XMLTextNode("key2", "context1");

    String[] expected = new String[] {"lang1", "lang2", "lang3", "lang4", "lang5"};
    Set<String> result = TremaCoreUtil.getLanguages(new ITextNode[] {textNode1, textNode2, textNode3});
    String[] actual = result.toArray(new String[result.size()]);
    Assert.assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      Assert.assertEquals(expected[i], actual[i]);
    }

    // test with empty text node
    result = TremaCoreUtil.getLanguages(new ITextNode[] {textNode3});
    Assert.assertEquals(0, result.size());

    // test with empty array
    result = TremaCoreUtil.getLanguages(new ITextNode[0]);
    Assert.assertEquals(0, result.size());
  }

}
