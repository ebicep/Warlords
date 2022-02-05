package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagInfo;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.option.marker.CompassTargetMarker;
import com.ebicep.warlords.maps.option.marker.FlagHolder;
import com.ebicep.warlords.maps.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.util.*;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class WarlordsPlayer {

    private final String name;
    private final UUID uuid;
    private final PlayingState gameState;
    private Team team;
    private AbstractPlayerClass spec;
    private Classes specClass;
    private Weapons weapon;
    private int health;
    private int maxHealth;
    private int regenTimer;
    private int respawnTimer = -1;
    private boolean dead = false;
    private float energy;
    private float maxEnergy;
    private float horseCooldown;
    private int healPowerupDuration = 4;
    private float currentHealthModifier = 1;
    private int flagCooldown;
    private int hitCooldown;
    // We have to store these in here as the new player might logout midgame
    private float walkspeed = 1;
    private int blocksTravelledCM = 0;
    private boolean infiniteEnergy;
    private boolean disableCooldowns;
    private double energyModifier;
    private double cooldownModifier;
    private boolean takeDamage = true;
    private boolean canCrit = true;

    private final List<Float> recordDamage = new ArrayList<>();

    private final PlayerStatistics stats;
    //assists = player - timeLeft(10 seconds)
    private final LinkedHashMap<WarlordsPlayer, Integer> hitBy = new LinkedHashMap<>();
    private final LinkedHashMap<WarlordsPlayer, Integer> healedBy = new LinkedHashMap<>();

    private final List<Location> locations = new ArrayList<>();

    private final CalculateSpeed speed;
    private boolean powerUpHeal = false;

    private final Location deathLocation;
    private LivingEntity entity = null;

    private final CooldownManager cooldownManager = new CooldownManager(this);
    @Nullable
    private FlagInfo carriedFlag = null;
    @Nullable
    private CompassTargetMarker compassTarget = null;

    /**
     * @param player is the assigned player as WarlordsPlayer.
     * @param gameState what gamestate should the WarlordsPlayer be assigned to.
     * @param team optional team parameter to assign the WarlordsPlayer to a team.
     * @param settings what settings profile does the WarlordsPlayer use.
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
        this.stats = new PlayerStatistics();
        this.team = team;
        this.specClass = settings.getSelectedClass();
        this.spec = specClass.create.get();
        this.maxHealth = this.spec.getMaxHealth();
        this.health = this.maxHealth;
        this.energy = 0;
        this.energyModifier = 1;
        this.maxEnergy = this.spec.getMaxEnergy();
        this.horseCooldown = 0;
        this.flagCooldown = 0;
        this.cooldownModifier = 1;
        this.hitCooldown = 20;
        this.speed = new CalculateSpeed(this::setWalkSpeed, 13);
        Player p = player.getPlayer();
        this.entity = spawnJimmy(p == null ? Warlords.getRejoinPoint(uuid) : p.getLocation(), null);
        this.weapon = Weapons.getSelected(player, settings.getSelectedClass());
        this.deathLocation = this.entity.getLocation();
        updatePlayerReference(p);
        this.compassTarget = gameState.getGame()
                .getMarkers(CompassTargetMarker.class)
                .stream().filter(c -> c.isEnabled())
                .sorted(Comparator.comparing((CompassTargetMarker c) -> c.getCompassTargetPriority(this)).reversed())
                .findFirst()
                .orElse(null);
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

    public static final String GIVE_ARROW = ChatColor.RED + "\u00AB";
    public static final String RECEIVE_ARROW = ChatColor.GREEN + "\u00BB";

    private void addHealingDamageInstance(WarlordsDamageHealingEvent event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (event.isHealingInstance()) {
            addHealingInstance(event);
        } else {
            addDamageInstance(event);
        }
    }
    /**
     * Adds a damage instance to an ability or a player.
     *
     * @param attacker Assigns the damage value to the original caster.
     * @param ability Name of the ability.
     * @param min The minimum damage amount.
     * @param max The maximum damage amount.
     * @param critChance The critical chance of the damage instance.
     * @param critMultiplier The critical multiplier of the damage instance.
     * @param ignoreReduction Whether the instance has to ignore damage reductions.
     */
    public void addDamageInstance(
            WarlordsPlayer attacker,
            String ability,
            float min,
            float max,
            int critChance,
            int critMultiplier,
            boolean ignoreReduction
    ) {
        this.addHealingDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, false, true));
    }
    private void addDamageInstance(WarlordsDamageHealingEvent event) {
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

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || getGameState() != getGame().getState()) {
            return;
        }

        // Inferno
        if (!attacker.getCooldownManager().getCooldown(Inferno.class).isEmpty() && (!isMeleeHit && !ability.equals("Time Warp"))) {
            critChance += attacker.getSpec().getOrange().getCritChance();
            critMultiplier += attacker.getSpec().getOrange().getCritMultiplier();
        }

        // Critical Hits
        float damageValue = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
        boolean isCrit = false;
        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            damageValue *= critMultiplier / 100f;
        }
        final float damageHealValueBeforeReduction = damageValue;
        addAbsorbed(Math.abs(damageValue - (damageValue *= 1 - spec.getDamageResistance() / 100f)));

        if (attacker == this && (isFallDamage || isMeleeHit)) {

            if (isMeleeHit) {
                // True damage
                sendMessage(GIVE_ARROW + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(min) + ChatColor.GRAY + " melee damage.");
                regenTimer = 10;
                if (health - min <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    die(attacker);
                    gameState.addKill(team, false);
                    if (entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(min) + ChatColor.GRAY + " melee damage and died.", 0, 40, 0);
                    }

                    health = 0;
                } else {
                    health -= min;
                }

                playHurtAnimation(this.entity, attacker);

            } else {

                // Fall Damage
                sendMessage(GIVE_ARROW + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(damageValue) + ChatColor.GRAY + " fall damage.");
                regenTimer = 10;
                if (health - damageValue < 0 && !cooldownManager.checkUndyingArmy(false)) {
                    die(attacker);
                    gameState.addKill(team, false); // TODO, fall damage is only a suicide if it happens more than 5 seconds after the last damage
                    // Title card "YOU DIED!"
                    if (entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(damageValue) + ChatColor.GRAY + " fall damage and died.", 0, 40, 0);
                    }

                    health = 0;
                } else {
                    health -= damageValue;
                }

                playHurtAnimation(entity, attacker);

                addAbsorbed(Math.abs(damageValue * spec.getDamageResistance() / 100));
            }
            cancelHealingPowerUp();
            return;
        }

        // Reduction before Intervene.
        if (!ignoreReduction) {

            // Flag carrier multiplier.
            
            damageValue *= getFlagDamageMultiplier();

            // Checks whether the player is standing in a Hammer of Light.
            if (!HammerOfLight.standingInHammer(attacker, entity)) {

                // Damage Increase
                // Example: 1.1 = 10% increase.

                // Checks whether the player has Berserk active.
                for (Cooldown cooldown : attacker.getCooldownManager().getCooldown(Berserk.class)) {
                    damageValue *= 1.3;
                }

                // Checks whether the player has Berserk active for self damage.
                for (Cooldown cooldown : cooldownManager.getCooldown(Berserk.class)) {
                    damageValue *= 1.1;
                }

                // Checks whether the player has been crippled by Healing Totem.
                if (!attacker.getCooldownManager().getCooldownFromName("Totem Crippling").isEmpty()) {
                    damageValue *= .75;
                }

                // Checks whether the player has been crippled by Crippling Strike.
                if (!attacker.getCooldownManager().getCooldown(CripplingStrike.class).isEmpty()) {
                    CripplingStrike cripplingStrike = (CripplingStrike) attacker.getCooldownManager().getCooldown(CripplingStrike.class).get(0).getCooldownObject();
                    damageValue *= .9 - (cripplingStrike.getConsecutiveStrikeCounter() * .05);
                }
            }
        }

        // Intervene
        if (!cooldownManager.getCooldown(Intervene.class).isEmpty() && cooldownManager.getCooldown(Intervene.class).get(0).getFrom() != this && !HammerOfLight.standingInHammer(attacker, entity) && isEnemy(attacker)) {
            Cooldown interveneCooldown = cooldownManager.getCooldown(Intervene.class).get(0);
            Intervene intervene = (Intervene) interveneCooldown.getCooldownObject();
            WarlordsPlayer intervenedBy = interveneCooldown.getFrom();

            damageValue *= .5;
            intervenedBy.addAbsorbed(damageValue);
            intervenedBy.setRegenTimer(10);
            intervene.addDamagePrevented(damageValue);
            intervenedBy.addDamageInstance(attacker, "Intervene", damageValue, damageValue, isCrit ? 100 : -1, 100, false);
            Location loc = getLocation();
            //EFFECTS + SOUNDS
            gameState.getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> p.playSound(loc, "warrior.intervene.block", 2, 1));
            playHitSound(attacker);
            entity.playEffect(EntityEffect.HURT);
            intervenedBy.getEntity().playEffect(EntityEffect.HURT);

            // Red line particle if the player gets hit
            Location lineLoc = getLocation().add(0, 1, 0);
            lineLoc.setDirection(lineLoc.toVector().subtract(intervenedBy.getLocation().add(0, 1, 0).toVector()).multiply(-1));
            for (int i = 0; i < Math.floor(getLocation().distance(intervenedBy.getLocation())) * 2; i++) {
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), lineLoc, 500);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), lineLoc, 500);
                lineLoc.add(lineLoc.getDirection().multiply(.5));
            }

            // Remove horses.
            removeHorse();
            intervenedBy.removeHorse();

            // Orbs of Life
            spawnOrbs(ability, attacker);

        } else {

            // Damage reduction after Intervene
            if (!ignoreReduction) {
                if (!HammerOfLight.standingInHammer(attacker, entity)) {

                    // Damage Reduction
                    // Example: .8 = 20% reduction.

                    // Checks whether the player has Ice Barrier Active.
                    for (Cooldown cooldown : cooldownManager.getCooldown(IceBarrier.class)) {
                        IceBarrier iceBarrier = (IceBarrier) cooldown.getCooldownObject();
                        addAbsorbed(Math.abs(damageValue - (damageValue *= iceBarrier.getDamageReduction())));
                    }

                    // Checks whether the player has Chain Lightning Active.
                    if (!cooldownManager.getCooldown(ChainLightning.class).isEmpty()) {
                        addAbsorbed(Math.abs(damageValue - (damageValue *= 1 - (Collections.max(cooldownManager.getCooldown(ChainLightning.class).stream()
                                .map(cd -> ((ChainLightning) cd.getCooldownObject()).getDamageReduction())
                                .collect(Collectors.toList())) * .1))));
                    }

                    // Checks whether the player has Spirit Link Active.
                    for (Cooldown cooldown : cooldownManager.getCooldown(SpiritLink.class)) {
                        addAbsorbed(Math.abs(damageValue - (damageValue *= .8)));
                    }

                    // Checks whether the player has Ice Barrier Active.
                    for (Cooldown cooldown : cooldownManager.getCooldown(LastStand.class)) {
                        WarlordsPlayer lastStandedBy = cooldown.getFrom();
                        LastStand lastStand = (LastStand) cooldown.getCooldownObject();
                        if (lastStandedBy == this) {
                            damageValue *= lastStand.getSelfDamageReduction();
                        } else {
                            damageValue *= lastStand.getTeammateDamageReduction();
                        }
                    }

                    // Checks whether the player has a Flag.
                    if (!cooldownManager.getCooldownFromName("Flag Damage Reduction").isEmpty()) {
                        damageValue *= .9;
                    }
                }
            }

            // Arcane Shield
            if (!cooldownManager.getCooldown(ArcaneShield.class).isEmpty() && isEnemy(attacker) && !HammerOfLight.standingInHammer(attacker, entity)) {
                ArcaneShield arcaneShield = (ArcaneShield) spec.getBlue();
                //adding dmg to shield
                arcaneShield.addShieldHealth(-damageValue);
                //check if broken
                if (arcaneShield.getShieldHealth() < 0) {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts(0);
                    }

                    cooldownManager.removeCooldown(ArcaneShield.class);
                    addDamageInstance(attacker, ability, -arcaneShield.getShieldHealth(), -arcaneShield.getShieldHealth(), isCrit ? 100 : -1, 100, true);

                    addAbsorbed(-(((ArcaneShield) spec.getBlue()).getShieldHealth()));

                    return;
                } else {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts((float) (arcaneShield.getShieldHealth() / (maxHealth * .5) * 20));
                    }

                    if (isMeleeHit) {
                        sendMessage(GIVE_ARROW + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s melee " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your melee hit was absorbed by " + name);
                    } else {
                        sendMessage(GIVE_ARROW + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s " + ability + " " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your " + ability + " was absorbed by " + name + ChatColor.GRAY + ".");
                    }

                    addAbsorbed(Math.abs(damageHealValueBeforeReduction));
                }

                //LAST STAND HEALING
                if (!cooldownManager.getCooldown(LastStand.class).isEmpty()) {
                    for (Cooldown cooldown : cooldownManager.getCooldown(LastStand.class)) {
                        WarlordsPlayer lastStandedBy = cooldown.getFrom();
                        lastStandedBy.addAbsorbed(damageValue);
                        //HEALING FROM LASTSTAND
                        if (lastStandedBy != this) {
                            float finalDamageHealValue = damageValue;
                            boolean finalIsCrit = isCrit;
                            //healing if multiple last stands
                            lastStandedBy.getCooldownManager().getCooldown(LastStand.class).stream()
                                    .filter(cd -> cd.getCooldownObject() == cooldown.getCooldownObject() && cd.getTimeLeft() > 0)
                                    .forEach(ls -> lastStandedBy.addHealingInstance(lastStandedBy, "Last Stand", finalDamageHealValue, finalDamageHealValue, finalIsCrit ? 100 : -1, 100, false, true));
                        }
                    }
                }

                //ORBS
                spawnOrbs(ability, attacker);

                playHurtAnimation(this.entity, attacker);

                if (!isMeleeHit) {
                    playHitSound(attacker);
                }
                removeHorse();

            } else {

                boolean debt = false;

                if (isEnemy(attacker)) {
                    hitBy.put(attacker, 10);

                    cancelHealingPowerUp();

                    removeHorse();
                    regenTimer = 10;

                    //LAST STAND HEALING
                    if (!cooldownManager.getCooldown(LastStand.class).isEmpty() && !HammerOfLight.standingInHammer(attacker, entity)) {
                        for (Cooldown cooldown : cooldownManager.getCooldown(LastStand.class)) {
                            WarlordsPlayer lastStandedBy = cooldown.getFrom();
                            lastStandedBy.addAbsorbed(damageValue);
                            //HEALING FROM LASTSTAND
                            if (lastStandedBy != this) {
                                float finalDamageHealValue = damageValue;
                                boolean finalIsCrit = isCrit;
                                //healing if multiple last stands
                                lastStandedBy.getCooldownManager().getCooldown(LastStand.class).stream()
                                        .filter(cd -> cd.getCooldownObject() == cooldown.getCooldownObject() && cd.getTimeLeft() > 0)
                                        .forEach(ls -> lastStandedBy.addHealingInstance(lastStandedBy, "Last Stand", finalDamageHealValue, finalDamageHealValue, finalIsCrit ? 100 : -1, 100, false, false));
                            }
                        }
                    }

                    // Death's Debt
                    List<Cooldown> debtsCooldown = cooldownManager.getCooldownFromName("Spirits Respite");
                    if (!debtsCooldown.isEmpty()) {
                        ((DeathsDebt) debtsCooldown.get(0).getCooldownObject()).addDelayedDamage(damageValue);
                        debt = true;
                    }

                    if (isCrit) {
                        if (isMeleeHit) {
                            sendMessage(GIVE_ARROW + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + "§l" + Math.round(damageValue) + "! " + ChatColor.GRAY + "critical melee damage.");
                            attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageValue) + "! " + ChatColor.GRAY + "critical melee damage.");
                        } else {
                            sendMessage(GIVE_ARROW + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + "§l" + Math.round(damageValue) + "! " + ChatColor.GRAY + "critical damage.");
                            attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageValue) + "! " + ChatColor.GRAY + "critical damage.");
                        }
                    } else {
                        if (isMeleeHit) {
                            sendMessage(GIVE_ARROW + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + Math.round(damageValue) + " " + ChatColor.GRAY + "melee damage.");
                            attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + Math.round(damageValue) + " " + ChatColor.GRAY + "melee damage.");
                        } else {
                            sendMessage(GIVE_ARROW + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + Math.round(damageValue) + " " + ChatColor.GRAY + "damage.");
                            attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + Math.round(damageValue) + " " + ChatColor.GRAY + "damage.");
                        }
                    }

                    // Repentance
                    if (spec instanceof Spiritguard) {
                        ((Repentance) spec.getBlue()).addToPool(damageValue);
                    }
                    if (attacker.getSpec() instanceof Spiritguard) {
                        if (!attacker.getCooldownManager().getCooldown(Repentance.class).isEmpty()) {
                            Repentance repentance = (Repentance) attacker.getSpec().getBlue();
                            int healthToAdd = (int) (repentance.getPool() * (repentance.getDamageConvertPercent() / 100f)) + 10;
                            attacker.addHealingInstance(attacker, "Repentance", healthToAdd, healthToAdd, -1, 100, false, false);
                            repentance.setPool(repentance.getPool() * .5f);
                            attacker.addEnergy(attacker, "Repentance", (float) (healthToAdd * .035));
                        }
                    }

                    // Orbs of Life + Spawns additional orb if the ability is Crippling Strike
                    spawnOrbs(ability, attacker);
                    if (ability.equals("Crippling Strike")) {
                        spawnOrbs(ability, attacker);
                    }

                    // Protector's Strike
                    if (ability.equals("Protector's Strike")) {

                        // Self Heal
                        if (Warlords.getPlayerSettings(attacker.uuid).getSkillBoostForClass() == ClassesSkillBoosts.PROTECTOR_STRIKE) {
                            attacker.addHealingInstance(attacker, ability, damageValue * 0.6f, damageValue * 0.6f, isCrit ? 100 : -1, 100, false, false);
                        } else {
                            attacker.addHealingInstance(attacker, ability, damageValue * 0.5f, damageValue * 0.5f, isCrit ? 100 : -1, 100, false, false);
                        }

                        // Ally Heal
                        for (WarlordsPlayer ally : PlayerFilter
                                .entitiesAround(attacker, 10, 10, 10)
                                .aliveTeammatesOfExcludingSelf(attacker)
                                .sorted(Comparator.comparing((WarlordsPlayer p) -> p.getCooldownManager().hasCooldown(HolyRadianceProtector.class) ? 0 : 1)
                                .thenComparing(Utils.sortClosestBy(WarlordsPlayer::getLocation, attacker.getLocation())))
                                .limit(2)
                        ) {
                            if (Warlords.getPlayerSettings(attacker.uuid).getSkillBoostForClass() == ClassesSkillBoosts.PROTECTOR_STRIKE) {
                                ally.addHealingInstance(attacker, ability, damageValue * 1.2f, damageValue * 1.2f, isCrit ? 100 : -1, 100, false, false);
                            } else {
                                ally.addHealingInstance(attacker, ability, damageValue, damageValue, isCrit ? 100 : -1, 100, false, false);
                            }
                        }
                    }
                }

                // Blood Lust
                if (!attacker.getCooldownManager().getCooldown(BloodLust.class).isEmpty()) {
                    BloodLust bloodLust = (BloodLust) attacker.getSpec().getBlue();
                    attacker.addHealingInstance(attacker, "Blood Lust", damageValue * (bloodLust.getDamageConvertPercent() / 100f), damageValue * (bloodLust.getDamageConvertPercent() / 100f), -1, 100, false, false);
                }

                updateJimmyHealth();

                // Adding/subtracting health

                // debt and healing
                if (!debt && takeDamage) {
                    this.health -= Math.round(damageValue);
                }

                attacker.addDamage(damageValue);
                playHurtAnimation(this.entity, attacker);
                recordDamage.add(damageValue);

                // The player died.
                if (this.health <= 0 && !cooldownManager.checkUndyingArmy(false)) {
                    if (attacker.entity instanceof Player) {
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 1);
                        ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 0.5f);
                    }

                    die(attacker);

                    attacker.addKill();

                    sendMessage(ChatColor.GRAY + "You were killed by " + attacker.getColoredName());
                    attacker.sendMessage(ChatColor.GRAY + "You killed " + getColoredName());

                    gameState.getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> {
                        if (p != this.entity && p != attacker.entity) {
                            p.sendMessage(getColoredName() + ChatColor.GRAY + " was killed by " + attacker.getColoredName());
                        }
                    });
                    gameState.getGame().spectators().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Bukkit.getPlayer(uuid).sendMessage(getColoredName() + ChatColor.GRAY + " was killed by " + attacker.getColoredName());
                        }
                    });
                    gameState.addKill(team, false);

                    // Title card "YOU DIED!"
                    if (this.entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + attacker.getName() + " killed you.", 0, 40, 0);
                    }
                } else {
                    if (!isMeleeHit && this != attacker && damageValue != 0) {
                        playHitSound(attacker);
                    }
                }
            }
        }

        // Windfury Weapon
        if (isMeleeHit) {
            if (!attacker.getCooldownManager().getCooldown(Windfury.class).isEmpty()) {
                int windfuryActivate = (int) (Math.random() * 100);
                if (((Windfury) attacker.getSpec().getPurple()).isFirstProc()) {
                    ((Windfury) attacker.getSpec().getPurple()).setFirstProc(false);
                    windfuryActivate = 0;
                }
                if (windfuryActivate < ((Windfury) attacker.getSpec().getPurple()).getProcChance()) {
                    new BukkitRunnable() {
                        int counter = 0;

                        @Override
                        public void run() {
                            gameState.getGame().forEachOnlinePlayerWithoutSpectators((player1, t) -> {
                                player1.playSound(getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                            });

                            if (Warlords.getPlayerSettings(attacker.uuid).getSkillBoostForClass() == ClassesSkillBoosts.WINDFURY_WEAPON) {
                                addDamageInstance(attacker, "Windfury Weapon", min * 1.35f * 1.2f, max * 1.35f * 1.2f, 25, 200, false);
                            } else {
                                addDamageInstance(attacker, "Windfury Weapon", min * 1.35f, max * 1.35f, 25, 200, false);
                            }

                            counter++;

                            if (counter == 2) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 3, 3);
                }

            // Earthliving Weapon.
            } else if (!attacker.getCooldownManager().getCooldown(Earthliving.class).isEmpty()) {
                int earthlivingActivate = (int) (Math.random() * 100);
                Earthliving earthliving = (Earthliving) attacker.getSpec().getPurple();
                if (earthliving.isFirstProc()) {
                    earthliving.setFirstProc(false);
                    earthlivingActivate = 0;
                }
                if (earthlivingActivate < earthliving.getProcChance()) {
                    boolean earthlivingBoost = Warlords.getPlayerSettings(attacker.uuid).getSkillBoostForClass() == ClassesSkillBoosts.EARTHLIVING_WEAPON;
                    float multiplyBy = earthlivingBoost ? 2.5f : 2.4f;

                    attacker.addHealingInstance(attacker, "Earthliving Weapon", 132 * multiplyBy, 179 * multiplyBy, 25, 200, false, false);

                    gameState.getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> {
                        p.playSound(getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                    });

                    for (WarlordsPlayer nearPlayer : PlayerFilter
                            .entitiesAround(attacker, 6, 6, 6)
                            .aliveTeammatesOfExcludingSelf(attacker)
                            .limit(2)
                    ) {
                        nearPlayer.addHealingInstance(attacker, "Earthliving Weapon", 132 * multiplyBy, 179 * multiplyBy, 25, 200, false, false);
                    }
                }
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
    public void addHealingInstance(
            WarlordsPlayer attacker,
            String ability,
            float min,
            float max,
            int critChance,
            int critMultiplier,
            boolean ignoreReduction,
            boolean isLastStandFromShield
    ) {
        addHealingDamageInstance(new WarlordsDamageHealingEvent(this, attacker, ability, min, max, critChance, critMultiplier, ignoreReduction, isLastStandFromShield, false));
    }

    private void addHealingInstance(WarlordsDamageHealingEvent event) {
        WarlordsPlayer attacker = event.getAttacker();
        String ability = event.getAbility();
        float min = event.getMin();
        float max = event.getMax();
        int critChance = event.getCritChance();
        int critMultiplier = event.getCritMultiplier();
        boolean ignoreReduction = event.isIgnoreReduction();
        boolean isLastStandFromShield = event.isIsLastStandFromShield();
        boolean isMeleeHit = ability.isEmpty();

        // Spawn Protection / Undying Army / Game State
        if ((dead && !cooldownManager.checkUndyingArmy(false)) || getGameState() != getGame().getState()) {
            return;
        }

        // Critical Hits
        float healValue = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
        boolean isCrit = false;

        if (crit <= critChance && attacker.canCrit) {
            isCrit = true;
            healValue *= critMultiplier / 100f;
        }

        // Checks whether the player has been wounded.
        if (!cooldownManager.getCooldown(WoundingStrikeBerserker.class).isEmpty()) {
            healValue *= .6;
        } else if (!cooldownManager.getCooldown(WoundingStrikeDefender.class).isEmpty()) {
            healValue *= .75;
        }

        // Self Healing
        if (this == attacker) {

            if (this.health + healValue > this.maxHealth) {
                healValue = this.maxHealth - this.health;
            }

            if (healValue < 0) return;

            if (healValue != 0) {
                if (isCrit) {
                    if (isLastStandFromShield) {
                        sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(healValue) + " Absorbed! " + ChatColor.GRAY + "health.");
                    } else {
                        sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(healValue) + "! " + ChatColor.GRAY + "health.");
                    }
                } else {
                    if (isLastStandFromShield) {
                        sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your " + ability + " healed you for " + ChatColor.GREEN + "" + Math.round(healValue) + " Absorbed " + ChatColor.GRAY + "health.");
                    } else {
                        sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " Your " + ability + " healed you for " + ChatColor.GREEN + "" + Math.round(healValue) + " " + ChatColor.GRAY + "health.");
                    }
                }
                health += healValue;
                addHealing(healValue);

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

                if (healValue < 0) return;

                if (healValue != 0) {
                    if (isCrit) {
                        sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(healValue) + "! " + ChatColor.GRAY + "health.");
                        attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "Your " + ability + " critically healed " + name + " for " + ChatColor.GREEN + "§l" + Math.round(healValue) + "! " + ChatColor.GRAY + "health.");
                    } else {
                        sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " healed for " + ChatColor.GREEN + "" + Math.round(healValue) + " " + ChatColor.GRAY + "health.");
                        attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "Your " + ability + " healed " + name + " for " + ChatColor.GREEN + "" + Math.round(healValue) + " " + ChatColor.GRAY + "health.");
                    }
                }

                health += healValue;
                attacker.addHealing(healValue);

                if (!isMeleeHit && !ability.equals("Healing Rain")) {
                    playHitSound(attacker);
                }
            }
        }
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
     * @param entity which entity is assigned to the hurt animation?
     * @param hurtPlayer what warlords player should play the hurt animation?
     */
    private void playHurtAnimation(LivingEntity entity, WarlordsPlayer hurtPlayer) {
        entity.playEffect(EntityEffect.HURT);
        for (Player player1 : hurtPlayer.getWorld().getPlayers()) {
            player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 2, 1);
        }
    }

    /**
     * @param ability which ability should drop Orbs of Life.
     * @param attacker is the caster of the ability.
     */
    public void spawnOrbs(String ability, WarlordsPlayer attacker) {
        if (!attacker.getCooldownManager().getCooldown(OrbsOfLife.class).isEmpty() && !ability.isEmpty() && !ability.equals("Intervene")) {
            attacker.getCooldownManager().getCooldown(OrbsOfLife.class).stream()
                    .filter(cooldown -> !cooldown.isHidden())
                    .forEach(cooldown -> {
                        OrbsOfLife orbsOfLife = (OrbsOfLife) cooldown.getCooldownObject();
                        Location location = getLocation();
                        Location spawnLocation = orbsOfLife.generateSpawnLocation(location);

                        OrbsOfLife.Orb orb = new OrbsOfLife.Orb(((CraftWorld) location.getWorld()).getHandle(), spawnLocation, attacker);
                        orbsOfLife.getSpawnedOrbs().add(orb);
                    });
        }
    }

    public void cancelHealingPowerUp() {
        if (powerUpHeal) {
            powerUpHeal = false;
            sendMessage(ChatColor.GOLD + "Your §a§lHealing Powerup §6has worn off.");
            setHealPowerupDuration(4);
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

        if (attacker != null) {
            if (attacker != this) {
                hitBy.putAll(attacker.getHealedBy());
            }

            hitBy.remove(attacker);
            hitBy.put(attacker, 10);
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
        }
        Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this, attacker));
    }

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable PlayerInventory inv) {
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
            if (isDeath()) {
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
        for (Cooldown cooldown : cooldownManager.getCooldowns()) {
            if (!cooldown.isHidden()) {
                if (cooldown.getActionBarName().equals("WND") || cooldown.getActionBarName().equals("CRIP")) {
                    actionBarMessage.append(ChatColor.RED);
                } else {
                    actionBarMessage.append(ChatColor.GREEN);
                }
                actionBarMessage.append(cooldown.getActionBarName()).append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append((int) cooldown.getTimeLeft() + 1).append(" ");
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
        ClassesSkillBoosts selectedBoost = Classes.getSelectedBoost(Bukkit.getOfflinePlayer(uuid));
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
                            Classes.getSelectedBoost(player).selectedDescription,
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
            ItemStack cooldown = new ItemStack(Material.INK_SACK, ability.getCurrentCooldownItem(), (byte) 8);
            player.getInventory().setItem(slot, cooldown);
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

    public UUID getUuid() {
        return uuid;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public AbstractPlayerClass getSpec() {
        return spec;
    }

    public void setSpec(AbstractPlayerClass spec, ClassesSkillBoosts skillBoost) {
        Warlords.getPlayerSettings(uuid).setSelectedClass(Classes.getClass(spec.getName()));
        Warlords.getPlayerSettings(uuid).setSkillBoostForSelectedClass(skillBoost);
        Player player = Bukkit.getPlayer(uuid);
        this.spec = spec;
        this.specClass = Warlords.getPlayerSettings(uuid).getSelectedClass();
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
        //sync bc player should be cached
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
            stats.addTotalRespawnTime();
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
        event.setCancelled(!(entity instanceof Player && ((Player) entity).isOnline()));
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
                sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
            } else {
                sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + giver.getName() + "'s " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
                giver.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " " + "Your " + ability + " gave " + name + " " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
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

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public float getHorseCooldown() {
        return horseCooldown;
    }

    public void setHorseCooldown(float horseCooldown) {
        this.horseCooldown = horseCooldown;
    }

    public int getFlagCooldown() {
        return flagCooldown;
    }

    public void setFlagCooldown(int flagCooldown) {
        this.flagCooldown = flagCooldown;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public boolean isPowerUpHeal() {
        return powerUpHeal;
    }

    public void setPowerUpHeal(boolean powerUpHeal) {
        this.powerUpHeal = powerUpHeal;
    }

    public void addKill() {
        this.stats.addKill();
    }

    public void addAssist() {
        this.stats.addAssist();
    }

    public LinkedHashMap<WarlordsPlayer, Integer> getHitBy() {
        return hitBy;
    }

    public LinkedHashMap<WarlordsPlayer, Integer> getHealedBy() {
        return healedBy;
    }

    public void addDeath() {
        this.stats.addDeath();
    }
    public void addDamage(float amount) {
        boolean onCarrier = FlagHolder.isPlayerHolderFlag(this);
        this.stats.addDamage((long) amount);
        if (onCarrier) {
            this.stats.addDamageOnCarrier((long) amount);
        }
    }

    public void addHealing(float amount) {
        boolean onCarrier = FlagHolder.isPlayerHolderFlag(this);
        this.stats.addHealing((long) amount);
        if (onCarrier) {
            this.stats.addDamageOnCarrier((long) amount);
        }
    }

    public void addAbsorbed(float amount) {
        this.stats.addAbsorbed((long) amount);
    }

    public ItemStack getStatItemStack(String name) {
        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.AQUA + "Stat Breakdown (" + name + "):");
        List<PlayerStatistics.Entry> entries = this.stats.getEntries();
        int length = entries.size() - 1;
        for (int i = 0; i < length; i++) {
            PlayerStatistics.Entry entry = entries.get(length - i - 1);
            if (name.equals("Kills")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getKills()));
            } else if (name.equals("Assists")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getAssists()));
            } else if (name.equals("Deaths")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getDeaths()));
            } else if (name.equals("Damage")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getDamage()));
            } else if (name.equals("Healing")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getHealing()));
            } else if (name.equals("Absorbed")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(entry.getAbsorbed()));
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
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
        return this.stats.total().getFlagsCaptured();
    }

    public void addFlagCap() {
        this.stats.addFlagCapture();
    }

    public int getFlagsReturned() {
        return this.stats.total().getFlagsReturned();
    }

    public void addFlagReturn() {
        this.stats.addFlagReturned();
    }

    public int getTotalCapsAndReturnsWeighted() {
        PlayerStatistics.Entry total = this.stats.total();
        return (total.getFlagsCaptured() * 5) + total.getFlagsReturned();
    }
    
    public boolean isDead() {
        return dead;
    }

    @Deprecated
    public boolean isDeath() {
        return isDead();
    }

    public boolean isAlive() {
        return !isDeath();
    }

    public void updatePlayerReference(@Nullable Player player) {
        if (player == this.entity) {
            return;
        }
        Location loc = this.getLocation();

        if (player == null) {
            if (this.entity instanceof Player) {
                this.entity = spawnJimmy(loc, ((Player) this.entity).getInventory());
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
        ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(0);
        this.assignItemLore(player);
        ArmorManager.resetArmor(player, getSpecClass(), getTeam());

        if (isDeath()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }
        // TODO Update the inventory based on the status of isUndyingArmyDead here
    }

    public Classes getSpecClass() {
        return specClass;
    }

    public Team getTeam() {
        return team;
    }

    public Game getGame() {
        return this.gameState.getGame();
    }

    @Nonnull
    public LivingEntity getEntity() {
        return this.entity;
    }

    @Nonnull
    public Location getLocation() {
        return this.entity.getLocation();
    }

    @Nonnull
    public Location getLocation(@Nonnull Location copyInto) {
        return this.entity.getLocation(copyInto);
    }

    public boolean isEnemyAlive(Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isEnemyAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDeath() &&
                p.getTeam() != getTeam();
    }

    public boolean isEnemy(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() != getTeam();
    }

    public boolean isTeammateAlive(Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isTeammateAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDeath() &&
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

    public PlayingState getGameState() {
        return this.gameState;
    }

    /**
     * Gets the damage multiplier caused by any carried flag
     * @return The flag damage multiplier, or 1 for easy calculations
     */
    public double getFlagDamageMultiplier() {
        return this.carriedFlag != null
                && this.carriedFlag.getFlag() instanceof PlayerFlagLocation
                && ((PlayerFlagLocation) this.carriedFlag.getFlag()).getPlayer() == this
                ? ((PlayerFlagLocation) this.carriedFlag.getFlag()).getComputedMultiplier()
                : 1;
    }

    public String getColoredName() {
        return getTeam().teamColor() + getName();
    }

    public String getColoredNameBold() {
        return getTeam().teamColor().toString() + ChatColor.BOLD + getName();
    }

    public void setVelocity(org.bukkit.util.Vector v) {
        if (cooldownManager.hasCooldownFromName("KB Resistance")) {
            setVelocity(v.multiply(1), true);
        } else {
            setVelocity(v, true);
        }
    }

    public void setVelocity(org.bukkit.util.Vector v, boolean kbAfterHorse) {
        if ((kbAfterHorse || this.entity.getVehicle() == null)) {
            if (cooldownManager.hasCooldownFromName("KB Resistance")) {
                this.entity.setVelocity(v.multiply(.75));
            } else {
                this.entity.setVelocity(v);
            }
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

    public void setDead(boolean dead) {
        this.dead = dead;
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

    public int getHealPowerupDuration() {
        return healPowerupDuration;
    }

    public void setHealPowerupDuration(int healPowerupDuration) {
        this.healPowerupDuration = healPowerupDuration;
    }

    @Nullable
    public FlagInfo getCarriedFlag() {
        return carriedFlag;
    }

    public void setCarriedFlag(@Nullable FlagInfo carriedFlag) {
        this.carriedFlag = carriedFlag;
    }
    
    @Nonnull
    public PlayerStatistics getStats() {
        return this.stats;
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
    
    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WarlordsPlayer other = (WarlordsPlayer) obj;
        return Objects.equals(this.uuid, other.uuid);
    }
}
