package com.ebicep.warlords.pve.items;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.Item;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveDamageCooldown;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

public enum Items {

    SPEED_1(ItemAttribute.ALPHA,
            ItemFamily.SPEED_BASIC,
            WeaponsRarity.COMMON
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(10);
        }
    },
    SPEED_2(ItemAttribute.ALPHA,
            ItemFamily.SPEED_BASIC,
            WeaponsRarity.RARE
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(20);
        }
    },
    SPEED_3(ItemAttribute.ALPHA,
            ItemFamily.SPEED_BASIC,
            WeaponsRarity.EPIC
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(30);
        }
    },
    ENERGY_1(ItemAttribute.BETA,
            ItemFamily.ENERGY_BASIC,
            WeaponsRarity.COMMON
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 5);
        }
    },
    ENERGY_2(ItemAttribute.BETA,
            ItemFamily.ENERGY_BASIC,
            WeaponsRarity.RARE
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 10);
        }
    },
    ENERGY_3(ItemAttribute.BETA,
            ItemFamily.ENERGY_BASIC,
            WeaponsRarity.EPIC
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 15);
        }
    },
    POWER_1(ItemAttribute.GAMMA,
            ItemFamily.POWER_BASIC,
            WeaponsRarity.COMMON
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            ItemAdditiveDamageCooldown.applyToPlayer(warlordsPlayer, 10);
        }
    },
    POWER_2(ItemAttribute.GAMMA,
            ItemFamily.POWER_BASIC,
            WeaponsRarity.RARE
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            ItemAdditiveDamageCooldown.applyToPlayer(warlordsPlayer, 20);
        }
    },
    POWER_3(ItemAttribute.GAMMA,
            ItemFamily.POWER_BASIC,
            WeaponsRarity.EPIC
    ) {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            ItemAdditiveDamageCooldown.applyToPlayer(warlordsPlayer, 30);
        }
    },

    ;

    public static final Items[] VALUES = values();

    public static void reload() {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.itemService.findAll())
                .syncLast(items -> {
                    for (Item item : items) {
                        item.importToItem();
                        ChatChannels.sendDebugMessage((CommandIssuer) null, ChatColor.GREEN + "Loaded item " + item.getItem(), false);
                    }
                }).execute();
    }

    public static void printAll(CommandIssuer issuer) {
        for (Items item : values()) {
            ChatChannels.sendDebugMessage(issuer,
                    ChatColor.GREEN + item.getName() + " | " + item.getWeight() + " | " + item.getAttribute() + " | " + item.getFamily() + " | " + item.getRarity() + " | " + item.getDescription(),
                    false
            );
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ItemAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ItemAttribute attribute) {
        this.attribute = attribute;
    }

    public ItemFamily getFamily() {
        return family;
    }

    public WeaponsRarity getRarity() {
        return rarity;
    }

    public void setRarity(WeaponsRarity rarity) {
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFamily(ItemFamily family) {
        this.family = family;
    }

    private String name;
    private int weight;
    private ItemAttribute attribute;
    private ItemFamily family;
    private WeaponsRarity rarity;
    private String description;

    Items() {
    }

    Items(ItemAttribute attribute, ItemFamily family, WeaponsRarity rarity) {
        this.attribute = attribute;
        this.family = family;
        this.rarity = rarity;
    }

    public void applyToPlayer(WarlordsPlayer warlordsPlayer) {

    }
}
