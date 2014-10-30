package com.netcetera.trema.core;



/**
 * A parse error during processing a Trema XML or CSV file.
 */
public class ParseException extends Exception {
  
  /** Comment for <code>serialVersionUID</code>. */
  private static final long serialVersionUID = 1L;
  
  private final int lineNumber;
  
  /**
   * Default no-args constructor.
   */
  public ParseException() {
    super();
    this.lineNumber = 0;
  }
  
  /**
   * Constructs a new trema <code>ParseException</code>.
   * @param message the error message
   */
  public ParseException(String message) {
    super(message);
    this.lineNumber = 0;
  }
  
  /**
   * Constructs a new trema <code>ParseException</code>.
   * @param message the error message
   * @param lineNumber the line number where the parse error occured
   */
  public ParseException(String message, int lineNumber) {
    super(message);
    this.lineNumber = lineNumber;
  }
  
  /**
   * Gets the line number of the parse error.
   * @return the line number of the parse error or zero if no line
   * number is available.
   */
  public int getLineNumber() {
    return lineNumber;
  }
  
}
