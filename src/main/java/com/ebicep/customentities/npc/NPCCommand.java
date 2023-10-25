package com.ebicep.customentities.npc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.versioned.BossBarTrait;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("npcwl")
@CommandPermission("group.administrator")
public class NPCCommand extends BaseCommand {

    private static final List<NPC> SPAWNED = new ArrayList<>();

    @Subcommand("createdbnpcs")
    public void createDBNPCs(CommandIssuer commandIssuer) {
        NPCManager.createDatabaseRequiredNPCs();
    }

    @Subcommand("test")
    public void test(Player player) {
        for (int i = 0; i < 3; i++) {
            NPC npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.SPIDER, "" + i);
            BossBarTrait bossBarTrait = npc.getOrAddTrait(BossBarTrait.class);
            bossBarTrait.setTitle(i + "");
            npc.spawn(player.getLocation());
            SPAWNED.add(npc);
        }
    }

    @Subcommand("test2")
    public void test2(Player player, Boolean collidable, Boolean paused) {
        for (int i = 0; i < 3; i++) {
            NPC npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.SPIDER, "" + i);
            npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, true);
            npc.data().set(NPC.Metadata.COLLIDABLE, collidable); //false
            BossBarTrait bossBarTrait = npc.getOrAddTrait(BossBarTrait.class);
            bossBarTrait.setTitle(i + "");
            npc.spawn(player.getLocation());
            npc.getNavigator().setPaused(paused); //true
            SPAWNED.add(npc);
        }
    }

    @Subcommand("clear")
    public void clear(Player player) {
        SPAWNED.forEach(NPC::destroy);
        SPAWNED.clear();
    }

    @Subcommand("target")
    public void target(Player player) {
        SPAWNED.forEach(npc -> npc.getNavigator().setTarget(player, true));
    }

    @Subcommand("notarget")
    public void noTarget(Player player) {
        SPAWNED.forEach(npc -> npc.getNavigator().cancelNavigation());
    }

}
