package com.ebicep.warlords.game.option.pvp;

import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.MountCommand;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHorseEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.Controllable;
import net.citizensnpcs.trait.HorseModifiers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class HorseOption implements Option, Listener {

    public static final ItemStack HORSE_ITEM = new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
            .name(Component.text("Mount", NamedTextColor.GREEN)
                           .append(Component.text(" - ", NamedTextColor.GRAY))
                           .append(Component.text("Right-Click!", NamedTextColor.YELLOW))
            )
            .lore(Component.text("Cooldown: ", NamedTextColor.GRAY)
                           .append(Component.text("15 seconds", NamedTextColor.AQUA)),
                    Component.empty(),
                    Component.text("Call your steed to assists you in battle", NamedTextColor.GRAY)
            )
            .get();
    private final HashMap<UUID, WarlordsHorse> playerHorses = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                // Decrementing mount cooldown.
                game.warlordsPlayers().forEach(wp -> {
                    if (wp.getHorseCooldown() > 0 && !wp.getEntity().isInsideVehicle()) {
                        wp.setHorseCooldown(wp.getHorseCooldown() - .05f);
                        Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;
                        if (player != null) {
                            updateInventory(wp, player);
                        }
                    }
                });
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            playerHorses.put(player.getUuid(), new WarlordsHorse());
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        if (warlordsPlayer.getHorseCooldown() > 0) {
            player.getInventory()
                  .setItem(7, new ItemStack(Material.IRON_HORSE_ARMOR, (int) warlordsPlayer.getHorseCooldown() + 1));
        } else {
            player.getInventory().setItem(7, HORSE_ITEM);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        Location location = player.getLocation();
        WarlordsEntity wp = Warlords.getPlayer(player);

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            ItemStack itemHeld = player.getEquipment().getItemInMainHand();
            int heldItemSlot = player.getInventory().getHeldItemSlot();
            if (wp == null || !wp.isAlive() || wp.getGame().isFrozen()) {
                return;
            }
            if (itemHeld.getType() != Material.GOLDEN_HORSE_ARMOR) {
                return;
            }
            if (heldItemSlot != 7 || player.getVehicle() != null || !(wp.getHorseCooldown() <= 0)) {
                return;
            }
            if (!LocationUtils.isMountableZone(location) || LocationUtils.blocksInFrontOfLocation(location)) {
                player.sendMessage(Component.text("You can't mount here!", NamedTextColor.RED));
                return;
            }
            double distance = LocationUtils.getDistance(player, .25);
            if (distance >= 2) {
                player.sendMessage(Component.text("You can't mount in the air!", NamedTextColor.RED));
            } else if (wp.getCarriedFlag() != null) {
                player.sendMessage(Component.text("You can't mount while holding the flag!", NamedTextColor.RED));
            } else {
                player.playSound(player.getLocation(), "mountup", 1, 1);
                WarlordsHorse warlordsHorse = activateHorseForPlayer(wp);
                if (!wp.isDisableCooldowns() && warlordsHorse != null) {
                    float cooldown = warlordsHorse.getCooldown();
                    wp.setHorseCooldown(cooldown);
                }
            }
        }
    }

    @Nullable
    public static WarlordsHorse activateHorseForPlayer(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity instanceof WarlordsPlayer)) {
            return null;
        }
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return null;
        }
        for (Option option : warlordsEntity.getGame().getOptions()) {
            if (option instanceof HorseOption) {
                HashMap<UUID, WarlordsHorse> horses = ((HorseOption) option).getPlayerHorses();
                WarlordsHorse warlordsHorse = horses.computeIfAbsent(warlordsEntity.getUuid(), k -> new WarlordsHorse());
                WarlordsPlayerHorseEvent horseEvent = new WarlordsPlayerHorseEvent(warlordsEntity);
                Bukkit.getPluginManager().callEvent(horseEvent);
                if (horseEvent.isCancelled()) {
                    return null;
                }
                warlordsHorse.spawn(player);
                return warlordsHorse;
            }
        }
        return null;
    }

    public HashMap<UUID, WarlordsHorse> getPlayerHorses() {
        return playerHorses;
    }

    public static class WarlordsHorse {

        private static final ItemStack SADDLE = new ItemStack(Material.SADDLE);
        private final int cooldown = 15;
        private final float speed = .32f;

        public void spawn(Player player) {
            UUID uuid = player.getUniqueId();
            if (MountCommand.PLAYER_MOUNT_TYPE.containsKey(uuid)) {
                EntityType entityType = MountCommand.PLAYER_MOUNT_TYPE.get(uuid);
                NPC npc = NPCManager.NPC_REGISTRY.createNPC(entityType, "MOUNT");
                npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
                npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) n -> 0f);
                npc.data().set(NPC.Metadata.FLYABLE, false);
                HorseModifiers horseModifiers = npc.getOrAddTrait(HorseModifiers.class);
                horseModifiers.setSaddle(SADDLE);
                horseModifiers.setColor(Horse.Color.BROWN);
                horseModifiers.setStyle(Horse.Style.NONE);
                Owner owner = npc.getOrAddTrait(Owner.class);
                owner.setOwner(player);
                Controllable controllable = npc.getOrAddTrait(Controllable.class);
                controllable.setEnabled(true);
                controllable.setOwnerRequired(true);
                NavigatorParameters defaultParameters = npc.getNavigator().getDefaultParameters();
                defaultParameters.speedModifier(MountCommand.getSpeed(entityType));

                npc.spawn(player.getLocation());
                if (npc.getEntity() instanceof Horse horse) {
                    horse.setJumpStrength(0);
                }
                controllable.mount(player);
            } else {
                CustomHorse customHorse = new CustomHorse(player.getLocation());
                Horse horse = (Horse) customHorse.getBukkitEntity();
                horse.setTamed(true);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.setOwner(player);
                horse.setJumpStrength(0);
                horse.setColor(Horse.Color.BROWN);
                horse.setStyle(Horse.Style.NONE);
                horse.setAdult();
                horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
                ((CraftWorld) player.getWorld()).getHandle().addFreshEntity(customHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
                horse.addPassenger(player); // not sure if including this in function above will cause issues
            }
        }

        public int getCooldown() {
            return cooldown;
        }

