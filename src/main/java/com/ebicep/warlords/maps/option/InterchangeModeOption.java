package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.TeamMarker;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.GameRunnable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class InterchangeModeOption implements Option {

    public static final int MAX_SWAP_TIME = 16;//80;
    public static final int MIN_SWAP_TIME = 15;//40;
    private int ticksUntilNextSwap = 0;

    @Override
    public void register(@Nonnull Game game) {

    }

    @Override
    public void start(@Nonnull Game game) {
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

    private void swap(Game game) {
        TeamMarker.getTeams(game).forEach(team -> swapTeamMembers(game, team));
    }

    private void swapTeamMembers(Game game, Team team) {
        List<WarlordsPlayer> teamPlayers = game.warlordsPlayers()
                .filter(warlordsPlayer -> warlordsPlayer.getTeam() == team)
                .collect(Collectors.toList());
        if (teamPlayers.isEmpty()) return;
        //take beginning player to swap with end
        WarlordsPlayer initialPlayer = teamPlayers.get(0);
        String name = initialPlayer.getName();
        UUID uuid = initialPlayer.getUuid();
        Location location = initialPlayer.getLocation();
        LivingEntity livingEntity = initialPlayer.getEntity();

        for (int i = 0; i < teamPlayers.size() - 1; i++) {
            transferPlayerStats(teamPlayers.get(i), teamPlayers.get(i + 1));
        }

        //give last player first players old stats
        WarlordsPlayer lastPlayer = teamPlayers.get(teamPlayers.size() - 1);
        lastPlayer.setName(name);
        lastPlayer.setUuid(uuid);
        lastPlayer.getEntity().teleport(location);
        lastPlayer.setEntity(livingEntity);
        lastPlayer.updateRedItem();
        lastPlayer.updatePurpleItem();
        lastPlayer.updateBlueItem();
        lastPlayer.updateOrangeItem();

        Warlords.getPlayers().put(uuid, lastPlayer);
        ArmorManager.resetArmor((Player) lastPlayer.getEntity(), lastPlayer.getSpecClass(), lastPlayer.getTeam());

    }

    //firstplayer gets the stats of the second
    private void transferPlayerStats(WarlordsPlayer firstPlayer, WarlordsPlayer secondPlayer) {
        firstPlayer.setName(secondPlayer.getName());
        firstPlayer.setUuid(secondPlayer.getUuid());
        firstPlayer.getEntity().teleport(secondPlayer.getLocation());
        firstPlayer.setEntity(secondPlayer.getEntity());
        firstPlayer.updateRedItem();
        firstPlayer.updatePurpleItem();
        firstPlayer.updateBlueItem();
        firstPlayer.updateOrangeItem();

        Warlords.getPlayers().put(secondPlayer.getUuid(), firstPlayer);
        ArmorManager.resetArmor((Player) firstPlayer.getEntity(), firstPlayer.getSpecClass(), firstPlayer.getTeam());

//        secondPlayer.setName(name);
//        secondPlayer.setUuid(uuid);
//        secondPlayer.getEntity().teleport(location);
//        secondPlayer.setEntity(livingEntity);
//        secondPlayer.updateRedItem();
//        secondPlayer.updatePurpleItem();
//        secondPlayer.updateBlueItem();
//        secondPlayer.updateOrangeItem();
//
//        Warlords.getPlayers().put(uuid, secondPlayer);
//        ArmorManager.resetArmor((Player) secondPlayer.getEntity(), secondPlayer.getSpecClass(), secondPlayer.getTeam());

//        AbstractPlayerClass abstractPlayerClass = firstPlayer.getSpec();
//        Classes classes = firstPlayer.getSpecClass();
//        Weapons weapons = firstPlayer.getWeapon();
//        int health = firstPlayer.getHealth();
//        int maxHealth = firstPlayer.getMaxHealth();
//        int regenTimer = firstPlayer.getRegenTimer();
//        int respawnTimer = firstPlayer.getRespawnTimer();
//        boolean dead = firstPlayer.isDead();
//        float energy = firstPlayer.getEnergy();
//        float maxEnergy = firstPlayer.getMaxEnergy();
//        float horseCooldown = firstPlayer.getHorseCooldown();
//        int healPowerupDuration = firstPlayer.getHealPowerupDuration();
//        float currentHealthModifier = firstPlayer.getCurrentHealthModifier();
//        int flagCooldown = firstPlayer.getFlagCooldown();
//        int hitCooldown = firstPlayer.getHitCooldown();
//        LinkedHashMap<WarlordsPlayer, Integer> hitBy = firstPlayer.getHitBy();
//        LinkedHashMap<WarlordsPlayer, Integer> healedBy = firstPlayer.getHealedBy();
//        CalculateSpeed calculateSpeed = firstPlayer.getSpeed();
//        boolean powerUpHeal = firstPlayer.isPowerUpHeal();
//        Location deathLocation = firstPlayer.getDeathLocation();
//        CooldownManager cooldownManager = firstPlayer.getCooldownManager();
//        FlagInfo flagInfo = firstPlayer.getCarriedFlag();
//        CompassTargetMarker compassTarget = firstPlayer.getCompassTarget();

    }

    private void generateNextSwapTime() {
        this.ticksUntilNextSwap = new Random().nextInt(MAX_SWAP_TIME - MIN_SWAP_TIME) + MIN_SWAP_TIME;
    }

}
