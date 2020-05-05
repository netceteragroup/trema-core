
package com.netcetera.trema.common;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLTextNode;
import com.netcetera.trema.core.XMLValueNode;
import com.netcetera.trema.core.api.ITextNode;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;


/**
 * Unit test for {@link TremaCoreUtil}.
 */
public class TremaCoreUtilTest {

  /**
   * Test reading languages used in TextNodes.
   */
  @Test
  public void shouldCollectPresentLanguages() {
    // given
    ITextNode textNode1 = new XMLTextNode("key1", "context1");
    textNode1.addValueNode(new XMLValueNode("lang1", Status.INITIAL, "value1"));
    textNode1.addValueNode(new XMLValueNode("lang2", Status.INITIAL, "value1"));
    textNode1.addValueNode(new XMLValueNode("lang2", Status.INITIAL, "value1"));

    ITextNode textNode2 = new XMLTextNode("key2", "context1");
    textNode2.addValueNode(new XMLValueNode("lang5", Status.INITIAL, "value1"));
    textNode2.addValueNode(new XMLValueNode("lang4", Status.INITIAL, "value1"));
    textNode2.addValueNode(new XMLValueNode("lang3", Status.INITIAL, "value1"));

    ITextNode textNode3 = new XMLTextNode("key2", "context1");

    // when
    Set<String> result = TremaCoreUtil.getLanguages(new ITextNode[] {textNode1, textNode2, textNode3});

    // then
    assertThat(result, contains("lang1", "lang2", "lang3", "lang4", "lang5"));
  }

  @Test
  void shouldSupportNodeWithoutValueNodes() {
    // given
    ITextNode textNode = new XMLTextNode("key2", "context1");

    // when
    Set<String> result = TremaCoreUtil.getLanguages(new ITextNode[]{textNode});

    // then
    assertThat(result, empty());
  }

  @Test
  void shouldHandleEmptyArray() {
    // given / when
    Set<String> result = TremaCoreUtil.getLanguages(new ITextNode[0]);

    // then
    assertThat(result, empty());
  }
}
