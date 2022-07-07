package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class AbstractWeapon {

    @Field("obtain_date")
    protected Date date = new Date();
    @Field("melee_damage")
    protected int meleeDamage;
    @Field("crit_chance_bonus")
    protected int critChance;
    @Field("crit_multiplier_bonus")
    protected int critMultiplier;
    @Field("health_bonus")
    protected int healthBonus;
    @Field("weapon_skin")
    protected Weapons selectedWeaponSkin = Weapons.STEEL_SWORD;
    @Field("unlocked_weapon_skins")
    protected List<Weapons> unlockedWeaponSkins = new ArrayList<>();
    @Field("bound_spec")
    protected Specializations boundedToSpec;
    @Field("star_piece_bonus")
    protected WeaponStats starPieceBonus;

    public AbstractWeapon() {
        generateStats();
    }

    public abstract List<String> getLore();

    public abstract void generateStats();

    public ItemStack generateItemStack() {
        List<String> lore = new ArrayList<>();
        lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + meleeDamage,
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + critChance + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + critMultiplier + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + healthBonus
        ));
        lore.addAll(getLore());
        if (boundedToSpec != null) {
            lore.add("");
            lore.add(ChatColor.AQUA + "Bound - " + boundedToSpec.name);
        }
        return new ItemBuilder(selectedWeaponSkin.getItem())
                .name(WeaponsPvE.getWeapon(this).getGeneralName())
                .lore(lore)
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }

    public ItemStack generateItemStackInLore(String name) {
        List<String> lore = new ArrayList<>();
        lore.add(WeaponsPvE.getWeapon(this).getGeneralName());
        lore.add("");
        lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + meleeDamage,
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + critChance + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + critMultiplier + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + healthBonus
        ));
        lore.addAll(getLore());
        if (boundedToSpec != null) {
            lore.add("");
            lore.add(ChatColor.AQUA + "Bound - " + boundedToSpec.name);
        }
        return new ItemBuilder(selectedWeaponSkin.getItem())
                .name(name)
                .lore(lore)
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }


    @Override
    public String toString() {
        return "AbstractWeapon{" +
                "meleeDamage=" + meleeDamage +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                ", healthBonus=" + healthBonus +
                '}';
    }

    public static void giveTestItem(Player player) {
        AbstractWeapon abstractWeapon = new CommonWeapon();
        AbstractWeapon abstractWeapon2 = new RareWeapon();
        AbstractWeapon abstractWeapon3 = new EpicWeapon();
        AbstractWeapon abstractWeapon4 = new LegendaryWeapon(player.getUniqueId());

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon);
        databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon2);
        databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon3);
        databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon4);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.spigot().sendMessage(new TextComponentBuilder(ChatColor.GOLD + "Test Weapon 1")
                .setHoverItem(abstractWeapon.generateItemStack())
                .getTextComponent()
        );

        player.spigot().sendMessage(new TextComponentBuilder(ChatColor.GOLD + "Test Weapon 4")
                .setHoverItem(abstractWeapon4.generateItemStack())
                .getTextComponent()
        );
    }

    public Date getDate() {
        return date;
    }

    public int getMeleeDamage() {
        return meleeDamage;
    }

    public int getCritChance() {
        return critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public int getHealthBonus() {
        return healthBonus;
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

    public Specializations getBoundedToSpec() {
        return boundedToSpec;
    }

    public void setBoundedToSpec(Specializations boundedToSpec) {
        this.boundedToSpec = boundedToSpec;
    }
}
