package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BoltaroExiled extends AbstractZombie implements EliteMob {

    public BoltaroExiled(Location spawnLocation) {
        super(spawnLocation,
                "Exiled Apostate",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.RED_EYE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Weapons.GEMINI.getItem()
                ),
                4000,
                0.3f,
                0,
                200,
                250
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        if (option.getDifficulty() == DifficultyIndex.EXTREME) {
            float newHealth = 2000;
            warlordsNPC.setMaxBaseHealth(newHealth);
            warlordsNPC.setHealth(newHealth);
            warlordsNPC.setMaxHealth(newHealth);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 1.3f);
    }
}
