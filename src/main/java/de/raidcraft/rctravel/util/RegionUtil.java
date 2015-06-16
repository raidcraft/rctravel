package de.raidcraft.rctravel.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.RCTravelPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public class RegionUtil {

    public static boolean isInsideRegion(Player player, Location min, Location max) {

        Location pLoc = player.getLocation();
        if (pLoc.getX() >= min.getX() && pLoc.getX() <= max.getX()
                && pLoc.getY() >= min.getY() && pLoc.getY() <= max.getY()
                && pLoc.getZ() >= min.getZ() && pLoc.getZ() <= max.getZ()) {
            return true;
        }
        return false;
    }
}
