package eu.minelife.mLCustomFishingV2.Listeners;

import eu.minelife.mLCustomFishingV2.Main;
import eu.minelife.mLCustomFishingV2.Configs.PluginConfig;
import eu.minelife.mLCustomFishingV2.DataGroup.ChanceGroup;
import eu.minelife.mLCustomFishingV2.DataGroup.RewardEntry;
import eu.minelife.mLCustomFishingV2.Utils.ChanceUtils;
import eu.minelife.mLCustomFishingV2.Utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FishingListener implements Listener {
    private final Main plugin;
    private final PluginConfig pluginConfig;
    private final Map<UUID, Location> lastBobberLocation = new HashMap<>();
    private final Map<UUID, Location> lastPlayerLocation = new HashMap<>();
    private final Map<UUID, Integer> castCount = new HashMap<>();

    public FishingListener(Main plugin, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Auto-fish prevention logic
        if (this.pluginConfig.isAutofishPreventionEnabled() && event.getState() == State.FISHING) {
            handleAutoFishPrevention(event, player, uuid);
            return;
        }

        // Reward logic only for caught fish
        if (event.getState() == State.CAUGHT_FISH) {
            handleFishCapture(event, player);
        }
    }

    private void handleAutoFishPrevention(PlayerFishEvent event, Player player, UUID uuid) {
        Location bobberLocation = event.getHook().getLocation();
        Location playerLocation = player.getLocation();

        if (lastBobberLocation.containsKey(uuid) && lastPlayerLocation.containsKey(uuid)) {
            Location oldBobber = lastBobberLocation.get(uuid);
            Location oldPlayer = lastPlayerLocation.get(uuid);

            if (sameLocation(oldBobber, bobberLocation, 2.0) &&
                    sameLocation(oldPlayer, playerLocation, 0.1)) {

                int count = castCount.getOrDefault(uuid, 0) + 1;
                castCount.put(uuid, count);

                if (pluginConfig.isDebug()) {
                    player.sendMessage("Cast count: " + count);
                }

                if (count >= pluginConfig.getMaxConsecutiveCasts()) {
                    sendAutoFishMessage(player);
                    event.setCancelled(true);
                    return;
                }
            } else {
                castCount.put(uuid, 1);
            }
        } else {
            castCount.put(uuid, 1);
        }

        lastBobberLocation.put(uuid, bobberLocation);
        lastPlayerLocation.put(uuid, playerLocation);
    }

    private void handleFishCapture(PlayerFishEvent event, Player player) {
        // Ensure caught item is a valid fish
        if (event.getCaught() instanceof Item) {
            Item caughtItem = (Item) event.getCaught();
            Material type = caughtItem.getItemStack().getType();

            //if (!isAllowedFish(type)) {
            //    caughtItem.setItemStack(new ItemStack(Material.COD));
            //}
        }

        // Get valid regions for rewards
        Location playerLoc = player.getLocation();
        List<String> regionIDs = WorldGuardUtils.getRegionsAtLocation(playerLoc);

        if (pluginConfig.isDebug()) {
            player.sendMessage("Regions: " + regionIDs);
        }

        processRegionRewards(player, regionIDs);
    }

    private void processRegionRewards(Player player, List<String> regionIDs) {
        for (String regionID : regionIDs) {
            List<ChanceGroup> regionGroups = pluginConfig.getRegionRewardMap().get(regionID);
            if (regionGroups == null) continue;

            for (ChanceGroup group : regionGroups) {
                if (tryGiveReward(player, group)) {
                    return; // Stop after first successful reward
                }
            }
        }
    }

    private boolean tryGiveReward(Player player, ChanceGroup group) {
        double percent = group.getPercent();
        List<RewardEntry> rewards = group.getRewards();

        if (pluginConfig.isDebug()) {
            player.sendMessage("Chance: " + percent);
        }

        if (ChanceUtils.checkChance(percent)) {
            RewardEntry chosen = rewards.get(ChanceUtils.getRandom().nextInt(rewards.size()));
            executeReward(player, chosen);
            return true;
        }
        return false;
    }

    private void executeReward(Player player, RewardEntry reward) {
        String command = reward.getCommand();
        String message = reward.getMessage();

        if (pluginConfig.isDebug()) {
            player.sendMessage("Command: " + command);
            player.sendMessage("Message: " + message);
        }

        if (!command.equalsIgnoreCase("NONE")) {
            command = command.replace("%NAME%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        if (!message.equalsIgnoreCase("NONE")) {
            player.sendMessage(pluginConfig.getPrefix() +
                    ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void sendAutoFishMessage(Player player) {
        player.sendMessage(pluginConfig.getPrefix() +
                pluginConfig.getMaxConsecutiveCastsMessage());
    }

    /*
    private boolean isAllowedFish(Material mat) {
        return mat == Material.COD ||
                mat == Material.SALMON ||
                mat == Material.PUFFERFISH ||
                mat == Material.TROPICAL_FISH;
    }
    */

    private boolean sameLocation(Location oldLoc, Location newLoc, double maxDist) {
        return oldLoc.getWorld().equals(newLoc.getWorld()) &&
                oldLoc.distanceSquared(newLoc) <= (maxDist * maxDist);
    }
}