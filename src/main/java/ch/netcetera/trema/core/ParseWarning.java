package ch.netcetera.trema.core;



/**
 * Represents a warning that occurs during parsing.
 */
public class ParseWarning {
  
  private String message = null;
  private int lineNumber = 0;
  
  /**
   * Constructs a new parse warning.
   * @param message the warning message
   * @param lineNumber the line number
   */
  public ParseWarning(String message, int lineNumber) {
    this.message = message;
    this.lineNumber = lineNumber;
  }
  
  /**
   * Gets the warning message.
   * @return the warning message.
   */
  public String getMessage() {
    return message;
  }
  
  /**
   * Gets the line number of this warning.
   * @return the line number of this warning or zero if no line
   * number is available.
   */
  public int getLineNumber() {
    return lineNumber;
  }
  
}
