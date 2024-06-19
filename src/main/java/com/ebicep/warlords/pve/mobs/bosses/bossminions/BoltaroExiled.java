package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BoltaroExiled extends AbstractMob implements BossMinionMob {

    public BoltaroExiled(Location spawnLocation) {
        this(spawnLocation, "Exiled Apostate", 4000, 0.3f, 5, 200, 250);
    }

    public BoltaroExiled(
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
                new Fireball(200, 400, MathUtils.generateRandomValueBetweenInclusive(6, 12))
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BOLTARO_EXLIED;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        if (option.getDifficulty() == DifficultyIndex.EXTREME) {
            float newHealth = 2000;
            warlordsNPC.setMaxHealthAndHeal(newHealth);
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

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 1.3f);
    }
}
