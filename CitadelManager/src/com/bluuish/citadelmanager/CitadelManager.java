package com.bluuish.citadelmanager;

import com.bluuish.citadelmanager.commands.*;
import com.bluuish.citadelmanager.events.citadelDeath;
import com.bluuish.citadelmanager.events.enterThrone;
import com.bluuish.citadelmanager.events.playerDisconnects;
import com.bluuish.citadelmanager.timers.citadelTimer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class CitadelManager extends JavaPlugin {

    public static Economy econ = null;
    public static StateFlag DarkCitadel;
    public static StateFlag ThroneRoom;
    public WorldGuardPlugin worldGuardPlugin = getWorldGuard();
    private File dataFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
    public static boolean kingOnline = false;
    @Override
    public void onEnable(){
        getServer().getPluginManager().registerEvents(new citadelDeath(), this);
        getServer().getPluginManager().registerEvents(new enterThrone(), this);
        getServer().getPluginManager().registerEvents(new playerDisconnects(), this);
        getCommand("forcecitadel").setExecutor(new forceCitadel());
        getCommand("activatecitadel").setExecutor(new activateCitadel(this));
        getCommand("disablecitadel").setExecutor(new disableCitadel());
        getCommand("checkcitadel").setExecutor(new checkCitadel());
        getCommand("stopcitadel").setExecutor(new stopCitadel());
        setupEconomy();
        this.saveDefaultConfig();
        if (!dataFile.exists()){
            saveResource("data.yml", false);
            yaml.set("previous-king", "");
            try {
                yaml.save(dataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (yaml.getBoolean("citadel-activated")){
            BukkitRunnable timer = new citadelTimer();
            Statics.citadelActivated = true;
            timer.runTaskTimer(CitadelManager.getPlugin(CitadelManager.class),0, 20);
        }
        getServer().getConsoleSender().sendMessage("§a[CITADELMANAGER]: Enabled!");
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        FlagRegistry registry = worldGuardPlugin.getFlagRegistry();
        try {

            StateFlag flag = new StateFlag("DarkCitadel", true);
            StateFlag flag2 = new StateFlag("ThroneRoom", true);

            registry.register(flag);
            registry.register(flag2);

            DarkCitadel = flag;
            ThroneRoom = flag2;
        } catch (FlagConflictException e) {

            Flag<?> existing = registry.get("DarkCitadel");
            Flag<?> existing2 = registry.get("ThroneRoom");
            if (existing instanceof StateFlag && existing2 instanceof StateFlag) {
                DarkCitadel = (StateFlag) existing;
                ThroneRoom = (StateFlag) existing2;
            }

            else {
                getServer().getConsoleSender().sendMessage("§4§l[CITADELMANAGER]: Something went wrong with loading a custom flag!");
            }
        }
        if (!dataFile.exists()){
            saveResource("data.yml", false);
            yaml.set("previous-king", "");
        }
        if (!yaml.getString("previous-king").equals("")){
            try {
                Statics.previousKing = Bukkit.getPlayer(yaml.getString("previous-king")).getName();
            }catch (NullPointerException e){
                Statics.previousKing = Bukkit.getOfflinePlayer(yaml.getString("previous-king")).getName();
            }
        }
        if (!yaml.getString("previous-event").equals("")){
            Statics.eventFinishDate = LocalDateTime.parse(yaml.getString("previous-event"));
        }
        if (yaml.getBoolean("citadel-activated")){
            BukkitRunnable timer = new citadelTimer();
            Statics.citadelActivated = true;
            timer.runTaskTimer(CitadelManager.getPlugin(CitadelManager.class),0, 20);
        }
    }


    public void onDisable(){
        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveDefaultConfig();
        getServer().getConsoleSender().sendMessage("§4[CITADELMANAGER]: Disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null || !(plugin instanceof WorldGuardPlugin)){
            return null;
        }
        return (WorldGuardPlugin) plugin;
    }

    public FileConfiguration getDataConfig() {
        return yaml;
    }

    public File getDataFile() {
        return dataFile;
    }


}
