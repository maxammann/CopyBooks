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

import com.p000ison.dev.copybooks.listeners.CBPlayerListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Max
 */
public class CopyBooks extends JavaPlugin
{

    private static Logger logger;

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

        pm.registerEvents(new CBPlayerListener(this), this);
        super.onEnable();
    }
}
