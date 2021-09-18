package com.ebicep.warlords;

public enum ChatChannels {

    ALL("All"),
    PARTY("Party"),

    ;

    public String name;

    ChatChannels(String name) {
        this.name = name;
    }
}
