package com.incomparablymobs.difficulty;

import com.incomparablymobs.config.ModConfig;
import net.minecraft.network.chat.Component;

// 3段階の難易度プリセット。それぞれの倍率をまとめて ModConfig に適用します。
// applyTo() はサーバー・クライアントどちらのスレッドから呼んでも安全なように、
// ModConfig の各 ConfigValue#set() のみを呼び出す単純な実装にしています。
public enum ModDifficulty {

    NOVICE("novice", 1, 1.0, 1.0, 1.0),
    DEFAULT("default", 1, 2.0, 1.5, 1.5),
    APOCALYPSE("apocalypse", 2, 2.0, 2.0, 3.0);

    private final String id;
    private final int tickRateMultiplier;
    private final double movementSpeedMultiplier;
    private final double damageMultiplier;
    private final double healthMultiplier;

    ModDifficulty(String id, int tickRateMultiplier, double movementSpeedMultiplier, double damageMultiplier, double healthMultiplier) {
        this.id = id;
        this.tickRateMultiplier = tickRateMultiplier;
        this.movementSpeedMultiplier = movementSpeedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.healthMultiplier = healthMultiplier;
    }

    public String getId() {
        return this.id;
    }

    public Component getDisplayName() {
        return Component.translatable("difficulty.incomparablymobs." + this.id);
    }

    public void applyTo() {
        ModConfig.TICK_RATE_MULTIPLIER.set(this.tickRateMultiplier);
        ModConfig.MOVEMENT_SPEED_MULTIPLIER.set(this.movementSpeedMultiplier);
        ModConfig.DAMAGE_MULTIPLIER.set(this.damageMultiplier);
        ModConfig.HEALTH_MULTIPLIER.set(this.healthMultiplier);
        ModConfig.SELECTED_DIFFICULTY.set(this.id);
    }

    public static ModDifficulty byId(String id) {
        for (ModDifficulty difficulty : values()) {
            if (difficulty.id.equals(id)) {
                return difficulty;
            }
        }
        return null;
    }
}
