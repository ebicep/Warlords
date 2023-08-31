package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.abilities.AdvancedVoidShred;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class VoidZombie extends AbstractZombie implements EliteMob {

    private static final int voidRadius = 4;

    public VoidZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Singularity",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        Weapons.VOID_EDGE.getItem()
                ),
                11000,
                0.1f,
                0,
                1500,
                2000,
                new VoidShred(),
                new AdvancedVoidShred(200, 300, .5f, -70, voidRadius, 20)
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 8 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    voidRadius,
                    new CircumferenceEffect(Particle.FIREWORKS_SPARK, Particle.FIREWORKS_SPARK).particlesPerCircumference(0.6),
                    new DoubleLineEffect(Particle.SPELL)
            ).playEffects();
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
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(
                deathLocation,
                FireworkEffect.builder()
                              .withColor(Color.WHITE)
                              .with(FireworkEffect.Type.BURST)
                              .withTrail()
                              .build(),
                1
        );
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ZOMBIE_DEATH, 2, 0.4f);
    }

    private static class VoidShred extends AbstractAbility {

        public VoidShred() {
            super("Void Shred", 2, 100);
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

            float healthDamage = wp.getMaxHealth() * 0.01f;
            wp.addDamageInstance(wp, "Void Shred", healthDamage, healthDamage, critChance, critMultiplier);
            return true;
        }
    }
}
