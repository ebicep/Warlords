package com.ebicep.warlords.database.repositories.player.pojos.general;

import java.util.List;

public class FutureMessage {
    private List<String> messages;
    private boolean centered;

    public FutureMessage(List<String> messages, boolean centered) {
        this.messages = messages;
        this.centered = centered;
    }

    public List<String> getMessages() {
        return messages;
    }

    public boolean isCentered() {
        return centered;
    }
}