package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulOfGradient;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;

import java.util.Collections;

public class Torment extends AbstractMob implements BossMob {

    public Torment(Location spawnLocation) {
        super(
                spawnLocation,
                "Torment",
                135000,
                0.39f,
                20,
                1600,
                2000
        );
    }

    public Torment(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("Torment", NamedTextColor.RED),
                Component.text("Corrupted Soul", NamedTextColor.WHITE)
        );

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
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getSource().getCooldownManager().hasCooldown(DamageCheck.class)) {
                    return currentDamageValue * 5;
                }

                return currentDamageValue * 0.25f;
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getSpeed().removeSlownessModifiers();
        if (ticksElapsed % 600 == 0) {
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }

                    EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 0.3 * counter, 255, 30, 30, counter, 4);
                    counter++;
                    if (counter == 40) {
                        for (int i = 0; i < option.playerCount(); i++) {
                            option.spawnNewMob(new SoulOfGradient(warlordsNPC.getLocation()));
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);

            for (WarlordsEntity we : PlayerFilterGeneric
                    .entitiesAround(warlordsNPC, 100, 100, 100)
                    .aliveEnemiesOf(warlordsNPC)
                    .leastAliveFirst()
                    .limit(1)
            ) {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.empty(),
                        Component.text(we.getName(), NamedTextColor.GOLD)
                                 .append(Component.text(" has been marked by Torment!", NamedTextColor.RED))
                );
                Utils.addKnockback(name, warlordsNPC.getLocation(), we, 2, 0.35);
                we.getCooldownManager().removeCooldown(DamageCheck.class, false);
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Tormenting Mark",
                        "MARK",
                        DamageCheck.class,
                        DamageCheck.DAMAGE_CHECK,
                        warlordsNPC,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        15 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                            if (ticksLeft % 10 == 0) {
                                EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), we.getLocation(), Particle.DRIP_LAVA);
                                EffectUtils.playSphereAnimation(we.getLocation(), 3, Particle.FLAME, 1);
                            }

                            if (ticksLeft % 5 == 0) {
                                for (WarlordsEntity ally : PlayerFilter
                                        .entitiesAround(we, 3.2, 3.2, 3.2)
                                        .aliveTeammatesOfExcludingSelf(we)
                                ) {
                                    ally.addInstance(InstanceBuilder
                                            .damage()
                                            .cause("Tormenting Mark")
                                            .source(warlordsNPC)
                                            .value(1000)
                                    );
                                }
                            }
                        })));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        EffectUtils.playFirework(receiver.getLocation(), FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL)
                .build());
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TORMENT;
    }

    @Override
    public Component getDescription() {
        return Component.text("Corrupted Soul", NamedTextColor.WHITE);
    }
}