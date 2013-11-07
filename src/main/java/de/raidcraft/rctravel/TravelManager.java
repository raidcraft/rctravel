package de.raidcraft.rctravel;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
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
    private Map<String, TargetStation> queuedPlayers = new CaseInsensitiveMap<>();

    public TravelManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    public void queuePlayer(Player player, TargetStation targetStation) {

        queuedPlayers.put(player.getName(), targetStation);
    }

    public void removeFromQueue(Player player) {

        queuedPlayers.remove(player.getName());
    }

    public void startTravel(Station station) {

        for(Map.Entry<String, TargetStation> entry : new CaseInsensitiveMap<>(queuedPlayers).entrySet()) {
            if(!entry.getValue().getStation().equals(station)) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if(player == null) continue;
            try {
                Station target = entry.getValue().getTarget();
                // check money
                if(target instanceof Chargeable) {
                    if(!RaidCraft.getEconomy().hasEnough(player.getName(), ((Chargeable) target).getPrice())) {
                        throw new RaidCraftException("Not enough money!");
                    }
                }

                station.travel(player, entry.getValue().getTarget().getLocation());

                // charge player
                if(entry.getValue().getTarget() instanceof Chargeable) {
                    RaidCraft.getEconomy().substract(player.getName(), ((Chargeable) entry.getValue().getTarget()).getPrice());
                }
            } catch (RaidCraftException e) {
                // ignore travel exceptions here
            }
            queuedPlayers.remove(entry.getKey());
        }
    }

    public void reload() {

        queuedPlayers.clear();
    }

    public class TargetStation {

        private Station station;
        private Station target;

        public TargetStation(Station station, Station target) {

            this.station = station;
            this.target = target;
        }

        public Station getStation() {

            return station;
        }

        public Station getTarget() {

            return target;
        }
    }
}
