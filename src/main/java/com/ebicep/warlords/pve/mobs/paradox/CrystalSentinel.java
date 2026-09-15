package com.ebicep.warlords.pve.mobs.paradox;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class CrystalSentinel extends AbstractMob implements IntermediateMob {

    public CrystalSentinel(Location spawnLocation) {
        super(
                spawnLocation,
                "Crystal Sentinel",
                5500,
                0.26f,
                20,
                400,
                600
        );
    }

    public CrystalSentinel(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CRYSTAL_SENTINEL;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 2, 0.5f);
        receiver.addSpeedModifier(attacker, "Crystal Weight", -15, 2 * 20);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_GLASS_BREAK, 2, 0.4f);
    }
}
