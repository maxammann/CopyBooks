/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.objects.Book;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static com.p000ison.dev.copybooks.util.BookIO.writeBook;
import static com.p000ison.dev.copybooks.util.BookIO.writeNBTBook;

/**
 * Represents a SaveCommand
 */
public class SaveCommand extends GenericCommand {


    public SaveCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(3, 4);
        setUsages("/cb save <file> <format> <iih/id> [id]");
        setPermissions("cb.commands.save");
        setIdentifiers("save");
        setPermissions("cb.admin.save");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        Book book = null;
        String mode = args[2];

        if (mode.equals("iih")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                try {
                    book = new Book(player.getItemInHand(), player.getName());
                } catch (InvalidBookException e) {
                    player.sendMessage(ChatColor.RED + "Failed to create book!");
                }
            }

        } else if (mode.equals("id")) {
            long id;

            try {
                id = Long.parseLong(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Failed to parse id!");
                return;
            }

            book = plugin.getStorageManager().retrieveBook(id);
        } else {
            sender.sendMessage(ChatColor.RED + "Mode not found!");
        }

        if (book == null) {
            sender.sendMessage(ChatColor.RED + "Book not found!");
            return;
        }

        File dir = new File(plugin.getDataFolder(), "saves");

        if (!dir.exists()) {
            if (!dir.mkdir()) {
                sender.sendMessage(ChatColor.RED + "Failed to create directory!");
                return;
            }
        }

        String fileName = args[0];
        String format = args[1];

        if (format.equalsIgnoreCase("nbt")) {
            File file = new File(dir, fileName + ".book");

            if (file.isDirectory()) {
                return;
            }

            if (file.exists()) {
                return;
            }

            try {
                writeNBTBook(book, new FileOutputStream(file));
            } catch (IOException e) {
                CopyBooks.debug(null, e);
            }

        } else if (format.equalsIgnoreCase("text")) {
            File file = new File(dir, fileName + ".txt");

            if (file.isDirectory()) {
                return;
            }

            if (file.exists()) {
                return;
            }

            try {
                writeBook(book.getPages(), new FileWriter(file));
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Failed at saving file!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Format not found!");
        }
    }
}
