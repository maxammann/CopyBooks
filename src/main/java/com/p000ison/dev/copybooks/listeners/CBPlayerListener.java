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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
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

        Book book = plugin.getStorageManager().retrieveBook(plugin.getSettingsManager().getIdByGroup(player));

        if (book == null) {
            return;
        }


        player.getInventory().addItem(book.toItemStack(1));
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

                    ItemStack item = book.toItemStack(1);
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

                    Transaction transaction =createTransactionFromString(lines, player.getName());
                    plugin.getEconomyManager().executeTransaction(transaction);
                    player.getInventory().addItem(plugin.getStorageManager().retrieveBook(transaction.getBookId()).toItemStack(transaction.getAmount()));
                }
            }
        }
    }


    public static Transaction createTransactionFromString(String[] lines, String opponent)
    {
        if (!detectSign(lines)) {
            return null;
        }

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
        String[] args = event.getFormat().split(" ");

        if (args.length == 1) {
            BookCommandHolder cmd = plugin.getSettingsManager().getCommand(args[0]);
            if (cmd == null) {
                return;
            }

            Book book = plugin.getStorageManager().retrieveBook(cmd.getId());


            if (book == null) {
                return;
            }

            ItemStack item = book.toItemStack(1);

            if (item != null) {
                player.getInventory().addItem(item);
                if (cmd.getMessage() != null) {
                    player.sendMessage(cmd.getMessage());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();

        Block block = event.getBlock();

        if (block == null) {
            return;
        }

        BlockState state = block.getState();

        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;

        String[] lines = sign.getLines();

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
        } else {
            if (detectSign(lines)) {
                if (!player.hasPermission("cb.place.economy.sign")) {
                    event.setCancelled(true);
                    return;
                }

                if (!plugin.getStorageManager().retrieveBook(Helper.getIdFromSign(lines[2].toCharArray())).getCreator().equals(player.getName())) {
                    player.sendMessage("This is not your book!");
                    return;
                }

                player.sendMessage("CopyBooks economy sign created!");
            }

        }
    }

    //     public static void main(String[] args) {
//         String[] lines = {"p000ison", "", "5[Hallo:23", "4.3:3"};
//
//         if (detectSign(lines)) {
//            System.out.println("success");
//
//             createTransactionFromString(lines , "HUhua");
//         }
//     }
    public static boolean detectSign(String[] lines)
    {
        if (lines[0].isEmpty() || lines[2].isEmpty() || lines[3].isEmpty()) {
            return false;
        }

        return (lines[2].substring(0, 1).matches("[0-9]+") && lines[2].contains("[") && lines[2].contains("]") && lines[3].matches("[0-9]+"));
    }
}
