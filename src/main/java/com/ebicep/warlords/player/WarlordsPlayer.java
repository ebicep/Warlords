package com.ebicep.warlords.player;

import com.ebicep.customentities.CustomHorse;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.HealingPowerup;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.rogue.specs.Vindicator;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.FlagInfo;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.bukkit.TeleportUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class WarlordsPlayer {

    //RED << (Receiving from enemy / Negative from team?)
    public static final String RECEIVE_ARROW_RED = ChatColor.RED + "\u00AB";
    //GREEN << (Receiving from team / Positive from enemy?)
    public static final String RECEIVE_ARROW_GREEN = ChatColor.GREEN + "\u00AB";
    //RED >> (Doing negatives teammates?)
    public static final String GIVE_ARROW_RED = ChatColor.RED + "\u00BB";
    //GREEN >> (Doing negatives to enemy / Doing positives to team)
    public static final String GIVE_ARROW_GREEN = ChatColor.GREEN + "\u00BB";

    @Deprecated
    private final PlayingState gameState;
    private final Game game;
    private final List<Float> recordDamage = new ArrayList<>();
    private final PlayerStatisticsMinute minuteStats;
    private final PlayerStatisticsSecond secondStats;
    private final List<Achievement.AbstractAchievementRecord> achievementsUnlocked = new ArrayList<>();
    //assists = player - timeLeft(10 seconds)
    private final LinkedHashMap<WarlordsPlayer, Integer> hitBy = new LinkedHashMap<>();
    private final LinkedHashMap<WarlordsPlayer, Integer> healedBy = new LinkedHashMap<>();
    private final List<Location> locations = new ArrayList<>();
    private final CalculateSpeed speed;
    private final Location deathLocation;
    private final CooldownManager cooldownManager = new CooldownManager(this);
    private String name;
    private UUID uuid;
    private Team team;
    private AbstractPlayerClass spec;
    private Specializations specClass;
    private Weapons weapon;
    private int health;
    private int maxHealth;
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
    private boolean wasSneaking = false;
    // We have to store these in here as the new player might logout midgame
    private float walkspeed = 1;
    private int blocksTravelledCM = 0;
    private boolean infiniteEnergy;
    private boolean disableCooldowns;
    private double energyModifier;
    private double cooldownModifier;
    private boolean takeDamage = true;
    private boolean canCrit = true;
    private double flagDamageMultiplier = 0;
    private boolean teamFlagCompass = true;
    private LivingEntity entity;
    @Nullable
    private FlagInfo carriedFlag = null;
    @Nullable
    private CompassTargetMarker compassTarget;

    /**
     * @param player    is the assigned player as WarlordsPlayer.
     * @param gameState what gamestate should the WarlordsPlayer be assigned to.
     * @param team      optional team parameter to assign the WarlordsPlayer to a team.
     * @param settings  what settings profile does the WarlordsPlayer use.
     */
    public WarlordsPlayer(
            @Nonnull OfflinePlayer player,
            @Nonnull PlayingState gameState,
            @Nonnull Team team,
            @Nonnull PlayerSettings settings
    ) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.gameState = gameState;
        this.game = gameState.getGame();
        this.minuteStats = new PlayerStatisticsMinute();
        this.secondStats = new PlayerStatisticsSecond(gameState);
        this.team = team;
        this.specClass = settings.getSelectedSpec();
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
        this.speed = new CalculateSpeed(this::setWalkSpeed, 13);
        Player p = player.getPlayer();
        this.entity = spawnJimmy(p == null ? Warlords.getRejoinPoint(uuid) : p.getLocation(), null);
        this.weapon = Weapons.getSelected(player, settings.getSelectedSpec());
        this.deathLocation = this.entity.getLocation();
        updatePlayerReference(p);
        this.compassTarget = gameState.getGame()
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

    private Optional<WarlordsDamageHealingFinalEvent> addHealingDamageInstance(WarlordsDamageHealingEvent event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        if (event.isHealingInstance()) {
            return addHealingInstance(event);
        } else {
            return addDamageInstance(event);
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
            WarlordsPlayer attacker,
            String ability,
            float min,
            float max,
            int critChance,
            int critMultiplier,
            boolean ignoreReduction
    ) {
        return this.addHealingDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, false, true));
    }

    private Optional<WarlordsDamageHealingFinalEvent> addDamageInstance(WarlordsDamageHealingEvent event) {
        WarlordsPlayer attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        int critChance = event.getCritChance();
        int critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();
        boolean isFallDamage = ability.equals("Fall");

        WarlordsDamageHealingFinalEvent finalEvent = null;

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || getGameState() != getGame().getState()) {
            return Optional.empty();
        }

        int initialHealth = health;

        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeReductionFromSelf(event);
        }
        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            abstractCooldown.doBeforeReductionFromAttacker(event);
        }

        for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
            critChance = abstractCooldown.addCritChanceFromAttacker(event, critChance);
            critMultiplier = abstractCooldown.addCritMultiplierFromAttacker(event, critMultiplier);
        }

        //crit
        float damageValue = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
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
                if (health - damageValue < 0 && !cooldownManager.checkUndyingArmy(false)) {
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
            if (!HammerOfLight.standingInHammer(attacker, entity)) {

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
        if (optionalInterveneCooldown.isPresent() && !HammerOfLight.standingInHammer(attacker, entity) && isEnemy(attacker)) {
            Intervene intervene = (Intervene) optionalInterveneCooldown.get().getCooldownObject();
            WarlordsPlayer intervenedBy = optionalInterveneCooldown.get().getFrom();

            damageValue *= .5;
            intervenedBy.addAbsorbed(damageValue);
            intervenedBy.setRegenTimer(10);
            intervene.addDamagePrevented(damageValue);
            intervenedBy.addDamageInstance(attacker, "Intervene", damageValue, damageValue, isCrit ? 100 : -1, 100, false);
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
                if (!HammerOfLight.standingInHammer(attacker, entity)) {
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

            if (!arcaneShields.isEmpty() && isEnemy(attacker) && !HammerOfLight.standingInHammer(attacker, entity)) {
                ArcaneShield arcaneShield = arcaneShields.get(0);
                //adding dmg to shield
                arcaneShield.addShieldHealth(-damageValue);
                //check if broken
                if (arcaneShield.getShieldHealth() < 0) {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts(0);
                    }

                    cooldownManager.removeCooldown(arcaneShield);
                    addDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, -arcaneShield.getShieldHealth(), -arcaneShield.getShieldHealth(), isCrit ? 100 : -1, 1, false, true, true));

                    addAbsorbed(-(arcaneShield.getShieldHealth()));

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

            } else {

                boolean debt = getCooldownManager().hasCooldownFromName("Spirits Respite");

                if (isEnemy(attacker)) {
                    hitBy.put(attacker, 10);

                    cancelHealingPowerUp();

                    removeHorse();
                    regenTimer = 10;

                    sendDamageMessage(attacker, this, ability, damageValue, isCrit, isMeleeHit);

                    if (spec instanceof Vindicator) {
                        ((SoulShackle) spec.getRed()).addToShacklePool(damageValue);
                    }

                    // Repentance
                    if (spec instanceof Spiritguard) {
                        ((Repentance) spec.getBlue()).addToPool(damageValue);
                    }

                    for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                        abstractCooldown.onDamageFromSelf(event, damageValue, isCrit);
                    }

                    for (AbstractCooldown<?> abstractCooldown : attacker.getCooldownManager().getCooldownsDistinct()) {
                        abstractCooldown.onDamageFromAttacker(event, damageValue, isCrit);
                    }

                }

                updateJimmyHealth();

                // Adding/subtracting health

                // debt and healing
                if (!debt && takeDamage) {
                    this.health -= Math.round(damageValue);
                }

                attacker.addDamage(damageValue, FlagHolder.isPlayerHolderFlag(this));
                playHurtAnimation(this.entity, attacker);
                recordDamage.add(damageValue);

                finalEvent = new WarlordsDamageHealingFinalEvent(
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
//                secondStats.addDamageHealingEventAsSelf(finalEvent);
//                attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
//
//                checkForAchievementsDamage(attacker);
                // The player died.
                if (this.health <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    if (attacker.entity instanceof Player) {
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 1);
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 0.5f);
                    }

                    attacker.addKill();

                    sendMessage(ChatColor.GRAY + "You were killed by " + attacker.getColoredName());
                    attacker.sendMessage(ChatColor.GRAY + "You killed " + getColoredName());

                    gameState.getGame().forEachOnlinePlayer((p, t) -> {
                        if (p != this.entity && p != attacker.entity) {
                            p.sendMessage(getColoredName() + ChatColor.GRAY + " was killed by " + attacker.getColoredName());
                        }
                    });

                    for (WarlordsPlayer enemy : PlayerFilter.playingGame(game)
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
            WarlordsPlayer attacker,
            String ability,
            float min,
            float max,
            int critChance,
            int critMultiplier,
            boolean ignoreReduction,
            boolean isLastStandFromShield
    ) {
        return this.addHealingDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, isLastStandFromShield, false));
    }

    private Optional<WarlordsDamageHealingFinalEvent> addHealingInstance(WarlordsDamageHealingEvent event) {
        WarlordsPlayer attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        int critChance = event.getCritChance();
        int critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();

        WarlordsDamageHealingFinalEvent finalEvent = null;

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || getGameState() != getGame().getState()) {
            return Optional.empty();
        }

        int initialHealth = health;

        // Critical Hits
        float healValue = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
        boolean isCrit = false;

        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            healValue *= critMultiplier / 100f;
        }

        final float healValueBeforeReduction = healValue;

        for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
            if (abstractCooldown.isHealing()) {
                healValue = abstractCooldown.doBeforeHealFromSelf(event, healValue);
            }
        }

        // Self Healing
        if (this == attacker) {

            if (this.health + healValue > this.maxHealth) {
                healValue = this.maxHealth - this.health;
            }

            if (healValue < 0) return Optional.empty();

            if (healValue != 0) {
                // Displays the healing message.
                sendHealingMessage(this, healValue, ability, isCrit, isLastStandFromShield, false);
                health += healValue;
                addHealing(healValue, FlagHolder.isPlayerHolderFlag(this));

                if (!isMeleeHit && !ability.equals("Healing Rain") && !ability.equals("Blood Lust")) {
                    playHitSound(attacker);
                }
            }

        } else {

            // Teammate Healing
            if (isTeammate(attacker)) {

                int maxHealth = this.maxHealth;
                if (ability.equals("Water Bolt") || ability.equals("Water Breath") || ability.equals("Healing Rain")) {
                    maxHealth *= 1.1;
                }

                if (this.health + healValue > maxHealth) {
                    healValue = maxHealth - this.health;
                }

                if (healValue < 0) return Optional.empty();

                boolean isOverheal = maxHealth > this.maxHealth && healValue + this.health > this.maxHealth;
                if (healValue != 0) {
                    sendHealingMessage(attacker, this, healValue, ability, isCrit, isLastStandFromShield, isOverheal);
                }

                health += healValue;
                attacker.addHealing(healValue, FlagHolder.isPlayerHolderFlag(this));

                if (!isMeleeHit && !ability.equals("Healing Rain")) {
                    playHitSound(attacker);
                }
            }
        }

        //attacker.sendMessage(ChatColor.GREEN + "Total Healing: " + attacker.getMinuteStats().total().getHealing());

        finalEvent = new WarlordsDamageHealingFinalEvent(
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
//        secondStats.addDamageHealingEventAsSelf(finalEvent);
//        attacker.getSecondStats().addDamageHealingEventAsAttacker(finalEvent);
//
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
    private void sendHealingMessage(@Nonnull WarlordsPlayer player, float healValue, String ability, boolean isCrit, boolean isLastStandFromShield, boolean isOverHeal) {
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
            @Nonnull WarlordsPlayer sender,
            @Nonnull WarlordsPlayer receiver,
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
            @Nonnull WarlordsPlayer sender,
            @Nonnull WarlordsPlayer receiver,
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
    private void playHitSound(WarlordsPlayer attacker) {
        if (attacker.entity instanceof Player) {
            ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 1, 1);
        }
    }

    /**
     * @param entity     which entity is assigned to the hurt animation?
     * @param hurtPlayer what warlords player should play the hurt animation?
     */
    private void playHurtAnimation(LivingEntity entity, WarlordsPlayer hurtPlayer) {
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

    public void die(@Nullable WarlordsPlayer attacker) {
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

    private void checkForAchievementsDamage(WarlordsPlayer attacker) {
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
        }
    }

    private void checkForAchievementsHealing(WarlordsPlayer attacker) {
        ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.LYCHEESIS);

        if (hasFlag()) {
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.REJUVENATION);
            ChallengeAchievements.checkForAchievement(attacker, ChallengeAchievements.CLERICAL_PRODIGY);
        }
    }

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = loc.getWorld().spawn(loc, Zombie.class);
        jimmy.setBaby(false);
        jimmy.setCustomNameVisible(true);
        jimmy.setCustomName(this.getSpec().getClassNameShortWithBrackets() + " " + this.getColoredName() + " " + ChatColor.RED + this.health + "❤"); // TODO add level and class into the name of this jimmy
        jimmy.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
        //prevents jimmy from moving
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) jimmy).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);

        if (inv != null) {
            jimmy.getEquipment().setBoots(inv.getBoots());
            jimmy.getEquipment().setLeggings(inv.getLeggings());
            jimmy.getEquipment().setChestplate(inv.getChestplate());
            jimmy.getEquipment().setHelmet(inv.getHelmet());
            jimmy.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            jimmy.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        }
        if (dead) {
            jimmy.remove();
        }
        return jimmy;
    }

    public void updateJimmyHealth() {
        if (getEntity() instanceof Zombie) {
            if (isDead()) {
                getEntity().setCustomName("");
            } else {
                String oldName = getEntity().getCustomName();
                String newName = oldName.substring(0, oldName.lastIndexOf(' ') + 1) + ChatColor.RED + getHealth() + "❤";
                getEntity().setCustomName(newName);
            }
        }
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    private void setWalkSpeed(float walkspeed) {
        this.walkspeed = walkspeed;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) player.setWalkSpeed(this.walkspeed);
    }

    public void displayActionBar() {
        StringBuilder actionBarMessage = new StringBuilder(ChatColor.GOLD + "§lHP: ");
        float healthRatio = (float) health / maxHealth;
        if (healthRatio >= .75) {
            actionBarMessage.append(ChatColor.DARK_GREEN);

        } else if (healthRatio >= .25) {
            actionBarMessage.append(ChatColor.YELLOW);

        } else {
            actionBarMessage.append(ChatColor.RED);

        }
        actionBarMessage.append("§l").append(health).append(ChatColor.GOLD).append("§l/§l").append(maxHealth).append("    ");
        actionBarMessage.append(team.boldColoredPrefix()).append(" TEAM  ");
        for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldowns()) {
            actionBarMessage.append(abstractCooldown.getNameAbbreviation());
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

        ArmorManager.resetArmor(player, getSpecClass(), getTeam());

        if (cooldownManager.hasCooldownFromName("Cloaked") && !hasFlag()) {
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
        } else {
            getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.showPlayer(player);
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
        ItemStack weapon = new ItemStack(this.weapon.item);
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
        weaponLeftClick(player);

        updateRedItem(player);
        updatePurpleItem(player);
        updateBlueItem(player);
        updateOrangeItem(player);
        updateHorseItem(player);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "Flag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(8, compass);
    }

    public void weaponLeftClick(Player player) {
        player.getInventory().setItem(
                0,
                new ItemBuilder(weapon.item)
                        .name(ChatColor.GOLD + "Warlord's " + weapon.name + " of the " + spec.getClass().getSimpleName())
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

    public void weaponRightClick(Player player) {
        player.getInventory().setItem(
                0,
                new ItemBuilder(weapon.item)
                        .name(ChatColor.GREEN + spec.getWeapon().getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Right-Click!")
                        .lore(ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getWeapon().getEnergyCost(),
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getWeapon().getCritChance() + "%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getWeapon().getCritMultiplier() + "%",
                                "",
                                spec.getWeapon().getDescription(),
                                "",
                                ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEFT-CLICK " + ChatColor.GREEN + "to view weapon stats!")
                        .unbreakable()
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                        .get());
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
            player.getInventory().setItem(7, horse);
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

    public AbstractPlayerClass getSpec() {
        return spec;
    }

    public void setSpec(AbstractPlayerClass spec, SkillBoosts skillBoost) {
        Warlords.getPlayerSettings(uuid).setSelectedSpec(Specializations.getSpecFromName(spec.getName()));
        Warlords.getPlayerSettings(uuid).setSkillBoostForSelectedSpec(skillBoost);
        Player player = Bukkit.getPlayer(uuid);
        this.spec = spec;
        this.specClass = Warlords.getPlayerSettings(uuid).getSelectedSpec();
        this.weapon = Weapons.getSelected(player, this.specClass);
        this.maxHealth = (int) (this.spec.getMaxHealth() * (gameState.getGame().getAddons().contains(GameAddon.COOLDOWN_MODE) ? 1.5 : 1));
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

        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        databasePlayer.getSpec(specClass).setSkillBoost(skillBoost);
        DatabaseManager.updatePlayerAsync(databasePlayer);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void showDeathAnimation() {
        if (this.entity instanceof Zombie) {
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
        if (respawnTimer == 0) {
            respawn();
        } else if (respawnTimer > 0) {
            minuteStats.addTotalRespawnTime();
            respawnTimer--;
            if (respawnTimer <= 11) {
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
        if (entity instanceof Player) {
            updatePlayer((Player) entity);
        } else {
            this.entity = spawnJimmy(respawnPoint, this.entity.getEquipment());
        }
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

    public void addEnergy(WarlordsPlayer giver, String ability, float amount) {
        if (energy + amount > maxEnergy) {
            this.energy = maxEnergy;
        } else if (energy + amount > 0) {
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
    }

    public void subtractEnergy(int amount) {
        if (!infiniteEnergy) {
            amount *= energyModifier;
            if (energy - amount > maxEnergy) {
                energy = maxEnergy;
            } else {
                this.energy -= amount;
            }
        }
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

    public LinkedHashMap<WarlordsPlayer, Integer> getHitBy() {
        return hitBy;
    }

    public LinkedHashMap<WarlordsPlayer, Integer> getHealedBy() {
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

    public void addAbsorbed(float amount) {
        this.minuteStats.addAbsorbed((long) amount);
    }

    public ItemStack getStatItemStack(String name) {
        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.AQUA + "Stat Breakdown (" + name + "):");
        List<PlayerStatisticsMinute.Entry> entries = this.minuteStats.getEntries();
        int length = entries.size();
        for (int i = 0; i < length; i++) {
            PlayerStatisticsMinute.Entry entry = entries.get(length - i - 1);
            switch (name) {
                case "Kills":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getKills()));
                    break;
                case "Assists":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getAssists()));
                    break;
                case "Deaths":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getDeaths()));
                    break;
                case "Damage":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getDamage()));
                    break;
                case "Healing":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getHealing()));
                    break;
                case "Absorbed":
                    lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getAbsorbed()));
                    break;
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String getStatString(String name) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.AQUA + "Stat Breakdown (" + name + "):");
        List<PlayerStatisticsMinute.Entry> entries = this.minuteStats.getEntries();
        int length = entries.size();
        for (int i = 0; i < length; i++) {
            PlayerStatisticsMinute.Entry entry = entries.get(length - i - 1);
            stringBuilder.append("\n");
            stringBuilder.append(ChatColor.WHITE + "Minute ").append(i + 1).append(": ").append(ChatColor.GOLD);
            switch (name) {
                case "Kills":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getKills()));
                    break;
                case "Assists":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getAssists()));
                    break;
                case "Deaths":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getDeaths()));
                    break;
                case "Damage":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getDamage()));
                    break;
                case "Healing":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getHealing()));
                    break;
                case "Absorbed":
                    stringBuilder.append(NumberFormat.addCommaAndRound(entry.getAbsorbed()));
                    break;
            }
        }
        return stringBuilder.toString();
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

    public void updatePlayerReference(@Nullable Player player) {
        if (player == this.entity) {
            return;
        }
        Location loc = this.getLocation();

        if (player == null) {
            if (this.entity instanceof Player) {
                ((Player) this.entity).getInventory().setHeldItemSlot(0);
                this.entity = spawnJimmy(loc, ((Player) this.entity).getEquipment());
            }
        } else {
            if (this.entity instanceof Zombie) { // This could happen if there was a problem during the quit event
                this.entity.remove();
            }
            player.teleport(loc);
            this.entity = player;
            updatePlayer(player);
        }
    }

    public void updatePlayer(@Nonnull Player player) {
        player.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
        player.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        player.setWalkSpeed(walkspeed);
        player.setMaxHealth(40);
        player.setLevel((int) this.getMaxEnergy());
        player.getInventory().clear();
        this.spec.getWeapon().updateDescription(player);
        this.spec.getRed().updateDescription(player);
        this.spec.getPurple().updateDescription(player);
        this.spec.getBlue().updateDescription(player);
        this.spec.getOrange().updateDescription(player);
        applySkillBoost(player);
        player.closeInventory();
        this.assignItemLore(player);
        updateArmor();

        resetPlayerAddons();

        if (isDead()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    private void resetPlayerAddons() {
        if (getEntity() instanceof Player) {
            Player player = (Player) getEntity();

            //Soulbinding weapon enchant
            if (getCooldownManager().hasCooldown(Soulbinding.class)) {
                ItemMeta itemMeta = player.getInventory().getItem(0).getItemMeta();
                itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
                player.getInventory().getItem(0).setItemMeta(itemMeta);
            } else {
                player.getInventory().getItem(0).removeEnchantment(Enchantment.OXYGEN);
            }

            //Undying army bone
            if (getCooldownManager().checkUndyingArmy(true)) {
                player.getInventory().setItem(5, UndyingArmy.BONE);
            } else {
                player.getInventory().remove(UndyingArmy.BONE);
            }

            //Arcane shield absorption hearts
            List<ArcaneShield> arcaneShields = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(ArcaneShield.class)
                    .collect(Collectors.toList());
            if (!arcaneShields.isEmpty()) {
                ArcaneShield arcaneShield = arcaneShields.get(0);
                ((CraftPlayer) player).getHandle().setAbsorptionHearts((float) (arcaneShield.getShieldHealth() / (getMaxHealth() * .5) * 20));
            } else {
                ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
            }
        }
    }

    public Specializations getSpecClass() {
        return specClass;
    }

    public Weapons getWeapon() {
        return weapon;
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

    public boolean isEnemyAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDead() &&
                p.getTeam() != getTeam();
    }

    public boolean isEnemy(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() != getTeam();
    }

    public boolean isTeammateAlive(@Nullable Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isTeammateAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDead() &&
                p.getTeam() == getTeam();
    }

    public boolean isTeammate(@Nullable WarlordsPlayer p) {
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
        }
    }

    public PlayingState getGameState() {
        return this.gameState;
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

    public void setVelocity(org.bukkit.util.Vector v) {
        if (cooldownManager.hasCooldownFromName("KB Resistance")) {
            v.multiply(0.75);
        }
        if (cooldownManager.hasCooldownFromName("Vindicate Debuff Immunity")) {
            v.multiply(0.5);
        }
        if (cooldownManager.hasCooldownFromName("KB Increase")) {
            v.multiply(1.5);
        }

        setVelocity(v, true);
    }

    public void setVelocity(org.bukkit.util.Vector v, boolean kbAfterHorse) {
        if ((kbAfterHorse || this.entity.getVehicle() == null)) {
            if (cooldownManager.hasCooldownFromName("KB Resistance")) {
                v.multiply(0.75);
            }
            if (cooldownManager.hasCooldownFromName("Vindicate Debuff Immunity")) {
                v.multiply(0.5);
            }
            if (cooldownManager.hasCooldownFromName("KB Increase")) {
                v.multiply(1.5);
            }

            this.entity.setVelocity(v);
        }
    }

    public void setVelocity(Location from, double multipliedBy, double y, boolean kbAfterHorse) {
        this.setVelocity(from, getLocation(), multipliedBy, y, kbAfterHorse);
    }

    public void setVelocity(Location from, Location to, double multipliedBy, double y, boolean kbAfterHorse) {
        if (((kbAfterHorse && this.entity.getVehicle() != null) || (!kbAfterHorse && this.entity.getVehicle() == null))) {
            if (cooldownManager.hasCooldownFromName("KB Resistance")) {
                this.entity.setVelocity((to.toVector().subtract(from.toVector()).normalize().multiply(multipliedBy).setY(y)).multiply(.75));
            } else {
                this.entity.setVelocity(to.toVector().subtract(from.toVector()).normalize().multiply(multipliedBy).setY(y));
            }
        }
    }

    public World getWorld() {
        return this.entity.getWorld();
    }

    public boolean isInfiniteEnergy() {
        return infiniteEnergy;
    }

    public void setInfiniteEnergy(boolean infiniteEnergy) {
        this.infiniteEnergy = infiniteEnergy;
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

    public List<Achievement.AbstractAchievementRecord> getAchievementsUnlocked() {
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

    public boolean isOnline() {
        return this.entity instanceof Player;
    }

    @Nullable
    public CompassTargetMarker getCompassTarget() {
        return this.compassTarget;
    }

    public void runEverySecond() {
        this.spec.runEverySecond();

        // Gives the player their respawn timer as display.
        this.decrementRespawnTimer();
    }

//    @Override
//    public int hashCode() {
//        return this.uuid.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final WarlordsPlayer other = (WarlordsPlayer) obj;
//        return Objects.equals(this.uuid, other.uuid);
//    }
}
