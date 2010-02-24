package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;

/**
 * Command TVFS.
 * See FTP spec for details on the command.
 *
 * If server operates on "Trivial Virtual File Store" then it must support "FEAT TVFS".
 * This TVFS command exists only to be included into the FEAT.
 */
public class TvfsCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = getReply();
    reply.setCode("200");
    reply.setText("OK");
    return reply;
  }
}
