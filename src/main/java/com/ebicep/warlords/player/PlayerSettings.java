package com.ebicep.warlords.player;

import com.ebicep.warlords.game.Team;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.ebicep.warlords.player.ArmorManager.Helmets.*;

public class PlayerSettings implements ConfigurationSerializable {
    private Classes selectedClass = Classes.PYROMANCER;
    private final HashMap<Classes, ClassesSkillBoosts> classesSkillBoosts = new HashMap<Classes, ClassesSkillBoosts>() {{
        put(Classes.PYROMANCER, ClassesSkillBoosts.FIREBALL);
        put(Classes.CRYOMANCER, ClassesSkillBoosts.FROST_BOLT);
        put(Classes.AQUAMANCER, ClassesSkillBoosts.WATER_BOLT);
        put(Classes.BERSERKER, ClassesSkillBoosts.WOUNDING_STRIKE_BERSERKER);
        put(Classes.DEFENDER, ClassesSkillBoosts.WOUNDING_STRIKE_DEFENDER);
        put(Classes.REVENANT, ClassesSkillBoosts.ORBS_OF_LIFE);
        put(Classes.AVENGER, ClassesSkillBoosts.AVENGER_STRIKE);
        put(Classes.CRUSADER, ClassesSkillBoosts.CRUSADER_STRIKE);
        put(Classes.PROTECTOR, ClassesSkillBoosts.PROTECTOR_STRIKE);
        put(Classes.THUNDERLORD, ClassesSkillBoosts.LIGHTNING_BOLT);
        put(Classes.SPIRITGUARD, ClassesSkillBoosts.FALLEN_SOULS);
        put(Classes.EARTHWARDEN, ClassesSkillBoosts.EARTHEN_SPIKE);
        put(Classes.ASSASSIN, ClassesSkillBoosts.EARTHEN_SPIKE);
        put(Classes.VINDICATOR, ClassesSkillBoosts.EARTHEN_SPIKE);
        put(Classes.APOTHECARY, ClassesSkillBoosts.EARTHEN_SPIKE);
    }};
    private boolean hotKeyMode = true;
    private HashMap<Classes, Weapons> weaponSkins = new HashMap<Classes, Weapons>() {{
        put(Classes.PYROMANCER, Weapons.FELFLAME_BLADE);
        put(Classes.CRYOMANCER, Weapons.FELFLAME_BLADE);
        put(Classes.AQUAMANCER, Weapons.FELFLAME_BLADE);
        put(Classes.BERSERKER, Weapons.FELFLAME_BLADE);
        put(Classes.DEFENDER, Weapons.FELFLAME_BLADE);
        put(Classes.REVENANT, Weapons.FELFLAME_BLADE);
        put(Classes.AVENGER, Weapons.FELFLAME_BLADE);
        put(Classes.CRUSADER, Weapons.FELFLAME_BLADE);
        put(Classes.PROTECTOR, Weapons.FELFLAME_BLADE);
        put(Classes.THUNDERLORD, Weapons.FELFLAME_BLADE);
        put(Classes.SPIRITGUARD, Weapons.FELFLAME_BLADE);
        put(Classes.EARTHWARDEN, Weapons.FELFLAME_BLADE);
    }};
    private Settings.ParticleQuality particleQuality = Settings.ParticleQuality.HIGH;
    /**
     * Preferred team in the upcoming warlords game
     */
    private transient Team wantedTeam = null;

    private ArmorManager.Helmets mageHelmet = SIMPLE_MAGE_HELMET;
    private ArmorManager.Helmets warriorHelmet = SIMPLE_WARRIOR_HELMET;
    private ArmorManager.Helmets paladinHelmet = SIMPLE_PALADIN_HELMET;
    private ArmorManager.Helmets shamanHelmet = SIMPLE_SHAMAN_HELMET;
    private ArmorManager.ArmorSets mageArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_MAGE;
    private ArmorManager.ArmorSets warriorArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_WARRIOR;
    private ArmorManager.ArmorSets paladinArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_PALADIN;
    private ArmorManager.ArmorSets shamanArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_SHAMAN;

    @Nonnull
    public Classes getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(@Nonnull Classes selectedClass) {
        this.selectedClass = selectedClass;
    }

    public ClassesSkillBoosts getSkillBoostForClass() {
        return classesSkillBoosts.get(selectedClass);
    }

    public HashMap<Classes, ClassesSkillBoosts> getClassesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void setSkillBoostForSelectedClass(ClassesSkillBoosts classesSkillBoost) {
        classesSkillBoosts.put(selectedClass, classesSkillBoost);
    }

