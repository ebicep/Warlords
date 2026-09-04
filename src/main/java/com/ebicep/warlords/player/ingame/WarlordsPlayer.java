package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerStunEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.speed.BaseToWalkingSpeedValueModifier;
import com.ebicep.warlords.player.ingame.motionsystem.speed.MaxSpeedReductionValueModifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class WarlordsPlayer extends WarlordsEntity implements Listener {

    public static final Set<UUID> STUNNED_PLAYERS = new HashSet<>();

    private static Zombie spawnSimpleJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        return loc.getWorld().spawn(loc, Zombie.class, zombie -> {
            AttributeInstance attribute = zombie.getAttribute(Attribute.MOVEMENT_SPEED);
            if (attribute != null) {
                attribute.setBaseValue(0);
            }
            attribute = zombie.getAttribute(Attribute.FOLLOW_RANGE);
            if (attribute != null) {
                attribute.setBaseValue(0);
            }

                    zombie.setAdult();
                    zombie.setCustomNameVisible(true);

                    EntityEquipment zombieEquipment = zombie.getEquipment();
                    if (inv != null) {
                        zombieEquipment.setBoots(inv.getBoots());
                        zombieEquipment.setLeggings(inv.getLeggings());
                        zombieEquipment.setChestplate(inv.getChestplate());
                        zombieEquipment.setHelmet(inv.getHelmet());
                        zombieEquipment.setItemInMainHand(inv.getItemInMainHand());
                    } else {
                        zombieEquipment.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    }
                    //prevents zombie from moving
                    zombie.setAI(false);
                }
        );
    }

    protected final AbilityTree abilityTree = new AbilityTree(this);
    protected CosmeticSettings cosmeticSettings;
    //    @Override
