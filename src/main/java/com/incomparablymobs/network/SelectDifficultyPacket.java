package com.incomparablymobs.network;

import com.incomparablymobs.config.ModConfig;
import com.incomparablymobs.difficulty.ModDifficulty;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

// クライアントの難易度選択画面でボタンが押されたときにサーバーへ送られるパケット。
public class SelectDifficultyPacket {

    private final String difficultyId;

    public SelectDifficultyPacket(String difficultyId) {
        this.difficultyId = difficultyId;
    }

    public static void encode(SelectDifficultyPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.difficultyId);
    }

    public static SelectDifficultyPacket decode(FriendlyByteBuf buf) {
        return new SelectDifficultyPacket(buf.readUtf());
    }

    public static void handle(SelectDifficultyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) {
                return;
            }

            // クライアント側の画面/コマンドでも同じチェックを行っていますが、
            // 改造クライアント等からパケットが直接送られてきた場合に備えてサーバー側でも検証します。
            if (!ModConfig.ENABLE_DIFFICULTY_SELECTION.get()) {
                sender.sendSystemMessage(Component.translatable("message.incomparablymobs.difficulty.disabled"));
                return;
            }

            // 難易度はワールド全体に影響する設定のため、権限を持つプレイヤーのみ変更を許可します。
            if (!sender.hasPermissions(2)) {
                sender.sendSystemMessage(Component.translatable("message.incomparablymobs.difficulty.no_permission"));
                return;
            }

            ModDifficulty difficulty = ModDifficulty.byId(packet.difficultyId);
            if (difficulty == null) {
                return;
            }

            // この選択が「ワールドで初めて選ばれた難易度」かどうかを、適用前の値で判定します。
            boolean isFirstSelection = ModConfig.SELECTED_DIFFICULTY.get().isEmpty();

            difficulty.applyTo();
            sender.sendSystemMessage(Component.translatable("message.incomparablymobs.difficulty.changed", difficulty.getDisplayName()));

            if (isFirstSelection) {
                // 初めて難易度を選んだプレイヤーに、以後の変更方法を案内します。
                sender.sendSystemMessage(Component.translatable("message.incomparablymobs.difficulty.hint_command"));
            }

            // 全クライアントのローカルコンフィグ値 (アニメーション加速などが参照する) も
            // 最新の難易度に合わせて更新するため、変更後の値を全員へ配信します。
            NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncDifficultyPacket(difficulty.getId()));
        });
        context.setPacketHandled(true);
    }
}
