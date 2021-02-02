package com.bluuish.citadelmanager.commands;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.events.enterThrone;
import com.bluuish.citadelmanager.timers.throneTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class disableCitadel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("disablecitadel") && player.hasPermission("citadelmanager.disable")) {
            if (Statics.citadelActivated){
                player.sendMessage("§4Citadel Manager has been disabled");
                Bukkit.getPluginManager().disablePlugin(CitadelManager.getPlugin(CitadelManager.class));
            }
            else{
                player.sendMessage("§4The citadel has not been enabled yet");
            }
        }
        return true;
    }

}
