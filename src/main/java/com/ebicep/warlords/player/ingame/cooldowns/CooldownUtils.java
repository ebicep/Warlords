package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddPotionEffectEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddSpeedModifierEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerStunEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CooldownUtils {

    public static List<AbstractCooldown<?>> getPrioritizedCooldowns(List<AbstractCooldown<?>> cooldowns, String method, Class<?>... parameterTypes) {
        return cooldowns
                .stream()
                .sorted((o1, o2) -> {
                    try {
                        Priority o1Priority = o1.getClass()
                                                .getMethod(method, parameterTypes)
                                                .getAnnotation(Priority.class);
                        Priority o2Priority = o2.getClass()
                                                .getMethod(method, parameterTypes)
                                                .getAnnotation(Priority.class);
                        return Integer.compare(
                                o1Priority == null ? 0 : o1Priority.value(),
                                o2Priority == null ? 0 : o2Priority.value()
                        );
                    } catch (Exception e) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    public static Listener getDefaultDebuffImmunityListener(WarlordsEntity immune) {
        return getDebuffImmunityListener(DebuffImmunity.getDefaultImmunity(immune));
    }

    public static Listener getDebuffImmunityListener(DebuffImmunity defaultImmunity) {
        return new Listener() {

            @EventHandler
            public void onAddCooldown(WarlordsAddCooldownEvent event) {
                if (event.getWarlordsEntity() != defaultImmunity.warlordsEntity) {
                    return;
                }
                event.setCancelled(defaultImmunity.cooldownPredicate.test(event));
            }

            @EventHandler
            public void onAddSpeed(WarlordsAddSpeedModifierEvent event) {
                if (event.getWarlordsEntity() != defaultImmunity.warlordsEntity) {
                    return;
                }
                event.setCancelled(defaultImmunity.speedPredicate.test(event));
            }

            @EventHandler
            public void onAddPotionEffect(WarlordsAddPotionEffectEvent event) {
                if (event.getWarlordsEntity() != defaultImmunity.warlordsEntity) {
                    return;
                }
                event.setCancelled(defaultImmunity.potionPredicate.test(event));
            }

            @EventHandler
            public void onStunEvent(WarlordsPlayerStunEvent event) {
                if (event.getWarlordsEntity() != defaultImmunity.warlordsEntity) {
                    return;
                }
                event.setCancelled(defaultImmunity.stunPredicate.test(event));
            }

        };
    }

    public static class DebuffImmunity {

        public static final Predicate<WarlordsAddCooldownEvent> DEFAULT_COOLDOWN = event -> event.getAbstractCooldown().getCooldownType() == CooldownTypes.LOW_LEVEL_DEBUFF;
        public static final Predicate<WarlordsAddSpeedModifierEvent> DEFAULT_SPEED = event -> event.getMotionModifier().getModifier() < 0;
        public static final Predicate<WarlordsAddPotionEffectEvent> DEFAULT_POTION = event ->
                PotionEffectType.BLINDNESS.equals(event.getPotionEffect().getType()) ||
                        PotionEffectType.NAUSEA.equals(event.getPotionEffect().getType());
        public static final Predicate<WarlordsPlayerStunEvent> DEFAULT_STUN = event -> true;

        public static DebuffImmunity getDefaultImmunity(WarlordsEntity warlordsEntity) {
            return DebuffImmunity
                    .create(warlordsEntity)
                    .cooldownPredicate(DEFAULT_COOLDOWN)
                    .speedPredicate(DEFAULT_SPEED)
                    .potionPredicate(DEFAULT_POTION);
        }

        public static DebuffImmunity create(WarlordsEntity warlordsEntity) {
            return new DebuffImmunity(warlordsEntity);
        }

        public DebuffImmunity speedPredicate(Predicate<WarlordsAddSpeedModifierEvent> predicate) {
            this.speedPredicate = predicate;
            return this;
        }

        private final WarlordsEntity warlordsEntity;
        private Predicate<WarlordsAddCooldownEvent> cooldownPredicate = event -> false;
        private Predicate<WarlordsAddSpeedModifierEvent> speedPredicate = event -> false;
        private Predicate<WarlordsAddPotionEffectEvent> potionPredicate = event -> false;
        private Predicate<WarlordsPlayerStunEvent> stunPredicate = event -> false;

        public DebuffImmunity(WarlordsEntity warlordsEntity) {
            this.warlordsEntity = warlordsEntity;
        }

        public DebuffImmunity potionPredicate(Predicate<WarlordsAddPotionEffectEvent> predicate) {
            this.potionPredicate = predicate;
            return this;
        }

        public DebuffImmunity cooldownPredicate(Predicate<WarlordsAddCooldownEvent> predicate) {
            this.cooldownPredicate = predicate;
            return this;
        }

        public DebuffImmunity stunPredicate(Predicate<WarlordsPlayerStunEvent> predicate) {
            this.stunPredicate = predicate;
            return this;
        }

        public DebuffImmunity cooldownPredicate() {
            this.cooldownPredicate = DEFAULT_COOLDOWN;
            return this;
        }

        public DebuffImmunity speedPredicate() {
            this.speedPredicate = DEFAULT_SPEED;
            return this;
        }

        public DebuffImmunity potionPredicate() {
            this.potionPredicate = DEFAULT_POTION;
            return this;
        }

        public DebuffImmunity stunPredicate() {
            this.stunPredicate = DEFAULT_STUN;
            return this;
        }

    }

}
