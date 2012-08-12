package com.p000ison.dev.copybooks.managers;

import com.p000ison.dev.copybooks.CopyBooks;
import com.p000ison.dev.copybooks.objects.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * @author phaed
 */
public final class CommandManager {

    private CopyBooks plugin;
    private LinkedHashMap<String, Command> commands;
    private LinkedHashMap<String, CommandState> commandchecks;

    public CommandManager(CopyBooks plugin)
    {
        this.plugin = plugin;
        commands = new LinkedHashMap<String, Command>();
        //commandchecks = new LinkedHashMap<String, CommandState>();
        //plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new CommandChecker(), 20L, 20L);
    }

    public void addCommandCheck(String player, String command)
    {
        commandchecks.put(player, new CommandState(command));
    }

    public String getPendingCommand(String player)
    {
        return commandchecks.get(player).getCommand();
    }

    private class CommandState {
        private long date;
        private String command;

        public CommandState(String command)
        {
            date = System.currentTimeMillis();
            this.command = command;
        }

        public long getDate()
        {
            return date;
        }

        public String getCommand()
        {
            return command;
        }
    }

    private class CommandChecker implements Runnable {

        @Override
        public void run()
        {
            for (Map.Entry<String, CommandState> entry : commandchecks.entrySet()) {
                if (System.currentTimeMillis() - 10000 > entry.getValue().getDate()) {
                    commandchecks.remove(entry.getKey());
                }
            }
        }
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
