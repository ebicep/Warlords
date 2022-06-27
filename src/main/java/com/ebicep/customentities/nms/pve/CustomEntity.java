package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;

public interface CustomEntity {

    void spawn(Location location);

    EntityInsentient get();


}
