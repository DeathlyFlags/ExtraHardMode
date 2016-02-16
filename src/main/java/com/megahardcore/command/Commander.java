/*
 * This file is part of
 * MegaHardCore Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * MegaHardCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaHardCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with MegaHardCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.megahardcore.command;


import com.megahardcore.MegaHardCore;
import com.megahardcore.service.CommandHandler;
import com.megahardcore.service.ICommand;
import com.megahardcore.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commander extends CommandHandler
{

    public Commander (MegaHardCore plugin)
    {
        super(plugin, "mhc");
        HelpCommand help = new HelpCommand();
        registerCommand("help", help);
        registerCommand("?", help);
        registerCommand("reload", new ReloadCommand());
        registerCommand("version", new VersionCommand());
        registerCommand("enabled", new EnabledCommand());
        registerCommand("debugtoggle", new DebugToggleCommand());
        registerCommand("debug", new DebugCommand());
    }


    @Override
    public boolean noArgs(CommandSender sender, Command command, String label)
    {
        sender.sendMessage(ChatColor.GRAY + "========= " + ChatColor.RED + plugin.getName() + ChatColor.GRAY + " =========");
        sender.sendMessage(" /mhc");
        sender.sendMessage("    help" + ChatColor.YELLOW + " - Show the help menu");
        sender.sendMessage("    version" + ChatColor.YELLOW + " - Show version info");
        if (sender.hasPermission(PermissionNode.ADMIN.getNode()))
        {
            sender.sendMessage("    reload " + ChatColor.YELLOW + "- Reload the plugin");
            sender.sendMessage("    enabled [world]" + ChatColor.YELLOW + "- Is megahardcore enabled");
        }
        return true;
    }


    @Override
    public boolean unknownCommand(CommandSender sender, Command command, String label, String[] args)
    {
        sender.sendMessage(ChatColor.YELLOW + plugin.getTag() + " Unknown command: " + ChatColor.WHITE + args[0]);
        return true;
    }


    private class HelpCommand implements ICommand
    {

        @Override
        public boolean execute (MegaHardCore plugin, CommandSender sender, Command command, String label, String[] args)
        {
            return noArgs(sender, command, label);
        }

    }
}
