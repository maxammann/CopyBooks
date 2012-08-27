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
import com.p000ison.dev.copybooks.util.InventoryHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Max
 */
public class CreateBookCommand extends GenericCommand {

    public CreateBookCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setPermissions("cb.command.create");
        setUsages("/cb create §f- Creates a book from a id");
        setArgumentRange(1, 2);
        setIdentifiers("create", "c");
        setPermissions("cb.admin.create");
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if (item == null) {
                return;
            }

            int amount = 1;

            if (args.length == 2) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + plugin.getTranslation("book.id.failed"));
                    return;
                }
            }

            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("book.amount.failed"));
                return;
            }

            Book book = plugin.getStorageManager().retrieveBook(id);

            if (book == null) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("book.not.found"));
                return;
            }

            if (!Book.hasPermission(book.getCreator(), player)) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("permission.not.for.this.book"));
                return;
            }

            ItemStack bookItem;
            try {
                bookItem = book.toItemStack(amount);
            } catch (InvalidBookException e) {
                CopyBooks.debug(null, e);
                return;
            }

            Inventory inv = player.getInventory();

            if (plugin.getSettingsManager().isNeedCreateBooks()) {

                int missing = InventoryHelper.contains(inv, Material.BOOK_AND_QUILL, 1, (short) -1);

                if (missing != 0) {
                    player.sendMessage(ChatColor.RED + String.format(plugin.getTranslation("books.missing"), missing));
                    return;
                }

                InventoryHelper.remove(inv, Material.BOOK_AND_QUILL, 1, (short) -1);

            }

            if (InventoryHelper.getAvailableSlots(inv, Material.BOOK_AND_QUILL, (short) -1, 1) != 0) {
                player.sendMessage(ChatColor.RED + plugin.getTranslation("not.enough.space"));
                return;
            }

            InventoryHelper.add(inv, item);

            player.updateInventory();

            sender.sendMessage(String.format(plugin.getTranslation("book.created"), book.getTitle(), book.getAuthor(), id));
        } else {
            sender.sendMessage(ChatColor.RED + plugin.getTranslation("only.players"));
        }
    }
}
