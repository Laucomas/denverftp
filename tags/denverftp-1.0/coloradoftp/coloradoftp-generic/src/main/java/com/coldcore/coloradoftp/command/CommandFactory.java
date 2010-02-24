package com.coldcore.coloradoftp.command;

import java.util.Set;

/**
 * Commands factory (creates all available commands).
 *
 * Creates a command based on supplied user input. If a command cannot be created it must
 * be substituted with one of the system commands carrying a proper error message.
 *
 * Control connection relies on this class to get a command object for submitted user input,
 * it then feeds a freshly created command to a command processor for further execution.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface CommandFactory {

  /** List all available command names
   * @return Names of commands (copy of the original set)
   */
  public Set<String> listNames();


  /** Create command object
   * @param input User input
   * @return Command or command with failed message (never NULL)
   */
  public Command create(String input);
}
