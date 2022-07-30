package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity<CustomMagmaCube> {

    private final int flameHitbox = 6;

    public CustomMagmaCube(World world) {
        super(world);
        setSize(7);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = (0.07F + (float) this.getSize() * 0.07F); //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    protected void bH() {

    }

    @Override
    public void onDeath(CustomMagmaCube customMagmaCube, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        if (customMagmaCube.getSize() <= 6) return;
        for (int i = 0; i < 2; i++) {
            CustomMagmaCube babyMagmaCube = new CustomMagmaCube(((CraftWorld) deathLocation.getWorld()).getHandle());
            babyMagmaCube.setSize(customMagmaCube.getSize() - 1);
            babyMagmaCube.spawn(deathLocation);
            WarlordsNPC entity = new WarlordsNPC(
                    UUID.randomUUID(),
                    "Illusion Illuminati",
                    Weapons.ABBADON,
                    (LivingEntity) babyMagmaCube.getBukkitEntity(),
                    waveDefenseOption.getGame(),
                    Team.RED,
                    Specializations.BERSERKER,
                    2500,
                    0.5f,
                    0,
                    50,
                    100
            );
            waveDefenseOption.getEntities().add(entity);
            waveDefenseOption.getGame().addNPC(entity);
        }

        WarlordsEntity we = Warlords.getPlayer(this.getBukkitEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, flameHitbox, flameHitbox, flameHitbox)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addDamageInstance(we, "Blight", 124, 332, -1, 100, false);
            }
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.playHelixAnimation(deathLocation, flameHitbox, 255, 40, 40);
        Utils.playGlobalSound(deathLocation, Sound.ENDERMAN_SCREAM, 1, 2);
    }

    @Override
    public CustomMagmaCube get() {
        return this;
    }

}
