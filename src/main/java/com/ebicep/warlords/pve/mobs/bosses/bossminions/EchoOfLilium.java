package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.Lilium;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Boss;

public class EchoOfLilium extends AbstractMob implements BossMinionMob {

    public EchoOfLilium(Location spawnLocation) {
        super(
                spawnLocation,
                "Echo of Lilium",
                50000,
                0.36f,
                20,
                450,
                650
        );
    }

    public EchoOfLilium(
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
        return Mob.ECHO_OF_LILIUM;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 400 == 0 && ticksElapsed > 0) {
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;
                    Lilium.bladeWaltsAbility(warlordsNPC);
                    if (t == 4) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 7);
        }

        if (ticksElapsed % 200 == 0) {
            PlayerFilter.entitiesAround(warlordsNPC, 10, 10, 10)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(player -> {
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 5, 0.7f);
                        Utils.addKnockback(name, warlordsNPC.getLocation(), player, -1.5, 0.3);
                        player.addInstance(InstanceBuilder
                                .damage()
                                .cause("Echo of Cuts")
                                .source(warlordsNPC)
                                .min(1000)
                                .max(1500)
                                .flags(InstanceFlags.TRUE_DAMAGE));
                    });
        }
    }
}
