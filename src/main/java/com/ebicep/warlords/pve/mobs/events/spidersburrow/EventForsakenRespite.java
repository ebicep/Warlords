package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.flags.Spider;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class EventForsakenRespite extends AbstractMob implements BossMinionMob, Spider {


    public EventForsakenRespite(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                2700,
                0.45f,
                0,
                300,
                450,
                new LeechAll()
        );
    }

    public EventForsakenRespite(
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
                new LeechAll()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_FORSAKEN_RESPITE;
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

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private static class LeechAll extends AbstractPveAbility {

        public LeechAll() {
            super("Leech All", 7, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            PlayerFilterGeneric.playingGameWarlordsPlayers(pveOption.getGame())
                               .enemiesOf(wp)
                               .forEach(warlordsPlayer ->
                                       ImpalingStrike.giveLeechCooldown(
                                               wp,
                                               warlordsPlayer,
                                               3,
                                               .25f,
                                               .15f,
                                               warlordsDamageHealingFinalEvent -> {

                                               }
                                       ));
            return true;
        }

    }
}
