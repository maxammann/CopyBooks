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

import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.api.WrittenBook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

    public static String removeColors(String text)
    {
        return text.replaceAll("[\u00a7][0-9a-f]", "");
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

    /**
     * Creates a book from a url
     *
     * @param url    The url to copy it from
     * @param title  The title of the book
     * @param author The author
     * @return A book with the content of the website
     * @throws IOException
     * @throws InvalidBookException
     */
    public static WrittenBook createBookFromURL(String url, String title, String author) throws IOException, InvalidBookException
    {
        return new CraftWrittenBook(title, author, getPagesFromURL(url));
    }

    /**
     * Formats a URL
     *
     * @param url
     * @return Returns the formatted one
     */
    public static String formatURL(String url)
    {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        return url;
    }

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

    /**
     * Reads pages from a website and adds them to a list
     *
     * @param site The url to read from
     * @return The created pages from this website
     * @throws IOException
     */
    public static List<String> getPagesFromURL(String site) throws IOException
    {
        URL url;
        InputStream is;
        BufferedReader reader;
        List<String> pages = new ArrayList<String>();
        char[] buffer = new char[256];
        url = new URL(site);

        is = url.openStream();
        reader = new BufferedReader(new InputStreamReader(is));

        int iterations = 0;

        while (reader.read(buffer) != -1) {
            iterations++;

            if (iterations >= 50) {
                break;
            }

            pages.add(new String(buffer));
        }

        if (is != null) {
            is.close();
        }
        return pages;
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
