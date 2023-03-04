package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

//public class ItemAdditiveDamageCooldown extends PermanentCooldown<Items> {
//
//    public static void applyToPlayer(WarlordsPlayer warlordsPlayer, int damageBoost) {
//        for (AbstractCooldown<?> cooldown : warlordsPlayer.getCooldownManager().getCooldowns()) {
//            if (cooldown instanceof ItemAdditiveDamageCooldown) {
//                ((ItemAdditiveDamageCooldown) cooldown).addDamageBoost(damageBoost);
//                return;
//            }
//        }
//        warlordsPlayer.getCooldownManager().addCooldown(new ItemAdditiveDamageCooldown(warlordsPlayer, damageBoost));
//    }
//
//    public void addDamageBoost(int damageBoost) {
//        this.damageBoost += damageBoost;
//    }
//
//    private int damageBoost;
//
//    public ItemAdditiveDamageCooldown(WarlordsEntity from, int damageBoost) {
//        super(
//                "ItemDamage",
//                null,
//                Items.class,
//                null,
//                from,
//                CooldownTypes.BUFF,
//                cooldownManager -> {
//                },
//                false
//        );
//        this.damageBoost = damageBoost;
//    }
//
//    @Override
//    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
//        return currentDamageValue * (1 + damageBoost / 100f);
//    }
//
//}
