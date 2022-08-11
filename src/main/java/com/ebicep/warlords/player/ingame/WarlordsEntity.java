package com.ebicep.warlords.player.ingame;

import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.HealingPowerup;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.*;
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
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.bukkit.TeleportUtils;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class WarlordsEntity {

    //RED << (Receiving from enemy / Negative from team?)
    public static final String RECEIVE_ARROW_RED = ChatColor.RED + "\u00AB";
    //GREEN << (Receiving from team / Positive from enemy?)
    public static final String RECEIVE_ARROW_GREEN = ChatColor.GREEN + "\u00AB";
    //RED >> (Doing negatives teammates?)
    public static final String GIVE_ARROW_RED = ChatColor.RED + "\u00BB";
    //GREEN >> (Doing negatives to enemy / Doing positives to team)
    public static final String GIVE_ARROW_GREEN = ChatColor.GREEN + "\u00BB";
    private static final int MINUTE_STATS_SPLITS = 35;
    protected final Game game;
    private final List<Float> recordDamage = new ArrayList<>();
    private final PlayerStatisticsMinute minuteStats;
    private final PlayerStatisticsSecond secondStats;
    private final List<Achievement.AbstractAchievementRecord<?>> achievementsUnlocked = new ArrayList<>();
    //assists = player - timeLeft(10 seconds)
    private final LinkedHashMap<WarlordsEntity, Integer> hitBy = new LinkedHashMap<>();
    private final LinkedHashMap<WarlordsEntity, Integer> healedBy = new LinkedHashMap<>();
    private final List<Location> locations = new ArrayList<>();
    private final Location deathLocation;
    private final CooldownManager cooldownManager = new CooldownManager(this);
    protected boolean spawnGrave = true;
    protected CalculateSpeed speed;
    protected String name;
    protected UUID uuid;
    protected AbstractPlayerClass spec;
    protected Weapons weaponSkin;
    protected float walkspeed = 1;
    protected LivingEntity entity;
    private Vector currentVector;
    private Team team;
    private Specializations specClass;
    private float health;
    private float maxHealth;
    private int regenTimer;
    private int respawnTimer = -1;
    private boolean dead = false;
    private float energy;
    private float maxEnergy;
    private CustomHorse horse;
    private float horseCooldown;
    private float currentHealthModifier = 1;
    private int flagDropCooldown;
    private int flagPickCooldown;
    private int hitCooldown;
    private int currency;
    private boolean wasSneaking = false;
    private int blocksTravelledCM = 0;
    private boolean noEnergyConsumption;
    private boolean disableCooldowns;
    private double energyModifier;
    private double cooldownModifier;
    private boolean takeDamage = true;
    private boolean canCrit = true;
    private double flagDamageMultiplier = 0;
    private boolean teamFlagCompass = true;
    @Nullable
    private FlagInfo carriedFlag = null;
    @Nullable
    private CompassTargetMarker compassTarget;
    private boolean active = true;
    private boolean isInPve = false;

    /**
     * @param uuid
     * @param name
     * @param game       what game should the WarlordsPlayer be assigned to.
     * @param team       optional team parameter to assign the WarlordsPlayer to a team.
     * @param weaponSkin
     * @param specClass
     * @param entity
     */
    public WarlordsEntity(
            @Nonnull UUID uuid,
            @Nonnull String name,
            @Nonnull Weapons weaponSkin,
            @Nonnull LivingEntity entity,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull Specializations specClass
    ) {
        this.name = name;
        this.uuid = uuid;
        this.game = game;
        this.minuteStats = new PlayerStatisticsMinute();
        this.secondStats = new PlayerStatisticsSecond();
        this.team = team;
        this.specClass = specClass;
        this.spec = specClass.create.get();
        this.maxHealth = this.spec.getMaxHealth();
        this.health = this.maxHealth;
        this.energy = 0;
        this.energyModifier = 1;
        this.maxEnergy = this.spec.getMaxEnergy();
        this.horseCooldown = 0;
        this.flagDropCooldown = 0;
        this.flagPickCooldown = 0;
        this.cooldownModifier = 1;
        this.hitCooldown = 20;
        this.speed = isInPve() ? new CalculateSpeed(this::setWalkSpeed, 13, true) : new CalculateSpeed(this::setWalkSpeed, 13);
        if (specClass == Specializations.APOTHECARY) {
            this.speed.addBaseModifier(10);
        }
        this.entity = entity;
        this.weaponSkin = weaponSkin;
        this.deathLocation = this.entity.getLocation();
        this.compassTarget = game
                .getMarkers(CompassTargetMarker.class)
                .stream().filter(c -> c.isEnabled())
                .sorted(Comparator.comparing((CompassTargetMarker c) -> c.getCompassTargetPriority(this)).reversed())
                .findFirst()
                .orElse(null);
        this.horse = new CustomHorse(((CraftWorld) entity.getWorld()).getHandle(), this);
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

    private Optional<WarlordsDamageHealingFinalEvent> addDamageHealingInstance(WarlordsDamageHealingEvent event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        if (event.isHealingInstance()) {
            Optional<WarlordsDamageHealingFinalEvent> eventOptional = addHealingInstance(event);
            eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager().callEvent(warlordsDamageHealingFinalEvent));
            return eventOptional;
        } else {
            Optional<WarlordsDamageHealingFinalEvent> eventOptional = addDamageInstance(event);
            eventOptional.ifPresent(warlordsDamageHealingFinalEvent -> Bukkit.getPluginManager().callEvent(warlordsDamageHealingFinalEvent));
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
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, false, true, Collections.emptyList()));
    }

    public Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean ignoreReduction,
            List<String> flags
    ) {
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, false, true, flags));
    }

    private Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(WarlordsDamageHealingEvent event) {
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

        WarlordsDamageHealingFinalEvent finalEvent = null;

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || !isActive()) {
            return Optional.empty();
        }

        float initialHealth = health;

        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeReductionFromSelf(event);
        }
        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeReductionFromAttacker(event);
        }

        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            if (critChance != -1) {
                critChance = abstractCooldown.addCritChanceFromAttacker(event, critChance);
                critMultiplier = abstractCooldown.addCritMultiplierFromAttacker(event, critMultiplier);
            }
        }

        //crit
        float damageValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;
        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            damageValue *= critMultiplier / 100f;
        }
        final float damageHealValueBeforeAllReduction = damageValue;
        addAbsorbed(Math.abs(damageValue - (damageValue *= 1 - spec.getDamageResistance() / 100f)));

        if (attacker == this && (isFallDamage || isMeleeHit)) {

            if (isMeleeHit) {
                // True damage
                sendMessage(RECEIVE_ARROW_RED + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(min) + ChatColor.GRAY + " melee damage.");
                regenTimer = 10;
                if (health - min <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    if (entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(min) + ChatColor.GRAY + " melee damage and died.", 0, 40, 0);
                    }
                    health = 0;
                    die(attacker);
                } else {
                    health -= min;
                    playHurtAnimation(this.entity, attacker);
                }
            } else {

                // Fall Damage
                sendMessage(RECEIVE_ARROW_RED + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(damageValue) + ChatColor.GRAY + " fall damage.");
                regenTimer = 10;
                if (health - damageValue <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    // Title card "YOU DIED!"
                    if (entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(damageValue) + ChatColor.GRAY + " fall damage and died.", 0, 40, 0);
                    }
                    health = 0;
                    die(attacker);
                } else {
                    health -= damageValue;
                    playHurtAnimation(entity, attacker);
                }

                for (OrderOfEviscerate orderOfEviscerate : new CooldownFilter<>(attacker, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(OrderOfEviscerate.class)
                        .collect(Collectors.toList())
                ) {
                    orderOfEviscerate.addAndCheckDamageThreshold(damageValue, attacker);
                }

                addAbsorbed(Math.abs(damageValue * spec.getDamageResistance() / 100));
            }
            cancelHealingPowerUp();

            return Optional.empty();
        }

        // Reduction before Intervene.
        if (!ignoreReduction) {

            // Flag carrier multiplier.

            damageValue *= getFlagDamageMultiplier();

            // Checks whether the player is standing in a Hammer of Light.
            if (!HammerOfLight.isStandingInHammer(attacker, this)) {

                // Damage Increase
                // Example: 1.1 = 10% increase.

                for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                    damageValue = abstractCooldown.modifyDamageBeforeInterveneFromSelf(event, damageValue);
                }

                for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                    damageValue = abstractCooldown.modifyDamageBeforeInterveneFromAttacker(event, damageValue);
                }

            }
        }

        final float damageHealValueBeforeInterveneReduction = damageValue;

        // Intervene
        Optional<RegularCooldown> optionalInterveneCooldown = new CooldownFilter<>(this, RegularCooldown.class)
                .filterCooldownClass(Intervene.class)
                .filter(regularCooldown -> regularCooldown.getFrom() != this)
                .findFirst();
        if (optionalInterveneCooldown.isPresent() &&
                optionalInterveneCooldown.get().getTicksLeft() > 0 &&
                !HammerOfLight.isStandingInHammer(attacker, this) &&
                isEnemy(attacker)
        ) {
            Intervene intervene = (Intervene) optionalInterveneCooldown.get().getCooldownObject();
            WarlordsEntity intervenedBy = optionalInterveneCooldown.get().getFrom();

            damageValue *= .5;
            intervenedBy.addAbsorbed(damageValue);
            intervenedBy.setRegenTimer(10);
            intervene.addDamagePrevented(damageValue);

            //breaking vene if above damage threshold
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
                intervenedBy.addDamageInstance(attacker, "Intervene", remainingVeneDamage, remainingVeneDamage, isCrit ? 100 : -1, 100, true);
                //extra overVeneDamage to target
                float overVeneDamage = intervene.getDamagePrevented() - intervene.getMaxDamagePrevented() / 2f;
                addDamageInstance(attacker, ability, overVeneDamage, overVeneDamage, isCrit ? 100 : -1, 100, true);

            } else {
                intervenedBy.addDamageInstance(attacker, "Intervene", damageValue, damageValue, isCrit ? 100 : -1, 100, false);
            }

            Location loc = getLocation();
            //EFFECTS + SOUNDS
            Utils.playGlobalSound(loc, "warrior.intervene.block", 2, 1);
            playHitSound(attacker);
            entity.playEffect(EntityEffect.HURT);
            intervenedBy.getEntity().playEffect(EntityEffect.HURT);

            // Red line particle if the player gets hit
            EffectUtils.playParticleLinkAnimation(getLocation(), intervenedBy.getLocation(), 255, 0, 0, 2);

            // Remove horses.
            removeHorse();
            intervenedBy.removeHorse();

            for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                abstractCooldown.onInterveneFromAttacker(event, damageValue);
            }

        } else {

            // Damage reduction after Intervene
            if (!ignoreReduction) {
                if (!HammerOfLight.isStandingInHammer(attacker, this)) {
                    // Damage Reduction
                    // Example: .8 = 20% reduction.
                    for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                        damageValue = abstractCooldown.modifyDamageAfterInterveneFromSelf(event, damageValue);
                    }

                    for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                        damageValue = abstractCooldown.modifyDamageAfterInterveneFromAttacker(event, damageValue);
                    }
                }
            }

            final float damageHealValueBeforeShieldReduction = damageValue;

            // Arcane Shield
            List<ArcaneShield> arcaneShields = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(ArcaneShield.class)
                    .collect(Collectors.toList());

            if (!arcaneShields.isEmpty() && isEnemy(attacker) && !HammerOfLight.isStandingInHammer(attacker, this)) {
                ArcaneShield arcaneShield = arcaneShields.get(0);
                //adding dmg to shield
                arcaneShield.addShieldHealth(-damageValue);
                //check if broken
                if (arcaneShield.getShieldHealth() <= 0) {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts(0);
                    }

                    cooldownManager.removeCooldownByObject(arcaneShield);
                    addDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, -arcaneShield.getShieldHealth(), -arcaneShield.getShieldHealth(), isCrit ? 100 : -1, 1, false, true, true, new ArrayList<>(0)));

                    addAbsorbed(-(arcaneShield.getShieldHealth()));

                    doOnStaticAbility(ArcaneShield.class, ArcaneShield::addTimesBroken);
                    return Optional.empty();
                } else {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts((float) (arcaneShield.getShieldHealth() / (maxHealth * .5) * 20));
                    }

                    if (isMeleeHit) {
                        sendMessage(RECEIVE_ARROW_RED + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s melee " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(GIVE_ARROW_GREEN + ChatColor.GRAY + " Your melee hit was absorbed by " + name);
                    } else {
                        sendMessage(RECEIVE_ARROW_RED + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s " + ability + " " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(GIVE_ARROW_GREEN + ChatColor.GRAY + " Your " + ability + " was absorbed by " + name + ChatColor.GRAY + ".");
                    }

                    addAbsorbed(Math.abs(damageHealValueBeforeAllReduction));
                }

                for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                    abstractCooldown.onShieldFromSelf(event, damageValue, isCrit);
                }

                for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                    abstractCooldown.onShieldFromAttacker(event, damageValue, isCrit);
                }

                playHurtAnimation(this.entity, attacker);

                if (!isMeleeHit) {
                    playHitSound(attacker);
                }
                removeHorse();

                finalEvent = new WarlordsDamageHealingFinalEvent(
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
                        true);
                secondStats.addDamageHealingEventAsSelf(finalEvent);
                attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);

