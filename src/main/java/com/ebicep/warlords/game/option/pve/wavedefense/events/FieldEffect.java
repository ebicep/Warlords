package com.ebicep.warlords.game.option.pve.wavedefense.events;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.game.state.EndState;
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
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

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
                                                .append(Component.text(effect.description, NamedTextColor.GRAY)), 180));
            lines.addAll(effect.subDescription);
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
                if (game.isState(EndState.class)) {
                    cancel();
                    return;
                }
                fieldEffects.forEach(fieldEffect -> fieldEffect.run(game, ticksElapsed));
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.onWarlordsEntityCreated(player));
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        fieldEffects.forEach(fieldEffect -> fieldEffect.afterAllWarlordsEntitiesCreated(players));
    }

    public enum FieldEffects {

        WARRIORS_TRIUMPH("Warrior's Triumph",
                "Ability durations are reduced by 30% on ability activation for non-Warrior specializations. Warrior strikes deal 200% more damage. The gravity is twice as intensive.",
                Collections.emptyList()
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
                "Reduce EPS by 10, base EPH increased by 150%. Melee damage increased by 50%.",
                Collections.emptyList()
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
                "All strikes deal 200% more damage to Egg Sacs and Poisonous Spiders. All healing abilities are increased by 15%.",
                Collections.emptyList()
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
                "Players and mobs will lose 1% of their max health every second.",
                Collections.emptyList()
        ) {
            @Override
            public void run(Game game, int ticksElapsed) {
                if (ticksElapsed < 200) {
                    return;
                }
                if (ticksElapsed % 20 != 0) {
                    return;
                }
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
                                    //warlordsEntity.playHurtAnimation(warlordsEntity.getEntity(), warlordsEntity);
                                }
                            });
            }
        },
        DUMB_DEBUFFS("Dumb Debuffs",
                "Each debuff on a mobs will increase the damage they take by 15%. (Max 120%)",
                Collections.emptyList()
        ) {
            @Override
            public void onWarlordsEntityCreated(WarlordsEntity player) {
                if (player instanceof WarlordsNPC) {
                    player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Dumb Debuffs",
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
                        public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            int debuffDamageBoost = Math.min(event.getWarlordsEntity().getCooldownManager().getDebuffCooldowns(true).size(), 12);
                            return currentDamageValue * (1 + (debuffDamageBoost * .15f));
                        }
                    });
                }
            }
        },
        TYCHE_PROSPERITY("Tyche Prosperity",
                "When two specs of the same class are present in the game, the following buffs are applied to all players:",
                new ArrayList<>() {{
                    add(Component.empty());
                    add(Component.text("Mage: ", NamedTextColor.GOLD).append(Component.text("+5 EPS / +10% Projectile Speed", NamedTextColor.GRAY)));
                    add(Component.text("Warrior: ", NamedTextColor.GOLD).append(Component.text("+5% Damage / +10% Knockback Resistance", NamedTextColor.GRAY)));
                    add(Component.text("Paladin: ", NamedTextColor.GOLD).append(Component.text("+5 EPH / +5% Movement Speed", NamedTextColor.GRAY)));
                    add(Component.text("Shaman: ", NamedTextColor.GOLD).append(Component.text("+10% Melee Damage / +5% Max HP", NamedTextColor.GRAY)));
                    add(Component.text("Rogue: ", NamedTextColor.GOLD).append(Component.text("+5% Healing / +10% Cooldown Reduction", NamedTextColor.GRAY)));
                    add(Component.text("Arcanist: ", NamedTextColor.GOLD).append(Component.text("+5% Damage Reduction / +10% Crit Chance", NamedTextColor.GRAY)));
                }}
        ) {
            @Override
            public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
                if (players.isEmpty()) {
                    return;
                }
                Game game = players.get(0).getGame();
                if (game == null) {
                    return;
                }
                Map<Classes, Integer> classCounts = new HashMap<>();
                players.forEach(p -> classCounts.merge(Specializations.getClass(p.getSpecClass()), 1, Integer::sum));
                classCounts.forEach((classes, integer) -> {
                    if (integer < 2) {
                        return;
                    }
                    ChatUtils.MessageType.GAME.sendMessage(name + ": Applied " + classes.name + " Bonus");
                    game.onlinePlayers().forEach(playerTeamEntry -> playerTeamEntry.getKey().sendMessage(
                            Component.textOfChildren(
                                    Component.text("Tyche Prosperity Bonus", NamedTextColor.WHITE, TextDecoration.BOLD),
                                    Component.text(" > ", NamedTextColor.DARK_GRAY),
                                    subDescription.get(classes.ordinal() + 1)
                            )
                    ));
                    switch (classes) {
                        case MAGE -> players.forEach(this::mageBonus);
                        case PALADIN -> players.forEach(this::paladinBonus);
                        case WARRIOR -> players.forEach(this::warriorBonus);
                        case SHAMAN -> players.forEach(this::shamanBonus);
                        case ROGUE -> players.forEach(this::rogueBonus);
                        case ARCANIST -> players.forEach(this::arcanistBonus);
                    }
                });
            }

            private void mageBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.getSpec().setEnergyPerSec(warlordsEntity.getSpec().getEnergyPerSec() + 5);
                warlordsEntity.getAbilitiesMatching(AbstractPiercingProjectile.class).forEach(proj -> proj.setProjectileSpeed(proj.getProjectileSpeed() * 1.1f));
            }

            private void paladinBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.getSpec().setEnergyPerHit(warlordsEntity.getSpec().getEnergyPerHit() + 5);
                warlordsEntity.getSpeed().addBaseModifier(5);
            }

            private void warriorBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        name,
                        null,
                        FieldEffect.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.FIELD_EFFECT,
                        cooldownManager -> {
                        },
                        false
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.05f;
                    }

                    @Override
                    public void multiplyKB(Vector currentVector) {
                        currentVector.multiply(.95);
                    }
                });
            }

            private void shamanBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.setMaxBaseHealth(warlordsEntity.getMaxBaseHealth() * 1.05f);
                warlordsEntity.heal();
                warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        name,
                        null,
                        FieldEffect.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.FIELD_EFFECT,
                        cooldownManager -> {
                        },
                        false
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (event.getAbility().isEmpty()) {
                            return currentDamageValue * 1.1f;
                        }
                        return currentDamageValue;
                    }
                });
            }

            private void rogueBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.getAbilities().forEach(ability -> ability.getCooldown().addMultiplicativeModifierMult(name, .9f));
                warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        name,
                        null,
                        FieldEffect.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.FIELD_EFFECT,
                        cooldownManager -> {
                        },
                        false
                ) {
                    @Override
                    public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                        return currentHealValue * 1.05f;
                    }
                });
            }

            private void arcanistBonus(WarlordsEntity warlordsEntity) {
                warlordsEntity.getAbilities().forEach(ability -> {
                    float critChance = ability.getCritChance();
                    if (critChance > 0) {
                        ability.setCritChance(critChance + 10f);
                    }
                });
                warlordsEntity.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        name,
                        null,
                        FieldEffect.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.FIELD_EFFECT,
                        cooldownManager -> {
                        },
                        false
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * .95f;
                    }
                });
            }
        },
        ;

        public final String name;
        public final String description;
        public final List<Component> subDescription;

        FieldEffects(String name, String description, List<Component> subDescription) {
            this.name = name;
            this.description = description;
            this.subDescription = subDescription;
        }

        public void onStart(Game game) {

        }

        public void onWarlordsEntityCreated(WarlordsEntity player) {

        }

        public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {

        }

        public void run(Game game, int ticksElapsed) {

        }

    }

}
