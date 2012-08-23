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

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.objects.Book;
import com.p000ison.dev.copybooks.objects.BookCommandHolder;
import com.p000ison.dev.copybooks.objects.Transaction;
import com.p000ison.dev.copybooks.util.Helper;
import com.p000ison.dev.copybooks.util.InventoryHelper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

        if (plugin.getSettingsManager().isOnlyFirstJoin() && !player.hasPlayedBefore()) {
            return;
        }

        for (long id : plugin.getSettingsManager().getIdsByGroup(player)) {
            Book book = plugin.getStorageManager().retrieveBook(id);

            if (book == null) {
                continue;
            }

            try {
                player.getInventory().addItem(book.toItemStack(1));
            } catch (InvalidBookException e) {
                CopyBooks.debug(null, e);
            }
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

                if (lines[0].equalsIgnoreCase(ChatColor.GREEN + "[CopyBooks]")) {

                    if (!player.hasPermission("cb.signs.unlimited")) {
                        player.sendMessage("You dont have permission!");
                        return;
                    }

                    if (lines[1].isEmpty()) {
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

                    if (!player.hasPermission("cb.signs.buy")) {
                        player.sendMessage("You dont have permission!");
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
                        player.sendMessage("bought!" + book.getTitle());
                        InventoryHelper.add(player.getInventory(), book.toItemStack(transaction.getAmount()));
                    } catch (InvalidBookException e) {
                        CopyBooks.debug(null, e);
                    }
                }
            }
        }
    }


    public static Transaction createTransactionFromString(String[] lines, String opponent)
    {
        char firstChar = lines[1].charAt(0);
        String requester = Helper.removeColors(lines[0] + (firstChar == ' ' ? "" : firstChar));
        System.out.println(requester);
        System.out.println(requester.toCharArray()[requester.length() - 1]);
        System.out.println(requester.toCharArray()[requester.length() - 1] == ' ');
//        char[] chars = lines[2].toCharArray();

//        int amount = Helper.getAmontFromSign(chars);
//        long id = Helper.getIdFromSign(chars);
        String[] idAndAmount = lines[2].split(":");
        long id = Long.parseLong(idAndAmount[0]);
        int amount = Integer.parseInt(idAndAmount[1]);
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

            if (!cmd.hasPermission(player)) {
                player.sendMessage("You dont have permission!");
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
    public void onSignPlace(SignChangeEvent event)
    {
        Player player = event.getPlayer();

        String[] lines = event.getLines();

        if (lines[0].equalsIgnoreCase("[CopyBooks]")) {

            if (!player.hasPermission("cb.signs.place-unlimited")) {
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
            if (detectSign(lines)) {
                if (!player.hasPermission("cb.signs.place-economy")) {
                    event.setCancelled(true);
                    return;
                }

                Book book = plugin.getStorageManager().retrieveBook(Helper.getIdFromSign(lines[2].toCharArray()));

                if (book == null) {
                    player.sendMessage("The book doesnt exist!");
                    return;
                }

                if (!Book.hasPermission(book.getCreator(), player)) {
                    player.sendMessage("You dont have permission to this book!");
                    return;
                }


                char charToAdd = ' ';

                //if both lines are empty -> auto-generate
                if (lines[0].isEmpty() && lines[1].isEmpty()) {
                    //check for name lenght
                    if (player.getName().length() <= 15) {
                        event.setLine(0, player.getName());
                    } else {
                        event.setLine(0, player.getName().substring(0, 15));
                        charToAdd = player.getName().charAt(16);
                    }

                } else if (!lines[0].isEmpty()) {
                    //check for admin shop
                    if (lines[0].equals("[AdminShop]") && !player.hasPermission("cb.admin.adminshop")) {
                        player.sendMessage("You dont have permission!");
                        event.setCancelled(false);
                        return;
                    }

                    //if the second line is not empty we have to care about a char in the second line
                    if (!lines[1].isEmpty()) {
                        if (lines[1].charAt(0) != ' ') {
                            charToAdd = lines[1].charAt(0);
                        }
                    }

                    String wholeName = (charToAdd == ' ' ? "" : charToAdd) + lines[0];

                    if (!wholeName.equals(player.getName()) && !player.hasPermission("cb.admin.others")) {
                        player.sendMessage("You dont have permission!");
                        event.setCancelled(false);
                        return;
                    }
                }

                event.setLine(1, generateSignBookTitle(book.getTitle(), charToAdd));

                //check for other lines
                String[] idAndAmount = lines[2].split(":");

                if (idAndAmount.length != 2) {
                    player.sendMessage("Invalid id or/and amount! 1");
                    event.setCancelled(true);
                    return;
                }

                if (!idAndAmount[0].matches("[0-9]+") || !idAndAmount[1].matches("[0-9]+")) {
                    player.sendMessage("Invalid id or/and amount! 2");
                    event.setCancelled(true);
                    return;
                }

                if (!lines[3].matches("[0-9]+")) {
                    player.sendMessage("Invalid price!");
                    event.setCancelled(true);
                    return;
                }


                event.getBlock().getState().update();

                player.sendMessage("CopyBooks economy sign created!");
            }
        }
    }

    public static String generateSignBookTitle(String bookTitle, char add)
    {
        int bookTitleLength = bookTitle.length();
        int test = 15 - bookTitleLength - 7; // [] and one one possible char

        if (test < 0) {
            bookTitle = bookTitle.substring(0, bookTitleLength - 7) + "...";
        }

        return add + "[" + bookTitle + "]";
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

    public static boolean detectSign(String[] lines)
    {
        return lines[2].contains(":") && lines[3].matches("[0-9]+");
    }
}
