package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class AerialAegis extends SpecialDeltaBuckler {

    public AerialAegis(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public AerialAegis() {

    }

    @Override
    public String getName() {
        return "Aerial Aegis";
    }

    @Override
    public String getBonus() {
        return "+1 Block Jump Height";
    }

    @Override
    public String getDescription() {
        return "It's floating?! How?!";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        new GameRunnable(warlordsPlayer.getGame()) {

            @Override
            public void run() {
                warlordsPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 1, true, false));
            }
        }.runTaskTimer(0, 100);
    }

}
