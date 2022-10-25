package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerDropWeaponEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerGiveWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
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

    public WarlordsNPC toNPC(Game game, Team team, UUID uuid) {
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

        Optional<Option> waveDefenseOptional = game.getOptions()
                .stream()
                .filter(option -> option instanceof WaveDefenseOption)
                .findFirst();
        if (waveDefenseOptional.isPresent()) {
            WaveDefenseOption waveDefenseOption = (WaveDefenseOption) waveDefenseOptional.get();

            onSpawn(waveDefenseOption);
            game.addNPC(warlordsNPC);

            double scale = 600.0;
            long playerCount = game.warlordsPlayers().count();
            double modifiedScale = scale - (playerCount > 1 ? 75 * playerCount : 0);
            float health = (float) Math.pow(warlordsNPC.getMaxHealth(), waveDefenseOption.getWaveCounter() / modifiedScale + 1);
            if (playerCount > 1 && warlordsNPC.getMobTier() == MobTier.BOSS) {
                warlordsNPC.setMaxHealth(health * (1 + (0.25f * playerCount)));
                warlordsNPC.setHealth(health * (1 + (0.25f * playerCount)));
            } else {
                warlordsNPC.setMaxHealth(health);
                warlordsNPC.setHealth(health);
            }
        }

        return warlordsNPC;
    }

    public abstract void onSpawn(WaveDefenseOption option);

    public AbstractMob<T> prependOperation(UnaryOperator<WarlordsNPC> mapper) {
        mapper.apply(this.warlordsNPC);
        return this;
    }

    public abstract void whileAlive(int ticksElapsed, WaveDefenseOption option);

    public abstract void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event);

    public abstract void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event);

    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        dropWeapon(killer);
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
        AtomicDouble dropRate = new AtomicDouble(dropRate());
        Bukkit.getPluginManager().callEvent(new WarlordsPlayerDropWeaponEvent(killer, dropRate));
        if (ThreadLocalRandom.current().nextDouble(0, bound) < dropRate.get()) {
            AbstractWeapon weapon = generateWeapon((WarlordsPlayer) killer);
            Bukkit.getPluginManager().callEvent(new WarlordsPlayerGiveWeaponEvent(killer, weapon));

            killer.getGame().forEachOnlinePlayer((player, team) -> {
                player.spigot().sendMessage(new ComponentBuilder(ChatColor.AQUA + killer.getName() + ChatColor.GRAY + " got lucky and found ")
                        .appendHoverItem(weapon.getName(), weapon.generateItemStack(false))
                        .append(ChatColor.GRAY + "!")
                        .create()
                );
            });
            killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 500, 2);
        }
    }

    public EntityLiving getTarget() {
        return this.entity.getTarget();
    }

    public void setTarget(WarlordsEntity target) {
        this.entity.setTarget(((CraftPlayer) target.getEntity()).getHandle());
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
