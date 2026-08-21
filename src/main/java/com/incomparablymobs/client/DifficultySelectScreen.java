package com.incomparablymobs.client;

import com.incomparablymobs.difficulty.ModDifficulty;
import com.incomparablymobs.network.NetworkHandler;
import com.incomparablymobs.network.SelectDifficultyPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

// ワールド参加時の自動表示、および /incomparablymobs difficulty コマンドの両方から
// 開かれる難易度選択画面。3段階 (Novice / Default / Apocalypse) をボタンで選択します。
@OnlyIn(Dist.CLIENT)
public class DifficultySelectScreen extends Screen {

    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COLUMN_GAP = 20;

    @Nullable
    private final String currentDifficultyId;

    public DifficultySelectScreen(@Nullable String currentDifficultyId) {
        super(Component.translatable("screen.incomparablymobs.difficulty.title"));
        this.currentDifficultyId = currentDifficultyId;
    }

    @Override
    protected void init() {
        ModDifficulty[] difficulties = ModDifficulty.values();
        int startX = this.getStartX(difficulties.length);
        int buttonY = this.height / 2 - BUTTON_HEIGHT / 2;

        for (int i = 0; i < difficulties.length; i++) {
            ModDifficulty difficulty = difficulties[i];
            int x = startX + i * (BUTTON_WIDTH + COLUMN_GAP);

            this.addRenderableWidget(Button.builder(
                    Component.translatable("difficulty.incomparablymobs." + difficulty.getId() + ".button"),
                    button -> this.onSelect(difficulty)
            ).bounds(x, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }
    }

    private int getStartX(int columnCount) {
        int totalWidth = BUTTON_WIDTH * columnCount + COLUMN_GAP * (columnCount - 1);
        return (this.width - totalWidth) / 2;
    }

    private void onSelect(ModDifficulty difficulty) {
        NetworkHandler.CHANNEL.sendToServer(new SelectDifficultyPacket(difficulty.getId()));
        // 実際の適用はサーバーの承認後に SyncDifficultyPacket 経由で行われますが、
        // 選択操作自体はここで完了とみなし画面を閉じます。
        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        ModDifficulty[] difficulties = ModDifficulty.values();
        int startX = this.getStartX(difficulties.length);
        int buttonY = this.height / 2 - BUTTON_HEIGHT / 2;
        int headerY = buttonY - 14;
        int descY = buttonY + BUTTON_HEIGHT + 8;

        for (int i = 0; i < difficulties.length; i++) {
            ModDifficulty difficulty = difficulties[i];
            int centerX = startX + i * (BUTTON_WIDTH + COLUMN_GAP) + BUTTON_WIDTH / 2;

            guiGraphics.drawCenteredString(this.font,
                    Component.translatable("difficulty.incomparablymobs." + difficulty.getId()),
                    centerX, headerY, 0xFFFFFF);

            Component description = Component.translatable("difficulty.incomparablymobs." + difficulty.getId() + ".description");
            List<FormattedCharSequence> lines = this.font.split(description, BUTTON_WIDTH);
            int y = descY;
            for (FormattedCharSequence line : lines) {
                guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xA0A0A0);
                y += this.font.lineHeight + 2;
            }
        }

        if (this.currentDifficultyId != null && !this.currentDifficultyId.isEmpty()) {
            ModDifficulty current = ModDifficulty.byId(this.currentDifficultyId);
            if (current != null) {
                guiGraphics.drawCenteredString(this.font,
                        Component.translatable("screen.incomparablymobs.difficulty.current", current.getDisplayName()),
                        this.width / 2, this.height - 24, 0x808080);
            }
        }
    }
}
