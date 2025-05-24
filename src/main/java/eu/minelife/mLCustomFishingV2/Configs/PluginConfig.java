package eu.minelife.mLCustomFishingV2.Configs;

import eu.minelife.mLCustomFishingV2.Main;
import eu.minelife.mLCustomFishingV2.DataGroup.ChanceGroup;
import eu.minelife.mLCustomFishingV2.DataGroup.RewardEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {
    private final Main plugin;
    private FileConfiguration config;
    private Map<String, List<ChanceGroup>> regionRewardMap;

    public PluginConfig(Main plugin) {
        this.plugin = plugin;
        this.reloadConfig();
    }

    public void reloadConfig() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.plugin.saveDefaultConfig();
        this.loadRegionRewards();
    }

    private void loadRegionRewards() {
        this.regionRewardMap = new HashMap();
        if (!this.config.isConfigurationSection("rewards")) {
            this.plugin.getLogger().warning("No 'rewards' section found in config!");
        } else {
            Set<String> regionKeys = this.config.getConfigurationSection("rewards").getKeys(false);
            Iterator var2 = regionKeys.iterator();

            while(true) {
                while(var2.hasNext()) {
                    String regionKey = (String)var2.next();
                    String regionPath = "rewards." + regionKey;
                    if (!this.config.isConfigurationSection(regionPath)) {
                        this.plugin.getLogger().warning("No config section found for region: " + regionKey);
                    } else {
                        List<ChanceGroup> chanceGroupsForRegion = new ArrayList();
                        Set<String> chanceKeys = this.config.getConfigurationSection(regionPath).getKeys(false);
                        Iterator var7 = chanceKeys.iterator();

                        while(var7.hasNext()) {
                            String chanceKey = (String)var7.next();

                            double percent;
                            String chancePath;
                            try {
                                chancePath = chanceKey.replace("_", ".");
                                percent = Double.parseDouble(chancePath);
                            } catch (NumberFormatException var21) {
                                this.plugin.getLogger().warning("Invalid chance key: " + chanceKey + " under region " + regionKey);
                                continue;
                            }

                            chancePath = regionPath + "." + chanceKey;
                            ConfigurationSection subRewardsSection = this.config.getConfigurationSection(chancePath);
                            if (subRewardsSection == null) {
                                this.plugin.getLogger().warning("No sub-reward entries at " + chancePath);
                            } else {
                                List<RewardEntry> rewardEntries = new ArrayList();
                                Set<String> subKeys = subRewardsSection.getKeys(false);
                                Iterator var15 = subKeys.iterator();

                                while(var15.hasNext()) {
                                    String subKey = (String)var15.next();
                                    String subPath = chancePath + "." + subKey;
                                    String cmd = this.config.getString(subPath + ".command", "NONE");
                                    String msg = this.config.getString(subPath + ".message", "NONE");
                                    RewardEntry entry = new RewardEntry(cmd, msg);
                                    rewardEntries.add(entry);
                                }

                                ChanceGroup group = new ChanceGroup(percent, rewardEntries);
                                chanceGroupsForRegion.add(group);
                            }
                        }

                        chanceGroupsForRegion.sort(Comparator.comparingDouble(ChanceGroup::getPercent));
                        this.regionRewardMap.put(regionKey, chanceGroupsForRegion);
                    }
                }

                return;
            }
        }
    }

    public Map<String, List<ChanceGroup>> getRegionRewardMap() {
        return this.regionRewardMap;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("prefix"));
    }

    public String getPermissionToReload() {
        return this.config.getString("reload-permission");
    }

    public String getNoPermissionMessage() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("no-permission-message"));
    }

    public String getReloadMessage() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("reload-message"));
    }

    public String getUsageMessage() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("usage-message"));
    }

    public boolean isDebug() {
        return this.config.getBoolean("debug");
    }

    public boolean isAutofishPreventionEnabled() {
        return this.config.getBoolean("autofish-prevention-enabled");
    }

    public int getMaxConsecutiveCasts() {
        return this.config.getInt("max-consecutive-casts");
    }

    public String getMaxConsecutiveCastsMessage() {
        return ChatColor.translateAlternateColorCodes('&', this.config.getString("max-consecutive-casts-message"));
    }
}