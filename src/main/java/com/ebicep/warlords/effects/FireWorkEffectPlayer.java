package com.ebicep.warlords.effects;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Level;

public class FireWorkEffectPlayer {

    private static Method firework_getHandle = null;
    private static Field fireworkTicksLived = null;
    private static Field expectedLifespan = null;
    private static boolean fireworksDisabled = false;

    public static boolean playFirework(Location loc, FireworkEffect... fe) {
        return playFirework(loc.getWorld(), loc, fe);
    }

    /**
     * Play a pretty firework at the location with the FireworkEffect when
     * called
     *
     * @param world
     * @param loc
     * @param fe
     * @return
     */
    public static boolean playFirework(World world, Location loc, FireworkEffect... fe) {
        Objects.requireNonNull(world, "world == null");
        Objects.requireNonNull(loc, "loc == null");
        Objects.requireNonNull(fe, "fe == null");
        for (int i = 0; i < fe.length; i++) {
            Objects.requireNonNull(fe[i++], "fe[" + i + "] == null");
        }
        if (!loc.getWorld().equals(world)) {
            throw new IllegalArgumentException("loc not in world");
        }
        if (fireworksDisabled) {
            return false;
        }
        try {
            playFirework0(world, loc, fe);
            return true;
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchFieldException | NoSuchMethodException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot use fireworks in this server", ex);
            fireworksDisabled = true;
        }
        return false;
    }

    private static void playFirework0(World world, Location loc, FireworkEffect... fe) throws
            InvocationTargetException, IllegalArgumentException, IllegalAccessException,
            SecurityException, NoSuchFieldException, NoSuchMethodException {

        Objects.requireNonNull(world);
        // Bukkit load (CraftFirework)
        Firework fw = (Firework) world.spawn(loc, Firework.class);
        fw.teleport(loc);
        Object nms_firework;
        if (firework_getHandle == null) {
            firework_getHandle = getMethod(fw.getClass(), "getHandle");

        }
        nms_firework = firework_getHandle.invoke(fw, (Object[]) null);
        if (fireworkTicksLived == null) {
            fireworkTicksLived = nms_firework.getClass().getDeclaredField("ticksFlown");
            fireworkTicksLived.setAccessible(true);
            expectedLifespan = nms_firework.getClass().getDeclaredField("expectedLifespan");
            expectedLifespan.setAccessible(true);
        }
        FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
        data.clearEffects();
        data.addEffect(fe[0]);
        fw.setFireworkMeta(data);
        //System.out.println(fw.getFireworkMeta());
        fireworkTicksLived.set(nms_firework, 1);
        expectedLifespan.set(nms_firework, 2);
    }

    private static Method getMethod(Class<?> cl, String method) throws NoSuchMethodException {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        throw new NoSuchMethodException(method + " on class " + cl);
    }

}
