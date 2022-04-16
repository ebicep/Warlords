package com.ebicep.warlords.player;

import com.ebicep.warlords.game.Team;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.ebicep.warlords.player.ArmorManager.Helmets.*;

public class PlayerSettings implements ConfigurationSerializable {
    private Specializations selectedSpec = Specializations.PYROMANCER;
    private final HashMap<Specializations, SkillBoosts> classesSkillBoosts = new HashMap<Specializations, SkillBoosts>() {{
        put(Specializations.PYROMANCER, SkillBoosts.FIREBALL);
        put(Specializations.CRYOMANCER, SkillBoosts.FROST_BOLT);
        put(Specializations.AQUAMANCER, SkillBoosts.WATER_BOLT);
        put(Specializations.BERSERKER, SkillBoosts.WOUNDING_STRIKE_BERSERKER);
        put(Specializations.DEFENDER, SkillBoosts.WOUNDING_STRIKE_DEFENDER);
        put(Specializations.REVENANT, SkillBoosts.ORBS_OF_LIFE);
        put(Specializations.AVENGER, SkillBoosts.AVENGER_STRIKE);
        put(Specializations.CRUSADER, SkillBoosts.CRUSADER_STRIKE);
        put(Specializations.PROTECTOR, SkillBoosts.PROTECTOR_STRIKE);
        put(Specializations.THUNDERLORD, SkillBoosts.LIGHTNING_BOLT);
        put(Specializations.SPIRITGUARD, SkillBoosts.FALLEN_SOULS);
        put(Specializations.EARTHWARDEN, SkillBoosts.EARTHEN_SPIKE);
        put(Specializations.ASSASSIN, SkillBoosts.JUDGEMENT_STRIKE);
        put(Specializations.VINDICATOR, SkillBoosts.RIGHTEOUS_STRIKE);
        put(Specializations.APOTHECARY, SkillBoosts.IMPALING_STRIKE);
    }};
    private boolean hotKeyMode = true;
    private HashMap<Specializations, Weapons> weaponSkins = new HashMap<Specializations, Weapons>() {{
        put(Specializations.PYROMANCER, Weapons.FELFLAME_BLADE);
        put(Specializations.CRYOMANCER, Weapons.FELFLAME_BLADE);
        put(Specializations.AQUAMANCER, Weapons.FELFLAME_BLADE);
        put(Specializations.BERSERKER, Weapons.FELFLAME_BLADE);
        put(Specializations.DEFENDER, Weapons.FELFLAME_BLADE);
        put(Specializations.REVENANT, Weapons.FELFLAME_BLADE);
        put(Specializations.AVENGER, Weapons.FELFLAME_BLADE);
        put(Specializations.CRUSADER, Weapons.FELFLAME_BLADE);
        put(Specializations.PROTECTOR, Weapons.FELFLAME_BLADE);
        put(Specializations.THUNDERLORD, Weapons.FELFLAME_BLADE);
        put(Specializations.SPIRITGUARD, Weapons.FELFLAME_BLADE);
        put(Specializations.EARTHWARDEN, Weapons.FELFLAME_BLADE);
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
    private ArmorManager.Helmets rogueHelmet = SIMPLE_ROGUE_HELMET;
    private ArmorManager.ArmorSets mageArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_MAGE;
    private ArmorManager.ArmorSets warriorArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_WARRIOR;
    private ArmorManager.ArmorSets paladinArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_PALADIN;
    private ArmorManager.ArmorSets shamanArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_SHAMAN;
    private ArmorManager.ArmorSets rogueArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE_ROGUE;

    @Nonnull
    public Specializations getSelectedSpec() {
        if (selectedSpec == null) {
            System.out.println("ERROR: SELECTED SPEC IS NULL");
            return Specializations.PYROMANCER;
        }
        return selectedSpec;
    }

    public void setSelectedSpec(Specializations selectedSpec) {
        if (selectedSpec != null) {
            this.selectedSpec = selectedSpec;
        }
    }

    public SkillBoosts getSkillBoostForClass() {
        return classesSkillBoosts.get(selectedSpec);
    }

    public HashMap<Specializations, SkillBoosts> getClassesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void setSkillBoostForSelectedSpec(SkillBoosts classesSkillBoost) {
        if (classesSkillBoost != null) {
            classesSkillBoosts.put(selectedSpec, classesSkillBoost);
        }
    }

    public void setSpecsSkillBoosts(HashMap<Specializations, SkillBoosts> classesSkillBoosts) {
        if (classesSkillBoosts != null) {
            for (SkillBoosts value : classesSkillBoosts.values()) {
                if (value == null) {
                    System.out.println("ERROR: SETTING SKILL BOOSTS - SKILL BOOST IS NULL");
                    break;
                }
            }
            classesSkillBoosts.values().removeAll(Collections.singleton(null));
            this.classesSkillBoosts.putAll(classesSkillBoosts);
        }
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
        config.put("class", selectedSpec.name());
        //config.put("classessSkillBoost", classesSkillBoosts.name());
        config.put("hotKeyMode", Boolean.toString(hotKeyMode));
        config.put("particleQuality", particleQuality.name());
        return config;
    }

    @Nonnull
    public static PlayerSettings deserialize(@Nonnull Map<String, Object> config) {
        PlayerSettings settings = new PlayerSettings();
        try {
            settings.setSelectedSpec(Specializations.valueOf(config.get("class").toString()));
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

    public HashMap<Specializations, Weapons> getWeaponSkins() {
        return weaponSkins;
    }

    public void setWeaponSkins(HashMap<Specializations, Weapons> weaponSkins) {
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
        armorSets.add(rogueHelmet);
        return armorSets;
    }

    public List<ArmorManager.ArmorSets> getArmorSets() {
        List<ArmorManager.ArmorSets> armorSets = new ArrayList<>();
        armorSets.add(mageArmor);
        armorSets.add(warriorArmor);
        armorSets.add(paladinArmor);
        armorSets.add(shamanArmor);
        armorSets.add(rogueArmor);
        return armorSets;
    }

    public void setHelmets(List<ArmorManager.Helmets> helmets) {
        this.mageHelmet = helmets.get(0);
        this.warriorHelmet = helmets.get(1);
        this.paladinHelmet = helmets.get(2);
        this.shamanHelmet = helmets.get(3);
        this.rogueHelmet = helmets.get(4);
    }

    public void setArmorSets(List<ArmorManager.ArmorSets> armorSets) {
        this.mageArmor = armorSets.get(0);
        this.warriorArmor = armorSets.get(1);
        this.paladinArmor = armorSets.get(2);
        this.shamanArmor = armorSets.get(3);
        this.rogueArmor = armorSets.get(4);
    }

    public ArmorManager.Helmets getMageHelmet() {
        return mageHelmet;
    }

    public void setMageHelmet(ArmorManager.Helmets mageHelmet) {
        if (mageHelmet != null) {
            this.mageHelmet = mageHelmet;
        }
    }

    public ArmorManager.Helmets getWarriorHelmet() {
        return warriorHelmet;
    }

    public void setWarriorHelmet(ArmorManager.Helmets warriorHelmet) {
        if (warriorHelmet != null) {
            this.warriorHelmet = warriorHelmet;
        }
    }

    public ArmorManager.Helmets getPaladinHelmet() {
        return paladinHelmet;
    }

    public void setPaladinHelmet(ArmorManager.Helmets paladinHelmet) {
        if (paladinHelmet != null) {
            this.paladinHelmet = paladinHelmet;
        }
    }

    public ArmorManager.Helmets getShamanHelmet() {
        return shamanHelmet;
    }

    public void setShamanHelmet(ArmorManager.Helmets shamanHelmet) {
        if (shamanHelmet != null) {
            this.shamanHelmet = shamanHelmet;
        }
    }

    public ArmorManager.Helmets getRogueHelmet() {
        return rogueHelmet;
    }

    public void setRogueHelmet(ArmorManager.Helmets rogueHelmet) {
        if (rogueHelmet != null) {
            this.rogueHelmet = rogueHelmet;
        }
    }

    public ArmorManager.ArmorSets getMageArmor() {
        return mageArmor;
    }

    public void setMageArmor(ArmorManager.ArmorSets mageArmor) {
        if (mageArmor != null) {
            this.mageArmor = mageArmor;
        }
    }

    public ArmorManager.ArmorSets getWarriorArmor() {
        return warriorArmor;
    }

    public void setWarriorArmor(ArmorManager.ArmorSets warriorArmor) {
        if (warriorArmor != null) {
            this.warriorArmor = warriorArmor;
        }
    }

    public ArmorManager.ArmorSets getPaladinArmor() {
        return paladinArmor;
    }

    public void setPaladinArmor(ArmorManager.ArmorSets paladinArmor) {
        if (paladinArmor != null) {
            this.paladinArmor = paladinArmor;
        }
    }

    public ArmorManager.ArmorSets getShamanArmor() {
        return shamanArmor;
    }

    public void setShamanArmor(ArmorManager.ArmorSets shamanArmor) {
        if (shamanArmor != null) {
            this.shamanArmor = shamanArmor;
        }
    }

    public ArmorManager.ArmorSets getRogueArmor() {
        return rogueArmor;
    }

    public void setRogueArmor(ArmorManager.ArmorSets rogueArmor) {
        if (rogueArmor != null) {
            this.rogueArmor = rogueArmor;
        }
    }
}
