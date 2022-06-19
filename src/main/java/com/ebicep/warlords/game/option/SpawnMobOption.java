package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.Specializations;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.WarlordsNPC;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SpawnMobOption implements Option {

    private final Location spawnPoint;

    public SpawnMobOption(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public void start(@Nonnull Game game) {

        new GameRunnable(game) {
            @Override
            public void run() {
                WarlordsEntity testDummy = game.addNPC(new WarlordsNPC(
                        UUID.randomUUID(),
                        "Test",
                        Weapons.ABBADON,
                        WarlordsNPC.spawnEntity(Skeleton.class, spawnPoint, new Utils.SimpleEntityEquipment(
                                new ItemStack(Material.BARRIER),
                                new ItemStack(Material.DIAMOND_CHESTPLATE),
                                new ItemStack(Material.DIAMOND_LEGGINGS),
                                new ItemStack(Material.DIAMOND_BOOTS),
                                new ItemStack(Material.BOW))),
                        (PlayingState) game.getState(),
                        Team.RED,
                        Specializations.PYROMANCER
                ));
            }
        }.runTaskTimer(100, 50);
    }
}
