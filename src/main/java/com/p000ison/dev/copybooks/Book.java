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

import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

/**
 * @author Max
 */
public class Book extends CraftWrittenBook {

    private long id;
    private String creator;

    public Book(long id, String title, String author, ArrayList<String> pages, String creator)
    {
        super(title, author, pages, creator);
        this.setId(id);
        this.setCreator(creator);
    }

    public static boolean hasPermission(Book book, CommandSender player)
    {
        if (player.hasPermission("cb.books.*")) {
            return true;
        }

        if (player.hasPermission("cb.books.own")) {
            if (book.getCreator().equals(player.getName())) {
                return true;
            }
        }

        return false;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }
}
