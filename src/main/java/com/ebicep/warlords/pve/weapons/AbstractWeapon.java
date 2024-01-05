package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.enchantments.Enchantment;
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

    public static final TextComponent GREEN_ARROW = Component.text(" > ", NamedTextColor.DARK_GREEN);
    protected static final DecimalFormat DECIMAL_FORMAT_TITLE = new DecimalFormat("#,###.##");

    static {
        DECIMAL_FORMAT_TITLE.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT_TITLE.setRoundingMode(RoundingMode.HALF_UP);
    }

    protected static String format(double value) {
        return NumberFormat.DECIMAL_FORMAT_OPTIONAL_TENTHS_PREFIX.format(value);
    }

    protected static Component formatTitleUpgrade(double value, String append) {
        return Component.text(DECIMAL_FORMAT_TITLE.format(value) + append, NamedTextColor.GREEN);
    }

    protected static Component formatTitleUpgrade(String prepend, double value) {
        return Component.text(prepend + DECIMAL_FORMAT_TITLE.format(value), NamedTextColor.GREEN);
    }

    protected static Component formatTitleUpgrade(double value) {
        return Component.text(DECIMAL_FORMAT_TITLE.format(value), NamedTextColor.GREEN);
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
        this.specialization = Specializations.generateSpec(PlayerSettings.getPlayerSettings(uuid).getSelectedSpec());
    }

    public abstract void generateStats();

    public AbstractWeapon(WarlordsPlayer warlordsPlayer) {
        generateStats();
        this.specialization = Specializations.generateSpec(warlordsPlayer.getSpecClass());
    }

    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        player.getHealth().addAdditiveModifier("Weapon Health (Base)", getHealthBonus());
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
                .unbreakable();
        List<Component> lore = new ArrayList<>();
        lore.addAll(getBaseStats());
        lore.addAll(getLore());
        List<Component> loreAddons = getLoreAddons();
        if (!loreAddons.isEmpty() || isBound) {
            lore.add(Component.empty());
        }
        lore.addAll(loreAddons);
        if (isBound) {
            lore.add(Component.text("BOUND", NamedTextColor.AQUA));
            if (enchantIfBound) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
        }
        return itemBuilder
                .lore(lore)
                .get();
    }

    public Component getHoverComponent(boolean enchantIfBound) {
        return getName().hoverEvent(generateItemStack(enchantIfBound));
    }

    public Component getName() {
        return Component.text(selectedWeaponSkin.getName() + " of the " + specialization.name, getTextColor());
    }

    public abstract List<Component> getBaseStats();

    public abstract List<Component> getLore();

    public List<Component> getLoreAddons() {
        return new ArrayList<>();
    }

    public abstract TextColor getTextColor();

    public ItemBuilder generateItemStackInLore(Component name) {
        List<Component> lore = new ArrayList<>();
        lore.add(getName());
        lore.add(Component.empty());
        lore.addAll(getBaseStats());
        lore.addAll(getLore());
        lore.add(Component.empty());
        lore.addAll(getLoreAddons());
        if (isBound) {
            lore.add(Component.text("BOUND", NamedTextColor.AQUA));
        }
        return new ItemBuilder(selectedWeaponSkin.getItem())
                .name(name)
                .lore(lore)
                .unbreakable();
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
