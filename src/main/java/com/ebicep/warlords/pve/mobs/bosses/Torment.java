package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulOfGradient;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.witherskeleton.AbstractWitherSkeleton;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Collections;

public class Torment extends AbstractWitherSkeleton implements BossMob {

    public Torment(Location spawnLocation) {
        super(
                spawnLocation,
                "Torment",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON_KING),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 230, 60, 60),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 230, 60, 60),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 230, 60, 60),
                        Weapons.SILVER_PHANTASM_TRIDENT.getItem()
                ),
                135000,
                0.39f,
                20,
                1600,
                2000
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                ChatColor.RED + "Torment",
                ChatColor.WHITE + "Corrupted Soul"
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
                if (event.getAttacker().getCooldownManager().hasCooldown(DamageCheck.class)) {
                    return currentDamageValue * 4;
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
                    EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 0.2 * counter, 255, 30, 30, counter, 4);
                    counter++;
                    if (counter == 40) {
                        option.spawnNewMob(new SoulOfGradient(warlordsNPC.getLocation()));
                        this.cancel();
                    }

                    if (warlordsNPC.isDead()) {
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
                                    ally.addDamageInstance(
                                            warlordsNPC,
                                            "Tormenting Mark",
                                            1000,
                                            1000,
                                            -1,
                                            100,
                                            true
                                    );
                                }
                            }
                        })));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {

    }
}