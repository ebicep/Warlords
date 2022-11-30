package com.ebicep.warlords.util.pve;

import javax.annotation.Nonnull;

// https://minecraft-heads.com/
public enum SkullID {
    // https://prnt.sc/KlIfvUXVDDp8
    SCULK_CORRUPTION(
            "c1a2f7d2-be42-43f0-83e7-1e84ddd53882",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTAxYzY3MmJiNDFiYzJmNTE2ODkwYzY2ZjM5NjIxNTRlMTQ2MDAzOGNhODM4MzdjOWUxNzdiM2M4ZTNjZDkwYyJ9fX0="
    ),
    // https://prnt.sc/lEMITadLJdty
    BLUE_GHOST(
            "98858a42-f28d-4708-965b-c4987f5ad104",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmViMTBjYjg3NTNkYTgwMDMwMzIwYmUyMzg5MWExM2ZmYzI4MmQ4NWU2ZDJiNzg2YmNlZjRlYmYyMzFhZDJlYSJ9fX0="
    ),
    // https://prnt.sc/QsBnWM03Uitp
    PURPLE_RIFT(
            "9516ba4d-7199-480e-8e15-f81569054612",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWMwODk0OTMxYTUxMDM4N2U0ZjYxMzgyOGYwMjM5Y2E0ZDkwODUzNDk5NjM3ZjYwNzlkMzkzMjdmMTQ2ZjBlOSJ9fX0="
    ),
    // https://prnt.sc/GW9JTDWANHjI
    LAVA_MONSTER(
            "0cacd58d-1100-41b3-938a-ec36b235d1bd",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM0OWE0NDFmNjFhYmYzZGM0M2UzNzUzOWVmM2E5YzQ1MTQwYmYzNzQyMTM5ZmY1MzY3OGU5MDJmNTczNzE0MiJ9fX0="
    ),
    // https://prnt.sc/U4ngXQYKVS4j
    IRON_QUEEN(
            "e857e0bd-cccc-431a-8df2-fda9a0d1b79b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTYyY2ZhZjM4ZGVhZWUzZDA4ZDk2NWNlNmRjNTgwYzhjY2Q3NWQzYjE0ZDFiZmViNGUwOTM3MjZiNmM3YjFlMiJ9fX0="
    ),
    // https://prnt.sc/COd07J8s4EIV
    DEMON_KING(
            "ddfa4368-e57b-4b0d-a891-303d6ce20d4e",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1NzE0ZjhhMDRiNTAyOWU3OWE5MDA3NmNlNTNiNGFkZGI2OTc4YThkYmUzMjAzMTIzMDc2Zjk5ZGQ1NDdiYiJ9fX0="
    ),
    // https://prnt.sc/qPX8fc4YJ4Ho
    DEMON_SKELETON(
            "eed60a6f-1249-4665-b191-94f847d684ef",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhNzdjNjY1OTU4ODQ0MDBmZTgxNzU2MWZkOGE0M2ZmOTkyYjQ1NjVlZjZjNWI1ZWQzMmRkMzkwOTVjOGIwMiJ9fX0="
    ),
    // https://prnt.sc/wZYR8rgfqTUj
    BURNING_WITHER_SKELETON(
            "e307413e-a49b-4fd6-acae-554e80015efa",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTFmNGY0ZGMzYjA5ODQ4MWQ4MTc1ZDg0ODkyYzE0NjM3ODU5YWNlNmI1MjQxN2Q0ZWRmZWRmNThlNmRiNTRmOSJ9fX0="
    ),
    // https://prnt.sc/6xLQRUy0lPko
    RED_EYE(
            "f327a9fc-26d1-4e2f-84c1-18741c587adf",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBhYzkwNzcxNzM5MzU4MDVhMzc5OTAzYmQ1OGQxMjc0NGViNDQyNjcwYmE5ZTAwNDMzMTI4ZDFjZDUyNjA5ZSJ9fX0="
    ),
    // https://prnt.sc/S2-LbZk0XORu
    DEMON(
            "db805929-f851-4067-82a9-f9c4fef1d42b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYyNGIwMmY3MjdkMjBmYmU0NzlkMTUxMzk4NWEyZTRkM2ExYTQ5MzA5ZGYwNDk2ZDczZDBhNDE0ZDA4ODdlMCJ9fX0="
    ),
    END_MONSTER(
            "b32a6652-bedc-4ae5-b873-9f105b787e95",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="
    ),
    SAMURAI(
            "3b53cfeb-2baf-4b11-ae51-fcc8fd08027b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUyZjg3MzllZGM5OWNmMWYwM2NmZDdhYTI4ODQyNzA0YTVlOWMwZjMxMTNiMTE4OGE2MzFjYmFmNWY3ZmY5ZSJ9fX0="
    ),
    FACELESS_BANDIT(
            "10e5ac9b-3c3b-4b2d-98f9-57ccc30e237e",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VlZTJjYjQxY2VkZTVhYTQ0MTE3MTYyNGUxZTFlMzg4YjgyNjJhNGEwYmI5ZGZiZmQ4ODljYTAyYzQxY2IifX19="
    ),
    FACELESS_MAGE(
            "b31f22fd-3cd3-4183-836b-d98d003df922",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFiOGI2ZDA4YjRhMTdlYjVmMTlkYTNlNTI4MzczYTBkNmQzNjA5ZTEzZmU0OWRjMDIwMDkxNDQ3NWQ4MjNhZiJ9fX0="
    ),
    PURPLE_KNIGHT(
            "c8f8e39e-f616-4564-bdb0-797c7b1c98e3",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEwM2IzNWQ0NDg1MGNiNDJiMDAwMTdhZGRiN2Y4NWVhYWMyNGI1NmEwY2Q1MWNhMWNhYzIyYjZlYjQyM2UxMSJ9fX0="
    ),
    PURPLE_ENDERMAN(
            "13d9ec9c-2de1-4f0c-b579-2eeaf95b6ca7",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY5MDIwYzA3ZDg3NWJhZDE0NDAzMzdhZGI1NWEwOGMxNWRiMDZiOTk0NjQ2YTY5MTc5NWY0Y2QyOTNmZTNkZSJ9fX0="
    ),
    NEON_ENDERMAN(
            "88e9a827-7429-4c7f-9f49-dfb8aa2123d0",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTJkZWZiZTNjZGUzMjZkNDUxMWJiNTMzMzlkNzc3YWZhNzAzZjNlYzRkYWE2OTdkNjFhNDQwMjc0NGNiYjBjZCJ9fX0="
    ),
    ITEM_CORE_BLUE(
            "009a87cf-a01b-4f26-ba2b-75dbee0cea98",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThmNWRjOTI3OTIzMjc5MTI3YTlkMmFkZTg2NDMyZjk4Nzc2MDljYjlmODM4NTRhNWI4OTJiZjdjYWQ5ZGYyZiJ9fX0="
    ),

    ;

    private final String id;
    private final String textureId;

    /**
     *
     * @param id uuid of the given custom skull.
     * @param textureId texture ID encoded in Base64, cannot be null.
     */
    SkullID(String id, @Nonnull String textureId) {
        this.id = id;
        this.textureId = textureId;
    }

    public String getTextureId() {
        return textureId;
    }

    public String getId() {
        return id;
    }
}
