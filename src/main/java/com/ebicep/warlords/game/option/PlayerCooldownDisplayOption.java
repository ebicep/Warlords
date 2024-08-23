package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCooldownDisplayOption implements Option, Listener {

    public static boolean enabled = true;
    private static final ItemStack GRAY_DYE = new ItemStack(Material.GRAY_DYE);
    private final Map<WarlordsEntity, CooldownData> playerSettings = new HashMap<>();
//    private final Map<Integer, EntityData> entityDataByID = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                playerSettings.forEach((warlordsEntity, cooldownData) -> {
                    if (ticksElapsed % 10 == 0) {
                        cooldownData.cooldowns.update(warlordsEntity);
                    }
                    cooldownData.cooldowns.teleport(warlordsEntity);
                });
                if (ticksElapsed % 40 == 0) {
                    Map<Team, List<WarlordsEntity>> warlordsEntityByTeam = new HashMap<>();
                    playerSettings.forEach((warlordsEntity, cooldownData) -> warlordsEntityByTeam.computeIfAbsent(warlordsEntity.getTeam(), k -> new ArrayList<>())
                                                                                                 .add(warlordsEntity));
                    playerSettings.forEach((warlordsEntity, cooldownData) -> {
                        if (!(warlordsEntity.getEntity() instanceof Player player)) {
                            return;
                        }
                        warlordsEntityByTeam.forEach((team, warlordsEntities) -> {
                            boolean sameTeam = team == warlordsEntity.getTeam();
                            boolean shouldSee = sameTeam && cooldownData.seeTeammates || !sameTeam && cooldownData.seeEnemies;
                            warlordsEntities.forEach(we -> {
                                CooldownData otherData = playerSettings.get(we);
                                if (otherData == null) {
                                    return;
                                }
                                boolean samePlayer = warlordsEntity == we;
                                otherData.cooldowns.cooldownEntities.forEach(cooldownEntities -> {
                                    if (!samePlayer && shouldSee && !player.canSee(cooldownEntities.itemDisplay)) {
                                        player.showEntity(Warlords.getInstance(), cooldownEntities.itemDisplay);
                                    } else if (samePlayer || !shouldSee && player.canSee(cooldownEntities.itemDisplay)) {
                                        player.hideEntity(Warlords.getInstance(), cooldownEntities.itemDisplay);
                                    }
                                    if (!samePlayer && shouldSee && !player.canSee(cooldownEntities.textDisplay)) {
                                        player.showEntity(Warlords.getInstance(), cooldownEntities.textDisplay);
                                    } else if (samePlayer || !shouldSee && player.canSee(cooldownEntities.textDisplay)) {
                                        player.hideEntity(Warlords.getInstance(), cooldownEntities.textDisplay);
                                    }
                                });
                            });
                        });
                    });
                }
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        playerSettings.clear();
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            playerSettings.computeIfAbsent(player, k -> new CooldownData());
        }
    }

    @EventHandler
    public void onPlayerDeath(WarlordsDeathEvent event) {
        CooldownData dead = playerSettings.get(event.getWarlordsEntity());
        if (dead == null) {
            return;
        }
        dead.cooldowns.cooldownEntities.forEach(Cooldowns.CooldownEntities::remove);
    }

    public Map<WarlordsEntity, CooldownData> getPlayerSettings() {
        return playerSettings;
    }

    public record EntityData(Entity entity, WarlordsEntity warlordsEntity) {

    }

    public static class CooldownData {

        private final Cooldowns cooldowns = new Cooldowns();
        private boolean seeTeammates = true;
        private boolean seeEnemies;

        public boolean isSeeTeammates() {
            return seeTeammates;
        }

        public void setSeeTeammates(boolean seeTeammates) {
            this.seeTeammates = seeTeammates;
        }

        public boolean isSeeEnemies() {
            return seeEnemies;
        }

        public void setSeeEnemies(boolean seeEnemies) {
            this.seeEnemies = seeEnemies;
        }
    }

    static class Cooldowns {

        private static final double SPACE_BETWEEN_COOLDOWN = .5;

        private final List<CooldownEntities> cooldownEntities = new ArrayList<>();

        public void update(WarlordsEntity warlordsEntity) {
            if (warlordsEntity.isDead()) {
                return;
            }
            for (int i = 1; i < warlordsEntity.getAbilities().size(); i++) {
                int cooldownIndex = i - 1;
                AbstractAbility ab = warlordsEntity.getAbilities().get(i);
                CooldownEntities cooldownEntity;
                if (cooldownIndex < cooldownEntities.size()) {
                    cooldownEntity = cooldownEntities.get(cooldownIndex);
                } else {
                    cooldownEntity = createCooldownEntities(warlordsEntity.getLocation(), ab);
//                    cooldownEntity.addTo(warlordsEntity, entityDataByID);
                    cooldownEntities.add(cooldownEntity);
                }
                if (cooldownEntity.ability != ab || !cooldownEntity.itemDisplay.isValid() || !cooldownEntity.textDisplay.isValid()) {
                    cooldownEntity.remove();
//                    cooldownEntity.removeFrom(entityDataByID);
                    cooldownEntity = createCooldownEntities(warlordsEntity.getLocation(), ab);
//                    cooldownEntity.addTo(warlordsEntity, entityDataByID);
                    cooldownEntities.set(cooldownIndex, cooldownEntity);
                }
                boolean onCooldown = ab.getCurrentCooldown() > 0;
                ItemDisplay itemDisplay = cooldownEntity.itemDisplay;
                if (itemDisplay.getItemStack() == null || itemDisplay.getItemStack().getType() != ab.getAbilityIcon().getType()) {
                    itemDisplay.setItemStack(onCooldown ? GRAY_DYE : ab.getAbilityIcon());
                } else if (onCooldown) {
                    itemDisplay.setItemStack(GRAY_DYE);
                }
                TextDisplay textDisplay = cooldownEntity.textDisplay;
                textDisplay.text(onCooldown ? Component.text(ab.getCurrentCooldownItem()) : null);
            }
            // remove any extra cooldown entities
            for (int i = cooldownEntities.size() - 1; i >= warlordsEntity.getAbilities().size() - 1; i--) {
                CooldownEntities cooldownEntity = cooldownEntities.remove(i);
                cooldownEntity.remove();
//                cooldownEntity.removeFrom(entityDataByID);
            }
        }

        private CooldownEntities createCooldownEntities(Location location, AbstractAbility ability) {
            ItemDisplay itemDisplay = location.getWorld().spawn(
                    new LocationBuilder(location)
                            .pitch(0),
                    ItemDisplay.class,
                    d -> {
                        d.setTransformation(new Transformation(
                                new Vector3f(),
                                new AxisAngle4f(),
                                new Vector3f(.5f, .5f, .1f),
                                new AxisAngle4f()
                        ));
                        d.setItemStack(ability.getAbilityIcon());
                        d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
                        d.setTeleportDuration(3);
                        d.setBrightness(new Display.Brightness(15, 15));
                        d.setBillboard(Display.Billboard.VERTICAL);
                        d.setViewRange(.4f);
                    }
            );
            TextDisplay textDisplay = location.getWorld().spawn(
                    new LocationBuilder(location)
                            .pitch(0),
                    TextDisplay.class,
                    d -> {
                        d.setTransformation(new Transformation(
                                new Vector3f(),
                                new AxisAngle4f(),
                                new Vector3f(1f),
                                new AxisAngle4f()
                        ));
//                        d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                        d.setShadowed(false);
                        d.text(Component.empty());
                        d.setTeleportDuration(3);
                        d.setBrightness(new Display.Brightness(15, 15));
                        d.setBillboard(Display.Billboard.VERTICAL);
                        d.setViewRange(.4f);
                    }
            );
            return new CooldownEntities(ability, itemDisplay, textDisplay);
        }

        private void teleport(WarlordsEntity warlordsEntity) {
            Location location = warlordsEntity.getLocation();
            int halfSize = cooldownEntities.size() / 2;
            double y = warlordsEntity.getLocation().getY() + 3;
            double x = -((halfSize * .375) + ((halfSize - 1) * SPACE_BETWEEN_COOLDOWN)) + .5;
            for (CooldownEntities cooldownEntity : cooldownEntities) {
                cooldownEntity.translateX((float) x);
                cooldownEntity.teleport(new Location(location.getWorld(), location.getX(), y, location.getZ()));
                x += SPACE_BETWEEN_COOLDOWN;
            }
        }

        public record CooldownEntities(AbstractAbility ability, ItemDisplay itemDisplay, TextDisplay textDisplay) {

            public void translateX(float translation) {
                Transformation itemDisplayTransformation = itemDisplay.getTransformation();
//                float random = ThreadLocalRandom.current().nextFloat(.0001f);
                itemDisplay.setTransformation(new Transformation(
                        new Vector3f(translation, 0, 0),
                        itemDisplayTransformation.getLeftRotation(),
                        itemDisplayTransformation.getScale(),
                        itemDisplayTransformation.getRightRotation()
                ));
                Transformation textDisplayTransformation = textDisplay.getTransformation();
                textDisplay.setTransformation(new Transformation(
                        new Vector3f(translation, 0, 0),
                        textDisplayTransformation.getLeftRotation(),
                        textDisplayTransformation.getScale(),
                        textDisplayTransformation.getRightRotation()
                ));
            }

            public void teleport(Location location) {
                itemDisplay.teleport(location);
                textDisplay.teleport(location.clone().add(0, .25f, 0));
            }

            public void remove() {
                itemDisplay.remove();
                textDisplay.remove();
            }

            public void addTo(WarlordsEntity displayFor, Map<Integer, EntityData> entityDataByID) {
                entityDataByID.put(itemDisplay.getEntityId(), new EntityData(itemDisplay, displayFor));
                entityDataByID.put(textDisplay.getEntityId(), new EntityData(textDisplay, displayFor));
            }

            public void removeFrom(Map<Integer, EntityData> entityDataByID) {
                entityDataByID.remove(itemDisplay.getEntityId());
                entityDataByID.remove(textDisplay.getEntityId());
            }

        }

    }

    /*

    //        packetListeners.add(new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
//                Player player = event.getPlayer();
//                WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(event.getPacket());
//                List<WrappedDataValue> packedItems = metadata.getPackedItems();
//                WrappedDataValue transformation = packedItems
//                        .stream()
//                        .filter(wrappedWatchableObject -> wrappedWatchableObject.getIndex() == 11) // https://wiki.vg/Entity_metadata#Entity_Metadata_Format:~:text=0-,11,-Vector3%20(26)
//                        .findAny()
//                        .orElse(null);
//                if (transformation != null) {
//                    int entityID = event.getPacket().getIntegers().read(0);
//                    Entity entity = entitiesByID.get(entityID);
//                    if (entity == null) {
//                        return;
//                    }
//                }
//            }
//        });
//        packetListeners.add(new PacketAdapter(Warlords.getInstance(),
//                ListenerPriority.NORMAL,
//                PacketType.Play.Server.SPAWN_ENTITY,
//                PacketType.Play.Server.ENTITY_TELEPORT,
//                PacketType.Play.Server.ENTITY_METADATA
//        ) {
//            @Override
//            public void onPacketSending(PacketEvent event) {
////                handleCancelPacket(event);
//            }
//        });
//        packetListeners.forEach(PacketUtils.PROTOCOL_MANAGER::addPacketListener);


    private void handleCancelPacket(PacketEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity == null) {
            return;
        }
        CooldownData cooldownData = playerSettings.get(warlordsEntity);
        if (cooldownData == null) {
            return;
        }
        int entityID = event.getPacket().getIntegers().read(0);
        EntityData entityData = entityDataByID.get(entityID);
        if (entityData == null) {
            return;
        }
        WarlordsEntity entityDataFor = entityData.warlordsEntity;
        if (entityDataFor == warlordsEntity) {
            String str = "";
            if (entityData.entity instanceof ItemDisplay itemDisplay) {
                str += itemDisplay.getItemStack();
            } else if (entityData.entity instanceof TextDisplay textDisplay) {
                str += textDisplay.getText();
            }
            System.out.println(warlordsEntity.getName() + " cancelling same player: " + event.getPacketType() + " - " + str);
            event.setCancelled(true);
            return;
        }
        Team entityTeam = entityDataFor.getTeam();
        if (!cooldownData.seeTeammates && entityTeam == warlordsEntity.getTeam() ||
                !cooldownData.seeEnemies && entityTeam != warlordsEntity.getTeam()
        ) {
            String str = "";
            if (entityData.entity instanceof ItemDisplay itemDisplay) {
                str += itemDisplay.getItemStack();
            } else if (entityData.entity instanceof TextDisplay textDisplay) {
                str += textDisplay.getText();
            }
            System.out.println(warlordsEntity.getName() + " cancelling team: " + event.getPacketType() + " - " + str);
            event.setCancelled(true);
        }
    }

     */


}
