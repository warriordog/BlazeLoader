package net.acomputerdog.BlazeLoader.api.render;

import org.lwjgl.opengl.GL11;

import manilla.util.MCColor;
import manilla.util.wrapper.Wrapper.CTMUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BLRenderBlocks {
	private final RenderBlocks render;
	
	public BLRenderBlocks(RenderBlocks rb) { 
		render = rb;
	}
	
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
    	if (APIRenderBlocks.HasSpecialRender(block)) {
			return ((IRenderSpecial)block).renderWorldBlock(render, x, y, z);
		} else {
	        int var5 = APIRenderBlocks.getColorMultiplier(block, render.blockAccess, x, y, z);
	        float redComp = (float)(var5 >> 16 & 255) / 255.0F,
	        	greenComp = (float)(var5 >> 8 & 255) / 255.0F,
	        	blueComp = (float)(var5 & 255) / 255.0F;
	        
	        if (EntityRenderer.anaglyphEnable) {
	        	redComp = (redComp * 30.0F + greenComp * 59.0F + blueComp * 11.0F) / 100.0F;
	        	greenComp = (redComp * 30.0F + greenComp * 70.0F) / 100.0F;
	        	blueComp = (redComp * 30.0F + blueComp * 70.0F) / 100.0F;
	        }
	        
	        if (Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0) {
	        	if (render.partialRenderBounds) {
	        		return renderStandardBlockWithAmbientOcclusionPartial(block, x, y, z, redComp, greenComp, blueComp);
	        	}
        		return renderStandardBlockWithAmbientOcclusion(block, x, y, z, redComp, greenComp, blueComp);
	        }
        	return renderStandardBlockWithColorMultiplier(block, x, y, z, redComp, greenComp, blueComp);
		}
    }
		
	public void renderStandardBlockAsItem(Block block, int metadata, int renderColor, float mult) {
		Tessellator var4 = Tessellator.instance;
		boolean over = APIRenderBlocks.getRenderGrass(block, metadata, true);
		boolean var5 = APIRenderBlocks.getRenderGrass(block, metadata, false) && !over;
		
        if (render.useInventoryTint) {
        	renderColor = var5 ? 16777215 : APIRenderBlocks.getRenderColor(block, metadata);
        	setColorTint(renderColor, mult);
        }
        
		if (renderColor == 16) metadata = 1;

        block.setBlockBoundsForItemRender();
        render.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        drawItemSide(var4, 0F, -1F, 0F, block, metadata, 0);
        
        if ((var5 && render.useInventoryTint) | over) {
    		renderColor = APIRenderBlocks.getRenderColor(block, metadata);
        	
    		setColorTint(renderColor, mult);
        	
        	if (over) {
        		drawItemSideUniversal(var4, 0F, -1F, 0F, block, metadata, 0, true);
        		
        		if (!(var5 && render.useInventoryTint)) {
        			resetColorTint(mult);
        		}
        	}
        }
        
        drawItemSide(var4, 0F, 1F, 0F, block, metadata, 1);
        
        if (over) drawItemSideOverlay(var4, 0F, 1F, 0F, block, metadata, 1, mult, renderColor);
        
        if (var5 && render.useInventoryTint) resetColorTint(mult);
        
        drawItemSide(var4, 0F, 0F, -1F, block, metadata, 2);
        drawItemSide(var4, 0F, 0F, 1F, block, metadata, 3);
        drawItemSide(var4, -1F, 0F, 0F, block, metadata, 4);
        drawItemSide(var4, 1F, 0F, 0F, block, metadata, 5);
        
        if (over) {
        	setColorTint(renderColor, mult);
        	drawItemSideUniversal(var4, 0F, 0F, -1F, block, metadata, 2, true);
            drawItemSideUniversal(var4, 0F, 0F, 1F, block, metadata, 3, true);
            drawItemSideUniversal(var4, -1F, 0F, 0F, block, metadata, 4, true);
            drawItemSideUniversal(var4, 1F, 0F, 0F, block, metadata, 5, true);
            resetColorTint(mult);
        }
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	private boolean renderStandardBlockWithAmbientOcclusionPartial(Block block, int x, int y, int z, float multR, float multG, float multB) {
		render.enableAO = true;
        
        float brightTopLeft, brightBottomLeft, brightBottomRight, brightTopRight,
				aoLight;
        
        boolean result = false,
        		renderGrass = APIRenderBlocks.getRenderGrass(render, block, x, y, z),
        		canGrassX, canGrassZPos, canGrassZNeg;
        
        int originalBrightness = block.getBlockBrightness(render.blockAccess, x, y, z),
        		usedBrightness;
        
        Tessellator.instance.setBrightness(983055);
        
        if (block.shouldSideBeRendered(render.blockAccess, x, y - 1, z, 0)) {
            if (render.renderMinY <= 0.0D) --y;

            render.aoLightValueScratchXYNN = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPN = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessYZNN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessYZNP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            } else {
            	render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            } else {
            	render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            } else {
            	render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            } else {
            	render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x + 1, y, z + 1);
            }

            if (render.renderMinY <= 0.0D) ++y;

            if (render.renderMinY <= 0.0D || !render.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            brightTopLeft = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + aoLight) / 4.0F;
            brightTopRight = (render.aoLightValueScratchYZNP + aoLight + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
            brightBottomRight = (aoLight + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
            brightBottomLeft = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + aoLight + render.aoLightValueScratchYZNN) / 4.0F;
            
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, usedBrightness);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, usedBrightness);

            if (renderGrass) {
            	resetColors(0.5F, 0.5F, 0.5F);
            } else {
            	resetColors(multR * 0.5F, multG * 0.5F, multB * 0.5F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 0), 0);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 0);
            
            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x, y + 1, z, 1)) {
            if (render.renderMaxY >= 1.0D) ++y;

            render.aoLightValueScratchXYNP = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPP = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessXYPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            render.aoBrightnessYZPN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessYZPP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            } else {
            	render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            } else {
            	render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
            	render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            } else {
            	render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z - 1);
            }
            
            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
            	render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            } else {
            	render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z + 1);
            }

            if (render.renderMaxY >= 1.0D) --y;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            brightTopRight = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + aoLight) / 4.0F;
            brightTopLeft = (render.aoLightValueScratchYZPP + aoLight + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            brightBottomLeft = (aoLight + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + aoLight + render.aoLightValueScratchYZPN) / 4.0F;
            
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, usedBrightness);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, usedBrightness);
            
            if (APIRenderBlocks.getRenderGrass(block, render.blockAccess.getBlockMetadata(x, y, z), true)) {
            	resetColors(1F, 1F, 1F);
            } else {
            	resetColors(multR, multG, multB);
            }
            
            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 1), 1);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 1);
            
            result = true;
        }

        float var23, var22, var25, var24;
        int var27, var26, var29, var28;

        if (block.shouldSideBeRendered(render.blockAccess, x, y, z - 1, 2)) {
            if (render.renderMinZ <= 0.0D) --z;

            render.aoLightValueScratchXZNN = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPN = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPN = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXZNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessYZNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessYZPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            render.aoBrightnessXZPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            } else {
            	render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
            	render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            } else {
            	render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            } else {
            	render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
            	render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            } else {
            	render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x + 1, y + 1, z);
            }

            if (render.renderMinZ <= 0.0D) ++z;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            
            var23 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + aoLight + render.aoLightValueScratchYZPN) / 4.0F;
            var22 = (aoLight + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            var25 = (render.aoLightValueScratchYZNN + aoLight + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
            var24 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * render.renderMaxY * (1.0D - render.renderMinX) + (double)var22 * render.renderMinY * render.renderMinX + (double)var25 * (1.0D - render.renderMaxY) * render.renderMinX + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
            brightBottomLeft = (float)((double)var23 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)var22 * render.renderMaxY * render.renderMaxX + (double)var25 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
            brightBottomRight = (float)((double)var23 * render.renderMinY * (1.0D - render.renderMaxX) + (double)var22 * render.renderMinY * render.renderMaxX + (double)var25 * (1.0D - render.renderMinY) * render.renderMaxX + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
            brightTopRight = (float)((double)var23 * render.renderMinY * (1.0D - render.renderMinX) + (double)var22 * render.renderMinY * render.renderMinX + (double)var25 * (1.0D - render.renderMinY) * render.renderMinX + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
            
            var27 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, usedBrightness);
            var26 = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, usedBrightness);
            var29 = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, usedBrightness);
            var28 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, usedBrightness);
            
            render.brightnessTopLeft = render.mixAoBrightness(var27, var26, var29, var28, render.renderMaxY * (1.0D - render.renderMinX), render.renderMaxY * render.renderMinX, (1.0D - render.renderMaxY) * render.renderMinX, (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
            render.brightnessBottomLeft = render.mixAoBrightness(var27, var26, var29, var28, render.renderMaxY * (1.0D - render.renderMaxX), render.renderMaxY * render.renderMaxX, (1.0D - render.renderMaxY) * render.renderMaxX, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
            render.brightnessBottomRight = render.mixAoBrightness(var27, var26, var29, var28, render.renderMinY * (1.0D - render.renderMaxX), render.renderMinY * render.renderMaxX, (1.0D - render.renderMinY) * render.renderMaxX, (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
            render.brightnessTopRight = render.mixAoBrightness(var27, var26, var29, var28, render.renderMinY * (1.0D - render.renderMinX), render.renderMinY * render.renderMinX, (1.0D - render.renderMinY) * render.renderMinX, (1.0D - render.renderMinY) * (1.0D - render.renderMinX));

            if (renderGrass) {
            	resetColors(0.8F, 0.8F, 0.8F);
            } else {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 2), 2);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 2);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x, y, z + 1, 3)) {
            if (render.renderMaxZ >= 1.0D) ++z;

            render.aoLightValueScratchXZNP = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPP = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNP = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXZNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessXZPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            render.aoBrightnessYZNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessYZPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x + 1, y + 1, z);
            }

            if (render.renderMaxZ >= 1.0D) --z;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            var23 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + aoLight + render.aoLightValueScratchYZPP) / 4.0F;
            var22 = (aoLight + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            var25 = (render.aoLightValueScratchYZNP + aoLight + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
            var24 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * render.renderMaxY * (1.0D - render.renderMinX) + (double)var22 * render.renderMaxY * render.renderMinX + (double)var25 * (1.0D - render.renderMaxY) * render.renderMinX + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
            brightBottomLeft = (float)((double)var23 * render.renderMinY * (1.0D - render.renderMinX) + (double)var22 * render.renderMinY * render.renderMinX + (double)var25 * (1.0D - render.renderMinY) * render.renderMinX + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
            brightBottomRight = (float)((double)var23 * render.renderMinY * (1.0D - render.renderMaxX) + (double)var22 * render.renderMinY * render.renderMaxX + (double)var25 * (1.0D - render.renderMinY) * render.renderMaxX + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
            brightTopRight = (float)((double)var23 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)var22 * render.renderMaxY * render.renderMaxX + (double)var25 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
            
            var27 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, usedBrightness);
            var26 = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, usedBrightness);
            var29 = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, usedBrightness);
            var28 = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, usedBrightness);
            
            render.brightnessTopLeft = render.mixAoBrightness(var27, var28, var29, var26, render.renderMaxY * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * render.renderMinX, render.renderMaxY * render.renderMinX);
            render.brightnessBottomLeft = render.mixAoBrightness(var27, var28, var29, var26, render.renderMinY * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * render.renderMinX, render.renderMinY * render.renderMinX);
            render.brightnessBottomRight = render.mixAoBrightness(var27, var28, var29, var26, render.renderMinY * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * render.renderMaxX, render.renderMinY * render.renderMaxX);
            render.brightnessTopRight = render.mixAoBrightness(var27, var28, var29, var26, render.renderMaxY * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * render.renderMaxX, render.renderMaxY * render.renderMaxX);

            if (renderGrass) {
            	resetColors(0.8F, 0.8F, 0.8F);
            } else  {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 3), 3);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 3);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x - 1, y, z, 4)) {
            if (render.renderMinX <= 0.0D) --x;

            render.aoLightValueScratchXYNN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZNN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZNP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYNP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessXZNN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessXZNP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYNP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z + 1);
            }

            if (render.renderMinX <= 0.0D) ++x;
            
            if (render.renderMinX <= 0.0D || !render.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            
            var23 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + aoLight + render.aoLightValueScratchXZNP) / 4.0F;
            var22 = (aoLight + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
            var25 = (render.aoLightValueScratchXZNN + aoLight + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
            var24 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var22 * render.renderMaxY * render.renderMaxZ + (double)var25 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)var23 * (1.0D - render.renderMaxY) * render.renderMaxZ);
            brightBottomLeft = (float)((double)var22 * render.renderMaxY * render.renderMinZ + (double)var25 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)var24 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)var23 * (1.0D - render.renderMaxY) * render.renderMinZ);
            brightBottomRight = (float)((double)var22 * render.renderMinY * render.renderMinZ + (double)var25 * render.renderMinY * (1.0D - render.renderMinZ) + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)var23 * (1.0D - render.renderMinY) * render.renderMinZ);
            brightTopRight = (float)((double)var22 * render.renderMinY * render.renderMaxZ + (double)var25 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)var24 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)var23 * (1.0D - render.renderMinY) * render.renderMaxZ);
            
            var27 = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, usedBrightness);
            var26 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, usedBrightness);
            var29 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, usedBrightness);
            var28 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, usedBrightness);
            
            render.brightnessTopLeft = render.mixAoBrightness(var26, var29, var28, var27, render.renderMaxY * render.renderMaxZ, render.renderMaxY * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * render.renderMaxZ);
            render.brightnessBottomLeft = render.mixAoBrightness(var26, var29, var28, var27, render.renderMaxY * render.renderMinZ, render.renderMaxY * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * render.renderMinZ);
            render.brightnessBottomRight = render.mixAoBrightness(var26, var29, var28, var27, render.renderMinY * render.renderMinZ, render.renderMinY * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * render.renderMinZ);
            render.brightnessTopRight = render.mixAoBrightness(var26, var29, var28, var27, render.renderMinY * render.renderMaxZ, render.renderMinY * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * render.renderMaxZ);

            if (renderGrass) {
            	resetColors(0.6F, 0.6F, 0.6F);
            } else  {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 4), 4);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 4);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x + 1, y, z, 5)) {
            if (render.renderMaxX >= 1.0D) ++x;

            render.aoLightValueScratchXYPN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYPN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessXZPN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessXZPP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z + 1);
            }

            if (render.renderMaxX >= 1.0D) --x;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            var23 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + aoLight + render.aoLightValueScratchXZPP) / 4.0F;
            var22 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + aoLight) / 4.0F;
            var25 = (render.aoLightValueScratchXZPN + aoLight + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
            var24 = (aoLight + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * (1.0D - render.renderMinY) * render.renderMaxZ + (double)var22 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)var25 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)var24 * render.renderMinY * render.renderMaxZ);
            brightBottomLeft = (float)((double)var23 * (1.0D - render.renderMinY) * render.renderMinZ + (double)var22 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)var25 * render.renderMinY * (1.0D - render.renderMinZ) + (double)var24 * render.renderMinY * render.renderMinZ);
            brightBottomRight = (float)((double)var23 * (1.0D - render.renderMaxY) * render.renderMinZ + (double)var22 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)var25 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)var24 * render.renderMaxY * render.renderMinZ);
            brightTopRight = (float)((double)var23 * (1.0D - render.renderMaxY) * render.renderMaxZ + (double)var22 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)var25 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)var24 * render.renderMaxY * render.renderMaxZ);
            
            var27 = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, usedBrightness);
            var26 = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, usedBrightness);
            var29 = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, usedBrightness);
            var28 = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, usedBrightness);
            
            render.brightnessTopLeft = render.mixAoBrightness(var27, var28, var29, var26, (1.0D - render.renderMinY) * render.renderMaxZ, (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), render.renderMinY * (1.0D - render.renderMaxZ), render.renderMinY * render.renderMaxZ);
            render.brightnessBottomLeft = render.mixAoBrightness(var27, var28, var29, var26, (1.0D - render.renderMinY) * render.renderMinZ, (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), render.renderMinY * (1.0D - render.renderMinZ), render.renderMinY * render.renderMinZ);
            render.brightnessBottomRight = render.mixAoBrightness(var27, var28, var29, var26, (1.0D - render.renderMaxY) * render.renderMinZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), render.renderMaxY * (1.0D - render.renderMinZ), render.renderMaxY * render.renderMinZ);
            render.brightnessTopRight = render.mixAoBrightness(var27, var28, var29, var26, (1.0D - render.renderMaxY) * render.renderMaxZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), render.renderMaxY * (1.0D - render.renderMaxZ), render.renderMaxY * render.renderMaxZ);

            if (renderGrass) {
            	resetColors(0.6F, 0.6F, 0.6F);
            } else {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 5), 5);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 5);

            result = true;
        }

        render.enableAO = false;
        return result;
    }
	
	private boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float multR, float multG, float multB) {
        render.enableAO = true;
        
        float brightTopLeft, brightBottomLeft, brightBottomRight, brightTopRight,
        	aoLight;
        
        boolean result = false,
        		renderGrass = APIRenderBlocks.getRenderGrass(render, block, x, y, z),
        		canGrassX, canGrassZPos, canGrassZNeg;
        
        int originalBrightness = block.getBlockBrightness(render.blockAccess, x, y, z),
        		usedBrightness;
        
        Tessellator.instance.setBrightness(983055);

        if (block.shouldSideBeRendered(render.blockAccess, x, y - 1, z, 0)) {
            if (render.renderMinY <= 0.0D) --y;

            render.aoLightValueScratchXYNN = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPN = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessYZNN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessYZNP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            } else {
                render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            } else {
                render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            } else {
                render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            } else {
                render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x + 1, y, z + 1);
            }

            if (render.renderMinY <= 0.0D) ++y;

            if (render.renderMinY <= 0.0D || !render.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            brightTopLeft = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + aoLight) / 4.0F;
            brightTopRight = (render.aoLightValueScratchYZNP + aoLight + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
            brightBottomRight = (aoLight + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
            brightBottomLeft = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + aoLight + render.aoLightValueScratchYZNN) / 4.0F;
            
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, usedBrightness);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, usedBrightness);

            if (renderGrass) {
            	resetColors(0.5F, 0.5F, 0.5F);
            } else {
            	resetColors(multR * 0.5F, multG * 0.5F, multB * 0.5F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 0), 0);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 0);
            
            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x, y + 1, z, 1)) {
            if (render.renderMaxY >= 1.0D) ++y;

            render.aoLightValueScratchXYNP = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPP = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessXYPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            render.aoBrightnessYZPN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessYZPP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            } else {
                render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            } else {
                render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            
            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            } else {
                render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            } else {
                render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z + 1);
            }

            if (render.renderMaxY >= 1.0D) --y;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            brightTopRight = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + aoLight) / 4.0F;
            brightTopLeft = (render.aoLightValueScratchYZPP + aoLight + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            brightBottomLeft = (aoLight + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + aoLight + render.aoLightValueScratchYZPN) / 4.0F;
            
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, usedBrightness);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, usedBrightness);
            
            if (APIRenderBlocks.getRenderGrass(block, render.blockAccess.getBlockMetadata(x, y, z), true)) {
            	resetColors(1F, 1F, 1F);
            } else {
            	resetColors(multR, multG, multB);
            }
            
            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 1), 1);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 1);
            
            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x, y, z - 1, 2)) {
            if (render.renderMinZ <= 0.0D) --z;
            
            render.aoLightValueScratchXZNN = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPN = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPN = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXZNN = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessYZNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessYZPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            render.aoBrightnessXZPN = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x + 1, y + 1, z);
            }

            if (render.renderMinZ <= 0.0D) ++z;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            brightTopLeft = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + aoLight + render.aoLightValueScratchYZPN) / 4.0F;
            brightBottomLeft = (aoLight + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchYZNN + aoLight + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
            brightTopRight = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + aoLight) / 4.0F;
            
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, usedBrightness);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, usedBrightness);

            if (renderGrass) {
            	resetColors(0.8F, 0.8F, 0.8F);
            } else {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 2), 2);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 2);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x, y, z + 1, 3)) {
            if (render.renderMaxZ >= 1.0D) ++z;

            render.aoLightValueScratchXZNP = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPP = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZNP = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchYZPP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXZNP = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            render.aoBrightnessXZPP = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            render.aoBrightnessYZNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessYZPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x + 1, y + 1, z);
            }

            if (render.renderMaxZ >= 1.0D) --z;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            brightTopLeft = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + aoLight + render.aoLightValueScratchYZPP) / 4.0F;
            brightTopRight = (aoLight + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchYZNP + aoLight + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
            brightBottomLeft = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + aoLight) / 4.0F;
            
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, usedBrightness);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, usedBrightness);

            if (renderGrass) {
            	resetColors(0.8F, 0.8F, 0.8F);
            } else {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 3), 3);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 3);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x - 1, y, z, 4)) {
            if (render.renderMinX <= 0.0D) --x;

            render.aoLightValueScratchXYNN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZNN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZNP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYNP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessXZNN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessXZNP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYNP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNNN = render.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNNP = render.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            } else {
                render.aoLightValueScratchXYZNPN = render.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            } else {
                render.aoLightValueScratchXYZNPP = render.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZNPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z + 1);
            }

            if (render.renderMinX <= 0.0D) ++x;

            if (render.renderMinX <= 0.0D || !render.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x - 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            brightTopRight = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + aoLight + render.aoLightValueScratchXZNP) / 4.0F;
            brightTopLeft = (aoLight + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
            brightBottomLeft = (render.aoLightValueScratchXZNN + aoLight + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + aoLight) / 4.0F;
            
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, usedBrightness);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, usedBrightness);

            if (renderGrass) {
            	resetColors(0.6F, 0.6F, 0.6F);
            } else {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 4), 4);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 4);

            result = true;
        }

        if (block.shouldSideBeRendered(render.blockAccess, x + 1, y, z, 5)) {
            if (render.renderMaxX >= 1.0D) ++x;

            render.aoLightValueScratchXYPN = render.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPN = render.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXZPP = render.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            render.aoLightValueScratchXYPP = render.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            render.aoBrightnessXYPN = block.getBlockBrightness(render.blockAccess, x, y - 1, z);
            render.aoBrightnessXZPN = block.getBlockBrightness(render.blockAccess, x, y, z - 1);
            render.aoBrightnessXZPP = block.getBlockBrightness(render.blockAccess, x, y, z + 1);
            render.aoBrightnessXYPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z);
            
            canGrassX = render.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = render.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            canGrassZNeg = render.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPNN = render.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNN = block.getBlockBrightness(render.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPNP = render.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPNP = block.getBlockBrightness(render.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = render.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassX && !canGrassZNeg) {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            } else {
                render.aoLightValueScratchXYZPPN = render.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPN = block.getBlockBrightness(render.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassX && !canGrassZPos) {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            } else {
                render.aoLightValueScratchXYZPPP = render.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                render.aoBrightnessXYZPPP = block.getBlockBrightness(render.blockAccess, x, y + 1, z + 1);
            }

            if (render.renderMaxX >= 1.0D) --x;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(render.blockAccess, x + 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = render.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            brightTopLeft = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + aoLight + render.aoLightValueScratchXZPP) / 4.0F;
            brightBottomLeft = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + aoLight) / 4.0F;
            brightBottomRight = (render.aoLightValueScratchXZPN + aoLight + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
            brightTopRight = (aoLight + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, usedBrightness);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, usedBrightness);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, usedBrightness);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, usedBrightness);

            if (renderGrass) {
            	resetColors(0.6F, 0.6F, 0.6F);
            } else {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, render.getBlockIcon(block, render.blockAccess, x, y, z, 5), 5);
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 5);

            result = true;
        }

        render.enableAO = false;
        return result;
    }
	
    private boolean renderStandardBlockWithColorMultiplier(Block block, int x, int y, int z, float multR, float multG, float multB) {
        render.enableAO = false;
        boolean renderGrass = APIRenderBlocks.getRenderGrass(render, block, x, y, z);
        
        float[][] colors = new float[4][3];
        float[] mult = MCColor.newRGBarray(multR, multG, multB);

        {
	        if (!APIRenderBlocks.getRenderGrass(block, render.blockAccess.getBlockMetadata(x, y, z), true)) {
	        	colors[1] = mult;
	        } else {
	        	colors[1] = MCColor.newRGBarray(1);
	        }
	        
	        float side0 = 0.5F,
    			side2and3 = 0.8F,
				side4and5 = 0.6F;
	        
	        if (renderGrass) {
	        	colors[0] = MCColor.newRGBarray(side0);
	            colors[2] = MCColor.newRGBarray(side2and3);
	            colors[3] = MCColor.newRGBarray(side4and5);
	        } else {
	        	colors[0] = MCColor.multRGBarray(side0, mult);
	            colors[2] = MCColor.multRGBarray(side2and3, mult);
	            colors[3] = MCColor.multRGBarray(side4and5, mult);
	        }
        }
        
        Tessellator tessellator = Tessellator.instance;
        int mixedBrightness = block.getBlockBrightness(render.blockAccess, x, y, z);
        boolean result = false;
        result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, 0, y, -1, z, 0, colors[0], mult, 0);
        result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, 0, y, 1, z, 0, colors[1], mult, 1);
        result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, 0, y, 0, z, -1, colors[2], mult, 2);
        result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, 0, y, 0, z, 1, colors[2], mult, 3);
		result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, -1, y, 0, z, 0, colors[3], mult, 4);
		result |= renderFaceOld(tessellator, block, renderGrass, mixedBrightness, x, 1, y, 0, z, 0, colors[3], mult, 5);
        return result;
    }
    
    private boolean renderFaceOld(Tessellator tessellator, Block par1Block, boolean renderGrass, int mixedBrightness, int x, int xOff, int y, int yOff, int z, int zOff, float[] rgb, float[] rgbMult, int side) {
    	if (par1Block.shouldSideBeRendered(render.blockAccess, x + xOff, y + yOff, z + zOff, side)) {
            tessellator.setBrightness(checkBoundsForSide(side) ? mixedBrightness : par1Block.getBlockBrightness(render.blockAccess, x + xOff, y + yOff, z + zOff));
            tessellator.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);
            IIcon texture = render.getBlockIcon(par1Block, render.blockAccess, x, y, z, side);
            
            RenderFaceforSide(par1Block, x, y, z, texture, side);

            if (renderGrass && !APIRenderBlocks.getHasSnow(render, par1Block, x, y, z)) {
            	if ((texture = APIRenderBlocks.getIconSideOverlay(render, par1Block, x, y, z, side)) != null) {
	                tessellator.setColorOpaque_F(rgb[0] * rgbMult[0], rgb[1] * rgbMult[1], rgb[2] * rgbMult[2]);
	                RenderFaceforSide(par1Block, x, y, z, texture, side);
            	}
            }
            return true;
        }
    	return false;
    }
    
    private boolean checkBoundsForSide(int side) {
    	switch (side) {
    	case 0: return render.renderMinY > 0.0D;
    	case 1: return render.renderMaxY < 1.0D;
    	case 2: return render.renderMinZ > 0.0D;
    	case 3: return render.renderMaxZ < 1.0D;
    	case 4: return render.renderMinX > 0.0D;
    	case 5: return render.renderMaxX < 1.0D;
    	}
    	return false;
    }
        	
    public void RenderFaceforSide(Block block, double x, double y, double z, IIcon icon, int side) {
    	switch(side) {
	    	case 0: render.renderFaceYNeg(block, x, y, z, icon); //bottom
				return;
	    	case 1: render.renderFaceYPos(block, x, y, z, icon); //top
				return;
	    	case 2: render.renderFaceZNeg(block, x, y, z, icon); //east
				return;
	    	case 3: render.renderFaceZPos(block, x, y, z, icon); //west
				return;
	    	case 4: render.renderFaceXNeg(block, x, y, z, icon); //north
				return;
	    	case 5: render.renderFaceXPos(block, x, y, z, icon); //south
	    		return;
    	}
    }
    
    public void drawStandardItemSides(Tessellator tess, Block block, int data) {
    	drawItemSide(tess, 0F, -1F, 0F, block, data, 0);
        drawItemSide(tess, 0F, 1F, 0F, block, data, 1);
        drawItemSide(tess, 0F, 0F, -1F, block, data, 2);
        drawItemSide(tess, 0F, 0F, 1F, block, data, 3);
        drawItemSide(tess, -1F, 0F, 0F, block, data, 4);
        drawItemSide(tess, 1F, 0F, 0F, block, data, 5);
    }
    
    public void drawStandardItemSidesOverlay(Tessellator tess, Block block, int data, float par3) {
    	setColorTint(APIRenderBlocks.getRenderColor(block, data), par3);
        drawItemSideUniversal(tess, 0F, -1F, 0F, block, data, 0, true);
        drawItemSideUniversal(tess, 0F, 1F, 0F, block, data, 1, true);
        drawItemSideUniversal(tess, 0F, 0F, -1F, block, data, 2, true);
        drawItemSideUniversal(tess, 0F, 0F, 1F, block, data, 3, true);
        drawItemSideUniversal(tess, -1F, 0F, 0F, block, data, 4, true);
        drawItemSideUniversal(tess, 1F, 0F, 0F, block, data, 5, true);
        resetColorTint(par3);
    }
    
    public void drawItemSide(Tessellator tess, float a, float b, float c, Block block, int data, int side) {
    	drawItemSideUniversal(tess, a, b, c, block, data, side, false);
    }
    
	public void renderIconSideOverlay(boolean renderGrass, Block block, float multR, float multG, float multB, int x, int y, int z, int side) {
		if (render.fancyGrass && renderGrass && !APIRenderBlocks.getHasSnow(render, block, x, y, z)) {
        	IIcon over = APIRenderBlocks.getIconSideOverlay(render, block, x, y, z, side);
        	
        	if (over != null) {
        		multColors(multR, multG, multB);
                RenderFaceforSide(block, (double)x, (double)y, (double)z, over, side);
        	}
        }
	}
    
    public void drawItemSideOverlay(Tessellator tess, float a, float b, float c, Block block, int data, int side, float par3, int renderColor) {
    	setColorTint(renderColor, par3);
    	drawItemSideUniversal(tess, a, b, c, block, data, side, true);
    	resetColorTint(par3);
    }
    
    private void drawItemSideUniversal(Tessellator tess, float a, float b, float c, Block block, int data, int side, boolean inv) {
    	tess.startDrawingQuads();
        tess.setNormal(a, b, c);
        CTMUtils.start();
        
        if (inv) {
        	IIcon over = APIRenderBlocks.getIconSideOverlay(render, block, data, 0, 0, 0, side);
        	if (over != null) {
        		RenderFaceforSide(block, 0, 0, 0, over, side);
        	}
        } else {
        	RenderFaceforSide(block, 0, 0, 0, CTMUtils.getTile(render, block, side, data, tess), side);
        }
        tess.draw();
    }
    
    public void setColorTint(int renderColor, float multi) {
        GL11.glColor4f(MCColor.r(renderColor) * multi, MCColor.g(renderColor) * multi, MCColor.b(renderColor) * multi, 1.0F);
    }
    
    public void resetColorTint(float original) {
    	GL11.glColor4f(original, original, original, 1.0F);
    }
	
    private void resetColors(float r, float g, float b) {
    	render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = r;
    	render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = g;
    	render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = b;
    }
    
    public void multColors(float r, float g, float b) {
    	render.colorRedTopLeft *= r;
    	render.colorRedBottomLeft *= r;
        render.colorRedBottomRight *= r;
        render.colorRedTopRight *= r;
        render.colorGreenTopLeft *= g;
        render.colorGreenBottomLeft *= g;
        render.colorGreenBottomRight *= g;
        render.colorGreenTopRight *= g;
        render.colorBlueTopLeft *= b;
        render.colorBlueBottomLeft *= b;
        render.colorBlueBottomRight *= b;
        render.colorBlueTopRight *= b;
    }
    
	public void multColorCorners(float BotLeft, float BotRight, float TopLeft, float TopRight) {
		render.colorRedTopLeft *= TopLeft;
		render.colorGreenTopLeft *= TopLeft;
		render.colorBlueTopLeft *= TopLeft;
        
		render.colorRedTopRight *= TopRight;
		render.colorGreenTopRight *= TopRight;
		render.colorBlueTopRight *= TopRight;
        
		render.colorRedBottomLeft *= BotLeft;
		render.colorGreenBottomLeft *= BotLeft;
		render.colorBlueBottomLeft *= BotLeft;
        
		render.colorRedBottomRight *= BotRight;
		render.colorGreenBottomRight *= BotRight;
		render.colorBlueBottomRight *= BotRight;
	}
    
	public void renderThickX(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 0, diag);
	}
	
	public void renderThickY(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 1, diag);
	}
	
	public void renderThickZ(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 5, diag);
	}
	
	public void renderWithThickness(Block block, float thickness, float x, float y, float z, int swap, int diagonal) {
		IIcon par2Icon = block.getIcon(0, render.blockAccess.getBlockMetadata((int)x, (int)y, (int)z));
				
        float minU,maxU,minV,maxV;
        
        if (render.uvRotateTop == 1) {
        	minU = par2Icon.getMaxU();
        	maxU = par2Icon.getMinU();
        	minV = par2Icon.getMinV();
        	maxV = par2Icon.getMaxV();
        } else if (render.uvRotateTop == 2) {
	    	minU = par2Icon.getMinU();
	    	maxU = par2Icon.getMaxU();
	    	minV = par2Icon.getMaxV();
	    	maxV = par2Icon.getMinV();
        } else if (render.uvRotateTop == 3) {
        	minU = par2Icon.getMaxU();
	    	maxU = par2Icon.getMinU();
	    	minV = par2Icon.getMaxV();
	    	maxV = par2Icon.getMinV();
        } else {
        	minU = par2Icon.getMinU();
	    	maxU = par2Icon.getMaxU();
	    	minV = par2Icon.getMinV();
	    	maxV = par2Icon.getMaxV();
        }
		
		int origX = par2Icon.getIconWidth();
		int origY = par2Icon.getIconHeight();
		
		Tessellator tess = Tessellator.instance;
		
		tess.setColorOpaque_F(1, 1, 1);
		tess.setBrightness(block.getBlockBrightness(render.blockAccess, (int)x, (int)y, (int)z));
		tess.addTranslation(x, y, z);
		
		float var8 = 0.5F * (maxU - minU) / (float)origX;
        float var9 = 0.5F * (maxV - minV) / (float)origY;
        
        float var11, var12;
        
        float diagNeg = diagonal == 2 ? 1 : 0;
        float diag = diagonal == 1 ? 1 : 0;
        
        tess.setNormal(0.0F, 0.0F, 1.0F);
        addVertexWithUVSwap(tess, 0, 0, diag, maxU, maxV, swap);
        addVertexWithUVSwap(tess, 1, 0, diag, minU, maxV, swap);
        addVertexWithUVSwap(tess, 1, 1, diagNeg, minU, minV, swap);
        addVertexWithUVSwap(tess, 0, 1, diagNeg, maxU, minV, swap);
        
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, -1.0F);
        addVertexWithUVSwap(tess, 0, 1, thickness + diagNeg, maxU, minV, swap);
        addVertexWithUVSwap(tess, 1, 1, thickness + diagNeg, minU, minV, swap);
        addVertexWithUVSwap(tess, 1, 0, thickness + diag, minU, maxV, swap);
        addVertexWithUVSwap(tess, 0, 0, thickness + diag, maxU, maxV, swap);
        
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(-1.0F, 0.0F, 0.0F);
        for (int i = 0; i < origX; ++i) {
            var11 = (float)i / (float)origX;
            var12 = maxU + (minU - maxU) * var11 - var8;
            addVertexWithUVSwap(tess, var11, 0, thickness + diag, var12, maxV, swap);
            addVertexWithUVSwap(tess, var11, 0, diag, var12, maxV, swap);
            addVertexWithUVSwap(tess, var11, 1, diagNeg, var12, minV, swap);
            addVertexWithUVSwap(tess, var11, 1, thickness + diagNeg, var12, minV, swap);
        }

        float var13;
        
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(1.0F, 0.0F, 0.0F);
        for (int i = 0; i < origX; ++i) {
            var11 = (float)i / (float)origX;
            var12 = maxU + (minU - maxU) * var11 - var8;
            var13 = var11 + 1.0F / (float)origX;
            addVertexWithUVSwap(tess, var13, 1, thickness + diagNeg, var12, minV, swap);
            addVertexWithUVSwap(tess, var13, 1, diagNeg, var12, minV, swap);
            addVertexWithUVSwap(tess, var13, 0, diag, var12, maxV, swap);
            addVertexWithUVSwap(tess, var13, 0, thickness + diag, var12, maxV, swap);
        }

        float diagMove;
        
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 1.0F, 0.0F);
        for (int i = 0; i < origY; ++i) {
            var11 = (float)i / (float)origY;
            var12 = maxV + (minV - maxV) * var11 - var9;
            var13 = var11 + 1.0F / (float)origY;
            
            diagMove = (diag/origY * (origY - i - 1)) + (diagNeg*1 - (diagNeg/origY * (origY - i - 1)));
            
            addVertexWithUVSwap(tess, 0, var13, diagMove, maxU, var12, swap);
            addVertexWithUVSwap(tess, 1, var13, diagMove, minU, var12, swap);
            
            diagMove = (diag - (diag/origY * (i + 1))) + (1*diagNeg - (diagNeg - (diagNeg/origY * (i + 1))));
            
            addVertexWithUVSwap(tess, 1, var13, thickness + diagMove, minU, var12, swap);
            addVertexWithUVSwap(tess, 0, var13, thickness + diagMove, maxU, var12, swap);
        }
        
        tess.draw();
        tess.startDrawingQuads();
        tess.setNormal(0.0F, -1.0F, 0.0F);
        for (int i = 0; i < origY; ++i) {
            var11 = (float)i / (float)origY;
            var12 = maxV + (minV - maxV) * var11 - var9;
            
            diagMove = (diag/origY * (origY - i)) + (diagNeg*1 - (diagNeg/origY * (origY - i)));
            
            addVertexWithUVSwap(tess, 1, var11, diagMove, minU, var12, swap);
            addVertexWithUVSwap(tess, 0, var11, diagMove, maxU, var12, swap);
            
            diagMove = (diag - (diag/origY * i)) + (1*diagNeg - (diagNeg - (diagNeg/origY * i)));
            
            addVertexWithUVSwap(tess, 0, var11, thickness + diagMove, maxU, var12, swap);
            addVertexWithUVSwap(tess, 1, var11, thickness + diagMove, minU, var12, swap);
        }

        tess.draw();
        tess.addTranslation(-x, -y, -z);
        tess.startDrawingQuads();
    }
	
	private static void addVertexWithUVSwap(Tessellator tess, double xOff, double yOff, double zOff, double u, double v, int swap) {
		/*
		 * 0:	x	y	z
		 * 1:	x	z	y
		 * 
		 * 2:	y	x	z
		 * 3:	y	z	x
		 * 
		 * 4:	z	x	y
		 * 5:	z	y	x
		 */
		
		switch (swap) {
		case 1: tess.addVertexWithUV(xOff, zOff, yOff, u, v);
			return;
		case 2: tess.addVertexWithUV(yOff, xOff, zOff, u, v);
			return;
		case 3: tess.addVertexWithUV(yOff, zOff, xOff, u, v);
			return;
		case 4: tess.addVertexWithUV(zOff, xOff, yOff, u, v);
			return;
		case 5: tess.addVertexWithUV(zOff, yOff, xOff, u, v);
			return;
		}
		
		tess.addVertexWithUV(xOff, yOff, zOff, u, v);
	}
}
