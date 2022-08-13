package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
                20000,
                0.465f,
                20,
                900,
                1100
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
                        20, 50, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 40 == 0) {
            Location loc = getWarlordsNPC().getLocation();
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 1.5f, 2);
            EffectUtils.playSphereAnimation(loc, 4, ParticleEffect.SPELL_WITCH, 2);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.35).setY(0.3);
        receiver.setVelocity(v, false);
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }
}
