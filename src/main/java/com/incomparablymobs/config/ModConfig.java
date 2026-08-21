package com.incomparablymobs.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    // ForgeConfigSpec.Builderを使って設定項目を定義していきます。
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // --- 設定項目の定義 ---

    // Tickrateの倍率 (元のTickに何回追加するか)
    // デフォルトは2倍。クラッシュを避けるため、上限を4に設定しています。
    public static final ForgeConfigSpec.IntValue TICK_RATE_MULTIPLIER = BUILDER
            .comment("Tickrate multiplier (Default 2, Max 2147483647)(e.g., 2 means 2x speed). Note: Values above 4 may cause crashes.")
            .defineInRange("tickRateMultiplier", 2, 1, 2147483647);

    // 移動速度の倍率
    // デフォルトは2.0倍。
    public static final ForgeConfigSpec.DoubleValue MOVEMENT_SPEED_MULTIPLIER = BUILDER
            .comment("Movement speed multiplier for mobs. (Default 2.0, Max 2147483647.0)(e.g., 2.0 means 2x speed)")
            .defineInRange("movementSpeedMultiplier", 2.0, 1.0, 2147483647.0);

    // 攻撃力の倍率
    // デフォルトは1.5倍。
    public static final ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER = BUILDER
            .comment("Damage multiplier for mobs. (Default 1.5, Max 2147483647.0)(e.g., 1.5 means 1.5x damage)")
            .defineInRange("damageMultiplier", 1.5, 1.0, 2147483647.0);

    // 最大HPの倍率
    // デフォルトは1.0倍（変更なし）。
    public static final ForgeConfigSpec.DoubleValue HEALTH_MULTIPLIER = BUILDER
            .comment("Max health multiplier for mobs. (Default 1.0, Max 2147483647.0)(e.g., 2.0 means 2x max health)")
            .defineInRange("healthMultiplier", 1.0, 1.0, 2147483647.0);

    // 選択中の難易度プリセットのID (novice / default / apocalypse)。
    // 空文字列は「まだ難易度が選択されていない」ことを表し、ワールド参加時に
    // 難易度選択画面を自動表示するかどうかの判定に使われます。
    public static final ForgeConfigSpec.ConfigValue<String> SELECTED_DIFFICULTY = BUILDER
            .comment("Currently selected difficulty preset id (novice / default / apocalypse). Empty means not yet selected.")
            .define("selectedDifficulty", "");

    // 難易度選択画面 (ワールド参加時の自動表示 + /incomparablymobs difficulty コマンド)
    // そのものを丸ごと無効化するスイッチ。false にするとどちらの経路からも使用できなくなります。
    public static final ForgeConfigSpec.BooleanValue ENABLE_DIFFICULTY_SELECTION = BUILDER
            .comment("If false, disables the difficulty selection feature entirely: neither the auto-prompt on world join nor the /incomparablymobs difficulty command will work.")
            .define("enableDifficultySelection", true);

    // エンティティのフィルタ方式。
    // "blacklist" (デフォルト): entityBlacklist に含まれるエンティティ「以外」全てを対象にする。
    //                          リストが空の場合、これまで通り全てのMobが対象になります (後方互換)。
    // "whitelist": entityWhitelist に含まれるエンティティ「のみ」を対象にする。
    public static final ForgeConfigSpec.ConfigValue<String> ENTITY_FILTER_MODE = BUILDER
            .comment("Entity filter mode: \"blacklist\" (affect everything except entityBlacklist, default) or \"whitelist\" (affect only entityWhitelist).")
            .defineInList("entityFilterMode", "blacklist", List.of("whitelist", "blacklist"));

    // ホワイトリスト。entityFilterMode が "whitelist" のときのみ使用されます。
    // エンティティのレジストリ名 (例: "minecraft:zombie") を文字列で指定します。
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_WHITELIST = BUILDER
            .comment("Entity registry names (e.g. \"minecraft:zombie\") to affect when entityFilterMode is \"whitelist\". Ignored in \"blacklist\" mode.")
            .defineList("entityWhitelist", ArrayList::new, obj -> obj instanceof String);

    // ブラックリスト。entityFilterMode が "blacklist" のときのみ使用されます。
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_BLACKLIST = BUILDER
            .comment("Entity registry names (e.g. \"minecraft:zombie\") to exclude when entityFilterMode is \"blacklist\". Ignored in \"whitelist\" mode.")
            .defineList("entityBlacklist", ArrayList::new, obj -> obj instanceof String);


    // --- 設定のビルド ---
    // これまでの定義を元に、Configの仕様（SPEC）を完成させます。
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}