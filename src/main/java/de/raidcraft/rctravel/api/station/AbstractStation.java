package de.raidcraft.rctravel.api.station;

import de.raidcraft.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
public abstract class AbstractStation implements Station {

    private String name;
    private Location location;
    private String displayName;

    protected AbstractStation(String name, Location location) {

        this.name = name;
        this.location = location;
        this.displayName = name.replace("_", " ").replace("-", " ");
    }

    @Override
    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    @Override
    public String getName() {

        return StringUtils.formatName(name);
    }

    @Override
    public Location getLocation() {

        return location;
    }

    @Override
    public double getDistance(Location location) {
        return getLocation().distance(location);
    }

    @Override
    public void travelFrom(Player player, Station sourceStation) {
        travel(player, sourceStation.getLocation(), getLocation());
    }

    @Override
    public void travelTo(Player player, Station targetStation) {
        travel(player, getLocation(), targetStation.getLocation());
    }

    @Override
    public void travelTo(Player player) {
        travel(player, player.getLocation(), getLocation());
    }

    @Override
    public double getPrice(Location start) {
        if (this instanceof Chargeable) {
            return ((Chargeable) this).getPrice((int) start.distance(getLocation()));
        }
        return 0;
    }

    @Override
    public int compareTo(Station o) {
        return getDisplayName().compareTo(o.getDisplayName());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractStation that = (AbstractStation) o;

        return name.equalsIgnoreCase(that.name);

    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
