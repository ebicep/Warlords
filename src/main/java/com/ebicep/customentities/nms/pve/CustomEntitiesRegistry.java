package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum CustomEntitiesRegistry {

    //https://minecraft.fandom.com/wiki/Java_Edition_data_values/Pre-flattening/Entity_IDs

    CREEPER("Creeper", 50, EntityCreeper.class, CustomCreeper.class),
    SKELETON("Skeleton", 51, EntitySkeleton.class, CustomSkeleton.class),
    SPIDER("Spider", 52, EntitySpider.class, CustomSpider.class),
    GIANT("Giant", 53, EntityGiantZombie.class, CustomGiant.class),
    ZOMBIE("Zombie", 54, EntityZombie.class, CustomZombie.class),
    SLIME("Slime", 55, EntitySlime.class, CustomSlime.class),
    GHAST("Ghast", 56, EntityGhast.class, CustomGhast.class),
    ZOMBIE_PIGMAN("PigZombie", 57, EntityPigZombie.class, CustomPigZombie.class),
    ENDERMAN("Enderman", 58, EntityEnderman.class, CustomEnderman.class),
    //    CAVE_SPIDER("CaveSpider", 59, EntityCaveSpider.class, CustomCaveSpider.class),
    //    SILVERFISH("Silverfish", 60, EntitySilverfish.class, CustomSilverfish.class),
    BLAZE("Blaze", 61, EntityBlaze.class, CustomBlaze.class),
    MAGMA_CUBE("LavaSlime", 62, EntityMagmaCube.class, CustomMagmaCube.class),
    //    ENDER_DRAGON("EnderDragon", 63, EntityEnderDragon.class, CustomEnderDragon.class),
//    WITHER("WitherBoss", 64, EntityWither.class, CustomWither.class),
//    BAT("Bat", 65, EntityBat.class, CustomBat.class),
//    WITCH("Witch", 66, EntityWitch.class, CustomWitch.class),
//    ENDERMITE("Endermite", 67, EntityEndermite.class, CustomEndermite.class),
    GUARDIAN("Guardian", 68, EntityGuardian.class, CustomGuardian.class),
//    SHULKER("Shulker", 69, EntityShulker.class, CustomShulker.class),
//    PIG("Pig", 90, EntityPig.class, CustomPig.class),
//    SHEEP("Sheep", 91, EntitySheep.class, CustomSheep.class),
//    COW("Cow", 92, EntityCow.class, CustomCow.class),
//    CHICKEN("Chicken", 93, EntityChicken.class, CustomChicken.class),
//    SQUID("Squid", 94, EntitySquid.class, CustomSquid.class),
//    WOLF("Wolf", 95, EntityWolf.class, CustomWolf.class),
//    MOOSHROOM("MushroomCow", 96, EntityMushroomCow.class, CustomMooshroom.class),
//    SNOWMAN("SnowMan", 97, EntitySnowman.class, CustomSnowman.class),
//    OCELOT("Ocelot", 98, EntityOcelot.class, CustomOcelot.class),
    IRON_GOLEM("VillagerGolem", 99, EntityIronGolem.class, CustomIronGolem.class),
//    HORSE("Horse", 100, EntityHorse.class, CustomHorse.class),
//    RABBIT("Rabbit", 101, EntityRabbit.class, CustomRabbit.class),
//    VILLAGER("Villager", 120, EntityVillager.class, CustomVillager.class),


    ;

    public final String name;
    public final int id;
    public final Class<? extends EntityInsentient> nmsClass;
    public final Class<? extends EntityInsentient> customClass;

    CustomEntitiesRegistry(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
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
        for (CustomEntitiesRegistry value : CustomEntitiesRegistry.values()) {
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
