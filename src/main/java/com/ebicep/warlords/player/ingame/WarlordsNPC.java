package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class WarlordsNPC extends WarlordsEntity {

    private float minMeleeDamage;
    private float maxMeleeDamage;
    private NPC npc;
    private AbstractMob mob;
    private Component mobNamePrefix = Component.empty();
    private ArmorStand nameDisplay;
    @Nonnull
    private TextColor nameColor = NamedTextColor.GRAY;
//    private HologramTrait hologramTrait;

    private int stunTicks;

    public WarlordsNPC(
            String name,
            NPC npc,
            Game game,
            Team team,
            int maxHealth,
            float walkSpeed,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractMob warlordsMob,
            AbstractPlayerClass playerClass
    ) {
        super(npc.getUniqueId(), name, npc.getEntity(), game, team, playerClass);
        this.npc = npc;
//        this.hologramTrait = npc.getOrAddTrait(HologramTrait.class);
//        this.hologramTrait.setUseDisplayEntities(false); //TODO
        this.mob = warlordsMob;
        if (warlordsMob != null && warlordsMob.getLevel() > 0) {
            mobNamePrefix = Component.textOfChildren(
                    Component.text("[", NamedTextColor.GRAY),
                    Component.text(warlordsMob.getLevel(), warlordsMob.getTextColor()),
                    Component.text("] ", NamedTextColor.GRAY)
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
    }

    @Nonnull
    private TextComponent getNameComponent() {
        if (mob == null) {
            return Component.text(name, nameColor);
        }
        TextComponent.Builder builder = Component.text();
        if (!mobNamePrefix.equals(Component.empty())) {
            builder.append(mobNamePrefix)
                   .append(Component.text("- "));
        }

        Aspect aspect = mob.getAspect();
        if (aspect != null) {
            nameColor = aspect.textColor;
            builder.append(Component.text(aspect.name + " ", aspect.textColor));
        }

        int resistance = spec.getDamageResistance();
        builder.append(Component.text(name, nameColor));
        if (resistance > 0) {
            builder.append(Component.text(" - "))
                   .append(Component.text(NumberFormat.formatOptionalTenths(resistance) + "% ⛊", NamedTextColor.GOLD));
        }

        return builder.build();
    }

    public Component getMobNamePrefix() {
        return mobNamePrefix;
    }

    public void setNameColor(TextColor nameColor) {
        this.nameColor = nameColor;
    }

    @Override
    public void die(@Nullable WarlordsEntity attacker) {
        super.die(attacker);
        npc.destroy();
        nameDisplay.remove();
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
                setStunTicks(potionEffect.getDuration());
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
            this.entity = updatedEntity;
        }
        super.runEveryTick();
        if (getStunTicks() > 0) {
            setStunTicks(getStunTicks() - 1, true);
        }
    }

    @Override
    public void updateHealth() {
        if (!isDead()) {
//            hologramTrait.setLine(0, LegacyComponentSerializer.legacySection().serialize(getNameComponent()));
            if (nameDisplay == null) {
                nameDisplay = Utils.spawnArmorStand(getLocation().add(0, entity.getHeight() + 0.275, 0), armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.customName(getNameComponent());
                    armorStand.setCustomNameVisible(true);
                });
            } else {
                nameDisplay.customName(getNameComponent());
                nameDisplay.teleport(entity.getLocation().add(0, entity.getHeight() + 0.275, 0));
//                entity.addPassenger(nameDisplay);
            }
            entity.customName(Component.text(NumberFormat.addCommaAndRound(this.getHealth()) + "❤", NamedTextColor.RED));
        }
    }

    @Override
    public void updateEntity() {
        updateHealth();
        entity.setCustomNameVisible(true);
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
    }

    @Override
    public void setDamageResistance(int damageResistance) {
        getSpec().setDamageResistance(Math.max(0, damageResistance));
        updateHealth();
        nameDisplay.customName(getNameComponent());
    }

    @Override
    public int getBaseHitCooldownValue() {
        return 20;
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
        if (stunTicks > 0) {
            if (this.stunTicks <= 0) {
                npc.data().set(NPC.Metadata.COLLIDABLE, false);
                noAI.set(true);
            }
        } else {
            noAI.set(false);
        }
        if (noAI.get() != null) {
            mob.toggleStun(noAI.get());
            //tick later to prevent collision issues
            if (!noAI.get()) {
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        npc.data().set(NPC.Metadata.COLLIDABLE, true);
                    }
                }.runTaskLater(1);
            }
        }
        //stun needs to be longer to override current
        if (decrement || this.stunTicks < stunTicks) {
            this.stunTicks = stunTicks;
        }
    }

    public AbstractMob getMob() {
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

    public NPC getNpc() {
        return npc;
    }
}
