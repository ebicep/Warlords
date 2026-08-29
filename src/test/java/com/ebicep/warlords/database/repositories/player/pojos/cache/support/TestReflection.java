package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public final class TestReflection {

    private TestReflection() {
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to set " + target.getClass().getSimpleName() + "." + fieldName, exception);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T getField(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to read " + target.getClass().getSimpleName() + "." + fieldName, exception);
        }
    }

    public static void addToCollection(Object target, String fieldName, Object element) {
        Collection<Object> collection = getField(target, fieldName);
        collection.add(element);
    }

    public static void updatePyromancerLeaf(
            Object classAggregator,
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer,
            Object game,
            com.ebicep.warlords.game.GameMode gameMode,
            com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase gamePlayer,
            com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult result,
            int multiplier,
            com.ebicep.warlords.database.repositories.player.PlayersCollections playersCollection
    ) {
        invoke(
                classAggregator,
                "updateSpecStats",
                databasePlayer,
                game,
                gameMode,
                gamePlayer,
                result,
                multiplier,
                playersCollection
        );
    }

    public static Object invoke(Object target, String methodName, Object... args) {
        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) {
                continue;
            }
            try {
                return method.invoke(target, args);
            } catch (IllegalArgumentException ignored) {
                // try next overload
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Failed to invoke " + methodName + " on " + target.getClass().getSimpleName(), exception);
            }
        }
        throw new IllegalStateException("No matching method " + methodName + " on " + target.getClass().getSimpleName());
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
