package com.ebicep.warlords.party;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class PartyPlayer {
    private UUID uuid;
    private PartyPlayerType partyPlayerType;
    private boolean isOnline;
    private boolean isAFK;
    private int offlineTimeLeft = -1;

    public PartyPlayer(UUID uuid, PartyPlayerType partyPlayerType) {
        this.uuid = uuid;
        this.partyPlayerType = partyPlayerType;
        this.isOnline = true;
    }

    public Component getPartyListDot() {
        if (!isOnline) {
            return Component.text(" ● ", NamedTextColor.RED);
        }
        if (isAFK) {
            return Component.text(" ● ", NamedTextColor.GOLD);
        }
        return Component.text(" ● ", NamedTextColor.GREEN);


    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public PartyPlayerType getPartyPlayerType() {
        return partyPlayerType;
    }

    public void setPartyPlayerType(PartyPlayerType partyPlayerType) {
        this.partyPlayerType = partyPlayerType;
    }

    public boolean isOffline() {
        return !isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public boolean isNotAFK() {
        return !isAFK;
    }

    public boolean isAFK() {
        return isAFK;
    }

    public void setAFK(boolean AFK) {
        isAFK = AFK;
    }

    public int getOfflineTimeLeft() {
        return offlineTimeLeft;
    }

    public void setOfflineTimeLeft(int offlineTimeLeft) {
        this.offlineTimeLeft = offlineTimeLeft;
    }
}
