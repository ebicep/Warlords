package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

@CommandAlias("spawntestdummy")
@CommandPermission("warlords.game.spawndummy")
public class SpawnTestDummyCommand extends BaseCommand {

    @Default
    @CommandCompletion("@gameteams @boolean")
    @Description("Spawns a test dummy on the specified team and true/false for whether it will take damage")
    public void spawnTestDummy(@Conditions("requireGame:withAddon=PRIVATE_GAME") WarlordsPlayer warlordsPlayer, @Values("@gameteams") Team team, @Values("@boolean") Boolean takeDamage) {
        Game game = warlordsPlayer.getGame();
        WarlordsEntity testDummy = game.addNPC(new WarlordsNPC(
                UUID.randomUUID(),
                "testdummy",
                Weapons.BLUDGEON,
                WarlordsNPC.spawnZombieNoAI(warlordsPlayer.getLocation(), null),
                game,
                team,
                Specializations.PYROMANCER
        ));
        //SKULL
        ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal());
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        playerSkull.setItemMeta(skullMeta);
        HeadUtils.PLAYER_HEADS.put(testDummy.getUuid(), CraftItemStack.asNMSCopy(playerSkull));

        testDummy.setTakeDamage(true);
        testDummy.setMaxHealth(1000000);
        testDummy.setHealth(1000000);
        testDummy.updateHealth();
        testDummy.setTakeDamage(takeDamage);
    }

}