//    public void setWasSneaking(boolean wasSneaking) {
//        super.setWasSneaking(wasSneaking);
//        if(wasSneaking) {
//            ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Player sneak " + name + " - " + specClass);
//        }
//    }
    @Nullable
    protected AbstractWeapon weapon;
    private int stunTicks = 0;
    private float previousHealth = 1;

    public WarlordsPlayer() {
        super();
    }

    public WarlordsPlayer(Player player, Specializations specialization, List<String> namespaces) {
        super(player, specialization, namespaces);
        DatabasePlayer databasePlayer = getDatabasePlayer();
        this.cosmeticSettings = new CosmeticSettings(
                databasePlayer.getSpec(specialization).getWeapon(),
                databasePlayer.getHelmet(specialization),
                databasePlayer.getArmorSet(specialization)
        );
        resetAbilityTree();
        if (isInPve()) {
            abilityTree.getUpgradeBranches().forEach(AbstractUpgradeBranch::runOnce);
        }
    }

    public void resetAbilityTree() {
        this.abilityTree.getUpgradeBranches().clear();
        this.abilityTree.setMaxMasterUpgrades(3);
        this.spec.setUpgradeBranches(this);
        this.abilityTree.resetAutoUpgradeProfile(getDatabasePlayer());
    }

    public WarlordsPlayer(
            @Nonnull OfflinePlayer player,
            @Nonnull Game game,
            @Nonnull Team team
    ) {
        this(Warlords.getRejoinPoint(player.getUniqueId()), player, game, team);
    }

    public WarlordsPlayer(
            @Nonnull Location location,
            @Nonnull OfflinePlayer player,
            @Nonnull Game game,
            @Nonnull Team team
    ) {
        super(player.getUniqueId(),
                player.getName(),
                spawnSimpleJimmy(location, null),
                game,
                team,
                DatabaseManager.getPlayer(player.getUniqueId()).getLastSpec()
        );
        this.compassTarget = game
                .getMarkers(CompassTargetMarker.class)
                .stream().filter(CompassTargetMarker::isEnabled)
                .max(Comparator.comparing((CompassTargetMarker c) -> c.getCompassTargetPriority(this)))
                .orElse(null);
        DatabasePlayer databasePlayer = getDatabasePlayer();
        this.cosmeticSettings = new CosmeticSettings(
                databasePlayer.getLastSpecWeapon(),
                databasePlayer.getHelmet(),
                databasePlayer.getArmorSet()
        );

        resetAbilityTree();
        if (isInPve()) {
            abilityTree.getUpgradeBranches().forEach(AbstractUpgradeBranch::runOnce);
        }

        updatePlayerReference(player.getPlayer());
        updateEntity();
    }

    @Override
    protected void resetSpeed() {
        super.resetSpeed();
        this.speed.addModifier(new MotionModifierBuilder()
                .setFrom(this)
                .setName("BASE")
                .setModifier(13 + spec.getSpeed())
                .setDuration(-1)
                .addAddons(
                        new BaseToWalkingSpeedValueModifier(BaseToWalkingSpeedValueModifier.BASE_PLAYER_WALK_SPEED),
                        new MaxSpeedReductionValueModifier()
                )
                .build()
        );
    }

    @Override
    public void sendMessage(Component component, boolean isDamageHealMessage) {
        super.sendMessage(component, isDamageHealMessage);
        debugMessageLog.add(component);
        if (isInPve() && debugMessageLog.size() > 200) {
            debugMessageLog.subList(0, 100).clear();
        }
    }

    @Override
    public void setTeam(Team team) {
        super.setTeam(team);
        queueUpdateTabName();
    }

    @Override
    protected boolean shouldCheckForAchievements() {
        return true;
    }

    @Override
    public void updateInventory(boolean closeInventory) {
        if (entity instanceof Player player) {
            player.getInventory().clear();

            game.forEachEnabledOption(option -> option.updateInventory(this, player));
            for (AbstractAbility ability : this.spec.getAbilities()) {
                ability.updateDescription(player);
            }
            updateItems();
            resetPlayerAddons();
            ArmorManager.resetArmor(player, this);

            if (closeInventory) {
                player.closeInventory();
            }
        }
    }

    @Override
    public boolean setStunTicks(int stunTicks) {
        WarlordsPlayerStunEvent stunEvent = new WarlordsPlayerStunEvent(this);
        Bukkit.getPluginManager().callEvent(stunEvent);
        if (stunEvent.isCancelled()) {
            return false;
        }
        if (this.stunTicks < stunTicks) {
            this.stunTicks = stunTicks;
        }
        STUNNED_PLAYERS.add(uuid);
        return true;
    }

    @Override
    public void unstun() {
        STUNNED_PLAYERS.remove(uuid);
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        boolean applied = super.addPotionEffect(potionEffect);
        if (applied) {
            if (potionEffect.getType() == PotionEffectType.INVISIBILITY) {
                PlayerFilterGeneric.playingGameWarlordsNPCs(game)
                                   .stream()
                                   .map(WarlordsNPC::getMob)
                                   .filter(Objects::nonNull)
                                   .filter(abstractMob -> abstractMob.getTarget() != null && abstractMob.getTarget().getUniqueId().equals(uuid))
                                   .forEach(AbstractMob::removeTarget);
            }
        }
        return applied;
    }

    @Override
    public boolean isOnline() {
        return this.entity instanceof Player;
    }

    @Override
    public void runEveryTick() {
        super.runEveryTick();
        // Checks whether the displayed health can be above or under 40 health total. (20 hearts.)
        float newHealth = getCurrentHealth() / getMaxHealth() * 40;
        if (newHealth < 0) {
            newHealth = 0;
        } else if (newHealth > 40) {
            newHealth = 40;
        }
        // setting health/energy to player
        if (getEntity() instanceof Player player) {
            //precaution
            if (newHealth != 0 && Float.compare(newHealth, previousHealth) != 0) {
                previousHealth = newHealth;
                player.setHealth(newHealth);
            }
            // Respawn fix for when a player is stuck or leaves the game.
            if (getCurrentHealth() <= 0 && player.getGameMode() == GameMode.SPECTATOR) {
                heal();
            }
            // Checks whether the player has under 0 energy to avoid infinite energy bugs.
            if (getCurrentEnergy() < 0) {
                setCurrentEnergy(1);
            } else if (getCurrentEnergy() > getMaxEnergy()) {
                setCurrentEnergy(getMaxEnergy());
            }
            player.setLevel((int) getCurrentEnergy());
            player.setExp(MathUtils.clamp(getCurrentEnergy() / getMaxEnergy(), 0, 1));
            // Saves the amount of blocks travelled per player.
            setBlocksTravelledCM(Utils.getPlayerMovementStatistics(player));
        }
        if (stunTicks > 0) {
            stunTicks--;
            if (stunTicks == 0) {
                unstun();
            }
        }
        int regenTickTimer = getRegenTickTimer();
        setRegenTickTimer(regenTickTimer - 1);
        if (regenTickTimer == 0) {
            getHitBy().clear();
        }
        //negative regen tick timer means the player is regenning, cant check per second because not fine enough
        if (regenTickTimer <= 0 && -regenTickTimer % 20 == 0) {
            int regenHealth = ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "regenHealth", int.class);
            regenPerSecond.setBaseValue(regenHealth);
            regenPerSecond.refresh();
            setCurrentHealth(Math.max(getCurrentHealth(), Math.min(getCurrentHealth() + regenPerSecond.getCalculatedValue(), getMaxHealth())));
        }
    }

    @Override
    public void updateHealth() {
        if (getEntity() instanceof Zombie) {
            if (isDead()) {
                getEntity().customName(Component.text(""));
            } else {
                getEntity().customName(Component.textOfChildren(
                        getColoredName(),
                        Component.text(" " + Math.round(getCurrentHealth()) + "❤", NamedTextColor.RED)
                ));
            }
        }
    }

    @Override
    public void updateEntity() {
        if (entity instanceof Player player) {
            player.removeMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, Warlords.getInstance());
            player.setMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, new FixedMetadataValue(Warlords.getInstance(), this));
            player.setWalkSpeed(getSpeed().getLastValue());
            player.setMaxHealth(40);
            player.setLevel((int) this.getMaxEnergy());

            updateInventory(true);
            resetPlayerAddons();
            updateArmor();

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (isDead()) {
                        player.setGameMode(GameMode.SPECTATOR);
                    } else {
                        player.setGameMode(GameMode.ADVENTURE);
                    }
                }
            }.runTaskLater(Warlords.getInstance(), 1);
        } else {
            this.entity.remove();
            if (!isDead()) {
                ItemStack[] armor = cosmeticSettings.getArmor(getTeam());
                this.entity = spawnJimmy(
                        this.entity.getLocation(),
                        new Utils.SimpleEntityEquipment(
                                armor[3],
                                armor[2],
                                armor[1],
                                armor[0],
                                cosmeticSettings.getWeaponSkin().getItem()
                        )
                );
            }
        }
    }

    @Override
    public void setSpec(Specializations spec) {
        Specializations oldSpec = this.specClass;
        super.setSpec(spec);
        if (weapon != null && weapon instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }
        this.specClass = spec;
        this.specClass.init(this);

        DatabasePlayer databasePlayer = getDatabasePlayer();
        cosmeticSettings.setWeaponSkin(databasePlayer.getSpec(spec).getWeapon());
        cosmeticSettings.setHelmet(databasePlayer.getHelmet(spec));
        cosmeticSettings.setArmorSet(databasePlayer.getArmorSet(spec));

        Player player = Bukkit.getPlayer(uuid);

        ArmorManager.resetArmor(player, this);
        game.forEachEnabledOption(option -> option.onSpecChange(this, oldSpec));
        updateInventory(true);
        queueUpdateTabName();
    }

    @Override
    public void runEverySecond() {
        super.runEverySecond();
        if (this.getWeapon() instanceof AbstractLegendaryWeapon && this.getEntity() instanceof Player player) {
            ((AbstractLegendaryWeapon) this.getWeapon()).updateAbilityItem(this, player);
        }
    }

    @Override
    public void setDamageResistance(float damageResistance) {
//        getSpec().setDamageResistance(damageResistance);
        getSpec().setDamageResistance(Math.max(0, damageResistance));
    }

    @Override
    public int getBaseHitCooldownValue() {
        return ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "playerMeleeCooldown", int.class);
    }

    @Override
    public ItemStack getHead() {
        return HeadUtils.getHead(uuid);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getHelmet() {
        return entity instanceof Player player ? player.getInventory().getHelmet() : cosmeticSettings.getArmor(getTeam())[3];
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getChestplate() {
        return entity instanceof Player player ? player.getInventory().getChestplate() : cosmeticSettings.getArmor(getTeam())[2];
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getLeggings() {
        return entity instanceof Player player ? player.getInventory().getLeggings() : cosmeticSettings.getArmor(getTeam())[1];
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getBoots() {
        return entity instanceof Player player ? player.getInventory().getBoots() : cosmeticSettings.getArmor(getTeam())[0];
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getWeaponItem() {
        return weapon == null ? cosmeticSettings.getWeaponSkin().getItem() : weapon.getSelectedWeaponSkin().getItem();
    }

    @Nullable
    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(@Nullable AbstractWeapon weapon) {
        this.weapon = weapon;
    }

    public void queueUpdateTabName() {
        game.getState(PlayingState.class).ifPresent(playingState -> playingState.getUpdater().markTabNameDirty(this));
    }

    public void resetPlayerAddons() {
        if (getEntity() instanceof Player player) {
            PlayerInventory playerInventory = player.getInventory();

            //Soulbinding weapon enchant
            ItemStack firstItem = playerInventory.getItem(0);
            if (firstItem != null) {
                if (getCooldownManager().hasCooldown(Soulbinding.SoulbindingData.class)) {
                    ItemMeta itemMeta = firstItem.getItemMeta();
                    itemMeta.addEnchant(Enchantment.RESPIRATION, 1, true);
                    firstItem.setItemMeta(itemMeta);
                } else {
                    firstItem.removeEnchantment(Enchantment.RESPIRATION);
                }
            }

            //Undying army bone
            if (UndyingArmy.checkUndyingArmy(this, true, null)) {
                playerInventory.setItem(5, UndyingArmy.BONE);
            } else {
                playerInventory.remove(UndyingArmy.BONE);
            }

            Shield.updateAbsorption(this);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (STUNNED_PLAYERS.contains(e.getPlayer().getUniqueId())) {
            if (
                    (e.getFrom().getX() != e.getTo().getX() ||
                            e.getFrom().getZ() != e.getTo().getZ()) &&
                            !(e instanceof PlayerTeleportEvent)
            ) {
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }

    public void updatePlayerReference(@Nullable Player player) {
        if (player == this.entity) {
            return;
        }
        Location loc = this.getLocation();

        if (player == null) {
            if (this.entity instanceof Player p) {
                p.getInventory().setHeldItemSlot(0);
                this.entity = spawnJimmy(loc, p.getEquipment());
                Warlords.setRejoinPoint(uuid, loc);
            }
        } else {
            if (this.entity instanceof Zombie) { // This could happen if there was a problem during the quit event
                this.entity.remove();
            }
            player.teleport(loc);
            this.entity = player;
            updateEntity();
        }
    }

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = spawnSimpleJimmy(loc, inv);
        jimmy.setMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, new FixedMetadataValue(Warlords.getInstance(), this));
        return jimmy;
    }

    public ItemStack getItemStackForAbility(AbstractAbility ability) {
        if (ability == spec.getWeapon()) {
            if (weapon == null) {
                return cosmeticSettings.getWeaponSkin().getItem();
            } else {
                return weapon.getSelectedWeaponSkin().getItem();
            }
        } else {
            return ability.getAbilityIcon();
        }
    }

    public AbilityTree getAbilityTree() {
        return abilityTree;
    }

    public CosmeticSettings getCosmeticSettings() {
        return cosmeticSettings;
    }

}
