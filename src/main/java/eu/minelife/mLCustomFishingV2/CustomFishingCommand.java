package eu.minelife.mLCustomFishingV2;

import eu.minelife.mLCustomFishingV2.Configs.PluginConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CustomFishingCommand implements CommandExecutor {
    private final Main plugin;
    private final PluginConfig pluginConfig;

    public CustomFishingCommand(Main plugin, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String var10001;
        if (!sender.hasPermission(this.pluginConfig.getPermissionToReload())) {
            var10001 = this.pluginConfig.getPrefix();
            sender.sendMessage(var10001 + this.pluginConfig.getNoPermissionMessage());
            return true;
        } else if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
            this.pluginConfig.reloadConfig();
            var10001 = this.pluginConfig.getPrefix();
            sender.sendMessage(var10001 + this.pluginConfig.getReloadMessage());
            return true;
        } else {
            var10001 = this.pluginConfig.getPrefix();
            sender.sendMessage(var10001 + this.pluginConfig.getUsageMessage());
            return true;
        }
    }
}