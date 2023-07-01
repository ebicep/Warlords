package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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

public class Windfury extends AbstractAbility implements Duration {

    public int timesProcd = 0;

    private int tickDuration = 160;
    private float procChance = 35;
    private int maxHits = 2;
    private float weaponDamage = 135;

    public Windfury() {
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
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "shaman.windfuryweapon.activation", 2, 1);

        Windfury tempWindfury = new Windfury();
        final boolean[] firstProc = {true};
        wp.getCooldownManager().removeCooldown(Windfury.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "FURY",
                Windfury.class,
                tempWindfury,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
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
            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().isEmpty()) {
                    WarlordsEntity victim = event.getWarlordsEntity();
                    WarlordsEntity attacker = event.getAttacker();

                    double windfuryActivate = ThreadLocalRandom.current().nextDouble(100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < procChance) {
                        timesProcd++;
                        new GameRunnable(victim.getGame()) {
                            final float minDamage = wp instanceof WarlordsPlayer && ((WarlordsPlayer) wp).getWeapon() != null ?
                                                    ((WarlordsPlayer) wp).getWeapon().getMeleeDamageMin() : 132;
                            final float maxDamage = wp instanceof WarlordsPlayer && ((WarlordsPlayer) wp).getWeapon() != null ?
                                                    ((WarlordsPlayer) wp).getWeapon().getMeleeDamageMax() : 179;
                            int counter = 0;

                            @Override
                            public void run() {
                                Utils.playGlobalSound(victim.getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                                float healthDamage = victim.getMaxHealth() * 0.01f;
                                if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                }
                                if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                }
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
                                }

                                counter++;
                                if (counter == maxHits) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(3, 3);
                    }
                }
            }
        });

        return true;
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
