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
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Max
 */
public class CBPlayerListener implements Listener
{

    private CopyBooks plugin;

    public CBPlayerListener(CopyBooks plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Action action = event.getAction();

        
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            
            
            //Test code :P crapy but works to get and set title/pages/autor
            System.out.println(((CraftItemStack)player.getItemInHand()).getHandle().tag.getString("title"));
            ((CraftItemStack)player.getItemInHand()).getHandle().tag.setString("title", "FUCKYOU");
            System.out.println(((CraftItemStack)player.getItemInHand()).getHandle().tag.getString("title"));
            
            System.out.println(((CraftItemStack)player.getItemInHand()).getHandle().tag.getList("pages").toString());
            NBTTagList d = new NBTTagList("pages");
            d.add(new NBTTagString("asdf", "dfxcv"));
            d.add(new NBTTagString("ddd", "kdfnk"));
            
            ((CraftItemStack)player.getItemInHand()).getHandle().tag.set("pages", d);
            System.out.println(((CraftItemStack)player.getItemInHand()).getHandle().tag.getList("pages"));
            
            if (block == null) {
                return;
            }

            if (block.getState() instanceof Sign) {
                String[] lines = ((Sign) block.getState()).getLines();

                if (!lines[0].equalsIgnoreCase("[CopyBooks]") || lines[1].equals("") || lines[2].equals("")) {
                    return;
                }

                int id = 0;

                try {
                    id = Integer.parseInt(lines[2]);
                } catch (NumberFormatException ex) {
                    player.sendMessage("Invalid format!");
                    return;
                }


                //give book by id
            }
        }
    }
}
