package com.p000ison.dev.copybooks.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a InventoryHelper
 */
public class InventoryHelper {


    /**
     * Removes a item from a inventory
     *
     * @param inventory The inventory to remove from.
     * @param mat       The material to remove .
     * @param amount    The amount to remove.
     * @param damage    The data value or -1 if this does not matter.
     * @return If the inventory has not enough items, this will return the amount of items which were not removed.
     */
    public static int remove(Inventory inventory, Material mat, int amount, short damage)
    {
        ItemStack[] contents = inventory.getContents();
        int removed = 0;
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || !item.getType().equals(mat)) {
                continue;
            }

            if (damage != (short) -1 && item.getDurability() != damage) {
                continue;
            }

            int remove = item.getAmount() - amount - removed;

            if (removed > 0) {
                removed = 0;
            }

            if (remove <= 0) {
                removed += Math.abs(remove);
                contents[i] = null;
            } else {
                item.setAmount(remove);
            }
        }
        return removed;
    }

    /**
     * Returns the available items of the item types that can be added
     *
     * @param inv        The inventory to check
     * @param mat        The material to check
     * @param durability the durability of the item to check or -1, if it does not matter
     * @return The available items of the item types that can be added
     */
    public static int getAvailableSlots(Inventory inv, Material mat, short durability, int maxStack)
    {
        int available = 0;

        for (ItemStack item : inv.getContents()) {

            if (item != null) {
                if (!item.getType().equals(mat)) {
                    continue;
                }

                if (durability != -1 && item.getDurability() == durability) {
                    continue;
                }

                available += maxStack - item.getAmount();
            }

            available += maxStack;
        }

        return available;
    }

//    public boolean add(Material mat, int amount, short durability)
//    {
//
//    }

    public static boolean add(Inventory inventory, ItemStack itemStack)
    {

        if (getAvailableSlots(inventory, itemStack.getType(), itemStack.getDurability(), itemStack.getMaxStackSize()) < itemStack.getAmount()) {
            return false;
        }

        int max = itemStack.getMaxStackSize();
        int missing = itemStack.getAmount();

        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item != null && (item.getTypeId() != itemStack.getTypeId() || item.getAmount() >= max)) {
                continue;
            }

            if (missing == 0) {
                return true;
            }

            int currentAmount = item == null ? 0 : item.getAmount();

            int add;
            int test = missing + currentAmount;

            if (test > max) {
                add = max;
                missing -= max - currentAmount;

            } else {
                add = test;
                // we finished our tour: missing = 0
                missing = 0;
            }

            ItemStack currentItemStack = itemStack.clone();
            currentItemStack.setAmount(add);
            inventory.setItem(i, currentItemStack);

        }
        return true;
    }

//    public static void main(String[] args)
//    {
//
//        ItemStack itemStack = new ItemStack(1, 50);   //50
//
//        ItemStack[] contents = {new ItemStack(1, 20), new ItemStack(2, 20), new ItemStack(1, 64), new ItemStack(1, 20)};
//        long s = System.currentTimeMillis();
//        add(contents, itemStack);
//
//
//        long e = System.currentTimeMillis();
//
//        System.out.println(e-s);
//
//        for (ItemStack i : contents) {
//            System.out.println(i.getType() + " - " + i.getAmount());
//        }
//    }


    /**
     * Checks weather the inventory contains a item or not.
     *
     * @param inventory The inventory to check..
     * @param mat       The material to check .
     * @param amount    The amount to check.
     * @param damage    The data value or -1 if this does not matter.
     * @return The amount of items the player has not. If this return 0 then the check was successfull.
     */
    public static int contains(Inventory inventory, Material mat, int amount, short damage)
    {
        ItemStack[] contents = inventory.getContents();
        int searchAmount = 0;
        for (ItemStack item : contents) {

            if (item == null || !item.getType().equals(mat)) {
                continue;
            }

            if (damage != -1 && item.getDurability() == damage) {
                continue;
            }

            searchAmount += item.getAmount();
        }
        return searchAmount - amount;
    }
}
