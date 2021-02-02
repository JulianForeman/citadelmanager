package com.bluuish.citadelmanager.commands;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.timers.throneTimer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class forceCitadel implements CommandExecutor {

    Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);
    RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){return true;}

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("forcecitadel") && player.hasPermission("citadelmanager.force")){
            if (Statics.citadelStarted){
                player.sendMessage("§4The citadel event has already started");
            }
            else if (!Statics.citadelActivated){
                player.sendMessage("§4The citadel has not been activated");
            }
            else{
                player.sendMessage("§9Dark Citadel Event Started");
                Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld(player.getWorld().getName())).getRegions();
                for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                    if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null || entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                        entry.getValue().setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
                        if (entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                            entry.getValue().setFlag(DefaultFlag.ENTRY, StateFlag.State.ALLOW);
                        }
                    }
                }
                if (Statics.previousKing == null){
                    Statics.citadelStarted = true;
                    return true;
                }
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + Statics.previousKing + " parent remove " + plugin.getConfig().getString("citadel-winner-group"));
                Statics.citadelStarted = true;
            }
        }
        return true;
    }
}
