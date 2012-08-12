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
package com.p000ison.dev.copybooks.objects;

import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Max
 */
public class Book extends CraftWrittenBook {

    private long id;
    private String creator;

    public Book(long id, String title, String author, List<String> pages, String creator) throws InvalidBookException
    {
        super(title, author, pages);
        this.setId(id);
        this.setCreator(creator);
    }


    public Book(String title, String author, List<String> pages, String creator) throws InvalidBookException
    {
        this(-1, title, author, pages, creator);
    }

    public Book(ItemStack itemStack, String creator) throws InvalidBookException
    {
        super(itemStack);
        this.creator = creator;
    }

    public static boolean hasPermission(String bookCreator, CommandSender player)
    {
        if (player.hasPermission("cb.books.*")) {
            return true;
        }

        if (player.hasPermission("cb.books.own")) {
            if (bookCreator.equals(player.getName())) {
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

    @Override
    public int hashCode()
    {
        return (int) getId() * getTitle().length();
    }

    @Override
    public boolean equals(Object obj)
    {
        Book book;
        if (obj instanceof Book) {
            book = (Book) obj;
        } else {
            return false;
        }

        if (id != book.getId()) {
            return false;
        }

        if (!getTitle().equals(book.getTitle())) {
            return false;
        }

        return true;
    }
}
