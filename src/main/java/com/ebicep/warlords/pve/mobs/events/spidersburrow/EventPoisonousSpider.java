package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.flags.Spider;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.List;

public class EventPoisonousSpider extends AbstractMob implements BossMinionMob, Spider {

    public EventPoisonousSpider(Location spawnLocation) {
        super(
                spawnLocation,
                "Poisonous Spider",
                4000,
                .55f,
                0,
                750,
                850,
                new PoisonNear()
        );
    }

    public EventPoisonousSpider(
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
                new PoisonNear()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_POISONOUS_SPIDER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
        }
    }

    private static class PoisonNear extends AbstractPveAbility implements Damages<PoisonNear.DamageValues> {

        public PoisonNear() {
            super("Poison", 375, 500, 3, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            PlayerFilterGeneric.playingGame(pveOption.getGame())
                               .enemiesOf(wp)
                               .forEach(warlordsEntity -> {
                                   warlordsEntity.addInstance(InstanceBuilder
                                           .damage()
                                           .ability(this)
                                           .source(wp)
                                           .value(damageValues.poisonDamage)
                                   );
                               });
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue poisonDamage = new Value.RangedValue(224, 377);
            private final List<Value> values = List.of(poisonDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }
}
