/*
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 */

package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.BookCommandHolder;
import com.p000ison.dev.copybooks.CopyBooks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a SettingsManager
 */
public class SettingsManager {

    private CopyBooks plugin;
    private FileConfiguration config;
    private Map<String, BookCommandHolder> commands = new HashMap<String, BookCommandHolder>();
    private boolean usesMySQL;
    private String host;
    private String user;
    private String password;
    private String db;

    public SettingsManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();

        usesMySQL = config.getBoolean("sql.mysql");
        host = config.getString("sql.host");
        user = config.getString("sql.user");
        password = config.getString("sql.password");
        db = config.getString("sql.db");

        ConfigurationSection section = config.getConfigurationSection("commands");

        for (String command : section.getKeys(false)) {
            commands.put(command, new BookCommandHolder(section.getLong(command + ".id"), section.getString(command + ".message")));
        }
    }

    private void reload()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();

        commands.clear();

        ConfigurationSection section = config.getConfigurationSection("commands");

        for (String command : section.getKeys(false)) {
            commands.put(command, new BookCommandHolder(section.getLong(command + ".id"), section.getString(command + ".message")));
        }
    }

    public long getIdByGroup(Player player)
    {
        ConfigurationSection section = config.getConfigurationSection("groups");

        String group;

        try {
            group = CopyBooks.getPermission().getPrimaryGroup(player);
        } catch (UnsupportedOperationException ex) {
            return section.getLong("default");
        }

        if (group == null) {
            return section.getLong("default");
        }

        return section.getLong(group);
    }

    public BookCommandHolder getCommand(String command)
    {
        return commands.get(command);
    }


    public boolean usesMySQL()
    {
        return usesMySQL;
    }

    public String getHost()
    {
        return host;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

    public String getDatabase()
    {
        return db;
    }
}
