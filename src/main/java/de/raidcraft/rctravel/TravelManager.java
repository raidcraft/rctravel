package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Philip Urban
 */
public class TravelManager {

    private RCTravelPlugin plugin;
    private Map<String, Journey> queuedPlayers = new CaseInsensitiveMap<>();

    public TravelManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    public void queuePlayer(Player player, Journey journey) {

        queuedPlayers.put(player.getName(), journey);
    }

    public void removeFromQueue(Player player) {

        queuedPlayers.remove(player.getName());
    }

    public void startTravel(Station station) {

        for (Map.Entry<String, Journey> entry : new CaseInsensitiveMap<>(queuedPlayers).entrySet()) {
            if (!entry.getValue().getStation().equals(station)) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            try {
                Station target = entry.getValue().getTarget();
                Station start = entry.getValue().getStation();
                // check money
                if (target instanceof Chargeable) {
                    if (!RaidCraft.getEconomy().hasEnough(player.getName(), ((Chargeable) target).getPrice((int) start.getLocation().distance(target.getLocation())))) {
                        throw new RaidCraftException("Not enough money!");
                    }
                }

                start.travel(player, entry.getValue().getTarget());
                queuedPlayers.remove(entry.getKey());
                // charge player
                if (entry.getValue().getTarget() instanceof Chargeable) {
                    RaidCraft.getEconomy().substract(player.getName(), ((Chargeable) entry.getValue().getTarget()).getPrice(), BalanceSource.TRAVEL, start.getDisplayName() + " -> " + target.getDisplayName());
                }
            } catch (RaidCraftException e) {
                // ignore travel exceptions here
            }
        }
    }

    public void reload() {

        queuedPlayers.clear();
    }
}
