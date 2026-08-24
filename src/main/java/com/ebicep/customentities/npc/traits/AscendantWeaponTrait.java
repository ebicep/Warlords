package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.weapons.menu.WeaponCraftMenu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class AscendantWeaponTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-ascendant-weapon");

    public AscendantWeaponTrait() {
        super("AscendantWeaponTrait");
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
        labelHologram.update(
                npc,
                ComponentBuilder.create("The Vaultkeeper", NamedTextColor.RED).build()
        );
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player.getUniqueId());
        WeaponCraftMenu.openWeaponAscendantCraftMenu(player, databasePlayer);
    }
}
