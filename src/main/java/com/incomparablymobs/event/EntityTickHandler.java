package com.incomparablymobs.event;

import com.google.common.collect.Lists; // この行を追加
import com.incomparablymobs.config.ModConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "incomparablymobs")
public class EntityTickHandler {

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
                if (entity != null && !(entity instanceof Player)) {
                    // --- ここまで修正 ---
                    for (int i = 0; i < extraTicks; i++) {
                        if (entity.isAlive() && !entity.isRemoved()) {
                            entity.tick();
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
}