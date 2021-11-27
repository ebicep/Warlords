package com.ebicep.warlords.database.newdb;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.newdb.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.newdb.repositories.games.GameService;
import com.ebicep.warlords.database.newdb.repositories.player.PlayerService;
import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.Settings;
import com.ebicep.warlords.player.Weapons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.HashMap;
import java.util.UUID;

public class DatabaseManager {

    public static PlayerService playerService;
    public static GameService gameService;

    public static String lastWarlordsPlusString = "";

    public static void init() {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        playerService = context.getBean("playerService", PlayerService.class);
        gameService = context.getBean("gameService", GameService.class);

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> loadPlayer(player.getUniqueId()));
    }

    public static void loadPlayer(UUID uuid) {
        if(playerService.findByUUID(uuid) == null) {
            Warlords.newChain().syncFirst(() -> {
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if(name == null) {
                    System.out.println("NULL NAME ERROR !!!!! " + uuid.toString());
                }
                return name;
            }).asyncLast((name) -> playerService.create(new DatabasePlayer(uuid, name))).execute();
        } else {
            System.out.println("Loaded Player " + uuid);
        }
    }

    private static void loadPlayerInfo(Player player) {
//        DatabasePlayer databasePlayer = playerService.findByUUID(player.getUniqueId());
//        Warlords.getPlayerSettings(player.getUniqueId()).setSelectedClass(databasePlayer.getLastSpec());
//        ArmorManager.Helmets.setSelectedMage(player, ArmorManager.Helmets.getMageHelmet((String) getPlayerInfoWithDotNotation(player, "mage.helm")));
//        ArmorManager.ArmorSets.setSelectedMage(player, ArmorManager.ArmorSets.getMageArmor((String) getPlayerInfoWithDotNotation(player, "mage.armor")));
//        ArmorManager.Helmets.setSelectedWarrior(player, ArmorManager.Helmets.getWarriorHelmet((String) getPlayerInfoWithDotNotation(player, "warrior.helm")));
//        ArmorManager.ArmorSets.setSelectedWarrior(player, ArmorManager.ArmorSets.getWarriorArmor((String) getPlayerInfoWithDotNotation(player, "warrior.armor")));
//        ArmorManager.Helmets.setSelectedPaladin(player, ArmorManager.Helmets.getPaladinHelmet((String) getPlayerInfoWithDotNotation(player, "paladin.helm")));
//        ArmorManager.ArmorSets.setSelectedPaladin(player, ArmorManager.ArmorSets.getPaladinArmor((String) getPlayerInfoWithDotNotation(player, "paladin.armor")));
//        ArmorManager.Helmets.setSelectedShaman(player, ArmorManager.Helmets.getShamanHelmet((String) getPlayerInfoWithDotNotation(player, "shaman.helm")));
//        ArmorManager.ArmorSets.setSelectedShaman(player, ArmorManager.ArmorSets.getShamanArmor((String) getPlayerInfoWithDotNotation(player, "shaman.armor")));
//        HashMap<Classes, Weapons> weaponSkins = new HashMap<>();
//        for (Classes value : Classes.values()) {
//            weaponSkins.put(value, Weapons.getWeapon(
//                    (String) getPlayerInfoWithDotNotation(player, Classes.getClassesGroup(value).name.toLowerCase() + "." + value.name.toLowerCase() + ".weapon")));
//        }
//        Weapons.setSelected(player, weaponSkins);
//        Settings.HotkeyMode.setSelected(player, Settings.HotkeyMode.getHotkeyMode((String) getPlayerInfoWithDotNotation(player, "hotkeymode")));
//        Settings.ParticleQuality.setSelected(player, Settings.ParticleQuality.getParticleQuality((String) getPlayerInfoWithDotNotation(player, "particle_quality")));
    }

}
