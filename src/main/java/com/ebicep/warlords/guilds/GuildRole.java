package com.ebicep.warlords.guilds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GuildRole {

    private String roleName;
    private Set<GuildPermissions> permissions = new HashSet<>();
    private Set<UUID> players = new HashSet<>();

    public GuildRole() {
    }

    public GuildRole(String roleName, GuildPermissions... permissions) {
        this.roleName = roleName;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<GuildPermissions> getPermissions() {
        return permissions;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
    }
}
