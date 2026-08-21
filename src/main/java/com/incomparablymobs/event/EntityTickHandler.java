package com.incomparablymobs.event;

import com.google.common.collect.Lists; // この行を追加
import com.incomparablymobs.config.EntityFilter;
import com.incomparablymobs.config.ModConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = "incomparablymobs")
public class EntityTickHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {

            int extraTicks = ModConfig.TICK_RATE_MULTIPLIER.get() - 1;
            if (extraTicks <= 0) {
                return;
            }

            // --- ここから修正 ---
            // serverLevel.getAllEntities() を直接ループする代わりに、
            // 新しいリストにコピーしてからループすることで、処理中のリスト変更によるエラーを防ぎます。
            for (Entity entity : Lists.newArrayList(serverLevel.getAllEntities())) {
                // entityがnullでないことを念のため確認します。
                // 矢やアイテム、TNT などの非LivingEntityを追加tickすると、内部状態の想定外の
                // 変化 (多重ヒット判定・爆発処理の重複実行など) によりクラッシュすることがあるため、
                // 加速対象は LivingEntity (Playerを除く) に限定します。
                if (entity instanceof LivingEntity && !(entity instanceof Player) && EntityFilter.isTarget(entity)) {
                    // --- ここまで修正 ---
                    for (int i = 0; i < extraTicks; i++) {
                        if (!entity.isAlive() || entity.isRemoved()) {
                            break;
                        }
                        try {
                            entity.tick();
                        } catch (Throwable t) {
                            // 他Modのエンティティは「1ゲームtickにつき1回だけtickされる」ことを
                            // 前提に実装されている場合があり、追加tickによってその前提が崩れて
                            // 例外を投げることがあります (例: 攻撃/ヒット判定を1回しか想定していない実装)。
                            // ここで捕まえて処理を継続することで、1体のエンティティの不具合が
                            // サーバー全体のクラッシュに波及するのを防ぎます。
                            LOGGER.error("[incomparablymobs] Extra tick threw an exception for entity {} ({}) at {}; skipping further extra ticks for it this tick.",
                                    entity.getClass().getName(), entity.getUUID(), entity.blockPosition(), t);
                            break;
                        }
                    }
                }
            }
        }
    }
}