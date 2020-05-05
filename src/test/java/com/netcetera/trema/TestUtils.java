package com.netcetera.trema;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test utilities.
 */
public final class TestUtils {

  private TestUtils() {
  }

  /**
   * Returns a Path to the file at the provided path from within the JAR, throwing an exception if it doesn't exist.
   *
   * @param path local path within the jar
   * @return the requested path
   */
  public static Path getFileFromJar(String path) {
    URL url = TestUtils.class.getResource(path);
    if (url == null) {
      throw new IllegalStateException("Path '" + path + "' does not exist");
    }

    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Failed to get '" + path + "'", e);
    }
  }
}
