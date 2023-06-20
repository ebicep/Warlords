package com.ebicep.warlords.player.ingame;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.entity.Mob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class WarlordsNPC extends WarlordsEntity {

    public static Zombie spawnZombieNoAI(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        return loc.getWorld().spawn(loc, Zombie.class, zombie -> {
            zombie.setAdult();
            zombie.setCustomNameVisible(true);

            if (inv != null) {
                zombie.getEquipment().setBoots(inv.getBoots());
                zombie.getEquipment().setLeggings(inv.getLeggings());
                zombie.getEquipment().setChestplate(inv.getChestplate());
                zombie.getEquipment().setHelmet(inv.getHelmet());
                zombie.getEquipment().setItemInMainHand(inv.getItemInMainHand());
            } else {
                zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            }
            zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
            zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(0);
            //prevents zombie from moving
            zombie.setAI(false);
        });

    }

    private float minMeleeDamage;
    private float maxMeleeDamage;
    private AbstractMob<?> mob;
    private int stunTicks;

    private Component mobNamePrefix = Component.empty();

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
        if (mob != null && mob.getMobTier() != null) {
            mobNamePrefix = Component.textOfChildren(
                    mob.getMobTier().getSymbol(),
                    Component.text(" - ", NamedTextColor.GRAY)
            );
        }
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
            getEntity().customName(Component.empty()
                                            .append(mobNamePrefix)
                                            .append(Component.text(NumberFormat.addCommaAndRound(this.getHealth()) + "❤",
                                                    NamedTextColor.RED,
                                                    TextDecoration.BOLD
                                            )));
        }
    }

    @Override
    public Runnable addSpeedModifier(WarlordsEntity from, String name, int modifier, int duration, String... toDisable) {
        if (modifier != -99 && getMobTier() != null) {
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

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        boolean applied = super.addPotionEffect(potionEffect);
        if (applied) {
            if (potionEffect.getType() == PotionEffectType.BLINDNESS && mob != null) {
                mob.removeTarget();
            }
        }
        return applied;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public void updateEntity() {
        getEntity().customName(Component.empty()
                                        .append(mobNamePrefix)
                                        .append(Component.text(NumberFormat.addCommaAndRound(this.getHealth()) + "❤", NamedTextColor.RED, TextDecoration.BOLD))
        );
        entity.setCustomNameVisible(true);
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (attribute != null) {
            attribute.setBaseValue(100);
        } else {
            entity.registerAttribute(Attribute.GENERIC_FOLLOW_RANGE);
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(80);
        }
    }

    @Override
    public void setDamageResistance(int damageResistance) {
        getSpec().setDamageResistance(Math.max(0, damageResistance));
    }

    @Nullable
    public MobTier getMobTier() {
        if (mob == null) {
            return null;
        }
        return mob.getMobTier() == null ? MobTier.BASE : mob.getMobTier();
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

    public int getStunTicks() {
        return stunTicks;
    }

    public void setStunTicks(int stunTicks) {
        setStunTicks(stunTicks, false);
    }

    public void setStunTicks(int stunTicks, boolean decrement) {
        AtomicReference<Boolean> ai = new AtomicReference<>();
        CustomEntity<?> customEntity = mob.getEntity();
        if (stunTicks > 0) {
            if (this.stunTicks <= 0) {
                customEntity.setStunned(true);
                ai.set(false);
            }
        } else {
            ai.set(true);
        }
        if (ai.get() != null) {
            Mob entityInsentient = customEntity.get();
            entityInsentient.setNoAi(ai.get());
            //tick later to prevent collision issues
            if (ai.get()) {
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        customEntity.setStunned(false);
                    }
                }.runTaskLater(1);
            }
        }
        //stun needs to be longer to override current
        if (decrement || this.stunTicks < stunTicks) {
            this.stunTicks = stunTicks;
        }
    }
}
