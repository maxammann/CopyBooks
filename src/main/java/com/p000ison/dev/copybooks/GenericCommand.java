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

import org.bukkit.command.CommandSender;

/**
 *
 * @author Max
 */
public abstract class GenericCommand implements Command
{

    private String name;
    private String[] permissions;
    private String[] identifiers;
    private String[] usages;
    private int maxArgs = 0, minArgs = 0;

    @Override
    public abstract void execute(CommandSender sender, String label, String[] args);

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String[] getPermissions()
    {
        return permissions;
    }

    @Override
    public void setPermissions(String... perm)
    {
        this.permissions = perm;
    }

    @Override
    public String[] getUsages()
    {
        return usages;
    }

    @Override
    public void setIdentifiers(String... identifiers)
    {
        this.identifiers = identifiers;
    }

    @Override
    public boolean isIdentifier(String cmd)
    {
        for (String c : identifiers) {
            if (c.equalsIgnoreCase(cmd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setUsages(String... text)
    {
        this.usages = text;
    }

    @Override
    public int getMaxArguments()
    {
        return maxArgs;
    }

    @Override
    public int getMinArguments()
    {
        return minArgs;
    }

    @Override
    public void setArgumentRange(int min, int max)
    {
        this.minArgs = min;
        this.maxArgs = max;
    }
}
