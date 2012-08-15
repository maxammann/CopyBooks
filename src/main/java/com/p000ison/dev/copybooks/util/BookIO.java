package com.p000ison.dev.copybooks.util;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.api.CraftWrittenBook;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import com.p000ison.dev.copybooks.api.WrittenBook;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a BookIO
 */
public final class BookIO {


    public static void writeBook(List<String> book, File file) throws IOException
    {
        Writer writer = new FileWriter(file);

        for (String page : book) {
            writer.write(page + "\n");
        }
        writer.flush();
        writer.close();
    }

    public static boolean writeNBTBook(WrittenBook book, File file) throws IOException
    {
        NBTOutputStream outputStream = null;
        try {
            outputStream = new NBTOutputStream(new FileOutputStream(file));

            List<Tag> contents = new ArrayList<Tag>();

            contents.add(new StringTag("title", book.getTitle()));
            contents.add(new StringTag("author", book.getAuthor()));

            List<StringTag> tagPages = new ArrayList<StringTag>();

            for (String page : book.getPages()) {
                tagPages.add(new StringTag("page", page));
            }

            ListTag<StringTag> pages = new ListTag<StringTag>("pages", StringTag.class, tagPages);

            contents.add(pages);

            CompoundTag root = new CompoundTag("Book", contents);

            outputStream.writeTag(root);

            outputStream.close();

            return true;
        } catch (IOException e) {
            CopyBooks.debug("Failed at writing book: " + book.getTitle(), e);
            return false;
        }
    }

    public static WrittenBook readNBTBook(File file) throws IOException
    {
        NBTInputStream inputStream = null;
        try {
            inputStream = new NBTInputStream(new FileInputStream(file));

            CraftWrittenBook book = new CraftWrittenBook();


            CompoundTag root = (CompoundTag) inputStream.readTag();

            if (root == null) {
                return null;
            }

            List<Tag> contents = root.getValue();

            if (contents == null) {
                return null;
            }

            StringTag title = (StringTag) contents.get(0);
            StringTag author = (StringTag) contents.get(1);
            ListTag<StringTag> pages = (ListTag<StringTag>) contents.get(2);

            List<String> convertedPages = new ArrayList<String>();

            for (StringTag page : pages.getValue()) {
                convertedPages.add(page.getValue());
            }


            book.setTitle(title.getValue());
            book.setAuthor(author.getValue());
            book.setPages(convertedPages);

            inputStream.close();

            return book;
        } catch (IOException e) {
            CopyBooks.debug("Failed at reading book", e);
            return null;
        } catch (Exception e) {
            CopyBooks.debug("Failed at reading book", e);
            return null;
        }
    }

    public static List<String> readBook(File file) throws IOException, InvalidBookException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> list = new ArrayList<String>();

        String page;

        while ((page = reader.readLine()) != null) {
            list.add(page);
        }


        reader.close();
        return list;
    }
}
