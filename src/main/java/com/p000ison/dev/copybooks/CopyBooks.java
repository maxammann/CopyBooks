/*
 * Copyright (C) 2012 p000ison
 * 
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 * 
 */
package com.p000ison.dev.copybooks;

import com.p000ison.dev.copybooks.commands.CopyBookCommand;
import com.p000ison.dev.copybooks.commands.ListCommand;
import com.p000ison.dev.copybooks.listeners.CBPlayerListener;
import com.p000ison.dev.copybooks.managers.BookManager;
import com.p000ison.dev.copybooks.managers.CommandManager;
import com.p000ison.dev.copybooks.managers.StorageManager;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Max
 */
public class CopyBooks extends JavaPlugin
{

    private static Logger logger;
    private ResourceBundle language;
    private CommandManager commandManager;
    private StorageManager storageManager;
    private BookManager bookManager;

    public static void debug(String msg, Throwable ex)
    {
        if (logger != null) {
            logger.log(Level.SEVERE, msg, ex);
        }
    }

    public static void debug(String msg)
    {
        if (logger != null) {
            logger.log(Level.INFO, msg);
        }
    }

    public static void debug(Level level, String msg)
    {
        if (logger != null) {
            logger.log(level, msg);
        }
    }

    @Override
    public void onEnable()
    {
        logger = getLogger();

        PluginManager pm = getServer().getPluginManager();

        language = ResourceBundle.getBundle("languages.lang");

        setupManagers();

        pm.registerEvents(new CBPlayerListener(this), this);
        super.onEnable();
    }

    private void setupManagers()
    {
        commandManager = new CommandManager(this);
        bookManager = new BookManager(this);
        storageManager = new StorageManager(this);
        
        commandManager = new CommandManager(this);
        commandManager.addCommand(new CopyBookCommand(this, "Copy"));
        commandManager.addCommand(new ListCommand(this, "List"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        commandManager.executeAll(sender, command, label, args);
        return true;
        
    }
    
    

    @Override
    public void onDisable()
    {
        ResourceBundle.clearCache(getClass().getClassLoader());
        language = null;
        storageManager.closeConnection();
        super.onDisable();
    }

    public BookManager getBookManager()
    {
        return bookManager;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public StorageManager getStorageManager()
    {
        return storageManager;
    }

    public String getTranslation(String key)
    {
        return language.getString(key);
    }
}