//                checkForAchievementsDamage(attacker);
            } else {

                boolean debt = getCooldownManager().hasCooldownFromName("Spirits Respite");

                if (isEnemy(attacker)) {
                    hitBy.put(attacker, 10);

                    cancelHealingPowerUp();

                    removeHorse();

                    sendDamageMessage(attacker, this, ability, damageValue, isCrit, isMeleeHit);

                    float finalDamageValue = damageValue;
                    doOnStaticAbility(SoulShackle.class, soulShackle -> soulShackle.addToShacklePool(finalDamageValue));
                    doOnStaticAbility(Repentance.class, repentance -> repentance.addToPool(finalDamageValue));

                    for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                        abstractCooldown.onDamageFromSelf(event, damageValue, isCrit);
                    }

                    for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                        abstractCooldown.onDamageFromAttacker(event, damageValue, isCrit);
                    }
                }

                regenTimer = 10;

                updateHealth();

                // Adding/subtracting health

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
                attacker.getRecordDamage().add(damageValue);

                finalEvent = new WarlordsDamageHealingFinalEvent(
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
                        true);
                secondStats.addDamageHealingEventAsSelf(finalEvent);
                attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
//
//                checkForAchievementsDamage(attacker);

                // The player died.
                if (this.health <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    if (attacker.entity instanceof Player) {
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 1);
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 0.5f);
                    }

                    attacker.addKill();

                    attacker.sendMessage(ChatColor.GRAY + "You killed " + getColoredName());
                    sendMessage(ChatColor.GRAY + "You were killed by " + attacker.getColoredName());

                    game.forEachOnlinePlayer((p, t) -> {
                        if (p != this.entity && p != attacker.entity) {
                            p.sendMessage(getColoredName() + ChatColor.GRAY + " was killed by " + attacker.getColoredName());
                        }
                    });

                    for (WarlordsEntity enemy : PlayerFilter.playingGame(game)
                            .enemiesOf(this)
                            .stream().collect(Collectors.toList())
                    ) {
                        for (AbstractCooldown<?> abstractCooldown : enemy.getCooldownManager().getCooldownsDistinct()) {
                            abstractCooldown.onDeathFromEnemies(event, damageValue, isCrit, enemy == attacker);
                        }
                    }

                    // Title card "YOU DIED!"
                    if (this.entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + attacker.getName() + " killed you.", 0, 40, 0);
                    }
                    die(attacker);
                } else {
                    if (!isMeleeHit && this != attacker && damageValue != 0) {
                        playHitSound(attacker);
                    }
                }
            }
        }

        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.onEndFromSelf(event, damageValue, isCrit);
        }

        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.onEndFromAttacker(event, damageValue, isCrit);
        }

        getCooldownManager().getCooldowns().removeAll(new CooldownFilter<>(attacker, DamageHealCompleteCooldown.class).stream().collect(Collectors.toList()));
        attacker.getCooldownManager().getCooldowns().removeAll(new CooldownFilter<>(attacker, DamageHealCompleteCooldown.class).stream().collect(Collectors.toList()));

        return Optional.ofNullable(finalEvent);
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
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, isLastStandFromShield, false, Collections.emptyList()));
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
            List<String> flags
    ) {
        return this.addDamageHealingInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, isLastStandFromShield, false, flags));
    }

    private Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(WarlordsDamageHealingEvent event) {
        WarlordsEntity attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        float critChance = event.getCritChance();
        float critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();

        WarlordsDamageHealingFinalEvent finalEvent = null;

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || !isActive()) {
            return Optional.empty();
        }

        float initialHealth = health;

        // Critical Hits
        float healValue = (int) ((Math.random() * (max - min)) + min);
        double crit = ThreadLocalRandom.current().nextDouble(100);
        boolean isCrit = false;

        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            healValue *= critMultiplier / 100f;
        }

        final float healValueBeforeReduction = healValue;

        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            healValue = abstractCooldown.doBeforeHealFromSelf(event, healValue);
        }

        // Self Healing
        if (this == attacker) {

            if (this.health + healValue > this.maxHealth) {
                healValue = this.maxHealth - this.health;
            }

            if (healValue <= 0) return Optional.empty();

            // Displays the healing message.
            sendHealingMessage(this, healValue, ability, isCrit, isLastStandFromShield, false);
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

                if (healValue <= 0) return Optional.empty();

                boolean isOverheal = maxHealth > this.maxHealth && healValue + this.health > this.maxHealth;
                sendHealingMessage(attacker, this, healValue, ability, isCrit, isLastStandFromShield, isOverheal);

                health += healValue;
                attacker.addHealing(healValue, FlagHolder.isPlayerHolderFlag(this));

                if (!isMeleeHit && !ability.equals("Healing Rain")) {
                    playHitSound(attacker);
                }
            }
        }

        //attacker.sendMessage(ChatColor.GREEN + "Total Healing: " + attacker.getMinuteStats().total().getHealing());

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
                false);
        secondStats.addDamageHealingEventAsSelf(finalEvent);
        attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);

