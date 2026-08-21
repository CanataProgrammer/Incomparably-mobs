package com.incomparablymobs.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

// このMod全体の効果 (移動速度・攻撃力・最大HP・tick加速・アニメーション加速) を
// 対象とするエンティティかどうかを、ホワイトリスト/ブラックリストのどちらか一方
// (entityFilterMode で切り替え) で判定します。
public final class EntityFilter {

    private EntityFilter() {
    }

    public static boolean isTarget(Entity entity) {
        ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        // レジストリ名が取得できない (通常は起こり得ない) 場合は、
        // 安全側として従来通り対象とみなします。
        if (registryName == null) {
            return true;
        }

        String id = registryName.toString();

        if ("whitelist".equals(ModConfig.ENTITY_FILTER_MODE.get())) {
            return ModConfig.ENTITY_WHITELIST.get().contains(id);
        }

        return !ModConfig.ENTITY_BLACKLIST.get().contains(id);
    }
}
