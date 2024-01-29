package com.ebicep.warlords.player.general;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.ebicep.warlords.player.general.ArmorManager.Helmets.*;
import static com.ebicep.warlords.player.general.Weapons.FELFLAME_BLADE;

@Deprecated // since use databaseplayer since its locally cached, two caches makes no sense
public class PlayerSettings {

    public static final HashMap<UUID, PlayerSettings> PLAYER_SETTINGS = new HashMap<>();

    private final UUID uuid;
    private final HashMap<Specializations, SkillBoosts> classesSkillBoosts = new HashMap<>() {{
        for (Specializations value : Specializations.VALUES) {
            put(value, value.skillBoosts.get(0));
        }
    }};
    private final HashMap<Specializations, Weapons> weaponSkins = new HashMap<>() {{
        for (Specializations value : Specializations.VALUES) {
            put(value, FELFLAME_BLADE);
        }
    }};
    private Specializations selectedSpec = Specializations.PYROMANCER;
    /**
     * Preferred team in the upcoming warlords game
     */
    private transient Team wantedTeam = null;

    private ArmorManager.Helmets mageHelmet = SIMPLE_MAGE_HELMET;
    private ArmorManager.Helmets warriorHelmet = SIMPLE_WARRIOR_HELMET;
    private ArmorManager.Helmets paladinHelmet = SIMPLE_PALADIN_HELMET;
    private ArmorManager.Helmets shamanHelmet = SIMPLE_SHAMAN_HELMET;
    private ArmorManager.Helmets rogueHelmet = SIMPLE_ROGUE_HELMET;
    private ArmorManager.Helmets arcanistHelmet = SIMPLE_ARCANIST_HELMET;
    private ArmorManager.ArmorSets mageArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;
    private ArmorManager.ArmorSets warriorArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;
    private ArmorManager.ArmorSets paladinArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;
    private ArmorManager.ArmorSets shamanArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;
    private ArmorManager.ArmorSets rogueArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;
    private ArmorManager.ArmorSets arcanistArmor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;

    public PlayerSettings(UUID uuid) {
        this.uuid = uuid;
    }

    @Nonnull
    public static PlayerSettings getPlayerSettings(@Nonnull Player player) {
        return getPlayerSettings(player.getUniqueId());
    }

    @Nonnull
    public static PlayerSettings getPlayerSettings(@Nonnull UUID uuid) {
        return getPlayerSettings(uuid, true);
    }

    @Nonnull
    public static PlayerSettings getPlayerSettings(@Nonnull UUID uuid, boolean computeIfAbsent) {
        if (computeIfAbsent) {
            return PLAYER_SETTINGS.computeIfAbsent(uuid, (k) -> new PlayerSettings(uuid));
        } else {
            return PLAYER_SETTINGS.getOrDefault(uuid, new PlayerSettings(uuid));
        }
    }

    public SkillBoosts getSkillBoostForClass() {
        return classesSkillBoosts.get(selectedSpec);
    }

    public SkillBoosts getSkillBoostForSpec(Specializations spec) {
        return classesSkillBoosts.get(spec);
    }

    public HashMap<Specializations, SkillBoosts> getClassesSkillBoosts() {
        return classesSkillBoosts;
    }

    public void setSkillBoostForSelectedSpec(SkillBoosts classesSkillBoost) {
        if (classesSkillBoost != null) {
            classesSkillBoosts.put(selectedSpec, classesSkillBoost);
        }
    }

    public void setSkillBoostForSpec(Specializations spec, SkillBoosts classesSkillBoost) {
        if (classesSkillBoost != null) {
            classesSkillBoosts.put(spec, classesSkillBoost);
        }
    }

    public void setSpecsSkillBoosts(HashMap<Specializations, SkillBoosts> classesSkillBoosts) {
        if (classesSkillBoosts != null) {
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

    public Weapons getWeaponSkinForSelectedSpec() {
        return this.getWeaponSkins().getOrDefault(this.getSelectedSpec(), FELFLAME_BLADE);
    }

    public HashMap<Specializations, Weapons> getWeaponSkins() {
        return weaponSkins;
    }

    @Nonnull
    public Specializations getSelectedSpec() {
        if (selectedSpec == null) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("ERROR: SELECTED SPEC IS NULL");
            return Specializations.PYROMANCER;
        }
        return selectedSpec;
    }

    public void setSelectedSpec(Specializations selectedSpec) {
        if (selectedSpec != null) {
            this.selectedSpec = selectedSpec;
        }
    }

    public void setWeaponSkins(HashMap<Specializations, Weapons> weaponSkins) {
        if (weaponSkins != null) {
            weaponSkins.values().removeAll(Collections.singleton(null));
            this.weaponSkins.putAll(weaponSkins);
        }
    }

    public ArmorManager.Helmets getHelmet(Specializations spec) {
        int index = spec.ordinal() / 3;
        return getHelmets().get(index);
    }

    public List<ArmorManager.Helmets> getHelmets() {
        List<ArmorManager.Helmets> armorSets = new ArrayList<>();
        armorSets.add(mageHelmet);
        armorSets.add(warriorHelmet);
        armorSets.add(paladinHelmet);
        armorSets.add(shamanHelmet);
        armorSets.add(rogueHelmet);
        armorSets.add(arcanistHelmet);
        return armorSets;
    }

    public ArmorManager.Helmets getHelmet(Classes classes) {
        return getHelmets().get(classes.ordinal());
    }

    public ArmorManager.ArmorSets getArmorSet(Specializations spec) {
        int index = spec.ordinal() / 3;
        return getArmorSets().get(index);
    }

    public List<ArmorManager.ArmorSets> getArmorSets() {
        List<ArmorManager.ArmorSets> armorSets = new ArrayList<>();
        armorSets.add(mageArmor);
        armorSets.add(warriorArmor);
        armorSets.add(paladinArmor);
        armorSets.add(shamanArmor);
        armorSets.add(rogueArmor);
        armorSets.add(arcanistArmor);
        return armorSets;
    }

    public ArmorManager.ArmorSets getArmorSet(Classes classes) {
        return getArmorSets().get(classes.ordinal());
    }

    public void setHelmet(Classes classes, ArmorManager.Helmets helmet) {
        switch (classes) {
            case MAGE -> this.mageHelmet = helmet;
            case WARRIOR -> this.warriorHelmet = helmet;
            case PALADIN -> this.paladinHelmet = helmet;
            case SHAMAN -> this.shamanHelmet = helmet;
            case ROGUE -> this.rogueHelmet = helmet;
            case ARCANIST -> this.arcanistHelmet = helmet;
        }
        DatabaseManager.updatePlayer(uuid, databasePlayer -> databasePlayer.getClass(classes).setHelmet(helmet));
    }

    public void setArmor(Classes classes, ArmorManager.ArmorSets armor) {
        switch (classes) {
            case MAGE -> this.mageArmor = armor;
            case WARRIOR -> this.warriorArmor = armor;
            case PALADIN -> this.paladinArmor = armor;
            case SHAMAN -> this.shamanArmor = armor;
            case ROGUE -> this.rogueArmor = armor;
            case ARCANIST -> this.arcanistArmor = armor;
        }
        DatabaseManager.updatePlayer(uuid, databasePlayer -> databasePlayer.getClass(classes).setArmor(armor));
    }

}
