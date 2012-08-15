package com.p000ison.dev.copybooks.objects;

/**
 * Represents a BookCommandHolder
 */
public class BookCommandHolder {

    private String msg;
    private long id;

    public BookCommandHolder(long id, String msg)
    {
        this.id = id;
        this.msg = msg;
    }

    public long getId()
    {
        return id;
    }

    public String getMessage()
    {
        return msg;
    }
}