package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectPlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.HealingRainBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class HealingRain extends AbstractAbility implements OrangeAbilityIcon, Duration, HitBox {

    public int playersHealed = 0;

    private int tickDuration = 200;
    private FloatModifiable radius = new FloatModifiable(8);

    public HealingRain() {
        this(60, 0);
    }

    public HealingRain(float cooldown, float startCooldown) {
        super("Healing Rain", 100, 125, cooldown, 50, 25, 180, startCooldown);
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Block targetBlock = !(wp.getEntity() instanceof Player) ? LocationUtils.getGroundLocation(wp.getLocation()).getBlock() : Utils.getTargetBlock(wp, 25);
        if (targetBlock.getType() == Material.AIR) {
            return false;
        }


        Location location = targetBlock.getLocation().clone();
        location.add(0, 1, 0);
        Utils.playGlobalSound(location, "mage.healingrain.impact", 2, 1);

        List<EffectPlayer<? super CircleEffect>> effects = new ArrayList<>();
        effects.add(new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE));
        if (!pveMasterUpgrade2) {
            effects.add(new AreaEffect(5, Particle.CLOUD).particlesPerSurface(0.025));
            effects.add(new AreaEffect(5, Particle.DRIP_WATER).particlesPerSurface(0.025));
        }
        float rad = radius.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                rad,
                effects.toArray(new EffectPlayer[0])
        );

        // pveMasterUpgrade2
        AtomicReference<List<Pair<WarlordsEntity, CircleEffect>>> personalCloud = new AtomicReference<>(new ArrayList<>());

        RegularCooldown<HealingRain> healingRainCooldown = new RegularCooldown<>(
                name,
                "RAIN",
                HealingRain.class,
                new HealingRain(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        for (WarlordsEntity enemyInRain : PlayerFilter
                                .entitiesAround(location, rad, rad, rad)
                                .aliveEnemiesOf(wp)
                                .limit(8)
                        ) {
                            Utils.playGlobalSound(enemyInRain.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1.8f);
                            EffectUtils.playFirework(enemyInRain.getLocation(), FireworkEffect.builder()
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
                    List<Pair<WarlordsEntity, CircleEffect>> personalCloudList = personalCloud.get();
                    if (pveMasterUpgrade2) {
                        personalCloudList.forEach(warlordsEntityCircleEffectPair -> {
                            WarlordsEntity cloudTeammate = warlordsEntityCircleEffectPair.getA();
                            CircleEffect effect = warlordsEntityCircleEffectPair.getB();
                            Location cloudTeammateLocation = cloudTeammate.getLocation();
                            Location center = effect.getCenter();
                            center.set(cloudTeammateLocation.getX(), cloudTeammateLocation.getY(), cloudTeammateLocation.getZ());
                            effect.playEffects();
                        });
                    }
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }

                    if (ticksElapsed % 10 == 0) {
                        List<WarlordsEntity> teammatesInRain = PlayerFilter
                                .entitiesAround(location, rad, rad, rad)
                                .aliveTeammatesOf(wp)
                                .toList();
                        if (pveMasterUpgrade2) {
                            // cloud only give to those in cloud or has been in cloud and is within 40 blocks of player
                            personalCloudList.removeIf(teammate ->
                                    //  !teammatesInRain.contains(teammate.getA()) &&
                                    teammate.getA().getLocation().distanceSquared(wp.getLocation()) > 40 * 40
                            );
                            for (WarlordsEntity teammateInRain : teammatesInRain) {
                                if (personalCloudList.stream().noneMatch(pair -> pair.getA() == teammateInRain)) {
                                    personalCloudList.add(new Pair<>(teammateInRain, new CircleEffect(
                                            wp.getGame(),
                                            wp.getTeam(),
                                            teammateInRain.getLocation().clone(),
                                            2,
                                            new AreaEffect(4, Particle.CLOUD).particlesPerSurface(0.1),
                                            new AreaEffect(4, Particle.DRIP_WATER).particlesPerSurface(0.1)
                                    )));
                                }
                            }
                            for (Pair<WarlordsEntity, CircleEffect> cloudTeammatePair : personalCloudList) {
                                WarlordsEntity cloudTeammate = cloudTeammatePair.getA();
                                heal(wp, cloudTeammate, "Rain Cloud");
                                CooldownManager cloudTeammateCooldownManager = cloudTeammate.getCooldownManager();
                                cloudTeammateCooldownManager.removeCooldownByName("Nimbus");
                                cloudTeammateCooldownManager.addCooldown(new RegularCooldown<>(
                                        "Nimbus",
                                        null,
                                        HealingRain.class,
                                        new HealingRain(),
                                        wp,
                                        CooldownTypes.ABILITY,
                                        cooldownManager -> {
                                        },
                                        10
                                ) {
                                    @Override
                                    public float addEnergyGainPerTick(float energyGainPerTick) {
                                        return energyGainPerTick + .25f;
                                    }
                                });
                            }
                        } else {
                            for (WarlordsEntity teammateInRain : teammatesInRain) {
                                heal(wp, teammateInRain, name);
                            }
                        }

                    }

                    if (ticksElapsed % 40 == 0) {
                        if (pveMasterUpgrade) {
                            for (WarlordsEntity enemyInRain : PlayerFilter
                                    .entitiesAround(location, rad, rad, rad)
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

        addSecondaryAbility(
                1,
                () -> {
                    if (wp.isAlive()) {
                        Location wpLocation = wp.getLocation();
                        wp.playSound(wpLocation, "mage.timewarp.teleport", 2, 1.35f);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" You moved your ", NamedTextColor.GRAY)
                                                 .append(Component.text("Healing Rain", NamedTextColor.GREEN))
                                                 .append(Component.text(" to your current location."))
                                )
                        );
                        location.set(wpLocation.getX(), wpLocation.getY() + .01, wpLocation.getZ());
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
            healthDamage = DamageCheck.clamp(healthDamage);
            strikeTarget.addDamageInstance(giver, name, 224 + healthDamage, 377 + healthDamage, -1, 100);
        }

    }

    private void heal(@Nonnull WarlordsEntity wp, WarlordsEntity teammateInRain, String name) {
        playersHealed++;
        teammateInRain.addHealingInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                EnumSet.of(InstanceFlags.CAN_OVERHEAL_OTHERS, InstanceFlags.NO_HIT_SOUND)
        );

        if (teammateInRain != wp) {
            Overheal.giveOverHeal(wp, teammateInRain);
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingRainBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
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
