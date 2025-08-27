package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.OrbitingSwords;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EchoOfBlades extends AbstractMob implements BossMinionMob {

    public EchoOfBlades(Location spawnLocation) {
        super(
                spawnLocation,
                "Echo of Blades",
                15000,
                0.25f,
                10,
                1200,
                1500
        );
    }

    public EchoOfBlades(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage, AbstractAbility... abilities) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public void onSpawn(PveOption option) {
        new OrbitingSwords(warlordsNPC.getLocation(), warlordsNPC, 3, 2, 2, 4);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ECHO_OF_BLADES;
    }
}
