package com.ebicep.warlords.sr;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;

import java.util.*;

@CommandAlias("balancer")
@CommandPermission("group.administrator")
public class BalancerCommand extends BaseCommand {

    @Subcommand("test")
    @Description("Test")
    public void test(CommandIssuer issuer) {
        List<Specializations> specs = new ArrayList<>();
        specs.add(Specializations.PYROMANCER);
        specs.add(Specializations.PYROMANCER);
        specs.add(Specializations.CRUSADER);
        specs.add(Specializations.CRUSADER);
        specs.add(Specializations.VINDICATOR);
        specs.add(Specializations.SPIRITGUARD);
        specs.add(Specializations.AQUAMANCER);
        specs.add(Specializations.PYROMANCER);
        specs.add(Specializations.PYROMANCER);
        specs.add(Specializations.EARTHWARDEN);
        Set<UUID> players = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            UUID uuid = UUID.randomUUID();
            DatabasePlayer databasePlayer = new DatabasePlayer(uuid, "PLAYER " + i);
            databasePlayer.setLastSpec(specs.remove(0));
            DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).put(uuid, databasePlayer);
            ChatUtils.MessageType.WARLORDS.sendMessage(uuid + " - " + databasePlayer.getLastSpec());
            players.add(uuid);
        }
        Balancer balancer = new Balancer(players, EnumSet.of(Team.RED, Team.BLUE));
        balancer.balance(false);
        balancer.printDebugInfo();
    }


}
