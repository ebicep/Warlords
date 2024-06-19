package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SlimeSize;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class SlimyChess extends AbstractMob implements AdvancedMob {
    public SlimyChess(Location spawnLocation) {
        super(
                spawnLocation,
                "Slimy Chess",
                10000,
                0.1f,
                30,
                0,
                0,
                new Blob()
        );
    }

    public SlimyChess(
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
                new Blob()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SLIMY_CHESS;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void onNPCCreate() {
        npc.getOrAddTrait(SlimeSize.class).setSize(10);
        npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> .1f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        //attacker.getSpec().increaseAllCooldownTimersBy(1);
    }

    private static class Blob extends AbstractAbility {

        public Blob() {
            super("Blob", 1, 50);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {


            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 10, 10, 10)
                    .aliveEnemiesOf(wp)
            ) {
                we.subtractEnergy(name, 10, true);
            }
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 100, 100, 100)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
                    .limit(1)
            ) {
                EffectUtils.playParticleLinkAnimation(wp.getLocation(), we.getLocation(), Particle.DRIP_LAVA);
                we.subtractEnergy(name, 5, true);
                we.addSpeedModifier(wp, "Blob Slowness", -20, 20);
            }
            return true;
        }
    }
}
