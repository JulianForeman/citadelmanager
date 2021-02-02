package com.bluuish.citadelmanager.events;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.timers.throneTimer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.Map;

public class playerDisconnects implements Listener {

    private Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (enterThrone.playersEntered.contains(player)){
            enterThrone.playersEntered.remove(player);
            if (enterThrone.timerStarted){
                enterThrone.timer.cancel();
            }
            Location tpLocation = new Location(player.getWorld(), plugin.getConfig().getInt("citadel-tp-x"), plugin.getConfig().getInt("citadel-tp-y"), plugin.getConfig().getInt("citadel-tp-z"));
            player.teleport(tpLocation);
            if (player.getName().equals(Statics.previousKing)){
                CitadelManager.kingOnline = false;
            }
        }
    }
}
