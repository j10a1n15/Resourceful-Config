package com.teamresourceful.resourcefulconfig.client.components.categories;

import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.ListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class CategoriesListWidget extends ListWidget {

    public CategoriesListWidget(int width, int height) {
        super(0, 0, width, height);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blitSprite(RenderType::guiTextured, ModSprites.CONTAINER, getX(), getY(), getWidth(), getHeight());
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
