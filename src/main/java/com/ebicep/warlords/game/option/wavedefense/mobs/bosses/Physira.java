package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Physira extends AbstractZombie implements BossMob {

    public Physira(Location spawnLocation) {
        super(spawnLocation,
                "Physira",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getPlayerSkull("ChybaWonsz"),
                        new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        new ItemStack(Material.BLAZE_POWDER)
                ),
                18000,
                0.45f,
                0,
                600,
                800
        );
    }

    @Override
    public void onSpawn() {

    }

    @Override
    public void whileAlive() {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }

}
