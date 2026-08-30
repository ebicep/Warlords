package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class OldTestCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (commandSender instanceof Player player && !player.isOp()) {
            return;
        }
        Player playerSender = commandSender instanceof Player player ? player : null;

        if (commandSender instanceof Player player) {
            player.give(new ItemBuilder(Material.GRAY_DYE, 5).name(Component.text("")).get());
//            long balanceThreadId = BalanceThreadContext.getLatestBalanceThreadId();
//            DatabaseGameCTF mockGame = createMockBacklogTestGame();
//            if (balanceThreadId == 0 || BotManager.jda == null) {
//                player.sendMessage(Component.text(
//                        BotManager.jda == null
//                                ? "Discord bot not connected; cannot post test JSON to balance thread"
//                                : "No balance thread tracked yet (post a balance embed in a bot-teams thread first)",
//                        NamedTextColor.RED
//                ));
//            } else {
//                Warlords.newChain()
//                        .async(() -> DatabaseGameCTF.sendGamesBacklogJsonToLatestBalanceThread(mockGame))
//                        .execute();
//                player.sendMessage(Component.text(
//                        "Sent mock games-backlog JSON (" + mockGame.getId() + ".json) to balance thread " + balanceThreadId,
//                        NamedTextColor.GREEN
//                ));
//            }
        }
        ChatChannels.sendDebugMessage(commandSender instanceof Player player ? player : null, "Executed OldTestCommand");
    }

    private static DatabaseGameCTF createMockBacklogTestGame() {
        DatabaseGameCTF databaseGame = new DatabaseGameCTF();
        databaseGame.setId("000000000000000000000000");
        databaseGame.setWinner(Team.BLUE);
        databaseGame.setBluePoints(3);
        databaseGame.setRedPoints(0);
        databaseGame.getPlayers().put(Team.BLUE, List.of(
                DatabaseGamePlayerCTF.forTest(UUID.fromString("19f33c9c-088d-48eb-bdae-ea226f6773db"), "sdrawk", 1, 10),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("331bfda4-a7aa-416a-bbed-a00082f4cad1"), "FantastickDuck", 8, 8),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("9048540a-fd2d-4fa2-8aea-b3e1c8b02ef6"), "2ExtraHearts", 3, 12),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("9f2b2230-3b2c-4b0f-a141-d7b598e236c7"), "sumSmash", 14, 10),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("79aaa188-3967-48fb-a68f-8a29c9d535c1"), "WaifuOverdose", 12, 0),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("d9ef87ba-1fde-4b78-9551-6c24a71e871f"), "TimeTracker", 14, 0),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("829451e6-9a66-45a7-b431-ad740f4b7305"), "Santenza", 1, 15),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("d22b2641-ab2a-4fad-b00a-4bc9371436ce"), "IceKid16", 1, 7)
        ));
        databaseGame.getPlayers().put(Team.RED, List.of(
                DatabaseGamePlayerCTF.forTest(UUID.fromString("c677538b-9529-443f-ac5f-3008447f5556"), "MyFriendsAreGone", 13, 8),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("5bd14934-3c38-4552-89c8-c3a2e1c0b9bd"), "07MrSoupy", 12, 13),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("5a8046c6-731b-429f-a121-7f3da033fabc"), "Richdragon123", 5, 4),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("8d832819-166e-456e-8bc6-0c0c5e06befc"), "Ubruh", 10, 10),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("52adf7e5-1e11-454b-801e-06df88c04c96"), "DjilobodjiBichmo", 13, 12),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("0ffe5c87-a129-4c98-ad91-3ffd577a0bb9"), "DaLigaIsSmilin", 3, 12),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("6c99dd59-6f79-4146-9a02-1f6a9728ab5e"), "Slatiana", 6, 7),
                DatabaseGamePlayerCTF.forTest(UUID.fromString("51de40df-17c5-47c7-b5cd-ab393410d21a"), "Super_Dookie_69", 15, 9)
        ));
        return databaseGame;
    }

}
