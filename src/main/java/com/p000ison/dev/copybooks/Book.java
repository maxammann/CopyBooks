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

/**
 *
 * @author Max
 */
public class Book
{

    private String title;
    private String author;
    private ArrayList<String> pages;

    public Book(String title, String author, ArrayList<String> pages)
    {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public ArrayList<String> getPages()
    {
        return pages;
    }
}
