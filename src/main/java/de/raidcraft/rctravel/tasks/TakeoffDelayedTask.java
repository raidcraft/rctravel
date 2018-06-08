package de.raidcraft.rctravel.tasks;

import de.raidcraft.RaidCraft;
import de.raidcraft.rctravel.Journey;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.TeleportTravelStation;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.entity.Player;

public class TakeoffDelayedTask implements Runnable {

    Station start;
    Station target;
    Player player;

    public TakeoffDelayedTask(Station start, Station target, Player player) {

        this.start = start;
        this.target = target;
        this.player = player;
    }

    @Override
    public void run() {

        if (start instanceof TeleportTravelStation) {
            RaidCraft.getComponent(RCTravelPlugin.class).getTravelManager().queuePlayer(player, new Journey(start, target));
        }

    }
}
