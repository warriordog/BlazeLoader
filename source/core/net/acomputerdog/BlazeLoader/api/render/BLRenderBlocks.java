package net.acomputerdog.BlazeLoader.api.render;

import org.lwjgl.opengl.GL11;

import net.acomputerdog.BlazeLoader.api.util.MCColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Wrapper around vanilla RenderBlocks providing additional functionality
 * and managed access to vanilla rendering methods
 * @author Sollace
 *
 */
public class BLRenderBlocks {
	
	/**
	 * Vanilla RenderBlocks instance
	 * Warning: Use with caution
	 */
	public final RenderBlocks renderBlocks;
	
	public BLRenderBlocks(RenderBlocks rb) { 
		renderBlocks = rb;
	}
	
	public void setOverrideBlockTexture(IIcon override) {
		renderBlocks.setOverrideBlockTexture(override);
	}
	
	public boolean hasOverrideBlockTexture() {
		return renderBlocks.hasOverrideBlockTexture();
	}
	
	public void clearOverrideBlockTexture() {
		renderBlocks.clearOverrideBlockTexture();
	}
	
	public void setRenderBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderBlocks.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public void setRenderBoundsFromBlock(Block block) {
		renderBlocks.setRenderBoundsFromBlock(block);
	}
	
	public void setRenderFromInside(boolean val) {
		renderBlocks.setRenderFromInside(val);
	}
	
	public void setFlipTexture(boolean val) {
		renderBlocks.flipTexture = val;
	}
	
	public void overrideBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderBlocks.overrideBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public Boolean getFlipTexture() {
		return renderBlocks.flipTexture;
	}
	
	public IBlockAccess getBlockAccess() {
		return renderBlocks.blockAccess;
	}
	
	public int getAoBrightness(int a, int b, int c, int d) {
		return renderBlocks.getAoBrightness(a, b, c, d);
	}
	
	public int mixAoBrightness(int a, int b, int c, int d, double e, double f, double g, double h) {
		return renderBlocks.mixAoBrightness(a, b, c, d, e, f, g, h);
	}
	
	public IIcon getBlockIcon(Block block) {
		return renderBlocks.getBlockIcon(block);
	}
	
	public IIcon getBlockIcon(Block block, IBlockAccess access, int x, int y, int z, int side) {
		return renderBlocks.getBlockIcon(block, access, x, y, z, side);
	}
	
	public IIcon getBlockIconFromSideAndMetadata(Block block, int side, int metadata) {
		return renderBlocks.getBlockIconFromSideAndMetadata(block, side, metadata);
	}
	
	public IIcon getBlockIconFromSide(Block block, int metadata) {
		return renderBlocks.getBlockIconFromSide(block, metadata);
	}

	
	
	
	
	
	
	
	/**
	 * Renders a block in the minecraft world
	 * @return true if blocks have been rendered
	 */
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        int var5 = APIRenderBlocks.getWorldRenderColor(block, renderBlocks.blockAccess, x, y, z);
        
        float redComp = (float)(var5 >> 16 & 255) / 255.0F,
        	greenComp = (float)(var5 >> 8 & 255) / 255.0F,
        	blueComp = (float)(var5 & 255) / 255.0F;
        
        if (EntityRenderer.anaglyphEnable) {
        	redComp = (redComp * 30.0F + greenComp * 59.0F + blueComp * 11.0F) / 100.0F;
        	greenComp = (redComp * 30.0F + greenComp * 70.0F) / 100.0F;
        	blueComp = (redComp * 30.0F + blueComp * 70.0F) / 100.0F;
        }
        
