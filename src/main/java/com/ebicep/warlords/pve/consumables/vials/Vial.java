package com.ebicep.warlords.pve.consumables.vials;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.consumables.Consumable;
import com.ebicep.warlords.pve.consumables.ConsumableManager;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public enum Vial implements Consumable, Spendable {

    INSIGNIA_BOOST_I("Insignia Boost I", "Increases Insignia gained in PvE.", VialEffect.INSIGNIA_GAIN, 1.25, 50_000, 200_000, Duration.ofHours(24), Material.HONEY_BOTTLE),
    INSIGNIA_BOOST_II("Insignia Boost II", "Increases Insignia gained in PvE.", VialEffect.INSIGNIA_GAIN, 1.5, 200_000, 1_000_000, Duration.ofHours(24), Material.HONEY_BOTTLE),
    WEAPON_DROP_RATE_I("Weapon Drop Rate I", "Increases weapon drop chance from PvE mobs.", VialEffect.WEAPON_DROP_RATE, 1.5, 100_000, 200_000, Duration.ofHours(24), Material.EXPERIENCE_BOTTLE),
    WEAPON_DROP_RATE_II("Weapon Drop Rate II", "Increases weapon drop chance from PvE mobs.", VialEffect.WEAPON_DROP_RATE, 2, 400_000, 800_000, Duration.ofHours(24), Material.EXPERIENCE_BOTTLE),
    ITEM_DROP_RATE_I("Item Drop Rate I", "Increases item drop chance from PvE mobs.", VialEffect.ITEM_DROP_RATE, 1.5, 200_000, 800_000, Duration.ofHours(24), Material.DRAGON_BREATH),
    ITEM_DROP_RATE_II("Item Drop Rate II", "Increases item drop chance from PvE mobs.", VialEffect.ITEM_DROP_RATE, 2, 800_000, 3_200_000, Duration.ofHours(24), Material.DRAGON_BREATH);

    public static final Vial[] VALUES = values();

    private final String name;
    private final String description;
    private final VialEffect effect;
    private final double multiplier;
    private final long playerCost;
    private final long guildUnlockCost;
    private final Duration duration;
    private final Material material;

    Vial(String name, String description, VialEffect effect, double multiplier, long playerCost, long guildUnlockCost, Duration duration, Material material) {
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.multiplier = multiplier;
        this.playerCost = playerCost;
        this.guildUnlockCost = guildUnlockCost;
        this.duration = duration;
        this.material = material;
    }

    @Override
    public String getId() {
        return "vial_" + name().toLowerCase();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getEffectDescription() {
        return "x" + NumberFormat.formatOptionalHundredths(multiplier) + " " + switch (effect) {
            case INSIGNIA_GAIN -> "Insignia Gain";
            case WEAPON_DROP_RATE -> "Weapon Drop Chance";
            case ITEM_DROP_RATE -> "Item Drop Chance";
        };
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public long getPlayerCost() {
        return playerCost;
    }

    @Override
    public long getGuildUnlockCost() {
        return guildUnlockCost;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public String getActiveGroup() {
        return effect.getActiveGroup();
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(material);
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        if (amount > 0) {
            manager.add(this, Math.toIntExact(amount));
        } else if (amount < 0) {
            manager.remove(this, Math.toIntExact(-amount));
        }
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return (long) databasePlayer.getPveStats().getConsumableManager().getAmount(this);
    }

    public VialEffect getEffect() {
        return effect;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
