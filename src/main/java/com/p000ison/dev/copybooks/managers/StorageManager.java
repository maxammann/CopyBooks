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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;

/**
 * @author phaed
 */
public final class StorageManager {

    private CopyBooks plugin;
    private DBCore core;
    private PreparedStatement insertBook, deleteBookByAuthor, deleteBookById,
            deleteBookByTitle, updateBookById, updateBookByTitle, retrieveBookById;

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
        if (plugin.getSettingsManager().usesMySQL()) {
            core = new MySQLCore(plugin.getSettingsManager().getHost(), plugin.getSettingsManager().getDatabase(), plugin.getSettingsManager().getUser(), plugin.getSettingsManager().getPassword());

            if (core.checkConnection()) {
                CopyBooks.debug("[CopyBooks] Connected successfully to MySQL Database");

                if (!core.existsTable("cb_books")) {
                    CopyBooks.debug("Creating table: cb_books");

                    String query = "CREATE TABLE IF NOT EXISTS `cb_books` ( `id` bigint(20) NOT NULL auto_increment, `title` varchar(25) NOT NULL, `pages` varchar(16) NOT NULL, `title` varchar(1000) NOT NULL,  `created`  timestamp default CURRENT_TIMESTAMP, `creator` varchar(16) NOT NULL, PRIMARY KEY  (`id`));";
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

                    String query = "CREATE TABLE IF NOT EXISTS `cb_books` ( `id` INTEGER PRIMARY KEY, `title` varchar(25) NOT NULL, `author` varchar(16) NOT NULL, `pages` varchar(1000) NOT NULL, `created` timestamp default CURRENT_TIMESTAMP, `creator` varchar(16) NOT NULL);";
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
        updateBookByTitle = core.prepareStatement("UPDATE `cb_books` SET title = ?, author = ?, pages = ? WHERE title = ?;");
        updateBookById = core.prepareStatement("UPDATE `cb_books` SET title = ?, author = ?, pages = ? WHERE id = ?;");
        deleteBookByTitle = core.prepareStatement("DELETE FROM `cb_books` WHERE title = ?;");
        deleteBookById = core.prepareStatement("DELETE FROM `cb_books` WHERE id = ?;");
        deleteBookByAuthor = core.prepareStatement("DELETE FROM `cb_books` WHERE author = ?;");
        retrieveBookById = core.prepareStatement("SELECT * FROM `cb_books` WHERE id = ?;");
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
//        Set<Book> books = retrieveBooks();
//        for (Book book : books) {
//            plugin.getBookManager().addBook(book.getId(), book);
//        }
    }

    public boolean deleteBookById(long id)
    {
        try {
            deleteBookById.setLong(1, id);
            int i = deleteBookById.executeUpdate();
            if (i == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException ex) {
            CopyBooks.debug(null, ex);
        }
        return false;
    }

    public void updateBookByTitle(String title, Book book)
    {
        try {
            updateBookByTitle.setString(1, book.getTitle());
            updateBookByTitle.setString(2, book.getAuthor());
            updateBookByTitle.setString(3, Helper.fromListToJSONString("pages", book.getPages()));
            updateBookByTitle.setString(4, title);
            updateBookByTitle.executeUpdate();
        } catch (SQLException ex) {
            CopyBooks.debug("Failed updating book!", ex);
        }
    }

    public void updateBookById(long id, Book book)
    {
        try {
            updateBookByTitle.setString(1, book.getTitle());
            updateBookByTitle.setString(2, book.getAuthor());
            updateBookByTitle.setString(3, Helper.fromListToJSONString("pages", book.getPages()));
            updateBookByTitle.setLong(4, id);
            updateBookByTitle.executeUpdate();
        } catch (SQLException ex) {
            CopyBooks.debug("Failed updating book!", ex);
        }
    }

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
        try {
            retrieveBookById.setLong(1, id);
            ResultSet res = retrieveBookById.executeQuery();

            if (res != null) {
                while (res.next()) {
                    try {
                        return new Book(res.getLong("id"), res.getString("title"), res.getString("author"), Helper.fromJSONStringtoList("pages", res.getString("pages")), res.getString("creator"));
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

    public List<Book> retrieveBooks(int min, int max)
    {
        List<Book> out = new ArrayList<Book>();

        String query = "SELECT * FROM  `cb_books` LIMIT " + min + ", " + max + ";";

        //      Calendar calendar = Calendar.getInstance();
        //     calendar.add(Calendar.WEEK_OF_YEAR, -2);
        //  Date twoWeeksBefore = calendar.getTime();

        ResultSet res = core.select(query);


        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        //   if (res.getTimestamp("insert_date").after(twoWeeksBefore)) {
                        Book book = new Book(res.getLong("id"), res.getString("title"), res.getString("author"), Helper.fromJSONStringtoList("pages", res.getString("pages")), res.getString("creator"));
                        out.add(book);
                        //         }
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
