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
 */

package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.api.WrittenBook;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import com.p000ison.dev.copybooks.util.InventoryHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;

import static com.p000ison.dev.copybooks.util.BookIO.*;

/**
 * Represents a SaveCommand
 */
public class LoadCommand extends GenericCommand {


    public LoadCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(2, 5);
        setUsages("/cb load <file> <nbt/text> <title> <author> [lineByline/noOrder] §f- Loads a book from a file");
        setPermissions("cb.commands.load");
        setIdentifiers("load");
        setPermissions("cb.admin.load");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;


            String fileName = args[0];
            String format = args[1];
            WrittenBook book = null;

            if (format.equalsIgnoreCase("nbt")) {
                File file = new File(new File(plugin.getDataFolder(), "saves"), fileName + ".book");

                if (file.isDirectory()) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("file.failed.is.directory"));
                    return;
                }

                if (!file.exists()) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("file.not.exists"));
                    return;
                }

                try {
                    book = readNBTBook(new FileInputStream(file));
                } catch (IOException e) {
                    CopyBooks.debug(null, e);
                }

            } else if (format.equalsIgnoreCase("text")) {
                File file = new File(new File(plugin.getDataFolder(), "saves"), fileName + ".txt");

                if (file.isDirectory()) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("file.failed.is.directory"));
                    return;
                }

                if (!file.exists()) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("file.not.exists"));
                    return;
                }

                String mode = null;
                String title;
                String author;

                if (args.length > 3) {
                    title = args[2];
                    author = args[3];
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("invalid.parameters"));
                    return;
                }

                if (args.length == 5) {
                    mode = args[4];
                }

                try {
                    if (mode == null || mode.equalsIgnoreCase(plugin.getTranslation("lineByLine.mode")) || mode.equalsIgnoreCase("lbl")) {
                        book = new CraftWrittenBook(title, author, readBook(new BufferedReader(new FileReader(file))));
                    } else if (mode.equalsIgnoreCase(plugin.getTranslation("no.order.mode"))) {
                        book = new CraftWrittenBook(title, author, readUnformattedBook(new FileReader(file)));
                    } else {
                        player.sendMessage(ChatColor.RED + plugin.getTranslation("mode.not.found"));
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("failed.reading.file"));
                    return;
                } catch (InvalidBookException e) {
                    sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.create.failed"));
                    return;
                }

            } else {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("book.create.failed"));
                return;
            }

            try {
                InventoryHelper.add(player.getInventory(), book.toItemStack(1));
                sender.sendMessage(ChatColor.GREEN + plugin.getTranslation("book.loaded"));
            } catch (InvalidBookException e) {
                sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.create.failed"));
            }
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("only.players"));
        }
    }
}
