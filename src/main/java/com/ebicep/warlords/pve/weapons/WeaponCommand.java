package com.ebicep.warlords.pve.weapons;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.LegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


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
    @Description("Give yourself a legendary weapon with your selected specialization")
    public void legendary(Player player) {
        giveWeapon(player, new LegendaryWeapon(player.getUniqueId()));
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
