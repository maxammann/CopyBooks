/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.objects.BasicBook;
import com.p000ison.dev.copybooks.objects.Book;
import com.p000ison.dev.copybooks.storage.DBCore;
import com.p000ison.dev.copybooks.storage.MySQLCore;
import com.p000ison.dev.copybooks.storage.SQLiteCore;
import com.p000ison.dev.copybooks.util.Helper;
import org.bukkit.ChatColor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author phaed
 */
public final class StorageManager {

    private CopyBooks plugin;
    private DBCore core;
    private PreparedStatement insertBook, deleteBookById, retrieveBookById, retrieveBooks, retrieveBooksByOwner;
    private CacheMap<Long, Book> cache;

    public StorageManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        cache = new CacheMap<Long, Book>(plugin.getSettingsManager().getCacheSize());
        initiateDB();
    }

    /**
     * Initiates the db
     */
    public void initiateDB()
    {
        if (plugin.getSettingsManager().usesMySQL()) {
            core = new MySQLCore(plugin.getSettingsManager().getHost(), plugin.getSettingsManager().getPort(), plugin.getSettingsManager().getDatabase(), plugin.getSettingsManager().getUser(), plugin.getSettingsManager().getPassword());

            if (core.checkConnection()) {
                CopyBooks.debug("[CopyBooks] Connected successfully to MySQL Database");

                if (!core.existsTable("cb_books")) {
                    CopyBooks.debug("Creating table: cb_books");

                    String query = "CREATE TABLE IF NOT EXISTS `cb_books` ( `id` bigint(20) NOT NULL auto_increment, `title` varchar(26) NOT NULL, `author` varchar(12800) NOT NULL, `created`  timestamp default CURRENT_TIMESTAMP, `creator` varchar(16) NOT NULL, PRIMARY KEY  (`id`));";
                    core.execute(query);
                }

            } else {
                CopyBooks.debug("[CopyBooks] " + ChatColor.RED + plugin.getTranslation("mysql.connection.failed"));
            }
        } else {

            CopyBooks.debug(Level.WARNING, "Using MySQL is highly recommended! (250x faster)");
            core = new SQLiteCore(plugin.getDataFolder().getPath());

            if (core.checkConnection()) {
                CopyBooks.debug("[CopyBooks] Connected successfully to SQLite Database");

                if (!core.existsTable("sc_clans")) {
                    CopyBooks.debug("Creating table: cb_books");

                    String query = "CREATE TABLE IF NOT EXISTS `cb_books` ( `id` INTEGER PRIMARY KEY, `title` varchar(26) NOT NULL, `author` varchar(26) NOT NULL, `pages` varchar(12800) NOT NULL, `created` timestamp default CURRENT_TIMESTAMP, `creator` varchar(16) NOT NULL);";
                    core.execute(query);
                }
            } else {
                CopyBooks.debug("[CopyBooks] " + ChatColor.RED + plugin.getTranslation("sqlite.connection.failed"));
            }
        }
        prepareStatements();
    }

    public void prepareStatements()
    {
        //prepare here
        insertBook = core.prepareStatement("INSERT INTO `cb_books` ( title, author, pages, creator ) VALUES ( ?, ?, ?, ? );");
//        updateBookByTitle = core.prepareStatement("UPDATE `cb_books` SET title = ?, author = ?, pages = ? WHERE title = ?;");
//        updateBookById = core.prepareStatement("UPDATE `cb_books` SET title = ?, author = ?, pages = ? WHERE id = ?;");
//        deleteBookByTitle = core.prepareStatement("DELETE FROM `cb_books` WHERE title = ?;");
        deleteBookById = core.prepareStatement("DELETE FROM `cb_books` WHERE id = ?;");
//        deleteBookByAuthor = core.prepareStatement("DELETE FROM `cb_books` WHERE author = ?;");
        retrieveBookById = core.prepareStatement("SELECT * FROM `cb_books` WHERE id = ?;");
        retrieveBooks = core.prepareStatement("SELECT * FROM  `cb_books` LIMIT ?, ?;");
        retrieveBooksByOwner = core.prepareStatement("SELECT * FROM  `cb_books` WHERE creator = ? LIMIT ?, ?;");
    }

    /**
     * Closes DB connection
     */
    public void closeConnection()
    {
        core.close();
    }

    public boolean deleteBookById(long id)
    {
        try {
            deleteBookById.setLong(1, id);
            int i = deleteBookById.executeUpdate();

            return i != 0;
        } catch (SQLException ex) {
            CopyBooks.debug(null, ex);
        }
        return false;
    }

