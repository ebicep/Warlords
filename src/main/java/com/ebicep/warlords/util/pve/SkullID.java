package com.ebicep.warlords.util.pve;

import javax.annotation.Nonnull;

// https://minecraft-heads.com/
public enum SkullID {
    // https://minecraft-heads.com/custom-heads/humanoid/50492-ethereal-wither-skull
    ETHEREAL_WITHER_SKULL("YjExNzc3OTE2ZTc1NjQ0M2UzMGEwNDNmMmJjOWNjZTBkMGZlNjI0YmQ2MTkyZDdiYTIzZjk2YTFkNzFiOWYzZiJ9fX0=="),
    // https://minecraft-heads.com/custom-heads/humanoid/59545-endless-destination-helmet
    GRADIENT_SOUL("MjdiZGJiZGY5NDg2ZmQxNzY5ZDE4YWU5MDJmMzQzYjgzYzUwMGE0YjM1ODQ3YmZkNDFmMmFhYWU0YmY3NTFiZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/decoration/59594-celestial-goldors-helmet-skin
    CELESTIAL_GOLDOR("OGU0MDZmNGIxMzExZWNjOTk4YjRkNzc3NTU3OWQ4Y2M1MTg0NmZhNjZmMmU0Yjc2MzdmNTU5M2RkOGJjZGMxZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/miscellaneous/17831-bow-and-arrow
    BOW_HEAD("NzU0Nzk1MTA0MjJiMWM1ZGNjNzdmNzVmZGMzMzQ2ZWQ0ZDlkYmJjYzFlODg1YjRhMjk5MmEyNzM3MzM2NDZhOSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/decoration/42556-diamond-block-sword
    SWORD_HEAD("ZjAwZTdiMzNlZTJhNjAwMjc1OGFjZmUwOGM3ZGY2YmQzN2E0OTdkYzlmODAwMGMzY2E5ODI0YTJjZmFiY2FkZCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/58843-hooded-knight
    HOODED_KNIGHT("NDg4ZWI0YjRjYWZhMWYzY2JlMDZjZjBlOTAzMGFkNzVjMjhiYjUwMGU4MDBiNjE5NmJkZjMyOTg2NWE1YzcxOCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/blocks/58184-slime
    SLIME_BLOCK("MjNmYmVhMjg3YjNhNjcyZWUzMjRjNzIwZTc3YWY4ZjczMGY4NTFkMjBkYWQ5ZmYxZmExYzA1MWVkZTViYzgxMyJ9fX0="),
    // https://minecraft-heads.com/custom-heads/decoration/58929-squash-helmet
    GREEN_LANCER("ZGUxMDg3YzBjNTE5YTlhMWRjZWU0MzI1NDEwYjE5YTFiZTQ4NTVlYWM1YTY2MmFlMWI1MjMzMjlmOTBmYWVjZCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/58273-deep-dark-worm
    DEEP_DARK_WORM("ZTg1NDRlNTQzMWRmMGZhMzM5MTkxYjMwY2Q1MGI3ZDJlZmRjNmU3NDQ1OTJhMWRmZjMwZTA1MjM0NzhiZTc1MSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/alphabet/58498-white-shekel
    WHITE_SHEKEL("Zjk5ZDdmN2JhNTk2MWY3NmFlMmNlY2E5MmU2OTYxZGY3NjVjNzFiZmEwY2VjYzVhZmM0ZWZjOTNiZDczN2RlZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/58702-shadow-demon
    SHADOW_DEMON("ODE4NDMwY2I2YTE1MDZkMDdlY2I3Y2M0OTgzODJjMWZkN2U3YWNiZTk4YmMwNTQzNDhlNzg1ZjA4YzIyMzJmOCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/58414-netherite-helmet
    NETHERITE_HELMET("N2EyYWI0N2FlODhlOTQ5ZDJlZTJkNjZkOGUyY2VjZDZjNjNiMzM2OWNkY2YyYzQ3YWUxZDVlMTc3OTRjYjMzOCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/58004-sculk-corruption
    SCULK_CORRUPTION("OTAxYzY3MmJiNDFiYzJmNTE2ODkwYzY2ZjM5NjIxNTRlMTQ2MDAzOGNhODM4MzdjOWUxNzdiM2M4ZTNjZDkwYyJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/58036-ghost
    BLUE_GHOST("ZmViMTBjYjg3NTNkYTgwMDMwMzIwYmUyMzg5MWExM2ZmYzI4MmQ4NWU2ZDJiNzg2YmNlZjRlYmYyMzFhZDJlYSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/57034-boy
    PURPLE_RIFT("OWMwODk0OTMxYTUxMDM4N2U0ZjYxMzgyOGYwMjM5Y2E0ZDkwODUzNDk5NjM3ZjYwNzlkMzkzMjdmMTQ2ZjBlOSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/57565-lava-monster
    LAVA_MONSTER("ZWM0OWE0NDFmNjFhYmYzZGM0M2UzNzUzOWVmM2E5YzQ1MTQwYmYzNzQyMTM5ZmY1MzY3OGU5MDJmNTczNzE0MiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/decoration/39006-iron-queen
    IRON_QUEEN("YTYyY2ZhZjM4ZGVhZWUzZDA4ZDk2NWNlNmRjNTgwYzhjY2Q3NWQzYjE0ZDFiZmViNGUwOTM3MjZiNmM3YjFlMiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/34425-demon-king
    DEMON_KING("YTk1NzE0ZjhhMDRiNTAyOWU3OWE5MDA3NmNlNTNiNGFkZGI2OTc4YThkYmUzMjAzMTIzMDc2Zjk5ZGQ1NDdiYiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/54672-skeleton
    DEMON_SKELETON("MzdhNzdjNjY1OTU4ODQ0MDBmZTgxNzU2MWZkOGE0M2ZmOTkyYjQ1NjVlZjZjNWI1ZWQzMmRkMzkwOTVjOGIwMiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/53252-elf
    BURNING_WITHER_SKELETON("OTFmNGY0ZGMzYjA5ODQ4MWQ4MTc1ZDg0ODkyYzE0NjM3ODU5YWNlNmI1MjQxN2Q0ZWRmZWRmNThlNmRiNTRmOSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/53826-eye
    RED_EYE("MjBhYzkwNzcxNzM5MzU4MDVhMzc5OTAzYmQ1OGQxMjc0NGViNDQyNjcwYmE5ZTAwNDMzMTI4ZDFjZDUyNjA5ZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/54662-demon
    DEMON("YjYyNGIwMmY3MjdkMjBmYmU0NzlkMTUxMzk4NWEyZTRkM2ExYTQ5MzA5ZGYwNDk2ZDczZDBhNDE0ZDA4ODdlMCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/53612-samurai-helmet
    END_MONSTER("MzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/53612-samurai-helmet
    SAMURAI("MzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/54551-bandit
    FACELESS_BANDIT("Y2VlZTJjYjQxY2VkZTVhYTQ0MTE3MTYyNGUxZTFlMzg4YjgyNjJhNGEwYmI5ZGZiZmQ4ODljYTAyYzQxY2IifX19="),
    // https://minecraft-heads.com/custom-heads/humanoid/54811-ghost
    FACELESS_MAGE("OGFiOGI2ZDA4YjRhMTdlYjVmMTlkYTNlNTI4MzczYTBkNmQzNjA5ZTEzZmU0OWRjMDIwMDkxNDQ3NWQ4MjNhZiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humans/54826-purple-knight-helmet
    PURPLE_KNIGHT("MmEwM2IzNWQ0NDg1MGNiNDJiMDAwMTdhZGRiN2Y4NWVhYWMyNGI1NmEwY2Q1MWNhMWNhYzIyYjZlYjQyM2UxMSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/54991-neon-enderman
    PURPLE_ENDERMAN("NmY5MDIwYzA3ZDg3NWJhZDE0NDAzMzdhZGI1NWEwOGMxNWRiMDZiOTk0NjQ2YTY5MTc5NWY0Y2QyOTNmZTNkZSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/monsters/54990-xenon-enderman
    NEON_ENDERMAN("OTJkZWZiZTNjZGUzMjZkNDUxMWJiNTMzMzlkNzc3YWZhNzAzZjNlYzRkYWE2OTdkNjFhNDQwMjc0NGNiYjBjZCJ9fX0="),
    // https://minecraft-heads.com/custom-heads/decoration/58672-core-blue
    ITEM_CORE_BLUE("ZThmNWRjOTI3OTIzMjc5MTI3YTlkMmFkZTg2NDMyZjk4Nzc2MDljYjlmODM4NTRhNWI4OTJiZjdjYWQ5ZGYyZiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/40957-white-spider
    WHITE_SPIDER("ZGQ1Y2ViMjBiMGExYjNmNDU4ZWE0NTA0Y2QwYzI3MTJkYjJmZTk3OTRmY2Q3YTVlMTgwMTI3YTQ4ZWMyNjQ3MyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/44568-jungle-spider
    JUNGLE_SPIDER("N2I1Y2NjZjRhYjExNDFjMzZmZmZiZmViZDZkMDlmMjVmMTBjODUxMmI1Y2JmMGMxNzRlMGQ1MzhjNmEzMThmMiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/52049-deep-dark-crawler
    DEEP_DARK_CRAWLER("ODk4ZTJmODU0YzJkMmRlNzU5NWIyMWJjMmY5MzYyMDA2ZWE3MmNiNjExODFkMmE5OWUwNjExY2Q3NDkwNzY5MyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/decoration/49995-spider-skull
    SPIDER("Yjg1YzFlZTYxZjJiZDQ0M2MwYTllNjE3ZjM3MjAzY2RmZjQ0MGJmYTJkMDBiNmRkMzZmZjgzNGNkODcwMmQ5In19fQ="),
    //https://minecraft-heads.com/custom-heads/monsters/23540-blood-spider
    BLOOD_SPIDER("ODMwMDk4NmVkMGEwNGVhNzk5MDRmNmFlNTNmNDllZDNhMGZmNWIxZGY2MmJiYTYyMmVjYmQzNzc3ZjE1NmRmOCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/2533-decapitated-spider
    DECAPITATED_SPIDER("OWQ3YmVlNDJmZGE1ZmUyYjhhZTI2ZmNmNDE0MGNhNTRkNWQzMzRiMmFlMWZlZDlmMWMwOGJhZWI2M2E3ZiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/48315-spider-spirit
    SPIDER_SPIRIT("NzU3MWFmMGM5MjEyMzM3OTdkODU1YzUyMDBhOTBlOTVhMWQ3YzdhNDAxY2FhMThiYjdmZWEwMjFmY2E0OTE1MyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/decoration/45040-spider-egg-sac
    EGG_SAC("NTI4NmE2Mjg4NjRlZmVjNzZkMjFmMWJmYjg0ZDE4MDliMzAyZGVhYjcyOGI4ZGFiNmJlODA0NjdiN2U2ZmNlOCJ9fX0"),
    //https://minecraft-heads.com/custom-heads/monsters/315-cave-spider
    CAVE_SPIDER("NDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"),
    //https://minecraft-heads.com/custom-heads/decoration/49967-iron-fist
    IRON_FIST("N2FlYWUwNmZiMmMzYzljYjhlODQzMGNjZGUxNTgzYzU2ZGE2M2I3ZDY4MDgxYzVjZmZiMDg1NzY2NmRlM2ZkYSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/decoration/37586-enchantment-book
    ENCHANTMENT_BOOK("N2RjOTg1YTdhNjhjNTc0ZjY4M2MwYjg1OTUyMWZlYjNmYzNkMmZmYTA1ZmEwOWRiMGJhZTQ0YjhhYzI5YjM4NSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/decoration/41807-google-home-mini-white
    GOOGLE_HOME_MINI("MTU5OWQ2YmJmNGZhMTg3MDk2ZjZlMDliNzZlZjZiMDc1M2NlYTMyOTBlOWQzZTZjY2E3MDdkNGYwODdlYjc1In19fQ"),
    //https://minecraft-heads.com/custom-heads/miscellaneous/50056-fancy-cube
    FANCY_CUBE("Nzk5OTA1MDc3NWRkNWE1MjQ3MzUyODRjYmJhYzQ1YWEzOTJjMGFjOGZhOTgwYmQyNGMzMzE1NTJiNjU0YjgyNCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/miscellaneous/54932-fancy-cube
    FANCY_CUBE_2("NzAwZTJmMjVlMDcyOGU4OTgxZjZiZDNlNDE2NTUzODBlMDBlN2U1NGI3NTU5ZDVmNDczNWU3MzkxY2RmMDJkMiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/59288-sculk-monster
    SCULK_MONSTER("YmE4ODk0MWJlYjE3MmFhODI1ZjQxMzZmNTg4Yzk5MDczZmRlZjg0M2QzN2QzN2Q2OWMyZDY2NWQ4OWM2NzJiMCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/52340-wither-soul
    WITHER_SOUL("NjIxNzg2OWVjMjA1ZDE3MjdmYzRjNjA1NWJkODY4Yjc4ODZmMmM4YWQ5OGZhNzA0Y2I3NmUxMWJkZDgwMjg3OSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/58154-seek-doors
    SEEK_DOORS("ZWZmZWU5MDZlYjBlNWJhODAzZWJhOTk2ZDQxZTM5ZjM5ZjM3YzFjMDNmZTA0ODUzNjMwOTRlMDA4MDQyMjU5In19fQ=="),


    ;

    private final String textureId;

    /**
     * @param textureId texture ID encoded in Base64, cannot be null.
     */
    SkullID(@Nonnull String textureId) {
        this.textureId = textureId;
    }

    public String getTextureId() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + textureId;
    }

}
