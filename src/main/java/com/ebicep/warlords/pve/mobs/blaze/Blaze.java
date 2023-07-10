package com.ebicep.warlords.pve.mobs.blaze;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class Blaze extends AbstractBlaze implements EliteMob {

    private static final double kindleRadius = 6;

    public Blaze(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Kindle",
                MobTier.ELITE,
                null,
                4000,
                0,
                10,
                100,
                200,
                new KindleWave()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, Particle.FLAME, 1, 20);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2, 0.5f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 2, 0.2f);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, Particle.FLAME, 1, 10);
    }

    private static class KindleWave extends AbstractAbility {

        public KindleWave() {
            super("Kindle Wave", 518, 805, 8, 100);
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
            Location loc = wp.getLocation();
            EffectUtils.playSphereAnimation(loc, kindleRadius, Particle.FLAME, 1);
            Utils.playGlobalSound(loc, "mage.inferno.activation", 2, 0.2f);
            new FallingBlockWaveEffect(
                    loc,
                    kindleRadius,
                    1.2,
                    Material.FIRE
            ).play();

            for (WarlordsEntity target : PlayerFilter
                    .entitiesAround(wp, kindleRadius, kindleRadius, kindleRadius)
                    .aliveEnemiesOf(wp)
            ) {
                target.addDamageInstance(
                        wp,
                        "Kindle Wave",
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier
                );
            }
            return true;
        }
    }
}
