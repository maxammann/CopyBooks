/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

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

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.objects.Book;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Max
 */
public class CopyBookCommand extends GenericCommand {

    public CopyBookCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setPermissions("cb.command.copy");
        setUsages("/cb copy §f- Copy a book");
        setArgumentRange(0, 0);
        setIdentifiers("copy");
        setPermissions("cb.admin.copy");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if (item == null || !item.getType().equals(Material.WRITTEN_BOOK)) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("book.not.written"));
                return;
            }

            Book book;

            try {
                book = new Book(item, player.getName());
            } catch (InvalidBookException e) {
                CopyBooks.debug(null, e);
                return;
            }

            plugin.getStorageManager().insertBook(book, player.getName());

            sender.sendMessage(String.format(ChatColor.GREEN + plugin.getTranslation("book.copied"), book.getTitle(), book.getAuthor()));
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("only.players"));
        }
    }
}
