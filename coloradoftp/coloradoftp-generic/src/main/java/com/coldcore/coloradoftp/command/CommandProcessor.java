package com.coldcore.coloradoftp.command;

/**
 * Processes user input.
 *
 * This class executes user commands and sends replies.
 *
 * When dealing with exceptions from command executions, this class must
 * not throw exceptions, instead it must send to user replies with error messages.
 * Note that error from a filesystem class are not always errors producing
 * default LOCAL ERROR reply. That is because filesystem is unable to
 * post replies and it communicates back using a special kind of exceptions.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface CommandProcessor {

  /** Execute the command
   * @param command Command
   */
  public void execute(Command command);
}