        if (Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0) {
        	if (renderBlocks.partialRenderBounds) {
        		return renderStandardBlockWithAmbientOcclusionPartial(block, x, y, z, redComp, greenComp, blueComp);
        	}
    		return renderStandardBlockWithAmbientOcclusion(block, x, y, z, redComp, greenComp, blueComp);
        }
    	return renderStandardBlockWithColorMultiplier(block, x, y, z, redComp, greenComp, blueComp);
    }
	
    /**
     * Renders a block in the inventory
     */
	public void renderStandardBlockAsItem(Block block, int metadata, float mult) {
		Tessellator tess = Tessellator.instance;
		boolean over = APIRenderBlocks.getRenderGrassInv(block, metadata);
		boolean renderGrass = over || block == Blocks.grass;//APIRenderBlocks.getRenderGrassInv(block, metadata);
		
		int renderColor = block.getRenderColor(metadata);
		
        if (renderBlocks.useInventoryTint) {
        	renderColor = renderGrass ? 16777215 : APIRenderBlocks.getInventoryRenderColor(block, metadata);
        	setColorTint(renderColor, mult);
        }
        
		if (block.getRenderType() == 16) metadata = 1;

        block.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        drawItemSide(tess, 0F, -1F, 0F, block, metadata, 0);
        
        if ((renderGrass && renderBlocks.useInventoryTint) || over) {
    		renderColor = APIRenderBlocks.getInventoryRenderColor(block, metadata);
        	
    		setColorTint(renderColor, mult);
        	
        	if (over) {
        		drawItemSideUniversal(tess, 0F, -1F, 0F, block, metadata, 0, true);
        		
        		if (!(renderGrass && renderBlocks.useInventoryTint)) {
        			resetColorTint(mult);
        		}
        	}
        }
        
        drawItemSide(tess, 0F, 1F, 0F, block, metadata, 1);
        
        if (over) drawItemSideOverlay(tess, 0F, 1F, 0F, block, metadata, 1, mult, renderColor);
        
        if (renderGrass && renderBlocks.useInventoryTint) resetColorTint(mult);
        
        drawItemSide(tess, 0F, 0F, -1F, block, metadata, 2);
        drawItemSide(tess, 0F, 0F, 1F, block, metadata, 3);
        drawItemSide(tess, -1F, 0F, 0F, block, metadata, 4);
        drawItemSide(tess, 1F, 0F, 0F, block, metadata, 5);
        
        if (over) {
        	setColorTint(renderColor, mult);
        	drawItemSideUniversal(tess, 0F, 0F, -1F, block, metadata, 2, true);
            drawItemSideUniversal(tess, 0F, 0F, 1F, block, metadata, 3, true);
            drawItemSideUniversal(tess, -1F, 0F, 0F, block, metadata, 4, true);
            drawItemSideUniversal(tess, 1F, 0F, 0F, block, metadata, 5, true);
            resetColorTint(mult);
        }
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	private boolean renderStandardBlockWithAmbientOcclusionPartial(Block block, int x, int y, int z, float multR, float multG, float multB) {
		renderBlocks.enableAO = true;
        
        float brightTopLeft, brightBottomLeft, brightBottomRight, brightTopRight,
				aoLight;
        
        boolean result = false,
        		renderGrass = APIRenderBlocks.getRenderGrass(this, block, x, y, z),
        		renderWithColor = true,
        		canGrassX, canGrassZPos, canGrassZNeg;
        
        int originalBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z),
        		usedBrightness;
        
        Tessellator.instance.setBrightness(983055);
        
        if (renderBlocks.getBlockIcon(block).getIconName().equals("grass_top")) {
            renderWithColor = false;
        } else if (renderBlocks.hasOverrideBlockTexture()) {
            renderWithColor = false;
        }
        
        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, 0)) {
            if (renderBlocks.renderMinY <= 0.0D) --y;

            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessYZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessYZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXYNN;
            } else {
            	renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXYNN;
            } else {
            	renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXYPN;
            } else {
            	renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXYPN;
            } else {
            	renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z + 1);
            }

            if (renderBlocks.renderMinY <= 0.0D) ++y;

            if (renderBlocks.renderMinY <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            brightTopLeft = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + aoLight) / 4.0F;
            brightTopRight = (renderBlocks.aoLightValueScratchYZNP + aoLight + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
            brightBottomRight = (aoLight + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
            brightBottomLeft = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + aoLight + renderBlocks.aoLightValueScratchYZNN) / 4.0F;
            
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, usedBrightness);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNN, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessYZNN, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.5F, multG * 0.5F, multB * 0.5F);
            } else {
            	resetColors(0.5F, 0.5F, 0.5F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 0), 0);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 0);
            
            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, 1)) {
            if (renderBlocks.renderMaxY >= 1.0D) ++y;

            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessXYPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            renderBlocks.aoBrightnessYZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessYZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXYNP;
            } else {
            	renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXYNP;
            } else {
            	renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXYPP;
            	renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXYPP;
            } else {
            	renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z - 1);
            }
            
            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXYPP;
            	renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXYPP;
            } else {
            	renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z + 1);
            }

            if (renderBlocks.renderMaxY >= 1.0D) --y;

            if (renderBlocks.renderMaxY >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            brightTopRight = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + aoLight) / 4.0F;
            brightTopLeft = (renderBlocks.aoLightValueScratchYZPP + aoLight + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            brightBottomLeft = (aoLight + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + aoLight + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, usedBrightness);
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXYPP, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPN, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, usedBrightness);
            
            if (!renderGrass) {
            	resetColors(multR, multG, multB);
            } else {
            	resetColors(1F, 1F, 1F);
            }
            
            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 1), 1);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 1);
            
            result = true;
        }

        float var23, var22, var25, var24;
        int var27, var26, var29, var28;

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, 2)) {
            if (renderBlocks.renderMinZ <= 0.0D) --z;

            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXZNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessYZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessYZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            renderBlocks.aoBrightnessXZPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            } else {
            	renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
            	renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            } else {
            	renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            } else {
            	renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
            	renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            } else {
            	renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y + 1, z);
            }

            if (renderBlocks.renderMinZ <= 0.0D) ++z;

            if (renderBlocks.renderMinZ <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            
            var23 = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + aoLight + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            var22 = (aoLight + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            var25 = (renderBlocks.aoLightValueScratchYZNN + aoLight + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
            var24 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX) + (double)var22 * renderBlocks.renderMinY * renderBlocks.renderMinX + (double)var25 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            brightBottomLeft = (float)((double)var23 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX) + (double)var22 * renderBlocks.renderMaxY * renderBlocks.renderMaxX + (double)var25 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));
            brightBottomRight = (float)((double)var23 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX) + (double)var22 * renderBlocks.renderMinY * renderBlocks.renderMaxX + (double)var25 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            brightTopRight = (float)((double)var23 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX) + (double)var22 * renderBlocks.renderMinY * renderBlocks.renderMinX + (double)var25 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));
            
            var27 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, usedBrightness);
            var26 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, usedBrightness);
            var29 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXZPN, usedBrightness);
            var28 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, usedBrightness);
            
            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(var27, var26, var29, var28, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMaxY * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(var27, var26, var29, var28, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMaxY * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(var27, var26, var29, var28, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMinY * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(var27, var26, var29, var28, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMinY * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            } else {
            	resetColors(0.8F, 0.8F, 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 2), 2);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 2);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, 3)) {
            if (renderBlocks.renderMaxZ >= 1.0D) ++z;

            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXZNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessXZPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            renderBlocks.aoBrightnessYZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessYZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y + 1, z);
            }

            if (renderBlocks.renderMaxZ >= 1.0D) --z;

            if (renderBlocks.renderMaxZ >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            var23 = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + aoLight + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
            var22 = (aoLight + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            var25 = (renderBlocks.aoLightValueScratchYZNP + aoLight + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            var24 = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX) + (double)var22 * renderBlocks.renderMaxY * renderBlocks.renderMinX + (double)var25 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            brightBottomLeft = (float)((double)var23 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX) + (double)var22 * renderBlocks.renderMinY * renderBlocks.renderMinX + (double)var25 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));
            brightBottomRight = (float)((double)var23 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX) + (double)var22 * renderBlocks.renderMinY * renderBlocks.renderMaxX + (double)var25 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            brightTopRight = (float)((double)var23 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX) + (double)var22 * renderBlocks.renderMaxY * renderBlocks.renderMaxX + (double)var25 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));
            
            var27 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessYZPP, usedBrightness);
            var26 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYZPPP, usedBrightness);
            var29 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, usedBrightness);
            var28 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZNP, usedBrightness);
            
            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(var27, var28, var29, var26, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX, renderBlocks.renderMaxY * renderBlocks.renderMinX);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(var27, var28, var29, var26, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX, renderBlocks.renderMinY * renderBlocks.renderMinX);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(var27, var28, var29, var26, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX, renderBlocks.renderMinY * renderBlocks.renderMaxX);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(var27, var28, var29, var26, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX, renderBlocks.renderMaxY * renderBlocks.renderMaxX);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            } else  {
            	resetColors(0.8F, 0.8F, 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 3), 3);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 3);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, 4)) {
            if (renderBlocks.renderMinX <= 0.0D) --x;

            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessXZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessXZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z + 1);
            }

            if (renderBlocks.renderMinX <= 0.0D) ++x;
            
            if (renderBlocks.renderMinX <= 0.0D || !renderBlocks.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            
            var23 = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + aoLight + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
            var22 = (aoLight + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;
            var25 = (renderBlocks.aoLightValueScratchXZNN + aoLight + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
            var24 = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + aoLight) / 4.0F;
            
            brightTopLeft = (float)((double)var22 * renderBlocks.renderMaxY * renderBlocks.renderMaxZ + (double)var25 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ) + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ) + (double)var23 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ);
            brightBottomLeft = (float)((double)var22 * renderBlocks.renderMaxY * renderBlocks.renderMinZ + (double)var25 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ) + (double)var24 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ) + (double)var23 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ);
            brightBottomRight = (float)((double)var22 * renderBlocks.renderMinY * renderBlocks.renderMinZ + (double)var25 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ) + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ) + (double)var23 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ);
            brightTopRight = (float)((double)var22 * renderBlocks.renderMinY * renderBlocks.renderMaxZ + (double)var25 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ) + (double)var24 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ) + (double)var23 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ);
            
            var27 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, usedBrightness);
            var26 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPP, usedBrightness);
            var29 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, usedBrightness);
            var28 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, usedBrightness);
            
            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(var26, var29, var28, var27, renderBlocks.renderMaxY * renderBlocks.renderMaxZ, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(var26, var29, var28, var27, renderBlocks.renderMaxY * renderBlocks.renderMinZ, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(var26, var29, var28, var27, renderBlocks.renderMinY * renderBlocks.renderMinZ, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(var26, var29, var28, var27, renderBlocks.renderMinY * renderBlocks.renderMaxZ, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            } else  {
            	resetColors(0.6F, 0.6F, 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 4), 4);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 4);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, 5)) {
            if (renderBlocks.renderMaxX >= 1.0D) ++x;

            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessXZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessXZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z + 1);
            }

            if (renderBlocks.renderMaxX >= 1.0D) --x;

            if (renderBlocks.renderMaxX >= 1.0D || !renderBlocks.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            var23 = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + aoLight + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            var22 = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + aoLight) / 4.0F;
            var25 = (renderBlocks.aoLightValueScratchXZPN + aoLight + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            var24 = (aoLight + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            
            brightTopLeft = (float)((double)var23 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ + (double)var22 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ) + (double)var25 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ) + (double)var24 * renderBlocks.renderMinY * renderBlocks.renderMaxZ);
            brightBottomLeft = (float)((double)var23 * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ + (double)var22 * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ) + (double)var25 * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ) + (double)var24 * renderBlocks.renderMinY * renderBlocks.renderMinZ);
            brightBottomRight = (float)((double)var23 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ + (double)var22 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ) + (double)var25 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ) + (double)var24 * renderBlocks.renderMaxY * renderBlocks.renderMinZ);
            brightTopRight = (float)((double)var23 * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ + (double)var22 * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ) + (double)var25 * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ) + (double)var24 * renderBlocks.renderMaxY * renderBlocks.renderMaxZ);
            
            var27 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, usedBrightness);
            var26 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPP, usedBrightness);
            var29 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXYPP, usedBrightness);
            var28 = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, usedBrightness);
            
            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(var27, var28, var29, var26, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMinY * renderBlocks.renderMaxZ);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(var27, var28, var29, var26, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMinY * renderBlocks.renderMinZ);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(var27, var28, var29, var26, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMaxY * renderBlocks.renderMinZ);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(var27, var28, var29, var26, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMaxY * renderBlocks.renderMaxZ);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            } else {
            	resetColors(0.6F, 0.6F, 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 5), 5);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 5);

            result = true;
        }

        renderBlocks.enableAO = false;
        return result;
    }
	
	private boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float multR, float multG, float multB) {
        renderBlocks.enableAO = true;
        
        float brightTopLeft, brightBottomLeft, brightBottomRight, brightTopRight,
        	aoLight;
        
        boolean result = false,
        		renderGrass = APIRenderBlocks.getRenderGrass(this, block, x, y, z),
        		renderWithColor = true,
        		canGrassX, canGrassZPos, canGrassZNeg;
        
        int originalBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z),
        		usedBrightness;
        
        Tessellator.instance.setBrightness(983055);
        
        if (renderBlocks.getBlockIcon(block).getIconName().equals("grass_top")) {
            renderWithColor = false;
        } else if (renderBlocks.hasOverrideBlockTexture()) {
            renderWithColor = false;
        }
        
        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y - 1, z, 0)) {
            if (renderBlocks.renderMinY <= 0.0D) --y;

            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessYZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessYZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXYNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXYNN;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXYNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXYPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXYPN;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXYPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z + 1);
            }

            if (renderBlocks.renderMinY <= 0.0D) ++y;

            if (renderBlocks.renderMinY <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y - 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            brightTopLeft = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + aoLight) / 4.0F;
            brightTopRight = (renderBlocks.aoLightValueScratchYZNP + aoLight + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
            brightBottomRight = (aoLight + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
            brightBottomLeft = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + aoLight + renderBlocks.aoLightValueScratchYZNN) / 4.0F;
            
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, usedBrightness);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNN, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessYZNN, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.5F, multG * 0.5F, multB * 0.5F);
            } else {
            	resetColors(0.5F, 0.5F, 0.5F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 0), 0);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 0);
            
            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y + 1, z, 1)) {
            if (renderBlocks.renderMaxY >= 1.0D) ++y;

            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessXYPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            renderBlocks.aoBrightnessYZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessYZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXYNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXYNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXYNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();
            
            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXYPP;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXYPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXYPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXYPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z + 1);
            }

            if (renderBlocks.renderMaxY >= 1.0D) --y;

            if (renderBlocks.renderMaxY >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y + 1, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            brightTopRight = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + aoLight) / 4.0F;
            brightTopLeft = (renderBlocks.aoLightValueScratchYZPP + aoLight + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            brightBottomLeft = (aoLight + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + aoLight + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, usedBrightness);
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXYPP, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPN, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, usedBrightness);
            
            if (!renderGrass) {
            	resetColors(multR, multG, multB);
            } else {
            	resetColors(1F, 1F, 1F);
            }
            
            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 1), 1);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 1);
            
            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z - 1, 2)) {
            if (renderBlocks.renderMinZ <= 0.0D) --z;
            
            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPN = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXZNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessYZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessYZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            renderBlocks.aoBrightnessXZPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y + 1, z);
            }

            if (renderBlocks.renderMinZ <= 0.0D) ++z;

            if (renderBlocks.renderMinZ <= 0.0D || !renderBlocks.blockAccess.getBlock(x, y, z - 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            brightTopLeft = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + aoLight + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            brightBottomLeft = (aoLight + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchYZNN + aoLight + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
            brightTopRight = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + aoLight) / 4.0F;
            
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXZPN, usedBrightness);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            } else {
            	resetColors(0.8F, 0.8F, 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 2), 2);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 2);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x, y, z + 1, 3)) {
            if (renderBlocks.renderMaxZ >= 1.0D) ++z;

            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZNP = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchYZPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXZNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            renderBlocks.aoBrightnessXZPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            renderBlocks.aoBrightnessYZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessYZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y + 1, z);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y - 1, z);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y + 1, z);
            }

            if (renderBlocks.renderMaxZ >= 1.0D) --z;

            if (renderBlocks.renderMaxZ >= 1.0D || !renderBlocks.blockAccess.getBlock(x, y, z + 1).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            brightTopLeft = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + aoLight + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
            brightTopRight = (aoLight + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchYZNP + aoLight + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            brightBottomLeft = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + aoLight) / 4.0F;
            
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessYZPP, usedBrightness);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYZPPP, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZNP, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.8F, multG * 0.8F, multB * 0.8F);
            } else {
            	resetColors(0.8F, 0.8F, 0.8F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 3), 3);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 3);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x - 1, y, z, 4)) {
            if (renderBlocks.renderMinX <= 0.0D) --x;

            renderBlocks.aoLightValueScratchXYNN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZNP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYNP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessXZNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessXZNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x - 1, y, z - 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x - 1, y, z + 1).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNNN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNNP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x - 1, y + 1, z).getCanBlockGrass();

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.aoLightValueScratchXZNN;
                renderBlocks.aoBrightnessXYZNPN = renderBlocks.aoBrightnessXZNN;
            } else {
                renderBlocks.aoLightValueScratchXYZNPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.aoLightValueScratchXZNP;
                renderBlocks.aoBrightnessXYZNPP = renderBlocks.aoBrightnessXZNP;
            } else {
                renderBlocks.aoLightValueScratchXYZNPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZNPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z + 1);
            }

            if (renderBlocks.renderMinX <= 0.0D) ++x;

            if (renderBlocks.renderMinX <= 0.0D || !renderBlocks.blockAccess.getBlock(x - 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x - 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x - 1, y, z).getAmbientOcclusionLightValue();
            brightTopRight = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + aoLight + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
            brightTopLeft = (aoLight + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;
            brightBottomLeft = (renderBlocks.aoLightValueScratchXZNN + aoLight + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + aoLight) / 4.0F;
            
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, usedBrightness);
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPP, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            } else {
            	resetColors(0.6F, 0.6F, 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 4), 4);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 4);

            result = true;
        }

        if (block.shouldSideBeRendered(renderBlocks.blockAccess, x + 1, y, z, 5)) {
            if (renderBlocks.renderMaxX >= 1.0D) ++x;

            renderBlocks.aoLightValueScratchXYPN = renderBlocks.blockAccess.getBlock(x, y - 1, z).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPN = renderBlocks.blockAccess.getBlock(x, y, z - 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXZPP = renderBlocks.blockAccess.getBlock(x, y, z + 1).getAmbientOcclusionLightValue();
            renderBlocks.aoLightValueScratchXYPP = renderBlocks.blockAccess.getBlock(x, y + 1, z).getAmbientOcclusionLightValue();
            
            renderBlocks.aoBrightnessXYPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z);
            renderBlocks.aoBrightnessXZPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z - 1);
            renderBlocks.aoBrightnessXZPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z + 1);
            renderBlocks.aoBrightnessXYPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z);
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y - 1, z).getCanBlockGrass();
            
            canGrassZPos = renderBlocks.blockAccess.getBlock(x + 1, y, z + 1).getCanBlockGrass();
            canGrassZNeg = renderBlocks.blockAccess.getBlock(x + 1, y, z - 1).getCanBlockGrass();

            if (!canGrassZNeg && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPNN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPNN = renderBlocks.blockAccess.getBlock(x, y - 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNN = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z - 1);
            }

            if (!canGrassZPos && !canGrassX) {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPNP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPNP = renderBlocks.blockAccess.getBlock(x, y - 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPNP = block.getBlockBrightness(renderBlocks.blockAccess, x, y - 1, z + 1);
            }
            
            canGrassX = renderBlocks.blockAccess.getBlock(x + 1, y + 1, z).getCanBlockGrass();

            if (!canGrassX && !canGrassZNeg) {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.aoLightValueScratchXZPN;
                renderBlocks.aoBrightnessXYZPPN = renderBlocks.aoBrightnessXZPN;
            } else {
                renderBlocks.aoLightValueScratchXYZPPN = renderBlocks.blockAccess.getBlock(x, y + 1, z - 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPN = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z - 1);
            }

            if (!canGrassX && !canGrassZPos) {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.aoLightValueScratchXZPP;
                renderBlocks.aoBrightnessXYZPPP = renderBlocks.aoBrightnessXZPP;
            } else {
                renderBlocks.aoLightValueScratchXYZPPP = renderBlocks.blockAccess.getBlock(x, y + 1, z + 1).getAmbientOcclusionLightValue();
                renderBlocks.aoBrightnessXYZPPP = block.getBlockBrightness(renderBlocks.blockAccess, x, y + 1, z + 1);
            }

            if (renderBlocks.renderMaxX >= 1.0D) --x;

            if (renderBlocks.renderMaxX >= 1.0D || !renderBlocks.blockAccess.getBlock(x + 1, y, z).isOpaqueCube()) {
                usedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x + 1, y, z);
            } else {
            	usedBrightness = originalBrightness;
            }

            aoLight = renderBlocks.blockAccess.getBlock(x + 1, y, z).getAmbientOcclusionLightValue();
            brightTopLeft = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + aoLight + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            brightBottomLeft = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + aoLight) / 4.0F;
            brightBottomRight = (renderBlocks.aoLightValueScratchXZPN + aoLight + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            brightTopRight = (aoLight + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            
            renderBlocks.brightnessTopLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, usedBrightness);
            renderBlocks.brightnessTopRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPP, usedBrightness);
            renderBlocks.brightnessBottomRight = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXYPP, usedBrightness);
            renderBlocks.brightnessBottomLeft = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, usedBrightness);

            if (renderWithColor && !renderGrass) {
            	resetColors(multR * 0.6F, multG * 0.6F, multB * 0.6F);
            } else {
            	resetColors(0.6F, 0.6F, 0.6F);
            }

            multColorCorners(brightBottomLeft, brightBottomRight, brightTopLeft, brightTopRight);
            RenderFaceforSide(block, (double)x, (double)y, (double)z, renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, 5), 5);
            
            renderIconSideOverlay(renderGrass, block, multR, multG, multB, x, y, z, 5);

            result = true;
        }

        renderBlocks.enableAO = false;
        return result;
    }
	
    private boolean renderStandardBlockWithColorMultiplier(Block block, int x, int y, int z, float multR, float multG, float multB) {
        renderBlocks.enableAO = false;
        boolean renderGrass = APIRenderBlocks.getRenderGrass(this, block, x, y, z) || block == Blocks.grass;
        
        float[][] colors = new float[4][3];
        float[] mult = MCColor.newRGBarray(multR, multG, multB);

        {
	        if (!APIRenderBlocks.getRenderGrassInv(block, renderBlocks.blockAccess.getBlockMetadata(x, y, z))) {
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
        int mixedBrightness = block.getBlockBrightness(renderBlocks.blockAccess, x, y, z);
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
    	if (par1Block.shouldSideBeRendered(renderBlocks.blockAccess, x + xOff, y + yOff, z + zOff, side)) {
            tessellator.setBrightness(checkBoundsForSide(side) ? mixedBrightness : par1Block.getBlockBrightness(renderBlocks.blockAccess, x + xOff, y + yOff, z + zOff));
            tessellator.setColorOpaque_F(rgb[0], rgb[1], rgb[2]);
            IIcon texture = renderBlocks.getBlockIcon(par1Block, renderBlocks.blockAccess, x, y, z, side);
            
            RenderFaceforSide(par1Block, x, y, z, texture, side);
            
            if (!renderBlocks.hasOverrideBlockTexture()) {
	            if (renderBlocks.fancyGrass && side > 1 && texture != null && texture.getIconName().equals("grass_side")) {
	                tessellator.setColorOpaque_F(rgb[0] * rgbMult[0], rgb[1] * rgbMult[1], rgb[2] * rgbMult[2]);
	                RenderFaceforSide(par1Block, x, y, z, BlockGrass.func_149990_e(), side);
	                return true;
            	}
	            
	            if (renderGrass && !APIRenderBlocks.getHasSnow(this, par1Block, x, y, z)) {
	            	if ((texture = APIRenderBlocks.getIconSideOverlay(this, par1Block, x, y, z, side)) != null) {
		                tessellator.setColorOpaque_F(rgb[0] * rgbMult[0], rgb[1] * rgbMult[1], rgb[2] * rgbMult[2]);
		                RenderFaceforSide(par1Block, x, y, z, texture, side);
	            	}
	            }
            }
            
            return true;
        }
    	return false;
    }
    
    private boolean checkBoundsForSide(int side) {
    	switch (side) {
    	case 0: return renderBlocks.renderMinY > 0.0D;
    	case 1: return renderBlocks.renderMaxY < 1.0D;
    	case 2: return renderBlocks.renderMinZ > 0.0D;
    	case 3: return renderBlocks.renderMaxZ < 1.0D;
    	case 4: return renderBlocks.renderMinX > 0.0D;
    	case 5: return renderBlocks.renderMaxX < 1.0D;
    	}
    	return false;
    }
        
    /**
     * Renders a face of a block in the minecraft world
     */
    public void RenderFaceforSide(Block block, double x, double y, double z, IIcon icon, int side) {
    	switch(side) {
	    	case 0: renderBlocks.renderFaceYNeg(block, x, y, z, icon); //bottom
				return;
	    	case 1: renderBlocks.renderFaceYPos(block, x, y, z, icon); //top
				return;
	    	case 2: renderBlocks.renderFaceZNeg(block, x, y, z, icon); //east
				return;
	    	case 3: renderBlocks.renderFaceZPos(block, x, y, z, icon); //west
				return;
	    	case 4: renderBlocks.renderFaceXNeg(block, x, y, z, icon); //north
				return;
	    	case 5: renderBlocks.renderFaceXPos(block, x, y, z, icon); //south
	    		return;
    	}
    }
    
    /**
     * As the name suggests.
     * Used by tallgrass 
     */
    public void drawCrossedSquares(IIcon icon, double x, double y, double z, float height) {
    	renderBlocks.drawCrossedSquares(icon, x, y, z, height);
    }
    
    /**
     * Draws all the sides of a block in the inventory
     */
    public void drawStandardItemSides(Tessellator tess, Block block, int metadata) {
    	drawItemSide(tess, 0F, -1F, 0F, block, metadata, 0);
        drawItemSide(tess, 0F, 1F, 0F, block, metadata, 1);
        drawItemSide(tess, 0F, 0F, -1F, block, metadata, 2);
        drawItemSide(tess, 0F, 0F, 1F, block, metadata, 3);
        drawItemSide(tess, -1F, 0F, 0F, block, metadata, 4);
        drawItemSide(tess, 1F, 0F, 0F, block, metadata, 5);
    }
    
    /**
     * Draws the overlay texture on a block in the inventory
     */
    public void drawStandardItemSidesOverlay(Tessellator tess, Block block, int metadata, float baseColor) {
    	setColorTint(APIRenderBlocks.getInventoryRenderColor(block, metadata), baseColor);
        drawItemSideUniversal(tess, 0F, -1F, 0F, block, metadata, 0, true);
        drawItemSideUniversal(tess, 0F, 1F, 0F, block, metadata, 1, true);
        drawItemSideUniversal(tess, 0F, 0F, -1F, block, metadata, 2, true);
        drawItemSideUniversal(tess, 0F, 0F, 1F, block, metadata, 3, true);
        drawItemSideUniversal(tess, -1F, 0F, 0F, block, metadata, 4, true);
        drawItemSideUniversal(tess, 1F, 0F, 0F, block, metadata, 5, true);
        resetColorTint(baseColor);
    }
    
    /**
     * Draws one side of a block in the inventory
     */
    public void drawItemSide(Tessellator tess, float a, float b, float c, Block block, int metadata, int side) {
    	drawItemSideUniversal(tess, a, b, c, block, metadata, side, false);
    }
    
    /**
     * Renders the overlay texture on one side of a block in the minecraft world
     */
	public void renderIconSideOverlay(boolean renderGrass, Block block, float multR, float multG, float multB, int x, int y, int z, int side) {
		if (!renderBlocks.hasOverrideBlockTexture()) {
			if (side > 1 && renderBlocks.fancyGrass) {
				IIcon var22 = renderBlocks.getBlockIcon(block, renderBlocks.blockAccess, x, y, z, side);
				
		        if (var22.getIconName().equals("grass_side")) {
		        	multColors(multR, multG, multB);
		            RenderFaceforSide(block, (double)x, (double)y, (double)z, BlockGrass.func_149990_e(), side);
		            return;
		        }
			}
			
			if (renderGrass && !APIRenderBlocks.getHasSnow(this, block, x, y, z)) {
	        	IIcon over = APIRenderBlocks.getIconSideOverlay(this, block, x, y, z, side);
	        	
	        	if (over != null) {
	        		multColors(multR, multG, multB);
	                RenderFaceforSide(block, (double)x, (double)y, (double)z, over, side);
	        	}
	        }
		}
	}
    
	/**
	 * Draws the overlay texture on one side of a block in the inventory
	 */
    public void drawItemSideOverlay(Tessellator tess, float a, float b, float c, Block block, int data, int side, float baseColor, int tintColor) {
    	setColorTint(tintColor, baseColor);
    	drawItemSideUniversal(tess, a, b, c, block, data, side, true);
    	resetColorTint(baseColor);
    }
    
    private void drawItemSideUniversal(Tessellator tess, float a, float b, float c, Block block, int data, int side, boolean inv) {
    	tess.startDrawingQuads();
        tess.setNormal(a, b, c);
        
        if (inv) {
        	IIcon over = APIRenderBlocks.getIconSideOverlay(this, block, data, 0, 0, 0, side);
        	if (over != null) {
        		RenderFaceforSide(block, 0, 0, 0, over, side);
        	}
        } else {
        	RenderFaceforSide(block, 0, 0, 0, block.getIcon(side, data), side);
        }
        tess.draw();
    }
    
    /**
     * Sets the render tint
     */
    public void setColorTint(int tintColor, float baseColor) {
        GL11.glColor4f(MCColor.r(tintColor) * baseColor, MCColor.g(tintColor) * baseColor, MCColor.b(tintColor) * baseColor, 1.0F);
    }
    
    public void resetColorTint(float original) {
    	GL11.glColor4f(original, original, original, 1.0F);
    }
	
    private void resetColors(float r, float g, float b) {
    	renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = r;
    	renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = g;
    	renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = b;
    }
    
    public void multColors(float r, float g, float b) {
    	renderBlocks.colorRedTopLeft *= r;
    	renderBlocks.colorRedBottomLeft *= r;
        renderBlocks.colorRedBottomRight *= r;
        renderBlocks.colorRedTopRight *= r;
        renderBlocks.colorGreenTopLeft *= g;
        renderBlocks.colorGreenBottomLeft *= g;
        renderBlocks.colorGreenBottomRight *= g;
        renderBlocks.colorGreenTopRight *= g;
        renderBlocks.colorBlueTopLeft *= b;
        renderBlocks.colorBlueBottomLeft *= b;
        renderBlocks.colorBlueBottomRight *= b;
        renderBlocks.colorBlueTopRight *= b;
    }
    
	public void multColorCorners(float BotLeft, float BotRight, float TopLeft, float TopRight) {
		renderBlocks.colorRedTopLeft *= TopLeft;
		renderBlocks.colorGreenTopLeft *= TopLeft;
		renderBlocks.colorBlueTopLeft *= TopLeft;
        
		renderBlocks.colorRedTopRight *= TopRight;
		renderBlocks.colorGreenTopRight *= TopRight;
		renderBlocks.colorBlueTopRight *= TopRight;
        
		renderBlocks.colorRedBottomLeft *= BotLeft;
		renderBlocks.colorGreenBottomLeft *= BotLeft;
		renderBlocks.colorBlueBottomLeft *= BotLeft;
        
		renderBlocks.colorRedBottomRight *= BotRight;
		renderBlocks.colorGreenBottomRight *= BotRight;
		renderBlocks.colorBlueBottomRight *= BotRight;
	}
    
	/**
	 * Renders a block as an item in the minecraft world with the thickness along the X-axis
	 */
	public void renderThickX(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 0, diag);
	}
	
	/**
	 * Renders a block as an item in the minecraft world with the thickness along the Y-axis
	 */
	public void renderThickY(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 1, diag);
	}
	
	/**
	 * Renders a block as an item in the minecraft world with the thickness along the Z-axis
	 */
	public void renderThickZ(Block block, float thickness, float x, float y, float z, int diag) {
		renderWithThickness(block, thickness, x, y, z, 5, diag);
	}
	
	private void renderWithThickness(Block block, float thickness, float x, float y, float z, int swap, int diagonal) {
		IIcon par2Icon = block.getIcon(0, renderBlocks.blockAccess.getBlockMetadata((int)x, (int)y, (int)z));
				
        float minU,maxU,minV,maxV;
        
        if (renderBlocks.uvRotateTop == 1) {
        	minU = par2Icon.getMaxU();
        	maxU = par2Icon.getMinU();
        	minV = par2Icon.getMinV();
        	maxV = par2Icon.getMaxV();
        } else if (renderBlocks.uvRotateTop == 2) {
	    	minU = par2Icon.getMinU();
	    	maxU = par2Icon.getMaxU();
	    	minV = par2Icon.getMaxV();
	    	maxV = par2Icon.getMinV();
        } else if (renderBlocks.uvRotateTop == 3) {
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
		tess.setBrightness(block.getBlockBrightness(renderBlocks.blockAccess, (int)x, (int)y, (int)z));
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
