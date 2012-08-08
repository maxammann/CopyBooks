package com.p000ison.dev.copybooks;

import com.p000ison.dev.copybooks.api.CraftWrittenBook;

import java.util.ArrayList;

/**
 * Represents a BookFormater
 */
public class BookFormater {

    private ArrayList<String> pages;

    public BookFormater(CraftWrittenBook book)
    {
        this.pages = book.getPages();

    }


    public static void main(String[] args)    {
        centerPage(3, "asfdasfddsf\nadfsdfdsf\nasfdasfd\n");
    }
    public static void centerPage(int page, String test)
    {
     //   String realPage = pages.get(page);

        StringBuilder sb = new StringBuilder();

        String[] lines = test.split("\n");

        for (String line : lines) {
            if (line.length() - 18 <= 0) {
                sb.append(getBlanks((18 - line.length()) / 2).append(line).append(18 - line.length() / 2)).append('\n');
            }

        }

        System.out.println(sb.toString());
    }

    public static StringBuilder getBlanks(int amount)
    {

        StringBuilder blanks = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            blanks.append(' ');

        }
        return blanks;
    }

}
