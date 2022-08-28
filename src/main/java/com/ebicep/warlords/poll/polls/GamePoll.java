package com.ebicep.warlords.poll.polls;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.poll.AbstractPoll;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GamePoll extends AbstractPoll<GamePoll> {

    private Game game;

    @Override
    public int getNumberOfPlayersThatCanVote() {
        return game.playersCount() - excludedPlayers.size();
    }

    @Override
    public List<UUID> getUUIDsAllowedToVote() {
        return game.onlinePlayers()
                .filter(playerTeamEntry -> playerTeamEntry.getValue() != null)
                .filter(playerTeamEntry -> !excludedPlayers.contains(playerTeamEntry.getKey().getUniqueId()))
                .map(Map.Entry::getKey)
                .map(Player::getUniqueId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendNonVoterMessage(Player player) {
        return true;
    }

    @Override
    public void onPollEnd() {
        //send poll results to other team/spectators
        List<UUID> allowedToVote = getUUIDsAllowedToVote();
        List<UUID> otherUUIDs = game.onlinePlayers()
                .map(Map.Entry::getKey)
                .map(Entity::getUniqueId)
                .filter(uuid -> !allowedToVote.contains(uuid))
                .collect(Collectors.toList());

        sendPollResultsToPlayers(game.onlinePlayers()
                .filter(playerTeamEntry -> otherUUIDs.contains(playerTeamEntry.getKey().getUniqueId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public static class Builder extends AbstractPoll.Builder<GamePoll, GamePoll.Builder> {

        public Builder() {
        }

        public Builder(Game game) {
            setGame(game);
        }

        @Override
        public GamePoll createPoll() {
            return new GamePoll();
        }

        @Override
        public GamePoll.Builder thisBuilder() {
            return this;
        }

        public GamePoll.Builder setGame(Game game) {
            poll.setGame(game);
            return builder;
        }

    }
}
