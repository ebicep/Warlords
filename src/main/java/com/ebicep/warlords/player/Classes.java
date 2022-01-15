package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.rogue.specs.assassin.Assassin;
import com.ebicep.warlords.classes.rogue.specs.vindicator.Vindicator;
import com.ebicep.warlords.classes.rogue.specs.apothecary.Apothecary;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.Thunderlord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import com.ebicep.warlords.util.WordWrap;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ebicep.warlords.player.ClassesSkillBoosts.*;

public enum Classes {
    PYROMANCER("Pyromancer",
            Pyromancer::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.", 200),
            SpecType.DAMAGE,
            FIREBALL, FLAME_BURST, TIME_WARP_PYROMANCER, ARCANE_SHIELD_PYROMANCER, INFERNO),
    CRYOMANCER("Cryomancer",
            Cryomancer::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.", 200),
            SpecType.TANK,
            FROST_BOLT, FREEZING_BREATH, TIME_WARP_CRYOMANCER, ARCANE_SHIELD_CRYOMANCER, ICE_BARRIER),
    AQUAMANCER("Aquamancer",
            Aquamancer::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies. This specialization has access to the 'Overheal' ability.", 200),
            SpecType.HEALER,
            WATER_BOLT, WATER_BREATH, TIME_WARP_AQUAMANCER, ARCANE_SHIELD_AQUAMANCER, HEALING_RAIN),
    BERSERKER("Berserker",
            Berserker::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Warrior specialization with a lust for blood and anger issues.", 200),
            SpecType.DAMAGE,
            WOUNDING_STRIKE_BERSERKER, SEISMIC_WAVE_BERSERKER, GROUND_SLAM_BERSERKER, BLOOD_LUST, BERSERK),
    DEFENDER("Defender",
            Defender::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.", 200),
            SpecType.TANK,
            WOUNDING_STRIKE_DEFENDER, SEISMIC_WAVE_DEFENDER, GROUND_SLAM_DEFENDER, INTERVENE, LAST_STAND),
    REVENANT("Revenant",
            Revenant::new,
            WordWrap.wrapWithNewline("§7A support-oriented Warrior specialization that can give allies a second chance of life.", 200),
            SpecType.HEALER,
            CRIPPLING_STRIKE, RECKLESS_CHARGE, GROUND_SLAM_REVENANT, ORBS_OF_LIFE, UNDYING_ARMY),
    AVENGER("Avenger",
            Avenger::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.", 200),
            SpecType.DAMAGE,
            AVENGER_STRIKE, CONSECRATE_AVENGER, LIGHT_INFUSION_AVENGER, HOLY_RADIANCE_AVENGER, AVENGERS_WRATH),
    CRUSADER("Crusader",
            Crusader::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.", 200),
            SpecType.TANK,
            CRUSADER_STRIKE, CONSECRATE_CRUSADER, LIGHT_INFUSION_CRUSADER, HOLY_RADIANCE_CRUSADER, INSPIRING_PRESENCE),
    PROTECTOR("Protector",
            Protector::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Paladin specialization that converts damage into healing for his allies and has access to greater healing abilities.", 200),
            SpecType.HEALER,
            PROTECTOR_STRIKE, CONSECRATE_PROTECTOR, LIGHT_INFUSION_PROTECTOR, HOLY_RADIANCE_PROTECTOR, HAMMER_OF_LIGHT),
    THUNDERLORD("Thunderlord",
            Thunderlord::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.", 200),
            SpecType.DAMAGE,
            LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON, LIGHTNING_ROD, CAPACITOR_TOTEM),
    SPIRITGUARD("Spiritguard",
            Spiritguard::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.", 200),
            SpecType.TANK,
            FALLEN_SOULS, SPIRIT_LINK, SOULBINDING_WEAPON, REPENTANCE, DEATHS_DEBT),
    EARTHWARDEN("Earthwarden",
            Earthwarden::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.", 200),
            SpecType.HEALER,
            EARTHEN_SPIKE, BOULDER, EARTHLIVING_WEAPON, CHAIN_HEAL, HEALING_TOTEM),
    ASSASSIN("Assassin",
            Assassin::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Rogue specialization that serves the art of stealth, rapidly taking out any enemies who cross them.", 200),
            SpecType.DAMAGE,
            JUDGEMENT_STRIKE),
    VINDICATOR("Vindicator",
            Vindicator::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Rogue specialization that deceives enemies by disabling their powers and use bulk power to protect allies.", 200),
            SpecType.TANK,
            RIGHTEOUS_STRIKE),
    APOTHECARY("Apothecary",
            Apothecary::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Rogue specialization that uses special brews and alchemical powers to weaken their foes while aiding allies.", 200),
            SpecType.HEALER,
            IMPALING_STRIKE),

    ;

    public final String name;
    public final Supplier<AbstractPlayerClass> create;
    public final String description;
    public final SpecType specType;
    public final List<ClassesSkillBoosts> skillBoosts;

    Classes(String name, Supplier<AbstractPlayerClass> create, String description, SpecType specType, ClassesSkillBoosts... skillBoosts) {
        this.name = name;
        this.create = create;
        this.description = description;
        this.specType = specType;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    public static Classes getClass(String name) {
        if(name == null) {
            return PYROMANCER;
        }
        for (Classes value : Classes.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return PYROMANCER;
    }

    public static ClassesGroup getClassesGroup(Classes selected) {
        return Arrays.stream(ClassesGroup.values()).filter(o -> o.subclasses.contains(selected)).collect(Collectors.toList()).get(0);
    }

    public static ClassesGroup getClassesGroup(String specName) {
        return Arrays.stream(ClassesGroup.values()).filter(o -> o.subclasses.stream().anyMatch(subClass -> subClass.name.equalsIgnoreCase(specName))).collect(Collectors.toList()).get(0);
    }

    /**
     *
     * @param player
     * @return
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass()} instead
     */
    @Deprecated
    public static Classes getSelected(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass();
    }

    /**
     *
     * @param player
     * @param selectedClass
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(selectedClass)} instead
     */
    @Deprecated
    public static void setSelected(OfflinePlayer player, Classes selectedClass) {
        Warlords.getPlayerSettings(player.getUniqueId()).setSelectedClass(selectedClass);
        // Game.State.updateTempPlayer(player);
        // setSelectedBoost(player, selectedClass.skillBoosts.get(0)); // This is already done by the player settings
    }

    /**
     *
     * Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts()} instead
     */
    public static ClassesSkillBoosts getSelectedBoost(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getSkillBoostForClass();
    }

    /**
     *
     * @param player
     * @param selectedBoost
     * @deprecated Trivial method, call {@code Warlords.getPlayerSettings(player.getUniqueId()).classesSkillBoosts(selectedBoost)} instead
     */
    @Deprecated
    public static void setSelectedBoost(OfflinePlayer player, ClassesSkillBoosts selectedBoost) {
        Warlords.getPlayerSettings(player.getUniqueId()).setSkillBoostForSelectedClass(selectedBoost);
        // Game.State.updateTempPlayer(player);
    }
}