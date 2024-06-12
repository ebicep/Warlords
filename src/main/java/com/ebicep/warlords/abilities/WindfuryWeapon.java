package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.WindfuryBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WindfuryWeapon extends AbstractAbility implements PurpleAbilityIcon, Duration {

    public int timesProcd = 0;

    private int tickDuration = 160;
    private float procChance = 35;
    private int maxHits = 2;
    private float weaponDamage = 135;

    public WindfuryWeapon() {
        super("Windfury Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Imbue your weapon with the power of the wind, causing each of your melee attacks to have a ")
                               .append(Component.text(format(procChance) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" chance to hit "))
                               .append(Component.text(maxHits, NamedTextColor.YELLOW))
                               .append(Component.text(" additional times for "))
                               .append(Component.text(format(weaponDamage) + "%", NamedTextColor.RED))
                               .append(Component.text(" weapon damage. The first melee hit is guaranteed to activate Windfury. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + timesProcd));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "shaman.windfuryweapon.activation", 2, 1);

        WindfuryWeapon tempWindfuryWeapon = new WindfuryWeapon();
        wp.getCooldownManager().removeCooldown(WindfuryWeapon.class, false);
        CalculateSpeed.Modifier shreddingFurySpeed = new CalculateSpeed.Modifier(wp, "Shredding Fury", 0, Integer.MAX_VALUE, Collections.emptyList(), false);
        wp.addSpeedModifier(shreddingFurySpeed);
        AtomicInteger procs = new AtomicInteger(0);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "FURY",
                WindfuryWeapon.class,
                tempWindfuryWeapon,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    shreddingFurySpeed.setDuration(0);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        wp.getWorld().spawnParticle(
                                Particle.CRIT,
                                wp.getLocation().add(0, 1.2, 0),
                                3,
                                0.2,
                                0,
                                0.2,
                                0.1,
                                null,
                                true
                        );

                    }
                })
        ) {

            private boolean firstProc = true;

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade2) {
                    return currentDamageValue * (100 - Math.min(15, procs.get() * 2.5f)) / 100;
                }
                return currentDamageValue;
            }

            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getAbility().isEmpty() || event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                    return;
                }
                WarlordsEntity victim = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getAttacker();

                double windfuryActivate = ThreadLocalRandom.current().nextDouble(100);
                if (firstProc) {
                    firstProc = false;
                    windfuryActivate = 0;
                }
                if (!(windfuryActivate < procChance)) {
                    return;
                }
                procs.incrementAndGet();
                timesProcd++;
                new GameRunnable(victim.getGame()) {
                    final float minDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                            warlordsPlayer.getWeapon().getMeleeDamageMin() : 132;
                    final float maxDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                            warlordsPlayer.getWeapon().getMeleeDamageMax() : 179;
                    int counter = 0;

                    @Override
                    public void run() {
                        Utils.playGlobalSound(victim.getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                        float healthDamage = victim.getMaxHealth() * 0.01f;
                        healthDamage = DamageCheck.clamp(healthDamage);
                        victim.addDamageInstance(
                                attacker,
                                name,
                                minDamage * (weaponDamage / 100f) + (pveMasterUpgrade ? healthDamage : 0),
                                maxDamage * (weaponDamage / 100f) + (pveMasterUpgrade ? healthDamage : 0),
                                critChance,
                                critMultiplier
                        );

                        if (pveMasterUpgrade) {
                            victim.setDamageResistance(victim.getSpec().getDamageResistance() - 2);
                            if (victim instanceof WarlordsNPC npc) {
                                npc.setDamageResistance(npc.getSpec().getDamageResistance() - 2);
                            }
                        }

                        counter++;
                        if (counter == maxHits) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(3, 3);

                if (pveMasterUpgrade2 && procs.get() <= 10) {
                    shreddingFurySpeed.setModifier(shreddingFurySpeed.getModifier() + 2.5f);
                    wp.getSpeed().setChanged(true);
                }
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WindfuryBranch(abilityTree, this);
    }

    public float getProcChance() {
        return procChance;
    }

    public void setProcChance(float procChance) {
        this.procChance = procChance;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public float getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(float weaponDamage) {
        this.weaponDamage = weaponDamage;
    }


    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
