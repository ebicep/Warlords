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
    // https://minecraft-heads.com/custom-heads/humanoid/50029-ghost-lime
    GHOST_LIME("N2I5N2JlZmJjZTI0YzE3ODVhZjNlYmJkMWIzNzYxZGE1OTJmNWY4ZTI5ZWRhOTlhN2I5ZWQwOWY4MjBmNTc3YiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/50028-ghost-magenta
    GHOST_MAGENTA("OTY5Yjk0Njg5NjQ2OGQwMzVmOWY4NjMxODk1YmM1ODU4ZGE2OGFkNWRhZTIxYjVmMDkwZDM2ZjVhMmUzZjYxZiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/50027-ghost-red
    GHOST_RED("NjRmYjU2MmU3ZGY5OTAxYmI5MTZkZjlmMWViNTcyNTA5ZDgyNzRkZGFjYWM1ZTUyNWU1ZTQ0MjNjYTMyY2YzMSJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/50026-ghost-gray
    GHOST_GRAY("MmQzNDVhNGUxYmI4N2Q4ODVjMGI4NGUxM2EzYjlkNTE3Yjk0NmY2NTQyODQ0ZTlkMGNlNmFjYjdhZWYyYWQ1NiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/47952-ghost
    GHOST_BLUE("YTE0YTZhZmEzNmM5N2M1MzE5ZWFmNTVkZTI0Y2JlN2UwNjc5ZjUwZTJhMTNkY2ZmOWUzZGE0MDg0Mzk3YTBjNiJ9fX0="),
    // https://minecraft-heads.com/custom-heads/humanoid/48225-ghost
    GHOST_PURPLE("Nzg1MGM3ODg4N2MyMzAzOGEzMjRmYWRkMGY0YjIyNDgxYTA2OWYwZTgwNGQ3ZWIwOGM1Mjc3ZDg3ZGM3OWEyYyJ9fX0="),
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
    //https://minecraft-heads.com/custom-heads/miscellaneous/46577-enchantment-cube
    ENCHANTMENT_CUBE("M2JiNzg4YzBmZjBmYzU3NDY5ODc4N2MzYzdmMWIzODVhMTljMTczODM1OTMyMzU2MzU0MmI1MzdkMDVmMWYyZSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/miscellaneous/5765-fancy-cube
    FANCY_CUBE_3("NGViYjEwZTczMWE0YmRkZTNkOWRmM2E1M2M2MjdhOGI4YzMyZDgyODM1ZGJiYjk4OGY0YTY5NzI3YTc2MSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/miscellaneous/41877-explosion,
    EXPLOSION("NjAyYTExNjkzMDlmMDVlZjJmMDYxYjFmYTBmZTIyNWYyOWQ3M2EyNGY4ZjA3Y2NjMmE3MDVkZWVhY2EwNjlkMSJ9fX0="),

    //https://minecraft-heads.com/custom-heads/alphabet/193-oak-wood-1
    OAK_WOOD_1("NzFiYzJiY2ZiMmJkMzc1OWU2YjFlODZmYzdhNzk1ODVlMTEyN2RkMzU3ZmMyMDI4OTNmOWRlMjQxYmM5ZTUzMCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/alphabet/505-stone-2
    STONE_2("YWNiNDE5ZDk4NGQ4Nzk2MzczYzk2NDYyMzNjN2EwMjY2NGJkMmNlM2ExZDM0NzZkZDliMWM1NDYzYjE0ZWJlIn19fQ=="),
    //https://minecraft-heads.com/custom-heads/alphabet/9162-yellow-1
    YELLOW_1("Yjk0OWRmMzZhMWEzZjdjYjRjNjcwNjVhOWQ1MzUwMjU4YTNjNTFiMDJhMWEzNzdiODRhODI4NzZkNzdiIn19fQ=="),
    //https://minecraft-heads.com/custom-heads/alphabet/7898-golden-2
    GOLD_2("ZGM2MWIwNGUxMmE4Nzk3NjdiM2I3MmQ2OTYyN2YyOWE4M2JkZWI2MjIwZjVkYzdiZWEyZWIyNTI5ZDViMDk3In19fQ=="),
    //https://minecraft-heads.com/custom-heads/alphabet/9916-lime-3
    LIME_3("YzQyMjZmMmViNjRhYmM4NmIzOGI2MWQxNDk3NzY0Y2JhMDNkMTc4YWZjMzNiN2I4MDIzY2Y0OGI0OTMxMSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/alphabet/10239-green-4
    GREEN_4("OGI1MjdiMjRiNWQyYmNkYzc1NmY5OTVkMzRlYWU1NzlkNzQxNGIwYTVmMjZjNGZmYTRhNTU4ZWNhZjZiNyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/alphabet/8942-blue-5
    BLUE_5("OWYyYTE0ZGJmOTU4ODEyNmM0M2NkMjExMWViNDFmMWRlNmQ4YzI4MWI2NTE5MTk0MzY0Yjk5NjVmYzQ1NmUifX19"),
    //https://minecraft-heads.com/custom-heads/alphabet/9589-pink-6
    PINK_6("YWQ2MTdjYzliZjk4MzMzM2JmMTI2NjQxNDNhNzcyZDUyMTU2YWM2YzQ4ZTE2OGExZDkxNmZiNjI5OTE2ZmIifX19"),
    //https://minecraft-heads.com/custom-heads/alphabet/9372-red-7
    RED_7("YWY0ZTdhNWNmNWI1YTRkMmZmNGZiMDQzM2IxYTY4NzUxYWExMmU5YTAyMWQzOTE4ZTkyZTIxOWE5NTNiIn19fQ=="),

    //https://minecraft-heads.com/custom-heads/monsters/55303-spider
    MC_SPIDER("MzVlMjQ4ZGEyZTEwOGYwOTgxM2E2Yjg0OGEwZmNlZjExMTMwMDk3ODE4MGVkYTQxZDNkMWE3YThlNGRiYTNjMyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/52304-slime
    MC_SLIME("NjFhZmZkMzFlZmMzN2JhODRmNTAxODczOTRkODY4ODM0NGNjZDA2Y2RjOTI2ZGRmY2YyZGYxMTY5ODZkY2E5In19fQ=="),
    //https://minecraft-heads.com/custom-heads/monsters/45421-magma-cube
    MC_MAGMACUBE("YTFjOTdhMDZlZmRlMDRkMDAyODdiZjIwNDE2NDA0YWIyMTAzZTEwZjA4NjIzMDg3ZTFiMGMxMjY0YTFjMGYwYyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/monsters/47778-blaze
    MC_BLAZE("YjIwNjU3ZTI0YjU2ZTFiMmY4ZmMyMTlkYTFkZTc4OGMwYzI0ZjM2Mzg4YjFhNDA5ZDBjZDJkOGRiYTQ0YWEzYiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/35861-witch
    MC_WITCH("ZmNlNjYwNDE1N2ZjNGFiNTU5MWU0YmNmNTA3YTc0OTkxOGVlOWM0MWUzNTdkNDczNzZlMGVlNzM0MjA3NGM5MCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/45422-iron-golem
    MC_GOLEM("ZTEzZjM0MjI3MjgzNzk2YmMwMTcyNDRjYjQ2NTU3ZDY0YmQ1NjJmYTlkYWIwZTEyYWY1ZDIzYWQ2OTljZjY5NyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/38374-piglin
    MC_PIGLIN("ZDcxYjNhZWUxODJiOWE5OWVkMjZjYmY1ZWNiNDdhZTkwYzJjM2FkYzA5MjdkZGUxMDJjN2IzMGZkZjdmNDU0NSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/animals/49759-angry-wolf
    MC_ANGRY_WOLF("M2Y2NWQ5MWU3ZjBhYmU0NmMyNmYyN2VmYmM3NTRhYjI3Yjc5MTdlZTVjODg4YzE3NDdkODgyZDgxYzFhMTNlOSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/alphabet/5650-question-mark
    QUESTION_MARK("ZWNjNThjYjU1YjFhMTFlNmQ4OGMyZDRkMWE2MzY2YzIzODg3ZGVlMjYzMDRiZGE0MTJjNGE1MTgyNWYxOTkifX19="),
    //https://minecraft-heads.com/custom-heads/humans/8152-apollo
    APOLLO("YzRhYzVjNTA5MjM3Mzc3NTNlYThiNjMwYTQyZjg5ZjVkOThjMTcyNDQ4MmJjM2ZmYzE0ZjllZDFmYmZlZWEifX19="),
    //https://minecraft-heads.com/custom-heads/humanoid/51769-ares
    ARES("OGZiZGFhM2U4ZDQ1MzY1Yjc3MzNiY2RiN2YyZjc4MjJlZTcyNzMzYWJmNjA3NDgwYzJhMzU2YWE0NzYwZDRmNSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humans/28229-hermes
    HERMES("OGU2ZjI4NDI1MjE3OWExNzE4ZTY4MTkwNzZiMTQyM2U2ODM1MWQ2OTc3YzQ2ODc2NGI3ZGZjOTRjNjgwMTk2NiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humans/28233-aphrodite
    APHRODITE("YmQ5N2MwNTE4NDYzN2Q0YzQxZjY2MzAyMjYwYWI4ZDJkNThkOGJmMGVkMmQwYzdjOWE3NjdjMzRjNmRhOTVmNCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humans/1584-bust-zeus
    BUST_ZEUS("MTI3ZDgxNWJmNjE5OWE4NjM3NDlkZDI3MWM3YzgxNDliOTk5N2JiYmE1MzFkMDM5NGNjZmMwOTQ0ZGEyNzYifX19"),
    //https://minecraft-heads.com/custom-heads/humans/36134-zeus
    ZEUS("ZGNkOWRkZjRmYjllMjVlNjJkMmU5ODU5NWQ1MTY4ZGUyYjMzNjdiYTc4ZjM2OTdiZTFjNDc5ZjM1MTAyYWQ3NiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/34400-poseidon
    POSEIDON("NjAzNTRiMDc5Y2VhMDBmMTFhMjhlN2MyMWJhOWMxM2ExN2NjY2IwNjA1NjAxOWNiNGM2NGJjMjQzNTc1NTI5YSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/18986-dark-wraith
    DARK_WRAITH("YTEyMDBjNDcwYzY3MjhkZDY1N2IyNDVjMmRiYzAzMTg5MWUzY2RkOWQ5OTc5MDk4NDU2YjMzZjgxMmFmZGMzZiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/2372-minotaur
    MINOTAUR("ZTI0NThmYWQzMmIxZjM0MjY0NTg5MjY3MTFjODJiNjYxYTAxYjIwZDE2MzNjMmE4ZDQyZmMzNzNkMTQ5ZGRjIn19fQ=="),
    //https://minecraft-heads.com/custom-heads/humanoid/57977-cyclops
    CYCLOPS("NzFjMGRiMjA0YWMzYTEyY2RlMzJjZDU3ODBkYjBhZjg2ZWQ5MDgyMTgxMDllOTU5NjRkMWExN2JhZjM0MzI0YiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/52611-siren
    SIREN("NGUxOTFkZTIwNWQzNmRmMGYzYWUyNzFlYWE0MGNlNDk0NTk3NjI0ZmQwYzJmZTU5MDQzMjFjYmQ0YWExMDJlZSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humans/58821-dryad
    DRYAD("NWY3ODMyZjk5YzFmMmZkYjZjM2JmZGJmNzNkMzdhOGY2ZWQ2NTg5MGIzODg1NDNkNjgzOGE1MWRmODYzZDNiZCJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/58891-faun
    FAUN("NWFmMzgwYTZlMTkwOTk1NzAyNTFkMWM4ZTk0NDllZjhhNjcxMDhiZTRiM2JmNDIwMzNhOTQyMWM2YjUyODhlMSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/humanoid/40649-skeleton-archer
    SKELETON_ARCHER("Y2UwZDNmMjhiOGUwZjBhNDVjYTJiNDJiNmY0OTViYmZkYTczNzMyZDNhM2YxMDhmNjM1MmU3NThlMWNiOWJhOSJ9fX0="),
    //https://minecraft-heads.com/custom-heads/decoration/65739-books,
    BOOKS("OGFhMGQ3YmNhNThlM2YyZGY0MjA2YjE5ZDg0NWVhYmY4MWIxMWZhZDIxYzAwN2U5ZWE3YzJjNjM5Yzc1MjAxZCJ9fX0=="),
    //https://minecraft-heads.com/custom-heads/head/56787-check-mark
    CHECK_MARk("YTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0="),
    //https://minecraft-heads.com/custom-heads/head/56786-question-mark-19
    QUESTION_MARK_BLACK_WHITE("MjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0="),
    //https://minecraft-heads.com/custom-heads/head/56785-wrong-mark
    WRONG_MARK("Mjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=");

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
