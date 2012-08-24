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
import com.p000ison.dev.copybooks.objects.Book;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Max
 */
public class RemoveBookCommand extends GenericCommand {

    public RemoveBookCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setPermissions("cb.command.remove");
        setUsages("/cb remove [id] §f- Removes a book");
        setArgumentRange(1, 1);
        setIdentifiers("remove", "rm");
        setPermissions("cb.commands.remove");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        long id;

        try {
            id = Long.parseLong(args[0]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.id.failed"));
            return;
        }

        Book book = plugin.getStorageManager().retrieveBook(id);

        if (book == null) {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.not.found"));
            return;
        }

        if (!Book.hasPermission(book.getCreator(), sender)) {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("permission.not.for.this.book"));
            return;
        }

        if (plugin.getStorageManager().deleteBookById(id)) {
            sender.sendMessage(ChatColor.GREEN + plugin.getTranslation("book.deleted"));
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.failed.deleted"));
        }
    }
}
