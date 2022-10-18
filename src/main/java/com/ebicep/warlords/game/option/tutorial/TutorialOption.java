package com.ebicep.warlords.game.option.tutorial;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TutorialOption implements Option {

    //stage of tutorial
    private final AtomicInteger stage = new AtomicInteger(1);
    //stage inside stage
    private final AtomicInteger stageSection = new AtomicInteger(1);
    //second counter for stage section
    private final AtomicInteger stageSectionCounter = new AtomicInteger();
    private WarlordsPlayer warlordsPlayer;
    private List<WarlordsNPC> testDummies;

    @Override
    public void start(@Nonnull Game game) {
        warlordsPlayer = game.warlordsPlayers().collect(Collectors.toList()).get(0);

        game.registerEvents(new Listener() {
            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingFinalEvent event) {
                if (!event.getPlayer().equals(warlordsPlayer)) {
                    return;
                }
                if (!(event.getPlayer() instanceof WarlordsNPC) || !testDummies.contains((WarlordsNPC) event.getPlayer())) {
                    return;
                }
                switch (stage.get()) {
                    case 1:
                        if (event.getAttacker() == warlordsPlayer) {
                            nextStageSection();
                        }
                        if (event.isDead()) {
                            nextStageSection(20);
                        }
                        break;
                    case 2:
                        if (event.getAttacker() == warlordsPlayer) {
                            nextStageSection();
                        }
                        break;
                }
            }
        });
        new GameRunnable(game) {

            @Override
            public void run() {
                LivingEntity entity = warlordsPlayer.getEntity();
                if (!(entity instanceof Player)) {
                    return;
                }
                Player p = (Player) entity;
                Location location = warlordsPlayer.getLocation();

                stageSectionCounter.getAndIncrement();

                switch (stage.get()) {
                    case 1:
                        switch (stageSection.get()) {
                            case 1:
                                switch (stageSectionCounter.get()) {
                                    case 1:
                                        spawnTestDummies(game, Collections.singletonList(new LocationBuilder(location).pitch(location.getPitch() + 180)));
                                        PacketUtils.sendTitle(p, ChatColor.LIGHT_PURPLE + "Right click to perform a powerful attack.", "", 0, 40, 20);
                                        break;
                                    case 15:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "HINT: You need to be within 3 blocks of your enemy to strike them.",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                }
                                break;
                            case 2:
                                switch (stageSectionCounter.get()) {
                                    case 1:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "You just used your right click to perform an attack as Avenger, a melee spec.",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 3:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "There are two types of right-click attacks, melee and ranged.",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                }
                                break;
                            case 20:
                                nextStage();
                                break;
                        }
                        break;
                    case 2:
                        switch (stageSection.get()) {
                            case 1:
                                switch (stageSectionCounter.get()) {
                                    case 1:
                                        warlordsPlayer.respawn();
                                        warlordsPlayer.setSpec(Specializations.PYROMANCER, SkillBoosts.FIREBALL);
                                        spawnTestDummies(game, Collections.singletonList(new LocationBuilder(location).pitch(location.getPitch() + 180)));

                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "Right click to perform a powerful attack Your spec has now been swapped to Pyromancer. Right click to shoot a projectile at your enemy.",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 15:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "HINT: You need to be within 3 blocks of your enemy to strike them.",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                }
                                break;
                            case 2:
                                switch (stageSectionCounter.get()) {
                                    case 0:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "Good job!",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 2:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 4:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 6:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 8:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 10:
                                        PacketUtils.sendTitle(p,
                                                ChatColor.LIGHT_PURPLE + "",
                                                "",
                                                0,
                                                40,
                                                20
                                        );
                                        break;
                                    case 12:
                                        break;

                                }
                                break;
                        }
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }


        }.runTaskTimer(15 * 20, 20);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player.getEntity() instanceof Player) {
            player.getGame().setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
            player.setTeam(Team.BLUE);
            player.updateArmor();
        }
        player.setSpec(Specializations.AVENGER, SkillBoosts.AVENGER_STRIKE);
    }

    private void nextStage() {
        stage.getAndIncrement();
        stageSection.set(1);
        stageSectionCounter.set(0);
    }

    private void nextStageSection() {
        nextStageSection(stageSectionCounter.get() + 1);
    }

    private void nextStageSection(int newStageSection) {
        stageSection.set(newStageSection);
        stageSectionCounter.set(0);
    }

    private void spawnTestDummies(Game game, List<Location> locations) {
        testDummies.forEach(warlordsNPC -> game.removePlayer(warlordsNPC.getUuid()));
        testDummies.clear();
        for (Location location : locations) {
            WarlordsNPC testDummy = game.addNPC(new WarlordsNPC(
                    UUID.randomUUID(),
                    "TestDummy",
                    Weapons.BLUDGEON,
                    WarlordsNPC.spawnZombieNoAI(location, null),
                    game,
                    Team.RED,
                    Specializations.PYROMANCER
            ));
            testDummy.setTakeDamage(true);
            testDummy.setMaxHealth(1000);
            testDummy.setHealth(1000);
            testDummy.setRegenTimer(Integer.MAX_VALUE);
            testDummy.updateHealth();
            testDummies.add(testDummy);
        }
    }

}
