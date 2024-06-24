package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
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
        });
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
    protected SkillBoosts skillBoost;
    @Nullable
    protected AbstractWeapon weapon;

    private boolean updateTabName = true;

    public WarlordsPlayer() {
        super();
    }

    public WarlordsPlayer(Player player, Specializations specialization) {
        super(player, specialization);
        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        this.cosmeticSettings = new CosmeticSettings(
                settings.getWeaponSkinForSelectedSpec(),
                settings.getHelmet(settings.getSelectedSpec()),
                settings.getArmorSet(settings.getSelectedSpec())
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
        DatabaseManager.getPlayer(uuid, this.abilityTree::resetAutoUpgradeProfile);
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
        this(location, player, game, team, PlayerSettings.getPlayerSettings(player.getUniqueId()));
    }

    private WarlordsPlayer(
            @Nonnull Location location,
            @Nonnull OfflinePlayer player,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull PlayerSettings settings
    ) {
        super(player.getUniqueId(),
                player.getName(),
                spawnSimpleJimmy(location, null),
                game,
                team,
                settings.getSelectedSpec()
        );
        this.compassTarget = game
                .getMarkers(CompassTargetMarker.class)
                .stream().filter(CompassTargetMarker::isEnabled)
                .max(Comparator.comparing((CompassTargetMarker c) -> c.getCompassTargetPriority(this)))
                .orElse(null);
        this.cosmeticSettings = new CosmeticSettings(
                settings.getWeaponSkinForSelectedSpec(),
                settings.getHelmet(settings.getSelectedSpec()),
                settings.getArmorSet(settings.getSelectedSpec())
        );
        this.skillBoost = settings.getSkillBoostForClass();

        resetAbilityTree();
        if (isInPve()) {
            abilityTree.getUpgradeBranches().forEach(AbstractUpgradeBranch::runOnce);
        }

        updatePlayerReference(player.getPlayer());
        updateEntity();

        if (player.getPlayer() != null && Permissions.isAdmin(player.getPlayer())) {
            this.setShowDebugMessage(true);
        }
    }

    public void stun() {
        STUNNED_PLAYERS.add(uuid);
    }

    public void unstun() {
        STUNNED_PLAYERS.remove(uuid);
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

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = spawnSimpleJimmy(loc, inv);
        jimmy.customName(Component.empty()
                                  .append(getSpec().getClassNameShortWithBrackets())
                                  .append(Component.text(" "))
                                  .append(this.getColoredName())
                                  .append(Component.text(" " + Math.round(this.getCurrentHealth()) + "❤",
                                          NamedTextColor.RED
                                  ))); // TODO add level and class into the name of this jimmy
        jimmy.setMetadata(WarlordsEntity.WARLORDS_ENTITY_METADATA, new FixedMetadataValue(Warlords.getInstance(), this));
        AttributeInstance attribute = jimmy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(0);
        }
        attribute = jimmy.getAttribute(Attribute.GENERIC_FOLLOW_RANGE);
        if (attribute != null) {
            attribute.setBaseValue(0);
        }
        //prevents jimmy from moving
        jimmy.setAI(false);
        if (isDead()) {
            jimmy.remove();
        }
        return jimmy;
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

    @Override
    protected boolean shouldCheckForAchievements() {
        return true;
    }

    @Override
    public void updateInventory(boolean closeInventory) {
        if (entity instanceof Player player) {
            player.getInventory().clear();

            for (Option option : game.getOptions()) {
                option.updateInventory(this, player);
            }
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
    public void setSpec(Specializations spec, SkillBoosts skillBoost) {
        Specializations oldSpec = this.specClass;
        super.setSpec(spec, skillBoost);
        if (weapon != null && weapon instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }
        this.specClass = spec;
        this.skillBoost = skillBoost;

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        cosmeticSettings.setWeaponSkin(playerSettings.getWeaponSkins().get(spec));
        cosmeticSettings.setHelmet(playerSettings.getHelmet(spec));
        cosmeticSettings.setArmorSet(playerSettings.getArmorSet(spec));

        Player player = Bukkit.getPlayer(uuid);

        ArmorManager.resetArmor(player, this);
        for (Option option : game.getOptions()) {
            option.onSpecChange(this, oldSpec);
        }
        updateInventory(true);
    }

    public void resetPlayerAddons() {
        if (getEntity() instanceof Player player) {
            PlayerInventory playerInventory = player.getInventory();

            //Soulbinding weapon enchant
            ItemStack firstItem = playerInventory.getItem(0);
            if (firstItem != null) {
                if (getCooldownManager().hasCooldown(Soulbinding.class)) {
                    ItemMeta itemMeta = firstItem.getItemMeta();
                    itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
                    firstItem.setItemMeta(itemMeta);
                } else {
                    firstItem.removeEnchantment(Enchantment.OXYGEN);
                }
            }

            //Undying army bone
            if (getCooldownManager().checkUndyingArmy(true)) {
                playerInventory.setItem(5, UndyingArmy.BONE);
            } else {
                playerInventory.remove(UndyingArmy.BONE);
            }

            double totalShieldHealth = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                    .mapToDouble(Shield::getShieldHealth)
                    .sum();
            giveAbsorption((float) (totalShieldHealth / getMaxHealth() * 40));
        }
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
        int regenTickTimer = getRegenTickTimer();
        setRegenTickTimer(regenTickTimer - 1);
        if (regenTickTimer == 0) {
            getHitBy().clear();
        }
        //negative regen tick timer means the player is regenning, cant check per second because not fine enough
        if (regenTickTimer <= 0 && -regenTickTimer % 20 == 0) {
            int healthToAdd = (int) (getMaxHealth() / 55.3);
            setCurrentHealth(Math.max(getCurrentHealth(), Math.min(getCurrentHealth() + healthToAdd, getMaxHealth())));
        }
    }

    @Override
    public void updateHealth() {
        if (getEntity() instanceof Zombie) {
            if (isDead()) {
                getEntity().customName(Component.text(""));
            } else {
                getEntity().customName(Component.textOfChildren(
                        Component.text("[", NamedTextColor.DARK_GRAY)
                                 .append(Component.text(getSpec().getClassNameShort(), NamedTextColor.GOLD))
                                 .append(Component.text("] ")),
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
            player.setWalkSpeed(walkSpeed);
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
            this.entity = spawnJimmy(this.entity.getLocation(), null);
        }
    }

    @Override
    public void setDamageResistance(float damageResistance) {
//        getSpec().setDamageResistance(damageResistance);
        getSpec().setDamageResistance(Math.max(0, damageResistance));
    }

    @Override
    public int getBaseHitCooldownValue() {
        return 12;
    }

    @Override
    public ItemStack getHead() {
        return HeadUtils.getHead(uuid);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getHelmet() {
        return entity instanceof Player player ? player.getInventory().getHelmet() : null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getChestplate() {
        return entity instanceof Player player ? player.getInventory().getChestplate() : null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getLeggings() {
        return entity instanceof Player player ? player.getInventory().getLeggings() : null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getBoots() {
        return entity instanceof Player player ? player.getInventory().getBoots() : null;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ItemStack getWeaponItem() {
        return weapon == null ? null : weapon.getSelectedWeaponSkin().getItem();
    }

    public void applySkillBoost(Player player) {
        for (AbstractAbility ability : spec.getAbilities()) {
            if (ability.getClass() == skillBoost.ability) {
                ability.boostSkill(skillBoost, spec);
                ability.updateDescription(player);
                updateItem(ability);
                break;
            }
        }
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

    @Nullable
    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(@Nullable AbstractWeapon weapon) {
        this.weapon = weapon;
    }

    public CosmeticSettings getCosmeticSettings() {
        return cosmeticSettings;
    }

    public SkillBoosts getSkillBoost() {
        return skillBoost;
    }

    public boolean isUpdateTabName() {
        return updateTabName;
    }

    public void queueUpdateTabName() {
        this.updateTabName = true;
    }
}
