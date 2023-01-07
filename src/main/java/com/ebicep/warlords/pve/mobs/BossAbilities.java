package com.ebicep.warlords.pve.mobs;

public class BossAbilities {

//    private void timedDamage(WaveDefenseOption option, long playerCount, int damageValue, int timeToDealDamage) {
//        damageToDeal.set((int) (damageValue * playerCount));
//
//        for (WarlordsEntity we : PlayerFilter
//                .playingGame(getWarlordsNPC().getGame())
//                .aliveEnemiesOf(warlordsNPC)
//        ) {
//            if (we.getEntity() instanceof Player) {
//                PacketUtils.sendTitle(
//                        (Player) we.getEntity(),
//                        "",
//                        ChatColor.RED + "Keep attacking Illumina to stop the draining!",
//                        10, 35, 0
//                );
//            }
//            Utils.addKnockback(warlordsNPC.getLocation(), we, -4, 0.35);
//            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.WITHER_SPAWN, 500, 0.3f);
//        }
//
//        AtomicInteger countdown = new AtomicInteger(timeToDealDamage);
//        new GameRunnable(warlordsNPC.getGame()) {
//            int counter = 0;
//            @Override
//            public void run() {
//                if (warlordsNPC.isDead()) {
//                    this.cancel();
//                    return;
//                }
//
//                if (damageToDeal.get() <= 0) {
//                    FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
//                            .withColor(Color.WHITE)
//                            .with(FireworkEffect.Type.BALL_LARGE)
//                            .build());
//                    warlordsNPC.getSpec().getBlue().onActivate(warlordsNPC, null);
//                    this.cancel();
//                    return;
//                }
//
//                if (counter++ % 20 == 0) {
//                    countdown.getAndDecrement();
//                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.NOTE_STICKS, 500, 0.4f);
//                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.NOTE_STICKS, 500, 0.4f);
//                    for (WarlordsEntity we : PlayerFilter
//                            .entitiesAround(warlordsNPC, 100, 100, 100)
//                            .aliveEnemiesOf(warlordsNPC)
//                    ) {
//                        EffectUtils.playParticleLinkAnimation(
//                                we.getLocation(),
//                                warlordsNPC.getLocation(),
//                                255,
//                                255,
//                                255,
//                                2
//                        );
//
//                        we.addDamageInstance(
//                                warlordsNPC,
//                                "Vampiric Leash",
//                                600,
//                                600,
//                                -1,
//                                100,
//                                true
//                        );
//                    }
//                }
//
//                if (countdown.get() <= 0 && damageToDeal.get() > 0) {
//                    for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
//                        option.spawnNewMob(new IronGolem(spawnLocation));
//                    }
//
//                    FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
//                            .withColor(Color.WHITE)
//                            .with(FireworkEffect.Type.BALL_LARGE)
//                            .build());
//                    EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 10);
//                    Utils.playGlobalSound(warlordsNPC.getLocation(), "shaman.earthlivingweapon.impact", 500, 0.5f);
//
//                    for (WarlordsEntity we : PlayerFilter
//                            .entitiesAround(warlordsNPC, 100, 100, 100)
//                            .aliveEnemiesOf(warlordsNPC)
//                    ) {
//                        Utils.addKnockback(warlordsNPC.getLocation(), we, -2, 0.4);
//                        EffectUtils.playParticleLinkAnimation(we.getLocation(), warlordsNPC.getLocation(), ParticleEffect.VILLAGER_HAPPY);
//                        we.addDamageInstance(
//                                warlordsNPC,
//                                "Death Ray",
//                                we.getMaxHealth() * 0.95f,
//                                we.getMaxHealth() * 0.95f,
//                                -1,
//                                100,
//                                true
//                        );
//
//                        warlordsNPC.addHealingInstance(
//                                warlordsNPC,
//                                "Death Ray Healing",
//                                we.getMaxHealth() * 2,
//                                we.getMaxHealth() * 2,
//                                -1,
//                                100,
//                                false,
//                                false
//                        );
//                    }
//
//                    this.cancel();
//                }
//
//                for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
//                    if (we.getEntity() instanceof Player) {
//                        PacketUtils.sendTitle(
//                                (Player) we.getEntity(),
//                                ChatColor.YELLOW.toString() + countdown.get(),
//                                ChatColor.RED.toString() + damageToDeal.get(),
//                                0, 4, 0
//                        );
//                    }
//                }
//            }
//        }.runTaskTimer(40, 0);
//    }
}
