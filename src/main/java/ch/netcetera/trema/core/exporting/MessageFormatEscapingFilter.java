package ch.netcetera.trema.core.exporting;



/**
 * Filter to be used when MessageFormat is used to read the properties.
 * When using Properties with MessageFormat, single quotes need to be treated specially: 
 * Two single-quotes next to each other will be interpreted as a single single-quote, 
 * rather than an empty literal string.
 */
public class MessageFormatEscapingFilter extends RegexExportFilter {
  
  /** Original value. */
  public static final String REGEX = "'";
  /** Replaced value. */
  public static final String REPLACEMENT = "''";
  
  /**
   * Constructor.
   */
  public MessageFormatEscapingFilter() {
    super(REGEX, REPLACEMENT);
  }
}
