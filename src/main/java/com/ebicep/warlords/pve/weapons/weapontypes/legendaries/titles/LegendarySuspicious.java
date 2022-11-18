package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LegendarySuspicious extends AbstractLegendaryWeapon {
    public static final List<Pair<Note, Integer>> noteDelay = new ArrayList<>() {{
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
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    public String getPassiveEffect() {
        return "Play an among us sound and gain 20 energy whenever you land a melee crit.";
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
    public LegendaryTitles getTitle() {
        return LegendaryTitles.SUSPICIOUS;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getGame().registerEvents(new Listener() {
            BukkitTask sound;

            @EventHandler
            public void onEvent(WarlordsDamageHealingFinalEvent event) {
                if (!event.getAttacker().equals(player)) {
                    return;
                }
                if (event.isDamageInstance() && event.isCrit() && event.getAbility().isEmpty()) {
                    player.addEnergy(player, "Suspicious Weapon", 20);
                    if (player.getEntity() instanceof Player) {
                        Player p = (Player) player.getEntity();
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
}
