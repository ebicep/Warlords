package com.ebicep.warlords.game.option.pve.tutorial;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeUnlockEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TutorialOption implements Option {

    //stage of tutorial
    private final AtomicInteger stage = new AtomicInteger(0);
    //stage inside stage
    private final AtomicInteger stageSection = new AtomicInteger(1);
    //second counter for stage section
    private final AtomicInteger stageSectionCounter = new AtomicInteger();
    private final List<WarlordsNPC> testDummies = new ArrayList<>();
    private WarlordsPlayer warlordsPlayer;
    private Location dummySpawnLocation;

    @Override
    public void start(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            boolean stage3DamageHealDone = false;

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getAttacker().equals(warlordsPlayer)) {
                    return;
                }
                if (!(event.getWarlordsEntity() instanceof WarlordsNPC) || !testDummies.contains((WarlordsNPC) event.getWarlordsEntity())) {
                    return;
                }
                switch (stage.get()) {
                    case 1 -> {
                        if (event.getAbility().equals("Avenger's Strike") && stageSection.get() == 1) {
                            nextStageSection();
                        } else {
                            event.setCancelled(true);
                        }
                    }
                    case 2 -> {
                        if (event.getAbility().isEmpty()) {
                            event.setCancelled(true);
                        } else {
                            nextStageSection();
                        }
                    }
                    case 3 -> {
                        if (stageSection.get() == 1) {
                            if (event.getAbility().isEmpty()) {
                                event.setCancelled(true);
                            } else {
                                if (!stage3DamageHealDone) {
                                    stage3DamageHealDone = true;
                                    nextStageSection();
                                }
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                event.setCancelled(true);

                AbstractAbility ability = event.getAbility();
                switch (stage.get()) {
                    case 1 -> {
                        if (ability instanceof AvengersStrike) {
                            event.setCancelled(false);
                        }
                    }
                    case 2 -> {
                        if (stageSection.get() == 1) {
                            if (ability instanceof Fireball) {
                                event.setCancelled(false);
                            }
                        }
                    }
                    case 3 -> {
                        switch (stageSection.get()) {
                            case 1 -> {
                                if (ability instanceof FlameBurst && stageSectionCounter.get() >= 1) {
                                    event.setCancelled(false);
                                }
                            }
                            case 2 -> {
                                if (ability instanceof AbstractTimeWarp && stageSectionCounter.get() >= 1) {
                                    event.setCancelled(false);
                                    new GameRunnable(game) {

                                        @Override
                                        public void run() {
                                            warlordsPlayer.resetAbilities(true);
                                            nextStageSection();
                                        }
                                    }.runTaskLater(20 * 4);
                                }
                            }
                            case 3 -> {
                                if (ability instanceof ArcaneShield && stageSectionCounter.get() >= 1) {
                                    event.setCancelled(false);
                                    new GameRunnable(game) {

                                        @Override
                                        public void run() {
                                            warlordsPlayer.resetAbilities(true);
                                            nextStageSection();
                                        }
                                    }.runTaskLater(20 * 4);
                                }
                            }
                            case 4 -> event.setCancelled(false);
                        }
                    }
                }
            }

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                    warlordsPlayer.respawn();
                    if (stage.get() == 3 && stageSection.get() == 4) {
                        warlordsPlayer.respawn();
                        warlordsPlayer.resetAbilities(true);
                        if (warlordsPlayer.getEntity() instanceof Player) {
                            sendTutorialMessage((Player) warlordsPlayer.getEntity(), "RIGHT CLICK TO PERFORM A POWERFUL ATTACK");
                        }
                    }
                } else {
                    if (stage.get() == 3 && stageSection.get() == 4) {
                        for (WarlordsNPC testDummy : testDummies) {
                            if (testDummy.isAlive()) {
                                return;
                            }
                        }
                        nextStageSection();
                    }
                }
            }

            @EventHandler
            public void onUpgradePurchase(WarlordsUpgradeUnlockEvent event) {
                if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                    if (stage.get() == 3 && stageSection.get() == 5) {
                        nextStageSection();
                    }
                }
            }

        });
        new GameRunnable(game) {

            private final String[] tutorialPrompts = new String[]{
                    "Hi! Welcome to the server.",
                    "It appears that you haven’t played Warlords before. On this server at least!",
                    "Here’s a quick tutorial to get you started.",
                    "I promise you that this won’t take long, if you wish to skip this, /tutorial skip",
            };

            private final String[] rangeAttackPrompts = new String[]{
                    "Good job!",
                    "There are currently 15 unique kits (specializations) in Warlords 2. Each kit has five abilities: your right click ability, and 4 other abilities.",
                    "You can cast these abilities by selecting the corresponding slots on your hotbar.",
                    "If you have not modified any controls, pressing 2, 3, 4, and 5 should activate these abilities.",
                    "We will now quickly go through the abilities of Pyromancer to demonstrate what abilities can do.",
            };

            private final String[] tutorialCompletionPrompts = new String[]{
                    "Congratulations! You now have a basic understanding of the Pyromancer class.",
                    "There are many unique abilities in Warlords which you can read their descriptions in the lobby.",
                    "You can also access the Warlords 2 documentation for abilities online at https://docs.flairy.me/skills/skills",
                    "Here’s just one more thing we want to show you before you venture into your first adventure.",
                    "You gain insignia when you kill enemies. You can use this insignia to buy in-game upgrades.",
                    "Press the nugget in your 8th slot to open the upgrade menu.",
                    "Click on any upgrade tree and buy the cheapest upgrade. You need to unlock the Tier I of a skill tree to purchase the later tiers.",
            };

            @Override
            public void run() {
                Entity entity = warlordsPlayer.getEntity();
                if (!(entity instanceof Player p)) {
                    return;
                }
                //System.out.println(stageSectionCounter);
                switch (stage.get()) {
                    case 0 -> intro(p);
                    case 1 -> strikeAttackTutorial(p);
                    case 2 -> rangedAttackTutorial(p);
                    case 3 -> activatingAbilities(p);
                }

                stageSectionCounter.getAndIncrement();
            }

            private void intro(Player p) {
                int counter = stageSectionCounter.get();
                if (counter % 2 != 0) {
                    return;
                }
                int index = counter / 2;
                if (index < tutorialPrompts.length) {
                    sendTutorialMessage(p, tutorialPrompts[index]);
                    if (index == tutorialPrompts.length - 1) {
                        nextStage();
                    }
                }
            }

            private void strikeAttackTutorial(Player p) {
                switch (stageSection.get()) {
                    case 1 -> {
                        switch (stageSectionCounter.get()) {
                            case 0 -> {
                                warlordsPlayer.respawn();
                                spawnTestDummies(game, Collections.singletonList(dummySpawnLocation.clone()));
                                sendTutorialMessage(p, "Right click to perform a powerful attack.");
                            }
                            case 15 -> sendTutorialMessage(p, "HINT: You need to be within 3 blocks of your enemy to strike them.");
                        }
                    }
                    case 2 -> {
                        switch (stageSectionCounter.get()) {
                            case 0 -> sendTutorialMessage(p, "You just used your right click to perform an attack as Avenger, a melee spec.");
                            case 2 -> sendTutorialMessage(p, "There are two types of right-click attacks, melee and ranged.");
                            case 3 -> nextStage();
                        }
                    }
                }
            }

            private void rangedAttackTutorial(Player p) {
                switch (stageSection.get()) {
                    case 1 -> {
                        if (stageSectionCounter.get() == 0) {
                            warlordsPlayer.respawn();
                            warlordsPlayer.setSpec(Specializations.PYROMANCER, SkillBoosts.FIREBALL);
                            spawnTestDummies(game, Collections.singletonList(dummySpawnLocation.clone()));
                            sendTutorialMessage(p,
                                    "Your spec has now been swapped to Pyromancer. Right-Click to shoot a projectile at your enemy."
                            );
                        }
                    }
                    case 2 -> {
                        int counter = stageSectionCounter.get();
                        if (counter % 2 != 0) {
                            return;
                        }
                        int index = counter / 2;
                        if (index < rangeAttackPrompts.length) {
                            sendTutorialMessage(p, rangeAttackPrompts[index]);
                            if (index == rangeAttackPrompts.length - 1) {
                                nextStage();
                            }
                        }
                    }
                }
            }

            private void activatingAbilities(Player p) {
                LocationBuilder spawnLocation = new LocationBuilder(dummySpawnLocation);
                switch (stageSection.get()) {
                    case 1 -> {
                        if (stageSectionCounter.get() == 1) {
                            warlordsPlayer.respawn();
                            warlordsPlayer.setSpec(Specializations.PYROMANCER, SkillBoosts.FIREBALL);
                            spawnTestDummies(game, Arrays.asList(spawnLocation, spawnLocation.clone().right(3), spawnLocation.clone().left(3)));

                            sendTutorialMessage(p, "Activate your red rune (Slot 2) while aiming at a group of enemies to deal massive damage to them.");
                        }
                    }
                    case 2 -> {
                        if (stageSectionCounter.get() == 1) {
                            testDummies.forEach(warlordsNPC -> game.removePlayer(warlordsNPC.getUuid()));
                            sendTutorialMessage(p,
                                    "Activate your purple rune (Slot 3) and walk around. After around 5 seconds, you will be teleported back to where you first cast your ability."
                            );
                        }
                    }
                    case 3 -> {
                        if (stageSectionCounter.get() == 1) {
                            sendTutorialMessage(p, "Activate your blue rune (Slot 4) and walk around. This summons a shield that will tank damage for you.");
                        }
                    }
                    case 4 -> {
                        if (stageSectionCounter.get() == 1) {
                            testDummies.forEach(warlordsNPC -> game.removePlayer(warlordsNPC.getUuid()));
                            testDummies.clear();
                            warlordsPlayer.respawn();
                            List<Location> locations = Arrays.asList(
                                    spawnLocation,
                                    spawnLocation.clone().right(10),
                                    spawnLocation.clone().right(5),
                                    spawnLocation.clone().left(10),
                                    spawnLocation.clone().left(5)
                            );
                            for (Location loc : locations) {
                                AbstractMob mob = Mob.ZOMBIE_LANCER.createMob(loc);
                                testDummies.add(game.addNPC(mob.toNPC(game, Team.RED, warlordsNPC -> {})));
                                mob.setTarget(warlordsPlayer);
                            }
                            sendTutorialMessage(p,
                                    "Activate your orange rune (Slot 5) and kill all the enemies! Your orange rune will make you deal more damage overall."
                            );
                        }
                    }
                    case 5 -> {
                        int counter = stageSectionCounter.get();
                        if (counter % 2 != 0) {
                            return;
                        }
                        int index = counter / 2;
                        if (index < tutorialCompletionPrompts.length) {
                            if (index == 5) {
                                warlordsPlayer.setCurrency(5000);
                                p.getInventory()
                                 .setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(Component.text("Upgrade Talisman", NamedTextColor.GREEN)).get());
                            }
                            sendTutorialMessage(p, tutorialCompletionPrompts[index]);
                        } else {
                            if (index == 30) {
                                stageSectionCounter.set(10);
                            }
                        }
                    }
                    case 6 -> {
                        switch (stageSectionCounter.get()) {
                            case 1 -> {
                                sendTutorialMessage(p,
                                        "You have now completed the tutorial. If you have any more questions, feel free to ask them in our Discord server! Hope you have fun with your first game of Warlords PvE."
                                );
                                sendTutorialMessage(p, "Link to join Discord Server: https://discord.gg/UMTjJ5Mdc8");
                            }
                            case 3 -> game.setNextState(new EndState(game, null));
                        }
                    }
                }
            }


        }.runTaskTimer(10, 35);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            warlordsPlayer = (WarlordsPlayer) player;
            dummySpawnLocation = new Location(player.getWorld(), 0, 2, 10, 180, 0);
            if (player.getEntity() instanceof Player) {
                player.getGame().setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }
            player.setSpec(Specializations.AVENGER, SkillBoosts.AVENGER_STRIKE);
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        if (stage.get() == 3 && stageSection.get() == 5 && stageSectionCounter.get() >= 11) {
            player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(Component.text("Upgrade Talisman", NamedTextColor.GREEN)).get());
        }
    }

    private void nextStageSection() {
        nextStageSection(stageSection.get() + 1);
    }

    private static void sendTutorialMessage(Player player, String message) {
        player.sendMessage(Component.text(">> ", NamedTextColor.GRAY).append(Component.text(message, NamedTextColor.GREEN)));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
    }

    private void nextStage() {
        //System.out.println("Stage " + stage.get() + " completed");
        stage.getAndIncrement();
        stageSection.set(1);
        stageSectionCounter.set(-1);
    }

    private void spawnTestDummies(Game game, List<Location> locations) {
        testDummies.forEach(warlordsNPC -> game.removePlayer(warlordsNPC.getUuid()));
        testDummies.clear();
        for (Location location : locations) {
            WarlordsNPC testDummy = game.addNPC(Mob.TEST_DUMMY.createMob(warlordsPlayer.getLocation()).toNPC(game, Team.RED, warlordsNPC -> {}));
            testDummy.setTakeDamage(true);
            testDummy.setMaxHealthAndHeal(100);
            testDummy.setRegenTickTimer(Integer.MAX_VALUE);
            testDummy.updateHealth();
            testDummies.add(testDummy);
        }
    }

    private void nextStageSection(int newStageSection) {
        //System.out.println("Stage: " + stage.get() + " Section: " + newStageSection);
        stageSection.set(newStageSection);
        stageSectionCounter.set(-1);
    }

}
