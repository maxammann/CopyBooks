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
package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.Book;
import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.GenericCommand;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Max
 */
public class CopyBookCommand extends GenericCommand
{

    public CopyBookCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setPermissions("cb.command.copy");
        setUsages("/cb copy - Copy a book");
        setArgumentRange(0, 0);
        setIdentifiers("copy");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if (item == null) {
                return;
            }

            if (!item.getType().equals(Material.APPLE)) {
                return;
            }


            ArrayList<String> pages = new ArrayList<String>();

            pages.add("Im a page!");

            Book book = new Book("Im a title!", "Im the author!", pages);

            plugin.getStorageManager().insertBook(book);

        } else {
            sender.sendMessage(plugin.getTranslation("only.player"));
        }
    }
}
