package com.ebicep.warlords.poll.polls;

import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.poll.AbstractPoll;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PartyPoll extends AbstractPoll<PartyPoll> {

    private Party party;

    public PartyPoll() {
    }

    @Override
    public int getNumberOfPlayersThatCanVote() {
        return party.getPartyPlayers().size() - excludedPlayers.size();
    }

    @Override
    public List<UUID> getUUIDsAllowedToVote() {
        return party.getAllPartyPeoplePlayerOnline().stream()
                    .map(Player::getUniqueId)
                    .filter(uniqueId -> !excludedPlayers.contains(uniqueId))
                    .toList();
    }

    @Override
    public boolean sendNonVoterMessage(Player player) {
        return getNumberOfPlayersThatCanVote() != playerAnsweredWithOption.size() &&
                (party.getPartyLeader().getUUID().equals(player.getUniqueId()) || party.getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUUID().equals(player.getUniqueId())));
    }

    @Override
    public void onPollEnd() {
        party.getPolls().remove(this);
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public static class Builder extends AbstractPoll.Builder<PartyPoll, Builder> {

        public Builder() {
        }

        public Builder(Party party) {
            setParty(party);
        }

        @Override
        public PartyPoll createPoll() {
            return new PartyPoll();
        }

        @Override
        public Builder thisBuilder() {
            return this;
        }

        public Builder setParty(Party party) {
            poll.setParty(party);
            return builder;
        }

    }
}
