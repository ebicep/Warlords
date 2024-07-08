package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendarySuspicious extends AbstractLegendaryWeapon {

    public static final int ENERGY_GAIN = 20;
    public static final int ENERGY_GAIN_PER_UPGRADE = 5;

    public static final List<Pair<Note, Integer>> NOTE_DELAY = new ArrayList<>() {{
        add(new Pair<>(new Note(0, Note.Tone.G, false), 7));
        add(new Pair<>(new Note(1, Note.Tone.C, false), 4));
        add(new Pair<>(new Note(1, Note.Tone.D, true), 4));
        add(new Pair<>(new Note(1, Note.Tone.F, false), 4));
        add(new Pair<>(new Note(2, Note.Tone.F, true), 4));
        add(new Pair<>(new Note(1, Note.Tone.F, false), 5));
        add(new Pair<>(new Note(1, Note.Tone.D, true), 5));
        add(new Pair<>(new Note(1, Note.Tone.C, false), 12));
        add(new Pair<>(new Note(1, Note.Tone.A, true), 4));
        add(new Pair<>(new Note(1, Note.Tone.D, false), 4));
        add(new Pair<>(new Note(1, Note.Tone.C, false), 4));
    }};

    public LegendarySuspicious() {
    }

    public LegendarySuspicious(UUID uuid) {
        super(uuid);
    }

    public LegendarySuspicious(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Suspicious Weapon",
                null,
                LegendarySuspicious.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float setCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (event.getCause().isEmpty()) {
                    return 50;
                }
                return currentCritChance;
            }
        });
        player.getGame().registerEvents(new Listener() {
            BukkitTask sound;

            @EventHandler
            public void onEvent(WarlordsDamageHealingFinalEvent event) {
                if (!event.getAttacker().equals(player)) {
                    return;
                }
                if (event.isDamageInstance() && event.isCrit() && event.getAbility().isEmpty()) {
                    player.addEnergy(player, "Suspicious Weapon", ENERGY_GAIN + ENERGY_GAIN_PER_UPGRADE * getTitleLevel());
                    if (player.getEntity() instanceof Player p) {
                        if (sound != null) {
                            sound.cancel();
                        }
                        sound = new BukkitRunnable() {
                            int tick = 0;
                            int delay = 0;

                            @Override
                            public void run() {
                                if (delay > 0) {
                                    delay--;
                                    return;
                                }
                                if (tick >= NOTE_DELAY.size()) {
                                    this.cancel();
                                    return;
                                }
                                Pair<Note, Integer> note = NOTE_DELAY.get(tick);
                                Utils.playGlobalSound(p.getLocation(), Instrument.PIANO, note.getA());
                                delay = note.getB();
                                tick++;
                            }
                        }.runTaskTimer(Warlords.getInstance(), 0, 0);

                    }
                }
            }
        });

    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Play an among us sound and gain ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(ENERGY_GAIN + ENERGY_GAIN_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(" energy when you land a melee crit."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.SUSPICIOUS;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 15;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 200;
    }

    @Override
    protected float getCritChanceValue() {
        return 50;
    }

    @Override
    protected float getCritMultiplierValue() {
        return -50;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade(ENERGY_GAIN + ENERGY_GAIN_PER_UPGRADE * getTitleLevel()),
                formatTitleUpgrade(ENERGY_GAIN + ENERGY_GAIN_PER_UPGRADE * getTitleLevelUpgraded())
        ));
    }
}
