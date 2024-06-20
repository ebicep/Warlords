package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class EventDjet extends AbstractMob implements BossMinionMob {

    private boolean wentBelowHealthThreshold = false;

    public EventDjet(Location spawnLocation) {
        super(spawnLocation,
                "Djet",
                9000,
                0.32f,
                10,
                930,
                1210,
                new FlameBurst() {{
                    this.getDamageValues().getFlameBurstDamage().min().setBaseValue(1200);
                    this.getDamageValues().getFlameBurstDamage().max().setBaseValue(1380);
                }},
                new SilenceCrippleAll()
        );
    }

    public EventDjet(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new FlameBurst() {{
                    this.getDamageValues().getFlameBurstDamage().min().setBaseValue(1200);
                    this.getDamageValues().getFlameBurstDamage().max().setBaseValue(1380);
                }},
                new SilenceCrippleAll()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_NARMER_DJET;
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
        if (!aboveHealthThreshold() && !wentBelowHealthThreshold) {
            wentBelowHealthThreshold = true;
            playerClass.getAbilities().get(0).getCooldown().addAdditiveModifier("Djet Health Threshold", 1000000f);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (aboveHealthThreshold()) {
            warlordsNPC.setDamageResistance(10);
        } else {
            warlordsNPC.setDamageResistance(30);
        }
    }

    private boolean aboveHealthThreshold() {
        return warlordsNPC.getCurrentHealth() > warlordsNPC.getMaxBaseHealth() * .75;
    }

    private static class SilenceCrippleAll extends AbstractPveAbility {

        public SilenceCrippleAll() {
            super("Djet", 5, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {


            for (WarlordsPlayer warlordsPlayer : PlayerFilterGeneric
                    .playingGameWarlordsPlayers(wp.getGame())
                    .aliveEnemiesOf(wp)
            ) {
                SoulShackle.shacklePlayer(warlordsPlayer, warlordsPlayer, 60);
                CripplingStrike.cripple(wp, warlordsPlayer, name, 3 * 20);
            }
            return true;
        }

    }
}
