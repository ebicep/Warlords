package com.ebicep.warlords.game.option.pvp;

import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.ShadowStep;
import com.ebicep.warlords.commands.debugcommands.misc.MountCommand;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
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
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class HorseOption implements Option, Listener {

    private static final String ON_USE_ID = "USE_HORSE_ITEM";
    public static ItemStack horseItem = getUpdatedHorseItem();

    public static ItemStack getUpdatedHorseItem() {
        return new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                .name(Component.text("Mount", NamedTextColor.GREEN)
                               .append(Component.text(" - ", NamedTextColor.GRAY))
                               .append(Component.text("Right-Click!", NamedTextColor.YELLOW))
                )
                .lore(Component.text("Cooldown: ", NamedTextColor.GRAY)
                               .append(Component.text(ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.horseCooldown", int.class) + " seconds",
                                       NamedTextColor.GOLD
                               )),
                        Component.empty(),
                        Component.text("Call your steed to assists you in battle", NamedTextColor.GRAY)
                )
                .setOnUseID(ON_USE_ID)
                .get();
    }

    private final HashMap<WarlordsEntity, WarlordsHorse> playerHorses = new HashMap<>();
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.game.registerEvents(this);
        new GameRunnable(game) {

            @Override
            public void run() {
                playerHorses.forEach((warlordsEntity, warlordsHorse) -> {
                    warlordsHorse.tick();
                    float previousCooldown = warlordsHorse.getCurrentCooldown();
                    if (previousCooldown > 0 && warlordsEntity.getEntity().getVehicle() == null) {
                        warlordsHorse.setCurrentCooldown(previousCooldown - .05f);
                    }
                    float currentCooldown = warlordsHorse.getCurrentCooldown();
                    if (((int) previousCooldown != (int) currentCooldown || previousCooldown > 0 && currentCooldown < 0) &&
                            warlordsEntity instanceof WarlordsPlayer warlordsPlayer &&
                            warlordsEntity.getEntity() instanceof Player player
                    ) { // only update if second changed
                        updateInventory(warlordsPlayer, player);
                    }
                });
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            getHorseForPlayer(player);
        }
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        playerHorses.values().forEach(WarlordsHorse::kill);
        playerHorses.clear();
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        WarlordsHorse horse = getHorseForPlayer(warlordsPlayer);
        PlayerInventory inventory = player.getInventory();
        if (horse.getCurrentCooldown() > 0) {
            inventory.setItem(7, new ItemStack(Material.IRON_HORSE_ARMOR, (int) horse.getCurrentCooldown() + 1));
        } else {
            inventory.setItem(7, horseItem);
        }
    }

    public WarlordsHorse getHorseForPlayer(WarlordsEntity warlordsEntity) {
        return playerHorses.computeIfAbsent(warlordsEntity, k -> new WarlordsHorse());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        Location location = player.getLocation();
        WarlordsEntity wp = Warlords.getPlayer(player);

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        ItemStack itemHeld = player.getEquipment().getItemInMainHand();
        if (wp == null || !wp.getGame().equals(game) || !wp.isAlive() || wp.getGame().isFrozen()) {
            return;
        }
        if (player.getVehicle() != null) {
            return;
        }
        ItemMeta itemMeta = itemHeld.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String onUseID = itemMeta.getPersistentDataContainer().get(ItemBuilder.ON_USE_NAMESPACED_KEY, PersistentDataType.STRING);
        if (!Objects.equals(onUseID, ON_USE_ID)) {
            return;
        }
        if (!LocationUtils.isMountableZone(location) || LocationUtils.blocksInFrontOfLocation(location)) {
            player.sendMessage(Component.text("You can't mount here!", NamedTextColor.RED));
            return;
        }
        double distance = LocationUtils.getDistance(player, .25);
        if (distance >= 2) {
            player.sendMessage(Component.text("You can't mount in the air!", NamedTextColor.RED));
            return;
        }
        if (wp.getCarriedFlag() != null) {
            player.sendMessage(Component.text("You can't mount while holding the flag!", NamedTextColor.RED));
            return;
        }
        WarlordsHorse warlordsHorse = activateHorseForPlayer(wp, true);
    }

    @Nullable
    public static WarlordsHorse activateHorseForPlayer(WarlordsEntity warlordsEntity, boolean checkCooldown) {
        if (!(warlordsEntity instanceof WarlordsPlayer)) {
            return null;
        }
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return null;
        }
        for (Option option : warlordsEntity.getGame().getOptions()) {
            if (option instanceof HorseOption horseOption) {
                WarlordsHorse warlordsHorse = horseOption.getHorseForPlayer(warlordsEntity);
                if (checkCooldown && warlordsHorse.getCurrentCooldown() > 0) {
                    return null;
                }
                WarlordsPlayerHorseEvent horseEvent = new WarlordsPlayerHorseEvent(warlordsEntity);
                Bukkit.getPluginManager().callEvent(horseEvent);
                if (horseEvent.isCancelled()) {
                    return null;
                }
                player.playSound(player.getLocation(), "mountup", 1, 1);
                if (!warlordsEntity.isDisableCooldowns() && warlordsHorse != null) {
                    warlordsHorse.setCurrentCooldown(warlordsHorse.getCooldown().getCalculatedValue());
                }
                warlordsHorse.spawn(player);
                return warlordsHorse;
            }
        }
        return null;
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onDamageHealingEvent(WarlordsDamageHealingEvent event) {
//        if (event.isCancelled()) {
//            getHorseForPlayer(event.getWarlordsEntity().getUuid()).kill();
//        }
//    }

    @EventHandler
    public void onDamageHealingFinalEvent(WarlordsDamageHealingFinalEvent event) {
        if (!event.isDamageInstance()) {
            return;
        }
        if (event.getInstanceFlags().contains(InstanceFlags.NO_DISMOUNT)) {
            return;
        }
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        if (event.getSource().getTeam() == warlordsEntity.getTeam()) {
            return;
        }
        getHorseForPlayer(warlordsEntity).damage(event.getValue());
    }

    @EventHandler(ignoreCancelled = true)
    public void onWarlordsDeathEvent(WarlordsDeathEvent event) {
        getHorseForPlayer(event.getWarlordsEntity()).kill();
    }

    @EventHandler
    public void onWarlordsPlayerClassRightClickEvent(WarlordsPlayerClassRightClickEvent event) {
        getHorseForPlayer(event.getWarlordsEntity()).kill();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
        if (!event.isCancelled() && event.getAbility() instanceof ShadowStep) {
            getHorseForPlayer(event.getWarlordsEntity()).kill();
        }
    }

    public static class WarlordsHorse {

        private static final ItemStack SADDLE = new ItemStack(Material.SADDLE);

        private final FloatModifiable cooldown = new FloatModifiable(ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.horseCooldown", int.class));
        private final float speed = .32f;
        private final FloatModifiable health = new FloatModifiable(0);
        private float currentCooldown = 0;
        private float currentHealth = 0;
        private Horse horse;
        private NPC npc;

        public void tick() {
            cooldown.tick();
            health.tick();
        }

        public void spawn(Player player) {
            this.currentHealth = health.getCalculatedValue();
            UUID uuid = player.getUniqueId();
            if (MountCommand.PLAYER_MOUNT_TYPE.containsKey(uuid)) {
                EntityType entityType = MountCommand.PLAYER_MOUNT_TYPE.get(uuid);
                this.npc = NPCManager.NPC_REGISTRY.createNPC(entityType, "MOUNT");
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
                if (npc.getEntity() instanceof Horse h) {
                    h.setJumpStrength(0);
                    h.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health.getCalculatedValue() == 0 ? 0 : 40);
                }
                controllable.mount(player);
            } else {
                CustomHorse customHorse = new CustomHorse(player.getLocation());
                this.horse = (Horse) customHorse.getBukkitEntity();
                horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health.getCalculatedValue() == 0 ? 0 : 40);
                horse.setTamed(true);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.setOwner(player);
                horse.setJumpStrength(0);
                horse.setColor(Horse.Color.BROWN);
                horse.setStyle(Horse.Style.NONE);
                horse.setAdult();
                horse.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
                ((CraftWorld) player.getWorld()).getHandle().addFreshEntity(customHorse, CreatureSpawnEvent.SpawnReason.CUSTOM);
                horse.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
                horse.addPassenger(player); // not sure if including this in function above will cause issues
            }
            updateHealthDisplay();
        }

        private void updateHealthDisplay() {
            float maxHealth = health.getCalculatedValue();
            if (maxHealth == 0) {
                return;
            }
            float newHealth = currentHealth / maxHealth * 40;
            if (newHealth <= 0) {
                newHealth = 1;
            }
            if (horse != null) {
                horse.setHealth(newHealth);
            } else if (npc != null) {
                Entity entity = npc.getEntity();
                if (entity instanceof Damageable damageable) {
                    damageable.setHealth(newHealth);
                }
            }
        }

        public void kill() {
            if (horse != null) {
                horse.remove();
            }
            if (npc != null) {
                npc.despawn();
            }
        }

        public void damage(float damage) {
            currentHealth -= damage;
            if (currentHealth <= 0) {
                if (horse != null) {
                    horse.remove();
                } else if (npc != null) {
                    npc.despawn();
                }
            }
            updateHealthDisplay();
        }

        public FloatModifiable getHealth() {
            return health;
        }

        public FloatModifiable getCooldown() {
            return cooldown;
        }

        public float getCurrentCooldown() {
            return currentCooldown;
        }

        public void setCurrentCooldown(float currentCooldown) {
            this.currentCooldown = currentCooldown;
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
