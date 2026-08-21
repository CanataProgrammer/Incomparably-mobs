package com.incomparablymobs.client;

import com.incomparablymobs.config.ModConfig;
import com.mojang.brigadier.Command;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.commands.Commands.literal;

// "/incomparablymobs difficulty" コマンドを登録します。
// 画面を開くだけの操作はサーバーへ問い合わせる必要がないため、クライアント専用コマンドとして
// 実装しています (RegisterClientCommandsEvent はクライアント側でのみ発火します)。
@Mod.EventBusSubscriber(modid = "incomparablymobs", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                literal("incomparablymobs")
                        .then(literal("difficulty")
                                .executes(context -> {
                                    if (!ModConfig.ENABLE_DIFFICULTY_SELECTION.get()) {
                                        context.getSource().sendFailure(Component.translatable("message.incomparablymobs.difficulty.disabled"));
                                        return 0;
                                    }
                                    ClientDifficultyScreenHandler.open(ModConfig.SELECTED_DIFFICULTY.get());
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }
}
