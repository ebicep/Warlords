package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity<CustomMagmaCube> {

    public CustomMagmaCube(World world) {
        super(world);
        setSize(7);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = (0.05F + (float) this.getSize() * 0.05F); //motion y
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
                    "Baby Magma Cube",
                    Weapons.ABBADON,
                    (LivingEntity) babyMagmaCube.getBukkitEntity(),
                    waveDefenseOption.getGame(),
                    Team.RED,
                    Specializations.AQUAMANCER,
                    2000,
                    0.5f,
                    0,
                    50,
                    100
            );
            waveDefenseOption.getEntities().add(entity);
            waveDefenseOption.getGame().addNPC(entity);
        }
    }

    @Override
    public CustomMagmaCube get() {
        return this;
    }

}
