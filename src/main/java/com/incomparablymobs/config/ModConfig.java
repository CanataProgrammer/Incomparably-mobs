package com.incomparablymobs.config;

import net.minecraftforge.common.ForgeConfigSpec;

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


    // --- 設定のビルド ---
    // これまでの定義を元に、Configの仕様（SPEC）を完成させます。
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}