package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.bukkit.ItemBuilder.*;

public final class WarlordsPlayer extends WarlordsEntity {

    private static Zombie spawnSimpleJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = loc.getWorld().spawn(loc, Zombie.class);
        jimmy.setBaby(false);
        jimmy.setCustomNameVisible(true);

        if (inv != null) {
            jimmy.getEquipment().setBoots(inv.getBoots());
            jimmy.getEquipment().setLeggings(inv.getLeggings());
            jimmy.getEquipment().setChestplate(inv.getChestplate());
            jimmy.getEquipment().setHelmet(inv.getHelmet());
            jimmy.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            jimmy.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        }
        return jimmy;
    }

    private final AbilityTree abilityTree = new AbilityTree(this);
    private final CosmeticSettings cosmeticSettings;
    private SkillBoosts skillBoost;
    private AbstractWeapon weapon;

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
        this.cosmeticSettings = new CosmeticSettings(
                settings.getWeaponSkinForSelectedSpec(),
                settings.getHelmet(settings.getSelectedSpec()),
                settings.getArmorSet(settings.getSelectedSpec())
        );
        this.skillBoost = settings.getSkillBoostForClass();
        this.spec.setUpgradeBranches(this);

        updatePlayerReference(player.getPlayer());
        updateInventory(true);
    }

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = spawnSimpleJimmy(loc, inv);
        jimmy.setCustomName(this.getSpec()
                .getClassNameShortWithBrackets() + " " + this.getColoredName() + " " + ChatColor.RED + Math.round(
                this.getHealth()) + "❤"); // TODO add level and class into the name of this jimmy
        jimmy.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
                .setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE)
                .setValue(0);
        //prevents jimmy from moving
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) jimmy).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
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
            if (this.entity instanceof Player) {
                ((Player) this.entity).getInventory().setHeldItemSlot(0);
                this.entity = spawnJimmy(loc, ((Player) this.entity).getEquipment());
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
    public void updateHealth() {
        if (getEntity() instanceof Zombie) {
            if (isDead()) {
                getEntity().setCustomName("");
            } else {
                String oldName = getEntity().getCustomName();
                String newName = oldName.substring(0, oldName.lastIndexOf(' ') + 1) + ChatColor.RED + Math.round(
                        getHealth()) + "❤";
                getEntity().setCustomName(newName);
            }
        }
    }

    @Override
    public void setSpec(Specializations spec, SkillBoosts skillBoost) {
        super.setSpec(spec, skillBoost);
        this.specClass = spec;
        this.skillBoost = skillBoost;

        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        cosmeticSettings.setWeaponSkin(playerSettings.getWeaponSkins().get(spec));
        cosmeticSettings.setHelmet(playerSettings.getHelmet(spec));
        cosmeticSettings.setArmorSet(playerSettings.getArmorSet(spec));

        Player player = Bukkit.getPlayer(uuid);

        ArmorManager.resetArmor(player, this);
        for (Option option : game.getOptions()) {
            option.onSpecChange(this);
        }
        updateInventory(true);
    }

    public void resetPlayerAddons() {
        if (getEntity() instanceof Player) {
            Player player = (Player) getEntity();
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

            //Arcane shield absorption hearts
            List<ArcaneShield> arcaneShields = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(ArcaneShield.class)
                    .collect(Collectors.toList());
            if (!arcaneShields.isEmpty()) {
                ArcaneShield arcaneShield = arcaneShields.get(0);
                ((CraftPlayer) player).getHandle()
                        .setAbsorptionHearts((float) (arcaneShield.getShieldHealth() / (getMaxHealth() * .5) * 20));
            } else {
                ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
            }
        }
    }

    @Override
    public void updateInventory(boolean closeInventory) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            player.getInventory().clear();

            for (Option option : game.getOptions()) {
                option.updateInventory(this, player);
            }
            for (AbstractAbility ability : this.spec.getAbilities()) {
                ability.updateDescription(player);
            }
            updateItems();
            resetPlayerAddons();

            if (closeInventory) {
                player.closeInventory();
            }
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
                        .filter(abstractMob -> abstractMob.getTarget() != null && abstractMob.getTarget().getUniqueID().equals(uuid))
                        .forEach(AbstractMob::removeTarget);
            }
        }
        return applied;
    }

    @Override
    public boolean isOnline() {
        return this.entity instanceof Player;
    }

    public void updateEntity() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            player.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
            player.setWalkSpeed(walkSpeed);
            player.setMaxHealth(40);
            player.setLevel((int) this.getMaxEnergy());

            updateInventory(true);
            resetPlayerAddons();
            updateArmor();

            if (isDead()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
            }
        } else {
            this.entity = spawnJimmy(this.entity.getLocation(), this.entity.getEquipment());
        }
    }

    public void applySkillBoost(Player player) {
        for (AbstractAbility ability : spec.getAbilities()) {
            if (ability.getClass() == skillBoost.ability) {
                ability.boostSkill(skillBoost, spec);
                ability.updateDescription(player);
                updateItems();
                break;
            }
        }
    }

    public ItemStack getItemStackForAbility(AbstractAbility ability) {
        if (ability == spec.getWeapon()) {
            return cosmeticSettings.getWeaponSkin().getItem();
        } else if (ability == spec.getRed()) {
            return RED_ABILITY;
        } else if (ability == spec.getPurple()) {
            return PURPLE_ABILITY;
        } else if (ability == spec.getBlue()) {
            return BLUE_ABILITY;
        } else if (ability == spec.getOrange()) {
            return ORANGE_ABILITY;
        }
        return null;
    }


    public AbilityTree getAbilityTree() {
        return abilityTree;
    }

    public void resetAbilityTree() {
        this.abilityTree.getUpgradeBranches().clear();
        this.spec.setUpgradeBranches(this);
    }

    public AbstractWeapon getAbstractWeapon() {
        return weapon;
    }

    public void setWeapon(AbstractWeapon weapon) {
        this.weapon = weapon;
    }

    public CosmeticSettings getCosmeticSettings() {
        return cosmeticSettings;
    }

    public SkillBoosts getSkillBoost() {
        return skillBoost;
    }
}