//    public void updateBookByTitle(String title, Book book)
//    {
//        try {
//            updateBookByTitle.setString(1, book.getTitle());
//            updateBookByTitle.setString(2, book.getAuthor());
//            updateBookByTitle.setString(3, Helper.fromListToJSONString("pages", book.getPages()));
//            updateBookByTitle.setString(4, title);
//            updateBookByTitle.executeUpdate();
//        } catch (SQLException ex) {
//            CopyBooks.debug("Failed updating book!", ex);
//        }
//    }
//
//    public void updateBookById(long id, Book book)
//    {
//        try {
//            updateBookByTitle.setString(1, book.getTitle());
//            updateBookByTitle.setString(2, book.getAuthor());
//            updateBookByTitle.setString(3, Helper.fromListToJSONString("pages", book.getPages()));
//            updateBookByTitle.setLong(4, id);
//            updateBookByTitle.executeUpdate();
//        } catch (SQLException ex) {
//            CopyBooks.debug("Failed updating book!", ex);
//        }
//    }

    public void insertBook(String title, String author, List<String> pages, String pusher)
    {
        try {
            insertBook.setString(1, title);
            insertBook.setString(2, author);
            insertBook.setString(3, Helper.fromListToJSONString("pages", pages));
            insertBook.setString(4, pusher);
            insertBook.executeUpdate();
        } catch (SQLException ex) {
            CopyBooks.debug("Failed inserting book!", ex);
        }
    }

    public void insertBook(Book book, String pusher)
    {
        insertBook(book.getTitle(), book.getAuthor(), book.getPages(), pusher);
    }

    public Book retrieveBook(long id)
    {
        if (id == -1) {
            return null;
        }

        Book cacheId = cache.get(id);

        if (cacheId != null) {
            return cacheId;
        }

        try {
            retrieveBookById.setLong(1, id);
            ResultSet res = retrieveBookById.executeQuery();

            if (res != null) {
                while (res.next()) {
                    try {
                        long iddb = res.getLong("id");
                        Book book = new Book(iddb, res.getString("title"), res.getString("author"), Helper.fromJSONStringtoList("pages", res.getString("pages")), res.getString("creator"));

                        cache.put(iddb, book);
                        return book;
                    } catch (Exception ex) {
                        CopyBooks.debug(null, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            CopyBooks.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return null;
    }

    public List<BasicBook> retrieveBooks(int min, int max, String creator)
    {
        List<BasicBook> out = new ArrayList<BasicBook>();
        try {
            ResultSet res;

            if (creator == null) {
                retrieveBooks.setInt(1, min);
                retrieveBooks.setInt(2, max);
                res = retrieveBookById.executeQuery();
            } else {
                retrieveBooksByOwner.setInt(1, min);
                retrieveBooksByOwner.setInt(2, max);
                retrieveBooksByOwner.setString(3, creator);
                res = retrieveBooksByOwner.executeQuery();
            }

            if (res != null) {
                while (res.next()) {
                    try {
                        BasicBook book = new BasicBook(res.getLong("id"), res.getString("title"), res.getString("author"), res.getString("creator"));
                        out.add(book);
                    } catch (Exception ex) {
                        CopyBooks.debug(null, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            CopyBooks.debug(String.format("An Error occurred: %s", ex.getErrorCode()), ex);
        }

        return out;
    }
}
