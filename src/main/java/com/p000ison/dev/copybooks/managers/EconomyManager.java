package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.objects.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Represents a EconomyManager
 */
public class EconomyManager {

    private CopyBooks plugin;
    private static Economy economy = null;
    private Map<String, Transaction> transactions;

    public EconomyManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        transactions = new HashMap<String, Transaction>();
        if (!setupEconomy()) {
            CopyBooks.debug(Level.WARNING, "No Economy plugin found!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public boolean checkTransaction(Transaction transaction)
    {
        return economy.getBalance(transaction.getOpponent()) >= transaction.getPrice() * transaction.getAmount();
    }

    public boolean executeTransaction(Transaction transaction)
    {
        return chargeMonay(transaction.getOpponent(), transaction.getPrice() * transaction.getAmount()) &&
                grantMonay(transaction.getRequester(), transaction.getPrice() * transaction.getAmount());

    }

    public void addTransaction(String requester, String opponent, long bookId, double price, int amount)
    {
        transactions.put(opponent, new Transaction(requester, opponent, bookId, price, amount));
    }

    public Transaction getTransactionByRequester(String requester)
    {
        for (Transaction transaction : transactions.values()) {
            if (transaction.getRequester().equals(requester)) {
                return transaction;
            }
        }
        return null;
    }

    public Transaction getTransactionByOpponent(String opponent)
    {
        return transactions.get(opponent);
    }

    public void cancelTransactionByRequester(String requester)
    {
        for (Map.Entry<String, Transaction> entry : transactions.entrySet()) {
            if (entry.getValue().getRequester().equals(requester)) {
                transactions.remove(entry.getValue());
            }
        }
    }

    public void cancelTransactionByOpponent(String opponent)
    {
        if (transactions.containsKey(opponent)) {
            transactions.remove(opponent);
        }
    }


    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public boolean chargeMoney(Player player, double amount)
    {
        return chargeMonay(player.getName(), amount);
    }

    public boolean chargeMonay(String player, double amount)
    {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean grantMoney(Player player, double amount)
    {
        return grantMonay(player.getName(), amount);
    }

    public boolean grantMonay(String player, double amount)
    {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

}

