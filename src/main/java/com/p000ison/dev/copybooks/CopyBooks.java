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

import com.p000ison.dev.copybooks.commands.*;
import com.p000ison.dev.copybooks.listeners.CBPlayerListener;
import com.p000ison.dev.copybooks.managers.CommandManager;
import com.p000ison.dev.copybooks.managers.EconomyManager;
import com.p000ison.dev.copybooks.managers.SettingsManager;
import com.p000ison.dev.copybooks.managers.StorageManager;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Max
 */
public class CopyBooks extends JavaPlugin {

    private static Logger logger;
    private ResourceBundle language;
    private CommandManager commandManager;
    private StorageManager storageManager;
    private SettingsManager settingsManager;
    private EconomyManager economyManager;
    private static Permission permission = null;

    public static void debug(String msg, Throwable ex)
    {
        if (logger != null) {
            logger.log(Level.SEVERE, msg, ex);
        }
    }

    public static void debug(String msg)
    {
        debug(Level.INFO, msg);
    }

    public static void debug(Level level, String msg)
    {
        if (logger != null) {
            logger.log(level, msg);
        }
    }

    public static Permission getPermission()
    {
        return permission;
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
        settingsManager = new SettingsManager(this);
        commandManager = new CommandManager(this);
        storageManager = new StorageManager(this);
        economyManager = new EconomyManager(this);
        setupPermissions();

        commandManager = new CommandManager(this);
        commandManager.addCommand(new CopyBookCommand(this, "Copy"));
        commandManager.addCommand(new ListCommand(this, "List"));
        commandManager.addCommand(new CreateBookCommand(this, "Create"));
        commandManager.addCommand(new DownloadCommand(this, "Download"));
        commandManager.addCommand(new UnsignBookCommand(this, "Unsign"));
        commandManager.addCommand(new RemoveBookCommand(this, "Remove"));
        commandManager.addCommand(new HelpCommand(this, "Help"));
        commandManager.addCommand(new SellCommand(this, "Sell"));
        commandManager.addCommand(new AcceptCommand(this, "Accept"));
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (getPermission() != null);
    }

//    public static void main(String[] args)
//    {
//
//        String test = "aweifjewlktnsklfd";
//
//        char[] oldChars = test.toCharArray();
//        int length = oldChars.length;
//
//        char[] newChars = new char[length];
//        for (int i = 0; i < length; i++) {
//            char c = (char) (oldChars[i] - 32);
//            newChars[i] = c;
//        }
//
//        System.out.println(String.valueOf(newChars));
//    }

    public SettingsManager getSettingsManager()
    {
        return settingsManager;
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

    public EconomyManager getEconomyManager()
    {
        return economyManager;
    }
}
