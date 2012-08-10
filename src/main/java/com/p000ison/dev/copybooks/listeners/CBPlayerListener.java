/*
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 */

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
package com.p000ison.dev.copybooks.listeners;

import com.p000ison.dev.copybooks.*;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Max
 */
public class CBPlayerListener implements Listener {

    private CopyBooks plugin;

    public CBPlayerListener(CopyBooks plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if(player.hasPlayedBefore()) {
            return;
        }

        Book book = plugin.getStorageManager().retrieveBook(plugin.getSettingsManager().getIdByGroup(player));

        if (book == null) {
            return;
        }


        try {

            player.getInventory().addItem(book.toItemStack(1));
        } catch (InvalidBookException e) {
            CopyBooks.debug(null, e);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();


        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block == null) {
            return;
        }

        if (block.getState() instanceof Sign) {
            String[] lines = ((Sign) block.getState()).getLines();
            if (action.equals(Action.RIGHT_CLICK_BLOCK)) {

                if (lines[0].equalsIgnoreCase("[CopyBooks]")) {
                    if (lines[1].equals("")) {
                        return;
                    }


                    Inventory inventory = plugin.getServer().createInventory(player, InventoryType.CHEST);

                    Book book = plugin.getStorageManager().retrieveBook(Integer.parseInt(lines[1]));

                    if (book == null) {
                        return;
                    }

                    ItemStack item = null;
                    try {
                        item = book.toItemStack(1);
                    } catch (InvalidBookException e) {
                        CopyBooks.debug(null, e);
                    }
                    ItemStack[] contents = new ItemStack[27];

                    for (int i = 0; i < 27; i++) {
                        contents[i] = item;
                    }

                    inventory.setContents(contents);
                    player.openInventory(inventory);

                } else {
                    if (!detectSign(lines)) {
                        return;
                    }

                    Transaction transaction = createTransactionFromString(lines, player.getName());

                    if (transaction == null) {
                        player.sendMessage("Failed to create the transaction!");
                        return;
                    }


                    Book book = plugin.getStorageManager().retrieveBook(transaction.getBookId());

                    if (book == null) {
                        player.sendMessage("Failed to get the book!");
                        return;
                    }

                    if (!plugin.getEconomyManager().executeTransaction(transaction)) {
                        player.sendMessage("You dont have enough money!");
                        return;
                    }

                    try {
                        player.getInventory().addItem(book.toItemStack(transaction.getAmount()));
                    } catch (InvalidBookException e) {
                        CopyBooks.debug(null, e);
                    }
                }
            }
        }
    }


    public static Transaction createTransactionFromString(String[] lines, String opponent)
    {
        String requester = lines[0] + lines[1];
        char[] chars = lines[2].toCharArray();

        int amount = Helper.getAmontFromSign(chars);
        long id = Helper.getIdFromSign(chars);
        double price = Double.parseDouble(lines[3]);

        return new Transaction(requester, opponent, id, price, amount);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");

        if (args.length == 1) {
            BookCommandHolder cmd = plugin.getSettingsManager().getCommand(args[0].trim());
            if (cmd == null) {
                return;
            }

            Book book = plugin.getStorageManager().retrieveBook(cmd.getId());


            if (book == null) {
                return;
            }

            ItemStack item = null;

            try {
                item = book.toItemStack(1);
            } catch (InvalidBookException e) {
                CopyBooks.debug(null, e);
            }

            if (item != null) {
                player.getInventory().addItem(item);
                if (cmd.getMessage() != null) {
                    player.sendMessage(cmd.getMessage());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(SignChangeEvent event)
    {
        Player player = event.getPlayer();

        String[] lines = event.getLines();

        if (lines[0].equalsIgnoreCase("[CopyBooks]")) {

            if (!player.hasPermission("cb.place.sign")) {
                event.setCancelled(true);
                player.sendMessage("You are not allowed to place this sign!");
                return;
            }

            if (!lines[1].matches("[0-9]+")) {
                event.setCancelled(true);
                player.sendMessage("The second line must contain the id of the book!");
                return;
            }

            player.sendMessage("CopyBooks created!");
            event.setLine(0, ChatColor.GREEN + "[CopyBooks]");
        } else {
            if (detectSignPlace(lines)) {
                if (!player.hasPermission("cb.place.economy.sign")) {
                    event.setCancelled(true);
                    return;
                }

                Book book = plugin.getStorageManager().retrieveBook(Helper.getIdFromSign(lines[2].toCharArray()));

                if (book == null) {
                    player.sendMessage("The book doesnt exist!");
                    return;
                }

                if (!Book.hasPermission(book, player)) {
                    player.sendMessage("You dont have permission to this book!");
                    return;
                }

                String name = player.getName();

                if (lines[0].isEmpty()) {
                    if (name.length() > 15) {
                        event.setLine(0, name.substring(0, 15));
                        event.setLine(1, name.substring(15, name.length()));
                    } else {
                        event.setLine(0, name);
                    }
                } else {
                    if (!lines[0].equals(name)) {

                        if (lines[0].equals("[AdminShop]") && !player.hasPermission("cb.admin.adminshop")) {
                            player.sendMessage("You dont have permission!");
                            event.setCancelled(false);
                            return;
                        }

                        if (!player.hasPermission("cb.admin.others")) {
                            player.sendMessage("You dont have permission!");
                            event.setCancelled(false);
                            return;
                        }
                    }
                }

                if (lines[1].isEmpty()) {
                    String bookTitle = book.getTitle();

                    int bookTitleLength = bookTitle.length();
                    int test = 15 - bookTitleLength - 2;

                    if (test < 0) {
                        bookTitle = bookTitle.substring(0, bookTitleLength - 2) + "...";
                    }

                    event.setLine(2, "[" + bookTitle + "]");
                } else {
                    String[] toFormat = lines[2].split(":");
                    String bookTitle = book.getTitle();

                    int bookTitleLength = bookTitle.length();
                    int line2Length = lines[2].length();
                    int test = 15 - bookTitleLength - line2Length - 2;

                    if (test < 0) {
                        bookTitle = bookTitle.substring(0, bookTitleLength - line2Length - 2) + "...";
                    }

                    event.setLine(2, toFormat[0] + "[" + bookTitle + "]:" + toFormat[1]);
                }

                event.getBlock().getState().update();

                player.sendMessage("CopyBooks economy sign created!");
            }
        }
    }

//    public static void main(String[] args)
//    {
//        String[] lines = {"vxcv", "", "d:23", "4.5"};
//
//        if (detectSignPlace(lines)) {
//            System.out.println("success");
//
//            createTransactionFromString(lines, "HUhua");
//
//            String[] toFormat = lines[2].split(":");
//            String bookTitle = "addddvgfhfgh";
//
//            int bookTitlelength = bookTitle.length();
//            int line2length = lines[2].length();
//            int test = 15 - bookTitlelength - line2length - 2;
//
//            if (test < 0) {
//                bookTitle = bookTitle.substring(0, bookTitlelength - line2length - 2) + "...";
//            }
//
//            System.out.println(bookTitle);
//        }
//    }

    public static boolean detectSignPlace(String[] lines)
    {
        return (!lines[2].isEmpty() && lines[2].substring(0, 1).matches("[0-9]+")) && lines[2].contains(":") && lines[3].matches("[0-9]+");
    }

    public static boolean detectSign(String[] lines)
    {
        return (!lines[2].isEmpty() && lines[2].substring(0, 1).matches("[0-9]+")) && lines[2].contains("[") && lines[2].contains("]:") && lines[3].matches("[0-9]+");
    }
}
