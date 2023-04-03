package com.ebicep.warlords.pve.mobs;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsDropWeaponEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveMobDropEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveWeaponEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsMobDropEvent;
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
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public abstract class AbstractMob<T extends CustomEntity<?>> implements Mob {

    protected final T entity;
    protected final EntityInsentient entityInsentient;
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

    protected WarlordsNPC warlordsNPC;

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

        this.entityInsentient = entity.get();
        this.entityInsentient.persistent = true;

        this.livingEntity = (LivingEntity) entityInsentient.getBukkitEntity();
        if (ee != null) {
            livingEntity.getEquipment().setBoots(ee.getBoots());
            livingEntity.getEquipment().setLeggings(ee.getLeggings());
            livingEntity.getEquipment().setChestplate(ee.getChestplate());
            livingEntity.getEquipment().setHelmet(ee.getHelmet());
            livingEntity.getEquipment().setItemInHand(ee.getItemInHand());
        } else {
            livingEntity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }
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

    public abstract void onSpawn(PveOption option);

    public AbstractMob<T> prependOperation(UnaryOperator<WarlordsNPC> mapper) {
        mapper.apply(this.warlordsNPC);
        return this;
    }

    public abstract void whileAlive(int ticksElapsed, PveOption option);

    public abstract void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event);

    public abstract void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event);

    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        dropWeapon(killer);
        //dropMobDrop(killer);
    }

    public void dropWeapon(WarlordsEntity killer) {
        if (DatabaseManager.playerService == null || !(killer instanceof WarlordsPlayer)) {
            return;
        }
        dropWeapon(killer, 100);
        PlayerFilter.playingGame(killer.getGame())
                    .teammatesOfExcludingSelf(killer)
                    .forEach(teammate -> dropWeapon(teammate, 200));
    }

    private void dropWeapon(WarlordsEntity killer, int bound) {
        AtomicDouble dropRate = new AtomicDouble(weaponDropRate());
        Bukkit.getPluginManager().callEvent(new WarlordsDropWeaponEvent(killer, dropRate));
        if (ThreadLocalRandom.current().nextDouble(0, bound) < dropRate.get()) {
            AbstractWeapon weapon = generateWeapon((WarlordsPlayer) killer);
            Bukkit.getPluginManager().callEvent(new WarlordsGiveWeaponEvent(killer, weapon));

            if (killer.getEntity() instanceof Player) {
                killer.getGame().forEachOnlinePlayer((player, team) -> {
                    player.spigot()
                          .sendMessage(new ComponentBuilder(Permissions.getPrefixWithColor((Player) killer.getEntity()) + killer.getName() + ChatColor.GRAY + " got lucky and found ")
                                  .appendHoverItem(weapon.getName(), weapon.generateItemStack(false))
                                  .append(ChatColor.GRAY + "!")
                                  .create()
                          );
                });
                killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 500, 2);
            }
        }
    }

    public void dropMobDrop(WarlordsEntity killer) {
        if (DatabaseManager.playerService == null || !(killer instanceof WarlordsPlayer)) {
            return;
        }
        HashMap<MobDrops, HashMap<DifficultyIndex, Double>> mobDrops = mobDrops();
        if (mobDrops.isEmpty()) {
            return;
        }
        Game game = killer.getGame();
        PveOption pveOption = game
                .getOptions()
                .stream()
                .filter(PveOption.class::isInstance)
                .map(PveOption.class::cast)
                .findFirst()
                .orElse(null);
        if (pveOption == null) {
            return;
        }
        DifficultyIndex difficultyIndex = pveOption.getDifficulty();
        PlayerFilterGeneric.playingGameWarlordsPlayers(game)
                           .teammatesOf((WarlordsPlayer) killer)
                           .forEach(warlordsPlayer -> {
                               mobDrops.forEach((drop, difficultyIndexDoubleHashMap) -> {
                                   AtomicDouble dropRate = new AtomicDouble(difficultyIndexDoubleHashMap.getOrDefault(difficultyIndex, -1d) * game.getGameMode()
                                                                                                                                                  .getMobDropModifier());
                                   Bukkit.getPluginManager().callEvent(new WarlordsMobDropEvent(warlordsPlayer, dropRate));
                                   if (ThreadLocalRandom.current().nextDouble(0, 1) <= dropRate.get()) {
                                       Bukkit.getPluginManager().callEvent(new WarlordsGiveMobDropEvent(warlordsPlayer, drop));

                                       game.forEachOnlinePlayer((player, team) -> {
                                           player.sendMessage(Permissions.getPrefixWithColor((Player) warlordsPlayer.getEntity()) + warlordsPlayer.getName() +
                                                   ChatColor.GRAY + " obtained a " +
                                                   drop.chatColor + drop.name +
                                                   ChatColor.GRAY + "!"
                                           );
                                       });
                                       warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.LEVEL_UP, 500, 2);
                                   }
                               });
                           });
    }

    public EntityLiving getTarget() {
        return this.entity.getTarget();
    }

    public void setTarget(WarlordsEntity target) {
        this.entity.setTarget((EntityLiving) ((CraftEntity) target.getEntity()).getHandle());
    }

    public void setTarget(LivingEntity target) {
        this.entity.setTarget((((EntityLiving) ((CraftEntity) target).getHandle())));
    }

    public void removeTarget() {
        this.entity.removeTarget();
    }

    public T getEntity() {
        return entity;
    }

    public EntityInsentient getEntityInsentient() {
        return entityInsentient;
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
}
