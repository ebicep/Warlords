package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Gambler extends BaseSet {

    private int randomEffectIntervalSeconds;
    private List<GamblerEffect> randomEffects;

    @Override
    public void init() {
        super.init();
        this.randomEffectIntervalSeconds = getValue("randomEffectIntervalSeconds", int.class);
        this.randomEffects = getListValue("randomEffects", GamblerEffect.class);
    }

    @Override
    public String getConfigFieldName() {
        return "gambler";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(randomEffectIntervalSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            if (randomEffectIntervalSeconds <= 0 || randomEffects.isEmpty()) {
                return;
            }

            CooldownManager cooldownManager =
                    warlordsPlayer.getCooldownManager();

            cooldownManager.removeCooldown(Gambler.class, false);

            cooldownManager.addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Gambler.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    manager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (warlordsPlayer.isDead()) {
                            return;
                        }

                        if (warlordsPlayer.getGame().getState()
                                instanceof EndState) {
                            return;
                        }

                        int intervalTicks = randomEffectIntervalSeconds * 20;

                        if (ticksElapsed <= 0 || ticksElapsed % intervalTicks != 0) {
                            return;
                        }

                        GamblerEffect effect = randomEffects.get(
                                ThreadLocalRandom.current()
                                        .nextInt(randomEffects.size())
                        );

                        applyEffect(warlordsPlayer, effect);
                    }
            ));
        }

        private void applyEffect(
                WarlordsPlayer warlordsPlayer,
                GamblerEffect effect
        ) {
            CooldownManager cooldownManager =
                    warlordsPlayer.getCooldownManager();

            cooldownManager.removeCooldown(
                    GamblerEffect.class,
                    false
            );

            switch (effect) {
                case DOUBLE_DAMAGE_10S ->
                        applyDoubleDamage(warlordsPlayer, effect);

                case DOUBLE_HEALING_10S ->
                        applyDoubleHealing(warlordsPlayer, effect);

                case TRIPLE_ENERGY_GAIN_10S ->
                        applyTripleEnergyGain(warlordsPlayer, effect);

                case NO_ENERGY_REGEN_10S ->
                        applyNoEnergyRegen(warlordsPlayer, effect);

                case SLOW_90_PERCENT_10S ->
                        applySlow(warlordsPlayer, effect);

                case TURN_INTO_FROG_15S ->
                        applyFrogDisguise(warlordsPlayer, effect);
            }

            warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.5f);
            warlordsPlayer.sendMessage(Component.text("Gambler rolled: ", NamedTextColor.GRAY, TextDecoration.BOLD)
                            .append(Component.text(
                                    effect.displayName,
                                    effect.color
                            ))
            );
        }

        private void applyDoubleDamage(
                WarlordsPlayer warlordsPlayer,
                GamblerEffect effect
        ) {
            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> {
                            }
                    );

            cooldown.addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (event.getWarlordsEntity()
                                .equals(warlordsPlayer)) {
                            return;
                        }

                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType
                                        .MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                2
                        );
                    }
            );

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private void applyDoubleHealing(
                WarlordsPlayer warlordsPlayer,
                GamblerEffect effect
        ) {
            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> {
                            }
                    );

            cooldown.addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealingValue) ->
                            currentHealingValue.addModifier(
                                    FloatModifiable.ModifierType
                                            .MULTIPLICATIVE_MULTIPLIER,
                                    getName(),
                                    2
                            )
            );

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private void applyTripleEnergyGain(WarlordsPlayer warlordsPlayer, GamblerEffect effect) {
            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> {}
                    );

            cooldown.addModifier(
                    Modifier.ENERGY_GAIN_PER_TICK,
                    energyGain -> energyGain.addModifier(
                            FloatModifiable.ModifierType
                                    .MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            3
                    )
            );

            cooldown.addModifier(
                    Modifier.ENERGY_GAIN_PER_HIT,
                    energyGain -> energyGain.addModifier(
                            FloatModifiable.ModifierType
                                    .MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            3
                    )
            );

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private void applyNoEnergyRegen(WarlordsPlayer warlordsPlayer, GamblerEffect effect) {
            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> {}
                    );

            cooldown.addModifier(
                    Modifier.ENERGY_GAIN_PER_TICK,
                    energyGain -> energyGain.addModifier(
                            FloatModifiable.ModifierType.OVERRIDING,
                            getName(),
                            0
                    )
            );

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private void applySlow(WarlordsPlayer warlordsPlayer, GamblerEffect effect) {
            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> {
                            }
                    );

            warlordsPlayer.addSpeedModifier(
                    warlordsPlayer,
                    effect.displayName,
                    -95,
                    cooldown
            );

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private void applyFrogDisguise(WarlordsPlayer warlordsPlayer, GamblerEffect effect) {
            if (!(warlordsPlayer.getEntity() instanceof Player player)) {
                return;
            }

            MobDisguise frogDisguise =
                    new MobDisguise(DisguiseType.FROG)
                            .setViewSelfDisguise(true)
                            .setKeepDisguiseOnPlayerDeath(false);

            RegularCooldown<GamblerEffect> cooldown =
                    createEffectCooldown(
                            warlordsPlayer,
                            effect,
                            () -> frogDisguise.removeDisguise(
                                    null
                            )
                    );

            DisguiseAPI.disguiseToAll(player, frogDisguise);

            warlordsPlayer.getCooldownManager()
                    .addCooldown(cooldown);
        }

        private RegularCooldown<GamblerEffect> createEffectCooldown(
                WarlordsPlayer warlordsPlayer,
                GamblerEffect effect,
                Runnable cleanup
        ) {
            AtomicBoolean cleanedUp = new AtomicBoolean(false);

            Consumer<CooldownManager> cleanupConsumer = manager -> {
                if (cleanedUp.compareAndSet(false, true)) {
                    cleanup.run();
                }
            };

            return new RegularCooldown<>(
                    effect.displayName,
                    effect.actionBarName,
                    GamblerEffect.class,
                    effect,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cleanupConsumer,
                    cleanupConsumer,
                    effect.durationSeconds * 20
            );
        }
    }

    private enum GamblerEffect {

        DOUBLE_DAMAGE_10S(
                "Double Damage",
                "DOUBLE DMG",
                10,
                NamedTextColor.RED
        ),
        DOUBLE_HEALING_10S(
                "Double Healing",
                "DOUBLE HEAL",
                10,
                NamedTextColor.GREEN
        ),
        TRIPLE_ENERGY_GAIN_10S(
                "Triple Energy Gain",
                "TRIPLE ENERGY",
                10,
                NamedTextColor.AQUA
        ),
        NO_ENERGY_REGEN_10S(
                "No Energy Regeneration",
                "NO EPS",
                10,
                NamedTextColor.DARK_RED
        ),
        SLOW_90_PERCENT_10S(
                "95% Slow",
                "SLOW",
                10,
                NamedTextColor.GRAY
        ),
        TURN_INTO_FROG_15S(
                "Frog",
                "FROGGED",
                15,
                NamedTextColor.GREEN
        );

        private final String displayName;
        private final String actionBarName;
        private final int durationSeconds;
        private final NamedTextColor color;

        GamblerEffect(
                String displayName,
                String actionBarName,
                int durationSeconds,
                NamedTextColor color
        ) {
            this.displayName = displayName;
            this.actionBarName = actionBarName;
            this.durationSeconds = durationSeconds;
            this.color = color;
        }
    }
}