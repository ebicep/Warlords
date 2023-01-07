package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.abilties.PrismGuard;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class VoidPigZombie extends AbstractPigZombie implements EliteMob {

    public VoidPigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Particle",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.HOODED_KNIGHT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
                        Weapons.NETHERSTEEL_KATANA.getItem()
                ),
                8000,
                0.2f,
                10,
                450,
                600
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        warlordsNPC.getSpec().setBlue(new PrismGuard());
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 10 == 0) {
            EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 6, ParticleEffect.CLOUD, 1);
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveTeammatesOfExcludingSelf(warlordsNPC)
            ) {
                we.addHealingInstance(
                        warlordsNPC,
                        "Void Healing",
                        200,
                        200,
                        -1,
                        100,
                        false,
                        false
                );
            }
        }

        if (ticksElapsed % 300 == 0) {
            warlordsNPC.getBlueAbility().onActivate(warlordsNPC, null);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
