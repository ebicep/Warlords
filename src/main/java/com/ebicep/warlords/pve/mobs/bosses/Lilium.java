package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.LiliathEngima;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NineCrystal;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;

import static java.lang.Math.cos;

public class Lilium extends AbstractMob implements BossMob {

    private Location mapCenter;

    private BouquetBarrageAbility bouquetBarrageAbility;
    private RoseGardenAbility roseGardenAbility;
    private BouquetWaltzAbility bouquetWaltzAbility;
    private PetalStormAbility petalStormAbility;

    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;
    private BossAbilityPhase phaseSix;
    private BossAbilityPhase phaseSeven;
    private BossAbilityPhase phaseEight;

    public Lilium(Location spawnLocation) {
        super(spawnLocation,
                "Lilium",
                10000,
                0.36f,
                30,
                600,
                900
        );
    }

    public Lilium(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ALLAY_DEATH, 500, 0.5f);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        bouquetBarrageAbility = new BouquetBarrageAbility(warlordsNPC, 3, 20, 40, 4, 2000, 3000, true, 40, 20);
        roseGardenAbility = new RoseGardenAbility(
                warlordsNPC,
                () -> mapCenter,
                8,
                22,
                2,
                6,
                40,
                200,
                200,
                2000,
                3000,
                true,
                40,
                30,
                true,
                6,
                2000,
                3000,
                true,
                Material.CRIMSON_FUNGUS,
                8,
                false,
                5,
                1
        );

        bouquetWaltzAbility = new BouquetWaltzAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation().clone(),
                () -> warlordsNPC.getLocation().clone().add(warlordsNPC.getLocation().getDirection().multiply(22)),
                15,
                2.5,
                4,
                true,
                80,
                1.2,
                6.0,
                0.25,
                1200,
                1800,
                true,
                30,
                30,
                true,
                0.45
        );

        petalStormAbility = new PetalStormAbility(
                warlordsNPC,
                () -> mapCenter,
                // pattern / timing
                8, 8, 30, 25, 12, 22.0, 22.0, 8.0,
                // impact / damage
                3.25, 1500f, 2100, true, 40, true, 30, 25,
                // lanes
                true, 'X', 5, "ALTERNATING", 1
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            petalStormAbility.cast();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 70, () -> {
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    Location crystalLoc = mapCenter.add(0, 1,0).clone();
                    if (t++ < 9) {
                        double angle = t / 9D * Math.PI * 2;
                        crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 25);
                        crystalLoc.setZ(mapCenter.getZ() + cos(angle) * 25);
                        LiliathEngima crystal = new LiliathEngima(crystalLoc, warlordsNPC);
                        pveOption.spawnNewMob(crystal, Team.RED);
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                    }

                    if (t == 9) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(40, 6);

        });

        phaseThree = new BossAbilityPhase(warlordsNPC, 90, () -> {});
        phaseFour = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseFive = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseSix = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseSeven = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseEight = new BossAbilityPhase(warlordsNPC, 70, () -> {});
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        EffectUtils.playCircularEffectAround(warlordsNPC.getGame(), warlordsNPC.getLocation(), Particle.CHERRY_LEAVES, 1, 1.3, 0.1, 2.2, 8, 1, 4, ticksElapsed);

        if (ticksElapsed % 400 == 0) {
            roseGardenAbility.cast();
        }

        if (ticksElapsed % 320 == 0) {
            bouquetBarrageAbility.cast();
        }

        if (ticksElapsed % 480 == 0 && ticksElapsed > 0) {
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;
                    bouquetWaltzAbility.cast();
                    if (t == 3) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
        }

        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        phaseThree.initialize(health);
        phaseFour.initialize(health);
        phaseFive.initialize(health);
        phaseSix.initialize(health);
        phaseSeven.initialize(health);
        phaseEight.initialize(health);
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(255, 192, 203);
    }

    @Override
    public Component getDescription() {
        return Component.text("Queen of Hearts", TextColor.color(218, 112, 214));
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LILIUM;
    }
}
