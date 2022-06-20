package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityZombie;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum CustomEntities {

    //https://minecraft.fandom.com/wiki/Java_Edition_data_values/Pre-flattening/Entity_IDs

    ZOMBIE("Zombie", 54, EntityZombie.class, CustomZombie.class),
    SKELETON("Skeleton", 51, EntitySkeleton.class, CustomSkeleton.class);

    public final String name;
    public final int id;
    public final Class<? extends EntityInsentient> nmsClass;
    public final Class<? extends EntityInsentient> customClass;

    CustomEntities(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
        this.name = name;
        this.id = id;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

//    public static <T extends EntityInsentient> void registerEntity(T entity, Class<T> customClass) {
//        for (CustomEntities value : CustomEntities.values()) {
//            if(value.customClass == customClass) {
//                registerEntity(value.name, value.id, value.nmsClass, value.entityType);
//                return;
//            }
//        }
//    }

    public static void registerEntities() {
        for (CustomEntities value : CustomEntities.values()) {
            registerEntity(value.name, value.id, value.nmsClass, value.customClass);
        }
    }

    public static void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
        try {
            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
