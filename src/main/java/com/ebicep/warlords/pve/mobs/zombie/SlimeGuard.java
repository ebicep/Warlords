package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;
import org.bukkit.Sound;

public class SlimeGuard extends AbstractMob implements AdvancedMob {

    public SlimeGuard(Location spawnLocation) {
        super(
                spawnLocation,
                "Slime Guard",
                6000,
                0.39f,
                10,
                500,
                700
        );
    }

    public SlimeGuard(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SLIME_GUARD;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_SLIME_JUMP, 500, 0.2f);
        receiver.addSpeedModifier(warlordsNPC, "Slime Slowness", -30, 2 * 20);
    }

}
