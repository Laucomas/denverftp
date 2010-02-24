package com.coldcore.coloradoftp.command.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.Reply;

import java.util.StringTokenizer;

/**
 * @see com.coldcore.coloradoftp.command.Command
 */
public class GenericReply implements Reply {

  protected String code;
  protected String text;
  protected Command command;


  public String getCode() {
    return code;
  }


  public void setCode(String code) {
    this.code = code;
  }


  public String getText() {
    return text;
  }


  public void setText(String text) {
    this.text = text;
  }


  public Command getCommand() {
    return command;
  }


  public void setCommand(Command command) {
    this.command = command;
  }


  public String prepare() {
    if (code == null || code.trim().length() != 3) throw new IllegalArgumentException("Invalid code");
    if (text == null) text = "";

    StringBuffer sb = new StringBuffer();
    text = text.trim();
    code = code.trim();

    //This may be just code
    if (text.equals("")) {
      sb.append(code).append("\r\n");
      return sb.toString();
    }

    //Or code with a single text line
    if (text.indexOf("\r\n") == -1) {
      sb.append(code).append(" ").append(text).append("\r\n");
      return sb.toString();
    }

    //Or multiline text
    sb.append(code).append("-");
    StringTokenizer st = new StringTokenizer(text, "\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken().trim();
      if (!st.hasMoreTokens()) sb.append(code);
      sb.append(" ").append(line).append("\r\n");
    }
    return sb.toString();
  }
}
