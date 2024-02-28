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
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WarlordsNPC extends WarlordsEntity {

    private final MobHologram mobHologram;
    private float minMeleeDamage;
    private float maxMeleeDamage;
    protected NPC npc;
    protected AbstractMob mob;
    protected Component mobNamePrefix = Component.empty();
    private ArmorStand playerHealthDisplay; // used for player entity type npcs
    @Nonnull
    protected TextColor nameColor = NamedTextColor.GRAY;
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
            AbstractPlayerClass playerClass,
            MobHologram mobHologram
    ) {
        super(npc.getUniqueId(), name, npc.getEntity(), game, team, playerClass);
        this.npc = npc;
//        this.hologramTrait = npc.getOrAddTrait(HologramTrait.class);
//        this.hologramTrait.setUseDisplayEntities(false); //TODO
        this.mob = warlordsMob;
        this.mobHologram = mobHologram;
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
        setMaxHealthAndHeal(maxHealth);

        mobHologram.getCustomHologramLines().add(new MobHologram.CustomHologramLine(this::getNameComponent));
    }

    @Nonnull
    protected TextComponent getNameComponent() {
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

        float resistance = spec.getDamageResistance();
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

    public MobHologram getMobHologram() {
        return mobHologram;
    }

    @Override
    public void removeHorse() {

    }

    @Override
    public void die(@Nullable WarlordsEntity attacker) {
        super.die(attacker);
        cleanup();
    }

    public void cleanup() {
        npc.destroy();
        if (playerHealthDisplay != null) {
            playerHealthDisplay.remove();
        }
        mobHologram.getCustomHologramLines().forEach(customHologramLine -> customHologramLine.getEntity().remove());
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
        Entity updatedEntity = npc.getEntity();
        if (updatedEntity != null && !Objects.equals(updatedEntity, entity) && updatedEntity instanceof LivingEntity || (isAlive() && entity != null && !entity.isValid())) {
            this.entity = updatedEntity;
        }
        super.runEveryTick();
        if (getStunTicks() > 0) {
            setStunTicks(getStunTicks() - 1, true);
        }
    }

    @Override
    public void updateHealth() {
        if (isDead() || entity == null) {
            return;
        }
        mobHologram.update();
        if (entity instanceof Player player) {
            double healthDisplayY = player.getEyeHeight() + 0.15;
            if (playerHealthDisplay == null) {
                playerHealthDisplay = Utils.spawnArmorStand(getLocation().add(0, healthDisplayY, 0), armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.customName(getNameComponent());
                    armorStand.setCustomNameVisible(true);
                });
            } else {
                playerHealthDisplay.customName(Component.text(NumberFormat.addCommaAndRound(this.getCurrentHealth()) + "❤", NamedTextColor.RED));
                playerHealthDisplay.teleport(entity.getLocation().add(0, healthDisplayY, 0));
            }
        } else {
            entity.customName(Component.text(NumberFormat.addCommaAndRound(this.getCurrentHealth()) + "❤", NamedTextColor.RED));
        }
    }

    @Override
    public void updateEntity() {
        if (entity == null) {
            return;
        }
        updateHealth();
        entity.setCustomNameVisible(true);
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
    }

    @Override
    public void setDamageResistance(float damageResistance) {
        getSpec().setDamageResistance(Math.max(0, damageResistance));
        updateHealth();
//        nameDisplay.customName(getNameComponent());
    }

    @Override
    public int getBaseHitCooldownValue() {
        return 20;
    }

    @Override
    public ItemStack getHead() {
        return mob.getMobRegistry().getHead();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getHelmet() {
        return npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HELMET);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getChestplate() {
        return npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.CHESTPLATE);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getLeggings() {
        return npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.LEGGINGS);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getBoots() {
        return npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.BOOTS);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getWeaponItem() {
        return npc.getOrAddTrait(Equipment.class).get(Equipment.EquipmentSlot.HAND);
    }

    public int getStunTicks() {
        return stunTicks;
    }

    public void setStunTicks(int stunTicks) {
        setStunTicks(stunTicks, false);
    }

//    public HologramTrait getHologramTrait() {
//        return hologramTrait;
//    }

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

    @Override
    protected void addToSpecMinuteStats(Consumer<PlayerStatisticsMinute> consumer) {
        // override to do nothing, npcs dont need stats, save memory
    }
}
