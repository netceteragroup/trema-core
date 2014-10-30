package ch.netcetera.trema.core.exporting;


/**
 * Exception indicating that the export failed.
 */
public class ExportException extends Exception {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 760497060967484249L;

  /**
   * Constructor.
   */
  public ExportException() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param message the message
   * @param cause the cause
   */
  public ExportException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   * 
   * @param message the message
   */
  public ExportException(String message) {
    super(message);
  }

  /**
   * Constructor.
   * 
   * @param cause the cause
   */
  public ExportException(Throwable cause) {
    super(cause);
  }

  
}
