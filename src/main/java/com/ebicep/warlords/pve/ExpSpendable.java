package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ExperienceManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public enum ExpSpendable implements Spendable {

    SPEC("Spec Experience", TextColor.color(140, 204, 205)) {
        @Override
        public ItemStack getItem() {
            return new ItemStack(Material.EXPERIENCE_BOTTLE);
        }

        @Override
        public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
            DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(databasePlayer.getLastSpec());
            databasePlayerSpec.setExperience(databasePlayerSpec.getExperience() + amount);
            UUID uuid = databasePlayer.getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                ExperienceManager.checkForPrestige(player, uuid, databasePlayer);
            }
        }

        @Override
        public Long getFromPlayer(DatabasePlayer databasePlayer) {
            return databasePlayer.getSpec(databasePlayer.getLastSpec()).getExperience();
        }
    };

    public final String name;
    public final TextColor textColor;

    ExpSpendable(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextColor getTextColor() {
        return textColor;
    }
}
