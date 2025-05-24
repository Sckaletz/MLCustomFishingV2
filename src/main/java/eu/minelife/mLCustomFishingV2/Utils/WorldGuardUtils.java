package eu.minelife.mLCustomFishingV2.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;

public class WorldGuardUtils {
    public static List<String> getRegionsAtLocation(Location loc) {
        List<String> regionIDs = new ArrayList();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        if (regionManager == null) {
            return regionIDs;
        } else {
            BlockVector3 pt = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            ApplicableRegionSet regionSet = regionManager.getApplicableRegions(pt);
            Iterator var6 = regionSet.iterator();

            while(var6.hasNext()) {
                ProtectedRegion r = (ProtectedRegion)var6.next();
                regionIDs.add(r.getId());
            }

            return regionIDs;
        }
    }
}