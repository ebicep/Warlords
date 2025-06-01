package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Leech;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.player.general.SkillBoosts.*;

public enum Specializations {
    PYROMANCER("Pyromancer",
            List.of("pyro"),
            Component.text("A damage-oriented Mage specialization that uses the destructive Fire spells to obliterate enemies.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            FIREBALL, FLAME_BURST, TIME_WARP_PYROMANCER, ARCANE_SHIELD_PYROMANCER, INFERNO
    ),
    CRYOMANCER("Cryomancer",
            List.of("cryo"),
            Component.text("A defense-oriented Mage specialization that uses Ice spells to slow down enemies and to creative defensive barriers.", NamedTextColor.GRAY),
            SpecType.TANK,
            FROST_BOLT, FREEZING_BREATH, TIME_WARP_CRYOMANCER, ARCANE_SHIELD_CRYOMANCER, ICE_BARRIER
    ),
    AQUAMANCER("Aquamancer",
            List.of("aqua"),
            Component.text(
                    "A healing-oriented Mage specialization that uses Water spells to heal allies and to deal minor damage to enemies. This specialization has access to the 'Overheal' ability.",
                    NamedTextColor.GRAY
            ),
            SpecType.HEALER,
            WATER_BOLT, WATER_BREATH, TIME_WARP_AQUAMANCER, ARCANE_SHIELD_AQUAMANCER, HEALING_RAIN
    ),
    BERSERKER("Berserker",
            List.of("bers", "berk"),
            Component.text("A damage-oriented Warrior specialization with a lust for blood and anger issues.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            WOUNDING_STRIKE_BERSERKER, SEISMIC_WAVE_BERSERKER, GROUND_SLAM_BERSERKER, BLOOD_LUST, BERSERK
    ),
    DEFENDER("Defender",
            List.of("def", "defe", "defer"),
            Component.text("A defense-oriented Warrior specialization that can protect teammates by mitigating damage and intercepting enemy hits.", NamedTextColor.GRAY),
            SpecType.TANK,
            WOUNDING_STRIKE_DEFENDER, SEISMIC_WAVE_DEFENDER, GROUND_SLAM_DEFENDER, INTERVENE, LAST_STAND
    ),
    REVENANT("Revenant",
            List.of("rev"),
            Component.text("A support-oriented Warrior specialization that can give allies a second chance of life.", NamedTextColor.GRAY),
            SpecType.HEALER,
            CRIPPLING_STRIKE, RECKLESS_CHARGE, GROUND_SLAM_REVENANT, ORBS_OF_LIFE, UNDYING_ARMY
    ),
    AVENGER("Avenger",
            List.of("ave"),
            Component.text("A damage-oriented Paladin specialization that focuses on draining energy from enemies and has access to minor healing.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            AVENGER_STRIKE, CONSECRATE_AVENGER, LIGHT_INFUSION_AVENGER, HOLY_RADIANCE_AVENGER, AVENGERS_WRATH
    ),
    CRUSADER("Crusader",
            List.of("cru", "crus"),
            Component.text("A defense-oriented Paladin specialization that inspires allies by granting them more energy in battle and has access to minor healing.",
                    NamedTextColor.GRAY
            ),
            SpecType.TANK,
            CRUSADER_STRIKE, CONSECRATE_CRUSADER, LIGHT_INFUSION_CRUSADER, HOLY_RADIANCE_CRUSADER, INSPIRING_PRESENCE
    ),
    PROTECTOR("Protector",
            List.of("prot"),
            Component.text("A healing-oriented Paladin specialization that converts damage into healing for their allies and has access to greater healing abilities.",
                    NamedTextColor.GRAY
            ),
            SpecType.HEALER,
            PROTECTOR_STRIKE, CONSECRATE_PROTECTOR, LIGHT_INFUSION_PROTECTOR, HOLY_RADIANCE_PROTECTOR, HAMMER_OF_LIGHT
    ),
    THUNDERLORD("Thunderlord",
            List.of("tl"),
            Component.text("A damage-oriented Shaman specialization that calls upon the power of Lightning to electrocute enemies.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            LIGHTNING_BOLT, CHAIN_LIGHTNING, WINDFURY_WEAPON, LIGHTNING_ROD, CAPACITOR_TOTEM
    ),
    SPIRITGUARD("Spiritguard",
            List.of("sg"),
            Component.text("A defense-oriented Shaman specialization that calls upon the aid of spirits old and new to mitigate damage and avoid death.", NamedTextColor.GRAY),
            SpecType.TANK,
            FALLEN_SOULS, SPIRIT_LINK, SOULBINDING_WEAPON, REPENTANCE, DEATHS_DEBT
    ),
    EARTHWARDEN("Earthwarden",
            List.of("ew"),
            Component.text("A healing-oriented Shaman specialization that calls upon the power of Earth to crush enemies and to aid allies.", NamedTextColor.GRAY),
            SpecType.HEALER,
            EARTHEN_SPIKE, BOULDER, EARTHLIVING_WEAPON, CHAIN_HEAL, HEALING_TOTEM
    ),
    ASSASSIN("Assassin",
            List.of("ass"),
            Component.text("A damage-oriented Rogue specialization that is a master of stealth, rapidly taking out any enemies who cross them.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            JUDGEMENT_STRIKE, INCENDIARY_CURSE, BLINDING_ASSAULT, SOUL_SWITCH, ORDER_OF_EVISCERATE
    ),
    VINDICATOR("Vindicator",
            List.of("vind"),
            Component.text("A defense-oriented Rogue specialization that deceives enemies by disabling their powers and use bulk power to protect allies.", NamedTextColor.GRAY),
            SpecType.TANK,
            RIGHTEOUS_STRIKE, SOUL_SHACKLE, HEART_TO_HEART, PRISM_GUARD, VINDICATE
    ),
    APOTHECARY("Apothecary",
            List.of("apoth", "apoc"),
            Component.text("A healing-oriented Rogue specialization that uses special brews and alchemical powers to weaken their foes and aid allies.", NamedTextColor.GRAY),
            SpecType.HEALER,
            IMPALING_STRIKE, SOOTHING_PUDDLE, VITALITY_CONCOCTION, REMEDIC_CHAINS, DRAINING_MIASMA
    ) {
        @Override
        public void init(WarlordsEntity warlordsEntity) {
            float leechHeal = AbstractAbility.convertToPercent(ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES,
                    this.name().toLowerCase() + ".leechHealPercent",
                    float.class
            ));
            warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Apothecary Leech",
                    null,
                    Specializations.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC,
                    cooldownManager -> {},
                    false
            ) {
                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent finalEvent) {
                            if (finalEvent.getSource() != warlordsEntity) {
                                return;
                            }
                            if (!finalEvent.isDamageInstance()) {
                                return;
                            }
                            Optional<Leech.LeechData> impalingStrikeData = new CooldownFilter<>(finalEvent.getWarlordsEntity(), RegularCooldown.class)
                                    .filterCooldownClassAndMapToObjectsOfClass(Leech.LeechData.class)
                                    .findAny();
                            float impalingStrikeHeal = 0;
                            float stacks = 0;
                            float leechAmount = 0;
                            if (impalingStrikeData.isPresent()) {
                                Leech.LeechData data = impalingStrikeData.get();
                                stacks = data.getStacksCount();
                                leechAmount = data.getLeechAmount();
                                impalingStrikeHeal = finalEvent.getValueBeforeAllReduction() * AbstractAbility.convertToPercent(stacks * leechAmount);
                            }
                            float passiveHeal = 0;
                            if (finalEvent.getFinalEventFlag() == WarlordsDamageHealingFinalEvent.FinalEventFlag.REGULAR) {
                                passiveHeal = finalEvent.getValue() * leechHeal;
                            }
                            warlordsEntity.addInstance(InstanceBuilder
                                    .healing()
                                    .cause("Leech")
                                    .source(warlordsEntity)
                                    .value(impalingStrikeHeal + passiveHeal)
                                    .flags(InstanceFlags.NO_HIT_SOUND, InstanceFlags.APOTH_SELF_HEAL)
                                    .debugMessages(ComponentBuilder
                                            .create("", NamedTextColor.GREEN)
                                            .text("Passive Apoth Heal: ", NamedTextColor.GRAY)
                                            .text(NumberFormat.formatOptionalHundredths(finalEvent.getValue()), NamedTextColor.GOLD)
                                            .text(" > ", NamedTextColor.GRAY)
                                            .text(NumberFormat.formatOptionalHundredths(passiveHeal), NamedTextColor.GOLD)
                                            .text("  |  ", NamedTextColor.GRAY)
                                            .text("Impaling Strike Heal: ", NamedTextColor.GRAY)
                                            .text(NumberFormat.formatOptionalHundredths(finalEvent.getValueBeforeAllReduction()), NamedTextColor.GOLD)
                                            .text(" > ", NamedTextColor.GRAY)
                                            .text(NumberFormat.formatOptionalHundredths(impalingStrikeHeal), NamedTextColor.GOLD)
                                            .text(" (", NamedTextColor.GRAY)
                                            .text(stacks, NamedTextColor.DARK_GREEN)
                                            .text("/", NamedTextColor.GRAY)
                                            .text(leechAmount, NamedTextColor.DARK_GREEN)
                                            .text(")", NamedTextColor.GRAY)
                                            .build())
                            );
                        }

                        @EventHandler
                        public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                            if (event.isDamageInstance()) {
                                return;
                            }
                            if (event.getWarlordsEntity() != warlordsEntity) {
                                return;
                            }
                            if (event.getCause().equals("Leech") && !event.getFlags().contains(InstanceFlags.APOTH_SELF_HEAL)) {
                                event.setCancelled(true);
                            }
                        }
                    };
                }
            });
        }
    },
    CONJURER("Conjurer",
            List.of("con"),
            Component.text("A damage-oriented Arcanist specialization that uses venomous attacks and pure magical spells to eliminate their foes.", NamedTextColor.GRAY),
            SpecType.DAMAGE,
            POISONOUS_HEX, SOULFIRE_BEAM, ENERGY_SEER_CONJURER, CONTAGIOUS_FACADE, ASTRAL_PLAGUE
    ),
    SENTINEL("Sentinel",
            List.of("sen"),
            Component.text("A defense-oriented Arcanist specialization that monitors enemies' skills while shielding their allies.", NamedTextColor.GRAY),
            SpecType.TANK,
            FORTIFYING_HEX, GUARDIAN_BEAM, ENERGY_SEER_SENTINEL, MYSTICAL_BARRIER, SANCTUARY
    ),
    LUMINARY("Luminary",
            List.of("lum"),
            Component.text("A healing-oriented Arcanist specialization who can bend the space between light and darkness to aid their allies and weaken foes.",
                    NamedTextColor.GRAY
            ),
            SpecType.HEALER,
            MERCIFUL_HEX, RAY_OF_LIGHT, ENERGY_SEER_LUMINARY, SANCTIFIED_BEACON, DIVINE_BLESSING
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
        if (selected == null) {
            return Classes.MAGE;
        }
        return switch (selected) {
            case PYROMANCER, CRYOMANCER, AQUAMANCER -> Classes.MAGE;
            case BERSERKER, DEFENDER, REVENANT -> Classes.WARRIOR;
            case AVENGER, CRUSADER, PROTECTOR -> Classes.PALADIN;
            case THUNDERLORD, SPIRITGUARD, EARTHWARDEN -> Classes.SHAMAN;
            case ASSASSIN, VINDICATOR, APOTHECARY -> Classes.ROGUE;
            case CONJURER, SENTINEL, LUMINARY -> Classes.ARCANIST;
        };
    }

    public static Specializations generateSpec(Specializations selectedSpec) {
        if (ThreadLocalRandom.current().nextDouble() < .25) {
            return selectedSpec;
        }
        Specializations[] otherSpecs = Arrays.stream(VALUES)
                                             .filter(value -> value != selectedSpec)
                                             .toArray(Specializations[]::new);
        return otherSpecs[ThreadLocalRandom.current().nextInt(otherSpecs.length)];
    }

    public final String name;
    public final List<String> aliases;
    public final SpecType specType;
    public final List<SkillBoosts> skillBoosts;
    private final TextComponent description;
    private boolean banned = false;

    Specializations(
            String name,
            List<String> aliases,
            TextComponent description,
            SpecType specType,
            SkillBoosts... skillBoosts
    ) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.specType = specType;
        this.skillBoosts = Arrays.asList(skillBoosts);
    }

    public void init(WarlordsEntity warlordsEntity) {

    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Specializations next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public TextComponent getDescription() {
        return description;
    }

    public AbstractPlayerClass create() {
        String specName = name().toLowerCase();
        return new AbstractPlayerClass(
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".className", String.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".name", String.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".maxHealth", int.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".maxEnergy", int.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".energyPerSec", int.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".energyPerHit", int.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".damageResistance", float.class),
                ConfigManager.getSpecsConfigValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".speed", int.class),
                ConfigManager.getSpecsConfigListValue(ConfigManager.DEFAULT_NAMESPACES, specName + ".abilities", String.class)
                             .stream()
                             .map(s -> {
                                 AbstractAbility ability = Ability.ABILITY_DATABASE_MAP.getOrDefault(s, Ability.FIREBALL).create.get();
                                 return ability;
                             })
                             .toList()
        ) {

        };
    }

}
