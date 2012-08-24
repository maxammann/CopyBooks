package com.p000ison.dev.copybooks.api;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a CraftWrittenBook
 */
public class CraftWrittenBook implements WrittenBook {
    private NBTTagCompound tag;

    public CraftWrittenBook()
    {
        tag = new NBTTagCompound();
    }

    public CraftWrittenBook(String title, String author, List<String> pages) throws InvalidBookException
    {
        tag = new NBTTagCompound();

        setTitle(title);
        setAuthor(author);
        setPages(pages);
    }

    public CraftWrittenBook(ItemStack itemStack) throws InvalidBookException
    {
        this((CraftItemStack) itemStack);
    }

    public CraftWrittenBook(CraftItemStack itemStack) throws InvalidBookException
    {

        if (itemStack.getTypeId() != 387) {
            throw new InvalidBookException("The book must be a written book!");
        }

        tag = itemStack.getHandle().tag;

        if (tag == null) {
            tag = new NBTTagCompound();
        }
    }

    @Override
    public String getTitle()
    {
        return tag.getString("title");
    }

    @Override
    public String getAuthor()
    {
        return tag.getString("author");
    }

    @Override
    public ArrayList<String> getPages()
    {
        ArrayList<String> out = new ArrayList<String>();

        NBTTagList pages = tag.getList("pages");

        for (int i = 0; i < pages.size(); i++) {
            out.add(((NBTTagString) pages.get(i)).data);
        }

        return out;
    }

    @Override
    public void setTitle(String title)
    {
        tag.setString("title", title);
    }

    @Override
    public void setAuthor(String author)
    {
        tag.setString("author", author);
    }

    @Override
    public void setPages(List<String> pages) throws InvalidBookException
    {
        NBTTagList list = new NBTTagList();
        for (String page : pages) {

            if (page.length() > 256) {
                throw new InvalidBookException("The lenght of a page is too long!");
            }

            NBTTagString nbtPage = new NBTTagString(page);
            nbtPage.data = page;

            list.add(nbtPage);
        }
        tag.set("pages", list);

    }

    @Override
    public boolean unsign()
    {
        if (!tag.hasKey("author") || !tag.hasKey("title")) {
            return false;
        }

        tag.remove("author");
        tag.remove("title");

        return true;
    }

    public ItemStack toItemStack(int amount) throws InvalidBookException
    {
        CraftItemStack item = new CraftItemStack(Material.WRITTEN_BOOK, amount);

        item.getHandle().tag = tag;

        return item;
    }
}
