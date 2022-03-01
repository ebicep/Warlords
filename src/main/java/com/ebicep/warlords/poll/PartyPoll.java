package com.ebicep.warlords.poll;

import com.ebicep.warlords.party.Party;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PartyPoll extends AbstractPoll {

    private Party party;

    public PartyPoll(Party party) {
        this.party = party;
    }

    public PartyPoll() {

    }

    @Override
    public int getNumberOfPlayersThatCanVote() {
        return party.getPartyPlayers().size() - excludedPlayers.size();
    }

    @Override
    public List<Player> getPlayersAllowedToVote() {
        return party.getAllPartyPeoplePlayerOnline().stream()
                .filter(player -> !excludedPlayers.contains(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendNonVoterMessage(Player player) {
        return (getNumberOfPlayersThatCanVote() != playerAnsweredWithOption.size() && (party.getPartyLeader().getUuid().equals(player.getUniqueId()) || party.getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()))));
    }

    @Override
    public void onPollEnd() {
        party.getPolls().remove(this);
    }

    public Party getParty() {
        return party;
    }

    public AbstractPoll setParty(Party party) {
        this.party = party;
        return this;
    }
}
