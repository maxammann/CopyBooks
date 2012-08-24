/*******************************************************************************
 * Copyright (C) 2012 p000ison
 *
 * This work is licensed under the Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of
 * this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send
 * a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco,
 * California, 94105, USA.
 ******************************************************************************/

package com.p000ison.dev.copybooks.commands;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.objects.Command;
import com.p000ison.dev.copybooks.objects.GenericCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max
 */
public class HelpCommand extends GenericCommand {

    private static final int CMDS_PER_PAGE = 12;
    private CopyBooks plugin;

    public HelpCommand(CopyBooks plugin, String name)
    {
        super(plugin, name);
        this.plugin = plugin;
        setUsages("/cb help §8[page#]");
        setArgumentRange(0, 1);
        setIdentifiers("cb", "copybooks", "help");
        setPermissions("cb.commands.help");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args)
    {
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + plugin.getTranslation("failed.parsing.number"));
            }
        }


        List<Command> sortCommands = plugin.getCommandManager().getCommands();
        List<Command> commands = new ArrayList<Command>();


        for (Command cmd : sortCommands) {
            String[] perms = cmd.getPermissions();

            if (perms == null) {
                commands.add(cmd);
                continue;
            }

            for (String perm : perms) {
                if (sender.hasPermission(perm)) {
                    commands.add(cmd);
                    break;
                }
            }
        }
        int numPages = commands.size() / CMDS_PER_PAGE;

        if (commands.size() % CMDS_PER_PAGE != 0) {
            numPages++;
        }

        if (page >= numPages || page < 0) {
            page = 0;
        }

        int start = page * CMDS_PER_PAGE;
        int end = start + CMDS_PER_PAGE;

        if (end > commands.size()) {
            end = commands.size();
        }

        StringBuilder menu = new StringBuilder();
        for (int c = start; c < end; c++) {
            Command cmd = commands.get(c);


            for (String usage : cmd.getUsages()) {
                menu.append("   §b").append(usage).append('\n');
            }
        }

        sender.sendMessage("§7CopyBooks  " + " §8<" + (page + 1) + "/" + numPages + ">");
        sender.sendMessage(menu.toString());
    }
}


