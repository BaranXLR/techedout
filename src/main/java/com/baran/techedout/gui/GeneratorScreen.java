package com.baran.techedout.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.baran.techedout.TechedOut;
import com.baran.techedout.container.GeneratorContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GeneratorScreen extends ContainerScreen<GeneratorContainer> {

    private ResourceLocation GUI = new ResourceLocation(TechedOut.MODID, "textures/gui/generator_gui.png");

    public GeneratorScreen(GeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }
    
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    	this.renderBackground(ms);
    	super.render(ms, mouseX, mouseY, partialTicks);
    	this.renderHoveredTooltip(ms, mouseX, mouseY);
    }
    
    protected void renderHoveredTooltip(MatrixStack ms, int x, int y) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
           this.renderTooltip(ms, this.hoveredSlot.getStack(), x, y);
        }
        if(x > guiLeft + 10 && x < guiLeft + 25 && y > guiTop + 5 && y < guiTop + 65) {
        	this.renderTooltip(ms, new TranslationTextComponent("message.energy", Integer.toString((this.container.getEnergy()))), x, y);
        }

     }
    
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack ms, int mouseX, int mouseY) {
    	Minecraft instance = Minecraft.getInstance();
		drawString(ms, instance.fontRenderer, "Test: " + container.getTicksLeft(), 10, 10, 0xffffff);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(ms, relX, relY, 0, 0, this.xSize, this.ySize);
        int i;
        if (((GeneratorContainer) this.container).getTicksLeft() > 0) {
            i = ((GeneratorContainer) this.container).getTicksLeftScaled(13);
            this.blit(ms, guiLeft + 82, guiTop + 31 + 12 - i, 180, 12 - i, 14, i + 1);
        }
        int i1;
        if (((GeneratorContainer) this.container).getEnergy() > 0) {
            i = ((GeneratorContainer) this.container).getEnergyScaled(58);
            this.blit(ms, guiLeft + 11, guiTop + 6 + 58 - i, 180, 14, 14, i + 1);
        }
        
    }
    
}
