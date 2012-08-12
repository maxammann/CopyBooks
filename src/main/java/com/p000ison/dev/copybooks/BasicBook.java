package com.p000ison.dev.copybooks;

/**
 * Represents a BasicBook
 */
public class BasicBook {
    private long id;
    private String title;
    private String author;
    private String creator;

    public BasicBook(long id, String title, String author, String creator)
    {
        this.title = title;
        this.author = author;
        this.id = id;
        this.creator = creator;
    }

    public long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getCreator()
    {
        return creator;
    }
}
