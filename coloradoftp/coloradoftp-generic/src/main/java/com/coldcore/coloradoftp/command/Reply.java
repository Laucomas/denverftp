package com.coldcore.coloradoftp.command;

/**
 * Server reply to user.
 * This class is used by a control connections to reply to users.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface Reply {

  /** Set code
   * @param code Code
   */
  public void setCode(String code);


  /** Get code
   * @return code Code
   */
  public String getCode();


  /** Set text (in multiline text lines must be separated by #13#10)
   * @param text Text
   */
  public void setText(String text);


  /** Get text
   * @return text Text
   */
  public String getText();


  /** Prepare reply for transfering
   * @return Prepared reply
   */
  public String prepare();


  /** Set command that triggered this reply
   * @param command Command
   */
  public void setCommand(Command command);


  /** Get command that triggered this reply
   * @return Command
   */
  public Command getCommand();
}
