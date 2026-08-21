package com.incomparablymobs.client;

import com.incomparablymobs.difficulty.ModDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// パケットハンドラ (共通コード) から呼ばれるクライアント専用の橋渡し役。
// Minecraft/Screen 等のクライアント専用クラスへの参照をこのクラスに閉じ込めることで、
// ネットワークパケットクラス自体がサーバー実行時にクライアント専用クラスを
// クラスロードしてしまうのを防ぎます。
@OnlyIn(Dist.CLIENT)
public class ClientDifficultyScreenHandler {

    public static void open(String currentDifficultyId) {
        Minecraft.getInstance().setScreen(new DifficultySelectScreen(currentDifficultyId));
    }

    public static void sync(String difficultyId) {
        ModDifficulty difficulty = ModDifficulty.byId(difficultyId);
        if (difficulty != null) {
            // クライアント側のローカルコンフィグ値も更新し、アニメーション加速など
            // クライアント側で参照している倍率をサーバーと一致させます。
            difficulty.applyTo();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof DifficultySelectScreen) {
            minecraft.setScreen(null);
        }
    }
}
