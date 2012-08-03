package com.p000ison.dev.copybooks.storage;

import com.p000ison.dev.copybooks.CopyBooks;

import java.io.File;
import java.sql.*;

/**
 * @author cc_madelg
 */
public class SQLiteCore implements DBCore {

    private Connection connection;
    private String dbLocation;
    private String dbName;
    private File file;

    /**
     * @param dbLocation
     */
    public SQLiteCore(String dbLocation)
    {
        this.dbName = "CopyBooks";
        this.dbLocation = dbLocation;

        initialize();
    }

    private void initialize()
    {
        if (file == null) {
            File dbFolder = new File(dbLocation);

            if (dbName.contains("/") || dbName.contains("\\") || dbName.endsWith(".db")) {
                CopyBooks.debug("The database name can not contain: /, \\, or .db");
                return;
            }
            if (!dbFolder.exists()) {
                dbFolder.mkdir();
            }

            file = new File(dbFolder.getAbsolutePath() + File.separator + dbName + ".db");
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (SQLException ex) {
            CopyBooks.debug("SQLite exception on initialize " + ex);
        } catch (ClassNotFoundException ex) {
            CopyBooks.debug("You need the SQLite library " + ex);
        }
    }

    /**
     * @return connection
     */
    @Override
    public Connection getConnection()
    {
        if (connection == null) {
            initialize();
        }

        return connection;
    }

    /**
     * @return whether connection can be established
     */
    @Override
    public Boolean checkConnection()
    {
        return getConnection() != null;
    }

    @Override
    public PreparedStatement prepareStatement(String statement)
    {
        try {
            return connection.prepareStatement(statement);
        } catch (SQLException ex) {
            CopyBooks.debug("Error at creating the statement: " + statement + "(" + ex.getMessage() + ")");
        }
        return null;
    }

    /**
     * Close connection
     */
    @Override
    public void close()
    {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            CopyBooks.debug("Failed to close database connection! " + e.getMessage());
        }
    }

    /**
     * Execute a select statement
     *
     * @param query
     * @return
     */
    @Override
    public ResultSet select(String query)
    {
        try {

            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            CopyBooks.debug("Error at SQL Query: " + ex.getMessage());
            CopyBooks.debug("Query: " + query);
        }
        return null;
    }

    /**
     * Execute an insert statement
     *
     * @param query
     */
    @Override
    public void insert(String query)
    {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                CopyBooks.debug("Error at SQL INSERT Query: " + ex);
                CopyBooks.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute an update statement
     *
     * @param query
     */
    @Override
    public void update(String query)
    {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                CopyBooks.debug("Error at SQL UPDATE Query: " + ex);
                CopyBooks.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute a delete statement
     *
     * @param query
     */
    @Override
    public void delete(String query)
    {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                CopyBooks.debug("Error at SQL DELETE Query: " + ex);
                CopyBooks.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute a statement
     *
     * @param query
     * @return
     */
    @Override
    public Boolean execute(String query)
    {
        try {
            getConnection().createStatement().execute(query);
            return true;
        } catch (SQLException ex) {
            CopyBooks.debug(ex.getMessage());
            CopyBooks.debug("Query: " + query);
            return false;
        }
    }

    /**
     * Check whether a table exists
     *
     * @param table
     * @return
     */
    @Override
    public Boolean existsTable(String table)
    {
        try {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            return tables.next();
        } catch (SQLException e) {
            CopyBooks.debug("Failed to check if table '" + table + "' exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check whether a Column exists
     *
     * @param colum
     * @return
     */
    @Override
    public Boolean existsColumn(String tabell, String colum)
    {
        try {
            ResultSet colums = getConnection().getMetaData().getColumns(null, null, tabell, colum);
            return colums.next();
        } catch (SQLException e) {
            CopyBooks.debug("Failed to check if colum '" + colum + "' exists: " + e.getMessage(), e);
            return false;
        }
    }
}
