package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.ItemBuilder;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class Settings {

    public static final String powerupsName = ChatColor.GOLD + "Energy Powerups";
    public static final String powerupsDescription = ChatColor.GRAY + "Replaces energy powerups with damage\n" + ChatColor.GRAY + "boosting powerups";

    public enum Powerup {

        DAMAGE(new ItemStack(Material.WOOL, 1, (short) 14)),
        ENERGY(new ItemStack(Material.WOOL, 1, (short) 1)),

        ;

        public ItemStack item;

        Powerup(ItemStack item) {
            this.item = item;
        }

        public static Powerup getPowerup(String name) {
            for (Powerup value : Powerup.values()) {
                if (value.name().equals(name)) {
                    return value;
                }
            }
            return DAMAGE;
        }

        public static Powerup getSelected(Player player) {
            return player.getMetadata("selected-powerup").stream()
                    .map(v -> v.value() instanceof Powerup ? (Powerup) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(DAMAGE);
        }

        public static void setSelected(Player player, Powerup selectedPowerup) {
            player.removeMetadata("selected-powerup", Warlords.getInstance());
            player.setMetadata("selected-powerup", new FixedMetadataValue(Warlords.getInstance(), selectedPowerup));
        }
    }

    public static final String hotkeyModeName = ChatColor.GREEN + "Hotkey Mode";

    public enum HotkeyMode {

        NEW_MODE(new ItemBuilder(Material.SUGAR_CANE)
                .name(hotkeyModeName)
                .lore(ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "NEW", "", ChatColor.YELLOW + "Click here to enable Classic mode.")
                .get()),
        CLASSIC_MODE(new ItemBuilder(Material.SUGAR)
                .name(hotkeyModeName)
                .lore(ChatColor.YELLOW + "Currently selected " + ChatColor.AQUA + "Classic", "", ChatColor.YELLOW + "Click here to enable NEW mode.")
                .get()),

        ;

        public ItemStack item;

        HotkeyMode(ItemStack item) {
            this.item = item;
        }

        public static HotkeyMode getHotkeyMode(String name) {
            for (HotkeyMode value : HotkeyMode.values()) {
                if (value.name().equals(name)) {
                    return value;
                }
            }
            return NEW_MODE;
        }

        public static HotkeyMode getSelected(Player player) {
            return player.getMetadata("selected-hotkeymode").stream()
                    .map(v -> v.value() instanceof HotkeyMode ? (HotkeyMode) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(NEW_MODE);
        }

        public static void setSelected(Player player, HotkeyMode selectedHotkeyMode) {
            player.removeMetadata("selected-hotkeymode", Warlords.getInstance());
            player.setMetadata("selected-hotkeymode", new FixedMetadataValue(Warlords.getInstance(), selectedHotkeyMode));
            Warlords.getPlayerSettings(player.getUniqueId()).setHotKeyMode(selectedHotkeyMode == NEW_MODE);
        }
    }

    public enum ParticleQuality {

        LOW(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 1).name(ChatColor.GOLD + "Low Quality").get(), ChatColor.GRAY + "Heavily reduces the amount of\n" + ChatColor.GRAY + "particles you will see.", 2),
        MEDIUM(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 4).name(ChatColor.YELLOW + "Medium Quality").get(), ChatColor.GRAY + "Reduces the amount of particles\n" + ChatColor.GRAY + "seem.", 4),
        HIGH(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).name(ChatColor.GREEN + "High Quality").get(), ChatColor.GRAY + "Shows all particles for the best\n" + ChatColor.GRAY + "experience.", 100000),

        ;

        public ItemStack item;
        public String description;
        public int particleReduction;

        ParticleQuality(ItemStack item, String description, int particleReduction) {
            this.item = item;
            this.description = description;
            this.particleReduction = particleReduction;
        }

        public static ParticleQuality getParticleQuality(String name) {
            for (ParticleQuality value : ParticleQuality.values()) {
                if (value.name().equals(name)) {
                    return value;
                }
            }
            return HIGH;
        }

        public static ParticleQuality getSelected(Player player) {
            return player.getMetadata("selected-particle-quality").stream()
                    .map(v -> v.value() instanceof ParticleQuality ? (ParticleQuality) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(MEDIUM);
        }

        public static void setSelected(Player player, ParticleQuality selectedParticleQuality) {
            player.removeMetadata("selected-particle-quality", Warlords.getInstance());
            player.setMetadata("selected-particle-quality", new FixedMetadataValue(Warlords.getInstance(), selectedParticleQuality));
            Warlords.getPlayerSettings(player.getUniqueId()).setParticleQuality(selectedParticleQuality);
        }
    }
}
