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
import com.p000ison.dev.copybooks.objects.BasicBook;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import com.p000ison.dev.copybooks.util.ChatBlock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Max
 */
public class ListCommand extends GenericCommand {

    private final int MAX_BOOKS_PER_PAGE = 9;

    public ListCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setPermissions("cb.command.list");
        setUsages("/cb list [own/page] §f- Lists all books");
        setArgumentRange(0, 1);
        setIdentifiers("list");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        int multiplier = 1;
        boolean isOwn = false;

        if (args.length == 1) {
            if (args[0].matches("[0-9]+")) {
                multiplier = Integer.parseInt(args[0]);
            } else if (args[0].equalsIgnoreCase("own")) {
                isOwn = true;
            }
        }

        String permCreator;

        if (sender.hasPermission("cb.books.*")) {
            if (!isOwn) {
                permCreator = null;
            } else {
                permCreator = sender.getName();
            }
            System.out.println("all");
        } else if (sender.hasPermission("cb.books.own")) {
            permCreator = sender.getName();
            System.out.println("own");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission for any book!");
            return;
        }

        List<BasicBook> books = plugin.getStorageManager().retrieveBooks((multiplier - 1) * MAX_BOOKS_PER_PAGE, multiplier * MAX_BOOKS_PER_PAGE, permCreator);

        if (books.isEmpty()) {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("no.books.found"));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, "List");
        ChatBlock.sendBlank(sender);

        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(true, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c");

        chatBlock.addRow(ChatColor.AQUA + "  " + "ID", "Title", "Author", "Creator");

        for (BasicBook book : books) {

            if (book != null) {
                String id = String.valueOf(book.getId());
                String title = book.getTitle();
                String author = book.getAuthor();
                String creator = book.getCreator();

                chatBlock.addRow(ChatColor.GRAY + "  " + id, title, author, creator);
            }
        }

        chatBlock.sendBlock(sender, MAX_BOOKS_PER_PAGE + 3);

    }
}
