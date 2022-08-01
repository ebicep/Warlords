package com.ebicep.warlords.party;

import com.ebicep.warlords.util.java.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyManager {

    public static final List<Party> PARTIES = new ArrayList<>();

    public static void disbandParty(Party party) {
        PARTIES.remove(party);
    }

    public static Optional<Party> getPartyFromLeaderName(String name) {
        return PARTIES.stream().filter(party -> party.getLeaderName().equals(name)).findFirst();
    }

    public static Pair<Party, PartyPlayer> getPartyAndPartyFromLeader(UUID uuid) {
        for (Party party : PARTIES) {
            if (party.getPartyLeader().getUUID().equals(uuid)) {
                return new Pair<>(party, party.getPartyLeader());
            }
        }
        return null;
    }

    public static Pair<Party, PartyPlayer> getPartyAndPartyPlayerFromAny(UUID uuid) {
        for (Party party : PARTIES) {
            for (PartyPlayer partyPlayer : party.getPartyPlayers()) {
                if (partyPlayer.getUUID().equals(uuid)) {
                    return new Pair<>(party, partyPlayer);
                }
            }
        }
        return null;
    }

    public static boolean inAParty(UUID uuid) {
        return PARTIES.stream().anyMatch(party -> party.hasUUID(uuid));
    }

    public static boolean inSameParty(UUID uuid1, UUID uuid2) {
        return PARTIES.stream().anyMatch(party -> party.hasUUID(uuid1) && party.hasUUID(uuid2));
    }
}
