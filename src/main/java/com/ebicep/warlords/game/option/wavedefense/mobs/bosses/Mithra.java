package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Mithra extends AbstractZombie implements BossMob {

    public Mithra(Location spawnLocation) {
        super(spawnLocation,
                "Mithra",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                20000,
                0.2f,
                20,
                1000,
                1300
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.LIGHT_PURPLE + "Mithra",
                        ChatColor.WHITE + "The Envoy Queen of Illusion",
                        20, 30, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 100 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    8,
                    new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE).particlesPerCircumference(2.5)
            ).playEffects();
            for (WarlordsEntity swapTarget : PlayerFilter
                    .entitiesAround(warlordsNPC, 8, 8, 8)
                    .aliveEnemiesOf(warlordsNPC)
                    .lookingAtFirst(warlordsNPC)
            ) {
                Location swapLocation = swapTarget.getLocation();
                Location ownLocation = warlordsNPC.getLocation();
                EffectUtils.playCylinderAnimation(swapLocation, 1.05, ParticleEffect.CLOUD, 1);
                EffectUtils.playCylinderAnimation(ownLocation, 1.05, ParticleEffect.CLOUD, 1);
                swapTarget.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false));
                swapTarget.teleport(new Location(
                        ownLocation.getWorld(),
                        ownLocation.getX(),
                        ownLocation.getY(),
                        ownLocation.getZ(),
                        ownLocation.getYaw(),
                        ownLocation.getPitch())
                );

                warlordsNPC.teleport(new Location(
                        swapLocation.getWorld(),
                        swapLocation.getX(),
                        swapLocation.getY(),
                        swapLocation.getZ(),
                        swapLocation.getYaw(),
                        swapLocation.getPitch())
                );
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
