package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;
import java.util.UUID;

public class SpawnTestDummyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WarlordsEntity player = BaseCommand.requireWarlordsPlayer(sender);

        if (!sender.hasPermission("warlords.game.spawndummy")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (player == null) {
            return true;
        }
        if (!player.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            sender.sendMessage("§cDebug commands are disabled in public games!");
            return true;
        }
        if (args.length >= 1) {
            String teamString = args[0];
            Optional<Team> teamOpt = TeamMarker.getTeams(player.getGame()).stream().filter(e -> e.name().equalsIgnoreCase(teamString)).findAny();
            if (teamOpt.isPresent()) {
                Team team = teamOpt.get();


                Game game = player.getGame();
                WarlordsEntity testDummy = game.addNPC(new WarlordsNPC(
                        UUID.randomUUID(),
                        "testdummy",
                        Weapons.BLUDGEON,
                        WarlordsNPC.spawnZombieNoAI(player.getLocation(), null),
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
                if (args.length >= 2 && args[1].equalsIgnoreCase("false")) {
                    testDummy.setTakeDamage(false);
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("true")) {
                    testDummy.setTakeDamage(true);
                } else {
                    sender.sendMessage("§cInvalid arguments! Valid arguments: [true, false]");
                }
            } else {
                sender.sendMessage("§cUnable to find team named " + teamString + ", valid options: " + TeamMarker.getTeams(player.getGame()));
                return true;
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("spawntestdummy").setExecutor(this);
    }
}
