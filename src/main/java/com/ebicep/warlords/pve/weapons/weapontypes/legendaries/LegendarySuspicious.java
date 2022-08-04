package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class LegendarySuspicious extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 180;
    public static final int MELEE_DAMAGE_MAX = 200;
    public static final int CRIT_CHANCE = 50;
    public static final int CRIT_MULTIPLIER = -50;
    public static final int HEALTH_BONUS = 500;
    public static final int SPEED_BONUS = 8;
    public static final int ENERGY_PER_HIT_BONUS = 5;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;
    public static final int SKILL_CRIT_MULTIPLIER_BONUS = 15;

    public LegendarySuspicious() {
    }

    public LegendarySuspicious(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getPassiveEffect() {
        return "Plays an Amogus sound whenever you land a melee crit.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        new GameRunnable(player.getGame()) {
            final Set<WarlordsDamageHealingFinalEvent> recordedEvents = new HashSet<>();
            BukkitTask sound;

            @Override
            public void run() {
                for (WarlordsDamageHealingFinalEvent warlordsDamageHealingFinalEvent : player.getSecondStats().getAllEventsAsAttacker()) {
                    if (recordedEvents.contains(warlordsDamageHealingFinalEvent)) {
                        continue;
                    }
                    recordedEvents.add(warlordsDamageHealingFinalEvent);
                    if (warlordsDamageHealingFinalEvent.isCrit()) {
                        if (player.getEntity() instanceof Player) {
                            Player p = (Player) player.getEntity();
                            List<Pair<Note, Integer>> noteDelay = new ArrayList<>();
                            noteDelay.add(new Pair<>(new Note(0, Note.Tone.G, false), 7));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.C, false), 4));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.D, true), 4));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.F, false), 4));
                            noteDelay.add(new Pair<>(new Note(2, Note.Tone.F, true), 4));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.F, false), 5));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.D, true), 5));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.C, false), 8));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.A, true), 4));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.D, false), 4));
                            noteDelay.add(new Pair<>(new Note(1, Note.Tone.C, false), 4));
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
                                    if (tick >= noteDelay.size()) {
                                        this.cancel();
                                        return;
                                    }
                                    Pair<Note, Integer> note = noteDelay.get(tick);
                                    p.playNote(p.getLocation(), Instrument.PIANO, note.getA());
                                    delay = note.getB();
                                    tick++;
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 0);

                        }
                    }
                }

            }
        }.runTaskTimer(10, 0);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerHitBonus = ENERGY_PER_HIT_BONUS;
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
        this.skillCritMultiplierBonus = SKILL_CRIT_MULTIPLIER_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
