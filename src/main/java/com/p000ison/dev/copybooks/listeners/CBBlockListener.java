package com.p000ison.dev.copybooks.listeners;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

/**
 * Represents a CBBlockListener
 */
public class CBBlockListener implements Listener {
    private CopyBooks plugin;

    public CBBlockListener(CopyBooks plugin)
    {
        this.plugin = plugin;
    }

    public void onBlockDispense(BlockDispenseEvent event)
    {
        Block block = event.getBlock();
        BlockState state = block.getState();

        if (!(state instanceof Dispenser)) {
            return;
        }

        Dispenser dispenser = (Dispenser) state;

        org.bukkit.material.Dispenser matDispenser = (org.bukkit.material.Dispenser) block.getState().getData();

        Block signBlock = block.getRelative(matDispenser.getFacing().getOppositeFace());

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
