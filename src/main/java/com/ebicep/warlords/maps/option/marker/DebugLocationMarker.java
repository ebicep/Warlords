package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Marks a location for the debug screen/command
 */
public interface DebugLocationMarker extends LocationMarker {

    /**
     * Gets the name of the marker
     *
     * @return the name
     */
    @Nonnull
    public String getName();

    /**
     * Get the extra debug information for this marker. Different calls to this
     * may return a list of a different length.
     *
     * @return The list of extra debug info
     */
    @Nonnull
    public List<String> getExtra();

    /**
     * Gets the creator of this debug marker.
     *
     * @return the creator
     */
    @Nonnull
    public Class<?> getCreator();

    /**
     * Gets the current location of the marker. Returned locations objects may
     * or may not change on their own, and modification of these objects results
     * in undefined behavior, which includes the actual game object moving
     *
     * @return The current location of this marker
     */
    @Nonnull
    @Override
    public Location getLocation();

    /**
     * Gets the material shown in the debug screen
     *
     * @return
     */
    @Nonnull
    public Material getMaterial();

    /**
     * Gets the data value belonging to the
     *
     * @return
     */
    public short getMaterialData();

    /**
     * Converts this debug marker into an item for the debug screen
     *
     * @return
     */
    @Nonnull
    public default ItemStack getAsItem() {
        ItemBuilder item = new ItemBuilder(getMaterial(), getMaterialData());
        item.name(getName());
        Location loc = getLocation();
        List<String> lore = new ArrayList<>();
        lore.add("X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ() + " Yaw" + loc.getYaw() + " Pitch " + loc.getPitch());
        lore.add("Source: " + getCreator().getName());
        lore.addAll(getExtra());
        item.lore(lore);
        return item.get();
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, String name, Location location) {
        return create(material, data, () -> creator, () -> name, () -> location, Collections::emptyList);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, String name, Location location, Supplier<List<String>> extra) {
        return create(material, data, () -> creator, () -> name, () -> location, extra);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, String name, Supplier<Location> location, Supplier<List<String>> extra) {
        return create(material, data, () -> creator, () -> name, location, extra);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, Supplier<String> name, Supplier<Location> location) {
        return create(material, data, () -> creator, name, location, Collections::emptyList);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Class<?> creator, Supplier<String> name, Supplier<Location> location, Supplier<List<String>> extra) {
        return create(material, data, () -> creator, name, location, extra);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Supplier<Class<?>> creator, Supplier<String> name, Supplier<Location> location) {
        return create(material, data, creator, name, location, Collections::emptyList);
    }

    public static DebugLocationMarker create(@Nullable Material material, int data, Supplier<Class<?>> creator, Supplier<String> name, Supplier<Location> location, Supplier<List<String>> extra) {
        Material newMaterial = material == null ? Material.BARRIER : material;
        short newData = (short) data;
        return new DebugLocationMarker() {
            @Override
            public String getName() {
                return name.get();
            }

            @Override
            public Class<?> getCreator() {
                return creator.get();
            }

            @Override
            public Location getLocation() {
                return location.get();
            }

            @Override
            public List<String> getExtra() {
                return extra.get();
            }

            @Override
            public Material getMaterial() {
                return newMaterial;
            }

            @Override
            public short getMaterialData() {
                return newData;
            }
        };
    }
}
