package com.ebicep.warlords.pve.mobs;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.events.player.ingame.pve.drops.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.mobtypes.Mob;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class AbstractMob<T extends CustomEntity<?>> implements Mob {

    protected final T entity;
    protected final net.minecraft.world.entity.Mob mob;
    protected final LivingEntity livingEntity;
    protected final Location spawnLocation;
    protected final String name;
    protected final MobTier mobTier;
    protected final EntityEquipment ee;
    protected final int maxHealth;
    protected final float walkSpeed;
    protected final int damageResistance;
    protected final float minMeleeDamage;
    protected final float maxMeleeDamage;
    protected final BossBar bossBar = BossBar.bossBar(Component.empty(), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    protected boolean showBossBar;

    protected WarlordsNPC warlordsNPC;
    protected PveOption pveOption;

    public AbstractMob(
            T entity,
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        this.entity = entity;
        this.spawnLocation = spawnLocation;
        this.name = name;
        this.mobTier = mobTier;
        this.ee = ee;
        this.maxHealth = maxHealth;
        this.walkSpeed = walkSpeed;
        this.damageResistance = damageResistance;
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;

        entity.spawn(spawnLocation);

        this.mob = entity.get();
        this.mob.persist = true;

        this.livingEntity = (LivingEntity) mob.getBukkitEntity();

        updateEquipment();

        if (getDescription() != null) {
            bossBar.name(Component.text(name, getColor()));
        }
    }

    public void updateEquipment() {
        EntityEquipment equipment = livingEntity.getEquipment();
        if (equipment == null) {
            return;
        }
        if (ee != null) {
            equipment.setBoots(ee.getBoots());
            equipment.setLeggings(ee.getLeggings());
            equipment.setChestplate(ee.getChestplate());
            equipment.setHelmet(ee.getHelmet());
            equipment.setItemInMainHand(ee.getItemInMainHand());
        } else {
            equipment.setHelmet(new ItemStack(Material.BARRIER));
        }
    }

    public Component getDescription() {
        return null;
    }

    public NamedTextColor getColor() {
        return NamedTextColor.WHITE;
    }

    public WarlordsNPC toNPC(Game game, Team team, UUID uuid, Consumer<WarlordsNPC> modifyStats) {
        this.warlordsNPC = new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                livingEntity,
                game,
                team,
                Specializations.PYROMANCER,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                this
        );
        AbstractAbility weapon = warlordsNPC.getSpec().getWeapon();
        if (weapon instanceof Fireball) {
            ((Fireball) weapon).setMaxDistance(150);
        }

        modifyStats.accept(warlordsNPC);
        game.addNPC(warlordsNPC);

        return warlordsNPC;
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
        if (!bossBar.name().equals(Component.empty())) {
            showBossBar = true;
        }
    }

    public Component getColoredName() {
        return Component.text(name, getColor());
    }

    public AbstractMob<T> prependOperation(UnaryOperator<WarlordsNPC> mapper) {
        mapper.apply(this.warlordsNPC);
        return this;
    }

    public abstract void whileAlive(int ticksElapsed, PveOption option);

    public abstract void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event);

    public abstract void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event);

    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        bossBar(option.getGame(), false);
        if (DatabaseManager.playerService == null || !(killer instanceof WarlordsPlayer)) {
            return;
        }
        if (pveOption == null) {
            return;
        }
        dropWeapon(killer);
        dropMobDrop(killer);
        dropItem(killer);
        dropBlessing(killer);
    }

    public void bossBar(Game game, boolean show) {
        game.onlinePlayers().forEach(playerTeamEntry -> {
            Player player = playerTeamEntry.getKey();
            if (show) {
                player.showBossBar(getBossBar());
            } else {
                player.hideBossBar(getBossBar());
            }
        });
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
        HashMap<MobDrops, HashMap<DifficultyIndex, Double>> mobDrops = mobDrops();
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
                                   if (ThreadLocalRandom.current().nextDouble(0, 1) <= dropRate.get() * dropRewardEvent.getModifier()) {
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
                               double rng = ThreadLocalRandom.current().nextDouble();
                               for (int i = validValues.length - 1; i >= 0; i--) {
                                   ItemTier itemTier = validValues[i];
                                   if (itemTier.dropChance == 0) {
                                       continue;
                                   }
                                   AtomicDouble dropRate = new AtomicDouble(itemTier.dropChance * game.getGameMode().getDropModifier());
                                   AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropItemEvent(warlordsPlayer, this, dropRate, itemTier);
                                   Bukkit.getPluginManager().callEvent(dropRewardEvent);
                                   if (rng < dropRate.get() * dropRewardEvent.getModifier()) {
                                       AbstractItem item = ItemType.getRandom().createBasic(itemTier);
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
                               }
                           });
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
                               if (ThreadLocalRandom.current().nextDouble() < dropRate.get() * dropRewardEvent.getModifier()) {
                                   Bukkit.getPluginManager().callEvent(new WarlordsGiveBlessingFoundEvent(warlordsPlayer));
                                   game.forEachOnlinePlayer((player, team) -> {
                                       AbstractItem.sendItemMessage(player,
                                               Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity(), true)
                                                          .append(Component.text(" got lucky and received an Unknown Blessing!", NamedTextColor.GRAY))
                                       );
                                   });
                                   warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                               }
                           });
    }

    public BossBar getBossBar() {
        return bossBar.progress(Math.max(0, Math.min(warlordsNPC.getHealth() / warlordsNPC.getMaxHealth(), 1)));
    }

    private void dropWeapon(WarlordsEntity killer, int bound) {
        AtomicDouble dropRate = new AtomicDouble(.01 * weaponDropRate() * killer.getGame().getGameMode().getDropModifier());
        AbstractWarlordsDropRewardEvent dropRewardEvent = new WarlordsDropWeaponEvent(killer, this, dropRate);
        Bukkit.getPluginManager().callEvent(dropRewardEvent);
        if (ThreadLocalRandom.current().nextDouble(0, bound) < dropRate.get() * dropRewardEvent.getModifier()) {
            AbstractWeapon weapon = generateWeapon((WarlordsPlayer) killer);
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

    protected BossBar getHealthModifiedBossBar() {
        return bossBar.progress(warlordsNPC.getHealth() / warlordsNPC.getMaxHealth());
    }

    public net.minecraft.world.entity.LivingEntity getTarget() {
        return this.entity.getTarget();
    }

    public void setTarget(WarlordsEntity target) {
        this.entity.setTarget((net.minecraft.world.entity.LivingEntity) ((CraftEntity) target.getEntity()).getHandle());
    }

    public void setTarget(LivingEntity target) {
        this.entity.setTarget((((net.minecraft.world.entity.LivingEntity) ((CraftEntity) target).getHandle())));
    }

    public void removeTarget() {
        this.entity.removeTarget();
    }

    public T getEntity() {
        return entity;
    }

    public net.minecraft.world.entity.Mob getMob() {
        return mob;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public WarlordsNPC getWarlordsNPC() {
        return warlordsNPC;
    }

    public String getName() {
        return name;
    }

    public MobTier getMobTier() {
        return mobTier;
    }

    public EntityEquipment getEe() {
        return ee;
    }

    public boolean isShowBossBar() {
        return showBossBar;
    }
}
