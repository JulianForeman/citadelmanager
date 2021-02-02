package com.bluuish.citadelmanager.commands;

import com.bluuish.citadelmanager.CitadelManager;
import com.bluuish.citadelmanager.Statics;
import com.bluuish.citadelmanager.timers.citadelTimer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sun.tools.jar.Main;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class activateCitadel implements CommandExecutor {

    private CitadelManager main;
    private WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    private RegionContainer container = worldGuard.getRegionContainer();
    private Boolean hasThrone = false;
    private Boolean hasCitadel = false;
    public static BukkitRunnable timer = new citadelTimer();

    public activateCitadel(CitadelManager main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){return true;}

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("activatecitadel") && player.hasPermission("citadelmanager.activate")){
            if (Statics.citadelActivated){
                player.sendMessage("§4The citadel is already activated");
                return true;
            }
            Map<String, ProtectedRegion> regions = container.get(Bukkit.getWorld("world")).getRegions();
            for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()){
                if (entry.getValue().getFlag(CitadelManager.DarkCitadel) != null) {
                    hasCitadel = true;
                }
                else if(entry.getValue().getFlag(CitadelManager.ThroneRoom) != null){
                    hasThrone = true;
                }
            }
            if (!hasCitadel) {
                player.sendMessage("§4There is no region with the darkcitadel flag set!");
            }
            else if(!hasThrone) {
                player.sendMessage("§4There is no region with the throneroom flag set!");
            }
            else{
                Statics.citadelActivated = true;
                Statics.eventFinishDate = LocalDateTime.now();
                timer.runTaskTimer(CitadelManager.getPlugin(CitadelManager.class),0, 20);
                player.sendMessage("§9The Dark Citadel has been activated");
                main.getDataConfig().set("citadel-activated", Statics.citadelActivated);
                try {
                    main.getDataConfig().save(main.getDataFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return true;
    }
}
