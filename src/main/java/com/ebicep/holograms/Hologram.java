package com.ebicep.holograms;

import com.ebicep.holograms.lines.AbstractHologramLine;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private String name;
    private Location location;
    private final List<AbstractHologramLine> lines = new ArrayList<>();
    private final HologramVisibility visibility = new HologramVisibility();


    public void sendToPlayer() {
//        for (AbstractHologramLine line : lines) {
//            TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
//            textDisplay.
//        }
    }

    public void appendText(String text) {
        //lines.add(new TextHologramLineBuilder().setText(text).createTextHologramLine());
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<AbstractHologramLine> getLines() {
        return lines;
    }

    public HologramVisibility getVisibility() {
        return visibility;
    }
}
