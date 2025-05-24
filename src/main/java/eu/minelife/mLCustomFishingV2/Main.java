package eu.minelife.mLCustomFishingV2;

import eu.minelife.mLCustomFishingV2.Configs.PluginConfig;
import eu.minelife.mLCustomFishingV2.Listeners.FishingListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private PluginConfig pluginConfig;

    public void onEnable() {
        this.getLogger().info("MLCustomFishingV2 enabled!");
        this.pluginConfig = new PluginConfig(this);
        this.pluginConfig.reloadConfig();
        this.getServer().getPluginManager().registerEvents(new FishingListener(this, this.pluginConfig), this);
        this.getCommand("customfishing").setExecutor(new CustomFishingCommand(this, this.pluginConfig));
    }
}
