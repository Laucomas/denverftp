package com.coldcore.coloradoftp.plugin.gateway.dao.database;

import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.dao.BaseUserDAO;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * User data access object (based on data base).
 */
public class DatabaseUserDAO extends BaseUserDAO {

  private static Logger log = Logger.getLogger(DatabaseUserDAO.class);
  protected DataSource dataSource;
  protected String table;
  protected String nameColumn;
  protected String passColumn;
  protected String roleColumn;


  public DatabaseUserDAO(DataSource dataSource) {
    this.dataSource = dataSource;
    if (dataSource == null) throw new IllegalArgumentException("Invalid data source");

    table = "USERS";
    nameColumn = "USERNAME";
    passColumn = "PASSWORD";
    roleColumn = "ROLE";
  }


  /** Get table name
   * @return Table name
   */
  public String getTable() {
    return table;
  }


  /** Set table name
   * @param table Table name
   */
  public void setTable(String table) {
    if (table == null) throw new IllegalArgumentException("Invalid table name");
    this.table = table;
  }


  /** Get user name column name
   * @return Name of the user name column
   */
  public String getNameColumn() {
    return nameColumn;
  }


  /** Set user name column name
   * @param nameColumn Name of the user name column
   */
  public void setNameColumn(String nameColumn) {
    if (nameColumn == null) throw new IllegalArgumentException("Invalid column name");
    this.nameColumn = nameColumn;
  }


  /** Get password column name
   * @return Name of the password column
   */
  public String getPassColumn() {
    return passColumn;
  }


  /** Set password column name
   * @param passColumn Name of the password column
   */
  public void setPassColumn(String passColumn) {
    if (passColumn == null) throw new IllegalArgumentException("Invalid column name");
    this.passColumn = passColumn;
  }


  /** Get role column name
   * @return Name of the role column
   */
  public String getRoleColumn() {
    return roleColumn;
  }


  /** Set role column name
   * @param roleColumn Name of the role column
   */
  public void setRoleColumn(String roleColumn) {
    if (roleColumn == null) throw new IllegalArgumentException("Invalid column name");
    this.roleColumn = roleColumn;
  }


  public User getUser(String username) throws Exception {
    String query = "SELECT "+nameColumn+", "+passColumn+", "+roleColumn+
            " FROM "+table+" WHERE "+nameColumn+" = ?";
    log.debug("Query: "+query);

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      con = dataSource.getConnection();
      stmt = con.prepareStatement(query);
      rs = stmt.executeQuery();
      if (rs.next()) {
        String name = rs.getString(1);
        String pass = rs.getString(2);
        String role = rs.getString(3);

        User user = constructUser(name, pass, role);
        log.debug("User '"+name+"' found with role '"+role+"'");
        return user;
      }
    } finally {
      try {rs.close();} catch (Exception e) {}
      try {stmt.close();} catch (Exception e) {}
      try {con.close();} catch (Exception e) {}
    }

    log.debug("User '"+username+"' not found");
    return null;
  }


  public void insertUser(User user) throws Exception {
    String query = "INSERT INTO "+table+" ("+
            nameColumn+", "+passColumn+", "+roleColumn+") VALUES (?,?,?)";
    log.debug("Query: "+query);

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = dataSource.getConnection();
      stmt = con.prepareStatement(query);
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPassword());
      stmt.setString(3, rolesAsString(user));
      int rows = stmt.executeUpdate();
      log.debug("User '"+user.getUsername()+"' inserted, affected "+rows+" rows");
    } finally {
      try {stmt.close();} catch (Exception e) {}
      try {con.close();} catch (Exception e) {}
    }
  }


  public void updateUser(User user) throws Exception {
    String query = "UPDATE "+table+" SET "+
            nameColumn+" = ?, "+passColumn+" = ?, "+roleColumn+" = ? "+
            "WHERE "+nameColumn+" = ?";
    log.debug("Query: "+query);

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = dataSource.getConnection();
      stmt = con.prepareStatement(query);
      int rows = stmt.executeUpdate();
      log.debug("User '"+user.getUsername()+"' updated, affected "+rows+" rows");
    } finally {
      try {stmt.close();} catch (Exception e) {}
      try {con.close();} catch (Exception e) {}
    }
  }
}
