package com.bluuish.citadelmanager.events;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.timers.throneTimer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

public class enterThrone implements Listener {

    private Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);
    private RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
    public static ArrayList<Player> playersEntered = new ArrayList<>();
    public static Boolean timerStarted = false;
    public static BukkitRunnable timer;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        RegionQuery query = container.createQuery();
        if (playersEntered.size() == 1 && !timerStarted && Statics.citadelStarted){
            timer = new throneTimer().throneTimerTask(playersEntered.get(0));
            timerStarted = true;;
            timer.runTaskTimer(plugin, 0, 20);
        }
        for (Iterator<ProtectedRegion> it = query.getApplicableRegions(player.getLocation()).iterator(); it.hasNext(); ) {
            ProtectedRegion p = it.next();
            if (p.getFlag(CitadelManager.ThroneRoom) != null && !playersEntered.contains(player)) {
                playersEntered.add(player);
                player.sendMessage("§eYou entered the throne room");
                if(playersEntered.size() > 1) {
                    throneTimer.claimThrone.removePlayer(player);
                    throneTimer.claimThrone.setVisible(false);
                    timer.cancel();
                }
                return;
            }
            else if(it.hasNext() && it.next().getFlag(CitadelManager.ThroneRoom) != null || it.hasNext() && it.next().getFlag(CitadelManager.DarkCitadel) != null || p.getFlag(CitadelManager.ThroneRoom) != null){
                return;
            }
            else if(p.getFlag(CitadelManager.ThroneRoom) == null && playersEntered.contains(player)) {
                player.sendMessage("§eYou left the throne room");
                if(Statics.citadelStarted){
                    throneTimer.claimThrone.removePlayer(player);
                    throneTimer.claimThrone.setVisible(false);
                }
                playersEntered.remove(player);
                if (timerStarted){
                    timer.cancel();
                }
                timerStarted = false;
            }
        }
        if ((WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).size() == 0 && playersEntered.contains(player))) {
            player.sendMessage("§eYou left the throne room");
            if (throneTimer.claimThrone instanceof BossBar) {
                if (throneTimer.claimThrone.isVisible()){
                    if (!throneTimer.claimThrone.getPlayers().isEmpty()) {
                        throneTimer.claimThrone.removePlayer(player);
                    }
                    throneTimer.claimThrone.setVisible(false);
                }
            }
            playersEntered.remove(player);
            if (timerStarted){
                timer.cancel();
            }
            timerStarted = false;
        }

    }


}
