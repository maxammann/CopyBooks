package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.Command;
import com.p000ison.dev.copybooks.CopyBooks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author phaed
 */
public final class CommandManager
{

    private CopyBooks plugin;
    private LinkedHashMap<String, Command> commands;

    public CommandManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        commands = new LinkedHashMap<String, Command>();
    }

    public void addCommand(Command command)
    {
        commands.put(command.getName().toLowerCase(), command);
    }

    public void removeCommand(String command)
    {
        commands.remove(command);
    }

    public Command getCommand(String name)
    {
        return commands.get(name.toLowerCase());
    }

    public List<Command> getCommands()
    {
        return new ArrayList<Command>(commands.values());
    }

    public boolean executeAll(final CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
    {
        String[] arguments;

        //Build the args; if the args length is 0 then build if from the base command
        if (args.length == 0) {
            arguments = new String[]{command.getName()};
        } else {
            arguments = args;
        }

        //Iterate through all arguments from the last to the first argument
        for (int argsIncluded = arguments.length; argsIncluded >= 0; argsIncluded--) {
            String identifier = "";
            //Build the identifier string
            for (int i = 0; i < argsIncluded; i++) {
                identifier += " " + arguments[i];
            }

            //trim the last ' '
            identifier = identifier.trim();
            for (Command cmd : commands.values()) {
                if (cmd.isIdentifier(identifier)) {
                    String[] realArgs = Arrays.copyOfRange(arguments, argsIncluded, arguments.length);

                    if (realArgs.length < cmd.getMinArguments() || realArgs.length > cmd.getMaxArguments()) {
                        displayCommandHelp(cmd, sender);
                        return true;
                    } else if (realArgs.length > 0 && realArgs[0].equals("?")) {
                        displayCommandHelp(cmd, sender);
                        return true;
                    }

                    cmd.execute(sender, label, realArgs);
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.DARK_RED + "Command not found!");

        return true;
    }

    private void displayCommandHelp(Command cmd, CommandSender sender)
    {
        sender.sendMessage("§cCommand:§e " + cmd.getName());
        String[] usages = cmd.getUsages();
        StringBuilder sb = new StringBuilder("§cUsage:§e ").append(usages[0]).append("\n");

        for (int i = 1; i < usages.length; i++) {
            sb.append("           ").append(usages[i]).append("\n");
        }
        sender.sendMessage(sb.toString());

    }
}
