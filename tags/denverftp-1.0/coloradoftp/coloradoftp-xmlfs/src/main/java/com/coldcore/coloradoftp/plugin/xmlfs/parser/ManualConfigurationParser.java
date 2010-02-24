package com.coldcore.coloradoftp.plugin.xmlfs.parser;

import com.coldcore.coloradoftp.plugin.xmlfs.DirProperty;
import com.coldcore.coloradoftp.plugin.xmlfs.User;
import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.plugin.xmlfs.VirtualFolder;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import com.coldcore.misc5.Xml;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML configuration file parser.
 * This class parses XML using proprietary (manual) logic.
 */
public class ManualConfigurationParser implements ConfigurationParser {
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([\\w]+)\\}");	// \$\{([a-zA-Z_0-9]+)\}
  private static Logger log = Logger.getLogger(ManualConfigurationParser.class);
  protected Document doc;
  protected String usersPath;
  protected FileAdapter fileAdapter;


  public void setFileAdapter(FileAdapter fileAdapter) {
    this.fileAdapter = fileAdapter;
  }


  public void initialize(String filename) throws ParsingException, FileNotFoundException {
    if (filename == null) throw new IllegalArgumentException("Filename must not be null");

    File file = new File(filename);
    if (file.exists() && file.isFile()) {
      Document doc;
      try {
        doc = Xml.loadXml(file);
      } catch (ParserConfigurationException e) {
        throw new ParsingException(e);
      } catch (IOException e) {
        throw new ParsingException(e);
      } catch (SAXException e) {
        throw new ParsingException(e);
      }
      initialize(doc);
      return;
    }

    ResourceLoader loader = new DefaultResourceLoader(getClass().getClassLoader());
    Resource r = loader.getResource(filename);
    if (r != null) {
      Document doc;
      try {
        doc = Xml.loadXml(r.getInputStream());
      } catch (ParserConfigurationException e) {
        throw new ParsingException(e);
      } catch (IOException e) {
        throw new ParsingException(e);
      } catch (SAXException e) {
        throw new ParsingException(e);
      }
      initialize(doc);
      return;
    }

    throw new FileNotFoundException("Configuration file cannot be found: "+filename);
  }


  public void initialize(InputStream in) throws ParsingException {
    if (in == null) throw new IllegalArgumentException("Stream must not be null");
    Document doc;
    try {
      doc = Xml.loadXml(in);
    } catch (ParserConfigurationException e) {
      throw new ParsingException(e);
    } catch (IOException e) {
      throw new ParsingException(e);
    } catch (SAXException e) {
      throw new ParsingException(e);
    }
    initialize(doc);
  }


  public void initialize(Document doc) throws ParsingException {
    this.doc = doc;
    if (doc == null) throw new IllegalArgumentException("Document must not be null");

    usersPath = getSingleNodeText("file-system/users-path", doc);
    if (usersPath == null) throw new ParsingException("error for node <users-path>");
  }


  /** Get text from node if it exists in single instance
   * @param node Node name (eg users/user/home)
   * @param n Parent node
   * @return Text or NULL if does not exist or no value or not single
   */
  private String getSingleNodeText(String node, Node n) {
    Element[] elemEs = Xml.findElements(node, n);
    if (elemEs.length != 1) return null;
    String str = Xml.getNodeText(elemEs[0]);
    if (str == null || str.length() == 0) return null;
    return substituteVariables(str);
  }


  /**
   * Performs variable substitution on all variables found in a source string. 
   * Variables are identified by ${...} where the text between the curly braces is the variable name. 
   * First, environment variables are searched for a matching variable name.  If no environment variable 
   * is found with the same name, then Java system properties are searched for the variable name.  Substitution 
   * is performed if any variable match is found.  If multiple variables are present in the source string 
   * they will be substituted in order.  If no variables are found in the source string, or no corresponding 
   * environment or Java system properties were found for the variables in the source string, then no changes 
   * will be made to the source.
   * @param source string possibly containing variables to substitute with environment variables or Java system properties.
   * @return the source string with variable substitutions applied, or null if the given source string was null.
   */
  private static String substituteVariables(final String source) {
    if (source == null) {
      return null;
    }
    
    String substitutedSource = "";
    int lastSourceIndex = 0;
    Matcher variableMatcher = VARIABLE_PATTERN.matcher(source);
    
    // Performing searches/substitutions for all variables identified in the source: 
    while (variableMatcher.find()) {
      // Extract the variable name:
      String variableName = variableMatcher.group(1);
      if ((variableName != null) && (variableName.length() > 0)) {
        String environmentVariableValue = System.getenv(variableName);
        String systemPropertyValue = System.getProperty(variableName);
        String value = null;
        
        // First try to use an environment variable:
        if (environmentVariableValue != null) {
          value = environmentVariableValue;
        }
        // Next try to use a system property:
        else if (systemPropertyValue != null) {
          value = systemPropertyValue;
        }
        
        // Perform substitution if any matching variables were found:
        if (value != null) {
          substitutedSource += source.substring(lastSourceIndex, variableMatcher.start());
          substitutedSource += value;
          lastSourceIndex = variableMatcher.end();
        }
      }
    }
    
    // Copy the remainder of the source to the substituted string:
    substitutedSource += source.substring(lastSourceIndex);
    return substitutedSource;
  }


  /** Get regular expresions and corresponding YES/NO value from node.
   * This method processes only nodes that have attributes.
   * @param node Node name (eg users/user/home)
   * @param n Parent node
   * @param fileRegexp TRUE to get expression from "file-regexp" attribute, FALSE to get from "folder-regexp"
   * @return Map where key is expression and value is YES/NO, in the same order as appear in XML
   */
  private Map<String,Boolean> getYesNoRegexpProperties(String node, Node n, boolean fileRegexp) throws ParsingException {
    Map<String,Boolean> map = new LinkedHashMap<String,Boolean>();
    Element[] elemEs = Xml.findElements(node, n);

    //Filter out those who do not have attributes
    Set<Element> set = new LinkedHashSet<Element>(Arrays.asList(elemEs));
    Iterator<Element> it = set.iterator();
    while (it.hasNext()) {
      Element e = it.next();
      if (!e.hasAttributes()) it.remove();
    }

    for (Element elem : set) {
      String regexp = elem.getAttribute(fileRegexp ? "file-regexp" : "folder-regexp");
      if (regexp.length() == 0) continue;
      String value = Xml.getNodeText(elem);
      if (!value.equals("0") && !value.equals("1")) throw new ParsingException("Invalid value in node <"+node+">");
      map.put(regexp, value.equals("1"));
    }

    return map;
  }


  /** Get all properties for a folder in order as they appead in XML
   * @param n Folder node with properties nodes
   * @param user User
   * @param folderAbsPath Absolute path to the folder
   * @return Set of properties in the same order as they appear in XML
   */
  private Set<DirProperty> getFolderProperties(Node n, User user, String folderAbsPath) throws ParsingException {
    Set<DirProperty> properties = new LinkedHashSet<DirProperty>();

    // **/properties
    Element[] propertiesEs = Xml.findElements("properties", n);
    log.debug("Found "+propertiesEs.length+" <properties> nodes, user: "+user.getUsername());

    for (Element propertiesE : propertiesEs) {
      DirProperty property = new DirProperty(fileAdapter);

      // users/user/home/properties/@dir
      String dir = propertiesE.getAttribute("dir");
      String absDir = dir.length() == 0 ? folderAbsPath : folderAbsPath+"/"+dir;
      property.setDirectory(absDir);

      // users/user/home/properties/@spread
      String spread = propertiesE.getAttribute("spread");
      if (spread.length() > 0) {
        if (!spread.equals("1") && !spread.equals("0"))
          throw new ParsingException("User "+user.getUsername()+", <home/properties> invalid 'spread'");
        property.setSpread(spread.equals("1"));
      }

      // users/user/home/properties/access (with attributes)
      property.addAccessFileRegexp(getYesNoRegexpProperties("access", propertiesE, true));
      property.addAccessFolderRegexp(getYesNoRegexpProperties("access", propertiesE, false));

      // users/user/home/properties/list (with attributes)
      property.addListFileRegexp(getYesNoRegexpProperties("list", propertiesE, true));
      property.addListFolderRegexp(getYesNoRegexpProperties("list", propertiesE, false));

      // users/user/home/properties/create (with attributes)
      property.addCreateFileRegexp(getYesNoRegexpProperties("create", propertiesE, true));
      property.addCreateFolderRegexp(getYesNoRegexpProperties("create", propertiesE, false));

      // users/user/home/properties/rename (with attributes)
      property.addRenameFileRegexp(getYesNoRegexpProperties("rename", propertiesE, true));
      property.addRenameFolderRegexp(getYesNoRegexpProperties("rename", propertiesE, false));

      // users/user/home/properties/delete (with attributes)
      property.addDeleteFileRegexp(getYesNoRegexpProperties("delete", propertiesE, true));
      property.addDeleteFolderRegexp(getYesNoRegexpProperties("delete", propertiesE, false));

      // users/user/home/properties/append (with attributes)
      property.addAppendFileRegexp(getYesNoRegexpProperties("append", propertiesE, true));
      if (getYesNoRegexpProperties("append", propertiesE, false).size() > 0) {
        throw new ParsingException("User "+user.getUsername()+", error for node <home/properties/append>");
      }

      // users/user/home/properties/overwrite (with attributes)
      property.addOverwriteFileRegexp(getYesNoRegexpProperties("overwrite", propertiesE, true));
      if (getYesNoRegexpProperties("overwrite", propertiesE, false).size() > 0) {
        throw new ParsingException("User "+user.getUsername()+", error for node <home/properties/overwrite>");
      }

      properties.add(property);
    }

    return properties;
  }


  /** Create list of users from XML
   * @return List of users and their related objects
   */
  public Set<User> createUsers() throws ParsingException {

    try {
      Set<User> users = new HashSet<User>();

      // users/user
      Element[] userEs = Xml.findElements("file-system/users/user", doc);
      log.debug("Found "+userEs.length+" <user> nodes");

      for (Element userE : userEs) {
        User user = new User();

        // users/user/username
        user.setUsername(getSingleNodeText("username", userE));
        if (user.getUsername() == null) throw new ParsingException("<user> node error for node <username>");

        // users/user/@default
        String def = userE.getAttribute("default");
        if (def.length() > 0) {
          if (!def.equals("1") && !def.equals("0"))
            throw new ParsingException("User "+user.getUsername()+", invalid 'default'");
          user.setDefault(def.equals("1"));
        }

        // users/user/home
        Element[] homeEs = Xml.findElements("home", userE);
        if (homeEs.length != 1) throw new ParsingException("User "+user.getUsername()+", error for node <home>");

        UserHome home = new UserHome(fileAdapter);
        home.setPath(usersPath+"/"+user.getUsername());
        user.setHome(home);

        // users/user/home/properties
        home.addProperties(getFolderProperties(homeEs[0], user, home.getPath()));

        // users/user/home/virtual-folders/folder
        Element[] folderEs = Xml.findElements("virtual-folders/folder", homeEs[0]);
        log.debug("Found "+folderEs.length+" <virtual-folders/folder> nodes, user: "+user.getUsername());
        Set<VirtualFolder> folders = new HashSet<VirtualFolder>();

        for (Element folderE : folderEs) {
          VirtualFolder folder = new VirtualFolder();

          folder.setName(getSingleNodeText("name", folderE));
          if (folder.getName() == null)
            throw new ParsingException("User "+user.getUsername()+", error for node <home/virtual-folders/folder/name>");

          folder.setPath(getSingleNodeText("path", folderE));
          if (folder.getPath() == null)
            throw new ParsingException("User "+user.getUsername()+", error for node <home/virtual-folders/folder/name>");

          folder.addProperties(getFolderProperties(folderE, user, folder.getPath()));

          folders.add(folder);
        }

        home.addFolders(folders);

        users.add(user);
      }

      log.info("Loaded "+users.size()+" users");
      return users;

    } catch (Throwable e) {
      log.error("Error parsing configuration", e);
      if (e instanceof ParsingException) throw (ParsingException) e;
      throw new ParsingException(e);
    }
  }


  /** Get absolute path to users directory
   * @return Absolute name of users directory
   */
  public String getUsersPath() {
    return usersPath;
  }
}
