package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class for weapons.
 */
public abstract class AbstractWeapon {

    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    protected static final DecimalFormat DECIMAL_FORMAT_TITLE = new DecimalFormat("#.##");

    static {
        DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
        DECIMAL_FORMAT.setPositivePrefix("+");
        DECIMAL_FORMAT.setNegativePrefix("-");

        DECIMAL_FORMAT_TITLE.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT_TITLE.setRoundingMode(RoundingMode.HALF_UP);
    }

    protected UUID uuid = UUID.randomUUID();
    @Field("obtain_date")
    protected Instant date = Instant.now();
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
        this.specialization = PlayerSettings.getPlayerSettings(uuid).getSelectedSpec();
    }

    public abstract void generateStats();

    public AbstractWeapon(WarlordsPlayer warlordsPlayer) {
        generateStats();
        this.specialization = warlordsPlayer.getSpecClass();
    }

    protected static String format(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    protected static String formatTitleUpgrade(double value, String append) {
        return ChatColor.GREEN + DECIMAL_FORMAT_TITLE.format(value) + append + ChatColor.GRAY;
    }

    protected static String formatTitleUpgrade(double value) {
        return ChatColor.GREEN + DECIMAL_FORMAT_TITLE.format(value) + ChatColor.GRAY;
    }

    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        player.setMaxBaseHealth(player.getMaxBaseHealth() + getHealthBonus());
        player.setHealth(player.getMaxHealth() + getHealthBonus());
    }

    public abstract float getHealthBonus();

    public abstract WeaponsPvE getRarity();

    public abstract int getMeleeDamageRange();

    public abstract float getMeleeDamageMin();

    public abstract float getMeleeDamageMax();

    public abstract float getCritChance();

    public abstract float getCritMultiplier();

    public ItemStack generateItemStack(boolean enchantIfBound) {
        ItemBuilder itemBuilder = new ItemBuilder(selectedWeaponSkin.getItem())
                .name(getName())
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<>();
        lore.addAll(getBaseStats());
        lore.addAll(getLore());
        List<String> loreAddons = getLoreAddons();
        if (!loreAddons.isEmpty() || isBound) {
            lore.add("");
        }
        lore.addAll(loreAddons);
        if (isBound) {
            lore.add(ChatColor.AQUA + "BOUND");
            if (enchantIfBound) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
        }
        return itemBuilder
                .lore(lore)
                .get();
    }

    public abstract List<String> getBaseStats();

    public abstract List<String> getLore();

    public List<String> getLoreAddons() {
        return new ArrayList<>();
    }

    public String getName() {
        return getChatColor() + selectedWeaponSkin.getName() + " of the " + specialization.name;
    }

    public abstract ChatColor getChatColor();

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

    public UUID getUUID() {
        return uuid;
    }

    public Instant getDate() {
        return date;
    }

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
