package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.PlayingState;
import static com.ebicep.warlords.player.Weapons.FELFLAME_BLADE;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public final class WarlordsPlayer extends WarlordsEntity {
    
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
        this(location, player, game, team, Warlords.getPlayerSettings(player.getUniqueId()));
    }
    private WarlordsPlayer(
            @Nonnull Location location,
            @Nonnull OfflinePlayer player,
            @Nonnull Game game,
            @Nonnull Team team,
            @Nonnull PlayerSettings settings
    ) {
        super(player.getUniqueId(), player.getName(), settings.getWeaponSkin(), spawnSimpleJimmy(location, null), game, team, settings.getSelectedSpec());
        updatePlayerReference(player.getPlayer());
    }
    
    @Override
    public boolean isOnline() {
        return this.entity instanceof Player;
    }
    
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

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = spawnSimpleJimmy(loc, inv);
        jimmy.setCustomName(this.getSpec().getClassNameShortWithBrackets() + " " + this.getColoredName() + " " + ChatColor.RED + this.getHealth() + "❤"); // TODO add level and class into the name of this jimmy
        jimmy.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
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
    public void updateHealth() {
        if (getEntity() instanceof Zombie) {
            if (isDead()) {
                getEntity().setCustomName("");
            } else {
                String oldName = getEntity().getCustomName();
                String newName = oldName.substring(0, oldName.lastIndexOf(' ') + 1) + ChatColor.RED + getHealth() + "❤";
                getEntity().setCustomName(newName);
            }
        }
    }
    
    private void resetPlayerAddons() {
        if (getEntity() instanceof Player) {
            Player player = (Player) getEntity();

            //Soulbinding weapon enchant
            if (getCooldownManager().hasCooldown(Soulbinding.class)) {
                ItemMeta itemMeta = player.getInventory().getItem(0).getItemMeta();
                itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
                player.getInventory().getItem(0).setItemMeta(itemMeta);
            } else {
                player.getInventory().getItem(0).removeEnchantment(Enchantment.OXYGEN);
            }

            //Undying army bone
            if (getCooldownManager().checkUndyingArmy(true)) {
                player.getInventory().setItem(5, UndyingArmy.BONE);
            } else {
                player.getInventory().remove(UndyingArmy.BONE);
            }

            //Arcane shield absorption hearts
            List<ArcaneShield> arcaneShields = new CooldownFilter<>(this, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(ArcaneShield.class)
                    .collect(Collectors.toList());
            if (!arcaneShields.isEmpty()) {
                ArcaneShield arcaneShield = arcaneShields.get(0);
                ((CraftPlayer) player).getHandle().setAbsorptionHearts((float) (arcaneShield.getShieldHealth() / (getMaxHealth() * .5) * 20));
            } else {
                ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
            }
        }
    }
    
    public void updateEntity() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            player.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
            player.setWalkSpeed(walkspeed);
            player.setMaxHealth(40);
            player.setLevel((int) this.getMaxEnergy());
            player.getInventory().clear();
            this.spec.getWeapon().updateDescription(player);
            this.spec.getRed().updateDescription(player);
            this.spec.getPurple().updateDescription(player);
            this.spec.getBlue().updateDescription(player);
            this.spec.getOrange().updateDescription(player);
            applySkillBoost(player);
            player.closeInventory();
            this.assignItemLore(player);
            updateArmor();

            resetPlayerAddons();

            if (isDead()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.setGameMode(GameMode.ADVENTURE);
            }
        } else {
            this.entity = spawnJimmy(this.entity.getLocation(), this.entity.getEquipment());
        }
    }
}
