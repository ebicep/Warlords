package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.*;
import static com.ebicep.warlords.player.Specializations.PYROMANCER;
import com.ebicep.warlords.util.warlords.GameRunnable;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;

public class DummySpawnOption implements Option {

    private final Location loc;
    private final Team team;
    private final String name;

    public DummySpawnOption(Location loc, Team team) {
        this(loc, team, team == Team.RED ? "TestDummy1" : "TestDummy2");
    }

    public DummySpawnOption(Location loc, Team team, String name) {
        this.loc = loc;
        this.team = team;
        this.name = name;
    }

    @Override
    public void start(@Nonnull Game game) {
        // Delay spawn by 5 seconds to avoid Null reference in PlayingState
        new GameRunnable(game) {
            @Override
            public void run() {
                if (getGame().getState() instanceof EndState) {
                    System.out.print(ChatColor.RED + "[DEBUG] CAUGHT INVALID DUMMY SPAWN - game was ended before initial spawn.");
                    return;
                }

                WarlordsEntity testDummy = game.addNPC(new WarlordsNPC(
                        UUID.randomUUID(),
                        name,
                        Weapons.ABBADON,
                        WarlordsNPC.spawnZombie(loc, null),
                        game,
                        team,
                        Specializations.PYROMANCER
                ));
                //SKULL
                ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal());
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                playerSkull.setItemMeta(skullMeta);
                Warlords.getPlayerHeads().put(testDummy.getUuid(), CraftItemStack.asNMSCopy(playerSkull));

                testDummy.teleport(loc);
                testDummy.setTakeDamage(true);
                testDummy.setMaxHealth(1000000);
                testDummy.setHealth(1000000);
                testDummy.updateHealth();
            }
        }.runTaskLater(100);
    }
}
