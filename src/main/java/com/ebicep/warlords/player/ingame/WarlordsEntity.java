package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.HealingPowerup;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.*;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.FlagInfo;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TeleportUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.ebicep.warlords.util.bukkit.ItemBuilder.*;

public abstract class WarlordsEntity {

    //RED << (Receiving from enemy / Negative from team?)
    public static final Component RECEIVE_ARROW_RED = Component.text("«", NamedTextColor.RED);
    //GREEN << (Receiving from team / Positive from enemy?)
    public static final Component RECEIVE_ARROW_GREEN = Component.text("«", NamedTextColor.GREEN);
    //RED >> (Doing negatives teammates?)
    public static final Component GIVE_ARROW_RED = Component.text("»", NamedTextColor.RED);
    //GREEN >> (Doing negatives to enemy / Doing positives to team)
    public static final Component GIVE_ARROW_GREEN = Component.text("»", NamedTextColor.GREEN);
    private static final int MINUTE_STATS_SPLITS = 35;
    protected final Game game;
    protected boolean spawnGrave = true;
    protected CalculateSpeed speed;
    protected String name;
    protected UUID uuid;
    protected AbstractPlayerClass spec;
    protected float walkSpeed = 1;
    protected LivingEntity entity;
    protected Specializations specClass;
    @Nullable
    protected CompassTargetMarker compassTarget;
    private final List<Float> recordDamage = new ArrayList<>();
    private final PlayerStatisticsMinute minuteStats = new PlayerStatisticsMinute();
    private final PlayerStatisticsSecond secondStats = new PlayerStatisticsSecond();
    private final List<Achievement.AbstractAchievementRecord<?>> achievementsUnlocked = new ArrayList<>();
    //assists = player - timeLeft(10 seconds)
    private final LinkedHashMap<WarlordsEntity, Integer> hitBy = new LinkedHashMap<>();
    private final LinkedHashMap<WarlordsEntity, Integer> healedBy = new LinkedHashMap<>();
    private final List<Location> locations = new ArrayList<>();
    private final Location deathLocation;
    private final CooldownManager cooldownManager = new CooldownManager(this);
    private Vector currentVector;
    private Team team;
    private float health;
    private float maxHealth;
    private float maxBaseHealth;
    private int regenTickTimer;
    private float regenTickTimerModifier = 1;
    private int respawnTickTimer = -1;
    private boolean dead = false;
    private float energy = 0;
    private float horseCooldown = 0;
    private float currentHealthModifier = 1;
    private int flagDropCooldown = 0;
    private int flagPickCooldown = 0;
    private int hitCooldown = 20;
    private int currency;
    private boolean wasSneaking = false;
    private int blocksTravelledCM = 0;
    private boolean noEnergyConsumption;
    private boolean disableCooldowns;
    private double energyModifier = 1;
    private double cooldownModifier = 1;
    private boolean takeDamage = true;
    private boolean canCrit = true;
    private boolean teamFlagCompass = true;
    @Nullable
    private FlagInfo carriedFlag = null;
    private boolean active = true;
    private boolean isInPve = false;
    private boolean showDebugMessage = false;
    private float bonusAgroWeight = 0;


    public WarlordsEntity(Player player, Specializations specialization) {
        this();
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.entity = player;
        this.specClass = specialization;
        this.spec = specialization.create.get();
    }

    public WarlordsEntity() {
        game = null;
        deathLocation = null;
    }

