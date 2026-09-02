package com.ebicep.warlords.pve.mobs;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCTargetAggroWarlordsEntityGoal;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbilityStats;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.events.player.ingame.pve.drops.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.NoTarget;
import com.ebicep.warlords.pve.mobs.flags.Unstunnable;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.tiers.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.google.common.util.concurrent.AtomicDouble;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.WolfModifiers;
import net.citizensnpcs.trait.versioned.BossBarTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractMob implements Mob {

    private static final double NEW_ITEM_DROP_CHANCE = 0.0005;

    protected final String name;
    protected final int maxHealth;
    protected final float walkSpeed;
    protected final float minMeleeDamage;
    protected final float maxMeleeDamage;
    protected final float meleeCritChance;
    protected final float meleeCritMultiplier;
    protected Location spawnLocation;
    protected NPC npc;
    protected EntityEquipment equipment;
    @Nullable
    protected Aspect aspect;
    protected WarlordsNPC warlordsNPC;
    protected PveOption pveOption;
    protected EnumSet<DynamicFlags> dynamicFlags = EnumSet.noneOf(DynamicFlags.class);

    @Nonnull
    protected MobPlayerClass playerClass;

    public AbstractMob(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        this(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                0,
                100,
                abilities
        );
    }

    public AbstractMob(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            float meleeCritChance,
            float meleeCritMultiplier,
            AbstractAbility... abilities
    ) {
        this.spawnLocation = spawnLocation;
        this.name = name;
        com.ebicep.warlords.pve.mobs.Mob mobRegistry = getMobRegistry();
        if (mobRegistry != null) {
            this.equipment = mobRegistry.equipment;
        }
        this.maxHealth = maxHealth;
        this.walkSpeed = walkSpeed;
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;
        this.meleeCritChance = meleeCritChance;
        this.meleeCritMultiplier = meleeCritMultiplier;
        this.playerClass = new MobPlayerClass(name, maxHealth, damageResistance, abilities);
    }

    public abstract com.ebicep.warlords.pve.mobs.Mob getMobRegistry();

    public void addAbility(AbstractAbility abilityToAdd) {
        playerClass.addAbility(abilityToAdd);
        warlordsNPC.getEnergy().setBaseValue(playerClass.getMaxEnergy());
        warlordsNPC.getEnergyPerSec().setBaseValue(playerClass.getEnergyPerSec());
    }


    public WarlordsNPC toNPC(Game game, Team team, Consumer<WarlordsNPC> modifyStats) {
        String name = this.name;
        if (name == null || name.isEmpty()) {
            name = UUID.randomUUID().toString();
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(new Throwable("Mob name is null or empty!"));
        }
        EntityType entityType = getMobRegistry().entityType;
        this.npc = NPCManager.NPC_REGISTRY.createNPC(entityType, name);

        NavigatorParameters defaultParameters = this.npc.getNavigator().getDefaultParameters();
        defaultParameters.attackStrategy(CustomAttackStrategy.ATTACK_STRATEGY);
        defaultParameters.attackRange(getDefaultAttackRange())
                         .stuckAction(null)
                         .updatePathRate(6)
                         .distanceMargin(.5)
                         .speedModifier(.9f)
                         .range(100);
        if (entityType == EntityType.PLAYER) {
//            defaultParameters.lookAtFunction(navigator -> {
//                EntityTarget entityTarget = navigator.getEntityTarget();
//                if (entityTarget != null) {
//                    Entity target = entityTarget.getTarget();
//                    return target instanceof LivingEntity livingEntity ? livingEntity.getEyeLocation() : target.getLocation().add(0, 1.75, 0);
//                }
//                return navigator.getNPC().getStoredLocation();
//            });
//            defaultParameters.useNewPathfinder(true);
        }
        switch (entityType) {
            case SLIME, MAGMA_CUBE -> {
                npc.getNavigator().getDefaultParameters().straightLineTargetingDistance(100);
            }
            case WOLF -> this.npc.getOrAddTrait(WolfModifiers.class).setAngry(true);
            case PLAYER -> {
                npc.getNavigator().getDefaultParameters().straightLineTargetingDistance(100);
                npc.data().set(NPC.Metadata.RESET_PITCH_ON_TICK, true);
            }
        }

        this.npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> 0f);
        this.npc.data().set(NPC.Metadata.COLLIDABLE, true);
        this.npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        this.npc.data().set(NPC.Metadata.KEEP_CHUNK_LOADED, true);

        giveGoals();
        onNPCCreate();
        updateEquipment();

        this.npc.spawn(spawnLocation);

        if (npc.getEntity() instanceof Player player) {
            player.setNoDamageTicks(0);
        }

        LivingEntity entity;
        if (this.npc.getEntity() instanceof LivingEntity livingEntity) {
            entity = livingEntity;
        } else {
            entity = null;
        }

        if (entity != null) {
            AttributeInstance scale = entity.getAttribute(Attribute.SCALE);
            if (scale != null) {
                scale.setBaseValue(getMobScale());
            }
        }
//
//        if (getMobRegistry().entityType == EntityType.SLIME) {
//            this.npc.setUseMinecraftAI(true); //TODO
//            Entity entity = this.npc.getEntity();
//            if (((CraftEntity) entity).getHandle() instanceof net.minecraft.world.entity.Mob mob) {
//                mob.goalSelector.removeAllGoals(goal -> true);
//                mob.targetSelector.removeAllGoals(goal -> true);
//                mob.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(70);
//                if (mob instanceof PathfinderMob) {
//                    mob.goalSelector.addGoal(1, new MeleeAttackGoal((PathfinderMob) mob, 1, true));
//                }
//                mob.targetSelector.addGoal(2, new TargetAggroWarlordsEntityGoal(mob));
//            }
//        }

        this.warlordsNPC = new WarlordsNPC(
                name,
                npc,
                game,
                team,
                maxHealth,
                walkSpeed,
                minMeleeDamage,
                maxMeleeDamage,
                meleeCritChance,
                meleeCritMultiplier,
                this,
                playerClass,
                new MobHologram.TextDisplayHologram(.5f) {

                    @Nullable
                    @Override
                    public Entity getEntity() {
                        if (warlordsNPC == null) {
                            return null;
                        }
                        return warlordsNPC.getEntity();
                    }

                }
        );
        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCurrentCooldown() < ability.getCooldownValue()) {
                warlordsNPC.setCurrentEnergy(warlordsNPC.getCurrentEnergy() + ability.getEnergyCostValue());
            }
        }

        modifyStats.accept(warlordsNPC);

        if (getDescription() != null) {
            BossBarTrait bossBarTrait = this.npc.getOrAddTrait(BossBarTrait.class);
            bossBarTrait.setTitle(LegacyComponentSerializer.legacySection().serialize(Component.text(name, getColor())));
            bossBarTrait.setColor(BarColor.RED);
            bossBarTrait.setStyle(BarStyle.SOLID);
            bossBarTrait.setProgressProvider(() -> {
                if (warlordsNPC == null) {
                    return 0.0;
                }
                return Math.max(0.0, Math.min(warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth(), 1));
            });
        }

        return warlordsNPC;
    }

    public double getMobScale() {
        return 1;
    }

    public double getDefaultAttackRange() {
        return 1.5;
    }

    public void giveGoals() {
        npc.getDefaultBehaviorController().addBehavior(new NPCTargetAggroWarlordsEntityGoal(npc, 70));
    }

    public void onNPCCreate() {

    }

    public void updateEquipment() {
        if (npc == null || equipment == null) {
            return;
        }
        Equipment equipmentTrait = npc.getOrAddTrait(Equipment.class);
        equipmentTrait.set(Equipment.EquipmentSlot.OFF_HAND, this.equipment.getItemInOffHand());
        equipmentTrait.set(Equipment.EquipmentSlot.HAND, this.equipment.getItemInMainHand());
        equipmentTrait.set(Equipment.EquipmentSlot.HELMET, this.equipment.getHelmet());
        equipmentTrait.set(Equipment.EquipmentSlot.CHESTPLATE, this.equipment.getChestplate());
        equipmentTrait.set(Equipment.EquipmentSlot.LEGGINGS, this.equipment.getLeggings());
        equipmentTrait.set(Equipment.EquipmentSlot.BOOTS, this.equipment.getBoots());
    }

    public Component getDescription() {
        return null;
    }

    public TextColor getColor() {
        return NamedTextColor.WHITE;
    }

    public void onSpawn(PveOption option) {
        this.pveOption = option;
        Component description = getDescription();
        if (description != null) {
            ChatUtils.sendTitleToGamePlayers(
                    option.getGame(),
                    getColoredName(),
                    description,
                    20, 30, 20
            );
        }
        handleAspects(option);
    }

    public Component getColoredName() {
        return Component.text(name, getColor());
    }

    protected void handleAspects(PveOption option) {
        if (this.aspect == null &&
                ThreadLocalRandom.current().nextDouble() < option.getDifficulty().getAspectChance().apply(option) &&
                !(this instanceof BossMob || this instanceof PlayerMob || this instanceof BossMinionMob)
        ) {
            this.aspect = Aspect.VALUES[ThreadLocalRandom.current().nextInt(Aspect.VALUES.length)];
        }
        if (this.aspect != null) {
            this.aspect.apply(warlordsNPC);
        }
    }

    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    public void activateAbilities() {
        if (!(warlordsNPC.getSpec() instanceof MobPlayerClass)) {
            return;
        }
        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCooldownValue() != 0 && !ability.anyCharges()) {
                continue;
            }
            if (warlordsNPC.getCurrentEnergy() < ability.getEnergyCostValue() * warlordsNPC.getEnergyModifier()) {
                continue;
            }
            WarlordsAbilityActivateEvent.Pre event = new WarlordsAbilityActivateEvent.Pre(warlordsNPC, null, ability, -1);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                continue;
            }
            boolean shouldApplyCooldown = ability.onActivate(warlordsNPC);
            if (shouldApplyCooldown) {
                WarlordsAbilityActivateEvent.Post post = new WarlordsAbilityActivateEvent.Post(warlordsNPC, null, ability, -1);
                Bukkit.getPluginManager().callEvent(post);

                if (ability instanceof AbilityStats<?, ?> abilityStats) {
                    abilityStats.getAbilityStats().addTimesUsed();
                }
                if (!warlordsNPC.isDisableCooldowns()) {
                    ability.useAbility();
                }
            }
        }
    }

    public void onEntityTarget(WarlordsEntity warlordsEntity) {

    }

    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {

    }

    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    public void onFinalDamageTaken(WarlordsDamageHealingFinalEvent event) {

    }

    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        cleanup(option);
        if (!(killer instanceof WarlordsPlayer)) {
            return;
        }
        dropWeapon(killer);
        dropMobDrop(killer);
        dropItem(killer);
    }

    public void cleanup(PveOption pveOption) {
    }

    public void dropWeapon(WarlordsEntity killer) {
        if (killer.getEntity() instanceof Player) {
            dropWeapon(killer, 1);
        }
        PlayerFilter.playingGame(killer.getGame())
                    .teammatesOfExcludingSelf(killer)
                    .filter(wp -> wp.getEntity() instanceof Player)
                    .forEach(teammate -> dropWeapon(teammate, 2));
    }

    public void dropMobDrop(WarlordsEntity killer) {
        if (pveOption == null) {
            return;
        }
        HashMap<MobDrop, HashMap<DifficultyIndex, Double>> mobDrops = mobDrops();
        if (mobDrops.isEmpty()) {
            return;
        }
        Game game = killer.getGame();
        DifficultyIndex difficultyIndex = pveOption.getDifficulty();
        PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                           .teammatesOf((WarlordsPlayer) killer)
                           .filter(wp -> wp.getEntity() instanceof Player)
                           .forEach(warlordsPlayer -> {
                               mobDrops.forEach((drop, difficultyIndexDoubleHashMap) -> {
                                   AtomicDouble dropRate = new AtomicDouble(difficultyIndexDoubleHashMap.getOrDefault(difficultyIndex, -1d) * game.getGameMode()
                                                                                                                                                  .getDropModifier());
                                   AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropMobDropEvent(warlordsPlayer,
                                           this,
                                           dropRate,
                                           drop
                                   );
                                   Bukkit.getPluginManager().callEvent(dropRewardEvent);
                                   if (!(ThreadLocalRandom.current().nextDouble(0, 1) <= dropRate.get() * dropRewardEvent.getModifier())) {
                                       return;
                                   }
                                   WarlordsGiveMobDropEvent dropEvent = new WarlordsGiveMobDropEvent(warlordsPlayer, drop);
                                   Bukkit.getPluginManager().callEvent(dropEvent);
                                   List<WarlordsPlayer> stolenBy = dropEvent.getStolenBy();
                                   if (!stolenBy.isEmpty()) {
                                       Collections.shuffle(stolenBy);
                                       WarlordsPlayer firstStealer = stolenBy.get(0);
                                       WarlordsPlayer lastStealer = stolenBy.get(stolenBy.size() - 1);
                                       Bukkit.getPluginManager().callEvent(new WarlordsGiveStolenMobDropEvent(lastStealer, drop));

                                       TextComponent.Builder stolenMessage = Component
                                               .text().color(NamedTextColor.GRAY)
                                               .append(Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true))
                                               .append(Component.text(" obtained a "))
                                               .append(Component.text(drop.name, drop.textColor))
                                               .append(Component.text(" but it was stolen by "))
                                               .append(Permissions.getPrefixWithColor((Player) firstStealer.getEntity(), true))
                                               .append(Component.text("!"));
                                       for (int i = 1; i < stolenBy.size() - 1; i++) {
                                           stolenMessage.append(Component.text(" But then "))
                                                        .append(Permissions.getPrefixWithColor((Player) stolenBy.get(i).getEntity(), true))
                                                        .append(Component.text(" stole it from "))
                                                        .append(Permissions.getPrefixWithColor((Player) stolenBy.get(i - 1).getEntity(), true))
                                                        .append(Component.text("!"));
                                       }
                                       game.forEachOnlinePlayer((player, team) -> player.sendMessage(stolenMessage.build()));
                                       lastStealer.playSound(lastStealer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 1.5f);
                                   } else {
                                       TextComponent.Builder obtainMessage = Component
                                               .text().color(NamedTextColor.GRAY)
                                               .append(Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true))
                                               .append(Component.text(" obtained a "))
                                               .append(Component.text(drop.name, drop.textColor))
                                               .append(Component.text("!"));
                                       game.forEachOnlinePlayer((player, team) -> player.sendMessage(obtainMessage.build()));
                                       warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                                   }
                               });
                           });
    }

    public void dropItem(WarlordsEntity killer) {
        Game game = killer.getGame();
        if (game.getGameMode().equals(GameMode.PVE_DEBUG)) {
            return;
        }
        PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                           .teammatesOf((WarlordsPlayer) killer)
                           .filter(wp -> wp.getEntity() instanceof Player)
                           .forEach(warlordsPlayer -> {
                               AtomicDouble dropRate = new AtomicDouble(NEW_ITEM_DROP_CHANCE * game.getGameMode().getDropModifier());
                               AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropNewItemEvent(warlordsPlayer, this, dropRate);
                               Bukkit.getPluginManager().callEvent(dropRewardEvent);
                               if (ThreadLocalRandom.current().nextDouble() >= dropRate.get() * dropRewardEvent.getModifier()) {
                                   return;
                               }
                               NewItem item = NewItemsUtils.generateRandomItem();
                               Bukkit.getPluginManager().callEvent(new WarlordsGiveNewItemEvent(warlordsPlayer, item));
                               DatabasePlayer databasePlayer = warlordsPlayer.getDatabasePlayer();
                               databasePlayer.getPveStats().getNewItemsManager().addItem(item);
                               DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                               game.forEachOnlinePlayer((player, team) -> NewItemsUtils.sendItemMessage(
                                       player,
                                       Component.text().color(NamedTextColor.GRAY)
                                                .append(Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true))
                                                .append(Component.text(" got lucky and found "))
                                                .append(item.getHoverComponent())
                                                .append(Component.text("!"))
                                                .build()
                               ));
                               warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                           });
    }

    private void dropWeapon(WarlordsEntity killer, int bound) {
        if (!(killer instanceof WarlordsPlayer warlordsPlayer)) {
            return;
        }
        AtomicDouble dropRate = new AtomicDouble(.01 * weaponDropRate() * killer.getGame().getGameMode().getDropModifier());
        AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropWeaponEvent(killer, this, dropRate);
        Bukkit.getPluginManager().callEvent(dropRewardEvent);
        if (ThreadLocalRandom.current().nextDouble(0, bound) < dropRate.get() * dropRewardEvent.getModifier()) {
            AbstractWeapon weapon = generateWeapon(warlordsPlayer);
            Bukkit.getPluginManager().callEvent(new WarlordsGiveWeaponEvent(killer, weapon));
            killer.getGame().forEachOnlinePlayer((player, team) -> {
                player.sendMessage(Component.text().color(NamedTextColor.GRAY)
                                            .append(Permissions.getPrefixWithColor((Player) killer.getEntity(), true))
                                            .append(Component.text(" got lucky and found "))
                                            .append(weapon.getHoverComponent(false))
                                            .append(Component.text("!"))
                );
            });
            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
        }
    }

    private void dropBlessing(WarlordsEntity killer) {
        Game game = killer.getGame();
        PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                           .teammatesOf((WarlordsPlayer) killer)
                           .filter(wp -> wp.getEntity() instanceof Player)
                           .forEach(warlordsPlayer -> {
                               AtomicDouble dropRate = new AtomicDouble(.00025 * game.getGameMode().getDropModifier());
                               AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropBlessingEvent(warlordsPlayer, this, dropRate);
                               Bukkit.getPluginManager().callEvent(dropRewardEvent);
                               if (!(ThreadLocalRandom.current().nextDouble() < dropRate.get() * dropRewardEvent.getModifier())) {
                                   return;
                               }
                               Bukkit.getPluginManager().callEvent(new WarlordsGiveBlessingFoundEvent(warlordsPlayer));
                               game.forEachOnlinePlayer((player, team) -> {
                                   AbstractItem.sendItemMessage(player,
                                           Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true)
                                                      .append(Component.text(" got lucky and received an Unknown Blessing!", NamedTextColor.GRAY))
                                   );
                               });
                               warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                           });
    }

    @Nullable
    public Entity getTarget() {
        EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
        return entityTarget == null ? null : entityTarget.getTarget();
    }

    public void setTarget(WarlordsEntity target) {
        if (this instanceof NoTarget) {
            return;
        }
        if (target == null) {
            npc.getNavigator().cancelNavigation();
            return;
        }
        if (!npc.isSpawned()) {
            return;
        }
        npc.getNavigator().setTarget(target.getEntity(), true);
    }

    public void setTarget(LivingEntity target) {
        if (this instanceof NoTarget) {
            return;
        }
        if (target == null) {
            npc.getNavigator().cancelNavigation();
            return;
        }
        if (!npc.isSpawned()) {
            return;
        }
        npc.getNavigator().setTarget(target, true);
    }

    public void removeTarget() {
        npc.getNavigator().cancelNavigation(CancelReason.PLUGIN);
    }

    public void toggleStun(boolean stun) {
        if (stun && this instanceof Unstunnable) {
            return;
        }
        npc.getNavigator().setPaused(stun);
    }

    public NPC getNpc() {
        return npc;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public WarlordsNPC getWarlordsNPC() {
        return warlordsNPC;
    }

    public String getName() {
        return name;
    }

    public EntityEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(EntityEquipment equipment) {
        this.equipment = equipment;
    }

    @Nullable
    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(@Nullable Aspect aspect) {
        this.aspect = aspect;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    @Nonnull
    public MobPlayerClass getPlayerClass() {
        return playerClass;
    }

    public float getMinMeleeDamage() {
        return minMeleeDamage;
    }

    public float getMaxMeleeDamage() {
        return maxMeleeDamage;
    }

    public EnumSet<DynamicFlags> getDynamicFlags() {
        return dynamicFlags;
    }

    public PveOption getPveOption() {
        return pveOption;
    }

}
