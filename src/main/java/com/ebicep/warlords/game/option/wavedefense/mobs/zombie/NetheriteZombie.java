package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.PacketListener;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;

public class NetheriteZombie extends AbstractZombie implements BasicMob {

    public NetheriteZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Void Lancer",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.NETHERITE_HELMET),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
                        Weapons.GEMINI.getItem()
                ),
                7000,
                0.3f,
                10,
                2700,
                3300
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 40 == 0) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveEnemiesOf(warlordsNPC)
                    .closestFirst(warlordsNPC)
            ) {
                EffectUtils.playParticleLinkAnimation(we.getLocation(), warlordsNPC.getLocation(), 0, 0, 0, 1);
                we.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.BUFF, 40);
            }

            FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                    .withColor(Color.BLACK)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withTrail()
                    .build());
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
