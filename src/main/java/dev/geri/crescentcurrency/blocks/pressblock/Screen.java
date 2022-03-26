package dev.geri.crescentcurrency.blocks.pressblock;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.geri.crescentcurrency.CrescentCurrency;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

public class Screen extends HandledScreen<ScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(CrescentCurrency.MODID, "textures/gui/press.png");

    public Screen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 177;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        this.drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.playerInventoryTitleY = Integer.MAX_VALUE;

        // Render tooltip
        final List<OrderedText> text = List.of(new TranslatableText("crescentcurrency.gui.button").asOrderedText());
        ButtonWidget.TooltipSupplier tooltip = (button, matrices, mouseX, mouseY) -> renderOrderedTooltip(matrices, text, mouseX, mouseY);

        // Create button
        ButtonWidget disenchantButton = new TexturedButtonWidget(
                x + 136,
                y + 21,
                34,
                34,
                0,
                0,
                34,
                new Identifier(CrescentCurrency.MODID, "textures/gui/button.png"),
                68,
                68,
                button -> ClientPlayNetworking.send(new Identifier(CrescentCurrency.MODID, "press_request"), PacketByteBufs.empty()),
                tooltip,
                Text.of("")
        );


        disenchantButton.active = true;
        this.addDrawableChild(disenchantButton);
    }

}

