package com.ebicep.customentities.npc;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class CustomNPC {

    private NPC npc;
    private Location spawnLocation;
    private boolean nameVisible;
    private List<String> npcInfo;

    public CustomNPC(NPC npc, Location spawnLocation, boolean nameVisible, List<String> npcInfo) {
        this.npc = npc;
        this.spawnLocation = spawnLocation;
        this.nameVisible = nameVisible;
        this.npcInfo = npcInfo;
    }

    public void hideName() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            if (scoreboard.getTeam(npc.getName()) != null) {
                scoreboard.getTeam(npc.getName()).unregister();
            }
            Team team = scoreboard.registerNewTeam(npc.getName());
            team.addEntry(npc.getName());
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

}
