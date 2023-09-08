package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EventPoisonousSpider extends AbstractZombie implements BossMinionMob, Spider {

    public EventPoisonousSpider(Location spawnLocation) {
        super(
                spawnLocation,
                "Poisonous Spider",
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        null
                ),
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
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        null
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new PoisonNear()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
            warlordsNPC.heal();
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private static class PoisonNear extends AbstractPveAbility {

        public PoisonNear() {
            super("Poison Near", 375, 500, 3, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            PlayerFilterGeneric.playingGame(pveOption.getGame())
                               .enemiesOf(wp)
                               .forEach(warlordsEntity -> warlordsEntity.addDamageInstance(
                                       wp,
                                       "Poison",
                                       minDamageHeal,
                                       maxDamageHeal,
                                       critChance,
                                       critMultiplier
                               ));
            return true;
        }

    }
}
