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

/**
 *
 * @author Max
 */
public class BookManager
{

    private CopyBooks plugin;

    public BookManager(CopyBooks plugin)
    {
        this.plugin = plugin;
    }
//Basic code to get the title of a book
//    public static void main(String[] args)
//    {
//        ItemStack i = new ItemStack(0, 5, (short) 5, (byte) 15);
//
//        ((CraftItemStack) i).getHandle().getTag();
//
//        File file = new File("test");
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException ex) {
//                Logger.getLogger(BookManager.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//        config.set("test", i);
//        try {
//            config.save(file);
//        } catch (IOException ex) {
//            Logger.getLogger(BookManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
