package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HealingPowerup;
import com.ebicep.warlords.abilities.internal.Overheal;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.*;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.FlagInfo;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceManager;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TeleportUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiableFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class WarlordsEntity {

    public static final String WARLORDS_ENTITY_METADATA = "WARLORDS_ENTITY";
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
    protected Entity entity;
    protected Specializations specClass;
    @Nullable
    protected CompassTargetMarker compassTarget;
    protected CooldownManager cooldownManager = new CooldownManager(this);
    protected float currentHealth;
    protected FloatModifiable health;
    protected FloatModifiableFilter maxBaseHealthFilter = new FloatModifiableFilter.BaseFilter();
    private final List<Float> recordDamage = new ArrayList<>();
    private final PlayerStatisticsMinute minuteStats = new PlayerStatisticsMinute();
    private final PlayerStatisticsSecond secondStats = new PlayerStatisticsSecond();
    private final Map<Specializations, PlayerStatisticsMinute> specMinuteStats = new HashMap<>();
    private final List<Achievement.AbstractAchievementRecord<?>> achievementsUnlocked = new ArrayList<>();
    //assists = player - timeLeft(10 seconds)
    private final LinkedHashMap<WarlordsEntity, Integer> hitBy = new LinkedHashMap<>();
    private final LinkedHashMap<WarlordsEntity, Integer> healedBy = new LinkedHashMap<>();
    private final List<Location> locations = new ArrayList<>();
    private Location deathLocation;
    private Vector currentVector;
    private Team team;
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
    private boolean takeDamage = true;
    private boolean canCrit = true;
    @Nullable
    private FlagInfo carriedFlag = null;
    private boolean active = true;
    private boolean isInPve = false;
    private boolean showDebugMessage = false;
    private float bonusAggroWeight = 0;


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
            @Nonnull Entity entity,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull Specializations specClass
    ) {
        this(uuid, name, entity, game, team, specClass.create.get());
        this.specClass = specClass;
    }

    public WarlordsEntity(
            @Nonnull UUID uuid,
            @Nonnull String name,
            @Nonnull Entity entity,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull AbstractPlayerClass playerClass
    ) {
        this.name = name;
        this.uuid = uuid;
        this.game = game;
        this.team = team;
        this.spec = playerClass;
//        this.maxHealth = this.spec.getMaxHealth();
        this.currentHealth = this.spec.getMaxHealth();
        this.health = new FloatModifiable(this.currentHealth) {{
            addFilter(maxBaseHealthFilter);
        }};
        this.isInPve = com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode());
        this.speed = isInPve() ?
                     new CalculateSpeed(this, this::setWalkSpeed, 13, true) :
                     new CalculateSpeed(this, this::setWalkSpeed, 13);
        this.entity = entity;
        this.deathLocation = this.entity.getLocation();
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

    public Optional<WarlordsDamageHealingFinalEvent> addInstance(InstanceBuilder instanceBuilder) {
        return InstanceManager.addDamageHealingInstance(this, instanceBuilder.target(this).build());
    }

    /**
     * @param attacker which player should hear the hitsound?
     */
    public void playHitSound(WarlordsEntity attacker) {
        if (attacker.entity instanceof Player) {
            ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    @Nonnull
    public Location getLocation() {
        return this.entity.getLocation();
    }

    public void playHitSound() {
        if (entity instanceof Player player) {
            player.playSound(getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    public void playHurtAnimation(WarlordsEntity attacker) {
        Location location = entity.getLocation();
        ServerLevel serverLevel = ((CraftWorld) entity.getWorld()).getHandle();
        serverLevel.broadcastDamageEvent(((CraftEntity) entity).getHandle(), serverLevel.damageSources().generic());
        for (Player p : attacker.getWorld().getPlayers()) {
            p.playSound(location, Sound.ENTITY_PLAYER_HURT, 2, 1);
        }
    }

    public World getWorld() {
        return this.entity.getWorld();
    }

    public void cancelHealingPowerUp() {
        if (this.getCooldownManager().hasCooldown(HealingPowerup.class)) {
            this.getCooldownManager().removeCooldown(HealingPowerup.class, false);
        }
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
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
            ItemStack item = player.getInventory().getItem(0);
            //removing sg shiny weapon
            if (item != null) {
                item.removeEnchantment(Enchantment.OXYGEN);
            }
            //removing boner
            player.getInventory().remove(UndyingArmy.BONE);
        }
        //removing yellow hearts
        giveAbsorption(0);
        Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this, attacker));

        //giving out assists
        hitBy.forEach((assisted, value) -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(assisted.getUuid(), assisted instanceof WarlordsPlayer && assisted.getEntity() instanceof Player);
            Settings.ChatSettings.ChatKills killsMode = databasePlayer.getChatKillsMode();
            if (killsMode == Settings.ChatSettings.ChatKills.ALL || killsMode == Settings.ChatSettings.ChatKills.ONLY_ASSISTS) {
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
            }
            assisted.addAssist();
        });
        hitBy.clear();
        regenTickTimer = 0;
        heal();
    }

    public void removeHorse() {
        if (entity.getVehicle() != null) {
            entity.getVehicle().remove();
        }
    }

    @Nonnull
    public Location getLocation(@Nonnull Location copyInto) {
        return this.entity.getLocation(copyInto);
    }

    public void showDeathAnimation() {
//        if (!(this.entity instanceof Player player)) {
//            this.entity.damage(200);
//        } else {
        // TODO: Fix zombie not dying upon spawn
//            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class, false, z -> {
//                EntityEquipment equipment = z.getEquipment();
//                PlayerInventory playerInventory = player.getInventory();
//                equipment.setBoots(playerInventory.getBoots());
//                equipment.setLeggings(playerInventory.getLeggings());
//                equipment.setChestplate(playerInventory.getChestplate());
//                equipment.setHelmet(playerInventory.getHelmet());
//                equipment.setItemInMainHand(playerInventory.getItemInMainHand());
//            });
//            zombie.setAI(false);
//            zombie.damage(2000);
//        }
    }

    public LinkedHashMap<WarlordsEntity, Integer> getHealedBy() {
        return healedBy;
    }

    public void addDeath() {
        this.minuteStats.addDeath();
        addToSpecMinuteStats(PlayerStatisticsMinute::addDeath);
    }

    /**
     * PotionEffectType.ABSORPTION gives 2 absorption hearts per amplifier, starting at amplifier 0
     *
     * @param amount The amount of absorption to give > 1 = 1 heart
     */
    public void giveAbsorption(double amount) {
        if (this instanceof WarlordsPlayer && entity instanceof Player player) {
            player.setAbsorptionAmount(MathUtils.clamp(amount, 0, Double.MAX_VALUE));
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    @Nonnull
    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void sendMessage(Component component) {
        sendMessage(component, false);
    }

    public Component getColoredName() {
        return Component.text(getName(), getTeam().getTeamColor());
    }

    public void addAssist() {
        this.minuteStats.addAssist();
        addToSpecMinuteStats(PlayerStatisticsMinute::addAssist);
    }

    public void heal() {
        this.currentHealth = getMaxBaseHealth();
    }

    protected void addToSpecMinuteStats(Consumer<PlayerStatisticsMinute> consumer) {
        if (specClass != null) {
            consumer.accept(specMinuteStats.computeIfAbsent(specClass, k -> new PlayerStatisticsMinute()));
        }
    }

    public void sendMessage(Component component, boolean isDamageHealMessage) {
        if (isDamageHealMessage && !showDebugMessage) {
            this.entity.sendMessage(component.hoverEvent(null));
        } else {
            this.entity.sendMessage(component);
        }
        if (!AdminCommand.DISABLE_SPECTATOR_MESSAGES && game != null) {
            game.spectators()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(player -> Objects.equals(player.getSpectatorTarget(), entity))
                .forEach(player -> {
                    if (Permissions.isAdmin(player)) {
                        player.sendMessage(component);
                    } else {
                        player.sendMessage(component.hoverEvent(null));
                    }
                });
        }
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public float getMaxBaseHealth() {
        return maxBaseHealthFilter.getCachedValue();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public boolean hasFlag() {
        return FlagHolder.isPlayerHolderFlag(this);
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

    /**
     * Performs consumer action on WarlordsPlayers static (not the temp ones made on onActivate()) spec abilities that matches the given class.
     * Player specs and their abilities are unknown at this point.
     *
     * @param abilityClass The class of the ability.
     * @param consumer     What to perform on the ability
     * @param <T>          The type of the ability.
     */
    public <T extends AbstractAbility> void doOnStaticAbility(Class<T> abilityClass, Consumer<T> consumer) {
        getAbilitiesMatching(abilityClass).forEach(consumer);
    }

    public <T> List<T> getAbilitiesMatching(Class<T> clazz) {
        return spec.getAbilities().stream()
                   .filter(clazz::isInstance)
                   .map(clazz::cast)
                   .collect(Collectors.toList());
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

    public Specializations getSpecClass() {
        return specClass;
    }

    public boolean isDisableCooldowns() {
        return disableCooldowns;
    }

    public void setDisableCooldowns(boolean disableCooldowns) {
        this.disableCooldowns = disableCooldowns;
    }

    /**
     * Used for custom abilities not in the spec, like legendary weapons
     *
     * @param player
     * @param slot
     * @param ability
     * @param item
     */
    public void updateCustomItem(Player player, int slot, AbstractAbility ability, @Nullable ItemStack item) {
        if (ability.getCurrentCooldown() > 0) {
            ItemBuilder cooldown = new ItemBuilder(Material.GRAY_DYE, ability.getCurrentCooldownItem());
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void resetAbilities(boolean closeInventory) {
        for (AbstractAbility ability : getAbilities()) {
            ability.setCurrentCooldown(0);
        }
        updateInventory(closeInventory);
    }

    public <T> List<AbstractAbility> getAbilities() {
        return spec.getAbilities();
    }

    public void updateInventory(boolean closeInventory) {

    }

    public void setSpec(Specializations spec, SkillBoosts skillBoost) {
        this.spec = spec.create.get();
        this.health.setBaseValue(this.spec.getMaxHealth());
        this.currentHealth = getMaxHealth();
        heal();
        this.energy = this.spec.getMaxEnergy();
        if (this instanceof WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.queueUpdateTabName();
        }
    }

    public float getMaxHealth() {
        return health.getCalculatedValue();
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(float currentHealth) {
        this.currentHealth = currentHealth;
    }

    public FloatModifiable getHealth() {
        return health;
    }

    public void setMaxHealthAndHeal(float newBaseValue) {
        health.setBaseValue(newBaseValue);
        heal();
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

    public void setRespawnTimerSeconds(int respawnTickTimer) {
        //convert respawntimer to ticks
        this.respawnTickTimer = respawnTickTimer == -1 ? -1 : respawnTickTimer * 20;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float addEnergy(WarlordsEntity giver, @Nullable String ability, float amount) {
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
        if ((int) energyGiven != 0 && ability != null) {
            DatabasePlayer receiverSettings = DatabaseManager.getPlayer(getUuid(), this instanceof WarlordsPlayer && getEntity() instanceof Player);
            DatabasePlayer giverSettings = DatabaseManager.getPlayer(giver.getUuid(), giver instanceof WarlordsPlayer && giver.getEntity() instanceof Player);
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

        return energyGiven;
    }

    public float getMaxEnergy() {
        return spec.getMaxEnergy();
    }

    public float subtractEnergy(String from, FloatModifiable amount, boolean fromAttacker) {
        return subtractEnergy(from, amount.getCalculatedValue(), fromAttacker);
    }

    public float subtractEnergy(String from, float amount, boolean fromAttacker) {
        float amountSubtracted = 0;
        if (!noEnergyConsumption) {
            amount *= energyModifier;
            if (energy - amount > getMaxEnergy()) {
                amountSubtracted = getMaxEnergy() - energy;
            } else if (energy - amount < 0) {
                amountSubtracted = energy;
            } else {
                amountSubtracted = amount;
            }
        }
        if (!fromAttacker) {
            WarlordsEnergyUseEvent.Pre event = new WarlordsEnergyUseEvent.Pre(this, from, amountSubtracted);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                amountSubtracted = 0;
            }
        }
        energy -= amountSubtracted;
        if (!fromAttacker) {
            Bukkit.getPluginManager().callEvent(new WarlordsEnergyUseEvent.Post(this, from, amountSubtracted));
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

    public void playSound(@NotNull Location loc, @NotNull Instrument instrument, @NotNull Note note) {
        playSound(loc, instrument, note, SoundSource.RECORDS);
    }

    public void playSound(@NotNull Location loc, @NotNull Instrument instrument, @NotNull Note note, SoundSource category) {
        if (this.entity instanceof Player player) {
//            ((Player) this.entity).playNote(loc, instrument, note);
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerGamePacketListenerImpl connection = craftPlayer.getHandle().connection;
            net.minecraft.world.level.block.state.properties.NoteBlockInstrument nms = CraftBlockData.toNMS(instrument,
                    net.minecraft.world.level.block.state.properties.NoteBlockInstrument.class
            );
            float f;
            if (nms.isTunable()) {
                f = (float) Math.pow(2.0D, (note.getId() - 12.0D) / 12.0D);
            } else {
                f = 1.0f;
            }
            if (!nms.hasCustomSound()) {
                connection.send(new ClientboundSoundPacket(nms.getSoundEvent(),
                        category,
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ(),
                        3.0f,
                        f,
                        craftPlayer.getHandle().getRandom().nextLong()
                ));
            }
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

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public void addKill() {
        this.minuteStats.addKill();
        addToSpecMinuteStats(PlayerStatisticsMinute::addKill);
    }

    public void addDamage(float amount, boolean onCarrier) {
        this.minuteStats.addDamage((long) amount);
        if (onCarrier) {
            this.minuteStats.addDamageOnCarrier((long) amount);
        }

        addToSpecMinuteStats(entries -> entries.addDamage((long) amount));
        if (onCarrier) {
            addToSpecMinuteStats(entries -> entries.addDamageOnCarrier((long) amount));
        }
    }

    public void addHealing(float amount, boolean onCarrier) {
        this.minuteStats.addHealing((long) amount);
        if (onCarrier) {
            this.minuteStats.addHealingOnCarrier((long) amount);
        }
        addToSpecMinuteStats(entries -> entries.addHealing((long) amount));
        if (onCarrier) {
            addToSpecMinuteStats(entries -> entries.addHealingOnCarrier((long) amount));
        }
    }

    public void addDamageTaken(float amount) {
        this.minuteStats.addDamageTaken((long) amount);
        addToSpecMinuteStats(entries -> entries.addDamageTaken((long) amount));
    }

    public void addAbsorbed(float amount) {
        this.minuteStats.addAbsorbed((long) amount);
        addToSpecMinuteStats(entries -> entries.addAbsorbed((long) amount));
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
            TextComponent.Builder hover = Component.text();
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
                        hover.append(Component.text("Minute " + index + ": ", NamedTextColor.WHITE)
                                              .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)),
                                                      NamedTextColor.GOLD
                                              )));
                        hover.append(Component.newline());
                    }
                    stringLength += LegacyComponentSerializer.legacySection().serialize(hover.build()).length();
                    component.append(Component.text(splitString[i], (i > minuteStatsTypeName.length() + 1 ? NamedTextColor.GOLD : NamedTextColor.WHITE)))
                             .hoverEvent(HoverEvent.showText(hover));
                    hover = Component.text();
                }
                //this will never happen in reality
                if (stringLength >= 8000) {
                    return component.build().replaceText(builder -> builder.match("Minute").replacement("Min."));
                }
            } else {
                hover.append(Component.text("Stat Breakdown (" + name + "):", NamedTextColor.AQUA));
                for (int i = 0; i < size; i++) {
                    PlayerStatisticsMinute.Entry entry = entries.get(i);
                    hover.append(Component.newline());
                    hover.append(Component.text("Minute " + (i + 1) + ": ", NamedTextColor.WHITE))
                         .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(entry)), NamedTextColor.GOLD));
                }
                component.append(Component.text(minuteStatsTypeName + ": ", NamedTextColor.WHITE)
                                          .append(Component.text(NumberFormat.addCommaAndRound(minuteStatsType.getValue.apply(minuteStats.total())),
                                                  NamedTextColor.GOLD
                                          )))
                         .hoverEvent(HoverEvent.showText(hover));
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

    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, String... toDisable) {
        return addSpeedModifier(new CalculateSpeed.Modifier(from, name, modifier, duration, Arrays.asList(toDisable), false));
    }

    public Runnable addSpeedModifier(CalculateSpeed.Modifier modifier) {
        WarlordsAddSpeedModifierEvent speedModifierEvent = new WarlordsAddSpeedModifierEvent(this, modifier);
        Bukkit.getPluginManager().callEvent(speedModifierEvent);
        if (speedModifierEvent.isCancelled()) {
            return () -> {
            };
        }
        return this.speed.addSpeedModifier(modifier);
    }

    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, boolean afterLimit, String... toDisable) {
        return addSpeedModifier(new CalculateSpeed.Modifier(from, name, modifier, duration, Arrays.asList(toDisable), afterLimit));
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public int getFlagsCaptured() {
        return this.minuteStats.total().getFlagsCaptured();
    }

    public void addFlagCap() {
        this.minuteStats.addFlagCapture();
        if (specClass != null) {
            specMinuteStats.computeIfAbsent(specClass, k -> new PlayerStatisticsMinute()).addFlagCapture();
        }
    }

    public int getFlagsReturned() {
        return this.minuteStats.total().getFlagsReturned();
    }

    public void addFlagReturn() {
        this.minuteStats.addFlagReturned();
        if (specClass != null) {
            specMinuteStats.computeIfAbsent(specClass, k -> new PlayerStatisticsMinute()).addFlagReturned();
        }
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
    public Location getEyeLocation() {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getEyeLocation();
        }
        return this.entity.getLocation();
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
            TeleportUtils.smoothTeleport((Player) this.entity, location);
        } else {
            Location location1 = this.getLocation();
            location1.setX(location.getX());
            location1.setY(location.getY());
            location1.setZ(location.getZ());
            this.entity.teleport(location1);
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

    public Component getColoredNameBold() {
        return Component.text(getName(), getTeam().getTeamColor(), TextDecoration.BOLD);
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
        WarlordsAddPotionEffectEvent potionEffectEvent = new WarlordsAddPotionEffectEvent(this, potionEffect);
        Bukkit.getPluginManager().callEvent(potionEffectEvent);
        if (potionEffectEvent.isCancelled()) {
            return false;
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removePotionEffect(potionEffect.getType());
            livingEntity.addPotionEffect(potionEffect);
        }
        return true;
    }

    public void removePotionEffect(PotionEffectType type) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.removePotionEffect(type);
        }
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.hasPotionEffect(type);
        }
        return false;
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
            player.setWalkSpeed(MathUtils.clamp(this.walkSpeed, -1f, 1f));
        } else if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(this.walkSpeed);
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
        if (this instanceof WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.queueUpdateTabName();
        }
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

    public void runEveryTick() {
        this.spec.runEveryTick(this);
        // Gives the player their respawn timer as display.
        this.decrementRespawnTimer();

        if (getEntity() instanceof Player player) {
            if (getCompassTarget() != null) {
                player.setCompassTarget(getCompassTarget().getLocation());
            }
        }
        this.health.tick();
        updateHealth();
        getSpeed().updateSpeed();
        getCooldownManager().reduceCooldowns();

        setWasSneaking(isSneaking());

        // Checks whether the player has overheal active and is full health or not.
        boolean hasOverhealCooldown = getCooldownManager().hasCooldown(Overheal.OVERHEAL_MARKER);
        boolean hasTooMuchHealth = getCurrentHealth() > getMaxHealth();

        if (hasOverhealCooldown && !hasTooMuchHealth) {
            getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
        }

        if (!hasOverhealCooldown && hasTooMuchHealth) {
            setCurrentHealth(getMaxHealth());
        }

        // Checks whether the displayed health can be above or under 40 health total. (20 hearts.)
        float newHealth = getCurrentHealth() / getMaxHealth() * 40;
        if (newHealth < 0) {
            newHealth = 0;
        } else if (newHealth > 40) {
            newHealth = 40;
        }
        if (UndyingArmy.checkUndyingArmy(this, newHealth)) {
            newHealth = 40;
        }

        // Energy
        if (getEnergy() < getMaxEnergy()) {
            // Standard energy value per second.
            float energyGainPerTick = getSpec().getEnergyPerSec() / 20;

            for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                energyGainPerTick = abstractCooldown.addEnergyGainPerTick(energyGainPerTick);
            }
            for (AbstractCooldown<?> abstractCooldown : getCooldownManager().getCooldownsDistinct()) {
                energyGainPerTick = abstractCooldown.multiplyEnergyGainPerTick(energyGainPerTick);
            }

            // Setting energy gain to the value after all ability instance multipliers have been applied.
            float newEnergy = getEnergy() + energyGainPerTick;
            if (newEnergy > getMaxEnergy()) {
                newEnergy = getMaxEnergy();
            }
            setEnergy(newEnergy);
        }

        // setting health/energy to player
        if (this instanceof WarlordsPlayer && getEntity() instanceof Player player) {
            //precaution
            player.setHealth(newHealth);
            // Respawn fix for when a player is stuck or leaves the game.
            if (getCurrentHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                heal();
            }
            // Checks whether the player has under 0 energy to avoid infinite energy bugs.
            if (getEnergy() < 0) {
                setEnergy(1);
            }
            player.setLevel((int) getEnergy());
            player.setExp(getEnergy() / getMaxEnergy());
            // Saves the amount of blocks travelled per player.
            setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
        }

        // Melee Cooldown
        if (getHitCooldown() > 0) {
            setHitCooldown(getHitCooldown() - 1);
        }
    }

    private void decrementRespawnTimer() {
        // Respawn
        if (respawnTickTimer == 20) {
            respawn();
        } else if (respawnTickTimer > 0) {
            minuteStats.addTotalRespawnTime();
            if (specClass != null) {
                specMinuteStats.computeIfAbsent(specClass, k -> new PlayerStatisticsMinute()).addTotalRespawnTime();
            }
            respawnTickTimer--;
            if (respawnTickTimer <= 600) {
                if (entity instanceof Player) {
                    entity.showTitle(Title.title(
                            Component.empty(),
                            Component.text("Respawning in... ", team.getTeamColor()).append(Component.text((respawnTickTimer / 20), NamedTextColor.YELLOW)),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                    ));
                }
            }
        }
    }

    public void displayCompassActionBar(@Nonnull Player player) {
        if (this.compassTarget != null) {
            player.sendActionBar(this.compassTarget.getToolbarName(this));
        } else {
            player.sendActionBar(Component.empty());
        }
    }

    public void displayActionBar() {
        TextComponent.Builder actionBarMessage = Component.text()
                                                          .append(Component.text("HP: ", NamedTextColor.GOLD, TextDecoration.BOLD));
        TextComponent.Builder healthBuilder = Component.text().decorate(TextDecoration.BOLD);
        float healthRatio = currentHealth / getMaxHealth();
        if (healthRatio > 1) {
            healthBuilder.color(NamedTextColor.GREEN);
        } else if (healthRatio >= .75) {
            healthBuilder.color(NamedTextColor.DARK_GREEN);
        } else if (healthRatio >= .25) {
            healthBuilder.color(NamedTextColor.YELLOW);
        } else {
            healthBuilder.color(NamedTextColor.RED);
        }
        int currentHealthRounded = Math.round(currentHealth);
        int maxHealthRounded = Math.round(getMaxHealth());
        int maxBaseHealthRounded = Math.round(getMaxBaseHealth());
        healthBuilder.append(Component.text(currentHealthRounded))
                     .append(Component.text("/", NamedTextColor.GOLD))
                     .append(Component.text(maxHealthRounded + "    ", maxHealthRounded > maxBaseHealthRounded ? NamedTextColor.YELLOW : NamedTextColor.GOLD));
        actionBarMessage.append(healthBuilder);
        actionBarMessage.append(team.boldColoredPrefix().append(Component.text(" TEAM  ")));
        for (AbstractCooldown<?> abstractCooldown : cooldownManager.getCooldowns()) {
            if (abstractCooldown.getNameAbbreviation() != null) {
                actionBarMessage.append(abstractCooldown.getNameAbbreviation()).append(Component.space());
            }
        }
        entity.sendActionBar(actionBarMessage.build());
    }

    @Nullable
    public CompassTargetMarker getCompassTarget() {
        return this.compassTarget;
    }

    public abstract void updateHealth();

    public CalculateSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(CalculateSpeed speed) {
        this.speed = speed;
    }

    public AbstractPlayerClass getSpec() {
        return spec;
    }

    public void updateItems() {
        spec.getAbilities().forEach(this::updateItem);
    }

    public void updateItem(AbstractAbility ability) {
        ability.queueUpdateItem();
    }

    public boolean isSneaking() {
        return this.entity instanceof Player && this.entity.isSneaking();
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
        respawnPoint = respawnPoint.clone();
        WarlordsRespawnEvent event = new WarlordsRespawnEvent(this, respawnPoint);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        onRespawn(respawnPoint);
    }

    public void onRespawn(Location respawnPoint) {
        if (entity instanceof Player player) {
            entity.clearTitle();
            player.setFlying(false);
            player.setGameMode(GameMode.ADVENTURE);
        }
        setRespawnTimerSeconds(-1);
        setEnergy(getMaxEnergy() / 2);
        dead = false;
        teleport(respawnPoint);

        heal();
        updateEntity();
    }

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public abstract void updateEntity();

    public void runEverySecond() {
        this.spec.runEverySecond(this);
        // Checks whether the player has a flag cooldown.
        if (getFlagDropCooldown() > 0) {
            setFlagDropCooldown(getFlagDropCooldown() - 1);
        }
        if (getFlagPickCooldown() > 0) {
            setFlagPickCooldown(getFlagPickCooldown() - 1);
        }
        // Combat Timer - Logs combat time after 4 seconds.
        if (getRegenTickTimer() > 6 * 20) {
            getMinuteStats().addTimeInCombat();
        }
        // Assists - 10 seconds timer.
        getHitBy().replaceAll((wp, integer) -> integer - 1);
        getHealedBy().replaceAll((wp, integer) -> integer - 1);
        getHitBy().entrySet().removeIf(p -> p.getValue() <= 0);
        getHealedBy().entrySet().removeIf(p -> p.getValue() <= 0);
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

    public int getRegenTickTimer() {
        return regenTickTimer;
    }

    public void setRegenTickTimer(int regenTickTimer) {
        this.regenTickTimer = regenTickTimer;
    }

    @Nonnull
    public PlayerStatisticsMinute getMinuteStats() {
        return this.minuteStats;
    }

    public LinkedHashMap<WarlordsEntity, Integer> getHitBy() {
        return hitBy;
    }

    public void setFlagPickCooldown(int flagPickCooldown) {
        this.flagPickCooldown = flagPickCooldown;
    }

    public Map<Specializations, PlayerStatisticsMinute> getSpecMinuteStats() {
        return specMinuteStats;
    }

    public void onRemove() {
        if (!(getEntity() instanceof Player)) {
            getEntity().remove();
        }
        getEntity().removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, Warlords.getInstance());
        FlagHolder.dropFlagForPlayer(this);
        getMinuteStats().getEntries().clear();
        getSecondStats().getEntries().forEach(entry -> {
            entry.getEventsAsSelf().clear();
            entry.getEventsAsAttacker().clear();
        });
        getSecondStats().getEntries().clear();
        getCooldownManager().clearAllCooldowns();
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
        return currentVector; //TODO just use velocity
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
        addCurrency(currency, false);
    }

    public void addCurrency(float currency, boolean noMessage) {
        WarlordsAddCurrencyEvent currencyEvent = new WarlordsAddCurrencyEvent(this, currency);
        Bukkit.getPluginManager().callEvent(currencyEvent);
        float currencyToAdd = currencyEvent.getCurrencyToAdd();
        this.currency += currencyToAdd;
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid, this instanceof WarlordsPlayer && getEntity() instanceof Player);
        if (!noMessage && databasePlayer.getChatInsigniaMode() == Settings.ChatSettings.ChatInsignia.ALL) {
            sendMessage(Component.text("+" + NumberFormat.formatOptionalHundredths(currencyToAdd) + " ❂ Insignia", NamedTextColor.GOLD));
        }
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

    public boolean isShowDebugMessage() {
        return showDebugMessage;
    }

    public void setShowDebugMessage(boolean showDebugMessage) {
        this.showDebugMessage = showDebugMessage;
    }

    public float getBonusAggroWeight() {
        return bonusAggroWeight;
    }

    public void setBonusAggroWeight(float aggroWeight) {
        this.bonusAggroWeight = aggroWeight * aggroWeight; // squared because values in PathfinderGoalTargetAgroWarlordsEntity are squared
    }

    public abstract void setDamageResistance(float damageResistance);

    public int getBaseHitCooldownValue() {
        return 20;
    }

    public abstract ItemStack getHead();

    @Nullable
    public abstract ItemStack getHelmet();

    @Nullable
    public abstract ItemStack getChestplate();

    @Nullable
    public abstract ItemStack getLeggings();

    @Nullable
    public abstract ItemStack getBoots();

    @Nullable
    public abstract ItemStack getWeaponItem();

}
