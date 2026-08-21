package com.incomparablymobs.network;

import com.incomparablymobs.client.ClientDifficultyScreenHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// サーバーからクライアントへ「難易度選択画面を開いてください」と伝える通知パケット。
// 現在選択中の難易度ID (未選択なら空文字列) を一緒に送り、画面側で表示できるようにします。
public class OpenDifficultyScreenPacket {

    private final String currentDifficultyId;

    public OpenDifficultyScreenPacket(String currentDifficultyId) {
        this.currentDifficultyId = currentDifficultyId;
    }

    public static void encode(OpenDifficultyScreenPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.currentDifficultyId);
    }

    public static OpenDifficultyScreenPacket decode(FriendlyByteBuf buf) {
        return new OpenDifficultyScreenPacket(buf.readUtf());
    }

    public static void handle(OpenDifficultyScreenPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientDifficultyScreenHandler.open(packet.currentDifficultyId)));
        context.setPacketHandled(true);
    }
}
