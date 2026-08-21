package com.incomparablymobs.event;

import com.incomparablymobs.config.ModConfig;
import com.incomparablymobs.network.NetworkHandler;
import com.incomparablymobs.network.OpenDifficultyScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

// ワールドに参加したプレイヤーに対し、難易度がまだ選択されていなければ
// 難易度選択画面を自動表示するよう通知パケットを送ります。
// 一度選択されると ModConfig.SELECTED_DIFFICULTY が空でなくなるため、以後は自動表示されません
// (それ以降は /incomparablymobs difficulty コマンドで同じ画面を開けます)。
@Mod.EventBusSubscriber(modid = "incomparablymobs")
public class PlayerJoinDifficultyPromptHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ModConfig.ENABLE_DIFFICULTY_SELECTION.get()) {
            return;
        }

        if (!ModConfig.SELECTED_DIFFICULTY.get().isEmpty()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // 難易度はワールド全体に影響する設定のため、権限を持たないプレイヤーには選択を求めません。
        if (!player.hasPermissions(2)) {
            return;
        }

        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenDifficultyScreenPacket(ModConfig.SELECTED_DIFFICULTY.get()));
    }
}
