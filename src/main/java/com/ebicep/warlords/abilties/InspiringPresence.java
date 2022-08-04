package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class InspiringPresence extends AbstractAbility {
    private final int speedBuff = 30;
    private final int radius = 10;
    protected int playersHit = 0;
    private boolean pveUpgrade = false;
    private int duration = 12;
    private int energyPerSecond = 10;
    private List<WarlordsEntity> playersEffected = new ArrayList<>();

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60f + 10.47f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your presence on the battlefield\n" +
                "§7inspires your allies, increasing\n" +
                "§7their energy regeneration by §e" + energyPerSecond + "\n" +
                "§7per second and their movement\n" +
                "§7by §e" + speedBuff + "% §7for §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 1);

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");

        InspiringPresence tempPresence = new InspiringPresence();
        wp.getCooldownManager().addCooldown(new RegularCooldown<InspiringPresence>(
                name,
                "PRES",
                InspiringPresence.class,
                tempPresence,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                        ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
                    }
                }
        ) {
            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });

        if (pveUpgrade) {
            resetCooldowns(wp);
        }

        for (WarlordsEntity presenceTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            playersHit++;
            tempPresence.getPlayersEffected().add(presenceTarget);
            if (pveUpgrade) {
                resetCooldowns(presenceTarget);
            }
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                    ChatColor.GRAY + " Your Inspiring Presence inspired " +
                    ChatColor.YELLOW + presenceTarget.getName() +
                    ChatColor.GRAY + "!"
            );

            Runnable cancelAllySpeed = presenceTarget.getSpeed().addSpeedModifier("Inspiring Presence", speedBuff, duration * 20, "BASE");
            presenceTarget.getCooldownManager().addCooldown(new RegularCooldown<InspiringPresence>(
                    name,
                    "PRES",
                    InspiringPresence.class,
                    tempPresence,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        cancelAllySpeed.run();
                    },
                    duration * 20
            ) {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    return energyGainPerTick + energyPerSecond / 20f;
                }
            });
        }

        return true;
    }

    private void resetCooldowns(WarlordsEntity we) {
        we.getSpec().getRed().setCurrentCooldown(0);
        we.getSpec().getPurple().setCurrentCooldown(0);
        we.getSpec().getBlue().setCurrentCooldown(0);
        if (!we.getSpec().getOrange().getName().equals("Inspiring Presence")) {
            we.getSpec().getOrange().setCurrentCooldown(0);
        }

        we.updateRedItem();
        we.updatePurpleItem();
        we.updateBlueItem();
        we.updateOrangeItem();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<WarlordsEntity> getPlayersEffected() {
        return playersEffected;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}