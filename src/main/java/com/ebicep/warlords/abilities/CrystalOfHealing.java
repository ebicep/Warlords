package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrystalOfHealing extends AbstractAbility implements PurpleAbilityIcon {

    private static final float RADIUS = 1.5f;
    private int duration = 15; // seconds
    private float maxHeal = 1500;
    private int lifeSpan = 45; // seconds

    public CrystalOfHealing() {
        super("Crystal of Healing", 20, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Create a crystal of healing that absorbs surrounding light over ")
                               .append(Component.text(format(duration), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, gradually increasing the amount of health it will restore to one ally when they absorb it, to a maximum of "))
                               .append(Component.text(format(maxHeal), NamedTextColor.GREEN))
                               .append(Component.text(" health. Grants 3 stacks of Merciful Hex at maximum charge. The crystal of healing has a lifespan of "))
                               .append(Component.text(format(lifeSpan), NamedTextColor.GOLD))
                               .append(Component.text(" seconds after its completion."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Block targetBlock = !(wp.getEntity() instanceof Player) ? LocationUtils.getGroundLocation(wp.getLocation()).getBlock() : Utils.getTargetBlock(wp, 15);
        if (targetBlock.getType() == Material.AIR) {
            return false;
        }

        Location groundLocation = targetBlock.getLocation().clone();
        groundLocation.add(.5, 1, .5);
        double baseY = groundLocation.getY();
        AtomicBoolean isCharged = new AtomicBoolean(false);

        Utils.playGlobalSound(wp.getLocation(), "arcanist.crystalofhealing.activation", 2, 0.85f);
        EffectUtils.playParticleLinkAnimation(wp.getLocation(), groundLocation, 0, 200, 0, 1);

        CircleEffect teamCircleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                groundLocation,
                RADIUS,
                new CircumferenceEffect(Particle.WAX_OFF, Particle.REDSTONE)
        );

        EffectUtils.playFirework(
                groundLocation,
                FireworkEffect.builder()
                              .withColor(Color.LIME)
                              .with(FireworkEffect.Type.BALL)
                              .trail(true)
                              .build()
        );

        ArmorStand crystal = Utils.spawnArmorStand(groundLocation, armorStand -> {
            armorStand.setGravity(true);
            armorStand.customName(Component.text(60, NamedTextColor.GREEN));
            armorStand.setCustomNameVisible(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.LIME_STAINED_GLASS));
        });
        for (WarlordsEntity warlordsEntity : PlayerFilter.playingGame(wp.getGame()).enemiesOf(wp)) {
            if (warlordsEntity.getEntity() instanceof Player p) {
                PacketUtils.removeEntityForPlayer(p, crystal.getEntityId());
            }
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "CRYSTAL",
                CrystalOfHealing.class,
                new CrystalOfHealing(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    crystal.remove();
                },
                false,
                (duration + lifeSpan) * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        teamCircleEffect.playEffects();
                    }
                    if (ticksElapsed % 2 == 0) {
                        Location crystalLocation = crystal.getLocation();
                        crystalLocation.setY(Math.sin(ticksElapsed * Math.PI / 40) / 4 + baseY);
                        crystalLocation.setYaw(crystalLocation.getYaw() + 10);
                        crystal.teleport(crystalLocation);
                    }
                    if (ticksElapsed % 20 == 0) {
                        int secondsElapsed = ticksElapsed / 20;
                        if (secondsElapsed < duration) {
                            crystal.customName(Component.text(duration - secondsElapsed, NamedTextColor.RED));
                        } else {
                            crystal.customName(Component.text(lifeSpan - (secondsElapsed - duration), NamedTextColor.GREEN));
                            isCharged.set(true);
                        }
                        if (pveMasterUpgrade) {
                            for (WarlordsEntity allyTarget : PlayerFilter
                                    .entitiesAround(crystal.getLocation(), 6, 6, 6)
                                    .aliveTeammatesOf(wp)
                            ) {
                                allyTarget.addInstance(InstanceBuilder
                                        .healing()
                                        .ability(this)
                                        .source(wp)
                                        .value(50)
                                );
                            }
                        }

                        EffectUtils.playCircularEffectAround(
                                wp.getGame(),
                                crystal.getLocation(),
                                Particle.VILLAGER_HAPPY,
                                1,
                                1,
                                0.1,
                                8,
                                1,
                                3
                        );
                    }
                    if (ticksElapsed < 40) {
                        return; // prevent instant pickup
                    }
                    PlayerFilter.entitiesAround(groundLocation, RADIUS, RADIUS, RADIUS)
                                .teammatesOf(wp)
                                .closestFirst(groundLocation)
                                .first(teammate -> {
                                    teammate.playSound(teammate.getLocation(), "shaman.earthlivingweapon.impact", 1, 0.45f);
                                    if (isCharged.get()) {
                                        for (int i = 0; i < 3; i++) {
                                            MercifulHex.giveMercifulHex(wp, teammate);
                                        }
                                    }
                                    EffectUtils.playFirework(
                                            groundLocation,
                                            FireworkEffect.builder()
                                                          .withColor(Color.WHITE)
                                                          .with(FireworkEffect.Type.STAR)
                                                          .build(),
                                            1
                                    );
                                    cooldown.setTicksLeft(0);
                                    int secondsElapsed = ticksElapsed / 20;
                                    float healAmount = secondsElapsed >= duration ? maxHeal : (maxHeal * ticksElapsed) / (duration * 20);
                                    teammate.addInstance(InstanceBuilder
                                            .healing()
                                            .ability(this)
                                            .source(wp)
                                            .value(healAmount)
                                    );
                                });
                })
        ));

        return true;
    }

    public float getMaxHeal() {
        return maxHeal;
    }

    public void setMaxHeal(float maxHeal) {
        this.maxHeal = maxHeal;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(int lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
