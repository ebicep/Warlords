package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.speed.BaseToWalkingSpeedValueModifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.mobs.bosses.raidbosses.RaidBossUtils;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WarlordsNPC extends WarlordsEntity {

    private static final int HOLOGRAM_UPDATE_INTERVAL = 4;

    protected float meleeCritChance;
    protected float meleeCritMultiplier;
    protected NPC npc;
    protected AbstractMob mob;
    protected Component mobNamePrefix = Component.empty();
    @Nonnull
    protected TextColor nameColor = NamedTextColor.GRAY;
    private final MobHologram mobHologram;
    private final int hologramUpdateOffset;
    private final RaidBossUtils.RaidBossHealthBar bossHealthBar;
    private float minMeleeDamage;
    private float maxMeleeDamage;
    private ArmorStand playerHealthDisplay; // used for player entity type npcs
    private int stunTicks;
    private int lastDisplayedHealth = Integer.MIN_VALUE;

    public WarlordsNPC(
            String name,
            NPC npc,
            Game game,
            Team team,
            int maxHealth,
            float walkSpeed,
            float minMeleeDamage,
            float maxMeleeDamage,
            float meleeCritChance,
            float meleeCritMultiplier,
            AbstractMob warlordsMob,
            AbstractPlayerClass playerClass,
            MobHologram mobHologram
    ) {
        super(npc.getUniqueId(), name, npc.getEntity(), game, team, playerClass);
        this.npc = npc;
        this.npc.data().set(WARLORDS_ENTITY_METADATA, this);
        this.mob = warlordsMob;
        this.mobHologram = mobHologram;
        this.hologramUpdateOffset = Math.floorMod(npc.getUniqueId().hashCode(), HOLOGRAM_UPDATE_INTERVAL);
        if (warlordsMob != null && warlordsMob.getInternalLevel() > 1) {
            mobNamePrefix = Component.textOfChildren(
                    Component.text("[", NamedTextColor.GRAY),
                    warlordsMob.getNamePrefix(),
                    Component.text("] ", NamedTextColor.GRAY)
            );
        }
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;
        this.meleeCritChance = meleeCritChance;
        this.meleeCritMultiplier = meleeCritMultiplier;
        this.speed.addModifier(new MotionModifierBuilder()
                .setFrom(this)
                .setName("BASE")
                .setModifier(13)
                .setDuration(-1)
                .addAddons(new BaseToWalkingSpeedValueModifier(walkSpeed))
                .build()
        );
        updateEntity();
        entity.setMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
        setMaxHealthAndHeal(maxHealth);

        mobHologram.getCustomHologramLines().add(new MobHologram.CustomHologramLine(this::getNameComponent));
        mobHologram.update();

        if (warlordsMob instanceof BossMob) {
            bossHealthBar = RaidBossUtils.createHealthBar(
                    this,
                    1f,
                    warlordsMob.getMobScale() + 0.1,
                    warlordsMob.getName(),
                    warlordsMob.getDescription(),
                    NamedTextColor.RED
            );
        } else {
            bossHealthBar = null;
        }
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

    @Override
    public boolean die(@Nullable WarlordsEntity attacker, WarlordsDeathEvent.DeathInfoBuilder deathInfoBuilder) {
        boolean dead = super.die(attacker, deathInfoBuilder);
        if (dead) {
            cleanup();
        }
        return dead;
    }

    @Override
    protected void addToSpecMinuteStats(Consumer<PlayerStatisticsMinute> consumer) {
        // override to do nothing, npcs dont need stats, save memory
    }

    @Override
    public boolean setStunTicks(int stunTicks) {
        AtomicReference<Boolean> noAI = new AtomicReference<>();
        if (mob == null) {
            return false;
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
            if (!noAI.get()) {
                unstun();
            }
        }
        //stun needs to be longer to override current
        if (this.stunTicks < stunTicks) {
            this.stunTicks = stunTicks;
        }
        return true;
    }

    @Override
    public void unstun() {
        mob.toggleStun(false);
        //tick later to prevent collision issues
        new GameRunnable(game) {
            @Override
            public void run() {
                npc.data().set(NPC.Metadata.COLLIDABLE, true);
            }
        }.runTaskLater(1);
    }

    @Override
    public void addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration) {
        if (modifier != -99) {
            if (getMob() instanceof BossLike) {
                if (modifier < 0) {
                    modifier *= .4f;
                }
            } else {
                if (modifier < 0) {
                    modifier *= .7f;
                }
            }
        }
        super.addSpeedModifier(from, name, modifier, duration);
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
        if (updatedEntity != null && (!Objects.equals(updatedEntity, entity) && updatedEntity instanceof LivingEntity || (isAlive() && entity != null && !entity.isValid()))) {
            this.entity = updatedEntity;
        }
        super.runEveryTick();
        if (bossHealthBar != null) {
            bossHealthBar.update();
        }
        if (stunTicks > 0) {
            stunTicks--;
            if (stunTicks == 0) {
                unstun();
            }
        }
    }

    @Override
    public void updateHealth() {
        if (isDead() || entity == null || !entity.isValid()) {
            return;
        }

        boolean hologramUpdateTick = Math.floorMod(getGame().getLoopTickCounter() + hologramUpdateOffset, HOLOGRAM_UPDATE_INTERVAL) == 0;
        if (hologramUpdateTick) {
            mobHologram.updatePosition();
        }

        int rounded = Math.round(getCurrentHealth());
        boolean shouldUpdateName = rounded != lastDisplayedHealth && getGame().getLoopTickCounter() % 2 == 0;

        if (entity instanceof Player player) {
            double healthDisplayY = player.getEyeHeight() + 0.15;
            if (playerHealthDisplay == null) {
                playerHealthDisplay = Utils.spawnArmorStand(getLocation().add(0, healthDisplayY, 0), armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.setCustomNameVisible(true);
                });
                shouldUpdateName = true;
            } else if (hologramUpdateTick) {
                playerHealthDisplay.teleport(entity.getLocation().add(0, healthDisplayY, 0));
            }
        }

        if (!shouldUpdateName) {
            return;
        }

        String healthText = NumberFormat.addCommaAndRound(rounded) + "❤";
        if (entity instanceof Player) {
            playerHealthDisplay.customName(Component.text(healthText, NamedTextColor.RED));
        } else {
            String citizensName = "§c" + healthText;
            if (!citizensName.equals(npc.getName())) {
                npc.setName(citizensName);
            }
        }
        lastDisplayedHealth = rounded;
    }

    @Override
    public void updateEntity() {
        if (entity == null) {
            return;
        }
        updateHealth();
        entity.setCustomNameVisible(true);
        entity.setMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, new FixedMetadataValue(Warlords.getInstance(), this));
    }

    @Override
    public void setSpec(Specializations spec) {

    }

    @Override
    public void setDamageResistance(float damageResistance) {
        getSpec().setDamageResistance(Math.max(0, damageResistance));
        mobHologram.markTextDirty();
        mobHologram.update();
        updateHealth();
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

    public AbstractMob getMob() {
        return mob;
    }

    public void cleanup() {
        if (bossHealthBar != null) {
            bossHealthBar.remove();
        }
        if (entity != null) {
            entity.removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, Warlords.getInstance());
            entity.remove();
        }
        npc.data().remove(WARLORDS_ENTITY_METADATA);
        npc.destroy();
        if (playerHealthDisplay != null) {
            playerHealthDisplay.remove();
        }
        mobHologram.clearLines();
    }

    public int getStunTicks() {
        return stunTicks;
    }

    public Component getMobNamePrefix() {
        return mobNamePrefix;
    }

    public void setNameColor(@Nonnull TextColor nameColor) {
        this.nameColor = nameColor;
        mobHologram.markTextDirty();
        mobHologram.update();
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

    public float getMeleeCritChance() {
        return meleeCritChance;
    }

    public float getMeleeCritMultiplier() {
        return meleeCritMultiplier;
    }

    public NPC getNpc() {
        return npc;
    }

    public MobHologram getMobHologram() {
        return mobHologram;
    }

}
