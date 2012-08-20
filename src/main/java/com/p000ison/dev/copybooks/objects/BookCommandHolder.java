/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

package com.p000ison.dev.copybooks.objects;

import org.bukkit.command.CommandSender;

/**
 * Represents a BookCommandHolder
 */
public class BookCommandHolder {

    private String msg;
    private long id;
    private String permission;

    public BookCommandHolder(long id, String msg, String permission)
    {
        this.id = id;
        this.msg = msg;
        this.permission = permission;
    }

    public long getId()
    {
        return id;
    }

    public String getMessage()
    {
        return msg;
    }

    public boolean hasPermission(CommandSender sender)
    {
        return sender.hasPermission("cb.commands.*") || permission == null || permission.equalsIgnoreCase("none") || sender.hasPermission(permission);
    }
}
