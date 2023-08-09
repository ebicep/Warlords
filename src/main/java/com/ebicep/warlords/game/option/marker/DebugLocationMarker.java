package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Marks a location for the debug screen/command
 */
public interface DebugLocationMarker extends LocationMarker {

    static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, TextComponent name, Location location) {
        return create(material, data, () -> creator, () -> name, () -> location, Collections::emptyList);
    }

    static DebugLocationMarker create(
            @Nullable Material material,
            int data,
            Supplier<Class<?>> creator,
            Supplier<TextComponent> name,
            Supplier<Location> location,
            Supplier<List<TextComponent>> extra
    ) {
        Material newMaterial = material == null ? Material.BARRIER : material;
        return create(() -> newMaterial, creator, name, location, extra);
    }

    static DebugLocationMarker create(
            Supplier<Material> material,
            Supplier<Class<?>> creator,
            Supplier<TextComponent> name,
            Supplier<Location> location,
            Supplier<List<TextComponent>> extra
    ) {
        return new DebugLocationMarker() {
            @Override
            public String toString() {
                TextComponent.Builder textComponent = Component.text(getCreator().getName() + ": ")
                                                               .append(getName())
                                                               .append(Component.text(": "))
                                                               .append(Component.text(getLocation() + " - "))
                                                               .toBuilder();
                for (TextComponent component : getExtra()) {
                    textComponent.append(component);
                }
                return PlainTextComponentSerializer.plainText().serialize(textComponent.build());
            }

            @Nonnull
            @Override
            public TextComponent getName() {
                return name.get();
            }

            @Nonnull
            @Override
            public List<TextComponent> getExtra() {
                return extra.get();
            }

            @Nonnull
            @Override
            public Class<?> getCreator() {
                return creator.get();
            }

            @Nonnull
            @Override
            public Location getLocation() {
                return location.get();
            }

            @Nonnull
            @Override
            public Material getMaterial() {
                return material.get();
            }
        };
    }

    static DebugLocationMarker create(
            @Nullable Material material,
            int data,
            Class<?> creator,
            TextComponent name,
            Location location,
            Supplier<List<TextComponent>> extra
    ) {
        return create(material, data, () -> creator, () -> name, () -> location, extra);
    }

    static DebugLocationMarker create(
            @Nullable Material material,
            int data,
            Class<?> creator,
            TextComponent name,
            Supplier<Location> location,
            Supplier<List<TextComponent>> extra
    ) {
        return create(material, data, () -> creator, () -> name, location, extra);
    }

    static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, Supplier<TextComponent> name, Supplier<Location> location) {
        return create(material, data, () -> creator, name, location, Collections::emptyList);
    }

    static DebugLocationMarker create(
            @Nullable Material material,
            int data,
            Class<?> creator,
            Supplier<TextComponent> name,
            Supplier<Location> location,
            Supplier<List<TextComponent>> extra
    ) {
        return create(material, data, () -> creator, name, location, extra);
    }

    static DebugLocationMarker create(
            @Nullable Material material,
            int data,
            Supplier<Class<?>> creator,
            Supplier<TextComponent> name,
            Supplier<Location> location
    ) {
        return create(material, data, creator, name, location, Collections::emptyList);
    }

    /**
     * Converts this debug marker into an item for the debug screen
     *
     * @return
     */
    @Nonnull
    default ItemStack getAsItem() {
        ItemBuilder item = new ItemBuilder(getMaterial(), 1);
        TextComponent name = getName();
        String nameContent = name.content();
        TextComponent newName;
        if (name.color() != null) {
            newName = name;
        } else {
            int index = nameContent.indexOf(": ");
            if (index > 0) {
                newName = Component.text(nameContent.substring(0, index + 1), NamedTextColor.GOLD)
                                   .append(Component.text(nameContent.substring(index + 1), NamedTextColor.WHITE));
            } else {
                newName = Component.text(nameContent, NamedTextColor.GOLD);
            }
        }
        item.name(newName);
        Location loc = getLocation();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("XYZ: ", NamedTextColor.GRAY)
                          .append(Component.text(loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + " Yaw/pitch: " + loc.getYaw() + "/" + loc.getPitch(), NamedTextColor.WHITE)));
        lore.add(Component.text("Source: ", NamedTextColor.GRAY).append(Component.text(getCreator().getName(), NamedTextColor.WHITE)));
        for (TextComponent extra : getExtra()) {
            String content = extra.content();
            TextComponent newString;
            if (extra.color() != null) {
                newString = extra;
            } else {
                int index = content.indexOf(": ");
                if (index > 0) {
                    newString = Component.text(content.substring(0, index + 1), NamedTextColor.GRAY).append(Component.text(content.substring(index + 1), NamedTextColor.WHITE));
                } else {
                    newString = Component.text(content, NamedTextColor.WHITE);
                }
            }
            lore.add(newString);
        }
        item.lore(lore);
        return item.get();
    }

    /**
     * Gets the material shown in the debug screen
     *
     * @return
     */
    @Nonnull
    Material getMaterial();

    /**
     * Gets the name of the marker
     *
     * @return the name
     */
    @Nonnull
    TextComponent getName();

    /**
     * Gets the current location of the marker. Returned locations objects may
     * or may not change on their own, and modification of these objects results
     * in undefined behavior, which includes the actual game object moving
     *
     * @return The current location of this marker
     */
    @Nonnull
    @Override
    Location getLocation();

    /**
     * Gets the creator of this debug marker.
     *
     * @return the creator
     */
    @Nonnull
    Class<?> getCreator();

    /**
     * Get the extra debug information for this marker. Different calls to this
     * may return a list of a different length.
     *
     * @return The list of extra debug info
     */
    @Nonnull
    List<TextComponent> getExtra();
}
