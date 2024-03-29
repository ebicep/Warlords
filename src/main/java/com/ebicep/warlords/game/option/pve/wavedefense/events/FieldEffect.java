package com.ebicep.warlords.game.option.pve.wavedefense.events;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventPoisonousSpider;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FieldEffect implements Option {

    private final EnumSet<FieldEffects> fieldEffects;

    public FieldEffect(List<Option> options) {
        this.fieldEffects = EnumSet.allOf(FieldEffects.class);
        addOptions(options);
    }

    private void addOptions(List<Option> options) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.text("Field Effects", NamedTextColor.WHITE, TextDecoration.BOLD));
        lines.add(Component.empty());
        fieldEffects.forEach(effect -> {
            lines.addAll(WordWrap.wrap(Component.text(effect.name + ": ", NamedTextColor.GREEN)
                                                .append(Component.text(effect.description, NamedTextColor.GRAY)), 170));
            lines.add(Component.empty());
        });
        options.add(TextOption.Type.CHAT_CENTERED.create(lines));
    }

    public FieldEffect(List<Option> options, FieldEffects... fieldEffects) {
        this.fieldEffects = EnumSet.of(fieldEffects[0], fieldEffects);
        addOptions(options);
    }

    @Override
    public void start(@Nonnull Game game) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.onStart(game));
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                fieldEffects.forEach(fieldEffect -> fieldEffect.run(game, ticksElapsed));
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.onWarlordsEntityCreated(player));
    }

    public enum FieldEffects {

        WARRIORS_TRIUMPH("Warrior's Triumph",
                "Ability durations are reduced by 30% on ability activation for non-Warrior specializations. Warrior strikes deal 200% more damage. The gravity is twice as intensive."
        ) {
            @Override
            public void onStart(Game game) {
                game.registerEvents(new Listener() {
                    @EventHandler
                    public void onCooldown(WarlordsAddCooldownEvent event) {
                        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (Specializations.getClass(event.getWarlordsEntity().getSpecClass()) == Classes.WARRIOR) {
                            return;
                        }
                        AbstractCooldown<?> abstractCooldown = event.getAbstractCooldown();
                        if (abstractCooldown instanceof LinkedCooldown) {
                            if (abstractCooldown.getFrom().equals(event.getWarlordsEntity())) {
                                LinkedCooldown<?> linkedCooldown = (LinkedCooldown<?>) abstractCooldown;
                                linkedCooldown.setTicksLeft((int) (linkedCooldown.getTicksLeft() * 0.7));
                            }
                        } else if (abstractCooldown instanceof RegularCooldown<?> regularCooldown) {
                            regularCooldown.setTicksLeft((int) (regularCooldown.getTicksLeft() * 0.7));
                        }
                    }

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!(event.getAttacker() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (Specializations.getClass(event.getAttacker().getSpecClass()) != Classes.WARRIOR) {
                            return;
                        }
                        String ability = event.getAbility();
                        if (ability.equals("Wounding Strike") || ability.equals("Crippling Strike")) {
                            event.setMin(event.getMin() * 3);
                            event.setMax(event.getMax() * 3);
                        }
                    }
                });
            }
        },
        CONQUERING_ENERGY("Conquering Energy",
                "Reduce EPS by 10, base EPH increased by 150%. Melee damage increased by 50%."
        ) {
            @Override
            public void onStart(Game game) {
                game.registerEvents(new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!(event.getAttacker() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (event.getAbility().isEmpty()) {
                            event.setMin(event.getMin() * 1.5f);
                            event.setMax(event.getMax() * 1.5f);
                        }
                    }

                });
            }

            @Override
            public void onWarlordsEntityCreated(WarlordsEntity player) {
                if (player instanceof WarlordsPlayer) {
                    AbstractPlayerClass spec = player.getSpec();
                    spec.setEnergyPerSec(spec.getEnergyPerSec() - 10);
                    spec.setEnergyPerHit(spec.getEnergyPerHit() * 2.5f);
                }
            }
        },
        ARACHNOPHOBIA("Arachnophobia",
                "All strikes deal 200% more damage to Egg Sacs and Poisonous Spiders. All healing abilities are increased by 15%."
        ) {
            @Override
            public void onStart(Game game) {
                game.registerEvents(new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!(event.getAttacker() instanceof WarlordsPlayer)) {
                            return;
                        }
                        if (event.isDamageInstance()) {
                            if (!(event.getWarlordsEntity() instanceof WarlordsNPC)) {
                                return;
                            }
                            AbstractMob<?> mob = ((WarlordsNPC) event.getWarlordsEntity()).getMob();
                            if (!(mob instanceof EventPoisonousSpider) && !(mob instanceof EventEggSac)) {
                                return;
                            }
                            if (event.getAbility().contains("Strike")) {
                                event.setMin(event.getMin() * 3);
                                event.setMax(event.getMax() * 3);
                            }
                        } else if (event.isHealingInstance()) {
                            event.setMin(event.getMin() * 1.15f);
                            event.setMax(event.getMax() * 1.15f);
                        }
                    }

//                    @EventHandler
//                    public void onWaveClear(WarlordsGameWaveClearEvent event) {
//                        int waveCleared = event.getWaveCleared();
//                        if (waveCleared != 0 && waveCleared % 5 == 0 && waveCleared <= 25) {
//                            game.warlordsPlayers().forEach(warlordsPlayer -> {
//                                for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
//                                    ability.setCooldown(ability.getCooldown() - .5f);
//                                }
//                            });
//                        }
//                    }

                });
            }
        },
        LOST_BUFF("Lost Buff",
                "Players and mobs will lose 1% of their max health every second."
        ) {
            @Override
            public void onStart(Game game) {
                new GameRunnable(game) {

                    @Override
                    public void run() {
                        PlayerFilter.playingGame(game)
                                    .forEach(warlordsEntity -> {
                                        if (warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossMob) {
                                            return;
                                        }
                                        float damage = warlordsEntity.getMaxHealth() * .01f;
                                        warlordsEntity.resetRegenTimer();
                                        if (warlordsEntity.getHealth() - damage <= 0 && !warlordsEntity.getCooldownManager().checkUndyingArmy(false)) {
                                            warlordsEntity.setHealth(0);
                                            warlordsEntity.die(warlordsEntity);
                                        } else {
                                            warlordsEntity.setHealth(warlordsEntity.getHealth() - damage);
                                            warlordsEntity.playHurtAnimation(warlordsEntity.getEntity(), warlordsEntity);
                                        }
                                    });
                    }

                }.runTaskTimer(200, 20);
            }
        },
        DEBUFF_THING("Debuff Thing",
                "Each debuff on a mobs will increase the damage they take by 10%. (Max 120%)"
        ) {
            @Override
            public void onWarlordsEntityCreated(WarlordsEntity player) {
                if (player instanceof WarlordsNPC) {
                    player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Debuff Thing",
                            null,
                            null,
                            null,
                            player,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            false
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            int debuffDamageBoost = Math.min(event.getWarlordsEntity().getCooldownManager().getDebuffCooldowns().size(), 12);
                            return currentDamageValue * (1 + (debuffDamageBoost * .2f));
                        }
                    });
                }
            }
        },
        ;

        public final String name;
        public final String description;

        FieldEffects(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void onStart(Game game) {

        }

        public void onWarlordsEntityCreated(WarlordsEntity player) {

        }

        public void run(Game game, int ticksElapsed) {

        }

    }

}
