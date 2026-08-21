package com.incomparablymobs.event;

import com.google.common.collect.Lists;
import com.incomparablymobs.config.EntityFilter;
import com.incomparablymobs.config.ModConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// EntityTickHandler はサーバー側 (ServerLevel) の entity.tick() のみを加速しているため、
// 移動やAIの「論理」は速くなる一方で、歩行モーションや攻撃スイングなどの
// アニメーション状態 (LivingEntity#tick() 内で更新される値) はクライアント側で
// 別途保持・更新されており、そちらは通常速度のままだった。
// クライアント側のエンティティ (ClientLevel が保持するレンダリング用コピー) も
// 同じ回数だけ追加で tick() することで、見た目のアニメーション速度をロジック側の
// 加速と一致させる。
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "incomparablymobs", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEntityAnimationTickHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ClientLevel clientLevel) {

            int extraTicks = ModConfig.TICK_RATE_MULTIPLIER.get() - 1;
            if (extraTicks <= 0) {
                return;
            }

            // レンダリング対象のエンティティ一覧をコピーしてから走査し、
            // tick中のエンティティ追加・削除によるエラーを防ぎます (EntityTickHandler と同様)。
            // サーバー側と同じ理由で、加速対象は LivingEntity (Playerを除く) に限定します。
            for (Entity entity : Lists.newArrayList(clientLevel.entitiesForRendering())) {
                if (entity instanceof LivingEntity && !(entity instanceof Player) && EntityFilter.isTarget(entity)) {
                    for (int i = 0; i < extraTicks; i++) {
                        if (!entity.isAlive() || entity.isRemoved()) {
                            break;
                        }
                        try {
                            entity.tick();
                        } catch (Throwable t) {
                            // サーバー側 (EntityTickHandler) と同じ理由で、1体のエンティティの
                            // 不具合がクライアントのクラッシュに波及しないようここで止めます。
                            LOGGER.error("[incomparablymobs] Extra client tick threw an exception for entity {} ({}) at {}; skipping further extra ticks for it this tick.",
                                    entity.getClass().getName(), entity.getUUID(), entity.blockPosition(), t);
                            break;
                        }
                    }
                }
            }
        }
    }
}
