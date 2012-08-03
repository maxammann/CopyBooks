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
package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.Book;
import com.p000ison.dev.copybooks.CopyBooks;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * @author Max
 */
public class BookManager {

    private CopyBooks plugin;

    public BookManager(CopyBooks plugin)
    {
        this.plugin = plugin;
    }

    private NBTTagCompound getTag(ItemStack item)
    {
        return ((CraftItemStack) item).getHandle().tag;
    }

    public boolean unsignBook(ItemStack item)
    {
        NBTTagCompound tag = getTag(item);

        if (tag.get("author") == null || tag.getString("title") == null) {
            return false;
        }
        tag.remove("author");
        tag.remove("title");
        return true;
    }

    public ItemStack getBookById(long id, int amount)
    {
        Book book = plugin.getStorageManager().retrieveBook(id);

        if (book == null) {
            return null;
        }

        return book.toItemStack(amount);
    }
}
