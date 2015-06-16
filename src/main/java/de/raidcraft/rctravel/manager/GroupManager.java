package de.raidcraft.rctravel.manager;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.rctravel.ConfigGroup;
import de.raidcraft.rctravel.RCTravelPlugin;
import de.raidcraft.rctravel.api.group.Group;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class GroupManager {

    private RCTravelPlugin plugin;
    private Map<String, Group> cachedGroups = new CaseInsensitiveMap<>();

    public GroupManager(RCTravelPlugin plugin) {

        this.plugin = plugin;
        reload();
    }

    public Group getGroup(String groupName) {

        Group group = cachedGroups.get(groupName);
        if (group == null) {
            for (Group gr : cachedGroups.values()) {
                if (gr.getName().toLowerCase().startsWith(groupName.toLowerCase())) {
                    group = gr;
                    break;
                }
            }
        }
        return group;
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
                cachedGroups.put(groupName, new ConfigGroup(configurationSection));
            }
        }
    }

    public void reload() {

        loadGroups();
    }
}
