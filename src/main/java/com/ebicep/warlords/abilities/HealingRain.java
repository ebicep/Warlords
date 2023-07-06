package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Overheal;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.HealingRainBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HealingRain extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int playersHealed = 0;

    private int tickDuration = 240;
    private int radius = 8;

    public HealingRain() {
        super("Healing Rain", 100, 125, 52.85f, 50, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Conjure rain at targeted location that will restore ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health every 0.5 seconds to allies. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.text("\n\nRecast to move Healing Rain to your location."))
                               .append(Component.text("\n\nHealing Rain can overheal allies for up to "))
                               .append(Component.text("10%", NamedTextColor.GREEN))
                               .append(Component.text(" of their max health as bonus health for "))
                               .append(Component.text(String.valueOf(Overheal.OVERHEAL_DURATION), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Block targetBlock = Utils.getTargetBlock(player, 25);
        if (targetBlock.getType() == Material.AIR) {
            wp.sendMessage(Component.text("The location is too far away!", NamedTextColor.RED));
            return false;
        }
        wp.subtractEnergy(energyCost, false);

        Location location = targetBlock.getLocation().clone();
        Utils.playGlobalSound(location, "mage.healingrain.impact", 2, 1);

        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                new AreaEffect(5, Particle.CLOUD).particlesPerSurface(0.025),
                new AreaEffect(5, Particle.DRIP_WATER).particlesPerSurface(0.025)
        );

        BukkitTask particleTask = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);

        RegularCooldown<HealingRain> healingRainCooldown = new RegularCooldown<>(
                name,
                "RAIN",
                HealingRain.class,
                new HealingRain(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    particleTask.cancel();
                    if (pveMasterUpgrade) {
                        for (WarlordsEntity enemyInRain : PlayerFilter
                                .entitiesAround(location, radius, radius, radius)
                                .aliveEnemiesOf(wp)
                                .limit(8)
                        ) {
                            Utils.playGlobalSound(enemyInRain.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.8f);
                            FireWorkEffectPlayer.playFirework(enemyInRain.getLocation(), FireworkEffect.builder()
                                                                                                       .withColor(Color.AQUA)
                                                                                                       .with(FireworkEffect.Type.BURST)
                                                                                                       .build());
                            strikeInRain(wp, enemyInRain);
                        }
                    }
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        for (WarlordsEntity teammateInRain : PlayerFilter
                                .entitiesAround(location, radius, radius, radius)
                                .aliveTeammatesOf(wp)
                        ) {
                            playersHealed++;
                            teammateInRain.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );

                            if (teammateInRain != wp) {
                                teammateInRain.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                                teammateInRain.getCooldownManager().addRegularCooldown(
                                        "Overheal",
                                        "OVERHEAL",
                                        Overheal.class,
                                        Overheal.OVERHEAL_MARKER,
                                        wp,
                                        CooldownTypes.BUFF,
                                        cooldownManager -> {
                                        },
                                        Overheal.OVERHEAL_DURATION * 20
                                );
                            }
                        }
                    }

                    if (ticksElapsed % 40 == 0) {
                        if (pveMasterUpgrade) {
                            for (WarlordsEntity enemyInRain : PlayerFilter
                                    .entitiesAround(location, radius, radius, radius)
                                    .aliveEnemiesOf(wp)
                                    .limit(8)
                            ) {
                                Utils.playGlobalSound(enemyInRain.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.8f);
                                FireWorkEffectPlayer.playFirework(enemyInRain.getLocation(), FireworkEffect.builder()
                                                                                                           .withColor(Color.AQUA)
                                                                                                           .with(FireworkEffect.Type.BURST)
                                                                                                           .build());
                                strikeInRain(wp, enemyInRain);
                            }
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(healingRainCooldown);

        addSecondaryAbility(() -> {
                    if (wp.isAlive()) {
                        wp.playSound(wp.getLocation(), "mage.timewarp.teleport", 2, 1.35f);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " §7You moved your §aHealing Rain §7to your current location.");
                        location.setX(wp.getLocation().getX());
                        location.setY(wp.getLocation().getY());
                        location.setZ(wp.getLocation().getZ());
                    }
                },
                true,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingRainCooldown)
        );

        return true;
    }

    private void strikeInRain(WarlordsEntity giver, WarlordsEntity hit) {
        for (WarlordsEntity strikeTarget : PlayerFilter
                .entitiesAround(hit, 2, 3, 2)
                .aliveEnemiesOf(giver)
        ) {
            strikeTarget.getWorld().spigot().strikeLightningEffect(strikeTarget.getLocation(), true);
            float healthDamage = strikeTarget.getMaxHealth() * 0.01f;
            if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                healthDamage = DamageCheck.MINIMUM_DAMAGE;
            }
            if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                healthDamage = DamageCheck.MAXIMUM_DAMAGE;
            }
            strikeTarget.addDamageInstance(giver, name, 224 + healthDamage, 377 + healthDamage, -1, 100);
        }

    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingRainBranch(abilityTree, this);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
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
