package com.ebicep.warlords.party.commands;

import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;

public class PartyPlayerWrapper {

    private final Pair<Party, PartyPlayer> partyPlayerPair;

    public PartyPlayerWrapper(Pair<Party, PartyPlayer> partyPlayerPair) {
        this.partyPlayerPair = partyPlayerPair;
    }

    public Party getParty() {
        return partyPlayerPair.getA();
    }

    public PartyPlayer getPartyPlayer() {
        return partyPlayerPair.getB();
    }

}
