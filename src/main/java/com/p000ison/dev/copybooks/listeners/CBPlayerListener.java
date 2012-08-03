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

        ItemStack book = plugin.getBookManager().getBookById(plugin.getSettingsManager().getIdByGroup(player), 1);

        if (book == null) {
            return;
        }

        player.getInventory().addItem(book);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();


        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            if (block == null) {
                return;
            }

            if (block.getState() instanceof Sign) {
                String[] lines = ((Sign) block.getState()).getLines();

                if (!lines[0].equalsIgnoreCase("[CopyBooks]") || lines[1].equals("") || lines[2].equals("")) {
                    return;
                }

                if (lines[2].equalsIgnoreCase("inf")) {
                    Inventory inventory = plugin.getServer().createInventory(player, InventoryType.CHEST);

                    ItemStack itemStack = plugin.getBookManager().getBookById(Integer.parseInt(lines[1]), 1);
                    ItemStack[] contents = new ItemStack[16];

                    for (int i = 0; i < 16; i++) {
                        contents[i] = itemStack;
                    }
                    inventory.setContents(contents);
                    player.openInventory(inventory);
                } else {
                    int id;

                    try {
                        id = Integer.parseInt(lines[2]);
                    } catch (NumberFormatException ex) {
                        player.sendMessage("Invalid format!");
                        return;
                    }

                    //sakldfnasldfkn
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

        if (lines[0].equalsIgnoreCase("[CopyBooks]") && !player.hasPermission("sb.place.sign")) {
            if (lines[2].equalsIgnoreCase("inf") && !player.hasPermission("sb.place.sing.inf")) {
                event.setCancelled(true);
            }
        }
    }
}