    /**
     * @param uuid
     * @param name
     * @param game      what game should the WarlordsPlayer be assigned to.
     * @param team      optional team parameter to assign the WarlordsPlayer to a team.
     * @param specClass
     * @param entity
     */
    public WarlordsEntity(
            @Nonnull UUID uuid,
            @Nonnull String name,
            @Nonnull LivingEntity entity,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull Specializations specClass
    ) {
        this.name = name;
        this.uuid = uuid;
        this.game = game;
        this.team = team;
        this.specClass = specClass;
        this.spec = specClass.create.get();
        this.maxHealth = this.spec.getMaxHealth();
        this.health = this.maxHealth;
        this.maxBaseHealth = this.maxHealth;
        this.speed = isInPve() ? new CalculateSpeed(this, this::setWalkSpeed,
                13,
                true
        ) : new CalculateSpeed(this, this::setWalkSpeed, 13);
        if (specClass == Specializations.APOTHECARY) {
            this.speed.addBaseModifier(10);
        }
        this.entity = entity;
        this.deathLocation = this.entity.getLocation();
        this.isInPve = com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode());
    }

    public boolean isInPve() {
        return isInPve;
    }

    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public String toString() {
        return "WarlordsPlayer{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    private void appendDebugMessageEvent(TextComponent.Builder debugMessage, WarlordsDamageHealingEvent event) {
        TextComponent grayBar = Component.text(" | ", NamedTextColor.GRAY);
        debugMessage.append(Component.newline())
                    .append(Component.text(" - ", NamedTextColor.GRAY));
        appendDebugMessage(debugMessage, "Self", this.getName(), false);
        debugMessage.append(grayBar);
        appendDebugMessage(debugMessage, "Attacker", event.getAttacker().getName(), false);
        debugMessage.append(grayBar);
        appendDebugMessage(debugMessage, "Ability", event.getAbility(), false);
        debugMessage.append(Component.newline())
                    .append(Component.text(" - ", NamedTextColor.GRAY));
        appendDebugMessage(debugMessage, "Min", event.getMin(), false);
        debugMessage.append(grayBar);
        appendDebugMessage(debugMessage, "Max", event.getMax(), false);
        debugMessage.append(Component.text("  |  ", NamedTextColor.GRAY));
        appendDebugMessage(debugMessage, "Crit Chance", event.getCritChance(), false);
        debugMessage.append(grayBar);
        appendDebugMessage(debugMessage, "Crit Multiplier", event.getCritMultiplier(), false);
        debugMessage.append(Component.newline())
                    .append(Component.text(" - ", NamedTextColor.GRAY));
        appendDebugMessage(debugMessage, "Ignore Reduction", "" + event.isIgnoreReduction(), false);
        debugMessage.append(grayBar);
        appendDebugMessage(debugMessage, "Flags", "" + event.getFlags(), false);
    }

    private Optional<WarlordsDamageHealingFinalEvent> addDamageHealingInstance(WarlordsDamageHealingEvent event) {
        if (isDead()) {
            return Optional.empty();
        }
        TextComponent.Builder debugMessage = Component.text().color(NamedTextColor.GREEN).append(Component.text("Pre Event:", NamedTextColor.AQUA));
        appendDebugMessageEvent(debugMessage, event);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        if (event.isHealingInstance()) {
            Optional<WarlordsDamageHealingFinalEvent> eventOptional = addHealingInstance(debugMessage, event);
            eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager()
                                                                             .callEvent(warlordsDamageHealingFinalEvent));
            return eventOptional;
        } else {
            Optional<WarlordsDamageHealingFinalEvent> eventOptional = addDamageInstance(debugMessage, event);
            eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager()
                                                                             .callEvent(warlordsDamageHealingFinalEvent));
            return eventOptional;
        }
    }

    /**
     * Adds a damage instance to an ability or a player.
     *
     * @param attacker        Assigns the damage value to the original caster.
     * @param ability         Name of the ability.
     * @param min             The minimum damage amount.
     * @param max             The maximum damage amount.
     * @param critChance      The critical chance of the damage instance.
     * @param critMultiplier  The critical multiplier of the damage instance.
     * @param ignoreReduction Whether the instance has to ignore damage reductions.
     */
    public Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction
    ) {
        return addDamageInstance(attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, EnumSet.noneOf(InstanceFlags.class), null);
    }

    public Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            UUID uuid
    ) {
        return addDamageInstance(attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, EnumSet.noneOf(InstanceFlags.class), uuid);
    }

    public Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            EnumSet<InstanceFlags> flags
    ) {
        return addDamageInstance(attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, flags, null);
    }

    public Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            EnumSet<InstanceFlags> flags,
            UUID uuid
    ) {
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(this,
                attacker,
                ability,
                min,
                max,
                critChance,
                critMultiplier,
                ignoreReduction,
                false,
                true,
                flags,
                uuid
        ));
    }

    private Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(TextComponent.Builder debugMessage, WarlordsDamageHealingEvent event) {
        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeVariableSetFromSelf(event);
        }
        for (AbstractCooldown<?> abstractCooldown : event.getAttacker().getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeVariableSetFromAttacker(event);
        }

        WarlordsEntity attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        float critChance = event.getCritChance();
        float critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();
        boolean isFallDamage = ability.equals("Fall");
        EnumSet<InstanceFlags> flags = event.getFlags();


        AtomicReference<WarlordsDamageHealingFinalEvent> finalEvent = new AtomicReference<>(null);
        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || !isActive()) {
            return Optional.empty();
        }

        debugMessage.append(Component.newline()).append(Component.text("Post Event:", NamedTextColor.AQUA));
        appendDebugMessageEvent(debugMessage, event);


        float initialHealth = health;

        List<AbstractCooldown<?>> selfCooldownsDistinct = getCooldownManager().getCooldownsDistinct();
        List<AbstractCooldown<?>> attackersCooldownsDistinct = attacker.getCooldownManager().getCooldownsDistinct();

        debugMessage.append(Component.newline()).append(Component.text("Before Reduction:", NamedTextColor.AQUA));
        appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Self Cooldowns");
        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.doBeforeReductionFromSelf(event);
            appendDebugMessage(debugMessage, 2, abstractCooldown);
        }
        appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Attacker Cooldowns");
        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.doBeforeReductionFromAttacker(event);
            appendDebugMessage(debugMessage, 2, abstractCooldown);
        }

        debugMessage.append(Component.newline()).append(Component.text("Crit Modifiers:", NamedTextColor.AQUA));
        appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Attacker Cooldowns");
        if (critChance > 0) {
            float previousCC = critChance;
            float previousCM = critMultiplier;
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                critChance = abstractCooldown.addCritChanceFromAttacker(event, critChance);
                critMultiplier = abstractCooldown.addCritMultiplierFromAttacker(event, critMultiplier);
                if (previousCC != critChance) {
                    appendDebugMessage(debugMessage, 2, "Crit Chance", critChance, abstractCooldown);
                }
                if (previousCM != critMultiplier) {
                    appendDebugMessage(debugMessage, 2, "Crit Multiplier", critMultiplier, abstractCooldown);
                }
                previousCC = critChance;
                previousCM = critMultiplier;
            }
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                critChance = abstractCooldown.setCritChanceFromAttacker(event, critChance);
                if (previousCC != critChance) {
                    appendDebugMessage(debugMessage, 2, "Crit Chance", critChance, abstractCooldown);
                }
                previousCC = critChance;
            }
        }
        //crit
        float damageValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;
        if (critChance > 0 && crit <= critChance && attacker.canCrit) {
            isCrit = true;
            damageValue *= critMultiplier / 100f;
        }
        debugMessage.append(Component.newline()).append(Component.text("Calculated Damage:", NamedTextColor.AQUA));
        appendDebugMessage(debugMessage, 1, "Damage Value", damageValue);
        appendDebugMessage(debugMessage, 1, "Crit", "" + isCrit);

        final float damageHealValueBeforeAllReduction = damageValue;
        if (!flags.contains(InstanceFlags.IGNORE_SELF_RES)) {
            debugMessage.append(Component.newline())
                        .append(Component.text("Spec Damage Reduction: ", NamedTextColor.AQUA))
                        .append(Component.text(spec.getDamageResistance(), NamedTextColor.BLUE));
            addAbsorbed(Math.abs(damageValue - (damageValue *= 1 - spec.getDamageResistance() / 100f)));
            appendDebugMessage(debugMessage, 1, "Damage Value", damageValue);
        }

        if (attacker == this && (isFallDamage || isMeleeHit)) {
            if (isMeleeHit) {
                // True damage
                sendTookDamageMessage(min, "melee damage");
                resetRegenTimer();
                if (health - min <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    entity.showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text("You took ", NamedTextColor.GRAY)
                                     .append(Component.text(Math.round(min), NamedTextColor.RED))
                                     .append(Component.text(" melee damage and died.")),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    health = 0;
                    die(attacker);
                } else {
                    health -= min;
                    playHurtAnimation(this.entity, attacker);
                }
            } else {
                // Fall Damage
                sendTookDamageMessage(damageValue, "fall damage");
                resetRegenTimer();
                if (health - damageValue <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    entity.showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text("You took ", NamedTextColor.GRAY)
                                     .append(Component.text(Math.round(min), NamedTextColor.RED))
                                     .append(Component.text(" fall damage and died.")),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    health = 0;
                    die(attacker);
                } else {
                    health -= damageValue;
                    playHurtAnimation(entity, attacker);
                }

                for (OrderOfEviscerate orderOfEviscerate : new CooldownFilter<>(attacker, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(OrderOfEviscerate.class)
                        .toList()
                ) {
                    orderOfEviscerate.addAndCheckDamageThreshold(damageValue, attacker);
                }

                addAbsorbed(Math.abs(damageValue * spec.getDamageResistance() / 100));
            }

            cancelHealingPowerUp();
            return Optional.empty();
        }
        float previousDamageValue = damageValue;
        // Reduction before Intervene.
        if (!ignoreReduction) {
            // Flag carrier multiplier.
            double flagMultiplier = getFlagDamageMultiplier();
            if (flagMultiplier != 1) {
                debugMessage.append(Component.newline()).append(Component.text("Flag Damage Multiplier:", NamedTextColor.AQUA));
            }
            damageValue *= flagMultiplier;
            if (flagMultiplier != 1) {
                appendDebugMessage(debugMessage, 1, "Damage Value", damageValue);
            }
            // Checks whether the player is standing in a Hammer of Light.
            if (HammerOfLight.notStandingInHammer(attacker, this)) {
                debugMessage.append(Component.newline()).append(Component.text("Before Intervene", NamedTextColor.AQUA));
                appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Self Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    damageValue = abstractCooldown.modifyDamageBeforeInterveneFromSelf(event, damageValue);
                    if (previousDamageValue != damageValue) {
                        appendDebugMessage(debugMessage, 2, "Damage Value", damageValue, abstractCooldown);
                    }
                    previousDamageValue = damageValue;
                }

                appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Attacker Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                    damageValue = abstractCooldown.modifyDamageBeforeInterveneFromAttacker(event, damageValue);
                    if (previousDamageValue != damageValue) {
                        appendDebugMessage(debugMessage, 2, "Damage Value", damageValue, abstractCooldown);
                    }
                    previousDamageValue = damageValue;
                }
            } else {
                debugMessage.append(Component.newline()).append(Component.text("In Hammer", NamedTextColor.RED));
            }
        }

        final float damageHealValueBeforeInterveneReduction = damageValue;
        // Intervene
        Optional<RegularCooldown> optionalInterveneCooldown = new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClass(Intervene.class)
                .filter(regularCooldown -> !Objects.equals(regularCooldown.getFrom(), this))
                .findFirst();
        if (optionalInterveneCooldown.isPresent() &&
                optionalInterveneCooldown.get().getTicksLeft() > 0 &&
                HammerOfLight.notStandingInHammer(attacker, this) &&
                isEnemy(attacker)
        ) {
            debugMessage.append(Component.newline()).append(Component.text("Intervene:", NamedTextColor.AQUA));

            Intervene intervene = (Intervene) optionalInterveneCooldown.get().getCooldownObject();
            WarlordsEntity intervenedBy = optionalInterveneCooldown.get().getFrom();
            damageValue *= (intervene.getDamageReduction() / 100f);
            appendDebugMessage(debugMessage, 1, "Damage Value", damageValue);
            intervenedBy.addAbsorbed(damageValue);
            intervenedBy.resetRegenTimer();
            intervene.addDamagePrevented(damageValue);
            // Break Intervene if above damage threshold
            if (intervene.getDamagePrevented() >= intervene.getMaxDamagePrevented() / 2f) {
                //defender
                new CooldownFilter<>(intervenedBy, RegularCooldown.class)
                        .filterCooldownObject(intervene)
                        .findFirst()
                        .ifPresent(regularCooldown -> regularCooldown.setTicksLeft(0));
                //vene target
                optionalInterveneCooldown.get().setTicksLeft(0);
                //remaining vene prevent damage
                float remainingVeneDamage = (intervene.getMaxDamagePrevented() / 2) - (intervene.getDamagePrevented() - damageValue);
                intervenedBy.addDamageInstance(attacker,
                        "Intervene",
                        remainingVeneDamage,
                        remainingVeneDamage,
                        isCrit ? 100 : 0,
                        100,
                        true
                );
                //extra overVeneDamage to target
                float overVeneDamage = intervene.getDamagePrevented() - intervene.getMaxDamagePrevented() / 2f;
                addDamageInstance(attacker, ability, overVeneDamage, overVeneDamage, isCrit ? 100 : 0, 100, true)
                        .ifPresent(finalEvent::set);
            } else {
                intervenedBy.addDamageInstance(attacker,
                        "Intervene",
                        damageValue,
                        damageValue,
                        isCrit ? 100 : 0,
                        100,
                        false
                );
                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        this,
                        attacker,
                        ability,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        0,
                        0,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true
                ));
            }

            Location loc = getLocation();
            //EFFECTS + SOUNDS
            Utils.playGlobalSound(loc, "warrior.intervene.block", 2, 1);
            playHitSound(attacker);
            entity.playEffect(EntityEffect.HURT);
            intervenedBy.getEntity().playEffect(EntityEffect.HURT);
            EffectUtils.playParticleLinkAnimation(getLocation(), intervenedBy.getLocation(), 255, 0, 0, 2);
            // Remove horses.
            removeHorse();
            intervenedBy.removeHorse();

            appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Intervene From Attacker");
            for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                abstractCooldown.onInterveneFromAttacker(event, damageValue);
                appendDebugMessage(debugMessage, 2, abstractCooldown);
            }
        } else {
            // Damage reduction after Intervene
            if (!ignoreReduction) {
                if (HammerOfLight.notStandingInHammer(attacker, this)) {
                    // Damage Reduction
                    // Example: .8 = 20% reduction.
                    debugMessage.append(Component.newline()).append(Component.text("After Intervene:", NamedTextColor.AQUA));
                    appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Self Cooldowns");
                    for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                        damageValue = abstractCooldown.modifyDamageAfterInterveneFromSelf(event, damageValue);
                        if (previousDamageValue != damageValue) {
                            appendDebugMessage(debugMessage, 2, "Damage Value", damageValue, abstractCooldown);
                        }
                        previousDamageValue = damageValue;
                    }

                    appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Attackers Cooldowns");
                    for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                        damageValue = abstractCooldown.modifyDamageAfterInterveneFromAttacker(event, damageValue);
                        if (previousDamageValue != damageValue) {
                            appendDebugMessage(debugMessage, 2, "Damage Value", damageValue, abstractCooldown);
                        }
                        previousDamageValue = damageValue;
                    }
                } else {
                    debugMessage.append(Component.newline()).append(Component.text("In Hammer", NamedTextColor.RED));
                }
            }

            final float damageHealValueBeforeShieldReduction = damageValue;
            // Arcane Shield
            Optional<RegularCooldown> arcaneShieldCooldown = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClass(ArcaneShield.class)
                    .filter(RegularCooldown::hasTicksLeft)
                    .findFirst();
            if (arcaneShieldCooldown.isPresent() &&
                    isEnemy(attacker) &&
                    HammerOfLight.notStandingInHammer(attacker, this)
            ) {
                debugMessage.append(Component.newline()).append(Component.text("Arcane Shield:", NamedTextColor.AQUA));

                RegularCooldown cooldown = arcaneShieldCooldown.get();
                ArcaneShield arcaneShield = (ArcaneShield) cooldown.getCooldownObject();
                //adding dmg to shield
                arcaneShield.addShieldHealth(-damageValue);
                //check if broken
                if (arcaneShield.getShieldHealth() <= 0) {
                    cooldown.setTicksLeft(0);
                    addDamageInstance(Component.text(), new WarlordsDamageHealingEvent(
                            this,
                            attacker,
                            ability,
                            -arcaneShield.getShieldHealth(),
                            -arcaneShield.getShieldHealth(),
                            isCrit ? 100 : 0,
                            1,
                            false,
                            true,
                            true,
                            EnumSet.noneOf(InstanceFlags.class)
                    ));

                    addAbsorbed(-(arcaneShield.getShieldHealth()));

                    doOnStaticAbility(ArcaneShield.class, ArcaneShield::addTimesBroken);
                    return Optional.empty();
                } else {
                    if (entity instanceof Player) {
                        entity.setAbsorptionAmount((float) (arcaneShield.getShieldHealth() / (maxHealth * .5) * 20));
                    }

                    if (isMeleeHit) {
                        sendMessage(RECEIVE_ARROW_RED.append(Component.text(" You absorbed " + attacker.getName() + "'s melee hit.",
                                NamedTextColor.GRAY
                        )));
                        attacker.sendMessage(GIVE_ARROW_GREEN.append(Component.text(" Your melee hit was absorbed by " + name + ".", NamedTextColor.GRAY)));
                    } else {
                        sendMessage(RECEIVE_ARROW_RED.append(Component.text(" You absorbed " + attacker.getName() + "'s " + ability + " hit.",
                                NamedTextColor.GRAY
                        )));
                        attacker.sendMessage(GIVE_ARROW_GREEN.append(Component.text(" Your " + ability + " was absorbed by " + name + ".",
                                NamedTextColor.GRAY
                        )));
                    }

                    addAbsorbed(Math.abs(damageHealValueBeforeAllReduction));
                }

                appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "On Shield");
                appendDebugMessage(debugMessage, 2, NamedTextColor.DARK_GREEN, "Self Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    abstractCooldown.onShieldFromSelf(event, damageValue, isCrit);
                    appendDebugMessage(debugMessage, 3, abstractCooldown);
                }

                appendDebugMessage(debugMessage, 2, NamedTextColor.DARK_GREEN, "Attackers Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                    abstractCooldown.onShieldFromAttacker(event, damageValue, isCrit);
                    appendDebugMessage(debugMessage, 3, abstractCooldown);
                }

                playHurtAnimation(this.entity, attacker);

                if (!isMeleeHit) {
                    playHitSound(attacker);
                }
                removeHorse();

                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        this,
                        attacker,
                        ability,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        damageHealValueBeforeShieldReduction,
                        damageValue,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true
                ));
                secondStats.addDamageHealingEventAsSelf(finalEvent.get());
                attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent.get());

                if (shouldCheckForAchievements()) {
                    checkForAchievementsDamage();
                }
                if (attacker.shouldCheckForAchievements()) {
                    checkForAchievementsDamageAttacker(attacker);
                }
            } else {
                debugMessage.append(Component.newline()).append(Component.text("Modify Damage After All:", NamedTextColor.AQUA));
                appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Self Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    damageValue = abstractCooldown.modifyDamageAfterAllFromSelf(event, damageValue, isCrit);
                    if (previousDamageValue != damageValue) {
                        appendDebugMessage(debugMessage, 2, "Damage Value", damageValue, abstractCooldown);
                    }
                    previousDamageValue = damageValue;
                }

                boolean debt = getCooldownManager().hasCooldownFromName("Spirits Respite");
                //if (isEnemy(attacker)) {
                hitBy.put(attacker, 10);
                cancelHealingPowerUp();
                removeHorse();

                float finalDamageValue = damageValue;
                doOnStaticAbility(SoulShackle.class, soulShackle -> soulShackle.addToShacklePool(finalDamageValue));

                sendDamageMessage(debugMessage, attacker, this, ability, damageValue, isCrit, isMeleeHit);

                //debugMessage.append("\n").append(ChatColor.AQUA).append("On Damage");
                //appendDebugMessage(debugMessage, 1, ChatColor.DARK_GREEN, "Self Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
                    abstractCooldown.onDamageFromSelf(event, damageValue, isCrit);
                    //appendDebugMessage(debugMessage, 2, abstractCooldown);
                }

                //appendDebugMessage(debugMessage, 1, ChatColor.DARK_GREEN, "Attackers Cooldowns");
                for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
                    abstractCooldown.onDamageFromAttacker(event, damageValue, isCrit);
                    //appendDebugMessage(debugMessage, 2, abstractCooldown);
                }
                //}

                resetRegenTimer();
                updateHealth();

                // debt and healing
                if (!debt && takeDamage) {
                    if (this.health - damageValue > maxHealth) {
                        this.health = maxHealth;
                    } else {
                        this.health -= damageValue;
                    }
                }

                attacker.addDamage(damageValue, FlagHolder.isPlayerHolderFlag(this));
                this.addDamageTaken(damageValue);
                playHurtAnimation(this.entity, attacker);
                if (attacker.isNoEnergyConsumption()) {
                    attacker.getRecordDamage().add(damageValue);
                }

                finalEvent.set(new WarlordsDamageHealingFinalEvent(
                        event,
                        this,
                        attacker,
                        ability,
                        initialHealth,
                        damageHealValueBeforeAllReduction,
                        damageHealValueBeforeInterveneReduction,
                        damageHealValueBeforeShieldReduction,
                        damageValue,
                        critChance,
                        critMultiplier,
                        isCrit,
                        true
                ));
                secondStats.addDamageHealingEventAsSelf(finalEvent.get());
                attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent.get());
                if (shouldCheckForAchievements()) {
                    checkForAchievementsDamage();
                }
                if (attacker.shouldCheckForAchievements()) {
                    checkForAchievementsDamageAttacker(attacker);
                }
                // The player died.
                if (this.health <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    if (attacker.entity instanceof Player) {
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 1);
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 500f, 0.5f);
                    }

                    attacker.addKill();

                    game.forEachOnlinePlayer((p, t) -> {
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                        if (playerSettings.getChatKillsMode() == Settings.ChatSettings.ChatKills.ALL) {
                            if (p == this.entity) {
                                sendMessage(Component.text("You were killed by ", NamedTextColor.GRAY)
                                                     .append(attacker.getColoredName()));
                            } else if (p == attacker.entity) {
                                attacker.sendMessage(Component.text("You killed ", NamedTextColor.GRAY)
                                                              .append(getColoredName()));
                            } else {
                                p.sendMessage(getColoredName()
                                        .append(Component.text(" was killed by ", NamedTextColor.GRAY))
                                        .append(attacker.getColoredName()));
                            }
                        }
                    });

                    for (WarlordsEntity enemy : PlayerFilter.playingGame(game)
                                                            .enemiesOf(this)
                                                            .stream().toList()
                    ) {
                        for (AbstractCooldown<?> abstractCooldown : enemy.getCooldownManager().getCooldownsDistinct()) {
                            abstractCooldown.onDeathFromEnemies(event, damageValue, isCrit, enemy == attacker);
                        }
                    }
                    entity.showTitle(Title.title(
                            Component.text("YOU DIED!", NamedTextColor.RED),
                            Component.text(attacker.getName() + " killed you.", NamedTextColor.GRAY),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                    die(attacker);
                } else {
                    if (!isMeleeHit && this != attacker && damageValue != 0) {
                        playHitSound(attacker);
                    }
                }
            }
        }

        for (AbstractCooldown<?> abstractCooldown : selfCooldownsDistinct) {
            abstractCooldown.onEndFromSelf(event, damageValue, isCrit);
        }

        for (AbstractCooldown<?> abstractCooldown : attackersCooldownsDistinct) {
            abstractCooldown.onEndFromAttacker(event, damageValue, isCrit);
        }

        return Optional.ofNullable(finalEvent.get());
    }

    private void sendTookDamageMessage(float damage, String from) {
        if (getEntity() instanceof Player) {
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(getUuid());
            if (playerSettings.getChatDamageMode() == Settings.ChatSettings.ChatDamage.ALL) {
                sendMessage(RECEIVE_ARROW_RED
                        .append(Component.text("You took ", NamedTextColor.GRAY))
                        .append(Component.text(Math.round(damage), NamedTextColor.RED))
                        .append(Component.text(" " + from + ".", NamedTextColor.GRAY))
                );
            }
        }
    }


    /**
     * Adds a healing instance to an ability or a player.
     *
     * @param attacker              Assigns the damage value to the original caster.
     * @param ability               Name of the ability.
     * @param min                   The minimum healing amount.
     * @param max                   The maximum healing amount.
     * @param critChance            The critical chance of the damage instance.
     * @param critMultiplier        The critical multiplier of the damage instance.
     * @param ignoreReduction       Whether the instance has to ignore damage reductions.
     * @param isLastStandFromShield Whether the instance if from last stand and absorbed healing
     */
    public Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            boolean isLastStandFromShield
    ) {
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(
                        this,
                        attacker,
                        ability,
                        min,
                        max,
                        critChance,
                        critMultiplier,
                        ignoreReduction,
                        isLastStandFromShield,
                        false,
                        EnumSet.noneOf(InstanceFlags.class)
                )
        );
    }

    public Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            boolean isLastStandFromShield,
            EnumSet<InstanceFlags> flags
    ) {
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(
                        this,
                        attacker,
                        ability,
                        min, max,
                        critChance,
                        critMultiplier,
                        ignoreReduction,
                        isLastStandFromShield,
                        false,
                        flags
                )
        );
    }

    private Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(TextComponent.Builder debugMessage, WarlordsDamageHealingEvent event) {
        WarlordsEntity attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        float critChance = event.getCritChance();
        float critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();

        WarlordsDamageHealingFinalEvent finalEvent;
        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || !isActive()) {
            return Optional.empty();
        }

        debugMessage.append(Component.newline()).append(Component.text("Post Event:", NamedTextColor.AQUA));
        appendDebugMessageEvent(debugMessage, event);

        float initialHealth = health;
        // Critical Hits
        float healValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;

        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            healValue *= critMultiplier / 100f;
        }
        debugMessage.append(Component.newline()).append(Component.text("Calculated Heal:", NamedTextColor.AQUA));
        appendDebugMessage(debugMessage, 1, "Heal Value", healValue);
        appendDebugMessage(debugMessage, 1, "Crit", "" + isCrit);

        final float healValueBeforeReduction = healValue;
        float previousHealValue = healValue;

        debugMessage.append(Component.newline()).append(Component.text("Before Heal", NamedTextColor.AQUA));
        appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Self Cooldowns");
        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            healValue = abstractCooldown.doBeforeHealFromSelf(event, healValue);
            if (previousHealValue != healValue) {
                appendDebugMessage(debugMessage, 2, "Heal Value", healValue, abstractCooldown);
            }
            previousHealValue = healValue;
        }

        appendDebugMessage(debugMessage, 1, NamedTextColor.DARK_GREEN, "Attackers Cooldowns");
        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            healValue = abstractCooldown.doBeforeHealFromAttacker(event, healValue);
            if (previousHealValue != healValue) {
                appendDebugMessage(debugMessage, 2, "Heal Value", healValue, abstractCooldown);
            }
            previousHealValue = healValue;
        }

        // Self Healing
        if (this == attacker) {

            if (this.health + healValue > this.maxHealth) {
                healValue = this.maxHealth - this.health;
            }

            if (healValue <= 0) {
                return Optional.empty();
            }

            // Displays the healing message.
            sendHealingMessage(debugMessage, this, healValue, ability, isCrit, isLastStandFromShield, false);
            health += healValue;
            addHealing(healValue, FlagHolder.isPlayerHolderFlag(this));

            if (!isMeleeHit && !ability.equals("Healing Rain") && !ability.equals("Blood Lust")) {
                playHitSound(attacker);
            }
        } else {
            // Teammate Healing
            if (isTeammate(attacker)) {
                float maxHealth = this.maxHealth;
                if (ability.equals("Water Bolt") || ability.equals("Water Breath") || ability.equals("Healing Rain")) {
                    maxHealth *= 1.1;
                }

                if (this.health + healValue > maxHealth) {
                    healValue = maxHealth - this.health;
                }

                if (healValue <= 0) {
                    return Optional.empty();
                }

                boolean isOverheal = maxHealth > this.maxHealth && healValue + this.health > this.maxHealth;
                sendHealingMessage(debugMessage, attacker, this, healValue, ability, isCrit, isLastStandFromShield, isOverheal);

                health += healValue;
                attacker.addHealing(healValue, FlagHolder.isPlayerHolderFlag(this));

                if (!isMeleeHit && !ability.equals("Healing Rain")) {
                    playHitSound(attacker);
                }
            }
        }

        finalEvent = new WarlordsDamageHealingFinalEvent(
                event,
                this,
                attacker,
                ability,
                initialHealth,
                healValueBeforeReduction,
                healValueBeforeReduction,
                healValueBeforeReduction,
                healValue,
                critChance,
                critMultiplier,
                isCrit,
                false
        );
        secondStats.addDamageHealingEventAsSelf(finalEvent);
        attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
        if (shouldCheckForAchievements()) {
            checkForAchievementsHealing();
        }
        if (attacker.shouldCheckForAchievements()) {
            checkForAchievementsHealingAttacker(attacker);
        }

        return Optional.of(finalEvent);
    }

    /**
     * @param player                which player should receive the message.
     * @param healValue             heal value of the message.
     * @param ability               which ability should the message display.
     * @param isCrit                whether if it's a critical hit message.
     * @param isLastStandFromShield whether the message is last stand healing.
     */
    private void sendHealingMessage(
            TextComponent.Builder debugMessage,
            @Nonnull WarlordsEntity player,
            float healValue,
            String ability,
            boolean isCrit,
            boolean isLastStandFromShield,
            boolean isOverHeal
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder healBuilder = Component.text().color(NamedTextColor.GREEN);
        if (isCrit) {
            healBuilder.decorate(TextDecoration.BOLD);
        }
        healBuilder.append(Component.text(Math.round(healValue)));
        healBuilder.append(Component.text(isCrit ? "!" : ""));
        if (isLastStandFromShield) {
            healBuilder.append(Component.text(" Absorbed!"));
        }
        secondHalf.append(healBuilder);
        secondHalf.append(Component.text(" health."));

        // Own Message
        TextComponent.Builder hitBuilder = Component.text(" Your " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        hitBuilder.append(Component.text(" healed you for "));

        TextComponent.Builder ownFeed = Component.text()
                                                 .append(GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUuid(), player.getEntity() instanceof Player);
        switch (playerSettings.getChatHealingMode()) {
            case ALL -> {
                if (player.showDebugMessage) {
                    player.sendMessage(ownFeed.build()
                                              .hoverEvent(HoverEvent.showText(debugMessage))
                    );
                } else {
                    player.sendMessage(ownFeed.build());
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (player.showDebugMessage) {
                        player.sendMessage(ownFeed.build()
                                                  .hoverEvent(HoverEvent.showText(debugMessage))
                        );
                    } else {
                        player.sendMessage(ownFeed.build());
                    }
                }
            }
        }
    }

    /**
     * @param sender                which player sends the message.
     * @param receiver              which player receives the message.
     * @param healValue             heal value of the message.
     * @param ability               which ability should the message display.
     * @param isCrit                whether if it's a critical hit message.
     * @param isLastStandFromShield whether the message is last stand healing.
     * @param isOverHeal            whether the message is overhealing.
     */
    private void sendHealingMessage(
            TextComponent.Builder debugMessage,
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            float healValue, String ability,
            boolean isCrit,
            boolean isLastStandFromShield,
            boolean isOverHeal
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder healBuilder = Component.text().color(NamedTextColor.GREEN);
        if (isCrit) {
            healBuilder.decorate(TextDecoration.BOLD);
        }
        healBuilder.append(Component.text(Math.round(healValue)));
        healBuilder.append(Component.text(isCrit ? "!" : ""));
        if (isLastStandFromShield) {
            healBuilder.append(Component.text(" Absorbed!"));
        }
        secondHalf.append(healBuilder);
        secondHalf.append(Component.text(" health."));

        // Own Message
        TextComponent.Builder hitBuilder = Component.text(" Your " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed " + name + " for "));
        } else {
            hitBuilder.append(Component.text(" healed " + name + " for "));
        }

        TextComponent.Builder ownFeed = Component.text()
                                                 .append(GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(sender.getUuid(), sender.getEntity() instanceof Player);
        switch (playerSettings.getChatHealingMode()) {
            case ALL -> {
                if (sender.showDebugMessage) {
                    sender.sendMessage(ownFeed.build()
                                              .hoverEvent(HoverEvent.showText(debugMessage.build()))
                    );
                } else {
                    sender.sendMessage(ownFeed.build());
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (sender.showDebugMessage) {
                        sender.sendMessage(ownFeed.build()
                                                  .hoverEvent(HoverEvent.showText(debugMessage.build()))
                        );
                    } else {
                        sender.sendMessage(ownFeed.build());
                    }
                }
            }
        }

        // Ally Message
        hitBuilder = Component.text(" " + sender.getName() + "'s " + ability, NamedTextColor.GRAY).toBuilder();
        if (isCrit) {
            hitBuilder.append(Component.text(" critically"));
        }
        if (isOverHeal) {
            hitBuilder.append(Component.text(" overhealed you for "));
        } else {
            hitBuilder.append(Component.text(" healed you for "));
        }

        TextComponent.Builder allyFeed = Component.text()
                                                  .append(RECEIVE_ARROW_GREEN)
                                                  .append(hitBuilder)
                                                  .append(secondHalf);

        playerSettings = PlayerSettings.getPlayerSettings(receiver.getUuid(), receiver.getEntity() instanceof Player);
        switch (playerSettings.getChatHealingMode()) {
            case ALL -> {
                if (receiver.showDebugMessage) {
                    receiver.sendMessage(allyFeed.build()
                                                 .hoverEvent(HoverEvent.showText(debugMessage.build()))
                    );
                } else {
                    receiver.sendMessage(allyFeed.build());
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (receiver.showDebugMessage) {
                        receiver.sendMessage(allyFeed.build()
                                                     .hoverEvent(HoverEvent.showText(debugMessage.build()))
                        );
                    } else {
                        receiver.sendMessage(allyFeed.build());
                    }
                }
            }
        }

    }

    /**
     * @param sender      which player sends the message.
     * @param receiver    which player should receive the message.
     * @param ability     what is the damage ability.
     * @param damageValue what is the damage value.
     * @param isCrit      whether if it's a critical hit message.
     * @param isMeleeHit  whether if it's a melee hit.
     */
    private void sendDamageMessage(
            TextComponent.Builder debugMessage,
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            String ability,
            float damageValue,
            boolean isCrit,
            boolean isMeleeHit
    ) {
        TextComponent.Builder secondHalf = Component.text().color(NamedTextColor.GRAY);
        TextComponent.Builder damageBuilder = Component.text().color(NamedTextColor.RED);
        if (isCrit) {
            damageBuilder.decorate(TextDecoration.BOLD);
        }
        damageBuilder.append(Component.text(Math.round(damageValue)));
        if (isCrit) {
            damageBuilder.append(Component.text("! "));
        }
        secondHalf.append(damageBuilder);
        if (isCrit) {
            secondHalf.append(Component.text("critical"));
        }
        if (isMeleeHit) {
            secondHalf.append(Component.text(" melee"));
        }
        secondHalf.append(Component.text(" damage."));

        // Receiver feed
        TextComponent.Builder hitBuilder = Component.text(" " + sender.getName(), NamedTextColor.GRAY).toBuilder();
        if (!isMeleeHit) {
            hitBuilder.append(Component.text("'s " + ability));
        }
        hitBuilder.append(Component.text(" hit you for "));
        TextComponent.Builder enemyFeed = Component.text()
                                                   .append(RECEIVE_ARROW_RED)
                                                   .append(hitBuilder)
                                                   .append(secondHalf);

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(receiver.getUuid(), receiver.getEntity() instanceof Player);
        switch (playerSettings.getChatDamageMode()) {
            case ALL -> {
                if (receiver.showDebugMessage) {
                    receiver.sendMessage(enemyFeed.build()
                                                  .hoverEvent(HoverEvent.showText(debugMessage.build()))
                    );
                } else {
                    receiver.sendMessage(enemyFeed.build());
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (receiver.showDebugMessage) {
                        receiver.sendMessage(enemyFeed.build()
                                                      .hoverEvent(HoverEvent.showText(debugMessage.build()))
                        );
                    } else {
                        receiver.sendMessage(enemyFeed.build());
                    }
                }
            }
        }

        // Sender feed
        hitBuilder = Component.text(" ", NamedTextColor.GRAY).toBuilder();
        if (isMeleeHit) {
            hitBuilder.append(Component.text("You hit "));
        } else {
            hitBuilder.append(Component.text("Your " + ability + " hit "));
        }
        hitBuilder.append(Component.text(name + " for "));
        TextComponent.Builder ownFeed = Component.text()
                                                 .append(GIVE_ARROW_GREEN)
                                                 .append(hitBuilder)
                                                 .append(secondHalf);

        playerSettings = PlayerSettings.getPlayerSettings(sender.getUuid(), sender.getEntity() instanceof Player);
        switch (playerSettings.getChatDamageMode()) {
            case ALL -> {
                if (sender.showDebugMessage) {
                    sender.sendMessage(ownFeed.build()
                                              .hoverEvent(HoverEvent.showText(debugMessage.build()))
                    );
                } else {
                    sender.sendMessage(ownFeed.build());
                }
            }
            case CRITS_ONLY -> {
                if (isCrit) {
                    if (sender.showDebugMessage) {
                        sender.sendMessage(ownFeed.build()
                                                  .hoverEvent(HoverEvent.showText(debugMessage.build()))
                        );
                    } else {
                        sender.sendMessage(ownFeed.build());
                    }
                }
            }
        }

    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, String title, String value) {
        appendDebugMessage(debugMessage, title, value, true);
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, String title, String value, boolean separator) {
        if (separator) {
            debugMessage.append(Component.newline()).append(Component.text(" - ", NamedTextColor.GRAY));
        }
        debugMessage.append(Component.text(title + ": ", NamedTextColor.GREEN)).append(Component.text(value, NamedTextColor.GOLD));
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, String title, float value) {
        appendDebugMessage(debugMessage, title, value, true);
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, String title, float value, boolean separator) {
        appendDebugMessage(debugMessage, title, NumberFormat.addCommaAndRoundHundredths(value), separator);
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, String title, String value, AbstractCooldown<?> cooldown) {
        appendDebugMessage(debugMessage, level, NamedTextColor.GREEN, title);
        debugMessage.append(Component.text(": "))
                    .append(Component.text(value, NamedTextColor.GOLD))
                    .append(Component.text(" (", NamedTextColor.DARK_GRAY))
                    .append(Component.text(cooldown.getName(), NamedTextColor.GRAY))
                    .append(Component.text(")", NamedTextColor.DARK_GRAY));
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, String title, float value, AbstractCooldown<?> cooldown) {
        appendDebugMessage(debugMessage, level, title, NumberFormat.addCommaAndRoundHundredths(value), cooldown);
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, String title, String value) {
        appendDebugMessage(debugMessage, level, NamedTextColor.GREEN, title);
        debugMessage.append(Component.text(": "))
                    .append(Component.text(value, NamedTextColor.GOLD));
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, String title, float value) {
        appendDebugMessage(debugMessage, level, title, NumberFormat.addCommaAndRoundHundredths(value));
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, String title) {
        appendDebugMessage(debugMessage, level, NamedTextColor.GREEN, title);
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, NamedTextColor textColor, String title) {
        debugMessage.append(Component.newline())
                    .append(Component.text(" ".repeat(Math.max(0, level == 1 ? 1 : level * 2)) + " - ", NamedTextColor.GRAY))
                    .append(Component.text(title, textColor));
    }

    private void appendDebugMessage(TextComponent.Builder debugMessage, int level, AbstractCooldown<?> cooldown) {
        appendDebugMessage(debugMessage, level, NamedTextColor.GREEN, cooldown.getName());
    }

    /**
     * @param attacker which player should hear the hitsound?
     */
    private void playHitSound(WarlordsEntity attacker) {
        if (attacker.entity instanceof Player) {
            ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    /**
     * @param entity     which entity is assigned to the hurt animation?
     * @param hurtPlayer what warlords player should play the hurt animation?
     */
    private void playHurtAnimation(LivingEntity entity, WarlordsEntity hurtPlayer) {
        entity.playEffect(EntityEffect.HURT);
        for (Player player1 : hurtPlayer.getWorld().getPlayers()) {
            player1.playSound(entity.getLocation(), Sound.ENTITY_PLAYER_HURT, 2, 1);
        }
    }

    public void cancelHealingPowerUp() {
        if (this.getCooldownManager().hasCooldown(HealingPowerup.class)) {
            this.getCooldownManager().removeCooldown(HealingPowerup.class, false);
        }
    }

    public void removeHorse() {
        if (entity.getVehicle() != null) {
            entity.getVehicle().remove();
        }
    }

    public void die(@Nullable WarlordsEntity attacker) {
        dead = true;

        removeHorse();

        getLocation(this.deathLocation);

        showDeathAnimation();

        cooldownManager.clearCooldowns();

        if (attacker != null) {
            if (attacker != this) {
                hitBy.putAll(attacker.getHealedBy());
            }
            hitBy.remove(attacker);
        }

        this.addDeath();
        FlagHolder.dropFlagForPlayer(this);

        if (entity instanceof Player player) {
            player.setGameMode(GameMode.SPECTATOR);
            //removing yellow hearts
            entity.setAbsorptionAmount(0);
            ItemStack item = player.getInventory().getItem(0);
            //removing sg shiny weapon
            if (item != null) {
                item.removeEnchantment(Enchantment.OXYGEN);
            }
            //removing boner
            player.getInventory().remove(UndyingArmy.BONE);
        }
        Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this, attacker));

        //giving out assists
        hitBy.forEach((assisted, value) -> {
            if (attacker == assisted || attacker == this) {
                assisted.sendMessage(Component.text("You assisted in killing ", NamedTextColor.GRAY)
                                              .append(getColoredName())
                );
            } else {
                if (attacker != null) {
                    assisted.sendMessage(Component.text("You assisted ", NamedTextColor.GRAY)
                                                  .append(attacker.getColoredName())
                                                  .append(Component.text(" in killing "))
                                                  .append(getColoredName()));
                }
            }
            assisted.addAssist();
        });
        hitBy.clear();
        regenTickTimer = 0;
        heal();
    }

    protected boolean shouldCheckForAchievements() {
        return false;
    }

    private void checkForAchievementsDamage() {
        for (ChallengeAchievements challengeAchievements : ChallengeAchievements.DAMAGE_ACHIEVEMENTS_SELF) {
            ChallengeAchievements.checkForAchievement(this, challengeAchievements);
        }
        if (hasFlag()) {

        }
    }

    private void checkForAchievementsDamageAttacker(WarlordsEntity attacker) {
        for (ChallengeAchievements challengeAchievements : ChallengeAchievements.DAMAGE_ACHIEVEMENTS_ATTACKER) {
            ChallengeAchievements.checkForAchievement(attacker, challengeAchievements);
        }
        if (hasFlag()) {
            for (ChallengeAchievements challengeAchievements : ChallengeAchievements.DAMAGE_ACHIEVEMENTS_ATTACKER_FLAG) {
                ChallengeAchievements.checkForAchievement(attacker, challengeAchievements);
            }
        }
    }

    private void checkForAchievementsHealing() {
        if (hasFlag()) {

        }
    }

    private void checkForAchievementsHealingAttacker(WarlordsEntity attacker) {
        for (ChallengeAchievements challengeAchievements : ChallengeAchievements.HEALING_ACHIEVEMENTS_ATTACKER) {
            ChallengeAchievements.checkForAchievement(attacker, challengeAchievements);
        }
        if (hasFlag()) {
            for (ChallengeAchievements challengeAchievements : ChallengeAchievements.HEALING_ACHIEVEMENTS_ATTACKER_FLAG) {
                ChallengeAchievements.checkForAchievement(attacker, challengeAchievements);
            }
        }
    }

    public abstract void updateHealth();

    /**
     * Performs consumer action on WarlordsPlayers static (not the temp ones made on onActivate()) spec abilities that matches the given class.
     * Player specs and their abilities are unknown at this point.
     *
     * @param abilityClass The class of the ability.
     * @param consumer     What to perform on the ability
     * @param <T>          The type of the ability.
     */
    public <T extends AbstractAbility> void doOnStaticAbility(Class<T> abilityClass, Consumer<T> consumer) {
        for (AbstractAbility ability : spec.getAbilities()) {
            if (ability.getClass().equals(abilityClass)) {
                consumer.accept(abilityClass.cast(ability));
                break;
            }
        }
    }

    public void displayActionBar() {
        TextComponent.Builder actionBarMessage = Component.text("HP: ", NamedTextColor.GOLD, TextDecoration.BOLD)
                                                          .toBuilder();
        TextComponent.Builder healthBuilder = Component.text().decorate(TextDecoration.BOLD);
        float healthRatio = health / maxHealth;
        if (healthRatio > 1) {
            healthBuilder.color(NamedTextColor.GREEN);
        } else if (healthRatio >= .75) {
            healthBuilder.color(NamedTextColor.DARK_GREEN);
        } else if (healthRatio >= .25) {
            healthBuilder.color(NamedTextColor.YELLOW);
        } else {
            healthBuilder.color(NamedTextColor.RED);
        }
        int maxHealthRounded = Math.round(maxHealth);
        int maxBaseHealthRounded = Math.round(maxBaseHealth);
        healthBuilder.append(Component.text(Math.round(health)))
                     .append(Component.text("/", NamedTextColor.GOLD))
                     .append(Component.text(maxHealthRounded + "    ", maxHealthRounded > maxBaseHealthRounded ? NamedTextColor.YELLOW : NamedTextColor.GOLD));
        actionBarMessage.append(healthBuilder);
        actionBarMessage.append(team.boldColoredPrefix().append(Component.text(" TEAM  ")));
        for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldowns()) {
            if (abstractCooldown.getNameAbbreviation() != null) {
                actionBarMessage.append(abstractCooldown.getNameAbbreviation());
            }
        }
        entity.sendActionBar(actionBarMessage.build());
    }

    public void displayFlagActionBar(@Nonnull Player player) {
        if (this.compassTarget != null) {
            player.sendActionBar(this.compassTarget.getToolbarName(this));
        } else {
            player.sendActionBar(Component.empty());
        }
    }

    public void updateArmor() {
        if (!(this.entity instanceof Player player)) {
            return;
        }

        if (!cooldownManager.hasCooldownFromName("Cloaked") || hasFlag()) {
            if (this instanceof WarlordsPlayer) {
                ArmorManager.resetArmor(player, (WarlordsPlayer) this);
            }

            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            for (Player otherPlayer : player.getWorld().getPlayers()) {
                otherPlayer.showPlayer(Warlords.getInstance(), player);
            }
        }

        if (hasFlag()) {
            ItemStack item = new ItemStack(getTeam() == Team.RED ? Material.RED_BANNER : Material.BLUE_BANNER);
            BannerMeta banner = (BannerMeta) item.getItemMeta();
            banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
            banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
            item.setItemMeta(banner);
            player.getInventory().setHelmet(item);
        }
    }

    public boolean hasFlag() {
        return FlagHolder.isPlayerHolderFlag(this);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Specializations getSpecClass() {
        return specClass;
    }

    public void updateItems() {
        if (entity instanceof Player player) {
            updateRedItem(player);
            updatePurpleItem(player);
            updateBlueItem(player);
            updateOrangeItem(player);
        }
    }

    public void updateRedItem(Player player) {
        updateItem(player, 1, spec.getRed(), RED_ABILITY);
    }

    public void updatePurpleItem(Player player) {
        updateItem(player, 2, spec.getPurple(), PURPLE_ABILITY);
    }

    public void updateBlueItem(Player player) {
        updateItem(player, 3, spec.getBlue(), BLUE_ABILITY);
    }

    public void updateOrangeItem(Player player) {
        updateItem(player, 4, spec.getOrange(), ORANGE_ABILITY);
    }

    public void updateItem(Player player, int slot, AbstractAbility ability, ItemStack item) {
        if (ability.getCurrentCooldown() > 0) {
            ItemBuilder cooldown = new ItemBuilder(Material.GRAY_DYE, ability.getCurrentCooldownItem())
                    .flags(ItemFlag.HIDE_ENCHANTS);
            if (!ability.getSecondaryAbilities().isEmpty()) {
                cooldown.enchant(Enchantment.OXYGEN, 1);
            }
            player.getInventory().setItem(slot, cooldown.get());
        } else {
            player.getInventory().setItem(
                    slot,
                    ability.getItem(item)
            );
        }
    }

    public void setRedCurrentCooldown(float currentCooldown) {
        if (!isDisableCooldowns()) {
            this.getRedAbility().setCurrentCooldown(currentCooldown);
            updateRedItem();
        }
    }

    public boolean isDisableCooldowns() {
        return disableCooldowns;
    }

    public AbstractAbility getRedAbility() {
        return spec.getRed();
    }

    public void updateRedItem() {
        if (entity instanceof Player) {
            updateRedItem((Player) entity);
        }
    }

    public void setDisableCooldowns(boolean disableCooldowns) {
        this.disableCooldowns = disableCooldowns;
    }

    public void subtractRedCooldown(float cooldown) {
        if (!isDisableCooldowns()) {
            this.getRedAbility().subtractCooldown(cooldown);
            updateRedItem();
        }
    }

    public void setPurpleCurrentCooldown(float currentCooldown) {
        if (!isDisableCooldowns()) {
            this.getPurpleAbility().setCurrentCooldown(currentCooldown);
            updatePurpleItem();
        }
    }

    public AbstractAbility getPurpleAbility() {
        return spec.getPurple();
    }

    public void updatePurpleItem() {
        if (entity instanceof Player) {
            updatePurpleItem((Player) entity);
        }
    }

    public void subtractPurpleCooldown(float cooldown) {
        if (!isDisableCooldowns()) {
            this.getPurpleAbility().subtractCooldown(cooldown);
            updatePurpleItem();
        }
    }

    public void setBlueCurrentCooldown(float currentCooldown) {
        if (!isDisableCooldowns()) {
            this.getBlueAbility().setCurrentCooldown(currentCooldown);
            updateBlueItem();
        }
    }

    public AbstractAbility getBlueAbility() {
        return spec.getBlue();
    }

    public void updateBlueItem() {
        if (entity instanceof Player) {
            updateBlueItem((Player) entity);
        }
    }

    public void subtractBlueCooldown(float cooldown) {
        if (!isDisableCooldowns()) {
            this.getBlueAbility().subtractCooldown(cooldown);
            updateBlueItem();
        }
    }

    public void setOrangeCurrentCooldown(float currentCooldown) {
        if (!isDisableCooldowns()) {
            this.getOrangeAbility().setCurrentCooldown(currentCooldown);
            updateOrangeItem();
        }
    }

    public AbstractAbility getOrangeAbility() {
        return spec.getOrange();
    }

    public void updateOrangeItem() {
        if (entity instanceof Player) {
            updateOrangeItem((Player) entity);
        }
    }

    public void subtractOrangeCooldown(float cooldown) {
        if (!isDisableCooldowns()) {
            this.getOrangeAbility().subtractCooldown(cooldown);
            updateOrangeItem();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AbstractPlayerClass getSpec() {
        return spec;
    }

    public void resetAbilities(boolean closeInventory) {
        for (AbstractAbility ability : spec.getAbilities()) {
            ability.setCurrentCooldown(0);
        }
        updateInventory(closeInventory);
    }

    public void updateInventory(boolean closeInventory) {

    }

    public void setSpec(Specializations spec, SkillBoosts skillBoost) {
        this.spec = spec.create.get();
        this.maxHealth = (this.spec.getMaxHealth() * (game.getAddons().contains(GameAddon.TRIPLE_HEALTH) ? 3 : 1));
        this.maxBaseHealth = this.maxHealth;
        this.health = this.maxHealth;
        this.energy = this.spec.getMaxEnergy();
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        if (maxHealth < maxBaseHealth) {
            maxHealth = maxBaseHealth;
        }
        this.maxHealth = maxHealth;
    }

    public float getMaxBaseHealth() {
        return maxBaseHealth;
    }

    public void setMaxBaseHealth(float maxBaseHealth) {
        this.maxBaseHealth = maxBaseHealth;
        if (maxHealth < maxBaseHealth) {
            maxHealth = maxBaseHealth;
        }
        //TODO change this
        spec.setMaxHealth((int) maxBaseHealth);
    }

    public void showDeathAnimation() {
        if (!(this.entity instanceof Player player)) {
            this.entity.damage(200);
        } else {
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class, false, z -> {
                EntityEquipment equipment = z.getEquipment();
                PlayerInventory playerInventory = player.getInventory();
                equipment.setBoots(playerInventory.getBoots());
                equipment.setLeggings(playerInventory.getLeggings());
                equipment.setChestplate(playerInventory.getChestplate());
                equipment.setHelmet(playerInventory.getHelmet());
                equipment.setItemInMainHand(playerInventory.getItemInMainHand());
            });
            zombie.damage(2000);
        }
    }

    public void heal() {
        this.health = this.maxBaseHealth;
    }

    public int getRegenTickTimer() {
        return regenTickTimer;
    }

    public void setRegenTickTimer(int regenTickTimer) {
        this.regenTickTimer = regenTickTimer;
    }

    public void resetRegenTimer() {
        regenTickTimer = (int) (200 * regenTickTimerModifier);
    }

    public void setRegenTickTimerModifier(float regenTickTimerModifier) {
        this.regenTickTimerModifier = regenTickTimerModifier;
    }

    public int getRespawnTickTimer() {
        return respawnTickTimer;
    }

    public void setRespawnTickTimer(int respawnTickTimer) {
        //convert respawntimer to ticks
        this.respawnTickTimer = respawnTickTimer * 20;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float addEnergy(WarlordsEntity giver, String ability, float amount) {
        float energyGiven = 0;
        if (energy + amount > getMaxEnergy()) {
            energyGiven = getMaxEnergy() - energy;
            this.energy = getMaxEnergy();
        } else if (energy + amount > 0) {
            energyGiven = amount;
            this.energy += amount;
        } else {
            this.energy = 1;
        }
        if ((int) energyGiven != 0) {
            if (getEntity() instanceof Player) {
                PlayerSettings receiverSettings = PlayerSettings.getPlayerSettings(getUuid());
                PlayerSettings giverSettings = PlayerSettings.getPlayerSettings(giver.getUuid());
                if (receiverSettings.getChatEnergyMode() == Settings.ChatSettings.ChatEnergy.ALL) {
                    if (this == giver) {
                        sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your " + ability + " gave you ", NamedTextColor.GRAY))
                                .append(Component.text((int) energyGiven, NamedTextColor.YELLOW))
                                .append(Component.text(" energy.", NamedTextColor.GRAY))
                        );
                    } else {
                        sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                                .append(Component.text(" " + giver.getName() + "'s " + ability + " gave you ", NamedTextColor.GRAY))
                                .append(Component.text((int) energyGiven, NamedTextColor.YELLOW))
                                .append(Component.text(" energy.", NamedTextColor.GRAY))
                        );
                    }
                }
                if (giverSettings.getChatEnergyMode() == Settings.ChatSettings.ChatEnergy.ALL) {
                    if (this != giver) {
                        giver.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your " + ability + " gave " + name + " ", NamedTextColor.GRAY))
                                .append(Component.text((int) energyGiven, NamedTextColor.YELLOW))
                                .append(Component.text(" energy.", NamedTextColor.GRAY))
                        );
                    }
                }
            }
        }

        return energyGiven;
    }

    public float getMaxEnergy() {
        return spec.getMaxEnergy();
    }

    @Nonnull
    public LivingEntity getEntity() {
        return this.entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void sendMessage(Component component) {
        this.entity.sendMessage(component);
        if (!AdminCommand.DISABLE_SPECTATOR_MESSAGES && game != null) {
            component.hoverEvent(null);
            game.spectators()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> Objects.equals(player.getSpectatorTarget(), entity))
                .forEach(player -> player.sendMessage(component));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float subtractEnergy(float amount, boolean fromAttacker) {
        float amountSubtracted = 0;
        if (!noEnergyConsumption) {
            amount *= energyModifier;
            if (energy - amount > getMaxEnergy()) {
                amountSubtracted = getMaxEnergy() - energy;
                energy = getMaxEnergy();
            } else if (energy - amount < 0) {
                amountSubtracted = energy;
                energy = 0;
            } else {
                amountSubtracted = amount;
                energy -= amount;
            }
        }
        if (!fromAttacker) {
            Bukkit.getPluginManager().callEvent(new WarlordsEnergyUsedEvent(this, amountSubtracted));
        }
        return amountSubtracted;
    }

    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (this.entity instanceof Player) {
            ((Player) this.entity).playSound(location, sound, volume, pitch);
        }
    }

    public void playSound(Location location, String soundString, float volume, float pitch) {
        if (this.entity instanceof Player) {
            ((Player) this.entity).playSound(location, soundString, volume, pitch);
        }
    }

    public boolean onHorse() {
        return this.entity.getVehicle() != null;
    }

    public float getHorseCooldown() {
        return horseCooldown;
    }

    public void setHorseCooldown(float horseCooldown) {
        this.horseCooldown = horseCooldown;
    }

    public int getFlagDropCooldown() {
        return flagDropCooldown;
    }

    public void setFlagDropCooldown(int flagDropCooldown) {
        this.flagDropCooldown = flagDropCooldown;
    }

    public int getFlagPickCooldown() {
        return flagPickCooldown;
    }

    public void setFlagPickCooldown(int flagPickCooldown) {
        this.flagPickCooldown = flagPickCooldown;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public void addKill() {
        this.minuteStats.addKill();
    }

    public void addAssist() {
        this.minuteStats.addAssist();
    }

    public LinkedHashMap<WarlordsEntity, Integer> getHitBy() {
        return hitBy;
    }

    public LinkedHashMap<WarlordsEntity, Integer> getHealedBy() {
        return healedBy;
    }

    public void addDeath() {
        this.minuteStats.addDeath();
    }

    public void addDamage(float amount, boolean onCarrier) {
        this.minuteStats.addDamage((long) amount);
        if (onCarrier) {
            this.minuteStats.addDamageOnCarrier((long) amount);
        }
    }

    public void addHealing(float amount, boolean onCarrier) {
        this.minuteStats.addHealing((long) amount);
        if (onCarrier) {
            this.minuteStats.addHealingOnCarrier((long) amount);
        }
    }

    public void addDamageTaken(float amount) {
        this.minuteStats.addDamageTaken((long) amount);
    }

    public void addAbsorbed(float amount) {
        this.minuteStats.addAbsorbed((long) amount);
    }

    /**
     * If minute stats size is greater than SPLITS, seperate hoverable texts into name of minuteStats
     *
     * <p>Ex: size of 60 = "Kil" will have first SPLITS mins while "ls" while have last SPLITS mins
     *
     * <p>This still has the chance of producing an error depending on how big the size of minuteStats is, how many stats the player has, and what other BaseComponents are on the same line as this. (String length increases).
     * Point of reference: 300 minutes with 500K damage/healing/absorbed PER minute did not produce an error
     * Max json size is 262144 chars
     *
     * @param minuteStatsType The type of minute stats to get the hoverable text for
     * @param hoverable       if the text should be hoverable
     * @return List of hoverable minute stats that make up minuteStatsType.name
     */
    public Component getAllMinuteHoverableStats(MinuteStats minuteStatsType, boolean hoverable) {
        if (!hoverable) {
            return Component.text(minuteStatsType.name + ": ", NamedTextColor.WHITE)
                            .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(minuteStats.total())), NamedTextColor.GOLD));
        } else {
            TextComponent.Builder component = Component.text();
            StringBuilder stringBuilder = new StringBuilder();
            String minuteStatsTypeName = minuteStatsType.name;

            List<PlayerStatisticsMinute.Entry> entries = minuteStats.getEntries();
            int size = entries.size();
            if (size > MINUTE_STATS_SPLITS) {
                int timesToSplit = size / MINUTE_STATS_SPLITS + 1;
                String[] splitString = StringUtils.splitStringNTimes(minuteStatsTypeName + ": " + NumberFormat.addCommaAndRound(
                        minuteStatsType.getValue.apply(minuteStats.total())), timesToSplit);
                int stringLength = 0;
                for (int i = 0; i < splitString.length; i++) {
                    for (int j = 0; j < MINUTE_STATS_SPLITS; j++) {
                        int index = i * MINUTE_STATS_SPLITS + j;
                        if (index >= size) {
                            break;
                        }
                        PlayerStatisticsMinute.Entry entry = entries.get(index);
                        stringBuilder.append(Component.text("Minute " + index + ": ", NamedTextColor.WHITE)
                                                      .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)),
                                                              NamedTextColor.GOLD
                                                      )));
                        stringBuilder.append(Component.newline());
                    }
                    stringBuilder.setLength(stringBuilder.length() - 1);
                    stringLength += stringBuilder.length();
                    component.append(Component.text(splitString[i], (i > minuteStatsTypeName.length() + 1 ? NamedTextColor.GOLD : NamedTextColor.WHITE)))
                             .hoverEvent(HoverEvent.showText(Component.text(stringBuilder.toString())));
                    stringBuilder.setLength(0);
                }
                //this will never happen in reality
                if (stringLength >= 8000) {
                    return component.build().replaceText(builder -> builder.match("Minute").replacement("Min."));
                }
            } else {
                stringBuilder.append(Component.text("Stat Breakdown (" + name + "):", NamedTextColor.AQUA));
                for (int i = 0; i < size; i++) {
                    PlayerStatisticsMinute.Entry entry = entries.get(i);
                    stringBuilder.append(Component.newline());
                    stringBuilder.append(Component.text("Minute " + (i + 1) + ": ", NamedTextColor.WHITE))
                                 .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)), NamedTextColor.GOLD));
                }
                component.append(Component.text(minuteStatsTypeName + ": ", NamedTextColor.WHITE)
                                          .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(minuteStats.total())),
                                                  NamedTextColor.GOLD
                                          )))
                         .hoverEvent(HoverEvent.showText(Component.text(stringBuilder.toString())));
            }

            return component.build();
        }
    }

    public void toggleTeamFlagCompass() {
        List<CompassTargetMarker> targets = getGame().getMarkers(CompassTargetMarker.class);
        boolean shouldPick = false;
        CompassTargetMarker first = null;
        for (CompassTargetMarker ctm : targets) {
            if (ctm == this.compassTarget) {
                shouldPick = true;
                if (first == null) {
                    first = ctm;
                }
                continue;
            }
            if (!ctm.isEnabled()) {
                continue;
            }
            if (first == null) {
                first = ctm;
            }
            if (shouldPick) {
                this.compassTarget = ctm;
                return;
            }
        }
        this.compassTarget = first;
    }

    public Game getGame() {
        return this.game;
    }

    public Runnable addSpeedModifier(WarlordsEntity from, String name, int modifier, int duration, String... toDisable) {
        AtomicReference<String> nameRef = new AtomicReference<>(name);
        AtomicInteger modifierRef = new AtomicInteger(modifier);
        AtomicInteger durationRef = new AtomicInteger(duration);
        AtomicReference<String[]> toDisableRef = new AtomicReference<>(toDisable);

        Bukkit.getPluginManager().callEvent(new WarlordsAddSpeedModifierEvent(this, from, nameRef, modifierRef, durationRef, toDisableRef));

        return this.speed.addSpeedModifier(from, nameRef.get(), modifierRef.get(), durationRef.get(), toDisableRef.get());
    }

    public CalculateSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(CalculateSpeed speed) {
        this.speed = speed;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public int getFlagsCaptured() {
        return this.minuteStats.total().getFlagsCaptured();
    }

    public void addFlagCap() {
        this.minuteStats.addFlagCapture();
    }

    public int getFlagsReturned() {
        return this.minuteStats.total().getFlagsReturned();
    }

    public void addFlagReturn() {
        this.minuteStats.addFlagReturned();
    }

    public int getTotalCapsAndReturnsWeighted() {
        PlayerStatisticsMinute.Entry total = this.minuteStats.total();
        return (total.getFlagsCaptured() * 5) + total.getFlagsReturned();
    }

    public boolean isAlive() {
        return !isDead();
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Nonnull
    public Location getLocation(@Nonnull Location copyInto) {
        return this.entity.getLocation(copyInto);
    }

    @Nonnull
    public Location getEyeLocation() {
        return this.entity.getEyeLocation();
    }

    public boolean isSneaking() {
        return this.entity instanceof Player && this.entity.isSneaking();
    }

    public boolean isWasSneaking() {
        return wasSneaking;
    }

    public void setWasSneaking(boolean wasSneaking) {
        this.wasSneaking = wasSneaking;
    }

    public boolean isEnemyAlive(@Nullable Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isEnemyAlive(@Nullable WarlordsEntity p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDead() &&
                p.getTeam() != getTeam();
    }

    public boolean isEnemy(@Nullable WarlordsEntity p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() != getTeam();
    }

    public boolean isTeammateAlive(@Nullable Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isTeammateAlive(@Nullable WarlordsEntity p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDead() &&
                p.getTeam() == getTeam();
    }

    public boolean isTeammate(@Nullable WarlordsEntity p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() == getTeam();
    }

    public void teleportLocationOnly(Location location) {
        if (this.entity instanceof Player) {
            TeleportUtils.teleport((Player) this.entity, location);
        } else {
            Location location1 = this.getLocation();
            location1.setX(location.getX());
            location1.setY(location.getY());
            location1.setZ(location.getZ());
            this.entity.teleport(location1);
        }
    }

    @Nonnull
    public Location getLocation() {
        return this.entity.getLocation();
    }

    /**
     * Gets the damage multiplier caused by any carried flag
     *
     * @return The flag damage multiplier, or 1 for easy calculations
     */
    public double getFlagDamageMultiplier() {
        return this.carriedFlag != null
                       && this.carriedFlag.getFlag() instanceof PlayerFlagLocation
                       && ((PlayerFlagLocation) this.carriedFlag.getFlag()).getPlayer() == this
               ? ((PlayerFlagLocation) this.carriedFlag.getFlag()).getComputedMultiplier()
               : 1;
    }

    public Component getColoredName() {
        return Component.text(getName(), getTeam().teamColor());
    }

    public Component getColoredNameBold() {
        return Component.text(getName(), getTeam().teamColor(), TextDecoration.BOLD);
    }

    public void setVelocity(String from, Vector v, boolean ignoreModifications) {
        setVelocity(from, v, true, ignoreModifications);
    }

    public void setVelocity(String from, Vector v, boolean kbAfterHorse, boolean ignoreModifications) {
        if ((kbAfterHorse || this.entity.getVehicle() == null)) {
            if (!ignoreModifications) {
                for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldownsDistinct()) {
                    abstractCooldown.multiplyKB(v);
                }
            }
            if (Double.isNaN(v.getX())) {
                v.setX(0);
            }
            if (Double.isNaN(v.getY())) {
                v.setY(0);
            }
            if (Double.isNaN(v.getZ())) {
                v.setZ(0);
            }
            WarlordsAddVelocityEvent warlordsAddVelocityEvent = new WarlordsAddVelocityEvent(this, from, v);
            Bukkit.getPluginManager().callEvent(warlordsAddVelocityEvent);
            if (!warlordsAddVelocityEvent.isCancelled()) {
                this.entity.setVelocity(v);
            }
        }
    }

    public boolean addPotionEffect(PotionEffect potionEffect) {
        if (this.getCooldownManager().hasCooldownFromName("Vindicate Debuff Immunity")) {
            if (PotionEffectType.BLINDNESS.equals(potionEffect.getType()) ||
                    PotionEffectType.CONFUSION.equals(potionEffect.getType())
            ) {
                return false;
            }
        }
        LivingEntity livingEntity = this.getEntity();
        livingEntity.removePotionEffect(potionEffect.getType());
        livingEntity.addPotionEffect(potionEffect);
        return true;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public World getWorld() {
        return this.entity.getWorld();
    }

    public boolean isNoEnergyConsumption() {
        return noEnergyConsumption;
    }

    public void setNoEnergyConsumption(boolean noEnergyConsumption) {
        this.noEnergyConsumption = noEnergyConsumption;
    }

    public double getEnergyModifier() {
        return energyModifier;
    }

    public void setEnergyModifier(double energyModifier) {
        this.energyModifier = energyModifier;
    }

    public double getCooldownModifier() {
        return cooldownModifier;
    }

    public void setCooldownModifier(double cooldownModifier) {
        this.cooldownModifier = cooldownModifier;
    }

    public boolean isTakeDamage() {
        return takeDamage;
    }

    public void setTakeDamage(boolean takeDamage) {
        this.takeDamage = takeDamage;
    }

    public boolean isCanCrit() {
        return canCrit;
    }

    public void setCanCrit(boolean canCrit) {
        this.canCrit = canCrit;
    }

    public void setBlocksTravelledCM(int blocksTravelledCM) {
        this.blocksTravelledCM = blocksTravelledCM;
    }

    public int getBlocksTravelled() {
        return blocksTravelledCM / 100;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    protected void setWalkSpeed(float walkspeed) {
        this.walkSpeed = walkspeed;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(this.walkSpeed);
        } else {
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(this.walkSpeed);
        }
    }

    public List<Float> getRecordDamage() {
        return recordDamage;
    }

    public float getCurrentHealthModifier() {
        return currentHealthModifier;
    }

    public void setCurrentHealthModifier(float currentHealthModifier) {
        this.currentHealthModifier = currentHealthModifier;
    }

    @Nullable
    public FlagInfo getCarriedFlag() {
        return carriedFlag;
    }

    public void setCarriedFlag(@Nullable FlagInfo carriedFlag) {
        this.carriedFlag = carriedFlag;
    }

    public List<Achievement.AbstractAchievementRecord<?>> getAchievementsUnlocked() {
        return achievementsUnlocked;
    }

    public boolean hasAchievement(ChallengeAchievements achievements) {
        return achievementsUnlocked.stream()
                                   .filter(ChallengeAchievements.ChallengeAchievementRecord.class::isInstance)
                                   .map(ChallengeAchievements.ChallengeAchievementRecord.class::cast)
                                   .map(ChallengeAchievements.ChallengeAchievementRecord::getAchievement)
                                   .anyMatch(achievement -> achievement == achievements);
    }

    public void unlockAchievement(ChallengeAchievements achievement) {
        achievementsUnlocked.add(new ChallengeAchievements.ChallengeAchievementRecord(achievement));
        if (entity instanceof Player) {
            DatabaseManager.getPlayer(uuid, databasePlayer -> {
                //only display achievement if they have never got it before
                if (!databasePlayer.hasAchievement(achievement)) {
                    achievement.sendAchievementUnlockMessage((Player) entity);
                    achievement.sendAchievementUnlockMessageToOthers(this);
                    //System.out.println(name + " unlocked achievement: " + achievement.name);
                }
            });
        }
    }

    public abstract boolean isOnline();

    @Nullable
    public CompassTargetMarker getCompassTarget() {
        return this.compassTarget;
    }

    public void runEveryTick() {
        this.spec.runEveryTick();
        // Gives the player their respawn timer as display.
        this.decrementRespawnTimer();
    }

    private void decrementRespawnTimer() {
        // Respawn
        if (respawnTickTimer == 20) {
            respawn();
        } else if (respawnTickTimer > 0) {
            minuteStats.addTotalRespawnTime();
            respawnTickTimer--;
            if (respawnTickTimer <= 600) {
                if (entity instanceof Player) {
                    entity.showTitle(Title.title(
                            Component.empty(),
                            Component.text("Respawning in... ", team.teamColor()).append(Component.text((respawnTickTimer / 20), NamedTextColor.YELLOW)),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                }
            }
        }
    }

    public void respawn() {
        List<Location> candidates = new ArrayList<>();
        double priority = Double.NEGATIVE_INFINITY;
        for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
            if (candidates.isEmpty()) {
                candidates.add(marker.getLocation());
                priority = marker.getPriority(this);
            } else {
                double newPriority = marker.getPriority(this);
                if (newPriority >= priority) {
                    if (newPriority > priority) {
                        candidates.clear();
                        priority = newPriority;
                    }
                    candidates.add(marker.getLocation());
                }
            }
        }
        Location respawnPoint =
                !candidates.isEmpty() ? candidates.get((int) (Math.random() * candidates.size())) :
                deathLocation != null ? deathLocation :
                getLocation();
        WarlordsRespawnEvent event = new WarlordsRespawnEvent(this, respawnPoint);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (entity instanceof Player) {
            entity.clearTitle();
        }
        setRespawnTickTimer(-1);
        setEnergy(getMaxEnergy() / 2);
        dead = false;
        teleport(respawnPoint);

        this.health = this.maxBaseHealth;
        updateEntity();
    }

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public abstract void updateEntity();

    public void runEverySecond() {
        this.spec.runEverySecond();
    }

    public void onRemove() {
        if (!(getEntity() instanceof Player)) {
            getEntity().remove();
        }
        getEntity().removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
        FlagHolder.dropFlagForPlayer(this);
        getMinuteStats().getEntries().clear();
        getSecondStats().getEntries().forEach(entry -> {
            entry.getEventsAsSelf().clear();
            entry.getEventsAsAttacker().clear();
        });
        getSecondStats().getEntries().clear();
        getCooldownManager().clearAllCooldowns();
    }

    @Nonnull
    public PlayerStatisticsMinute getMinuteStats() {
        return this.minuteStats;
    }

    public PlayerStatisticsSecond getSecondStats() {
        return secondStats;
    }

    public boolean shouldSpawnGrave() {
        return spawnGrave;
    }

    public void setSpawnGrave(boolean spawnGrave) {
        this.spawnGrave = spawnGrave;
    }

    public Vector getCurrentVector() {
        return currentVector;
    }

    public void setCurrentVector(Vector currentVector) {
        this.currentVector = currentVector;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public void addCurrency(int currency) {
        AtomicInteger currencyToAdd = new AtomicInteger(currency);
        Bukkit.getPluginManager().callEvent(new WarlordsAddCurrencyEvent(this, currencyToAdd));
        this.currency += currencyToAdd.get();
        sendMessage(Component.text("+" + currencyToAdd.get() + " ❂ Insignia", NamedTextColor.GOLD));
        Bukkit.getPluginManager().callEvent(new WarlordsAddCurrencyFinalEvent(this));
    }

    public void sendMessage(String message) {
        this.entity.sendMessage(message);
        if (!AdminCommand.DISABLE_SPECTATOR_MESSAGES && game != null) {
            game.spectators()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> Objects.equals(player.getSpectatorTarget(), entity))
                .forEach(player -> player.sendMessage(message));
        }
    }

    public void subtractCurrency(int currency) {
        if (currency < 0) {
            currency = 0;
        }
        this.currency -= currency;
    }

    public float getFallDistance() {
        return this.getEntity().getFallDistance();
    }

    public void setFallDistance(float amount) {
        this.getEntity().setFallDistance(amount);
    }

    public void setShowDebugMessage(boolean showDebugMessage) {
        this.showDebugMessage = showDebugMessage;
    }

    public float getBonusAgroWeight() {
        return bonusAgroWeight;
    }

    public void setBonusAgroWeight(float agroWeight) {
        this.bonusAgroWeight = agroWeight * agroWeight; // squared because values in PathfinderGoalTargetAgroWarlordsEntity are squared
    }

    public abstract void setDamageResistance(int damageResistance);
}
