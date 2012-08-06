package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.GenericCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a AcceptCommand
 */
public class SellCommand extends GenericCommand {

    public SellCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(3, 4);
        setIdentifiers("sell", "offer");
        setUsages("/cb sell - Sell a book");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = ((Player) sender);

            int amount = 1;


            if (args.length == 4) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("The amount must be a number!");
                    return;
                }
            }
            Player opponent = plugin.getServer().getPlayer(args[0]);

            if (opponent == null) {
                player.sendMessage("Player not found!");
                return;
            }

            long id;

            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("The id must be a number!");
                return;
            }

            double price;

            try {
                price = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage("The price must be a number!");
                return;
            }

            player.sendMessage("Opened a transaction with " + opponent.getName());
            plugin.getEconomyManager().addTransaction(player.getName(), opponent.getName(), id, price, amount);
        }
    }
}
