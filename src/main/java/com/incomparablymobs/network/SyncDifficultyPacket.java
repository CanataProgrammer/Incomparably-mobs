package com.incomparablymobs.network;

import com.incomparablymobs.client.ClientDifficultyScreenHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// サーバーが難易度の変更を確定した後、全クライアントへ新しい難易度IDを配信するパケット。
public class SyncDifficultyPacket {

    private final String difficultyId;

    public SyncDifficultyPacket(String difficultyId) {
        this.difficultyId = difficultyId;
    }

    public static void encode(SyncDifficultyPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.difficultyId);
    }

    public static SyncDifficultyPacket decode(FriendlyByteBuf buf) {
        return new SyncDifficultyPacket(buf.readUtf());
    }

    public static void handle(SyncDifficultyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientDifficultyScreenHandler.sync(packet.difficultyId)));
        context.setPacketHandled(true);
    }
}
