package com.ebicep.warlords.game.option.wackamole;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.game.option.wackamole.moles.MoleArmorStand;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("whackamole|wam")
@CommandPermission("group.administrator")
public class WhackAMoleCommand extends BaseCommand {

    @Subcommand("addhead")
    public void addHead(Player player, String id) {
        if (!id.startsWith("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv")) {
            id = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + id;
        }
        MoleArmorStand.HEADS.add(id);
        ChatChannels.sendDebugMessage(player, Component.text("Added mole head " + id, NamedTextColor.GREEN));
    }


}
