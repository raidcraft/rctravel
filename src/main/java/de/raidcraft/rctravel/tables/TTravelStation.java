package de.raidcraft.rctravel.tables;

import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rc_travel_stations")
public class TTravelStation {

    @Id
    private int id;
    private String name;
    private String groupName;
    private int price;
    private int x;
    private int y;
    private int z;
    private int pitch;
    private int yaw;
    private String world;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

    public int getPrice() {

        return price;
    }

    public void setPrice(int price) {

        this.price = price;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getZ() {

        return z;
    }

    public void setZ(int z) {

        this.z = z;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public int getPitch() {

        return pitch;
    }

    public void setPitch(int pitch) {

        this.pitch = pitch;
    }

    public int getYaw() {

        return yaw;
    }

    public void setYaw(int yaw) {

        this.yaw = yaw;
    }

    public int getXMin() {

        return xMin;
    }

    public void setXMin(int xMin) {

        this.xMin = xMin;
    }

    public int getYMin() {

        return yMin;
    }

    public void setYMin(int yMin) {

        this.yMin = yMin;
    }

    public int getZMin() {

        return zMin;
    }

    public void setZMin(int zMin) {

        this.zMin = zMin;
    }

    public int getXMax() {

        return xMax;
    }

    public void setXMax(int xMax) {

        this.xMax = xMax;
    }

    public int getYMax() {

        return yMax;
    }

    public void setYMax(int yMax) {

        this.yMax = yMax;
    }

    public int getZMax() {

        return zMax;
    }

    public void setZMax(int zMax) {

        this.zMax = zMax;
    }

    public Vector getSk89qMinPoint() {

        return new Vector(xMin, yMin, zMin);
    }

    public Vector getSk89qMaxPoint() {

        return new Vector(xMax, yMax, zMax);
    }

    public Location getBukkitMinPoint() {

        World world = getBukkitWorld();
        if (world == null) return null;

        return new Location(world, xMin, yMin, zMin);
    }

    public Location getBukkitMaxPoint() {

        World world = getBukkitWorld();
        if (world == null) return null;

        return new Location(world, xMax, yMax, zMax);
    }

    public World getBukkitWorld() {

        return Bukkit.getWorld(getWorld());
    }

    public Location getBukkitLocation() {

        World world = getBukkitWorld();
        if (world == null) return null;

        return new Location(world, x / 100D, y / 100D, z / 100D, yaw / 100F, pitch / 100F);
    }
}
