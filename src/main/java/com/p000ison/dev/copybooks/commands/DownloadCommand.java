package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.*;
import com.p000ison.dev.copybooks.api.InvalidBookException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * Represents a AcceptCommand
 */
public class DownloadCommand extends GenericCommand {

    public DownloadCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        setArgumentRange(2, 3);
        setIdentifiers("download");
        setUsages("/cb download <url> <title> [author]- Downloads a url from this page.");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            String author = null;

            if (args.length == 3) {
                author = args[2];
            }

            Book book = null;


            try {
                book = Helper.createBookFromURL(player.getName(), Helper.formatURL(args[0]), args[1], author);
            } catch (IOException e) {
                player.sendMessage("Invalid URL!");
                return;
            } catch (InvalidBookException e) {
                player.sendMessage("Failed to create book!");
                return;
            }

            try {
                player.getInventory().addItem(book.toItemStack(1));
            } catch (InvalidBookException e) {
                player.sendMessage("Failed to create book!");
                return;
            }

            player.sendMessage("Book downloaded!");
        } else {

        }
    }
}
