/*
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 */

package com.p000ison.dev.copybooks;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

/**
 * Represents a InvView
 */
public class InvView  extends InventoryView{
    @Override
    public Inventory getTopInventory()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Inventory getBottomInventory()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HumanEntity getPlayer()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InventoryType getType()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
