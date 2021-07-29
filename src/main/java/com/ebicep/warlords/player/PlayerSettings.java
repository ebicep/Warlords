package com.ebicep.warlords.player;

import com.ebicep.warlords.maps.Team;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerSettings implements ConfigurationSerializable {
    private Classes selectedClass = Classes.CRYOMANCER;
    private ClassesSkillBoosts classesSkillBoosts = selectedClass.skillBoosts.get(0);
    private boolean hotKeyMode = true;
    private Weapons weapon = Weapons.FELFLAME_BLADE;
    /**
     * Preferred team in the upcoming warlords game
     */
    private transient Team wantedTeam = null;

    @Nonnull
    public Classes selectedClass() {
        return selectedClass;
    }

    public void selectedClass(@Nonnull Classes selectedClass) {
        this.selectedClass = selectedClass;
        if (!selectedClass.skillBoosts.contains(classesSkillBoosts)) {
            classesSkillBoosts = selectedClass.skillBoosts.get(0);
        }
    }

    @Nonnull
    public ClassesSkillBoosts classesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void classesSkillBoosts(@Nonnull ClassesSkillBoosts classesSkillBoosts) {
        this.classesSkillBoosts = classesSkillBoosts;
    }

    @Nullable
    public Team wantedTeam() {
        return wantedTeam;
    }

    public void wantedTeam(@Nullable Team wantedTeam) {
        this.wantedTeam = wantedTeam;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("class", selectedClass.name());
        config.put("classessSkillBoost", classesSkillBoosts.name());
        config.put("hotKeyMode", Boolean.toString(hotKeyMode));
        return config;
    }

    @Nonnull
    public static PlayerSettings deserialize(@Nonnull Map<String, Object> config) {
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

    public Weapons weapon() {
        return weapon;
    }

    public void weapon(Weapons weapon) {
        this.weapon = weapon;
    }

}
