package com.ebicep.warlords.pve.mobs;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCTargetAggroWarlordsEntityGoal;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.events.player.ingame.pve.drops.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.NoTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.tiers.Mob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public abstract class AbstractMob implements Mob {

    protected final Location spawnLocation;
    protected final String name;
    protected final int maxHealth;
    protected final float walkSpeed;
    protected final float minMeleeDamage;
    protected final float maxMeleeDamage;
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
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
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
        this.playerClass = new MobPlayerClass(name, maxHealth, damageResistance, abilities);
    }

    public abstract com.ebicep.warlords.pve.mobs.Mob getMobRegistry();

    public WarlordsNPC toNPC(Game game, Team team, Consumer<WarlordsNPC> modifyStats) {
        EntityType entityType = getMobRegistry().entityType;
        this.npc = NPCManager.NPC_REGISTRY.createNPC(entityType, name);

        NavigatorParameters defaultParameters = this.npc.getNavigator().getDefaultParameters();
        defaultParameters.attackStrategy(CustomAttackStrategy.ATTACK_STRATEGY);
        defaultParameters.attackRange(1)
                         .stuckAction(null) // disable tping to player if too far away
                         .updatePathRate(5)
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
            case SLIME, MAGMA_CUBE -> npc.getNavigator().getDefaultParameters().straightLineTargetingDistance(100);
            case WOLF -> this.npc.getOrAddTrait(WolfModifiers.class).setAngry(true);
            case PLAYER -> {
                npc.getNavigator().getDefaultParameters().straightLineTargetingDistance(100);
                npc.data().set(NPC.Metadata.RESET_PITCH_ON_TICK, true);
            }
        }

        this.npc.data().set(NPC.Metadata.COLLIDABLE, true);
        this.npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, entityType != EntityType.PLAYER);

        giveGoals();
        onNPCCreate();
        updateEquipment();

        this.npc.spawn(spawnLocation);

        if (npc.getEntity() instanceof Player player) {
            player.setNoDamageTicks(0);
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
                this,
                playerClass
        );
        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCurrentCooldown() < ability.getCooldownValue()) {
                warlordsNPC.setEnergy(warlordsNPC.getEnergy() + ability.getEnergyCostValue());
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

    public void giveGoals() {
        //TODO wander? - waypoints trait
//        npc.getNavigator().getLocalParameters()
//           .avoidWater(true);
        npc.getDefaultGoalController().addGoal(new NPCTargetAggroWarlordsEntityGoal(npc, 70), 2);
    }

    public void onNPCCreate() {

    }

    public void updateEquipment() {
        if (npc == null || equipment == null) {
            return;
        }
        Equipment equipmentTrait = npc.getOrAddTrait(Equipment.class);
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
        // null checks to handle manual spawns with aspects
        if (this.aspect == null &&
                ThreadLocalRandom.current().nextDouble() < option.getDifficulty().getAspectChance().apply(option) &&
                !(this instanceof BossMob || this instanceof PlayerMob)
        ) {
            this.aspect = Aspect.VALUES[ThreadLocalRandom.current().nextInt(Aspect.VALUES.length)];
        }
        if (this.aspect != null) {
            this.aspect.apply(warlordsNPC);
        }
    }

    public Component getColoredName() {
        return Component.text(name, getColor());
    }

    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    public void activateAbilities() {
        if (!(warlordsNPC.getSpec() instanceof MobPlayerClass)) {
            return;
        }
        warlordsNPC.getAbilities().forEach(ability -> {
            if (ability.getCooldownValue() != 0 && ability.getCurrentCooldown() != 0) {
                return;
            }
            if (warlordsNPC.getEnergy() < ability.getEnergyCostValue() * warlordsNPC.getEnergyModifier()) {
                return;
            }
            WarlordsAbilityActivateEvent.Pre event = new WarlordsAbilityActivateEvent.Pre(warlordsNPC, null, ability);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            boolean shouldApplyCooldown = ability.onActivate(warlordsNPC);
            if (shouldApplyCooldown) {
                WarlordsAbilityActivateEvent.Post post = new WarlordsAbilityActivateEvent.Post(warlordsNPC, null, ability);
                Bukkit.getPluginManager().callEvent(post);

                ability.addTimesUsed();
                if (!warlordsNPC.isDisableCooldowns()) {
                    ability.setCurrentCooldown(ability.getCooldownValue());
                }
            }
        });
    }

    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {

    }

    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    public void onFinalDamageTaken(WarlordsDamageHealingFinalEvent event) {

    }

    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        if (DatabaseManager.playerService == null || !(killer instanceof WarlordsPlayer)) {
            return;
        }
        if (pveOption == null) {
            return;
        }
        dropWeapon(killer);
        dropMobDrop(killer);
        dropItem(killer);
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
        PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                           .teammatesOf((WarlordsPlayer) killer)
                           .filter(wp -> wp.getEntity() instanceof Player)
                           .forEach(warlordsPlayer -> {
                               ItemTier[] validValues = ItemTier.VALID_VALUES;
                               for (int i = validValues.length - 1; i >= 0; i--) {
                                   ItemTier itemTier = validValues[i];
                                   if (itemTier.dropChance == 0) {
                                       continue;
                                   }
                                   double rng = ThreadLocalRandom.current().nextDouble();
                                   AtomicDouble dropRate = new AtomicDouble(itemTier.dropChance * game.getGameMode().getDropModifier());
                                   AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropItemEvent(warlordsPlayer, this, dropRate, itemTier);
                                   Bukkit.getPluginManager().callEvent(dropRewardEvent);
                                   if (!(rng < dropRate.get() * dropRewardEvent.getModifier())) {
                                       continue;
                                   }
                                   AbstractItem item = ItemType.getRandom().create(itemTier);
                                   Bukkit.getPluginManager().callEvent(new WarlordsGiveItemEvent(warlordsPlayer, item));
                                   game.forEachOnlinePlayer((player, team) -> {
                                       AbstractItem.sendItemMessage(player,
                                               Component.text().color(NamedTextColor.GRAY)
                                                        .append(Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true))
                                                        .append(Component.text(" got lucky and found "))
                                                        .append(item.getHoverComponent())
                                                        .append(Component.text("!"))
                                                        .build()
                                       );
                                   });
                                   warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                                   break;
                               }
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
        if (stun) {
            //npc.getNavigator().cancelNavigation(CancelReason.PLUGIN);
        }
        npc.getNavigator().setPaused(stun);
    }

    public NPC getNpc() {
        return npc;
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
}
