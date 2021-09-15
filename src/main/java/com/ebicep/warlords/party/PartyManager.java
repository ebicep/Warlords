package com.ebicep.warlords.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {

    private List<Party> parties = new ArrayList<>();

    public List<Party> getParties() {
        return parties;
    }

    public void disbandParty(Party party) {
        parties.remove(party);
    }

    public Optional<Party> getPartyFromLeader(UUID uuid) {
        return parties.stream().filter(party -> party.getLeader().equals(uuid)).findFirst();
    }

    public Optional<Party> getPartyFromAny(UUID uuid) {
        return parties.stream().filter(party -> party.getAllPartyPeople().contains(uuid)).findFirst();
    }

    public boolean inAParty(UUID uuid) {
        return parties.stream().anyMatch(party -> party.hasUUID(uuid));
    }
}
