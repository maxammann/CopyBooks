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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Represents a SettingsManager
 */
public class SettingsManager {

    private CopyBooks plugin;
    private FileConfiguration config;

    public SettingsManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    private void reload()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public long getIdByGroup(Player player)
    {
        ConfigurationSection section = config.getConfigurationSection("groups");

        String group;

        try {
            group = plugin.getPermissions().getPrimaryGroup(player);
        } catch (UnsupportedOperationException ex) {
            return section.getLong("default");
        }

        if (group == null) {
            return section.getLong("default");
        }

        return section.getLong(group);
    }
}
