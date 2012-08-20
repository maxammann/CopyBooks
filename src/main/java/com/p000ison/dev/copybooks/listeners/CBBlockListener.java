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

package com.p000ison.dev.copybooks.listeners;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 * Represents a CBBlockListener
 */
public class CBBlockListener implements Listener {
    private CopyBooks plugin;

    public CBBlockListener(CopyBooks plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockDispense(BlockRedstoneEvent event)
    {
        Block block = event.getBlock();
        System.out.println(block.getType());
        BlockState state = block.getState();

        if (!(state instanceof Dispenser)) {
            return;
        }

        Dispenser dispenser = (Dispenser) state;

        org.bukkit.material.Dispenser matDispenser = (org.bukkit.material.Dispenser) block.getState().getData();

        Block signBlock = block.getRelative(matDispenser.getFacing().getOppositeFace());
        System.out.println(signBlock.getType());

        BlockState signState = signBlock.getState();

        if (!(signState instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) signState;

        if (!sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[CopyBooks]]")) {
            return;
        }

        try {
            dispenser.getInventory().setItem(0, plugin.getStorageManager().retrieveBook(Long.parseLong(sign.getLine(1))).toItemStack(1));
            dispenser.dispense();
        } catch (InvalidBookException e) {
            CopyBooks.debug(null, e);

        }
    }
}
