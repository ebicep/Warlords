package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.Collections;

public class ExiledSkeleton extends AbstractSkeleton implements EliteMob {
    public ExiledSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Sorcerer",
                MobTier.ELITE,
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
                1000
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
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
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        warlordsNPC.getSpeed().removeSlownessModifiers();
        if (ticksElapsed % 80 == 0) {
            EffectUtils.playSphereAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.FLAME, 1);
            for (WarlordsEntity wp : PlayerFilter
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                wp.getCooldownManager().removeCooldown(Fireball.class, false);
                wp.getCooldownManager().addCooldown(new RegularCooldown<Fireball>(
                        name,
                        "BLI",
                        Fireball.class,
                        new Fireball(),
                        warlordsNPC,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        4 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                            if (ticksLeft % 20 == 0) {
                                float healthDamage = wp.getMaxHealth() * 0.05f;
                                wp.addDamageInstance(
                                        warlordsNPC,
                                        "Blighted Scorch",
                                        healthDamage,
                                        healthDamage,
                                        0,
                                        100,
                                        false
                                );
                            }
                        })
                ));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.getCooldownManager().removeCooldown(WoundingStrikeBerserker.class, false);
        receiver.getCooldownManager().addCooldown(new RegularCooldown<WoundingStrikeBerserker>(
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
                        receiver.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
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
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.SKELETON_DEATH, 2, 0.4f);
    }
}
