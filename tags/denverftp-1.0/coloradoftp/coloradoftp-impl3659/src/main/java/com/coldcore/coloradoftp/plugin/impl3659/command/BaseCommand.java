package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Base class.
 */
abstract public class BaseCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(BaseCommand.class);
  protected SimpleDateFormat dateFormatter;

  //yyyyMMddHHmmss.SSS, yyyyMMddHHmmss.SS, yyyyMMddHHmmss.S, yyyyMMddHHmmss
  public static final String DATE_FORMAT_REGEXP = "yyyyMMddHHmmss(\\.[S]{1,3}){0,1}";


  protected BaseCommand() {
    super();
    setupDateFormatter("yyyyMMddHHmmss");
  }


  /** Setup the date formatter
   * @param format Date format pattern
   */
  protected void setupDateFormatter(String format) {
    dateFormatter = new SimpleDateFormat(format);
    dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

}