    public void setClassesSkillBoosts(HashMap<Classes, ClassesSkillBoosts> classesSkillBoosts) {
        this.classesSkillBoosts.putAll(classesSkillBoosts);
    }

    @Nullable
    public Team getWantedTeam() {
        if (wantedTeam == null) {
            Team newTeam = Math.random() <= .5 ? Team.BLUE : Team.RED;
            setWantedTeam(newTeam);
            return newTeam;
        }
        return wantedTeam;
    }

    public void setWantedTeam(@Nullable Team wantedTeam) {
        this.wantedTeam = wantedTeam;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("class", selectedClass.name());
        //config.put("classessSkillBoost", classesSkillBoosts.name());
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
            //settings.setClassesSkillBoosts(ClassesSkillBoosts.valueOf(config.get("classessSkillBoost").toString()));
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

    public HashMap<Classes, Weapons> getWeaponSkins() {
        return weaponSkins;
    }

    public void setWeaponSkins(HashMap<Classes, Weapons> weaponSkins) {
        this.weaponSkins = weaponSkins;
    }

    public Settings.ParticleQuality getParticleQuality() {
        return particleQuality;
    }

    public void setParticleQuality(Settings.ParticleQuality particleQuality) {
        this.particleQuality = particleQuality;
    }

    public boolean isHotKeyMode() {
        return hotKeyMode;
    }

    public List<ArmorManager.Helmets> getHelmets() {
        List<ArmorManager.Helmets> armorSets = new ArrayList<>();
        armorSets.add(mageHelmet);
        armorSets.add(warriorHelmet);
        armorSets.add(paladinHelmet);
        armorSets.add(shamanHelmet);
        return armorSets;
    }

    public List<ArmorManager.ArmorSets> getArmorSets() {
        List<ArmorManager.ArmorSets> armorSets = new ArrayList<>();
        armorSets.add(mageArmor);
        armorSets.add(warriorArmor);
        armorSets.add(paladinArmor);
        armorSets.add(shamanArmor);
        return armorSets;
    }

    public void setHelmets(List<ArmorManager.Helmets> helmets) {
        this.mageHelmet = helmets.get(0);
        this.warriorHelmet = helmets.get(1);
        this.paladinHelmet = helmets.get(2);
        this.shamanHelmet = helmets.get(3);
    }

    public void setArmorSets(List<ArmorManager.ArmorSets> armorSets) {
        this.mageArmor = armorSets.get(0);
        this.warriorArmor = armorSets.get(1);
        this.paladinArmor = armorSets.get(2);
        this.shamanArmor = armorSets.get(3);
    }

    public ArmorManager.Helmets getMageHelmet() {
        return mageHelmet;
    }

    public void setMageHelmet(ArmorManager.Helmets mageHelmet) {
        this.mageHelmet = mageHelmet;
    }

    public ArmorManager.Helmets getWarriorHelmet() {
        return warriorHelmet;
    }

    public void setWarriorHelmet(ArmorManager.Helmets warriorHelmet) {
        this.warriorHelmet = warriorHelmet;
    }

    public ArmorManager.Helmets getPaladinHelmet() {
        return paladinHelmet;
    }

    public void setPaladinHelmet(ArmorManager.Helmets paladinHelmet) {
        this.paladinHelmet = paladinHelmet;
    }

    public ArmorManager.Helmets getShamanHelmet() {
        return shamanHelmet;
    }

    public void setShamanHelmet(ArmorManager.Helmets shamanHelmet) {
        this.shamanHelmet = shamanHelmet;
    }

    public ArmorManager.ArmorSets getMageArmor() {
        return mageArmor;
    }

    public void setMageArmor(ArmorManager.ArmorSets mageArmor) {
        this.mageArmor = mageArmor;
    }

    public ArmorManager.ArmorSets getWarriorArmor() {
        return warriorArmor;
    }

    public void setWarriorArmor(ArmorManager.ArmorSets warriorArmor) {
        this.warriorArmor = warriorArmor;
    }

    public ArmorManager.ArmorSets getPaladinArmor() {
        return paladinArmor;
    }

    public void setPaladinArmor(ArmorManager.ArmorSets paladinArmor) {
        this.paladinArmor = paladinArmor;
    }

    public ArmorManager.ArmorSets getShamanArmor() {
        return shamanArmor;
    }

    public void setShamanArmor(ArmorManager.ArmorSets shamanArmor) {
        this.shamanArmor = shamanArmor;
    }
}
