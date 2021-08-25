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
    private Settings.ParticleQuality particleQuality = Settings.ParticleQuality.HIGH;
    /**
     * Preferred team in the upcoming warlords game
     */
    private transient Team wantedTeam = null;

    @Nonnull
    public Classes getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(@Nonnull Classes selectedClass) {
        this.selectedClass = selectedClass;
        if (!selectedClass.skillBoosts.contains(classesSkillBoosts)) {
            classesSkillBoosts = selectedClass.skillBoosts.get(0);
        }
    }

    @Nonnull
    public ClassesSkillBoosts getClassesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void setClassesSkillBoosts(@Nonnull ClassesSkillBoosts classesSkillBoosts) {
        this.classesSkillBoosts = classesSkillBoosts;
    }

    @Nullable
    public Team getWantedTeam() {
        return wantedTeam;
    }

    public void setWantedTeam(@Nullable Team wantedTeam) {
        this.wantedTeam = wantedTeam;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("class", selectedClass.name());
        config.put("classessSkillBoost", classesSkillBoosts.name());
        config.put("hotKeyMode", Boolean.toString(hotKeyMode));
        config.put("particleQuality", particleQuality.name());
        return config;
    }

    @Nonnull
    public static PlayerSettings deserialize(@Nonnull Map<String, Object> config) {
        PlayerSettings settings = new PlayerSettings();
        try {
            settings.setSelectedClass(Classes.valueOf(config.get("class").toString()));
        } catch (IllegalArgumentException ignored) {
        }
        try {
            settings.setClassesSkillBoosts(ClassesSkillBoosts.valueOf(config.get("classessSkillBoost").toString()));
        } catch (IllegalArgumentException ignored) {
        }
        settings.hotKeyMode = !"false".equals(config.get("hotKeyMode"));
        try {
            settings.setParticleQuality(Settings.ParticleQuality.valueOf(config.get("particleQuality").toString()));
        } catch (IllegalArgumentException ignored) {
        }
        return settings;
    }

    public boolean getHotKeyMode() {
        return hotKeyMode;
    }

    public void setHotKeyMode(boolean hotKeyMode) {
        this.hotKeyMode = hotKeyMode;
    }

    public Weapons getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapons weapon) {
        this.weapon = weapon;
    }

    public Settings.ParticleQuality getParticleQuality() {
        return particleQuality;
    }

    public void setParticleQuality(Settings.ParticleQuality particleQuality) {
        this.particleQuality = particleQuality;
    }
}
