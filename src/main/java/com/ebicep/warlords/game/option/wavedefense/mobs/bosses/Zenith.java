package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Zenith extends AbstractZombie implements BossMob {

    public Zenith(Location spawnLocation) {
        super(spawnLocation,
                "Zenith",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.PURPLE_ENDERMAN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
                        new ItemStack(Material.DIAMOND_SPADE)
                ),
                27000,
                0.35f,
                20,
                1200,
                2400
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 6);
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.DARK_PURPLE + getWarlordsNPC().getName(),
                        ChatColor.LIGHT_PURPLE + "Leader of the Illusion Vanguard",
                        20, 40, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        Location loc = warlordsNPC.getLocation();
        if (ticksElapsed % 200 == 0) {
            EffectUtils.strikeLightningInCylinder(loc, 8, false, 10, warlordsNPC.getGame());
            shockwave(loc, 8, 10);
            EffectUtils.strikeLightningInCylinder(loc, 16, false, 15, warlordsNPC.getGame());
            shockwave(loc, 15, 15);
            EffectUtils.strikeLightningInCylinder(loc, 24, false, 20, warlordsNPC.getGame());
            shockwave(loc, 20, 20);
        }

        if (ticksElapsed % 40 == 0) {
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 1.5f, 2);
            EffectUtils.playSphereAnimation(loc, 4, ParticleEffect.SPELL_WITCH, 2);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
        Utils.addKnockback(attacker.getLocation(), receiver, -1, 0.3);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private void shockwave(Location loc, double radius, int tickDelay) {
        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                Utils.playGlobalSound(loc, Sound.ENDERDRAGON_GROWL, 10, 0.4f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 10, 0.4f);
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(loc, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    we.addDamageInstance(warlordsNPC, "Armageddon", 600, 900, -1, 100, false);
                    Utils.addKnockback(warlordsNPC.getLocation(), we, -3, 0.2);
                }
            }
        }.runTaskLater(tickDelay);
    }
}
