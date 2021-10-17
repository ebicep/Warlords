package com.ebicep.warlords.player;

public class CustomScoreboardPair {

    private String prefix;
    private String suffix;

    public CustomScoreboardPair() {
        this.prefix = "";
        this.suffix = "";
    }

    public CustomScoreboardPair(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPrefixAndSuffix(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }
}
