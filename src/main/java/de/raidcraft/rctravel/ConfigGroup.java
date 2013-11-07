package de.raidcraft.rctravel;

import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class ConfigGroup implements Group {

    private String name;
    private String iconName;
    private String vehicleName;
    private double defaultPrice;
    private int lockTime;
    private int unlockTime;

    public ConfigGroup(ConfigurationSection config) {

        this.name = config.getString("name");
        this.iconName = config.getString("icon", "sign");
        this.defaultPrice = config.getDouble("default-price", 0);
        this.lockTime = config.getInt("lock-time", 15);
        this.unlockTime = config.getInt("unlock-time", 3);
        this.vehicleName = config.getString("vehicle-name", "Transportmittel");
    }

    @Override
    public String getIconName() {

        return iconName;
    }

    @Override
    public String getVehicleName() {

        return vehicleName;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getPlainName() {

        return StringUtils.formatName(name);
    }

    @Override
    public double getDefaultPrice() {

        return defaultPrice;
    }

    @Override
    public int getLockTime() {

        return lockTime;
    }

    @Override
    public int getUnlockTime() {

        return unlockTime;
    }
}
