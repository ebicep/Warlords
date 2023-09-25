package com.ebicep.warlords.player.ingame;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.mobs.mobflags.BossLike;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
    private float damageResistance;
    private AbstractMob<?> mob;
    private int stunTicks;
    private Component mobNamePrefix = Component.empty();
    private ArmorStand nameDisplay;

    public WarlordsNPC(
            String name,
            LivingEntity entity,
            Game game,
            Team team,
            Specializations specClass
    ) {
        super(entity.getUniqueId(), name, entity, game, team, specClass);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
    }

    @Nonnull
    private TextComponent getNameComponent() {
        if (mob == null) {
            return Component.text(name, NamedTextColor.GRAY);
        }
        TextComponent.Builder builder = Component.text();
        builder.append(mobNamePrefix)
               .append(Component.text("- "));

        TextColor nameColor = NamedTextColor.GRAY;
        Aspect aspect = mob.getAspect();
        if (aspect != null) {
            nameColor = aspect.textColor;
            builder.append(Component.text(aspect.name + " ", aspect.textColor));
        }

        builder.append(Component.text(name, nameColor))
               .append(Component.text(" - "))
               .append(Component.text(NumberFormat.formatOptionalTenths(spec.getDamageResistance()) + "% ⛊", NamedTextColor.GOLD));

        return builder.build();
    }

    public WarlordsNPC(
            String name,
            LivingEntity entity,
            Game game,
            Team team,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractMob<?> mob,
            AbstractPlayerClass playerClass
    ) {
        super(entity.getUniqueId(), name, entity, game, team, playerClass);
        this.mob = mob;
        if (mob != null) {
            mobNamePrefix = Component.textOfChildren(
                    Component.text("[", NamedTextColor.GRAY),
                    Component.text(mob.getLevel(), mob.getTextColor()),
                    Component.text("] ", NamedTextColor.GRAY)
            );
        }
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;
        this.damageResistance = damageResistance;
        this.speed = new CalculateSpeed(this, this::setWalkSpeed, 13, true);
        this.speed.setBaseSpeedToWalkingSpeed(walkSpeed);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
        setMaxBaseHealth(maxHealth);
    }

    public Component getMobNamePrefix() {
        return mobNamePrefix;
    }

    @Override
    public void die(@Nullable WarlordsEntity attacker) {
        super.die(attacker);
        nameDisplay.remove();
    }

    @Override
    public int getBaseHitCooldownValue() {
        return 20;
    }

    @Override
    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, String... toDisable) {
        if (modifier != -99) {
            if (getMob() instanceof BossLike) {
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
    public void runEveryTick() {
        // updating entity reference in case it was unloaded
        Entity updatedEntity = Bukkit.getEntity(uuid);
        if (!Objects.equals(updatedEntity, entity) && updatedEntity instanceof LivingEntity) {
            this.entity = (LivingEntity) updatedEntity;
        }
        super.runEveryTick();
        if (getStunTicks() > 0) {
            setStunTicks(getStunTicks() - 1, true);
        }
    }

    @Override
    public void updateHealth() {
        if (!isDead()) {
            nameDisplay.customName(getNameComponent());
            nameDisplay.teleport(entity.getLocation().add(0, entity.getHeight() + 0.275, 0));

            entity.customName(Component.text(NumberFormat.addCommaAndRound(this.getHealth()) + "❤", NamedTextColor.RED));
        }
    }

    @Override
    public void updateEntity() {
        if (nameDisplay == null) {
            nameDisplay = Utils.spawnArmorStand(getLocation().add(0, entity.getHeight() + 0.275, 0), armorStand -> {
                armorStand.setMarker(true);
                armorStand.customName(getNameComponent());
                armorStand.setCustomNameVisible(true);
            });
        } else {
            nameDisplay.customName(getNameComponent());
            nameDisplay.teleport(entity.getLocation().add(0, entity.getHeight() + 0.275, 0));
        }

        entity.customName(Component.text(NumberFormat.addCommaAndRound(this.getHealth()) + "❤", NamedTextColor.RED));
        entity.setCustomNameVisible(true);
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));

        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (attribute != null) {
            attribute.setBaseValue(100);
        } else {
            entity.registerAttribute(Attribute.GENERIC_FOLLOW_RANGE);
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(100);
        }
    }

    @Override
    public void setDamageResistance(int damageResistance) {
        getSpec().setDamageResistance(Math.max(0, damageResistance));
        nameDisplay.customName(getNameComponent());
    }

    public int getStunTicks() {
        return stunTicks;
    }

    public void setStunTicks(int stunTicks) {
        setStunTicks(stunTicks, false);
    }

    public void setStunTicks(int stunTicks, boolean decrement) {
        AtomicReference<Boolean> noAI = new AtomicReference<>();
        if (mob == null) {
            return;
        }
        CustomEntity<?> customEntity = mob.getEntity();
        if (stunTicks > 0) {
            if (this.stunTicks <= 0) {
                customEntity.setStunned(true);
                noAI.set(true);
            }
        } else {
            noAI.set(false);
        }
        if (noAI.get() != null) {
            Mob entityInsentient = customEntity.get();
            entityInsentient.setNoAi(noAI.get());
            //tick later to prevent collision issues
            if (!noAI.get()) {
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

    public AbstractMob<?> getMob() {
        return mob;
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

    public float getDamageResistancePrefix() {
        return damageResistance;
    }

    public void setDamageResistancePrefix(float damageResistance) {
        this.damageResistance = Math.max(0, damageResistance);
    }
}
