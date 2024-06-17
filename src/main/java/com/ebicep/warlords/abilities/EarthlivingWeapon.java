package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.EarthlivingWeaponBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EarthlivingWeapon extends AbstractAbility implements PurpleAbilityIcon, Duration {

    public int timesProcd = 0;
    public int playersHealed = 0;

    private int tickDuration = 160;
    private float procChance = 40;
    private int maxAllies = 2;
    private int weaponDamage = 240;
    private int maxHits = 1;

    public EarthlivingWeapon() {
        super("Earthliving Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Imbue your weapon with the power of the Earth, causing each of your melee attacks to have a ")
                               .append(Component.text(format(procChance) + "% ", NamedTextColor.YELLOW))
                               .append(Component.text("chance to heal you and "))
                               .append(Component.text("2", NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies for "))
                               .append(Component.text(weaponDamage + "%", NamedTextColor.GREEN))
                               .append(Component.text(" weapon damage. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.text("\n\nThe first hit is guaranteed to activate Earthliving."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + timesProcd));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);

        EarthlivingWeapon tempEarthlivingWeapon = new EarthlivingWeapon();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "EARTH",
                EarthlivingWeapon.class,
                tempEarthlivingWeapon,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(
                                Particle.VILLAGER_HAPPY,
                                wp.getLocation().add(0, 1.2, 0),
                                2,
                                0.3,
                                0.3,
                                0.3,
                                0.1
                        );
                    }
                })
        ) {

            private boolean firstProc = true;
            private Set<WarlordsEntity> alreadyProcd = new HashSet<>();

            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getCause().isEmpty()) {
                    return;
                }
                WarlordsEntity victim = event.getWarlordsEntity();
                WarlordsEntity attacker = event.getSource();

                double earthlivingActivate = ThreadLocalRandom.current().nextDouble(100);
                if (firstProc) {
                    firstProc = false;
                    earthlivingActivate = 0;
                }
                if (!(earthlivingActivate < procChance)) {
                    return;
                }

                boolean previosulyProcd = alreadyProcd.contains(victim);
                if (pveMasterUpgrade) {
                    energyPulseOnHit(attacker, victim);
                } else if (pveMasterUpgrade2) {
                    alreadyProcd.add(victim);
                }

                new GameRunnable(victim.getGame()) {
                    final float minDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                            warlordsPlayer.getWeapon().getMeleeDamageMin() : 132;
                    final float maxDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                            warlordsPlayer.getWeapon().getMeleeDamageMax() : 179;
                    int counter = 0;

                    @Override
                    public void run() {
                        timesProcd++;
                        Utils.playGlobalSound(victim.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);

                        float cc = pveMasterUpgrade2 && !previosulyProcd ? 100 : critChance;
                        attacker.addHealingInstance(
                                attacker,
                                name,
                                minDamage * convertToPercent(weaponDamage),
                                maxDamage * convertToPercent(weaponDamage),
                                cc,
                                critMultiplier
                        );

                        for (WarlordsEntity nearPlayer : PlayerFilter
                                .entitiesAround(attacker, 6, 6, 6)
                                .aliveTeammatesOfExcludingSelf(attacker)
                                .limit(maxAllies)
                        ) {
                            playersHealed++;
                            nearPlayer.addHealingInstance(
                                    attacker,
                                    name,
                                    minDamage * convertToPercent(weaponDamage),
                                    maxDamage * convertToPercent(weaponDamage),
                                    cc,
                                    critMultiplier
                            );
                        }

                        counter++;
                        if (counter == maxHits) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(3, 8);
            }

            @Override
            public float addEnergyPerHit(WarlordsEntity we, float energyPerHit) {
                return energyPerHit + 10f;
            }
        });

        return true;
    }

    private void energyPulseOnHit(WarlordsEntity giver, WarlordsEntity target) {
        target.getCooldownManager().addRegularCooldown(
                "Earthliving PvE",
                "",
                EarthlivingWeapon.class,
                new EarthlivingWeapon(),
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    Utils.playGlobalSound(target.getLocation(), "shaman.earthlivingweapon.impact", 2, 1.2f);
                    new FallingBlockWaveEffect(target.getLocation(), 6, 1, Material.BIRCH_SAPLING).play();
                    for (WarlordsEntity ally : PlayerFilter
                            .entitiesAround(target, 10, 10, 10)
                            .aliveTeammatesOf(giver)
                            .closestFirst(target)
                    ) {
                        float missingHealth = (ally.getMaxHealth() - ally.getCurrentHealth()) * 0.1f;
                        if (missingHealth <= 0) {
                            continue;
                        }
                        ally.addHealingInstance(
                                giver,
                                "Loamliving Weapon",
                                missingHealth,
                                missingHealth,
                                0,
                                100
                        );
                        ally.addEnergy(giver, "Loamliving Weapon", missingHealth / 20);
                    }
                },
                2 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (target instanceof WarlordsNPC) {
                        ((WarlordsNPC) target).setStunTicks(2);
                    }

                    if (ticksElapsed % 5 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1.05, Particle.VILLAGER_HAPPY, 1);
                    }
                })
        );
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EarthlivingWeaponBranch(abilityTree, this);
    }

    public float getProcChance() {
        return procChance;
    }

    public void setProcChance(float procChance) {
        this.procChance = procChance;
    }

    public int getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(int weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
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


