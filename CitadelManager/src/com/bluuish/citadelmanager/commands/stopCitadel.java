package com.bluuish.citadelmanager.commands;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.events.enterThrone;
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

import java.util.Map;

public class stopCitadel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){return true;}
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("stopcitadel") && player.hasPermission("citadel.stop")){
            if (!Statics.citadelStarted || !Statics.citadelActivated){
                player.sendMessage("§4The dark citadel event is either not activated or not running.");
            }
            else{
                if (enterThrone.playersEntered == null || enterThrone.playersEntered.size() > 0) {
                    enterThrone.timer.cancel();
                }
                else if (!Statics.citadelStarted && Statics.firstEvent){
                    throneTimer.timer.cancel();
                    activateCitadel.timer.cancel();
                }
                else if (!Statics.citadelStarted){
                    throneTimer.timer.cancel();
                }
                Statics.citadelStarted = false;
                RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
                Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld("world")).getRegions();
                for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                    if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null || entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                        entry.getValue().setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
                        if (entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                            entry.getValue().setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
                        }
                    }
                }
                player.sendMessage("§4The dark citadel event has been stopped");
            }
        }
        return true;
    }
}
