package com.ebicep.warlords.player.ingame;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.java.NumberFormat;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class WarlordsNPC extends WarlordsEntity {

    private float minMeleeDamage;
    private float maxMeleeDamage;
    private AbstractMob<?> mob;

    public WarlordsNPC(
            UUID uuid,
            String name,
            Weapons weapon,
            LivingEntity entity,
            Game game,
            Team team,
            Specializations specClass
    ) {
        super(uuid, name, entity, game, team, specClass);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
    }

    public WarlordsNPC(
            UUID uuid,
            String name,
            Weapons weapon,
            LivingEntity entity,
            Game game,
            Team team,
            Specializations specClass,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(uuid, name, entity, game, team, specClass);
        this.walkSpeed = walkSpeed;
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
        setMaxBaseHealth(maxHealth);
        spec.setDamageResistance(damageResistance);
    }

    public WarlordsNPC(
            UUID uuid,
            String name,
            Weapons weapon,
            LivingEntity entity,
            Game game,
            Team team,
            Specializations specClass,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractMob<?> mob
    ) {
        super(uuid, name, entity, game, team, specClass);
        this.mob = mob;
        this.setInPve(true);
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;
        this.speed = new CalculateSpeed(this, this::setWalkSpeed, 13, true);
        this.speed.setBaseSpeedToWalkingSpeed(walkSpeed);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
        setMaxBaseHealth(maxHealth);
        spec.setDamageResistance(damageResistance);
    }

    @Override
    public void updateHealth() {
        if (!isDead()) {
            String oldName = getEntity().getCustomName();
            String newName = oldName.substring(0, oldName.lastIndexOf(' ') + 1) + ChatColor.RED + ChatColor.BOLD + NumberFormat.addCommaAndRound(this.getHealth()) + "❤";
            getEntity().setCustomName(newName);
        }
    }

    @Override
    public void updateEntity() {
        entity.setCustomName(
                (mob != null && mob.getMobTier() != null ? ChatColor.GOLD + mob.getMobTier().getSymbol() + " §7- " : "")
                        + ChatColor.RED + ChatColor.BOLD + NumberFormat.addCommaAndRound(this.getHealth()) + "❤"
        );
        entity.setCustomNameVisible(true);
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) entity).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(80);
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    public static Zombie spawnZombieNoAI(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = loc.getWorld().spawn(loc, Zombie.class);
        jimmy.setBaby(false);
        jimmy.setCustomNameVisible(true);

        if (inv != null) {
            jimmy.getEquipment().setBoots(inv.getBoots());
            jimmy.getEquipment().setLeggings(inv.getLeggings());
            jimmy.getEquipment().setChestplate(inv.getChestplate());
            jimmy.getEquipment().setHelmet(inv.getHelmet());
            jimmy.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            jimmy.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        }
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
        //prevents jimmy from moving
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) jimmy).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
        return jimmy;

    }

    public static <T extends LivingEntity> T spawnEntity(@Nonnull Class<T> clazz, @Nonnull Location loc, @Nullable EntityEquipment inv) {
        T entity = loc.getWorld().spawn(loc, clazz);
        if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby(false);
        }

        entity.setCustomNameVisible(true);

        if (inv != null) {
            entity.getEquipment().setBoots(inv.getBoots());
            entity.getEquipment().setLeggings(inv.getLeggings());
            entity.getEquipment().setChestplate(inv.getChestplate());
            entity.getEquipment().setHelmet(inv.getHelmet());
            entity.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            entity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }

        return entity;
    }

    public static <T extends CustomEntity<?>> LivingEntity spawnCustomEntity(
            @Nonnull Class<T> clazz,
            Supplier<T> create,
            Consumer<T> onCreate,
            @Nonnull Location loc,
            @Nullable EntityEquipment inv
    ) {
        T customEntity = create.get();
        onCreate.accept(customEntity);
        customEntity.spawn(loc);

        EntityInsentient entityInsentient = customEntity.get();
        entityInsentient.persistent = true;

        LivingEntity entity = (LivingEntity) entityInsentient.getBukkitEntity();
        if (inv != null) {
            entity.getEquipment().setBoots(inv.getBoots());
            entity.getEquipment().setLeggings(inv.getLeggings());
            entity.getEquipment().setChestplate(inv.getChestplate());
            entity.getEquipment().setHelmet(inv.getHelmet());
            entity.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            entity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }

        return entity;
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        boolean applied = super.addPotionEffect(potionEffect);
        if (applied) {
            if (potionEffect.getType() == PotionEffectType.BLINDNESS) {
                SoulShackle.shacklePlayer(mob.getWarlordsNPC(), mob.getWarlordsNPC(), 1);
                mob.getWarlordsNPC().getSpeed().addSpeedModifier(mob.getWarlordsNPC(), "Blindness", -99, 1, "BASE");
                mob.removeTarget();
            }
        }
        return applied;
    }

    @Override
    public Runnable addSpeedModifier(WarlordsEntity from, String name, int modifier, int duration, String... toDisable) {
        if (modifier != -99) {
            if (getMobTier() == MobTier.BOSS) {
                if (modifier < 0) {
                    modifier *= .4;
                }
            } else {
                if (modifier < 0) {
                    modifier *= .7;
                }
            }
        }
        return super.addSpeedModifier(from, name, modifier, duration, toDisable);
    }

    public float getMinMeleeDamage() {
        return minMeleeDamage;
    }

    public void setMinMeleeDamage(int minMeleeDamage) {
        this.minMeleeDamage = minMeleeDamage;
    }

    public float getMaxMeleeDamage() {
        return maxMeleeDamage;
    }

    public void setMaxMeleeDamage(int maxMeleeDamage) {
        this.maxMeleeDamage = maxMeleeDamage;
    }

    public AbstractMob<?> getMob() {
        return mob;
    }

    public MobTier getMobTier() {
        return mob.getMobTier();
    }

}
