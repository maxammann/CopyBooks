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

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max
 */
public class Book {

    private long id;
    private String title;
    private String author;
    private ArrayList<String> pages;

    public Book(long id, String title, String author, ArrayList<String> pages)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.pages = pages;
    }

    public Book(String title, String author, ArrayList<String> pages)
    {
        this(0, title, author, pages);
    }

    public Book(ItemStack item)
    {
        NBTTagCompound tag = ((CraftItemStack) (item)).getHandle().tag;

        if (tag == null) {
            tag = new NBTTagCompound();
        }

        String author = tag.getString("author");
        String title = tag.getString("title");

        NBTTagList pages = tag.getList("pages");
        ArrayList<String> realPages = new ArrayList<String>();

        for (int i = 0; i < pages.size(); i++) {
            realPages.add(pages.get(i).getName());
        }

        this.title = title;
        this.author = author;
        this.pages = realPages;
    }


    public long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public ArrayList<String> getPages()
    {
        return pages;
    }

    public ItemStack toItemStack(int amount)
    {
        CraftItemStack item = new CraftItemStack(Material.WRITTEN_BOOK);
        NBTTagCompound newBookData = new NBTTagCompound();

        newBookData.setString("author", this.getAuthor());
        newBookData.setString("title", this.getTitle());

        NBTTagList pages = new NBTTagList();

        List<String> bookPages = this.getPages();

        for (int i = 0; i < bookPages.size(); i++) {
            pages.add(new NBTTagString(String.valueOf(i), bookPages.get(i)));
        }

        newBookData.set("pages", pages);

        item.getHandle().tag = newBookData;

        return (ItemStack) item;
    }
}
