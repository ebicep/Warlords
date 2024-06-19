package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ZombieKnight extends AbstractMob implements AdvancedMob {

    public ZombieKnight(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Knight",
                7000,
                0.3f,
                10,
                1000,
                1300,
                new ReduceWeaponCooldowns()
        );
    }

    public ZombieKnight(
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
                maxMeleeDamage,
                new ReduceWeaponCooldowns()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZOMBIE_KNIGHT;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    private static class ReduceWeaponCooldowns extends AbstractAbility {

        public ReduceWeaponCooldowns() {
            super("Reduce Weapon", 6, 100);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {


            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 5, 5, 5)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
            ) {
                EffectUtils.playParticleLinkAnimation(we.getLocation(), wp.getLocation(), 0, 0, 0, 1);
                we.getCooldownManager().subtractTicksOnRegularCooldowns(20, CooldownTypes.WEAPON);
            }

            EffectUtils.playFirework(wp.getLocation(), FireworkEffect.builder()
                                                                              .withColor(Color.BLACK)
                                                                              .with(FireworkEffect.Type.BALL_LARGE)
                                                                              .build());
            return true;
        }
    }
}
