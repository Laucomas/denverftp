package com.coldcore.coloradoftp.plugin.xmlfs;

import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.NativeFileAdapter;
import com.coldcore.coloradoftp.plugin.xmlfs.parser.ConfigurationParser;
import com.coldcore.coloradoftp.plugin.xmlfs.parser.ManualConfigurationParser;
import com.coldcore.coloradoftp.plugin.xmlfs.parser.ParsingException;
import com.coldcore.coloradoftp.plugin.xmlfs.permissionsmanager.GenericPermissionsManager;
import com.coldcore.coloradoftp.plugin.xmlfs.permissionsmanager.PermissionsManager;
import com.coldcore.coloradoftp.plugin.xmlfs.resolver.GenericVirtualPathResolver;
import com.coldcore.coloradoftp.plugin.xmlfs.resolver.NativeRealPathResolver;
import com.coldcore.coloradoftp.plugin.xmlfs.resolver.RealPathResolver;
import com.coldcore.coloradoftp.plugin.xmlfs.resolver.VirtualPathResolver;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

/**
 * Main class.
 * <p/>
 * By default it maps to a HDD to support the behaviour of the Hard File System plug-in.
 * <p/>
 * Every user starts in his own "/" root directory. This appears as a root directory of the server but
 * it can be just anywhere in the actual filesystem. User cannot go outside of his/her home directory,
 * but virtual folders can be mounted to user's home allowing user to travel anywhere withing the actual
 * filesystem.
 */
public class XmlFS {

    private static Logger log = Logger.getLogger(XmlFS.class);

    protected PermissionsManager permissionsManager;
    protected ConfigurationParser configurationParser;
    protected VirtualPathResolver virtualPathResolver;
    protected RealPathResolver realPathResolver;
    protected Set<User> users;
    protected FileAdapter fileAdapter;


    public XmlFS() {
      virtualPathResolver = new GenericVirtualPathResolver();
      realPathResolver = new NativeRealPathResolver();
      permissionsManager = new GenericPermissionsManager();
      configurationParser = new ManualConfigurationParser();
      fileAdapter = new NativeFileAdapter();
    }


    /**
     * Initialization
     * @param doc Configuration XML
     */
    public void initialize(Document doc) throws ParsingException {
        changeFileAdapter();
        configurationParser.initialize(doc);
        afterInitialization();
    }


    /**
     * Initialization
     * @param filename Path to configuration XML
     */
    public void initialize(String filename) throws ParsingException, FileNotFoundException {
        changeFileAdapter();
        configurationParser.initialize(filename);
        afterInitialization();
    }


    /**
     * Initialization
     * @param in Stream with configuration XML
     */
    public void initialize(InputStream in) throws ParsingException {
        changeFileAdapter();
        configurationParser.initialize(in);
        afterInitialization();
    }


    /**
     * Executed after configuration has been parsed
     */
    protected void afterInitialization() throws ParsingException {
        users = configurationParser.createUsers();
    }


    /**
     * Changes file adapters of the underlying objects
     */
    protected void changeFileAdapter() {
      permissionsManager.setFileAdapter(fileAdapter);
      configurationParser.setFileAdapter(fileAdapter);
    }


    public void setFileAdapter(FileAdapter fileAdapter) {
      this.fileAdapter = fileAdapter;
    }


    public FileAdapter getFileAdapter() {
      return fileAdapter;
    }


    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }


    public void setPermissionsManager(PermissionsManager permissionsManager) {
        this.permissionsManager = permissionsManager;
    }


    public VirtualPathResolver getVirtualPathResolver() {
        return virtualPathResolver;
    }


    public void setVirtualPathResolver(VirtualPathResolver virtualPathResolver) {
        this.virtualPathResolver = virtualPathResolver;
    }


    public RealPathResolver getRealPathResolver() {
        return realPathResolver;
    }


    public void setRealPathResolver(RealPathResolver realPathResolver) {
        this.realPathResolver = realPathResolver;
    }


    public ConfigurationParser getConfigurationParser() {
        return configurationParser;
    }


    public void setConfigurationParser(ConfigurationParser configurationParser) {
        this.configurationParser = configurationParser;
    }


    /**
     * Find user (by username, not case sensitive)
     *
     * @param userSession User sessison
     * @return User object (or default user object) or NULL if user not found
     */
    public User findUser(Session userSession) {
        String username = (String) userSession.getAttribute(SessionAttributeName.USERNAME);
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) return user;
        }
        for (User user : users) {
            if (user.isDefault()) return user;
        }
        log.warn("User " + username + " has no filesystem entry");
        return null;
    }


    public Set<User> getUsers() {
        return users;
  }
}
