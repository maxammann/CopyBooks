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
package com.p000ison.dev.copybooks;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Max
 */
public class Helper {

    public static String fromListToJSONString(String key, List<String> list)
    {

        JSONObject json = new JSONObject();

        JSONArray array = new JSONArray();
        array.addAll(list);

        json.put(key, array);

        return json.toString();
    }

    public static int getAmontFromSign(char[] chars)
    {
        StringBuilder amountString = new StringBuilder();

        for (int i = chars.length - 1; i > 0; i--) {
            if (chars[i] == ':') {
                break;
            }

            amountString.append(chars[i]);
        }

        int amount = -1;

        try {
            amount = Integer.parseInt(amountString.toString());
        } catch (NumberFormatException e) {
            CopyBooks.debug(Level.WARNING, ChatColor.DARK_RED + "Failed at parsing sign!");
        }

        return amount;
    }

    public static long getIdFromSign(char[] chars)
    {
        StringBuilder idString = new StringBuilder();

        for (char character : chars) {
            if (character == '[' || character == ':') {
                break;
            }

            idString.append(character);

        }
        long id = -1;

        try {
            id = Long.parseLong(idString.toString());
        } catch (NumberFormatException e) {
            CopyBooks.debug(Level.WARNING, ChatColor.DARK_RED + "Failed at parsing sign!");
        }
        return id;
    }

    public static ArrayList<String> fromJSONStringtoList(String key, String string)
    {

        if (string != null && !string.isEmpty()) {
            ArrayList<String> out = new ArrayList<String>();

            JSONObject flags = (JSONObject) JSONValue.parse(string);

            if (flags != null) {
                for (Object keys : flags.keySet()) {
                    try {

                        if (keys.equals(key)) {
                            JSONArray list = (JSONArray) flags.get(keys);

                            if (list != null) {
                                for (Object k : list) {
                                    out.add(k.toString());
                                }
                            }
                        }

                    } catch (Exception ex) {
                        CopyBooks.debug(String.format("Failed reading flag: %s", keys));
                        CopyBooks.debug(String.format("Value: %s", flags.get(key)));
                        CopyBooks.debug(null, ex);
                    }
                }
            }
            return out;
        }
        return null;
    }
}
