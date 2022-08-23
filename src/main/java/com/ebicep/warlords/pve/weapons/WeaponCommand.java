package com.ebicep.warlords.pve.weapons;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;


@CommandAlias("weapon")
@Conditions("database:player")
public class WeaponCommand extends BaseCommand {

    public static void giveWeapon(Player player, AbstractWeapon abstractWeapon) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                new TextComponent(ChatColor.GRAY + "Spawned weapon: "),
                new TextComponentBuilder(abstractWeapon.getName())
                        .setHoverItem(abstractWeapon.generateItemStack())
                        .getTextComponent());
    }

    @Subcommand("starter")
    @Description("Gives you a starter weapon")
    public void stater(Player player) {
        giveWeapon(player, new StarterWeapon(player.getUniqueId()));
    }

    @Subcommand("common")
    @Description("Give yourself a common weapon")
    public void common(Player player) {
        giveWeapon(player, new CommonWeapon(player.getUniqueId()));
    }

    @Subcommand("rare")
    @Description("Give yourself a rare weapon")
    public void rare(Player player) {
        giveWeapon(player, new RareWeapon(player.getUniqueId()));
    }

    @Subcommand("epic")
    @Description("Give yourself a epic weapon")
    public void epic(Player player) {
        giveWeapon(player, new EpicWeapon(player.getUniqueId()));
    }

    @Subcommand("legendary")
    @Description("Give yourself a legendary weapon with your selected specialization, optional title")
    public void legendary(Player player, @Optional LegendaryTitles title) {
        if (title != null) {
            giveWeapon(player, title.create.apply(player.getUniqueId()));
        } else {
            giveWeapon(player, new LegendaryWeapon(player.getUniqueId()));
        }
    }

    @Subcommand("clear")
    @Description("Clears your weapon inventory")
    public void clear(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        databasePlayer.getPveStats().getWeaponInventory().clear();
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                new TextComponent(ChatColor.GRAY + "Cleared weapon inventory."));
    }

    @Subcommand("list")
    @Description("Lists all your weapons")
    public void list(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        for (int i = 0; i < weaponInventory.size(); i++) {
            AbstractWeapon weapon = weaponInventory.get(i);
            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GOLD.toString() + (i + 1) + ". "),
                    new TextComponentBuilder(weapon.getName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent());
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
