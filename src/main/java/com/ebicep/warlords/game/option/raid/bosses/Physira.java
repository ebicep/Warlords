package com.ebicep.warlords.game.option.raid.bosses;

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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Physira extends AbstractZombie implements BossMob {

    public Physira(Location spawnLocation) {
        super(spawnLocation,
                "Physira",
                MobTier.RAID_BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON_KING),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 60, 60),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 60, 60),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 60, 60),
                        Weapons.SILVER_PHANTASM_STAFF_2.getItem()
                ),
                200000,
                0.15f,
                30,
                3000,
                4000
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        Location loc = warlordsNPC.getLocation();
        float health = warlordsNPC.getMaxHealth();
        float phaseOneHealth = health * .8f;
        float phaseTwoHealth = health * .6f;
        float phaseThreeHealth = health * .4f;
        float phaseFourHealth = health * .2f;

        int shieldHealth = (int) (4000 * option.getGame().warlordsPlayers().count());

        if (warlordsNPC.getHealth() > phaseOneHealth) {
            // Deal (4000 x PLAYER COUNT) damage in 10 seconds to break shield, if not broken deal 90% of all players' max health as damage (bypasses damage reduction.)
        } else if (warlordsNPC.getHealth() > phaseTwoHealth) {
            Bukkit.broadcastMessage("phase 2");
        } else if (warlordsNPC.getHealth() > phaseThreeHealth) {
            Bukkit.broadcastMessage("phase 3");
            // Knockback waves, Lock on players with the most health (if a player dies heal 10% current hp.)
        } else if (warlordsNPC.getHealth() > phaseFourHealth) {
            Bukkit.broadcastMessage("phase 4");
            // Knockback waves, Lock on players with the most health (if a player dies heal 10% current hp.)
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
