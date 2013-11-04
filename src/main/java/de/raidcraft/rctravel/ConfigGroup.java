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
    private double defaultPrice;

    public ConfigGroup(ConfigurationSection config) {

        this.name = config.getString("name");
        this.iconName = config.getString("icon", "sign");
        this.defaultPrice = config.getDouble("default-price", 0);
    }

    @Override
    public String getIconName() {

        return iconName;
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
}
