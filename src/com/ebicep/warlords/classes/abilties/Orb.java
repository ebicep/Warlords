package com.ebicep.warlords.classes.abilties;

import org.bukkit.World;

//public class Orb extends EntityExperienceOrb {
//    public Orb(World world) {
//        super(world);
//    }
//
//    @Override
//    public void b_(EntityHuman entityhuman) {}
//
//    public static FairyOrb spawn(Location loc) {
//        World w = ((CraftWorld) loc.getWorld()).getHandle();
//        FairyOrb f = new FairyOrb(w);
//        f.setPosition(loc.getX(), loc.getY(), loc.getZ());
//        w.addEntity(f, SpawnReason.CUSTOM);
//        return f;
//    }
//
//    public static void registerEntity() {
//        try {
//            Method a = net.minecraft.server.v1_6_R3.EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
//            a.setAccessible(true);
//            a.invoke(a, FairyOrb.class, "ExperienceOrb", 301);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}