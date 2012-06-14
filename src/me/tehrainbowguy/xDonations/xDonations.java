package me.tehrainbowguy.xDonations;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

import static me.tehrainbowguy.xDonations.MySql.*;


public class xDonations extends JavaPlugin {


    public void onDisable() {
        try {
            closeConn();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        log.info(this + " is now disabled!");
    }
    public static FileConfiguration config;
    void setupConfig() {
        config = getConfig();
        try {
            File XBank = new File("plugins" + File.separator + "xDonations" + File.separator + "config.yml");
            XBank.mkdir();
            saveConfig();
        } catch (Exception e) {
            log.severe("[xDonations] There was a big ass error, you should poke rainbow!");
            e.printStackTrace();
        }
        if (!config.contains("xp.config.database")) {
            config.set("xp.config.database", "jdbc:mysql://localhost:3306/minecraft");
        }
        if (!config.contains("xp.config.user")) {
            config.set("xp.config.user", "root");
        }
        if (!config.contains("xp.config.password")) {
            config.set("xp.config.password", "YourAwesomePassword");
        }
        saveConfig();

    }
    public static Permission permission = null;
    private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    public Logger log;

    public void onEnable() {
        log = getServer().getLogger();
        setupPermissions();
        setupConfig();
            try {
                initDB();
                createTables();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                this.getPluginLoader().disablePlugin(this);
            }
        log.info(this + " is now enabled!");
    }

    private double getBal(String p){
        double bal = 0;
            try {
                bal = MySql.getBalance(p);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return bal;
    }
    private void setBal(String p, double newbal){
            try {
                MySql.setBalance(p, newbal);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("xDonations")) {
            if(sender instanceof Player){
                sender.sendMessage("You need to do this via console....");
                return true;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length != 3) {
                    return false;
                }
                String arg1 = args[1];
                String arg2 = args[2];

               final double currbal = getBal(arg2);
               final double wanttodep = Double.parseDouble(arg1);
                final double total = currbal + wanttodep;
                setBal(arg2 ,currbal + wanttodep);

                if(currbal < 5 & total >= 5){
                   log.info("Setting " + arg2 + " to Donor");
                    getServer().dispatchCommand(sender, "pex user " + arg2 + " group set Donator");
                } else
                if(currbal < 20 && total >= 20){
                    log.info("Setting " + arg2 + " to Donor+");
                    getServer().dispatchCommand(sender, "pex user " + arg2 + " group set DonatorPlus");

                } else
                if(currbal < 100 && total >= 100){
                    log.info("Setting " + arg2 + " to DonorX");
                    getServer().dispatchCommand(sender, "pex user " + arg2 + " group set DonatorExtreme");

                }

               return true;
            }
        }

        return false;
    }

}
