package com.ebicep.warlords.guilds.bounty;

public class GuildBountyProgress {

    private GuildBounty bounty;
    private long value;
    private boolean completed;

    public GuildBountyProgress() {
    }

    public GuildBountyProgress(GuildBounty bounty) {
        this.bounty = bounty;
    }

    public GuildBounty getBounty() {
        return bounty;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = bounty == null ? Math.max(0, value) : Math.min(Math.max(0, value), bounty.getTarget());
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean add(long amount) {
        return update(Math.min(bounty.getTarget(), value + amount));
    }

    public boolean updateMax(long newValue) {
        return update(Math.max(value, Math.min(newValue, bounty.getTarget())));
    }

    private boolean update(long newValue) {
        value = newValue;
        if (!completed && value >= bounty.getTarget()) {
            completed = true;
            return true;
        }
        return false;
    }
}
