package com.p000ison.dev.copybooks.objects;

/**
 * Represents a Transaction
 */
public class Transaction {

    private String requester;
    private String opponent;
    private long bookId;
    private double price;
    private int amount;

    public Transaction(String requester, String opponent, long bookId, double price, int amount)
    {
        this.requester = requester;
        this.opponent = opponent;
        this.bookId = bookId;
        this.price = price;
        this.amount = amount;
    }


    public String getRequester()
    {
        return requester;
    }

    public String getOpponent()
    {
        return opponent;
    }

    public long getBookId()
    {
        return bookId;
    }

    public double getPrice()
    {
        return price;
    }

    public int getAmount()
    {
        return amount;
    }
}
