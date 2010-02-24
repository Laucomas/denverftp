package com.coldcore.coloradoftp.plugin.xmlfs.parser;

import com.coldcore.coloradoftp.plugin.xmlfs.User;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import org.w3c.dom.Document;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

/**
 * XML configuration file parser.
 *
 * This class should create properties objects in the same orded as they appear in XML.
 * Usually the first property is more superior than the second and the second one
 * takes over the third one etc.
 */
public interface ConfigurationParser {

    public void setFileAdapter(FileAdapter fileAdapter);

    /** Initialization
     * @param doc Configuration XML
     */
    public void initialize(Document doc) throws ParsingException;

    /** Initialization
     * @param filename Path to configuration XML
     */
    public void initialize(String filename) throws ParsingException, FileNotFoundException;

    /** Initialization
     * @param in Stream with configuration XML
     */
    public void initialize(InputStream in) throws ParsingException;

    /** Create list of users from XML
     * @return List of users and their related objects
     */
    public Set<User> createUsers() throws ParsingException;

    /** Get absolute path to users directory
     * @return Absolute name of users directory
     */
    public String getUsersPath() throws ParsingException;
}
