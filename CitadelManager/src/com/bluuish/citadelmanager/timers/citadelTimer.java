package com.bluuish.citadelmanager.timers;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class citadelTimer extends BukkitRunnable {

    private Player player;
    private CitadelManager main = CitadelManager.getPlugin(CitadelManager.class);

    private Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);
    private WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    private RegionContainer container = worldGuard.getRegionContainer();
    public static LocalDateTime timeNow;

    @Override
    public void run() {
        timeNow = LocalDateTime.now();
        if (Statics.previousKing != null){
            try{
                player = Bukkit.getPlayer(Statics.previousKing);
            }
            catch(NullPointerException e){
                player = Bukkit.getOfflinePlayer(main.getDataConfig().getString("previous-king")).getPlayer();
            }
        }
        long hours = ChronoUnit.HOURS.between(Statics.eventFinishDate, timeNow);


        if (hours >= plugin.getConfig().getInt("citadel-cooldown-hours")){
            try{
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + Statics.previousKing + " parent remove " + plugin.getConfig().getString("citadel-winner-group"));
            }
            catch(NullPointerException e){

            }
            Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld("world")).getRegions();
            for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null || entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                    entry.getValue().setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
                    if (entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                        entry.getValue().setFlag(DefaultFlag.ENTRY, StateFlag.State.ALLOW);
                    }
                }
            }
            if (Statics.firstEvent){
                Statics.firstEvent = false;
            }
            Statics.citadelStarted = true;
            this.cancel();
        }
        main.getDataConfig().set("previous-event", Statics.eventFinishDate.toString());
        try {
            main.getDataConfig().save(main.getDataFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
