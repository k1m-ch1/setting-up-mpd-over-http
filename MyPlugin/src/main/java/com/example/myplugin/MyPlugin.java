package com.example.myplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MyPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getLogger().info("ExternalCommand plugin enabled!");
  }

  @Override
  public void onDisable() {
    getLogger().info("ExternalCommand plugin disabled.");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("mpc")) {
      try {
        // Replace this with your actual script or command
        String[] fixed = {"mpc"};
        String[] fullCommand = new String[fixed.length + args.length];
        // copy from source array at inndex, to dest array at index, and copy this amount of lenth
        System.arraycopy(fixed,0, fullCommand, 0, fixed.length);
        System.arraycopy(args, 0, fullCommand, fixed.length, args.length);
        Process process = new ProcessBuilder(fullCommand).start();
        sender.sendMessage("✅ toggled mpc!");
      } catch (IOException e) {
        sender.sendMessage("❌ Failed to toggle mpc");
        e.printStackTrace();
        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("mpc_add")) {
      try {
        // Replace this with your actual script or command
          String fullArg = String.join(" ", args);
          sender.sendMessage(args);
          //sender.sendMessage("source /home/k1mch1/.bashrc && mpc_auto_add.sh " + fullArg);
          //Process process = new ProcessBuilder("/home/k1mch1/.local/bin/mpc_auto_add.sh", fullArg).start();
          ProcessBuilder pb = new ProcessBuilder("/home/k1mch1/.local/bin/mpc_auto_add.sh", fullArg); 
          pb.start();
      } catch (IOException e) {
        sender.sendMessage("❌ something went wrong...");
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }
}
