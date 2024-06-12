package com.ebicep.warlords.pve.mobs.events.boltarobonanza;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.concurrent.ThreadLocalRandom;

public class EventBoltaroShadow extends AbstractMob implements BossMinionMob {

    private boolean forceSplit = false;
    private int split;

    public EventBoltaroShadow(Location spawnLocation, int split) {
        this(spawnLocation,
                "Shadow Boltaro",
                6000,
                0.42f,
                10,
                200,
                400
        );
        this.split = split;
    }

    public EventBoltaroShadow(Location spawnLocation) {
        this(spawnLocation, 0);
    }

    public EventBoltaroShadow(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            int split
    ) {
        super(spawnLocation,
                name,
                (int) (maxHealth * (1 + split * .025)),
                walkSpeed,
                damageResistance,
                minMeleeDamage * (1 + split * .025f),
                maxMeleeDamage * (1 + split * .025f),
                new Fireball(100, 200, MathUtils.generateRandomValueBetweenInclusive(4, 8))
        );
        this.split = split;
    }

    public EventBoltaroShadow(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        this(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, 0);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_BOLTARO_SHADOW;
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
        Utils.addKnockback(name, attacker.getLocation(), receiver, -1.1, 0.26);
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
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 0.5f);

        int nextSplit = split + 1;
        option.spawnNewMob(new EventBoltaroShadow(warlordsNPC.getLocation(), nextSplit));
        if (forceSplit || ThreadLocalRandom.current().nextDouble(0, 1) < (1.0 / nextSplit)) {
            option.spawnNewMob(new EventBoltaroShadow(warlordsNPC.getLocation(), nextSplit));
        }
    }

    public int getSplit() {
        return split;
    }

    @Override
    public double weaponDropRate() {
        return 2;
    }
}
