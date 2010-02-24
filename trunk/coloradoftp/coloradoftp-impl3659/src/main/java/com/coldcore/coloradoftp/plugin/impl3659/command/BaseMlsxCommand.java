package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Base class for MLSx commands.
 */
abstract public class BaseMlsxCommand extends BaseCommand {

  private static Logger log = Logger.getLogger(BaseMlsxCommand.class);
  protected Set<String> facts;

  public static final String MLST_SELECTED_FACTS = "MLST.selected.facts";


  public BaseMlsxCommand() {
    super();

    facts = new LinkedHashSet<String>();
    facts.add("type");
    facts.add("size");
    facts.add("modify");
    facts.add("perm");
  }


  /** Get user selected facts
   * @return Set of facts
   */
  protected Set<String> getSelectedFacts() {
    Session session = controlConnection.getSession();
    Set<String> selectedFacts = (Set<String>) session.getAttribute(MLST_SELECTED_FACTS);
    if (selectedFacts == null) {
      selectedFacts = new HashSet<String>(facts);
      session.setAttribute(MLST_SELECTED_FACTS, selectedFacts);
    }
    return selectedFacts;
  }


  /** Get facts for specified path.
   * @param lf File to get the facts for
   * @param cdir Absolute current/listed directory (to set "type=cdir" fact)
   * @return Map where the key is the fact name and the value is the fact value
   */
  protected Map<String,String> getPathFacts(ListingFile lf, String cdir) {
    Map<String,String> map = new HashMap<String,String>();

    if (lf.isDirectory()) {
      if (lf.getAbsolutePath().equals(cdir)) map.put("type", "cdir");
      else map.put("type", "dir");
    } else {
      map.put("type", "file");
      map.put("size", ""+lf.getSize());
    }

    map.put("modify", dateFormatter.format(lf.getLastModified()));
    map.put("perm", lf.getMlsxFacts());

    return map;
  }


  /** Get facts as string to send to the user.
   * @param pathFacts Map where the key is the fact name and the value is the fact value
   * @return Facts string prperly formed based on OPTS command
   */
  protected String preparePathFacts(Map<String,String> pathFacts) {
    Set<String> selectedFacts = getSelectedFacts();
    String pfacts = "";
    for (String fact : pathFacts.keySet())
      if (selectedFacts.contains(fact)) pfacts += fact+"="+pathFacts.get(fact)+";";
    return pfacts;
  }
}
