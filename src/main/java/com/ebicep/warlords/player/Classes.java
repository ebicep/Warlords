package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.Thunderlord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import com.ebicep.warlords.util.WordWrap;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ebicep.warlords.player.ClassesSkillBoosts.*;

public enum Classes {
    PYROMANCER("Pyromancer",
            Pyromancer::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.", 200),
            new ItemStack(Material.NETHER_STALK, 1),
            FIREBALL, FLAME_BURST),
    CRYOMANCER("Cryomancer",
            Cryomancer::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.", 200),
            new ItemStack(Material.CLAY_BALL, 1),
            FROST_BOLT, FREEZING_BREATH),
    AQUAMANCER("Aquamancer",
            Aquamancer::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies.", 200),
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            WATER_BOLT, WATER_BREATH, HEALING_RAIN),
    BERSERKER("Berserker",
            Berserker::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Warrior specialization with a lust for blood and anger issues.", 200),
            new ItemStack(Material.NETHER_STALK, 1),
            WOUNDING_STRIKE_BERSERKER, SEISMIC_WAVE, GROUND_SLAM),
    DEFENDER("Defender",
            Defender::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.", 200),
            new ItemStack(Material.CLAY_BALL, 1),
            WOUNDING_STRIKE_DEFENDER, SEISMIC_WAVE, GROUND_SLAM),
    REVENANT("Revenant",
            Revenant::new,
            WordWrap.wrapWithNewline("§7A support-oriented Warrior specialization that can give allies a second chance of life.", 200),
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            CRIPPLING_STRIKE, RECKLESS_CHARGE, ORBS_OF_LIFE),
    AVENGER("Avenger",
            Avenger::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.", 200),
            new ItemStack(Material.NETHER_STALK, 1),
            AVENGER_STRIKE, CONSECRATE),
    CRUSADER("Crusader",
            Crusader::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.", 200),
            new ItemStack(Material.CLAY_BALL, 1),
            CRUSADER_STRIKE, CONSECRATE),
    PROTECTOR("Protector",
            Protector::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Paladin specialization that converts damage into healing for his allies and has access to greater healing abilities.", 200),
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            PROTECTOR_STRIKE, HOLY_RADIANCE, HAMMER_OF_LIGHT),
    THUNDERLORD("Thunderlord",
            Thunderlord::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.", 200),
            new ItemStack(Material.NETHER_STALK, 1),
            LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON, CAPACITOR_TOTEM),
    SPIRITGUARD("Spiritguard",
            Spiritguard::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.", 200),
            new ItemStack(Material.CLAY_BALL, 1),
            FALLEN_SOULS, SPIRIT_LINK),
    EARTHWARDEN("Earthwarden",
            Earthwarden::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.", 200),
            new ItemStack(Material.INK_SACK, 1, (short) 6),
            EARTHEN_SPIKE, BOULDER, CHAIN_HEAL, HEALING_TOTEM),

    ;

    public final String name;
    public final Supplier<AbstractPlayerClass> create;
    public final String description;
    public final ItemStack icon;
    public final List<ClassesSkillBoosts> skillBoosts;

    Classes(String name, Supplier<AbstractPlayerClass> create, String description, ItemStack icon, ClassesSkillBoosts... skillBoosts) {
        this.name = name;
        this.create = create;
        this.description = description;
        this.icon = icon;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    public static Classes getClass(String name) {
        for (Classes value : Classes.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return CRYOMANCER;
    }

    public static ClassesGroup getClassesGroup(Classes selected) {
        return Arrays.stream(ClassesGroup.values()).filter(o -> o.subclasses.contains(selected)).collect(Collectors.toList()).get(0);
    }

    /**
     *
     * @param player
     * @return
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass()} instead
     */
    @Deprecated
    public static Classes getSelected(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).selectedClass();
    }

    /**
     *
     * @param player
     * @param selectedClass
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(selectedClass)} instead
     */
    @Deprecated
    public static void setSelected(OfflinePlayer player, Classes selectedClass) {
        Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(selectedClass);
        // Game.State.updateTempPlayer(player);
        // setSelectedBoost(player, selectedClass.skillBoosts.get(0)); // This is already done by the player settings
    }

    /**
     *
     * @param player
     * @return
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts()} instead
     */
    @Deprecated
    public static ClassesSkillBoosts getSelectedBoost(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts();
    }

    /**
     *
     * @param player
     * @param selectedBoost
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts(selectedBoost)} instead
     */
    @Deprecated
    public static void setSelectedBoost(OfflinePlayer player, ClassesSkillBoosts selectedBoost) {
        Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts(selectedBoost);
        // Game.State.updateTempPlayer(player);
    }
}