package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

public class CustomIronGolem extends EntityIronGolem implements CustomEntity<CustomIronGolem> {

    public CustomIronGolem(World world) {
        super(world);
        resetAI(world);
        giveBaseAI(1.0, 0.6);
    }

    @Override
    public void onDeath(CustomIronGolem entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.IRONGOLEM_DEATH, 2, 0.4f);
    }

    @Override
    public CustomIronGolem get() {
        return this;
    }
}
