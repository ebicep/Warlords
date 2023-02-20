package com.ebicep.warlords.pve.items;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.Item;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveDamageCooldown;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.pve.SkullUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum Items {

    SPEED_1() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(10);
        }
    },
    SPEED_2() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(20);
        }
    },
    SPEED_3() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(30);
        }
    },
    ENERGY_1() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 5);
        }
    },
    ENERGY_2() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 10);
        }
    },
    ENERGY_3() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpec().setEnergyPerSec(warlordsPlayer.getSpec().getEnergyPerSec() + 15);
        }
    },
    POWER_1() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            ItemAdditiveDamageCooldown.applyToPlayer(warlordsPlayer, 10);
        }
    },
    POWER_2() {
        @Override
        public void applyToPlayer(WarlordsPlayer warlordsPlayer) {
            ItemAdditiveDamageCooldown.applyToPlayer(warlordsPlayer, 20);
        }
    },
    POWER_3() {
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

    public String getSkullID() {
        return skullID;
    }

    public void setSkullID(String skullID) {
        this.skullID = skullID;
    }

    public String getSkullTextureID() {
        return skullTextureID;
    }

    public void setSkullTextureID(String skullTextureID) {
        this.skullTextureID = skullTextureID;
    }

    private String name;
    private int weight;
    private ItemAttribute attribute;
    private ItemFamily family;
    private WeaponsRarity rarity;
    private String description;
    private String skullID;
    private String skullTextureID;

    Items() {
    }

    public void applyToPlayer(WarlordsPlayer warlordsPlayer) {

    }

    public ItemStack generateItemStack() {
        return generateItemBuilder().get();
    }

    public ItemBuilder generateItemBuilder() {
        return new ItemBuilder(SkullUtils.getSkullFrom(skullID, skullTextureID))
                .name(ChatColor.GREEN + name)
                .lore(
                        ChatColor.GRAY + "Weight: " + ChatColor.GREEN + weight,
                        ChatColor.GRAY + "Attribute: " + ChatColor.GREEN + attribute.name,
                        ChatColor.GRAY + "Family: " + ChatColor.GREEN + family.name,
                        ChatColor.GRAY + "Rarity: " + rarity.coloredName(),
                        "",
                        WordWrap.wrapWithNewline(ChatColor.GRAY + description, 150)
                );
    }
}
