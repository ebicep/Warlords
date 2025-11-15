package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class FrostVeil extends AbstractMob implements BossMinionMob {

    public FrostVeil(Location spawnLocation) {
        super(
                spawnLocation,
                "Frost Veil",
                2500,
                0.35f,
                10,
                500,
                800
        );
    }

    public FrostVeil(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2, 0.5f);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.FROST_VEIL;
    }
}
