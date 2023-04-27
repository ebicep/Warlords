package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.UUID;

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
                    ChatUtils.MessageTypes.WARLORDS.sendErrorMessage("[DEBUG] CAUGHT INVALID DUMMY SPAWN - game was ended before initial spawn.");
                    return;
                }

                WarlordsEntity testDummy = game.addNPC(new WarlordsNPC(
                        UUID.randomUUID(),
                        name,
                        Weapons.ABBADON,
                        WarlordsNPC.spawnZombieNoAI(loc, null),
                        game,
                        team,
                        Specializations.PYROMANCER
                ));
                //SKULL
                ItemStack playerSkull = new ItemStack(Material.ZOMBIE_HEAD);
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                playerSkull.setItemMeta(skullMeta);
                HeadUtils.PLAYER_HEADS.put(testDummy.getUuid(), CraftItemStack.asNMSCopy(playerSkull));

                testDummy.teleport(loc);
                testDummy.setTakeDamage(true);
                testDummy.setMaxBaseHealth(1000000);
                testDummy.setHealth(1000000);
                testDummy.updateHealth();
            }
        }.runTaskLater(100);
    }
}