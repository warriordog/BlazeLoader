package net.acomputerdog.BlazeLoader.api.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import manilla.util.ManillaUtil;
import net.acomputerdog.BlazeLoader.api.block.ApiBlock;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class APIRenderBlocks {
	
	private static final BLRenderBlocks renderer = BLRenderBlocks.instance;
	
	private static List<Block> GrassTypeRenderBlocks = new ArrayList();
		
	public static boolean registerGrassRender(Block block) {
		if (!GrassTypeRenderBlocks.contains(block)) {
			if (IsValidGrassBlock(block)) {
				GrassTypeRenderBlocks.add(block);
				return true;
			} else {
				throwException(block.getClass().getCanonicalName() + " Does not implement IGrassBlock", new IllegalArgumentException(block.getClass().getCanonicalName()));
			}
			return false;
		}
		throwException("block register duplication when adding " + block.getUnlocalizedName() + " : " + ApiBlock.getBlockName(block) + " to grass register", new IllegalArgumentException(block.getClass().getCanonicalName()));
		return false;
	}
	
	public static boolean unregisterGrassRender(Block block) {
		if (GrassTypeRenderBlocks.contains(block)) {
			GrassTypeRenderBlocks.remove(block);
			return true;
		}
		return false;
	}
		
	public static IIcon getIconSideOverlay(RenderBlocks rb, Block block, int x, int y, int z, int side) {
		return getIconSideOverlay(rb, block, rb.blockAccess.getBlockMetadata(x, y, z), x, y, z, side);
	}
	
	public static IIcon getIconSideOverlay(RenderBlocks rb, Block block, int metadata, int x, int y, int z, int side) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).getIconSideOverlay(rb.blockAccess, metadata, x, y, z, side);
		}
		return block.getBlockTextureFromSide(3);
	}
	
	public static boolean getRenderGrass(RenderBlocks rb, Block block, int x, int y, int z) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).IsGrassBlock(rb.blockAccess.getBlockMetadata(x, y, z));
		}
		return false;
	}
	
	public static boolean getHasSnow(RenderBlocks rb, Block block, int x, int y, int z) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).HasSnow(rb.blockAccess, x, y, z);
		}
		return false;
	}
	
	private static boolean IsValidGrassBlock(Block block) {
		for (Class i : block.getClass().getInterfaces()) {
			if (IGrassBlock.class.isAssignableFrom(i)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean HasSpecialRender(Block block) {
		for (Class i : block.getClass().getInterfaces()) {
			if (IRenderSpecial.class.isAssignableFrom(i)) return true;
		}
		return false;
	}
	
	public static boolean getRenderGrass(Block block, int Metadata, boolean inv) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).IsGrassBlockInv(Metadata);
		}
		return false;
	}
	
	public static int getColorMultiplier(Block block, IBlockAccess access, int x, int y, int z) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).colorMultiplier2(access, x, y, z);
		}
		return block.colorMultiplier(access, x, y, z);
	}
	
	public static int getRenderColor(Block block, int metadata) {
		if (GrassTypeRenderBlocks.contains(block)) {
			return ((IGrassBlock)block).getRenderColor2(metadata);
		}
		return block.getRenderColor(metadata);
	}
	
	/**
	 * Temporary
	 * @param message
	 * @param innerException
	 */
    private static void throwException(String message, Throwable innerException) {
    	if (!ManillaUtil.IsServer()) {
    		Minecraft m = Minecraft.getMinecraft();
            if (m != null) {
            	m.displayCrashReport(CrashReport.makeCrashReport(innerException, message));
            	return;
            }
    	} else {
			ManillaUtil.getLogAgent().logWarningException(message, innerException);
			return;
    	}
        throw new RuntimeException(innerException);
    }
}
