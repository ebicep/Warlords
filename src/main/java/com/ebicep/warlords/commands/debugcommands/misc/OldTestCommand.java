package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OldTestCommand implements CommandExecutor {

    public static List<DatabaseGameBase> GAMES = new ArrayList<>();

    private static double getWeight(float itemScore, ItemTier tier) {
        ItemTier.WeightRange weightRange = tier.weightRange;
        if (itemScore <= 10) {
            return weightRange.max();
        }
        if (45 <= itemScore && itemScore <= 55) {
            return weightRange.normal();
        }
        if (itemScore >= 90) {
            return weightRange.min();
        }
        int weight = weightRange.max();
        double midWeight = weightRange.normal();
        // 10-mid
        double bottomToMidIncrement = getBottomToMidIncrement(weightRange, midWeight);
        // 10-mid
        double midToTopIncrement = getMidToTopIncrement(weightRange, midWeight);
        for (double weightCheck = 10; weightCheck < 45; weightCheck += midToTopIncrement) {
            weight--;
//            System.out.println("Weight: " + weight);
//            System.out.println("WeightCheck: " + weightCheck + " - " + (weightCheck + midToTopIncrement));
            if (weightCheck <= itemScore && itemScore < weightCheck + midToTopIncrement) {
                return weight;
            }
            if (weight < 0) {
                return 100;
            }
        }
        weight--;
        for (double weightCheck = 55; weightCheck < 90; weightCheck += bottomToMidIncrement) {
            weight--;
//            System.out.println("Weight: " + weight);
//            System.out.println("WeightCheck: " + weightCheck + " - " + (weightCheck + bottomToMidIncrement));
            if (weightCheck <= itemScore && itemScore < weightCheck + bottomToMidIncrement) {
                return weight;
            }
            if (weight < 0) {
                return 100;
            }
        }
        return 100;
    }

    private static double getBottomToMidIncrement(ItemTier.WeightRange weightRange, double midWeight) {
//        System.out.println("35 / (" + midWeight + " - " + weightRange.getMin() + " - 1)");
//        System.out.println("35 / " + (midWeight - weightRange.getMin() - 1));
        return 35d / (midWeight - weightRange.min() - 1);
    }

    private static double getMidToTopIncrement(ItemTier.WeightRange weightRange, double midWeight) {
//        System.out.println("35 / (" + weightRange.getMax() + " - " + midWeight + " - 1)");
//        System.out.println("35 / " + (weightRange.getMax() - midWeight - 1));
        return 35d / (weightRange.max() - midWeight - 1);
    }

    private static void extracted(String collection) {
        MongoCollection<Document> usersCollection = DatabaseManager.mongoClient
                .getDatabase("Warlords")
                .getCollection(collection);

//        Bson filter = new Document("_class", "com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvE");
//        Bson update = new Document("$rename", new Document("_class", "com\\.ebicep\\.warlords\\.database\\.repositories\\.games\\.pojos\\.pve\\.wavedefense\\.DatabaseGamePvEWaveDefense"));
        Bson filter = Filters.regex("_class", "com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase");
        Bson update = Updates.set("_class", "com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense");

        UpdateResult result = usersCollection.updateMany(filter, update);
        long modifiedCount = result.getModifiedCount();
        ChatUtils.MessageType.WARLORDS.sendMessage("Modified " + modifiedCount + " documents in " + collection);
    }

    private static Color generateDistinctColor(int prestigeLevel) {
        float hue = (float) prestigeLevel / 100.0f;
        float saturation = 1f;
        float brightness = 1f;
        return Color.getHSBColor(hue, saturation, brightness);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player player) {

            if (!player.isOp()) {
                return true;
            }
        }

        int level = 20;
        if (commandSender instanceof Player player) {

            LocationBuilder locationBuilder = new LocationBuilder(player.getEyeLocation()).right(.1f);
            List<Display> displays = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                double random = .1;
                // generate value between -random and random
                float offset = (float) MathUtils.generateRandomValueBetweenInclusive(-random, random);
                System.out.println(offset);
                double yOffset = MathUtils.generateRandomValueBetweenInclusive(-random, random);
                System.out.println(yOffset);
                LocationBuilder current = locationBuilder
                        .clone()
                        .forward(MathUtils.generateRandomValueBetweenInclusive(-.5, .5))
                        .left(offset)
                        .addY(yOffset);
                BlockDisplay display = player.getWorld().spawn(current, BlockDisplay.class, d -> {
                    Material material = ThreadLocalRandom.current().nextBoolean() ? Material.LIGHT_BLUE_WOOL : Material.BLUE_GLAZED_TERRACOTTA;
                    d.setBlock(material.createBlockData());
//                d.setItemStack(new ItemStack(Material.ECHO_SHARD));
                    BoundingBox boundingBox = player.getBoundingBox();
                    double x = boundingBox.getMaxX() - boundingBox.getMinX();
                    double y = boundingBox.getMaxY() - boundingBox.getMinY();
                    double z = boundingBox.getMaxZ() - boundingBox.getMinZ();
                    double height = player.getHeight();
                    System.out.println(height);
                    float scale = .3f;
                    d.setTransformation(new Transformation(
                            new Vector3f(),
                            new AxisAngle4f(),
                            new Vector3f(scale, scale, scale),
                            new AxisAngle4f(
                                    (float) 1,
                                    ThreadLocalRandom.current().nextFloat(),
                                    ThreadLocalRandom.current().nextFloat(),
                                    ThreadLocalRandom.current().nextFloat()
                            )
                    ));
                    d.setTeleportDuration(3);
                });
                displays.add(display);
                if (i >= 6) {
                    locationBuilder.backward((float) MathUtils.generateRandomValueBetweenInclusive(.8, 1));
                }

                new BukkitRunnable() {
                    int counter = 0;

                    @Override
                    public void run() {
//                        current.forward(2);
//                        display.teleport(current);
                        if (counter++ > 3 * 20) {
                            display.remove();
                            cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 0);
                //   double distance = targetLocation.distance(current) + 1;
                //                new GameRunnable(warlordsNPC.getGame()) {
                //                    final double increment = 1.5;
                //                    int counter = 0;
                //                    double travelled = 0;
                //
                //                    @Override
                //                    public void run() {
                ////                        current.forward(increment);
                ////                        display.teleport(current);
                ////                        travelled += increment;
                ////                        if (counter++ > 3 * 20 || travelled > distance) {
                ////                            display.remove();
                ////                            cancel();
                ////                        }
                //                        display.remove();
                //                    }
                //                }.runTaskLater(teleportDuration);
            }

//            Display display = player.getWorld().spawn(player.getLocation(), BlockDisplay.class, d -> {
//                d.setBlock(Material.BLUE_GLAZED_TERRACOTTA.createBlockData());
////                d.setItemStack(new ItemStack(Material.ECHO_SHARD));
//                BoundingBox boundingBox = player.getBoundingBox();
//                double x = boundingBox.getMaxX() - boundingBox.getMinX();
//                double y = boundingBox.getMaxY() - boundingBox.getMinY();
//                double z = boundingBox.getMaxZ() - boundingBox.getMinZ();
//                double height = player.getHeight();
//                System.out.println(height);
//                d.setTransformation(new Transformation(
//                        new Vector3f(),
//                        new AxisAngle4f(),
//                        new Vector3f(6, .5f, .5f),
//                        new AxisAngle4f()
//                ));
//                d.setTeleportDuration(0);
//            });


//            LocationBuilder location = new LocationBuilder(player.getLocation());
//            location.setYaw(location.getYaw() - 90);
//            location.setPitch(0);
//            final ItemDisplay arrow = player.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
//                itemDisplay.setItemStack(new ItemStack(Material.ARROW));
//                itemDisplay.setTransformation(new Transformation(
//                                new Vector3f(),
//                                new AxisAngle4f((float) Math.toRadians(45 + player.getPitch()), 0, 0, 1),
//                                new Vector3f(1.5f),
//                                new AxisAngle4f()
//                        )
//                );
//                itemDisplay.setTeleportDuration(20);
//            });
////            arrow.teleport(new LocationBuilder(player.getLocation()).forward(20));
//            new BukkitRunnable() {
//                int counter = 0;
//
//                @Override
//                public void run() {
////                    arrow.teleport(location.forward(1));
//                    if (counter++ > 10 * 20) {
//                        arrow.remove();
//                    }
//                }
//            }.runTaskTimer(Warlords.getInstance(), 0, 0);


//            Guardian guard = player.getWorld().spawn(new LocationBuilder(player.getLocation()).forward(10), Guardian.class, guardian -> {
//                guardian.setInvisible(true);
//                guardian.setTarget(player);
//                guardian.setLaser(true);
//                guardian.setLaserTicks(500);
//            });
//            World world = player.getWorld();
//            CustomGuardian guardian = new CustomGuardian(world);
//            guardian.setInvisible(true);
//            guardian.setTarget((LivingEntity) ((CraftEntity) player).getHandle());
//            guardian.setLaser(true);
//            guardian.setLaserTicks(500);
//            guardian.setPos(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
//            ((CraftWorld) world).getHandle().addFreshEntity(guardian, CreatureSpawnEvent.SpawnReason.CUSTOM);

//            craftGuardian.move


//            DatabaseManager.getPlayer(UUID.fromString("931d683f-b7cb-4770-a1b6-d39d89cd2d3a"), databasePlayer -> {
//                ItemEquipMenu.openItemLoadoutMenu(player, null, databasePlayer);
//            });

//            Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
//            for (Option option : game.getOptions()) {
//                if (option instanceof PveOption pveOption) {
//                    for (AbstractMob mob : pveOption.getMobs()) {
//                        if (mob instanceof Chessking) {
//                            Entity entity = mob.getWarlordsNPC().getEntity();
//                            if (entity instanceof LivingEntity living) {
//                                living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
//                            }
//                        }
//                    }
//                }
//            }

//            NPC mount = NPCManager.NPC_REGISTRY.createNPC(EntityType.MAGMA_CUBE, "test");
//            mount.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
////            HologramTrait hologramTrait = mount.getOrAddTrait(HologramTrait.class);
////            hologramTrait.setUseDisplayEntities(true);
////            hologramTrait.setLine(0, "TEST");
////            hologramTrait.setLine(1, LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("HELLO", TextColor.color(123, 123, 123))));
//            NPC npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.MAGMA_CUBE, "test");
//            npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
////            Equipment equipment = mount.getOrAddTrait(Equipment.class);
////            equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.BLACK_BANNER));
//
//            mount.spawn(player.getLocation());
//            npc.spawn(player.getLocation());
//
//            mount.getEntity().addPassenger(npc.getEntity());
//
//            mount.getNavigator().setTarget(player, true);
//            npc.getNavigator().setTarget(player, true);
//
//            UUID uuid = UUID.fromString("9f2b2230-3b2c-4b0f-a141-d7b598e236c7");
//            for (DatabaseGameBase game : GAMES) {
//                System.out.println(game.getDate());
////                for (DatabaseGamePlayerBase basePlayer : game.getBasePlayers()) {
////                    if (basePlayer.getUuid().equals(uuid)) {
////                        DatabaseGameBase.updatePlayerStatsFromTeam(game,
////                                basePlayer,
////                                1
////                        );
////                    }
////                }
//            }

//            GAMES.clear();
//            Warlords.newChain()
//                    .async(() -> {
//                        List<DatabaseGameBase> games = DatabaseManager.gameService
//                                .findAll(GamesCollections.EVENT_PVE)
//                                .stream()
//                                .filter(databaseGameBase -> {
//                                    if (!databaseGameBase.isCounted()) {
//                                        return false;
//                                    }
//                                    Instant date = databaseGameBase.getExactDate();
//                                    return true || date.isAfter(Instant.parse("2023-11-05T05:00:00Z")) && date.isBefore(Instant.parse("2023-11-05T17:00:00Z"));
//                                })
//                                .filter(databaseGameBase -> databaseGameBase instanceof DatabaseGamePvEEventTheAcropolis || databaseGameBase instanceof DatabaseGamePvEEventTartarus)
//                                .toList();
//                        games.forEach(game -> {
//                            for (DatabaseGamePlayerBase basePlayer : game.getBasePlayers()) {
//                                if (basePlayer.getUuid().equals(uuid)) {
////                                    System.out.println(game.getDate());
//                                    GAMES.add(game);
////                                DatabaseGameBase.updatePlayerStatsFromTeam(game,
////                                        basePlayer,
////                                        1
////                                );
//                                    return;
//                                }
//                            }
//                        });
//                    }).sync(() -> {
//                        System.out.println("GAMES: " + GAMES.size());
//                        long points = 0;
//                        for (DatabaseGameBase game : GAMES) {
//                            if (game instanceof DatabaseGamePvEEvent) {
//                                for (DatabaseGamePlayerPvEEvent p : ((DatabaseGamePvEEvent) game).getPlayers()) {
//                                    if (p.getUuid().equals(uuid)) {
//                                        points += p.getPoints();
//                                    }
//                                }
//                            }
//                        }
//                        System.out.println("POINTS: " + points);
//                    }).execute();


//            Skeleton skeleton = player.getWorld().spawn(player.getLocation(), Skeleton.class);
//            player.getWorld().spawn(player.getLocation(), Horse.class, horse -> {
//                horse.addPassenger(skeleton);
//            });

//            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
//            hologramTrait.setUseDisplayEntities(true);
//            hologramTrait.addLine("test");
//            hologramTrait.addLine("hello");
//            hologramTrait.addLine("world");
//            hologramTrait.addLine("123");
//////
//////            npc.data().set(NPC.Metadata.KEEP_CHUNK_LOADED, true);
//////            npc.data().set(NPC.Metadata.ACTIVATION_RANGE, 100);
//////
//            npc.spawn(player.getLocation().add(0, -3, 0));
//
//            npc.getNavigator().setTarget(player, true);
//
//            Waypoints waypoints = npc.getOrAddTrait(Waypoints.class);
//            waypoints.setWaypointProvider("wander");
//            WanderWaypointProvider provider = (WanderWaypointProvider) waypoints.getCurrentProvider();
//            provider.addRegionCentre(player.getLocation());
//            provider.setXYRange(10, 0);


//            DatabaseManager.getPlayer(((Player) commandSender).getUniqueId(), databasePlayer -> {
//                for (Currencies value : Currencies.VALUES) {
//                    System.out.println(value.name + ": " + databasePlayer.getPveStats().getCurrencyValue(value));
//                }
//            });
            //            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
//                System.out.println("reformatting : " + databasePlayer.getName());
//                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//                DatabasePlayerWaveDefenseStats waveDefenseStats = pveStats.getWaveDefenseStats();
//                waveDefenseStats.setEasyStats(pveStats.getEasyStats());
//                waveDefenseStats.setNormalStats(pveStats.getNormalStats());
//                waveDefenseStats.setHardStats(pveStats.getHardStats());
//                waveDefenseStats.setEndlessStats(pveStats.getEndlessStats());
//                waveDefenseStats.setMage(pveStats.getMage());
//                waveDefenseStats.setPaladin(pveStats.getPaladin());
//                waveDefenseStats.setWarrior(pveStats.getWarrior());
//                waveDefenseStats.setRogue(pveStats.getRogue());
//                waveDefenseStats.setShaman(pveStats.getShaman());
//                waveDefenseStats.setPlayerCountStats(pveStats.getPlayerCountStats());
//                waveDefenseStats.merge(pveStats);
//                Warlords.newChain()
//                        .async(() -> {
//                            System.out.println("updating : " + databasePlayer.getName());
//                            DatabaseManager.playerService.update(databasePlayer, PlayersCollections.LIFETIME);
//                        })
//                        .execute();
//            });


//            TextComponent component = Component.text(">>  Achievement Unlocked: ", NamedTextColor.GREEN)
//                                               .append(Component.text(ChallengeAchievements.RETRIBUTION_OF_THE_DEAD.name, NamedTextColor.GOLD)
//                                                                .hoverEvent(HoverEvent.showText(WordWrap.wrapWithNewline(Component.text(ChallengeAchievements.RETRIBUTION_OF_THE_DEAD.description, NamedTextColor.GREEN), 200))))
//                                               .append(Component.text("  <<"));
//            ChatUtils.sendMessageToPlayer(player, component, NamedTextColor.GREEN, true);

//            Component component = Component.text("TEST", NamedTextColor.GREEN)
//                                           .append(Component.newline())
//                                           .append(Component.text("TEST2")
//                                                            .append(Component.newline())
//                                                            .append(Component.text("TEST3"))
//                                                            .append(Component.text("H", NamedTextColor.RED)))
//
//                                           .append(Component.text("TEST4"))
//                                           .append(Component.newline())
//                                           .append(Component.text("TEST5"));
//
//            ChatUtils.sendMessageToPlayer(player, component, NamedTextColor.GREEN, true);
            // System.out.println(LegacyComponentSerializer.legacyAmpersand().serialize(component));


//            Location eyeLocation = player.getEyeLocation();
//            eyeLocation.setPitch(0);
//            for (int i = 0; i < 90; i++) {
//                Location from = new LocationBuilder(eyeLocation)
//                        .addY(2)
//                        .yaw(i * 4)
//                        .forward(5);
//                AbstractChain.spawnChain(from, player.getLocation(), new ItemStack(Material.SPRUCE_FENCE_GATE));
//            }
//
//            EffectUtils.displayParticle(
//                    Particle.SPELL_WITCH,
//                    player.getLocation().subtract(0, 3, 0),
//                    1000,
//                    10,
//                    0,
//                    10,
//                    0
//            );


//            for (int i = 0; i < 10_000; i++) {
//                player.sendMessage(NumberFormat.addCommaAndRound(i));
//            }


//            Bukkit.getOnlinePlayers()
//                  .stream()
//                  .filter(player1 -> player1 != player)
//                  .forEach(otherPlayer -> {
//                      Component displayName = Component.text("test", NamedTextColor.RED);
//                      PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
//                      packet.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME));
//                      packet.getPlayerInfoDataLists().write(
//                              0,
//                              Collections.singletonList(
//                                      new PlayerInfoData(
//                                              new WrappedGameProfile(otherPlayer.getUniqueId(), otherPlayer.getName()),
//                                              0,
//                                              EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
//                                              AdventureComponentConverter.fromComponent(displayName)
//                                      ))
//                      );
//                      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
////                      otherPlayer.getPlayerProfile().setName("tst");
////                      GameProfile gameProfile = com.destroystokyo.paper.profile.CraftPlayerProfile.asAuthlibCopy(otherPlayer.getPlayerProfile());
////                      player.hidePlayer(Warlords.getInstance(), otherPlayer);
////                      ClientboundGameProfilePacket clientboundGameProfilePacket = new ClientboundGameProfilePacket(gameProfile);
////                      ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
////                      ClientboundRemoveMobEffectPacket packet = new ClientboundRemoveMobEffectPacket(otherPlayer.getEntityId(), MobEffects.INVISIBILITY);
////                      serverPlayer.connection.send(packet);
//                  });
        }


//
//        for (DatabasePlayer databasePlayer : DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).values()) {
//            int wins = databasePlayer.getPveStats().getWins();
//            int wdWins = databasePlayer.getPveStats().getWaveDefenseStats().getWins();
//            if (wins != wdWins) {
//                System.out.println(databasePlayer.getName() + " : " + wins + " - " + wdWins + " - LIFETIME");
////                for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
////                    if (activeCollection != PlayersCollections.LIFETIME) {
////                        for (DatabasePlayer dp : DatabaseManager.CACHED_PLAYERS.get(activeCollection).values()) {
////                            int wins2 = dp.getPveStats().getWins();
////                            int wdWins2 = dp.getPveStats().getWaveDefenseStats().getWins();
////                            if (wins2 != wdWins2) {
////                                System.out.println(dp.getName() + " : " + wins2 + " - " + wdWins2 + " - " + activeCollection.name());
////                            }
////                        }
////                    }
////                }
////                System.out.println("-------");
//            }
//        }

//        for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
//            for (DatabasePlayer databasePlayer : DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).values()) {
//                System.out.println("reformatting : " + databasePlayer.getName());
//                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//                DatabasePlayerWaveDefenseStats waveDefenseStats = pveStats.getWaveDefenseStats();
//                waveDefenseStats.setEasyStats(pveStats.getEasyStats());
//                waveDefenseStats.setNormalStats(pveStats.getNormalStats());
//                waveDefenseStats.setHardStats(pveStats.getHardStats());
//                waveDefenseStats.setEndlessStats(pveStats.getEndlessStats());
//                waveDefenseStats.setMage(pveStats.getMage());
//                waveDefenseStats.setPaladin(pveStats.getPaladin());
//                waveDefenseStats.setWarrior(pveStats.getWarrior());
//                waveDefenseStats.setRogue(pveStats.getRogue());
//                waveDefenseStats.setShaman(pveStats.getShaman());
//                waveDefenseStats.setPlayerCountStats(pveStats.getPlayerCountStats());
//                waveDefenseStats.merge(pveStats);
//                Warlords.newChain()
//                        .async(() -> {
//                            System.out.println("updating : " + databasePlayer.getName());
//                            DatabaseManager.playerService.update(databasePlayer, PlayersCollections.LIFETIME);
//                        })
//                        .execute();
//            }

//        }


//        extracted("Games_Information");
//        extracted("Games_Information_PvE");
        ChatUtils.MessageType.WARLORDS.sendMessage("DONE");


//        for (Mobs value : Mobs.values()) {
//            System.out.println(value.createMob.apply(SPAWN_POINT).getName());
//        }
//
//        for (ItemTier tier : ItemTier.VALID_VALUES) {
//            System.out.println("Tier: " + tier.name);
//            System.out.println("Bottom to Mid Increment: " + getBottomToMidIncrement(tier.weightRange, tier.weightRange.getNormal()));
//            System.out.println("Mid to Top Increment: " + getMidToTopIncrement(tier.weightRange, tier.weightRange.getNormal()));
//            //int i = 89;
//            for (int i = 0; i < 100; i += 5) {
//                System.out.println(tier.name + " - " + i + " = " + getWeight(i, tier));
//            }
//            //break;
//        }

//
//        for (GameManager.GameHolder game : Warlords.getGameManager().getGames()) {
//            System.out.println(game.getMap() + " - " + game.getGame());
//        }

//        Warlords.newChain()
//                .async(() -> {
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.LIFETIME
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.SEASON_7
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.WEEKLY
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.DAILY
//                    );
//                }).execute();

//        Warlords.newChain()
//                .async(() -> {
//                    DatabaseManager.playerService.findAll()
//                    for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
//                        System.out.println("-----------------------------------------");
//                        System.out.println("Updating " + activeCollection.name);
//                        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(activeCollection);
//                        for (DatabasePlayer databasePlayer : databasePlayers) {
//                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//                            DatabasePlayerPvEEventStats eventStats = pveStats.getEventStats();
//                            if(eventStats.getPlays() == 0) {
//                                continue;
//                            }
//                            DatabasePlayerPvEEventBoltaroStats boltaroStats = eventStats.getBoltaroStats();
//                            DatabasePlayerPvEEventBoltaroDifficultyStats event = boltaroStats.getEvent(1672131600);
//                            boltaroStats.setLairStats(event.getLairStats());
//                            boltaroStats.setBonanzaStats(event.getBonanzaStats());
//
//                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, activeCollection);
//                        }
//                        System.out.println("-----------------------------------------");
//                    }
//                }).execute();

//        for (Guild guild : GuildManager.GUILDS) {
//            Map<Long, Long> map = guild.getEventStats().get(GameEvents.BOLTARO);
//            if(map == null) {
//                continue;
//            }
//            System.out.println(guild.getName() + " - " + map.getOrDefault(1672131600L,0L));
//            long total = 0;
//            for (GuildPlayer player : guild.getPlayers()) {
//                Map<Long, Long> longMap = player.getEventStats().get(GameEvents.BOLTARO);
//                if(longMap == null) {
//                    continue;
//                }
//                System.out.println("  " + player.getName() + " - " + longMap.getOrDefault(1672131600L, 0L));
//                DatabasePlayer databasePlayer = DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).get(player.getUUID());
//                long cumulative = databasePlayer.getPveStats().getEventStats().getEventPointsCumulative();
//                longMap.put(1672131600L, cumulative);
//                System.out.println("  " + player.getName() + " - " + longMap.get(1672131600L));
//                total += cumulative;
//            }
//            map.put(1672131600L, total);
//            guild.queueUpdate();
//            System.out.println(guild.getName() + " - " + map.get(1672131600L));
//        }


//        Quests quest = Quests.DAILY_300_KA;
//        ChatUtils.sendCenteredMessageWithEvents(player, Component.text()
//                .appendHoverText(ChatColor.GREEN + quest.name, quest.getHoverText())
//                .create()
//        );
//
//        QuestsMenu.openQuestMenu(player);

        //      MasterworksFairManager.currentFair.setStartDate(Instant.now().minus(7, ChronoUnit.DAYS).plus(1, ChronoUnit.MINUTES));
        //System.out.println(Instant.now().minus(7, ChronoUnit.DAYS).minus(2, ChronoUnit.MINUTES));


        return true;
    }
}