//        public class GroundController implements Controllable.MovementController {
//            private int jumpTicks = 0;
//            private double speed = 0.07;
//            private static final float AIR_SPEED = 0.5F;
//            private static final float GROUND_SPEED = 0.5F;
//            private static final float JUMP_VELOCITY = 0.5F;
//            private final Controllable controllable;
//
//            public GroundController(Controllable controllable) {
//                this.controllable = controllable1;
//            }
//
//            public void leftClick(PlayerInteractEvent event) {
//            }
//
//            public void rightClick(PlayerInteractEvent event) {
//            }
//
//            public void rightClickEntity(NPCRightClickEvent event) {
//                controllable.enterOrLeaveVehicle(event.getClicker());
//            }
//
//            public void run(Player rider) {
//                boolean onGround = NMS.isOnGround(controllable.getNPC().getEntity());
//                float speedMod = controllable.getNPC().getNavigator().getDefaultParameters().modifiedSpeed(onGround ? 0.5F : 0.5F);
//                if (!Util.isHorse(controllable.getNPC().getEntity().getType())) {
//                    this.speed = controllable.updateHorizontalSpeed(controllable.getNPC().getEntity(), rider, this.speed, speedMod, Settings.Setting.MAX_CONTROLLABLE_GROUND_SPEED.asDouble());
//                }
//
//                boolean shouldJump = NMS.shouldJump(rider);
//                if (shouldJump) {
//                    if (onGround && this.jumpTicks == 0) {
//                        controllable.npc.getEntity().setVelocity(controllable.npc.getEntity().getVelocity().setY(0.5F));
//                        this.jumpTicks = 10;
//                    }
//                } else {
//                    this.jumpTicks = 0;
//                }
//
//                this.jumpTicks = Math.max(0, this.jumpTicks - 1);
//                controllable.setMountedYaw(controllable.npc.getEntity());
//            }
//        }

    }

}
