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
import com.p000ison.dev.copybooks.objects.Transaction;
import com.p000ison.dev.copybooks.util.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AcceptCommand extends GenericCommand {

    public AcceptCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(0, 0);
        setIdentifiers("accept");
        setUsages("/cb accept - Accept a book offer.");
        setPermissions("cb.commands.accept");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Transaction transaction = plugin.getEconomyManager().getTransactionByOpponent(player.getName());

            if (transaction == null) {
                player.sendMessage("No transaction found!");
                return;
            }

            Player requester = plugin.getServer().getPlayerExact(transaction.getRequester());

            if (requester == null) {
                player.sendMessage("The requester is no longer online!");
                plugin.getEconomyManager().cancelTransactionByOpponent(player.getName());
                return;
            }

            if (!plugin.getEconomyManager().checkTransaction(transaction)) {
                player.sendMessage("You do not have enough money!");
                requester.sendMessage(String.format("%s has not enough money!", player.getName()));
                return;
            }


            Book book = plugin.getStorageManager().retrieveBook(transaction.getBookId());

            if (book == null) {
                requester.sendMessage("Book not found!");
                player.sendMessage("Book not found!");
                return;
            }

            ItemStack item = null;

            try {
                item = book.toItemStack(transaction.getAmount());
            } catch (InvalidBookException e) {
                CopyBooks.debug(null, e);
            }

            if (item == null) {
                CopyBooks.debug("Failed to create ItemStack!");
                return;
            }

            Inventory inv = player.getInventory();

            int missing = InventoryHelper.contains(inv, Material.BOOK_AND_QUILL, transaction.getAmount(), (short) -1);

            if (missing != 0) {
                player.sendMessage(String.format("%d books are missing!", missing));
                requester.sendMessage(String.format("%s is missing %d books are missing!", player.getName(), missing));
                return;
            }

            if (InventoryHelper.getAvailableSlots(inv, Material.BOOK_AND_QUILL, (short)-1, 1) != 0) {
                player.sendMessage("Please make a bit space in your inventory!");
                requester.sendMessage("Other player has not enough space in his inventory!");
                return;
            }

            plugin.getEconomyManager().executeTransaction(transaction);
            InventoryHelper.add(inv, item);
            InventoryHelper.remove(inv, Material.BOOK_AND_QUILL, transaction.getAmount(), (short) -1);
            plugin.getEconomyManager().cancelTransactionByOpponent(player.getName());

            requester.sendMessage("Transaction successfull!");
            player.sendMessage("Transaction successfull!");
        } else {

        }
    }
}
