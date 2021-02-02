package com.bluuish.citadelmanager.commands;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.timers.citadelTimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.temporal.ChronoUnit;

public class checkCitadel implements CommandExecutor {

    private Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("checkcitadel") && player.hasPermission("citadelmanager.check")) {
            if (Statics.citadelActivated){
                Integer hoursPassed = (int)ChronoUnit.HOURS.between(Statics.eventFinishDate, citadelTimer.timeNow);
                int hours = plugin.getConfig().getInt("citadel-cooldown-hours") - hoursPassed;
                if (Statics.citadelStarted){
                    player.sendMessage("§bThe Dark Citadel has started");
                }
                else if (hours > 1){
                    player.sendMessage("§bThe next Dark Citadel Event is in " + hours + " hours");
                }
                else{
                    player.sendMessage("§bThe next Dark Citadel Event is in less than an hour");
                }
            }
            else{
                player.sendMessage("§4The Dark Citadel is not active");
            }
        }
        return true;
    }
}
