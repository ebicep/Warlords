package com.ebicep.warlords.player.general;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.mage.specs.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.Avenger;
import com.ebicep.warlords.classes.paladin.specs.Crusader;
import com.ebicep.warlords.classes.paladin.specs.Protector;
import com.ebicep.warlords.classes.rogue.specs.Apothecary;
import com.ebicep.warlords.classes.rogue.specs.Assassin;
import com.ebicep.warlords.classes.rogue.specs.Vindicator;
import com.ebicep.warlords.classes.shaman.specs.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.Thunderlord;
import com.ebicep.warlords.classes.warrior.specs.Berserker;
import com.ebicep.warlords.classes.warrior.specs.Defender;
import com.ebicep.warlords.classes.warrior.specs.Revenant;
import com.ebicep.warlords.util.bukkit.WordWrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.ebicep.warlords.player.general.SkillBoosts.*;

public enum Specializations {
    PYROMANCER("Pyromancer",
            List.of("pyro"),
            Pyromancer::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.", 200),
            SpecType.DAMAGE,
            FIREBALL, FLAME_BURST, TIME_WARP_PYROMANCER, ARCANE_SHIELD_PYROMANCER, INFERNO
    ),
    CRYOMANCER("Cryomancer",
            List.of("cryo"),
            Cryomancer::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.",
                    200
            ),
            SpecType.TANK,
            FROST_BOLT, FREEZING_BREATH, TIME_WARP_CRYOMANCER, ARCANE_SHIELD_CRYOMANCER, ICE_BARRIER
    ),
    AQUAMANCER("Aquamancer",
            List.of("aqua"),
            Aquamancer::new,
            WordWrap.wrapWithNewline(
                    "§7A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies. This specialization has access to the 'Overheal' ability.",
                    200
            ),
            SpecType.HEALER,
            WATER_BOLT, WATER_BREATH, TIME_WARP_AQUAMANCER, ARCANE_SHIELD_AQUAMANCER, HEALING_RAIN
    ),
    BERSERKER("Berserker",
            List.of("bers", "berk"),
            Berserker::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Warrior specialization with a lust for blood and anger issues.", 200),
            SpecType.DAMAGE,
            WOUNDING_STRIKE_BERSERKER, SEISMIC_WAVE_BERSERKER, GROUND_SLAM_BERSERKER, BLOOD_LUST, BERSERK
    ),
    DEFENDER("Defender",
            List.of("def", "defe", "defer"),
            Defender::new,
            WordWrap.wrapWithNewline("§7A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.",
                    200
            ),
            SpecType.TANK,
            WOUNDING_STRIKE_DEFENDER, SEISMIC_WAVE_DEFENDER, GROUND_SLAM_DEFENDER, INTERVENE, LAST_STAND
    ),
    REVENANT("Revenant",
            List.of("rev"),
            Revenant::new,
            WordWrap.wrapWithNewline("§7A support-oriented Warrior specialization that can give allies a second chance of life.", 200),
            SpecType.HEALER,
            CRIPPLING_STRIKE, RECKLESS_CHARGE, GROUND_SLAM_REVENANT, ORBS_OF_LIFE, UNDYING_ARMY
    ),
    AVENGER("Avenger",
            List.of("ave"),
            Avenger::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.",
                    200
            ),
            SpecType.DAMAGE,
            AVENGER_STRIKE, CONSECRATE_AVENGER, LIGHT_INFUSION_AVENGER, HOLY_RADIANCE_AVENGER, AVENGERS_WRATH
    ),
    CRUSADER("Crusader",
            List.of("cru", "crus"),
            Crusader::new,
            WordWrap.wrapWithNewline(
                    "§7A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.",
                    200
            ),
            SpecType.TANK,
            CRUSADER_STRIKE, CONSECRATE_CRUSADER, LIGHT_INFUSION_CRUSADER, HOLY_RADIANCE_CRUSADER, INSPIRING_PRESENCE
    ),
    PROTECTOR("Protector",
            List.of("prot"),
            Protector::new,
            WordWrap.wrapWithNewline(
                    "§7A healing-oriented Paladin specialization that converts damage into healing for his allies and has access to greater healing abilities.",
                    200
            ),
            SpecType.HEALER,
            PROTECTOR_STRIKE, CONSECRATE_PROTECTOR, LIGHT_INFUSION_PROTECTOR, HOLY_RADIANCE_PROTECTOR, HAMMER_OF_LIGHT
    ),
    THUNDERLORD("Thunderlord",
            List.of("tl"),
            Thunderlord::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.", 200),
            SpecType.DAMAGE,
            LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON, LIGHTNING_ROD, CAPACITOR_TOTEM
    ),
    SPIRITGUARD("Spiritguard",
            List.of("ew"),
            Spiritguard::new,
            WordWrap.wrapWithNewline(
                    "§7A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.",
                    200
            ),
            SpecType.TANK,
            FALLEN_SOULS, SPIRIT_LINK, SOULBINDING_WEAPON, REPENTANCE, DEATHS_DEBT
    ),
    EARTHWARDEN("Earthwarden",
            List.of("sg"),
            Earthwarden::new,
            WordWrap.wrapWithNewline("§7A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.", 200),
            SpecType.HEALER,
            EARTHEN_SPIKE, BOULDER, EARTHLIVING_WEAPON, CHAIN_HEAL, HEALING_TOTEM
    ),
    ASSASSIN("Assassin",
            List.of("ass"),
            Assassin::new,
            WordWrap.wrapWithNewline("§7A damage-oriented Rogue specialization that is a master of stealth, rapidly taking out any enemies who cross them.",
                    200
            ),
            SpecType.DAMAGE,
            JUDGEMENT_STRIKE, INCENDIARY_CURSE, BLINDING_ASSAULT, SOUL_SWITCH, ORDER_OF_EVISCERATE
    ),
    VINDICATOR("Vindicator",
            List.of("vind"),
            Vindicator::new,
            WordWrap.wrapWithNewline(
                    "§7A defense-oriented Rogue specialization that deceives enemies by disabling their powers and use bulk power to protect allies.",
                    200
            ),
            SpecType.TANK,
            RIGHTEOUS_STRIKE, SOUL_SHACKLE, HEART_TO_HEART, PRISM_GUARD, VINDICATE
    ),
    APOTHECARY("Apothecary",
            List.of("apoth", "apoc"),
            Apothecary::new,
            WordWrap.wrapWithNewline(
                    "§7A healing-oriented Rogue specialization that uses special brews and alchemical powers to weaken their foes and aid allies.",
                    200
            ),
            SpecType.HEALER,
            IMPALING_STRIKE, SOOTHING_PUDDLE, VITALITY_LIQUOR, REMEDIC_CHAINS, DRAINING_MIASMA
    ),

    ;

    public static final Specializations[] VALUES = values();
    public static final List<String> NAMES = new ArrayList<>();

    static {
        for (Specializations c : VALUES) {
            NAMES.add(c.name);
            NAMES.addAll(c.aliases);
        }
    }

    public final String name;
    public final List<String> aliases;
    public final Supplier<AbstractPlayerClass> create;
    public final String description;
    public final SpecType specType;
    public final List<SkillBoosts> skillBoosts;
    private boolean banned = false;

    Specializations(String name, List<String> aliases, Supplier<AbstractPlayerClass> create, String description, SpecType specType, SkillBoosts... skillBoosts) {
        this.name = name;
        this.aliases = aliases;
        this.create = create;
        this.description = description;
        this.specType = specType;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    public static Specializations getSpecFromName(String name) {
        if (name == null) {
            return PYROMANCER;
        }
        for (Specializations value : Specializations.VALUES) {
            String nameLowerCase = name.toLowerCase();
            if (value.name.equalsIgnoreCase(name) || value.aliases.contains(nameLowerCase)) {
                return value;
            }
        }
        return PYROMANCER;
    }

    public static Specializations getSpecFromNameNullable(String name) {
        if (name == null) {
            return null;
        }
        for (Specializations value : Specializations.VALUES) {
            String nameLowerCase = name.toLowerCase();
            if (value.name.equalsIgnoreCase(name) || value.aliases.contains(nameLowerCase)) {
                return value;
            }
        }
        return null;
    }

    public static Classes getClass(Specializations selected) {
        switch (selected) {
            case PYROMANCER:
            case CRYOMANCER:
            case AQUAMANCER:
                return Classes.MAGE;
            case BERSERKER:
            case DEFENDER:
            case REVENANT:
                return Classes.WARRIOR;
            case AVENGER:
            case CRUSADER:
            case PROTECTOR:
                return Classes.PALADIN;
            case THUNDERLORD:
            case SPIRITGUARD:
            case EARTHWARDEN:
                return Classes.SHAMAN;
        }
        return Classes.ROGUE;
        //return Arrays.stream(Classes.VALUES).filter(o -> o.subclasses.contains(selected)).collect(Collectors.toList()).get(0);
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}