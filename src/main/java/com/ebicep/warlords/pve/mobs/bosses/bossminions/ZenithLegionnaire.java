package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class ZenithLegionnaire extends AbstractZombie implements BossMinionMob {

    public ZenithLegionnaire(Location spawnLocation) {
        super(spawnLocation,
                "Zenith Legionnaire",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 100, 0, 80),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 100, 0, 80),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 100, 0, 80),
                        Weapons.LUNAR_JUSTICE.getItem()
                ),
                4400,
                0.32f,
                10,
                1000,
                1500,
                new Remedy()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, -1.1, 0.3);
        Utils.playGlobalSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 2, 0.2f);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.ORANGE)
                                                                       .with(FireworkEffect.Type.BALL)
                                                                       .withTrail()
                                                                       .build());
    }

    private static class Remedy extends AbstractPveAbility {

        public Remedy() {
            super(
                    "Remedy",
                    500,
                    500,
                    10,
                    100
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(energyCost, false);

            PlayerFilter.playingGame(wp.getGame())
                        .filter(we -> we.getName().equals("Zenith"))
                        .forEach(zenith -> {
                            zenith.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );

                            Utils.playGlobalSound(zenith.getLocation(), "shaman.earthlivingweapon.impact", 3, 1.5f);
                            EffectUtils.playParticleLinkAnimation(zenith.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY);
                        });
            if (wp instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().removeTarget();
            }

            return true;
        }
    }
}