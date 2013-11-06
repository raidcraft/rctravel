package de.raidcraft.rctravel.api.station;

/**
 * @author Philip Urban
 */
public interface SchematicStation {

    public void changeSchematic(boolean locked);

    public String getLockedSchematicName();

    public String getUnlockedSchematicName();
}
