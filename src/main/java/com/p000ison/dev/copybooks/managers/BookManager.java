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

import com.p000ison.dev.copybooks.CopyBooks;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Max
 */
public class BookManager
{

    private CopyBooks plugin;
   // private HashMap<Long, Book> books = new HashMap<Long, Book>();

    public BookManager(CopyBooks plugin)
    {
        this.plugin = plugin;
    }
    public ItemStack createBook(String title, String author, ArrayList<String> pages) {
        ItemStack item = new ItemStack(Material.AIR);
    //    item.set....
        
        
        return item;
    }
    
    
    

}
