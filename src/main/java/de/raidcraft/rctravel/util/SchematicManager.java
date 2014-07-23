package de.raidcraft.rctravel.util;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rctravel.RCTravelPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

/**
 * @author Philip Urban
 */
public class SchematicManager {

    private static String SCHEMATIC_SUFFIX = ".schematic";
    private RCTravelPlugin plugin;

    public SchematicManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
    }

    public void createSchematic(World world, Location minPoint, Location maxPoint, String schematicName) throws RaidCraftException {

        try {
            File file = new File(getSchematicDir(world), schematicName + SCHEMATIC_SUFFIX);

            BukkitWorld bukkitWorld = new BukkitWorld(world);

            Vector min = new Vector(Math.min(minPoint.getX(), maxPoint.getX()),
                    Math.min(minPoint.getY(), maxPoint.getY()),
                    Math.min(minPoint.getZ(), maxPoint.getZ()));
            Vector max = new Vector(Math.max(minPoint.getX(), maxPoint.getX()),
                    Math.max(minPoint.getY(), maxPoint.getY()),
                    Math.max(minPoint.getZ(), maxPoint.getZ()));

            // create clipboard
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            // store blocks
            clipboard.copy(new EditSession(bukkitWorld, Integer.MAX_VALUE));
            // store entities
            //            for (LocalEntity entity : bukkitWorld.getEntities(new CuboidRegion(min, max))) {
            //                clipboard.storeEntity(entity);
            //            }
            // save schematic
            MCEditSchematicFormat.MCEDIT.save(clipboard, file);
        } catch (IOException | DataException e) {
            throw new RaidCraftException("Fehler beim speichern der Schematic!");
        }
    }

    public void pasteSchematic(World world, String schematicName) throws RaidCraftException {

        File file = new File(getSchematicDir(world), schematicName + SCHEMATIC_SUFFIX);
        try {
            CuboidClipboard clipboard = MCEditSchematicFormat.MCEDIT.load(file);
            clipboard.paste(new EditSession(new BukkitWorld(world), 200000), clipboard.getOrigin(), false);
            //            clipboard.pasteEntities(clipboard.getOrigin());
        } catch (IOException | DataException e) {
            throw new RaidCraftException("Fehler beim laden der Schematic!");
        } catch (MaxChangedBlocksException e) {
            throw new RaidCraftException("Fehler beim pasten der Schematic! (Zu viele Blöcke)");
        }
    }

    public void deleteSchematic(World world, String schematicName) throws RaidCraftException {

        File file = new File(getSchematicDir(world), schematicName + SCHEMATIC_SUFFIX);

        if (!file.delete()) {
            throw new RaidCraftException("Can't remove schematic file " + file.getAbsolutePath());
        }
    }

    public File getSchematicDir(World world) throws RaidCraftException {

        File dir;
        dir = new File(plugin.getDataFolder(), "schematics");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
            }
        }
        dir = new File(dir, world.getName());
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RaidCraftException("Der Schematics Ordner konnte nicht erstellt werden!");
            }
        }
        return dir;
    }
}
