/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

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

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.objects.BookCommandHolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
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
    private int port;
    private boolean onlyFirstJoin;
    private int cacheSize;

    public SettingsManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();

        load();
    }

    private void load()
    {
        usesMySQL = config.getBoolean("sql.mysql", false);
        host = config.getString("sql.host");
        user = config.getString("sql.user");
        port = config.getInt("sql.port");
        password = config.getString("sql.password");
        db = config.getString("sql.database");
        onlyFirstJoin = config.getBoolean("settings.only-on-first-join", true);
        cacheSize = config.getInt("settings.cache-size");

        commands.clear();

        ConfigurationSection section = config.getConfigurationSection("commands");

        for (String command : section.getKeys(false)) {
            commands.put(command, new BookCommandHolder(section.getLong(command + ".id"), section.getString(command + ".message"), section.getString(command + ".permission")));
        }
    }

    public void reload()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();

        load();
    }

    public List<Long> getIdsByGroup(Player player)
    {
        ConfigurationSection section = config.getConfigurationSection("groups");

        String group;

        try {
            group = CopyBooks.getPermission().getPrimaryGroup(player);
        } catch (UnsupportedOperationException ex) {
            return section.getLongList("default");
        }

        if (group == null) {
            return section.getLongList("default");
        }

        return section.getLongList(group);
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

    public int getPort()
    {
        return port;
    }

    public boolean isOnlyFirstJoin()
    {
        return onlyFirstJoin;
    }

    public int getCacheSize()
    {
        return cacheSize;
    }
}
