package com.incomparablymobs.network;

import com.incomparablymobs.incomparablymobs;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(incomparablymobs.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextId = 0;

    public static void register() {
        CHANNEL.registerMessage(nextId++, OpenDifficultyScreenPacket.class,
                OpenDifficultyScreenPacket::encode, OpenDifficultyScreenPacket::decode, OpenDifficultyScreenPacket::handle);
        CHANNEL.registerMessage(nextId++, SelectDifficultyPacket.class,
                SelectDifficultyPacket::encode, SelectDifficultyPacket::decode, SelectDifficultyPacket::handle);
        CHANNEL.registerMessage(nextId++, SyncDifficultyPacket.class,
                SyncDifficultyPacket::encode, SyncDifficultyPacket::decode, SyncDifficultyPacket::handle);
    }
}
