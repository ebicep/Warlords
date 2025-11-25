package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

public class EchoOfBlades extends AbstractMob implements BossMinionMob {

    private Location mapCenter;

    public EchoOfBlades(Location spawnLocation) {
        super(
                spawnLocation,
                "Echo of Blades",
                7000,
                0.25f,
                10,
                1200,
                1500
        );
    }

    public EchoOfBlades(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5);

        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.GRAY)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 40 == 0) {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                    .filter(we -> we.getName().equals("One of Nine"))
                    .forEach(nine -> {
                        nine.addInstance(InstanceBuilder
                                .healing()
                                .cause("Healing")
                                .source(warlordsNPC)
                                .value(50)
                        );
                        nine.getCooldownManager().removeCooldown(EchoOfBlades.class, false);
                        nine.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Protection",
                                null,
                                EchoOfBlades.class,
                                null,
                                warlordsNPC,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {},
                                41
                        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, 0.1f);
                                }
                        ));

                        Utils.playGlobalSound(nine.getLocation(), "shaman.earthlivingweapon.impact", 20, 1.5f);
                        EffectUtils.playCylinderAnimation(nine.getLocation(), 2, Particle.FIREWORK, 1);
                        EffectUtils.playParticleLinkAnimation(nine.getLocation(), warlordsNPC.getLocation(), Particle.SCULK_SOUL);
                    });
        }
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ECHO_OF_BLADES;
    }
}
