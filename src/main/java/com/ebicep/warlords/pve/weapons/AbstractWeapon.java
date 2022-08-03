package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class for weapons.
 */
public abstract class AbstractWeapon {

    @Field("obtain_date")
    protected Instant date = Instant.now();
    @Field("melee_damage")
    protected float meleeDamage;
    @Field("health_bonus")
    protected float healthBonus;
    @Field("weapon_skin")
    protected Weapons selectedWeaponSkin = Weapons.STEEL_SWORD;
    @Field("unlocked_weapon_skins")
    protected List<Weapons> unlockedWeaponSkins = new ArrayList<>();
    @Field("specialization")
    protected Specializations specialization;
    @Field("bound")
    protected boolean isBound = false;

    public AbstractWeapon() {
    }

    public AbstractWeapon(UUID uuid) {
        generateStats();
        this.specialization = Warlords.getPlayerSettings(uuid).getSelectedSpec();
    }

    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        player.setMaxHealth(Math.round(player.getMaxHealth() + getHealthBonus()));
        player.setHealth(Math.round(player.getMaxHealth() + getHealthBonus()));
    }

    public abstract ChatColor getChatColor();

    public abstract List<String> getLore();

    public List<String> getLoreAddons() {
        return new ArrayList<>();
    }

    public abstract void generateStats();

    public abstract int getMeleeDamageRange();

    public String getName() {
        return getChatColor() + selectedWeaponSkin.getName() + " of the " + specialization.name;
    }

    protected List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + meleeDamage,
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + healthBonus
        );
    }

    public ItemStack generateItemStack() {
        List<String> lore = new ArrayList<>();
        lore.addAll(getBaseStats());
        lore.addAll(getLore());
        lore.add("");
        lore.addAll(getLoreAddons());
        if (isBound) {
            lore.add(ChatColor.AQUA + "BOUND");
        }
        return new ItemBuilder(selectedWeaponSkin.getItem())
                .name(getName())
                .lore(lore)
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }

    public ItemBuilder generateItemStackInLore(String name) {
        List<String> lore = new ArrayList<>();
        lore.add(getName());
        lore.add("");
        lore.addAll(getBaseStats());
        lore.addAll(getLore());
        lore.add("");
        lore.addAll(getLoreAddons());
        if (isBound) {
            lore.add(ChatColor.AQUA + "BOUND");
        }
        return new ItemBuilder(selectedWeaponSkin.getItem())
                .name(name)
                .lore(lore)
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
    }

    public Instant getDate() {
        return date;
    }

    public abstract float getMeleeDamageMin();

    public abstract float getMeleeDamageMax();

    public abstract float getCritChance();

    public abstract float getCritMultiplier();

    public abstract float getHealthBonus();

    public Weapons getSelectedWeaponSkin() {
        return selectedWeaponSkin;
    }

    public void setSelectedWeaponSkin(Weapons selectedWeaponSkin) {
        this.selectedWeaponSkin = selectedWeaponSkin;
    }

    public List<Weapons> getUnlockedWeaponSkins() {
        return unlockedWeaponSkins;
    }

    public Specializations getSpecializations() {
        return specialization;
    }

    public void setSpecializations(Specializations specializations) {
        this.specialization = specializations;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

}
