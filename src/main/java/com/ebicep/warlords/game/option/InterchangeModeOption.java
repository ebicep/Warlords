package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InterchangeModeOption implements Option {

    public static final int MAX_SWAP_TIME = 60;
    public static final int MIN_SWAP_TIME = 30;
    private final HashMap<UUID, Specializations> previousSelectedSpecs = new HashMap<>();
    private final HashMap<UUID, HashMap<Specializations, SkillBoosts>> previousSelectedSkillBoosts = new HashMap<>();
    private final HashMap<UUID, HashMap<Specializations, Weapons>> previousSelectedWeaponSkins = new HashMap<>();
    private final HashMap<UUID, List<ArmorManager.Helmets>> previousSelectedHelmets = new HashMap<>();
    private final HashMap<UUID, List<ArmorManager.ArmorSets>> previousSelectedArmorSets = new HashMap<>();

    private int secondsUntilNextSwap = 0;

    @Override
    public void register(@Nonnull Game game) {

    }

    @Override
    public void start(@Nonnull Game game) {
        //saving player info as it will be modified during the game
        game.getPlayers().forEach((uuid, team) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            previousSelectedSpecs.put(uuid, playerSettings.getSelectedSpec());
            previousSelectedSkillBoosts.put(uuid, playerSettings.getClassesSkillBoosts());
            previousSelectedWeaponSkins.put(uuid, playerSettings.getWeaponSkins());
            previousSelectedHelmets.put(uuid, ArmorManager.Helmets.getSelected(uuid));
            previousSelectedArmorSets.put(uuid, ArmorManager.ArmorSets.getSelected(uuid));
        });

        generateNextSwapTime();

        new GameRunnable(game) {
            int secondsPast = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    return;
                }
                if (secondsPast >= secondsUntilNextSwap) {
                    swap(game);
                    generateNextSwapTime();
                    secondsPast = 0;
                }
                secondsPast++;
            }

        }.runTaskTimer(GameRunnable.SECOND, GameRunnable.SECOND);
    }

    @Override
    public void onGameEnding(@Nonnull Game game) {
        //resetting player info
        game.getPlayers().forEach((uuid, team) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            playerSettings.setSelectedSpec(previousSelectedSpecs.get(uuid));
            playerSettings.setSpecsSkillBoosts(previousSelectedSkillBoosts.get(uuid));
            playerSettings.setWeaponSkins(previousSelectedWeaponSkins.get(uuid));
            playerSettings.setHelmets(previousSelectedHelmets.get(uuid));
            playerSettings.setArmorSets(previousSelectedArmorSets.get(uuid));
        });
    }

    private void swap(Game game) {
        TeamMarker.getTeams(game).forEach(team -> swapTeamMembers(game, team));
    }

    //the player BEFORE becomes the player AFTER
    //the last player BECOMES the first player
    private void swapTeamMembers(Game game, Team team) {
        List<WarlordsPlayer> teamPlayers = game.warlordsPlayers()
                .filter(warlordsPlayer -> warlordsPlayer.getTeam() == team)
                .collect(Collectors.toList());
        if (teamPlayers.size() <= 1) return;

        //Storing all player information as swapping jumbles it up
        HashMap<UUID, Location> playerLocations = new HashMap<>();
        HashMap<UUID, Specializations> playerClasses = new HashMap<>();
        HashMap<UUID, HashMap<Specializations, SkillBoosts>> playerBoosts = new HashMap<>();
        HashMap<UUID, HashMap<Specializations, Weapons>> playerWeaponSkins = new HashMap<>();
        HashMap<UUID, List<ArmorManager.Helmets>> playerHelmets = new HashMap<>();
        HashMap<UUID, List<ArmorManager.ArmorSets>> playerArmorSets = new HashMap<>();
        HashMap<UUID, Boolean> playerOnHorse = new HashMap<>();
        for (WarlordsPlayer teamPlayer : teamPlayers) {
            UUID uuid = teamPlayer.getUuid();
            playerLocations.put(uuid, teamPlayer.getLocation());
            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            playerClasses.put(uuid, playerSettings.getSelectedSpec());
            playerBoosts.put(uuid, playerSettings.getClassesSkillBoosts());
            playerWeaponSkins.put(uuid, playerSettings.getWeaponSkins());
            playerHelmets.put(uuid, playerSettings.getHelmets());
            playerArmorSets.put(uuid, playerSettings.getArmorSets());
            playerOnHorse.put(uuid, teamPlayer.getEntity().getVehicle() != null);
        }

        //take beginning player to swap with end
        WarlordsPlayer secondPlayer = teamPlayers.get(0);
        String secondPlayerName = secondPlayer.getName();
        UUID secondPlayerUuid = secondPlayer.getUuid();
        LivingEntity secondPlayerEntity = secondPlayer.getEntity();

        PlayerSettings playerSettings = Warlords.getPlayerSettings(secondPlayer.getUuid());

        for (int i = 0; i < teamPlayers.size() - 1; i++) {
            transferPlayerStats(teamPlayers.get(i), teamPlayers.get(i + 1),
                    playerLocations,
                    playerClasses,
                    playerBoosts,
                    playerWeaponSkins,
                    playerHelmets,
                    playerArmorSets,
                    playerOnHorse
            );
        }

        //give last player first players old stats
        WarlordsPlayer firstPlayer = teamPlayers.get(teamPlayers.size() - 1);
        System.out.println("LAST SWAP - " + firstPlayer.getName() + " <<< " + secondPlayerName);

        UUID firstPlayerUuid = firstPlayer.getUuid();
        firstPlayer.setName(secondPlayerName);
        firstPlayer.setUuid(secondPlayerUuid);
        secondPlayerEntity.teleport(playerLocations.get(firstPlayerUuid));
        firstPlayer.setEntity(secondPlayerEntity);
        if (playerOnHorse.get(firstPlayerUuid)) {
            firstPlayer.getHorse().spawn();
        }
        if (firstPlayer.getEntity() instanceof Player) {
            PacketUtils.sendTitle((Player) firstPlayer.getEntity(),
                    ChatColor.YELLOW + "Swapped to",
                    ChatColor.GREEN.toString() + ChatColor.MAGIC + "00" + ChatColor.GREEN + " " + firstPlayer.getSpecClass().name + "! " + ChatColor.MAGIC + "00",
                    10, 40, 10
            );
        }
        //copying over playersettings
        playerSettings.setSelectedSpec(playerClasses.get(firstPlayerUuid));
        playerSettings.setWeaponSkins(playerWeaponSkins.get(firstPlayerUuid));
        playerSettings.setSpecsSkillBoosts(playerBoosts.get(firstPlayerUuid));
        playerSettings.setHelmets(playerHelmets.get(firstPlayerUuid));
        playerSettings.setArmorSets(playerArmorSets.get(firstPlayerUuid));

        if (firstPlayer.getEntity() instanceof Player) {
            firstPlayer.updatePlayer((Player) firstPlayer.getEntity());
        }
        Warlords.getPlayers().put(secondPlayerUuid, firstPlayer);


        Warlords.newChain()
                .delay(100, TimeUnit.MILLISECONDS)
                .sync(() -> {
                    ArmorManager.resetArmor(firstPlayer.getUuid(), firstPlayer.getEntity(), firstPlayer.getSpecClass(), firstPlayer.getTeam());
                }).execute();
    }

    //firstplayer gets the stats of the second
    private void transferPlayerStats(WarlordsPlayer firstPlayer, WarlordsPlayer secondPlayer,
                                     HashMap<UUID, Location> playerLocations,
                                     HashMap<UUID, Specializations> playerClasses,
                                     HashMap<UUID, HashMap<Specializations, SkillBoosts>> playerBoosts,
                                     HashMap<UUID, HashMap<Specializations, Weapons>> playerWeaponSkins,
                                     HashMap<UUID, List<ArmorManager.Helmets>> playerHelmets,
                                     HashMap<UUID, List<ArmorManager.ArmorSets>> playerArmorSets,
                                     HashMap<UUID, Boolean> playerOnHorse
    ) {
        System.out.println("SWAP - " + firstPlayer.getName() + " <<< " + secondPlayer.getName());

        UUID firstPlayerUuid = firstPlayer.getUuid();
        firstPlayer.setName(secondPlayer.getName());
        firstPlayer.setUuid(secondPlayer.getUuid());
        secondPlayer.teleport(playerLocations.get(firstPlayerUuid));
        firstPlayer.setEntity(secondPlayer.getEntity());
        if (playerOnHorse.get(firstPlayerUuid)) {
            firstPlayer.getHorse().spawn();
        }
        if (firstPlayer.getEntity() instanceof Player) {
            PacketUtils.sendTitle((Player) firstPlayer.getEntity(),
                    ChatColor.YELLOW + "Swapped to",
                    ChatColor.GREEN.toString() + ChatColor.MAGIC + "00" + ChatColor.GREEN + " " + firstPlayer.getSpecClass().name + "! " + ChatColor.MAGIC + "00",
                    10, 40, 10
            );
        }
        //copying over playersettings
        PlayerSettings playerSettings = Warlords.getPlayerSettings(secondPlayer.getUuid());
        playerSettings.setSelectedSpec(playerClasses.get(firstPlayerUuid));
        playerSettings.setWeaponSkins(playerWeaponSkins.get(firstPlayerUuid));
        playerSettings.setSpecsSkillBoosts(playerBoosts.get(firstPlayerUuid));
        playerSettings.setHelmets(playerHelmets.get(firstPlayerUuid));
        playerSettings.setArmorSets(playerArmorSets.get(firstPlayerUuid));
        if (firstPlayer.getEntity() instanceof Player) {
            firstPlayer.updatePlayer((Player) firstPlayer.getEntity());
        }
        Warlords.getPlayers().put(secondPlayer.getUuid(), firstPlayer);


        Warlords.newChain()
                .delay(100, TimeUnit.MILLISECONDS)
                .sync(() -> {
                    ArmorManager.resetArmor(firstPlayer.getUuid(), firstPlayer.getEntity(), firstPlayer.getSpecClass(), firstPlayer.getTeam());
                }).execute();
    }

    private void generateNextSwapTime() {
        this.secondsUntilNextSwap = new Random().nextInt(MAX_SWAP_TIME - MIN_SWAP_TIME) + MIN_SWAP_TIME;
        System.out.println("Swapping in " + secondsUntilNextSwap + " seconds");
    }

}
