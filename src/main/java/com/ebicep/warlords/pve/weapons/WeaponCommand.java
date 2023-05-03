package com.ebicep.warlords.pve.weapons;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;


@CommandAlias("weapon")
@CommandPermission("group.administrator")
@Conditions("database:player")
public class WeaponCommand extends BaseCommand {

    @Subcommand("starter")
    @Description("Gives you a starter weapon")
    public void stater(Player player) {
        giveWeapon(player, new StarterWeapon(player.getUniqueId()));
    }

    public static void giveWeapon(Player player, AbstractWeapon abstractWeapon) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().getWeaponInventory().add(abstractWeapon);
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text("Spawned weapon: ", NamedTextColor.GRAY)
                                                                                .append(abstractWeapon.getName()
                                                                                                      .hoverEvent(abstractWeapon.generateItemStack(false)
                                                                                                                                .asHoverEvent()))
            );
        });
    }

    @Subcommand("common")
    @Description("Give yourself a common weapon")
    public void common(Player player, @Default("1") Integer amount) {
        for (int i = 0; i < amount; i++) {
            giveWeapon(player, new CommonWeapon(player.getUniqueId()));
        }
    }

    @Subcommand("rare")
    @Description("Give yourself a rare weapon")
    public void rare(Player player, @Default("1") Integer amount) {
        for (int i = 0; i < amount; i++) {
            giveWeapon(player, new RareWeapon(player.getUniqueId()));
        }
    }

    @Subcommand("epic")
    @Description("Give yourself a epic weapon")
    public void epic(Player player, @Default("1") Integer amount) {
        for (int i = 0; i < amount; i++) {
            giveWeapon(player, new EpicWeapon(player.getUniqueId()));
        }
    }

    @Subcommand("legendary")
    @Description("Give yourself a legendary weapon with your selected specialization, optional title")
    public void legendary(Player player, @Optional LegendaryTitles title, @Default("0") @Conditions("limits:min=0,max=4") Integer level) {
        if (title != null) {
            AbstractLegendaryWeapon legendaryWeapon = title.create.apply(player.getUniqueId());
            legendaryWeapon.setTitleLevel(level);
            giveWeapon(player, legendaryWeapon);
        } else {
            giveWeapon(player, new LegendaryWeapon(player.getUniqueId()));
        }
    }

    @Subcommand("clear")
    @Description("Clears your weapon inventory")
    public void clear(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().getWeaponInventory().clear();
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text("Cleared weapon inventory.", NamedTextColor.GRAY));
        });
    }

    @Subcommand("list")
    @Description("Lists all your weapons")
    public void list(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
            for (int i = 0; i < weaponInventory.size(); i++) {
                AbstractWeapon weapon = weaponInventory.get(i);
                player.sendMessage(Component.text((i + 1) + ". ", NamedTextColor.GOLD)
                                            .append(weapon.getHoverComponent(false)));
            }
        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
