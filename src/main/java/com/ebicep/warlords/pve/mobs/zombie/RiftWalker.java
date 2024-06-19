package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class RiftWalker extends AbstractMob implements EliteMob {

    public RiftWalker(Location spawnLocation) {
        super(
                spawnLocation,
                "Rift Walker",
                9000,
                0.33f,
                10,
                800,
                1000,
                new RiftSpeed()
        );
    }

    public RiftWalker(
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
                new RiftSpeed()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.RIFT_WALKER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getSpeed().removeSlownessModifiers();
        if (ticksElapsed % 40 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 2, 0.2f);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, 1, 0.15);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        self.addSpeedModifier(self, "Rift Speed On Damage", 40, 5);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.PURPLE)
                                                                       .with(FireworkEffect.Type.BURST)
                                                                       .withTrail()
                                                                       .build());
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ZOMBIE_DEATH, 2, 0.4f);
    }

    private static class RiftSpeed extends AbstractAbility {

        public RiftSpeed() {
            super("Rift Speed", 10, 100);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {


            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.2f);
            wp.addSpeedModifier(wp, "Rift Speed", 80, 2 * 20);
            return true;
        }
    }
}
