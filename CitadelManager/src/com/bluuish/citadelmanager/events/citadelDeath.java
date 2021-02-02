package com.bluuish.citadelmanager.events;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.timers.throneTimer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;

public class citadelDeath implements Listener {

    private Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);
    private RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int deathMoney = plugin.getConfig().getInt("citadel-death-money");
        Player player = event.getEntity();
        RegionQuery query = container.createQuery();
        double playerBalance = CitadelManager.econ.getBalance(player);

        for(Iterator<ProtectedRegion> it = query.getApplicableRegions(player.getLocation()).iterator(); it.hasNext();) {
            ProtectedRegion p = it.next();
            if (p.getFlag(CitadelManager.DarkCitadel) != null|| p.getFlag(CitadelManager.ThroneRoom) != null){
                if (playerBalance > deathMoney){
                    enterThrone.playersEntered.remove(player);
                    if (throneTimer.claimThrone instanceof BossBar) {
                        if (throneTimer.claimThrone.isVisible()){
                            if (!throneTimer.claimThrone.getPlayers().isEmpty()) {
                                throneTimer.claimThrone.removePlayer(player);
                            }
                            throneTimer.claimThrone.setVisible(false);
                        }
                    }
                    CitadelManager.econ.withdrawPlayer(player, deathMoney);
                    player.sendMessage("§4You died in the Citadel and lost $" + deathMoney);
                }
                else{
                    enterThrone.playersEntered.remove(player);
                    CitadelManager.econ.withdrawPlayer(player, playerBalance);
                    if (throneTimer.claimThrone instanceof BossBar) {
                        if (throneTimer.claimThrone.isVisible()){
                            if (!throneTimer.claimThrone.getPlayers().isEmpty()) {
                                throneTimer.claimThrone.removePlayer(player);
                            }
                            throneTimer.claimThrone.setVisible(false);
                        }
                    }
                    player.sendMessage("§4You died in the Citadel and lost $" + playerBalance);
                }
                return;
            }
        }
    }
}

