package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class SkeletalSorcerer extends AbstractSkeleton implements ChampionMob {
    public SkeletalSorcerer(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Sorcerer",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.WHITE_SHEKEL),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
                        Weapons.SILVER_PHANTASM_SWORD_4.getItem()
                ),
                8000,
                0.3f,
                10,
                800,
                1000,
                new Fireball(5.5f), new BlightedScorch()
        );
    }

    public SkeletalSorcerer(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.WHITE_SHEKEL),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
                        Weapons.SILVER_PHANTASM_SWORD_4.getItem()
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Fireball(5.5f), new BlightedScorch()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                // immune to KB
                currentVector.multiply(0.05);
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getSpeed().removeSlownessModifiers();
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.getCooldownManager().removePreviousWounding();
        receiver.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "WND",
                WoundingStrikeBerserker.class,
                new WoundingStrikeBerserker(),
                attacker,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                        receiver.sendMessage(Component.text("You are no longer ", NamedTextColor.GRAY)
                                                      .append(Component.text("wounded", NamedTextColor.RED))
                                                      .append(Component.text("."))
                        );
                    }
                },
                5 * 20
        ) {
            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .5f;
            }
        });
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                           .withColor(Color.ORANGE)
                                                           .with(FireworkEffect.Type.BURST)
                                                           .withTrail()
                                                           .build());
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_SKELETON_DEATH, 2, 0.4f);
    }

    private static class BlightedScorch extends AbstractAbility {

        public BlightedScorch() {
            super("Blighted Scorch", 4, 100);
        }

        @Override
        public void updateDescription(Player player) {

        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
            wp.subtractEnergy(energyCost, false);

            EffectUtils.playSphereAnimation(wp.getLocation(), 6, Particle.FLAME, 1);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.getCooldownManager().removeCooldown(BlightedScorch.class, false);
                enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "BLI",
                        BlightedScorch.class,
                        new BlightedScorch(),
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        4 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                            if (ticksLeft % 20 == 0) {
                                float healthDamage = enemy.getMaxHealth() * 0.05f;
                                if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                }
                                if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                }
                                enemy.addDamageInstance(
                                        wp,
                                        name,
                                        healthDamage,
                                        healthDamage,
                                        critChance,
                                        critMultiplier
                                );
                            }
                        })
                ));
            }
            return true;
        }
    }
}
