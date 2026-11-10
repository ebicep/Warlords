package com.ebicep.warlords.game.option.pvp;

import com.ebicep.customentities.nms.MountEntitySetup;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.mounts.MountArmor;
import com.ebicep.warlords.player.general.mounts.MountProfile;
import com.ebicep.warlords.player.general.mounts.MountType;
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
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

public class MountOption implements Option, Listener {

    public static final NamespacedKey MOUNT_KEY = new NamespacedKey(Warlords.getInstance(), "warlords_mount");

    private static final String ON_USE_ID = "USE_HORSE_ITEM";
    private static final ItemStack[] IRON_HORSE_ARMOR_BY_AMOUNT = new ItemStack[17];
    public static ItemStack mountItem = getUpdatedMountItem();

    public static boolean isMount(@Nullable Entity entity) {
        return entity != null && entity.getPersistentDataContainer().has(MOUNT_KEY, PersistentDataType.BYTE);
    }

    public static void markMount(@Nonnull Entity entity) {
        entity.getPersistentDataContainer().set(MOUNT_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    public static ItemStack getUpdatedMountItem() {
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

    private static ItemStack ironHorseArmorForAmount(int amount) {
        int index = Math.min(Math.max(amount, 1), 16);
        ItemStack cached = IRON_HORSE_ARMOR_BY_AMOUNT[index];
        if (cached == null) {
            cached = new ItemBuilder(Material.IRON_HORSE_ARMOR, index)
                    .name(Component.empty())
                    .setOnUseID(ON_USE_ID)
                    .get();
            IRON_HORSE_ARMOR_BY_AMOUNT[index] = cached;
        }
        return cached;
    }

    private final HashMap<WarlordsEntity, WarlordsMount> playerMounts = new HashMap<>();
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.game.registerEvents(this);
        new GameRunnable(game) {

            @Override
            public void run() {
                playerMounts.forEach((warlordsEntity, warlordsMount) -> {
                    warlordsMount.tick();
                    float previousCooldown = warlordsMount.getCurrentCooldown();
                    if (previousCooldown > 0 && warlordsEntity.getEntity().getVehicle() == null) {
                        warlordsMount.setCurrentCooldown(previousCooldown - .05f);
                    }
                    float currentCooldown = warlordsMount.getCurrentCooldown();
                    if (((int) previousCooldown != (int) currentCooldown || previousCooldown > 0 && currentCooldown <= 0) &&
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
            getMountForPlayer(player);
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        WarlordsMount mount = getMountForPlayer(warlordsPlayer);
        PlayerInventory inventory = player.getInventory();
        if (mount.getCurrentCooldown() > 0) {
            inventory.setItem(7, ironHorseArmorForAmount((int) mount.getCurrentCooldown() + 1));
        } else {
            inventory.setItem(7, mountItem);
        }
    }

    public WarlordsMount getMountForPlayer(WarlordsEntity warlordsEntity) {
        return playerMounts.computeIfAbsent(warlordsEntity, k -> new WarlordsMount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDismount(EntityDismountEvent event) {
        // VehicleExitEvent only fires for Bukkit vehicles. Ravagers and hoglins are not vehicles, so they would stay behind.
        if (isMount(event.getDismounted())) {
            event.getDismounted().remove();
        }
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
        activateMountForPlayer(wp, true);
    }

    @Nullable
    public static WarlordsMount activateMountForPlayer(WarlordsEntity warlordsEntity, boolean checkCooldown) {
        if (!(warlordsEntity instanceof WarlordsPlayer)) {
            return null;
        }
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return null;
        }
        for (Option option : warlordsEntity.getGame().getOptions()) {
            if (option instanceof MountOption mountOption) {
                WarlordsMount warlordsMount = mountOption.getMountForPlayer(warlordsEntity);
                if (checkCooldown && warlordsMount.getCurrentCooldown() > 0) {
                    return null;
                }
                WarlordsPlayerHorseEvent horseEvent = new WarlordsPlayerHorseEvent(warlordsEntity);
                Bukkit.getPluginManager().callEvent(horseEvent);
                if (horseEvent.isCancelled()) {
                    return null;
                }
                player.playSound(player.getLocation(), "mountup", 1, 1);
                if (!warlordsEntity.isDisableCooldowns()) {
                    warlordsMount.setCurrentCooldown(warlordsMount.getCooldown().getCalculatedValue());
                }
                warlordsMount.spawn(player);
                return warlordsMount;
            }
        }
        return null;
    }

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
        getMountForPlayer(warlordsEntity).damage(event.getValue());
    }

    @EventHandler(ignoreCancelled = true)
    public void onWarlordsDeathEvent(WarlordsDeathEvent event) {
        getMountForPlayer(event.getWarlordsEntity()).kill();
    }

    @EventHandler
    public void onWarlordsPlayerClassRightClickEvent(WarlordsPlayerClassRightClickEvent event) {
        WarlordsHorseAbilityDismountEvent dismountEvent = new WarlordsHorseAbilityDismountEvent(
                event.getWarlordsEntity(),
                event.getAbility()
        );
        Bukkit.getPluginManager().callEvent(dismountEvent);
        if (!dismountEvent.isCancelled()) {
            getMountForPlayer(event.getWarlordsEntity()).kill();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
        if (!event.isCancelled() && event.getAbility() instanceof ShadowStep) {
            getMountForPlayer(event.getWarlordsEntity()).kill();
        }
    }

    public static class WarlordsMount {

        private static final ItemStack SADDLE = new ItemStack(Material.SADDLE);

        private final FloatModifiable cooldown = new FloatModifiable(ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "ctf.horseCooldown", int.class));
        private final FloatModifiable health = new FloatModifiable(0);
        private float currentCooldown = 0;
        private float currentHealth = 0;
        private LivingEntity mount;
        private NPC npc;

        public void tick() {
            cooldown.tick();
            health.tick();
            Entity ridden = mount != null ? mount : npc != null ? npc.getEntity() : null;
            if (ridden != null && ridden.isValid() && !ridden.getPassengers().isEmpty() && !LocationUtils.isMountableZone(ridden)) {
                kill();
            }
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
                if (npc.getEntity() instanceof Horse horse) {
                    horse.setJumpStrength(0);
                    horse.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health.getCalculatedValue() == 0 ? 0 : 40);
                }
                if (npc.getEntity() != null) {
                    markMount(npc.getEntity());
                }
                controllable.mount(player);
            } else {
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
                boolean hasSupporter = Permissions.isSupporter(player);
                MountProfile mounts = databasePlayer.getMounts();
                if (mounts.applySupporterDefaults(hasSupporter)) {
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
                MountType type = mounts.getEffectiveType(hasSupporter);
                MountArmor armor = mounts.getEffectiveArmor(hasSupporter);
                net.minecraft.world.entity.Mob customMount = type.create(player.getLocation());
                this.mount = (LivingEntity) customMount.getBukkitEntity();
                mount.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health.getCalculatedValue() == 0 ? 0 : 40);
                mount.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(MountEntitySetup.MOVEMENT_SPEED);
                setBase(mount, Attribute.SCALE, type.getScale());
                setBase(mount, Attribute.STEP_HEIGHT, 1.0);
                if (mount instanceof AbstractHorse horse) {
                    horse.setTamed(true);
                    horse.setOwner(player);
                    horse.setJumpStrength(0);
                    if (type.isHorse()) {
                        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        if (armor != null) {
                            horse.getEquipment().setItem(EquipmentSlot.BODY, armor.createItem());
                        }
                        if (horse instanceof Horse colored && type.getColor() != null) {
                            colored.setColor(type.getColor());
                            colored.setStyle(Horse.Style.NONE);
                        }
                    }
                } else if (mount instanceof Pig) {
                    mount.getEquipment().setItem(EquipmentSlot.SADDLE, new ItemStack(Material.SADDLE));
                }
                if (mount instanceof Ageable ageable) {
                    ageable.setAdult();
                }
                markMount(mount);
                ((CraftWorld) player.getWorld()).getHandle().addFreshEntity(customMount, CreatureSpawnEvent.SpawnReason.CUSTOM);
                mount.addPassenger(player);
            }
            updateHealthDisplay();
        }

        private static void setBase(LivingEntity entity, Attribute attribute, double value) {
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                instance.setBaseValue(value);
            }
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
            if (mount != null) {
                mount.setHealth(newHealth);
            } else if (npc != null) {
                Entity entity = npc.getEntity();
                if (entity instanceof Damageable damageable) {
                    damageable.setHealth(newHealth);
                }
            }
        }

        public void kill() {
            if (mount != null) {
                mount.remove();
            }
            if (npc != null) {
                npc.despawn();
            }
        }

        public void damage(float damage) {
            currentHealth -= damage;
            if (currentHealth <= 0) {
                if (mount != null) {
                    mount.remove();
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

    }

}
