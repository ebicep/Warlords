package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.rewards.RewardTypes;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public enum WeaponsPvE {

    NONE("None",
            null,
            ChatColor.GRAY,
            null,
            null,
            null,
            null,
            null,
            null),
    COMMON("Common",
            CommonWeapon.class,
            ChatColor.GREEN,
            new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5),
            MasterworksFair::getCommonPlayerEntries,
            DatabasePlayerPvE::getCommonStarPieces,
            DatabasePlayerPvE::addCommonStarPiece,
            DatabasePlayerPvE::subtractCommonStarPiece,
            RewardTypes.COMMON_STAR_PIECE),
    RARE("Rare",
            RareWeapon.class,
            ChatColor.BLUE,
            new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3),
            MasterworksFair::getRarePlayerEntries,
            DatabasePlayerPvE::getRareStarPieces,
            DatabasePlayerPvE::addRareStarPiece,
            DatabasePlayerPvE::subtractRareStarPiece,
            RewardTypes.RARE_STAR_PIECE),
    EPIC("Epic",
            EpicWeapon.class,
            ChatColor.DARK_PURPLE,
            new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2),
            MasterworksFair::getEpicPlayerEntries,
            DatabasePlayerPvE::getEpicStarPieces,
            DatabasePlayerPvE::addEpicStarPiece,
            DatabasePlayerPvE::subtractEpicStarPiece,
            RewardTypes.EPIC_STAR_PIECE),
    LEGENDARY("Legendary",
            AbstractLegendaryWeapon.class,
            ChatColor.GOLD,
            new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1),
            null,
            DatabasePlayerPvE::getLegendaryStarPieces,
            DatabasePlayerPvE::addLegendaryStarPiece,
            DatabasePlayerPvE::addLegendaryStarPiece,
            RewardTypes.LEGENDARY_STAR_PIECE);

    public final String name;
    public final Class<?> weaponClass;
    public final ChatColor chatColor;
    public final ItemStack glassItem;
    public final Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries;
    public final Function<DatabasePlayerPvE, Integer> getStarPiece;
    public final Consumer<DatabasePlayerPvE> addStarPiece;
    public final Consumer<DatabasePlayerPvE> subtractStarPiece;
    public final RewardTypes starPieceRewardType;

    WeaponsPvE(String name, Class<?> weaponClass, ChatColor chatColor, ItemStack glassItem, Function<MasterworksFair, List<MasterworksFairPlayerEntry>> getPlayerEntries, Function<DatabasePlayerPvE, Integer> getStarPiece, Consumer<DatabasePlayerPvE> addStarPiece, Consumer<DatabasePlayerPvE> subtractStarPiece, RewardTypes starPieceRewardType) {
        this.weaponClass = weaponClass;
        this.chatColor = chatColor;
        this.name = name;
        this.getPlayerEntries = getPlayerEntries;
        this.glassItem = glassItem;
        this.getStarPiece = getStarPiece;
        this.addStarPiece = addStarPiece;
        this.subtractStarPiece = subtractStarPiece;
        this.starPieceRewardType = starPieceRewardType;
    }

    private static final WeaponsPvE[] vals = values();

    public WeaponsPvE next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public static WeaponsPvE getWeapon(AbstractWeapon abstractWeapon) {
        for (WeaponsPvE value : values()) {
            if (value.weaponClass == abstractWeapon.getClass()) {
                return value;
            }
        }
        return NONE;
    }

    public String getChatColorName() {
        return chatColor.toString() + name;
    }
}
