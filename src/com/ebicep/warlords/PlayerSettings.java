package com.ebicep.warlords;

import com.ebicep.warlords.util.Classes;
import com.ebicep.warlords.util.ClassesSkillBoosts;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PlayerSettings implements ConfigurationSerializable {
    private Classes selectedClass = Classes.CRYOMANCER;
    private ClassesSkillBoosts classesSkillBoosts = selectedClass.skillBoosts.get(0);
    private boolean hotKeyMode = true;

    public Classes selectedClass() {
        return selectedClass;
    }

    public void selectedClass(Classes selectedClass) {
        this.selectedClass = selectedClass;
        if (!selectedClass.skillBoosts.contains(classesSkillBoosts)) {
            classesSkillBoosts = selectedClass.skillBoosts.get(0);
        }
    }

    public ClassesSkillBoosts classesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void classesSkillBoosts(ClassesSkillBoosts classesSkillBoosts) {
        this.classesSkillBoosts = classesSkillBoosts;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("class", selectedClass.name());
        config.put("classessSkillBoost", classesSkillBoosts.name());
        config.put("hotKeyMode", Boolean.toString(hotKeyMode));
        return config;
    }
    
    public static PlayerSettings deserialize(Map<String, Object> config) {
        PlayerSettings settings = new PlayerSettings();
        try {
            settings.selectedClass(Classes.valueOf(config.get("class").toString()));
        } catch(IllegalArgumentException ignored) {
        }
        try {
            settings.classesSkillBoosts(ClassesSkillBoosts.valueOf(config.get("classessSkillBoost").toString()));
        } catch(IllegalArgumentException ignored) {
        }
        settings.hotKeyMode = !"false".equals(config.get("hotKeyMode"));
        return settings;
    } 

    public boolean hotKeyMode() {
        return hotKeyMode;
    }

    public void hotKeyMode(boolean hotKeyMode) {
        this.hotKeyMode = hotKeyMode;
    }
    
}
