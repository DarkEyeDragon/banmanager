package com.warhut.banmanager;

import java.sql.SQLException;
import java.util.UUID;

public class BanTimeValidator{
    BanManager plugin;
    MySQLHandler mysqlHandler;

    public BanTimeValidator(BanManager plugin){
         this.plugin = plugin;
         mysqlHandler = new MySQLHandler(plugin);
    }

   public boolean checkDatabase(){
       plugin.getServer().getLogger().info("Checking database for expired bans.");
       try{
           plugin.getServer().getLogger().info(mysqlHandler.getBanList().toString());
           for (UUID uuid : mysqlHandler.getBanList()) {
                System.out.println(uuid);
                System.out.println(mysqlHandler.getRemainingBanTime(uuid));
                if(mysqlHandler.getRemainingBanTime(uuid) < 0){
                    mysqlHandler.removeFromBanList(uuid, mysqlHandler.getBansTable());
                }
            }
        }catch(SQLException e){
            e.getMessage();
       }
        return false;
   }

}
