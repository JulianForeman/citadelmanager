package com.bluuish.citadelmanager.timers;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class throneTimer extends BukkitRunnable {

    private Player player;
    private final Plugin plugin = CitadelManager.getPlugin(CitadelManager.class);
    private final WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    private final RegionContainer container = worldGuard.getRegionContainer();
    public static BukkitRunnable timer;
    private Boolean sentMessage = false;
    int countdown = plugin.getConfig().getInt("citadel-capture-time");
    double waitTime = plugin.getConfig().getInt("citadel-capture-time") * 0.01;
    public static BossBar claimThrone;
    int previousCountdown;



    public BukkitRunnable throneTimerTask(Player nextKing){
        player = nextKing;
        previousCountdown = countdown;
        claimThrone = Bukkit.createBossBar(player.getDisplayName() + " §bWill claim the throne in " + countdown, BarColor.BLUE, BarStyle.SOLID);
        return this;
    }

    @Override
    public void run() {
        timer = new citadelTimer();
        countdown--;
        if (countdown > 0){
            claimThrone.setTitle(player.getDisplayName() + " §bClaims the throne in " + countdown);
            claimThrone.addPlayer(player);
            if (previousCountdown - waitTime == countdown){
                previousCountdown = countdown;
                claimThrone.setProgress(claimThrone.getProgress() - 0.01);
            }
        }
        else{
            if (!sentMessage){
                sentMessage = true;
                claimThrone.setVisible(false);
                claimThrone.removePlayer(player);
                Statics.previousKing = player.getName();
                Bukkit.broadcastMessage(player.getDisplayName() + " §bhas claimed the throne!");
                try{
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().set("previous-king", Bukkit.getPlayer(Statics.previousKing).getUniqueId().toString());
                }catch (NullPointerException e){
                }
                try{
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().set("previous-king", Bukkit.getOfflinePlayer(Statics.previousKing).getUniqueId().toString());
                }
                catch(NullPointerException e){

                }
                try {
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().save(CitadelManager.getPlugin(CitadelManager.class).getDataFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Location tpLocation = new Location(player.getWorld(), plugin.getConfig().getInt("citadel-tp-x"), plugin.getConfig().getInt("citadel-tp-y"), plugin.getConfig().getInt("citadel-tp-z"));
                player.teleport(tpLocation);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + player.getName() + " parent add " + plugin.getConfig().getString("citadel-winner-group"));
                Statics.citadelStarted = false;
                Statics.eventFinishDate = LocalDateTime.now();
                try {
                    if (!timer.isCancelled()) {
                        timer.cancel();
                    }
                } catch(IllegalStateException e){

                }
                timer.runTaskTimer(CitadelManager.getPlugin(CitadelManager.class),0, 6000);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "lp user " + player.getName() + " parent add " + plugin.getConfig().getString("citadel-winner-group"));
                Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld("world")).getRegions();
                for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                    if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null || entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                        entry.getValue().setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
                        if (entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                            entry.getValue().setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
                        }
                    }
                }
            }
            else{
                Statics.previousKing = player.getName();
                try{
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().set("previous-king", Bukkit.getPlayer(Statics.previousKing).getUniqueId().toString());
                }catch (NullPointerException e){
                }
                try{
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().set("previous-king", Bukkit.getOfflinePlayer(Statics.previousKing).getUniqueId().toString());
                }
                catch(NullPointerException e){

                }
                try {
                    CitadelManager.getPlugin(CitadelManager.class).getDataConfig().save(CitadelManager.getPlugin(CitadelManager.class).getDataFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                claimThrone.setVisible(false);
                claimThrone.removePlayer(player);
                Statics.citadelStarted = false;
                Statics.eventFinishDate = LocalDateTime.now();
                timer.runTaskTimer(CitadelManager.getPlugin(CitadelManager.class),0, 6000);
                Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld("world")).getRegions();
                for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                    if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null || entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                        entry.getValue().setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
                        if (entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                            entry.getValue().setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
                        }
                    }
                }
            }
            this.cancel();
        }

    }
}
