
package ch.netcetera.trema.common;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.XMLTextNode;
import ch.netcetera.trema.core.XMLValueNode;
import ch.netcetera.trema.core.api.ITextNode;



/**
 * Unit test for the <code>TremaUtil</code> class.
 */
public class TremaUtilTest {


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
    Set<String> result = TremaUtil.getLanguages(new ITextNode[] {textNode1, textNode2, textNode3});
    String[] actual = result.toArray(new String[result.size()]);
    Assert.assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
      Assert.assertEquals(expected[i], actual[i]);
    }

    // test with empty text node
    result = TremaUtil.getLanguages(new ITextNode[] {textNode3});
    Assert.assertEquals(0, result.size());

    // test with empty array
    result = TremaUtil.getLanguages(new ITextNode[0]);
    Assert.assertEquals(0, result.size());
  }

  /**
   * Tests stripping extensions.
   */
  @Test
  public void testStripExtension() {
    String expected = "foo";
    String actual = TremaUtil.stripExtension("foo.txt");
    Assert.assertEquals(expected, actual);

    expected = "/folder/path/foo";
    actual = TremaUtil.stripExtension("/folder/path/foo.txt");
    Assert.assertEquals(expected, actual);

    expected = "C:/folder/path/foo";
    actual = TremaUtil.stripExtension("C:/folder/path/foo.txt");
    Assert.assertEquals(expected, actual);

    expected = "foo";
    actual = TremaUtil.stripExtension("foo");
    Assert.assertEquals(expected, actual);

    expected = "foo";
    actual = TremaUtil.stripExtension("foo.");
    Assert.assertEquals(expected, actual);

    expected = ".foo";
    actual = TremaUtil.stripExtension(".foo");
    Assert.assertEquals(expected, actual);

    expected = ".foo";
    actual = TremaUtil.stripExtension(".foo.txt");
    Assert.assertEquals(expected, actual);

    expected = "folder\\path.path\\foo";
    actual = TremaUtil.stripExtension("folder\\path.path\\foo.txt");
    Assert.assertEquals(expected, actual);

    expected = "/folder.folder/path/foo";
    actual = TremaUtil.stripExtension("/folder.folder/path/foo.txt");
    Assert.assertEquals(expected, actual);
  }

  /**
   * Tests TremaUtil.toStringArray().
   */
  @Test
  public void testToString() {
    Object[] strings = new Object[] {"string1", "string2", "string3"};
    String[] result = TremaUtil.toStringArray(strings);
    Assert.assertArrayEquals(strings, result);

    //assertArrayEquals(strings, result);

    // special cases
    Assert.assertNull(TremaUtil.toStringArray(null));
    Assert.assertEquals(0, TremaUtil.toStringArray(new Object[0]).length);
  }

  /**
   * Tests TremaUtil.rotate().
   */
  @Test
  public void testRotate() {
    String[] pool = new String[] {"one", "two", "three"};

    for (int i = 0; i < pool.length; i++) {
      Assert.assertArrayEquals(pool, TremaUtil.rotate(pool, pool[i], pool.length));
    }
    Assert.assertArrayEquals(new String[] {"zero", "one", "two"}, TremaUtil.rotate(pool, "zero", 3));

    pool = new String[] {"one", "two"};
    Assert.assertArrayEquals(new String[] {"zero", "one", "two"}, TremaUtil.rotate(pool, "zero", 3));

    pool = new String[] {"one"};
    Assert.assertArrayEquals(new String[] {"zero", "one"}, TremaUtil.rotate(pool, "zero", 3));

    pool = new String[0];
    Assert.assertArrayEquals(new String[] {"zero"}, TremaUtil.rotate(pool, "zero", 3));

    pool = null;
    Assert.assertArrayEquals(new String[] {"zero"}, TremaUtil.rotate(pool, "zero", 3));
  }

}
