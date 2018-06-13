package de.raidcraft.rctravel.manager;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.rctravel.ConfigStationGroup;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.StationGroup;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * @author Philip Urban
 */
public class GroupManager {

    private RCTravelPlugin plugin;
    private Map<String, StationGroup> cachedGroups = new CaseInsensitiveMap<>();

    public GroupManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        reload();
    }

    public Optional<StationGroup> getGroup(String groupName) {

        if (groupName == null) return Optional.empty();

        StationGroup stationGroup = cachedGroups.get(groupName);
        if (stationGroup == null) {
            for (StationGroup gr : cachedGroups.values()) {
                if (gr.getName().toLowerCase().startsWith(groupName.toLowerCase())) {
                    stationGroup = gr;
                    break;
                }
            }
        }
        return Optional.ofNullable(stationGroup);
    }

    public void loadGroups() {

        cachedGroups.clear();
        loadDir("groups");
    }

    private void loadDir(String dirName) {

        File configFolder = new File(plugin.getDataFolder(), dirName);
        configFolder.mkdirs();
        loadConfig(configFolder);
    }

    private void loadConfig(File dir) {


        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // recursive loading of all sub directories
                loadConfig(file);
            }
            if (file.getName().endsWith(".yml")) {

                String name = file.getName().replace(".yml", "");
                ConfigurationSection configurationSection = plugin.configure(new SimpleConfiguration<>(plugin, file), false);
                String groupName = configurationSection.getString("name");
                if (groupName == null) continue;
                cachedGroups.put(groupName, new ConfigStationGroup(configurationSection));
            }
        }
    }

    public void reload() {

        loadGroups();
    }
}
