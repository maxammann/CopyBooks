package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.Book;
import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.Helper;
import com.p000ison.dev.copybooks.storage.DBCore;
import com.p000ison.dev.copybooks.storage.MySQLCore;
import com.p000ison.dev.copybooks.storage.SQLiteCore;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.debugging.Level;
import java.util.logging.Level;
import org.bukkit.ChatColor;

/**
 * @author phaed
 */
public final class StorageManager
{
    
    private CopyBooks plugin;
    private DBCore core;
    private PreparedStatement insertBook, updateBook, deleteBook, retrieveBooks;

    /**
     *
     */
    public StorageManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        initiateDB();
        updateDatabase();
        importFromDatabase();
    }

    /**
     * Initiates the db
     */
    public void initiateDB()
    {
        if (plugin.getSettingsManager().isUseMysql()) {
            core = new MySQLCore(plugin.getSettingsManager().getHost(), plugin.getSettingsManager().getDatabase(), plugin.getSettingsManager().getUsername(), plugin.getSettingsManager().getPassword());
            
            if (core.checkConnection()) {
                CopyBooks.debug("[CopyBooks] Connected successfully to MySQL Database");
                
                if (!core.existsTable("sc_clans")) {
                    CopyBooks.debug("Creating table: sc_clans");
                    
                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( `id` bigint(20) NOT NULL auto_increment, `verified` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `color_tag` varchar(25) NOT NULL, `name` varchar(100) NOT NULL, `friendly_fire` tinyint(1) default '0', `founded` bigint NOT NULL, `last_used` bigint NOT NULL, `packed_allies` text NOT NULL, `packed_rivals` text NOT NULL, `packed_bb` mediumtext NOT NULL, `cape_url` varchar(255) NOT NULL, `flags` text NOT NULL, `balance` double(64,2), PRIMARY KEY  (`id`), UNIQUE KEY `uq_CopyBooks_1` (`tag`));";
                    core.execute(query);
                }
                
            } else {
                CopyBooks.debug("[CopyBooks] " + ChatColor.RED + plugin.getLang("mysql.connection.failed"));
            }
        } else {
            
            CopyBooks.debug(Level.WARNING, "Using MySQL is highly recommended! (250x faster)");
            core = new SQLiteCore(plugin.getDataFolder().getPath());
            
            if (core.checkConnection()) {
                CopyBooks.debug("[CopyBooks] Connected successfully to SQLite Database");
                
                if (!core.existsTable("sc_clans")) {
                    CopyBooks.debug("Creating table: sc_clans");
                    
                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( `id` bigint(20), `verified` tinyint(1) default '0', `tag` varchar(25) NOT NULL, `color_tag` varchar(25) NOT NULL, `name` varchar(100) NOT NULL, `friendly_fire` tinyint(1) default '0', `founded` bigint NOT NULL, `last_used` bigint NOT NULL, `packed_allies` text NOT NULL, `packed_rivals` text NOT NULL, `packed_bb` mediumtext NOT NULL, `cape_url` varchar(255) NOT NULL, `flags` text NOT NULL, `balance` double(64,2) default 0.0,  PRIMARY KEY  (`id`), UNIQUE (`tag`));";
                    core.execute(query);
                }
                
                
            } else {
                CopyBooks.debug("[CopyBooks] " + ChatColor.RED + plugin.getLang("sqlite.connection.failed"));
            }
        }
        prepareStatements();
    }
    
    public void prepareStatements()
    {
        //prepare here
    }

    /**
     * Closes DB connection
     */
    public void closeConnection()
    {
        core.close();
    }

    /**
     * Import all data from database to memory
     */
    public void importFromDatabase()
    {
    }
    
    public Set<Book> retrieveClans()
    {
        Set<Book> out = new HashSet<Book>();
        
        String query = "SELECT * FROM  `cb_books`;";
        ResultSet res = core.select(query);
        
        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        Book book = new Book(res.getString("page"), res.getString("author"), Helper.fromJSONStringtoList("pages", res.getString("pages")));
                        out.add(book);
                    } catch (Exception ex) {
                        CopyBooks.debug(null, ex);
                    }
                }
            } catch (SQLException ex) {
                CopyBooks.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
            }
        }
        
        return out;
    }
    
    private void updateDatabase()
    {
    }
}
