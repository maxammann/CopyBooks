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
import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.api.WrittenBook;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import com.p000ison.dev.copybooks.util.BookIO;
import com.p000ison.dev.copybooks.util.Helper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DownloadCommand extends GenericCommand {

    private static final String pasteBinApi = "http://pastebin.com/raw.php?i=";

    public DownloadCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(3, 5);
        setIdentifiers("download", "dl");
        setUsages("/cb dl <default/pastebin> <url/pasteid> <id> [title] [author] §f- Downloads a book form a webpage.");
        setPermissions("cb.admin.download");
        dependAnotherThread(true);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            long id;

            try {
                id = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + plugin.getTranslation("book.id.failed"));
                return;
            }

            String mode = args[0];

            String author = player.getName();

            if (args.length == 5) {
                author = args[4];
            }


            WrittenBook book;

            String url = null;

            if (mode.equalsIgnoreCase("default")) {
                url = Helper.formatURL(args[1]);
            } else if (mode.equalsIgnoreCase("pastebin")) {
                url = pasteBinApi + args[1];
            }

            if (url == null) {
                return;
            }

            String title = url;

            if (args.length >= 4) {
                title = args[3];
            }

            try {
                book = new CraftWrittenBook(title, author, BookIO.readBookUnformattedFromURL(url));
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + String.format(plugin.getTranslation("url.failed.to.connect"), e.getMessage()));
                return;
            } catch (InvalidBookException e) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("book.create.failed"));
                return;
            }

            plugin.getStorageManager().insertBook(book, player.getName());

            player.sendMessage(ChatColor.RED + plugin.getTranslation("book.downloaded.pushed.to.db"));
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("only.players"));
        }
    }
}