//        checkForAchievementsHealing(attacker);

        return Optional.of(finalEvent);
    }

    /**
     * @param player                which player should receive the message.
     * @param healValue             heal value of the message.
     * @param ability               which ability should the message display.
     * @param isCrit                whether if it's a critical hit message.
     * @param isLastStandFromShield whether the message is last stand healing.
     */
    private void sendHealingMessage(@Nonnull WarlordsEntity player, float healValue, String ability, boolean isCrit, boolean isLastStandFromShield, boolean isOverHeal) {
        StringBuilder ownFeed = new StringBuilder();
        ownFeed.append(GIVE_ARROW_GREEN).append(ChatColor.GRAY)
                .append(" Your ").append(ability);
        if (isCrit) {
            ownFeed.append(" critically");
        }
        ownFeed.append(" healed you for ").append(ChatColor.GREEN);
        if (isCrit) {
            ownFeed.append("§l");
        }
        ownFeed.append(Math.round(healValue));
        ownFeed.append(isCrit ? "!" : "");
        if (isLastStandFromShield) {
            ownFeed.append(" Absorbed!");
        }
        ownFeed.append(ChatColor.GRAY).append(" health.");

        player.sendMessage(ownFeed.toString());
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
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            float healValue, String ability,
            boolean isCrit,
            boolean isLastStandFromShield,
            boolean isOverHeal
    ) {
        // Own Message
        StringBuilder ownFeed = new StringBuilder();
        ownFeed.append(GIVE_ARROW_GREEN).append(ChatColor.GRAY)
                .append(" Your ").append(ability);

        if (isCrit) {
            ownFeed.append(" critically");
        }

        if (isOverHeal) {
            ownFeed.append(" overhealed ").append(name).append(" for ").append(ChatColor.GREEN);
        } else {
            ownFeed.append(" healed ").append(name).append(" for ").append(ChatColor.GREEN);
        }

        if (isCrit) {
            ownFeed.append("§l");
        }

        ownFeed.append(Math.round(healValue));
        ownFeed.append(isCrit ? "!" : "");

        if (isLastStandFromShield) {
            ownFeed.append(" Absorbed!");
        }

        ownFeed.append(ChatColor.GRAY).append(" health.");

        sender.sendMessage(ownFeed.toString());

        // Ally Message
        StringBuilder allyFeed = new StringBuilder();
        allyFeed.append(RECEIVE_ARROW_GREEN).append(ChatColor.GRAY).append(" ").append(sender.getName())
                .append("'s ").append(ability);

        if (isCrit) {
            allyFeed.append(" critically");
        }

        if (isOverHeal) {
            allyFeed.append(" overhealed you for ").append(ChatColor.GREEN);
        } else {
            allyFeed.append(" healed you for ").append(ChatColor.GREEN);
        }

        if (isCrit) {
            allyFeed.append("§l");
        }

        allyFeed.append(Math.round(healValue));
        allyFeed.append(isCrit ? "!" : "");

        if (isLastStandFromShield) {
            allyFeed.append(" Absorbed!");
        }

        allyFeed.append(ChatColor.GRAY).append(" health.");

        receiver.sendMessage(allyFeed.toString());
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
            @Nonnull WarlordsEntity sender,
            @Nonnull WarlordsEntity receiver,
            String ability,
            float damageValue,
            boolean isCrit,
            boolean isMeleeHit
    ) {
        // Receiver feed
        StringBuilder enemyFeed = new StringBuilder();
        enemyFeed.append(RECEIVE_ARROW_RED).append(ChatColor.GRAY).append(" ").append(sender.getName());
        if (!isMeleeHit) {
            enemyFeed.append("'s ").append(ability);
        }
        enemyFeed.append(" hit you for ").append(ChatColor.RED);
        if (isCrit) {
            enemyFeed.append("§l");
        }
        enemyFeed.append(Math.round(damageValue));
        if (isCrit) {
            enemyFeed.append("! ").append(ChatColor.GRAY).append("critical");
        }
        if (isMeleeHit) {
            enemyFeed.append(ChatColor.GRAY).append(" melee");
        }
        enemyFeed.append(ChatColor.GRAY).append(" damage.");

        receiver.sendMessage(enemyFeed.toString());

        // Sender feed
        StringBuilder ownFeed = new StringBuilder();
        ownFeed.append(GIVE_ARROW_GREEN).append(ChatColor.GRAY).append(" ");
        if (isMeleeHit) {
            ownFeed.append("You hit ");
        } else {
            ownFeed.append("Your ").append(ability).append(" hit ");
        }
        ownFeed.append(name);
        ownFeed.append(" for ").append(ChatColor.RED);
        if (isCrit) {
            ownFeed.append("§l");
        }
        ownFeed.append(Math.round(damageValue));
        if (isCrit) {
            ownFeed.append("! ").append(ChatColor.GRAY).append("critical");
        }
        if (isMeleeHit) {
            ownFeed.append(ChatColor.GRAY).append(" melee");
        }
        ownFeed.append(ChatColor.GRAY).append(" damage.");

        sender.sendMessage(ownFeed.toString());
    }

    /**
     * @param attacker which player should hear the hitsound?
     */
    private void playHitSound(WarlordsEntity attacker) {
        if (attacker.entity instanceof Player) {
            ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 1, 1);
        }
    }

    /**
     * @param entity     which entity is assigned to the hurt animation?
     * @param hurtPlayer what warlords player should play the hurt animation?
     */
    private void playHurtAnimation(LivingEntity entity, WarlordsEntity hurtPlayer) {
        entity.playEffect(EntityEffect.HURT);
        for (Player player1 : hurtPlayer.getWorld().getPlayers()) {
            player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 2, 1);
        }
    }

    public void cancelHealingPowerUp() {
        if (this.getCooldownManager().hasCooldown(HealingPowerup.class)) {
            sendMessage(ChatColor.GOLD + "Your §a§lHEALING §6powerup has worn off.");
            this.getCooldownManager().removeCooldown(HealingPowerup.class);
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

        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.setGameMode(GameMode.SPECTATOR);
            //removing yellow hearts
            ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts(0);
            ItemStack item = ((CraftPlayer) entity).getInventory().getItem(0);
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
                assisted.sendMessage(
                        ChatColor.GRAY +
                                "You assisted in killing " +
                                getColoredName()
                );
            } else {
                if (attacker != null) {
                    assisted.sendMessage(
                            ChatColor.GRAY +
                                    "You assisted " +
                                    attacker.getColoredName() +
                                    ChatColor.GRAY + " in killing " +
                                    getColoredName()
                    );
                }
            }
            assisted.addAssist();
        });
        hitBy.clear();
        regenTimer = 0;
        heal();
    }

    private void checkForAchievementsDamage(WarlordsEntity attacker) {
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.BLITZKRIEG);
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.SNIPE_SHOT);
        ChallengeAchievements.checkForAchievement(this, ChallengeAchievements.DUCK_TANK);
        ChallengeAchievements.checkForAchievement(this, ChallengeAchievements.SPLIT_SECOND);
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.REVENGE_BLAST);
        if (hasFlag()) {
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.ASSASSINATE);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.SILENCE_PEON);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.ORBIFICATOR);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.HOUR_OF_RECKONING);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.TALENT_SHREDDER);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.PERSISTENT_THREAT);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.WHERE_ARE_YOU_GOING);

            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.EXTENDED_COMBAT); //NEED TEST

        }
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.ROADBLOCK);

    }

    private void checkForAchievementsHealing(WarlordsEntity attacker) {
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.LYCHEESIS);

        if (hasFlag()) {
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.REJUVENATION);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.CLERICAL_PRODIGY);
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
            }
        }
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    protected void setWalkSpeed(float walkspeed) {
        this.walkspeed = walkspeed;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(this.walkspeed);
        } else {
            ((EntityLiving) ((CraftEntity) entity).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.walkspeed);
        }
    }

    public void displayActionBar() {
        StringBuilder actionBarMessage = new StringBuilder(ChatColor.GOLD + "§lHP: ");
        float healthRatio = health / maxHealth;
        if (healthRatio >= .75) {
            actionBarMessage.append(ChatColor.DARK_GREEN);

        } else if (healthRatio >= .25) {
            actionBarMessage.append(ChatColor.YELLOW);

        } else {
            actionBarMessage.append(ChatColor.RED);

        }
        actionBarMessage.append("§l").append(Math.round(health)).append(ChatColor.GOLD).append("§l/§l").append(Math.round(maxHealth)).append("    ");
        actionBarMessage.append(team.boldColoredPrefix()).append(" TEAM  ");
        for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldowns()) {
            if (abstractCooldown.getNameAbbreviation() != null) {
                actionBarMessage.append(abstractCooldown.getNameAbbreviation()).append(" ");
            }
        }
        if (entity instanceof Player) {
            PacketUtils.sendActionBar((Player) entity, actionBarMessage.toString());
        }
    }

    public void displayFlagActionBar(@Nonnull Player player) {
        if (this.compassTarget != null) {
            PacketUtils.sendActionBar(player, this.compassTarget.getToolbarName(this));
        } else {
            PacketUtils.sendActionBar(player, "");
        }
    }

    public void applySkillBoost(Player player) {
        SkillBoosts selectedBoost = Warlords.getPlayerSettings(Bukkit.getOfflinePlayer(uuid).getUniqueId()).getSkillBoostForClass();
        if (selectedBoost != null) {
            if (spec.getWeapon().getClass() == selectedBoost.ability) {
                spec.getWeapon().boostSkill(selectedBoost, spec);
                spec.getWeapon().updateDescription(player);
            } else if (spec.getRed().getClass() == selectedBoost.ability) {
                spec.getRed().boostSkill(selectedBoost, spec);
                spec.getRed().updateDescription(player);
            } else if (spec.getPurple().getClass() == selectedBoost.ability) {
                spec.getPurple().boostSkill(selectedBoost, spec);
                spec.getPurple().updateDescription(player);
            } else if (spec.getBlue().getClass() == selectedBoost.ability) {
                spec.getBlue().boostSkill(selectedBoost, spec);
                spec.getBlue().updateDescription(player);
            } else if (spec.getOrange().getClass() == selectedBoost.ability) {
                spec.getOrange().boostSkill(selectedBoost, spec);
                spec.getOrange().updateDescription(player);
            }
        }
    }

    public void updateArmor() {
        if (!(this.entity instanceof Player)) {
            return;
        }

        Player player = (Player) this.entity;

        if (!cooldownManager.hasCooldownFromName("Cloaked") || hasFlag()) {
            ArmorManager.resetArmor(player, getSpecClass(), getTeam());

            getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
            for (Player otherPlayer : player.getWorld().getPlayers()) {
                otherPlayer.showPlayer(player);
            }
        }

        if (hasFlag()) {
            ItemStack item = new ItemStack(Material.BANNER);
            BannerMeta banner = (BannerMeta) item.getItemMeta();
            banner.setBaseColor(getTeam() == Team.RED ? DyeColor.BLUE : DyeColor.RED);
            banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
            banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
            item.setItemMeta(banner);
            player.getInventory().setHelmet(item);
        }
    }

    public void assignItemLore(Player player) {
        //§
        ItemStack weapon = new ItemStack(this.weaponSkin.getItem());
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.setDisplayName("§cWarlord's Felflame of the " + spec.getWeapon().getName());
        ArrayList<String> weaponLore = new ArrayList<>();
        weaponLore.add("§7Damage: §c132 §7- §c179");
        weaponLore.add("§7Crit Chance: §c25%");
        weaponLore.add("§7Crit Multiplier: §c200%");
        weaponLore.add("");
        String classNamePath = spec.getClass().getGenericSuperclass().getTypeName();
        weaponLore.add("§a" + classNamePath.substring(classNamePath.indexOf("Abstract") + 8) + " (" + spec.getClass().getSimpleName() + "):");
        weaponLore.add("§aIncreases the damage you");
        weaponLore.add("§adeal with " + spec.getWeapon().getName() + " by §c20%");
        weaponLore.add("");
        weaponLore.add("§7Health: §a+800");
        weaponLore.add("§7Max Energy: §a+35");
        weaponLore.add("§7Cooldown Reduction: §a+13%");
        weaponLore.add("§7Speed: §a+13%");
        weaponLore.add("");
        weaponLore.add("§6Skill Boost Unlocked");
        weaponLore.add("§3Crafted");
        weaponLore.add("§dVoid Forged [4/4]");
        weaponLore.add("§aEQUIPPED");
        weaponLore.add("§bBOUND");
        weaponMeta.setLore(weaponLore);
        weapon.setItemMeta(weaponMeta);
        weaponMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(0, weapon);
        weaponLeftClick();

        updateRedItem(player);
        updatePurpleItem(player);
        updateBlueItem(player);
        updateOrangeItem(player);
        updateHorseItem(player);
    }

    public void assignFlagCompass(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "Flag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);
        switch (this.getGame().getGameMode()) {
            case CAPTURE_THE_FLAG:
                player.getInventory().setItem(8, compass);
                break;
            default:
                player.getInventory().setItem(8, null);
                break;
        }
    }

    public void weaponLeftClick() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.getInventory().setItem(
                    0,
                    new ItemBuilder(weaponSkin.getItem())
                            .name(ChatColor.GOLD + "Warlord's " + weaponSkin.getName() + " of the " + spec.getClass().getSimpleName())
                            .lore(
                                    ChatColor.GRAY + "Damage: " + ChatColor.RED + "132 " + ChatColor.GRAY + "- " + ChatColor.RED + "179",
                                    ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "25%",
                                    ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + "200%",
                                    "",
                                    ChatColor.GREEN + spec.getClassName() + " (" + spec.getClass().getSimpleName() + "):",
                                    Warlords.getPlayerSettings(player.getUniqueId()).getSkillBoostForClass().selectedDescription,
                                    "",
                                    ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+800",
                                    ChatColor.GRAY + "Max Energy: " + ChatColor.GREEN + "+35",
                                    ChatColor.GRAY + "Cooldown Reduction: " + ChatColor.GREEN + "+13%",
                                    ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+13%",
                                    "",
                                    ChatColor.GOLD + "Skill Boost Unlocked",
                                    ChatColor.DARK_AQUA + "Crafted",
                                    ChatColor.LIGHT_PURPLE + "Void Forged [4/4]",
                                    ChatColor.GREEN + "EQUIPPED",
                                    ChatColor.AQUA + "BOUND",
                                    "",
                                    ChatColor.YELLOW + ChatColor.BOLD.toString() + "RIGHT-CLICK " + ChatColor.GREEN + "to view " + ChatColor.YELLOW + spec.getWeapon().getName(),
                                    ChatColor.GREEN + "stats!")
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void weaponRightClick() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.getInventory().setItem(
                    0,
                    new ItemBuilder(weaponSkin.getItem())
                            .name(ChatColor.GREEN + spec.getWeapon().getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Right-Click!")
                            .lore(ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(spec.getWeapon().getEnergyCost()),
                                    ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(spec.getWeapon().getCritChance()) + "%",
                                    ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(spec.getWeapon().getCritMultiplier()) + "%",
                                    "",
                                    spec.getWeapon().getDescription(),
                                    "",
                                    ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEFT-CLICK " + ChatColor.GREEN + "to view weapon stats!")
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void updateItem(Player player, int slot, AbstractAbility ability, ItemStack item) {
        if (ability.getCurrentCooldown() > 0) {
            ItemBuilder cooldown = new ItemBuilder(Material.INK_SACK, ability.getCurrentCooldownItem(), (byte) 8)
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

    public ItemStack getItemStackForAbility(AbstractAbility ability) {
        if (ability == spec.getWeapon()) {
            return weaponSkin.getItem();
        } else if (ability == spec.getRed()) {
            return new ItemStack(Material.INK_SACK, 1, (byte) 1);
        } else if (ability == spec.getPurple()) {
            return new ItemStack(Material.GLOWSTONE_DUST);
        } else if (ability == spec.getBlue()) {
            return new ItemStack(Material.INK_SACK, 1, (byte) 10);
        } else if (ability == spec.getOrange()) {
            return new ItemStack(Material.INK_SACK, 1, (byte) 14);
        }
        return null;
    }

    public void updateItems() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            updateRedItem(player);
            updatePurpleItem(player);
            updateBlueItem(player);
            updateOrangeItem(player);
        }
    }

    public void updateRedItem() {
        if (entity instanceof Player) {
            updateRedItem((Player) entity);
        }
    }

    public void updateRedItem(Player player) {
        updateItem(player, 1, spec.getRed(), new ItemStack(Material.INK_SACK, 1, (byte) 1));
    }

    public void updatePurpleItem() {
        if (entity instanceof Player) {
            updatePurpleItem((Player) entity);
        }
    }

    public void updatePurpleItem(Player player) {
        updateItem(player, 2, spec.getPurple(), new ItemStack(Material.GLOWSTONE_DUST));
    }

    public void updateBlueItem() {
        if (entity instanceof Player) {
            updateBlueItem((Player) entity);
        }
    }

    public void updateBlueItem(Player player) {
        updateItem(player, 3, spec.getBlue(), new ItemStack(Material.INK_SACK, 1, (byte) 10));
    }

    public void updateOrangeItem() {
        if (entity instanceof Player) {
            updateOrangeItem((Player) entity);
        }
    }

    public void updateOrangeItem(Player player) {
        updateItem(player, 4, spec.getOrange(), new ItemStack(Material.INK_SACK, 1, (byte) 14));
    }

    public void updateHorseItem() {
        if (entity instanceof Player) {
            updateHorseItem((Player) entity);
        }
    }

    public void updateHorseItem(Player player) {
        if (horseCooldown > 0) {
            ItemStack cooldown = new ItemStack(Material.IRON_BARDING, (int) horseCooldown + 1);
            player.getInventory().setItem(7, cooldown);
        } else {
            ItemStack horse = new ItemStack(Material.GOLD_BARDING);
            ItemMeta horseMeta = horse.getItemMeta();
            horseMeta.setDisplayName(ChatColor.GREEN + "Mount " + ChatColor.GRAY + "- §eRight-Click!");
            ArrayList<String> horseLore = new ArrayList<>();
            horseLore.add(ChatColor.GRAY + "Cooldown: §b15 seconds");
            horseLore.add("");
            horseLore.add(ChatColor.GRAY + "Call your steed to assists you in battle");
            horseMeta.setLore(horseLore);
            horse.setItemMeta(horseMeta);
            horseMeta.spigot().setUnbreakable(true);
            switch (this.getGame().getGameMode()) {
                case CAPTURE_THE_FLAG:
                case TEAM_DEATHMATCH:
                case INTERCEPTION:
                case SIMULATION_TRIAL:
                case DEBUG:
                    player.getInventory().setItem(7, horse);
                    break;
                case WAVE_DEFENSE:
                    player.getInventory().setItem(
                            7,
                            new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.GREEN + "Upgrade Menu").get()
                    );
                    break;
                default:
                    player.getInventory().setItem(7, null);
                    break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public void setSpec(AbstractPlayerClass spec, SkillBoosts skillBoost) {
        Warlords.getPlayerSettings(uuid).setSelectedSpec(Specializations.getSpecFromName(spec.getName()));
        Warlords.getPlayerSettings(uuid).setSkillBoostForSelectedSpec(skillBoost);
        Player player = Bukkit.getPlayer(uuid);
        this.spec = spec;
        this.specClass = Warlords.getPlayerSettings(uuid).getSelectedSpec();
        this.weaponSkin = Weapons.getSelected(player, this.specClass);
        this.maxHealth = (this.spec.getMaxHealth() * (game.getAddons().contains(GameAddon.TRIPLE_HEALTH) ? 3 : 1));
        this.health = this.maxHealth;
        this.maxEnergy = this.spec.getMaxEnergy();
        this.energy = this.maxEnergy;
        ArmorManager.resetArmor(player, specClass, team);
        applySkillBoost(player);
        this.spec.getWeapon().updateDescription(player);
        this.spec.getRed().updateDescription(player);
        this.spec.getPurple().updateDescription(player);
        this.spec.getBlue().updateDescription(player);
        this.spec.getOrange().updateDescription(player);
        assignItemLore(Bukkit.getPlayer(uuid));
        assignFlagCompass(Bukkit.getPlayer(uuid));

        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        databasePlayer.getSpec(specClass).setSkillBoost(skillBoost);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
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
        this.maxHealth = maxHealth;
    }

    public void showDeathAnimation() {
        if (!(this.entity instanceof Player)) {
            this.entity.damage(200);
        } else {
            Player player = (Player) this.entity;
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
            zombie.getEquipment().setBoots(player.getInventory().getBoots());
            zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
            zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
            zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
            zombie.getEquipment().setItemInHand(player.getInventory().getItemInHand());
            zombie.damage(2000);
        }
    }

    public void heal() {
        this.health = this.maxHealth;
    }

    private void decrementRespawnTimer() {
        // Respawn
        if (respawnTimer == 1) {
            respawn();
        } else if (respawnTimer > 0) {
            minuteStats.addTotalRespawnTime();
            respawnTimer--;
            if (respawnTimer <= 30) {
                if (entity instanceof Player) {
                    PacketUtils.sendTitle((Player) entity, "", team.teamColor() + "Respawning in... " + ChatColor.YELLOW + respawnTimer, 0, 40, 0);
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
            PacketUtils.sendTitle((Player) entity, "", "", 0, 0, 0);
        }
        setRespawnTimer(-1);
        setEnergy(getMaxEnergy() / 2);
        dead = false;
        teleport(respawnPoint);

        this.health = this.maxHealth;
        updateEntity();
    }

    public int getRegenTimer() {
        return regenTimer;
    }

    public void setRegenTimer(int regenTimer) {
        this.regenTimer = regenTimer;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(int respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float addEnergy(WarlordsEntity giver, String ability, float amount) {
        float energyGiven = 0;
        if (energy + amount > maxEnergy) {
            energyGiven = maxEnergy - energy;
            this.energy = maxEnergy;
        } else if (energy + amount > 0) {
            energyGiven = amount;
            this.energy += amount;
        } else {
            this.energy = 1;
        }
        if ((int) amount != 0) {
            if (this == giver) {
                sendMessage(GIVE_ARROW_GREEN + ChatColor.GRAY + " Your " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
            } else {
                sendMessage(RECEIVE_ARROW_GREEN + ChatColor.GRAY + " " + giver.getName() + "'s " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
                giver.sendMessage(GIVE_ARROW_GREEN + ChatColor.GRAY + " " + "Your " + ability + " gave " + name + " " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
            }
        }

        return energyGiven;
    }

    public float subtractEnergy(float amount, boolean fromAttacker) {
        float amountSubtracted = 0;
        if (!noEnergyConsumption) {
            amount *= energyModifier;
            if (energy - amount > maxEnergy) {
                amountSubtracted = maxEnergy - energy;
                energy = maxEnergy;
            } else if (energy - amount < 0) {
                amountSubtracted = energy;
                energy = 0;
            } else {
                amountSubtracted = amount;
                energy -= amount;
            }
        }
        if (!fromAttacker) {
            Bukkit.getPluginManager().callEvent(new WarlordsPlayerEnergyUsed(this, amountSubtracted));
        }
        return amountSubtracted;
    }

    public void sendMessage(String message) {
        if (this.entity instanceof Player) { // TODO check if this if is really needed, we can send a message to any entity??
            this.entity.sendMessage(message);
        }
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
        return this.entity.isInsideVehicle();
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public CustomHorse getHorse() {
        return horse;
    }

    public void setHorse(CustomHorse horse) {
        this.horse = horse;
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
     *
     * @param minuteStatsType The type of minute stats to get the hoverable text for
     * @return List of hoverable minute stats that make up minuteStatsType.name
     */
    public List<TextComponent> getAllMinuteHoverableStats(MinuteStats minuteStatsType) {
        List<TextComponentBuilder> components = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        String minuteStatsTypeName = minuteStatsType.name;

        List<PlayerStatisticsMinute.Entry> entries = minuteStats.getEntries();
        int size = entries.size();
        if (size > MINUTE_STATS_SPLITS) {
            int timesToSplit = size / MINUTE_STATS_SPLITS + 1;
            String[] splitString = StringUtils.splitStringNTimes(minuteStatsTypeName + ": 50,000" + NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(minuteStats.total())), timesToSplit);
            int stringLength = 0;
            for (int i = 0; i < splitString.length; i++) {
                for (int j = 0; j < MINUTE_STATS_SPLITS; j++) {
                    int index = i * MINUTE_STATS_SPLITS + j;
                    if (index >= size) {
                        break;
                    }
                    PlayerStatisticsMinute.Entry entry = entries.get(index);
                    stringBuilder.append(ChatColor.WHITE).append("Minute ").append(index).append(": ").append(ChatColor.GOLD);
                    stringBuilder.append(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)));
                    stringBuilder.append("\n");
                }
                stringBuilder.setLength(stringBuilder.length() - 1);
                stringLength += stringBuilder.length();
                components.add(new TextComponentBuilder((i > minuteStatsTypeName.length() + 1 ? ChatColor.GOLD : ChatColor.WHITE) + splitString[i])
                        .setHoverText(stringBuilder.toString())
                );
                stringBuilder.setLength(0);
            }
            //this will never happen in reality
            if (stringLength >= 8000) {
                for (TextComponentBuilder component : components) {
                    component.setHoverText(component.getHoverText().replace("Minute", "Min."));
                }
            }
        } else {
            stringBuilder.append(ChatColor.AQUA).append("Stat Breakdown (").append(name).append("):");
            for (int i = 0; i < size; i++) {
                PlayerStatisticsMinute.Entry entry = entries.get(i);
                stringBuilder.append("\n");
                stringBuilder.append(ChatColor.WHITE).append("Minute ").append(i + 1).append(": ").append(ChatColor.GOLD);
                stringBuilder.append(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)));
            }
            components.add(new TextComponentBuilder(ChatColor.WHITE + minuteStatsTypeName + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(minuteStats.total())))
                    .setHoverText(stringBuilder.toString())
            );
        }

        return components.stream()
                .map(TextComponentBuilder::getTextComponent)
                .collect(Collectors.toList());
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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isAlive() {
        return !isDead();
    }

    public abstract void updateEntity();

    public Specializations getSpecClass() {
        return specClass;
    }

    public Weapons getWeaponSkin() {
        return weaponSkin;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Game getGame() {
        return this.game;
    }

    @Nonnull
    public LivingEntity getEntity() {
        return this.entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    @Nonnull
    public Location getLocation() {
        return this.entity.getLocation();
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
        return this.entity instanceof Player && ((Player) this.entity).isSneaking();
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

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public void teleportLocationOnly(Location location) {
        if (this.entity instanceof Player) {
            TeleportUtils.teleport((Player) this.entity, location);
        } else {
            Location location1 = this.getLocation();
            location1.setX(location.getX());
            location1.setY(location.getY());
            location1.setZ(location.getZ());
            this.entity.teleport(location);
        }
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

    public boolean hasFlag() {
        return FlagHolder.isPlayerHolderFlag(this);
    }

    public String getColoredName() {
        return getTeam().teamColor() + getName();
    }

    public String getColoredNameBold() {
        return getTeam().teamColor().toString() + ChatColor.BOLD + getName();
    }

    public void setVelocity(Vector v, boolean ignoreModifications) {
        setVelocity(v, true, ignoreModifications);
    }

    public void setVelocity(Vector v, boolean kbAfterHorse, boolean ignoreModifications) {
        if ((kbAfterHorse || this.entity.getVehicle() == null)) {
            if (!ignoreModifications) {
                for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldownsDistinct()) {
                    abstractCooldown.multiplyKB(v);
                }
            }
            this.entity.setVelocity(v);
        }
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        if (this.getCooldownManager().hasCooldownFromName("Vindicate Debuff Immunity")) {
            if (PotionEffectType.BLINDNESS.equals(potionEffect.getType()) ||
                    PotionEffectType.CONFUSION.equals(potionEffect.getType())
            ) {
                return;
            }
        }
        //addPotionEffect(effect, force);
        this.getEntity().addPotionEffect(potionEffect, true);
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

    public boolean isDisableCooldowns() {
        return disableCooldowns;
    }

    public void setDisableCooldowns(boolean disableCooldowns) {
        this.disableCooldowns = disableCooldowns;
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

    public int getBlocksTravelledCM() {
        return blocksTravelledCM;
    }

    public void setBlocksTravelledCM(int blocksTravelledCM) {
        this.blocksTravelledCM = blocksTravelledCM;
    }

    public float getWalkspeed() {
        return walkspeed;
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

    @Nonnull
    public PlayerStatisticsMinute getMinuteStats() {
        return this.minuteStats;
    }

    public PlayerStatisticsSecond getSecondStats() {
        return secondStats;
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
            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
            //only display achievement if they have never got it before
            if (!databasePlayer.hasAchievement(achievement)) {
                achievement.sendAchievementUnlockMessage((Player) entity);
                achievement.sendAchievementUnlockMessageToOthers(this);
            }
        }
        System.out.println(name + " unlocked achievement: " + achievement.name);
    }

    public abstract boolean isOnline();

    @Nullable
    public CompassTargetMarker getCompassTarget() {
        return this.compassTarget;
    }

    public void runEverySecond() {
        this.spec.runEverySecond();
        // Gives the player their respawn timer as display.
        this.decrementRespawnTimer();
    }

    public void runEveryTick() {
        this.spec.runEveryTick();
    }

    public void onRemove() {
        if (!(getEntity() instanceof Player)) {
            getEntity().remove();
        }
        getEntity().removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
        FlagHolder.dropFlagForPlayer(this);
        getCooldownManager().clearAllCooldowns();
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
        this.currency += currency;
    }

    public void subtractCurrency(int currency) {
        if (currency < 0) {
            currency = 0;
        }
        this.currency -= currency;
    }

    public boolean isInPve() {
        return isInPve;
    }

    public void setInPve(boolean inPve) {
        isInPve = inPve;
    }
}
