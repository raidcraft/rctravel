package de.raidcraft.rctravel.api.group;

/**
 * @author Philip Urban
 */
public interface Group {

    public String getName();

    public String getPlainName();

    public String getIconName();

    public double getDefaultPrice();

    public int getScheduledCooldown();
}
