/**
 *
 */
package ch.netcetera.trema.core.exporting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


/**
 * When used through m2eclipse, maven plugins that create build artifacts should
 * use a non standard output stream. This allows m2eclipse / eclipse to detect
 * newly created files and refresh the workspace accordingly. This class allows
 * to inject a OutputStreamFactory into the the classes that need to write using
 * an output stream.
 *
 *
 * @author tzueblin
 *
 */
public interface OutputStreamFactory {

  /**
   * Create an output stream from a file.
   *
   * @param file the file
   * @return OutputStream the output stream
   * @throws IOException in case the stream could not be created
   */
  OutputStream createOutputStream(File file) throws IOException;

}
