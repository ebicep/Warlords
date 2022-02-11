package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.GameRunnable;
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

    public static final int MAX_SWAP_TIME = 11;//80;
    public static final int MIN_SWAP_TIME = 10;//40;
    private final HashMap<UUID, Classes> previousSelectedClasses = new HashMap<>();
    private final HashMap<UUID, HashMap<Classes, ClassesSkillBoosts>> previousSelectedSkillBoosts = new HashMap<>();
    private final HashMap<UUID, HashMap<Classes, Weapons>> previousSelectedWeaponSkins = new HashMap<>();
    private final HashMap<UUID, List<ArmorManager.Helmets>> previousSelectedHelmets = new HashMap<>();
    private final HashMap<UUID, List<ArmorManager.ArmorSets>> previousSelectedArmorSets = new HashMap<>();

    private int ticksUntilNextSwap = 0;

    @Override
    public void register(@Nonnull Game game) {

    }

    @Override
    public void start(@Nonnull Game game) {
        //saving player info as it will be modified during the game
        game.getPlayers().forEach((uuid, team) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            previousSelectedClasses.put(uuid, playerSettings.getSelectedClass());
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
                System.out.println(secondsPast + " - " + ticksUntilNextSwap);
                if (secondsPast >= ticksUntilNextSwap) {
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
            playerSettings.setSelectedClass(previousSelectedClasses.get(uuid));
            playerSettings.setClassesSkillBoosts(previousSelectedSkillBoosts.get(uuid));
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
        System.out.println(teamPlayers);

        //take beginning player to swap with end
        WarlordsPlayer secondPlayer = teamPlayers.get(0);
        String secondPlayerName = secondPlayer.getName();
        UUID secondPlayerUuid = secondPlayer.getUuid();
        Location secondPlayerLocation = secondPlayer.getLocation();
        LivingEntity secondPlayerEntity = secondPlayer.getEntity();
        boolean onHorse = secondPlayerEntity.getVehicle() != null;

        PlayerSettings oldSettings = Warlords.getPlayerSettings(secondPlayerUuid);
        Classes oldClasses = oldSettings.getSelectedClass();
        ClassesSkillBoosts oldBoost = oldSettings.getSkillBoostForClass();
        List<ArmorManager.Helmets> oldHelmets = ArmorManager.Helmets.getSelected(secondPlayerUuid);
        List<ArmorManager.ArmorSets> oldArmorSets = ArmorManager.ArmorSets.getSelected(secondPlayerUuid);

        for (int i = 0; i < teamPlayers.size() - 1; i++) {
            transferPlayerStats(teamPlayers.get(i), teamPlayers.get(i + 1));
        }

        //give last player first players old stats
        WarlordsPlayer firstPlayer = teamPlayers.get(teamPlayers.size() - 1);

        System.out.println("LAST SWAP - " + firstPlayer.getName() + " <<< " + secondPlayerName);

        UUID firstPlayerUuid = firstPlayer.getUuid();
        firstPlayer.setName(secondPlayerName);
        firstPlayer.setUuid(secondPlayerUuid);
        firstPlayer.getEntity().teleport(secondPlayerLocation);
        if (onHorse) {
            firstPlayer.getHorse().spawn();
        }
        firstPlayer.setEntity(secondPlayerEntity);
        //copying over playersettings
        PlayerSettings playerSettings = Warlords.getPlayerSettings(firstPlayerUuid);
        playerSettings.setSelectedClass(oldClasses);
        playerSettings.setSkillBoostForSelectedClass(oldBoost);
        playerSettings.setHelmets(oldHelmets);
        playerSettings.setArmorSets(oldArmorSets);
        if (firstPlayer.getEntity() instanceof Player) {
            firstPlayer.updatePlayer((Player) firstPlayer.getEntity());
        }
        Warlords.getPlayers().put(secondPlayerUuid, firstPlayer);


        Warlords.newChain()
                .delay(1, TimeUnit.MILLISECONDS)
                .sync(() -> {
                    ArmorManager.resetArmor(firstPlayer.getUuid(), firstPlayer.getEntity(), firstPlayer.getSpecClass(), firstPlayer.getTeam());
                }).execute();
    }

    //firstplayer gets the stats of the second
    private void transferPlayerStats(WarlordsPlayer firstPlayer, WarlordsPlayer secondPlayer) {
        System.out.println("SWAP - " + firstPlayer.getName() + " <<< " + secondPlayer.getName());

        UUID uuid = firstPlayer.getUuid();
        firstPlayer.setName(secondPlayer.getName());
        firstPlayer.setUuid(secondPlayer.getUuid());
        firstPlayer.getEntity().teleport(secondPlayer.getLocation());
        if (secondPlayer.getEntity().getVehicle() != null) {
            firstPlayer.getHorse().spawn();
        }
        firstPlayer.setEntity(secondPlayer.getEntity());
        //copying over playersettings
        PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
        PlayerSettings otherPlayerSettings = Warlords.getPlayerSettings(secondPlayer.getUuid());
        playerSettings.setSelectedClass(otherPlayerSettings.getSelectedClass());
        playerSettings.setSkillBoostForSelectedClass(otherPlayerSettings.getSkillBoostForClass());
        playerSettings.setHelmets(ArmorManager.Helmets.getSelected(secondPlayer.getUuid()));
        playerSettings.setArmorSets(ArmorManager.ArmorSets.getSelected(secondPlayer.getUuid()));
        if (firstPlayer.getEntity() instanceof Player) {
            firstPlayer.updatePlayer((Player) firstPlayer.getEntity());
        }
        Warlords.getPlayers().put(secondPlayer.getUuid(), firstPlayer);


        Warlords.newChain()
                .delay(1, TimeUnit.MILLISECONDS)
                .sync(() -> {
                    ArmorManager.resetArmor(firstPlayer.getUuid(), firstPlayer.getEntity(), firstPlayer.getSpecClass(), firstPlayer.getTeam());
                }).execute();
    }

    private void generateNextSwapTime() {
        this.ticksUntilNextSwap = new Random().nextInt(MAX_SWAP_TIME - MIN_SWAP_TIME) + MIN_SWAP_TIME;
    }

}
