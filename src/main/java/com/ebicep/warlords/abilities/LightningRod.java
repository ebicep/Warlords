package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.LightningRodBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightningRod extends AbstractAbility implements BlueAbilityIcon {

    private final int knockbackRadius = 5;
    private int energyRestore = 160;
    private int healthRestore = 30;

    public LightningRod() {
        this(31.32f, 0);
    }

    public LightningRod(float cooldown, float startCooldown) {
        super("Lightning Rod", 0, 0, cooldown, 0, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Call down an energizing bolt of lightning upon yourself, restoring ")
                               .append(Component.text(healthRestore + "%", NamedTextColor.GREEN))
                               .append(Component.text(" health and "))
                               .append(Component.text(energyRestore + " ", NamedTextColor.YELLOW))
                               .append(Component.text("energy and knock all nearby enemies in a "))
                               .append(Component.text(knockbackRadius + " ", NamedTextColor.YELLOW))
                               .append(Component.text("block radius back."));

    }


    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        List<WarlordsEntity> hit = kbHealEnergy(wp);

        if (pveMasterUpgrade) {
            damageIncreaseOnUse(wp);
            new GameRunnable(wp.getGame()) {
                int bonusActivations = 0;

                @Override
                public void run() {
                    if (bonusActivations++ < 2) {
                        kbHealEnergy(wp);
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 40);
        } else if (pveMasterUpgrade2) {
            giveCallOfThunderEffect(wp, hit);
        }

        // pulsedamage
        List<CapacitorTotem> totemDownAndClose = AbstractTotem.getTotemsDownAndClose(wp, wp.getEntity(), CapacitorTotem.class);
        totemDownAndClose.forEach(capacitorTotem -> {
            ArmorStand totem = capacitorTotem.getTotem();

            Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
            wp.playSound(wp.getLocation(), "shaman.chainlightning.impact", 2, 1);

            capacitorTotem.pulseDamage();
            if (capacitorTotem.isPveMasterUpgrade()) {
                capacitorTotem.setRadius(capacitorTotem.getRadius() + 0.5);
            }
            capacitorTotem.addProc();
        });


        return true;
    }

    private List<WarlordsEntity> kbHealEnergy(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyRestore);
        Utils.playGlobalSound(wp.getLocation(), "shaman.lightningrod.activation", 2, 1);
        new FallingBlockWaveEffect(wp.getLocation(), knockbackRadius, 1, Material.ORANGE_TULIP).play();
        wp.getWorld().spigot().strikeLightningEffect(wp.getLocation(), true);
        wp.addHealingInstance(
                wp,
                name,
                (wp.getMaxHealth() * (healthRestore / 100f)),
                (wp.getMaxHealth() * (healthRestore / 100f)),
                critChance,
                critMultiplier
        );

        List<WarlordsEntity> hit = PlayerFilter
                .entitiesAround(wp, knockbackRadius, knockbackRadius, knockbackRadius)
                .aliveEnemiesOf(wp)
                .toList();
        for (WarlordsEntity enemy : hit) {
            if (pveMasterUpgrade2) {
                if (enemy instanceof WarlordsNPC warlordsNPC) {
                    warlordsNPC.setStunTicks(60);
                }
            } else {
                final Location loc = enemy.getLocation();
                final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
                enemy.setVelocity(name, v, false);
            }
        }
        return hit;
    }

    private void damageIncreaseOnUse(WarlordsEntity we) {
        we.addSpeedModifier(we, "Rod Speed", 40, 12 * 20, "BASE");
        we.getCooldownManager().removeCooldown(LightningRod.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ROD DMG",
                LightningRod.class,
                new LightningRod(),
                we,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                12 * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.4f;
            }
        });
    }

    private void giveCallOfThunderEffect(WarlordsEntity from, List<WarlordsEntity> hit) {
        LightningRod tempRod = new LightningRod();
        for (WarlordsEntity warlordsEntity : hit) {
            warlordsEntity.getCooldownManager().removeCooldownByName("Call of Thunder Debuff");
            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Call of Thunder Debuff",
                    "THUN",
                    LightningRod.class,
                    tempRod,
                    from,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    8 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            EffectUtils.displayParticle(
                                    Particle.CRIT_MAGIC,
                                    warlordsEntity.getLocation().add(0, 1.2, 0),
                                    3,
                                    .25,
                                    .25,
                                    .25,
                                    0
                            );
                        }
                    })
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 1.25f;
                }
            });
        }
        from.getCooldownManager().removeCooldownByName("Call of Thunder Buff");
        from.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Call of Thunder Buff",
                "THUN",
                LightningRod.class,
                tempRod,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                8 * 20
        ) {
            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + 15 / 20f;
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightningRodBranch(abilityTree, this);
    }

    public int getHealthRestore() {
        return healthRestore;
    }

    public void setHealthRestore(int healthRestore) {
        this.healthRestore = healthRestore;
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }


}
