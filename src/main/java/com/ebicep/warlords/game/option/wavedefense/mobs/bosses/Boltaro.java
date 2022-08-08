package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.game.Team;
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
import org.bukkit.util.Vector;

import java.util.UUID;

public class Boltaro extends AbstractZombie implements BossMob {

    public Boltaro(Location spawnLocation) {
        super(spawnLocation,
                "Boltaro",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.END_MONSTER),
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.COOKED_FISH)
                ),
                13000,
                0.55f,
                20,
                350,
                500
        );
    }

    @Override
    public void onSpawn() {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.RED + getWarlordsNPC().getName(),
                        ChatColor.GOLD + "Right Hand of the Illusion Vanguard",
                        20, 40, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {
        Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(-1.25).setY(0.3);
        receiver.setVelocity(v, false);

        if (!ability.equals("Multi Hit")) {
            new GameRunnable(attacker.getGame()) {
                @Override
                public void run() {
                    Utils.playGlobalSound(receiver.getLocation(), Sound.ARROW_HIT, 2, 0.2f);
                    Vector v = attacker.getLocation().toVector().subtract(receiver.getLocation().toVector()).normalize().multiply(1).setY(0.7);
                    receiver.setVelocity(v, false);
                    receiver.addDamageInstance(attacker, "Multi Hit", minMeleeDamage, maxMeleeDamage, -1, 100, false);
                }
            }.runTaskLater(20);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {
        Vector v = mob.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(-1.1).setY(0.2);
        attacker.setVelocity(v, false);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        for (int i = 0; i < 2; i++) {
            Boltaro boltaro = new Boltaro(deathLocation);
            boltaro.toNPC(waveDefenseOption.getGame(), Team.RED, UUID.randomUUID());
            waveDefenseOption.getGame().addNPC(boltaro.getWarlordsNPC());
            waveDefenseOption.getMobs().add(boltaro);
        }
    }
}
