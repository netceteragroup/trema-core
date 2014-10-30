/**
 * 
 */
package com.netcetera.trema.core.exporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;



/**
 * Default implementation used when not inside an m2eclipse or maven context.
 * 
 * 
 * @author tzueblin
 * 
 */
public class FileOutputStreamFactory implements OutputStreamFactory {

  /*
   * (non-Jsdoc)
   * @see
   * com.netcetera.trema.console.OutputStreamFactory#createOutputStream(java.
   * io.File)
   */
  @Override
  public OutputStream createOutputStream(File file) throws IOException {
    return new FileOutputStream(file);
  }

}
