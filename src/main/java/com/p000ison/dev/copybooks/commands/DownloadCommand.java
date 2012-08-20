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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DownloadCommand extends GenericCommand {

    public DownloadCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(2, 4);
        setIdentifiers("download", "dl");
        setUsages("/cb dl <default/pastebin> <url/pasteid> [title] [author] - Downloads a book form a webpage.");
        setPermissions("cb.admin.download");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String mode = args[0];

            String author = player.getName();

            if (args.length == 4) {
                author = args[3];
            }


            WrittenBook book;

            String url = null;

            if (mode.equalsIgnoreCase("default")) {
                url = Helper.formatURL(args[1]);
            } else if (mode.equalsIgnoreCase("pastebin")) {
                url = "http://pastebin.com/raw.php?i=" + args[1];
            }

            if (url == null) {
                return;
            }

            String title = url;

            if (args.length >= 3) {
                title = args[2];
            }

            try {
                book = new CraftWrittenBook(title, author, BookIO.readBookUnformattedFromURL(url));
            } catch (IOException e) {
                player.sendMessage("Failed to connect to the url! [" + e.getMessage() + "]");
                return;
            } catch (InvalidBookException e) {
                player.sendMessage("Failed to create book!");
                return;
            }

            try {
                player.getInventory().addItem(book.toItemStack(1));
            } catch (InvalidBookException e) {
                player.sendMessage("Failed to create book!");
                return;
            }

            player.sendMessage("Book downloaded!");
        } else {

        }
    }
}
