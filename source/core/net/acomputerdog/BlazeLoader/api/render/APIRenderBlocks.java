package net.acomputerdog.BlazeLoader.api.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.acomputerdog.BlazeLoader.api.block.ApiBlock;
import net.acomputerdog.BlazeLoader.mod.ModLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Provides methods to access the IRenderSpecial and IGrassBlock methods
 * on any block object 
 * @author Sollace
 */
public class APIRenderBlocks {
	private static Map<Block, Boolean[]> BlockRenderMapping = new HashMap<Block, Boolean[]>();
	
	/**
	 * Returns true if the given block implements the IRenderSpecial interface
	 */
	public static boolean HasSpecialRender(Block block) {
		if (BlockRenderMapping.containsKey(block)) {
			Boolean result = getHasSpecialRender(block);
			if (result != null) {
				return result;
			}
		}
		for (Class i : block.getClass().getInterfaces()) {
			if (IRenderSpecial.class.isAssignableFrom(i)) return setHasSpecialRender(block, true);
		}
		return setHasSpecialRender(block, false);
	}
	
	/**
	 * Returns true if the given block implements the IGrassBlock interface
	 */
	public static boolean HasGrassRender(Block block) {
		if (BlockRenderMapping.containsKey(block)) {
			Boolean result = getHasGrassRender(block);
			if (result != null) {
				return result;
			}
		}
		for (Class i : block.getClass().getInterfaces()) {
			if (IGrassBlock.class.isAssignableFrom(i)) return setHasGrassRender(block, true);
		}
		return setHasGrassRender(block, false);
	}
	
	private static Boolean getHasGrassRender(Block block) {
		if (BlockRenderMapping.containsKey(block)) {
			return BlockRenderMapping.get(block)[0];
		}
		return null;
	}
	
	private static boolean setHasGrassRender(Block block, boolean val) {
		if (!BlockRenderMapping.containsKey(block)) {
			BlockRenderMapping.get(block)[0] = val;
		} else {
			BlockRenderMapping.put(block, new Boolean[] { val, null });
		}
		return val;
	}
	
	private static Boolean getHasSpecialRender(Block block) {
		if (BlockRenderMapping.containsKey(block)) {
			return BlockRenderMapping.get(block)[1];
		}
		return null;
	}
	
	private static boolean setHasSpecialRender(Block block, boolean val) {
		if (BlockRenderMapping.containsKey(block)) {
			BlockRenderMapping.get(block)[1] = val;
		} else {
			BlockRenderMapping.put(block, new Boolean[] { null, val });
		}
		return val;
	}
	
	/**
	 * Gets the overlay icon for a block
	 * @param rb	Instance of BLRenderBlocks doing the rendering
	 * @param block	The Block
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @param z		Z coordinate
	 * @param side	Side of block being rendered
	 * @return	IICon for the given side of the block at the given coordinates 
	 */
	public static IIcon getIconSideOverlay(BLRenderBlocks rb, Block block, int x, int y, int z, int side) {
		return getIconSideOverlay(rb, block, rb.getBlockAccess().getBlockMetadata(x, y, z), x, y, z, side);
	}
	
	/**
	 * Gets the overlay icon for a block
	 * @param rb	Instance of BLRenderBlocks doing the rendering
	 * @param block	The Block
	 * @param metadata	Metadata value of the given block
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @param z		Z coordinate
	 * @param side	Side of block being rendered
	 * @return	IICon for the given side of the block at the given coordinates 
	 */
	public static IIcon getIconSideOverlay(BLRenderBlocks rb, Block block, int metadata, int x, int y, int z, int side) {
		if (HasGrassRender(block)) {
			return ((IGrassBlock)block).getIconSideOverlay(rb.getBlockAccess(), metadata, x, y, z, side);
		}
		IIcon i = rb.getBlockIcon(block, rb.getBlockAccess(), x, y, z, side);
		if ("grass_side".equals(i.getIconName())) {
			return BlockGrass.func_149990_e();
		}
		return null;
	}
	
	/**
	 * Returns whether a given block must render with grass overlays
	 * @param rb	Instance of BLRenderBlocks doing the rendering
	 * @param block	The Block
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @param z		Z coordinate
	 * @return boolean whether this block must be rendered with overlays
	 */
	public static boolean getRenderGrass(BLRenderBlocks rb, Block block, int x, int y, int z) {
		return HasGrassRender(block) ? ((IGrassBlock)block).IsGrassBlock(rb.getBlockAccess().getBlockMetadata(x, y, z)) : false;
	}
	
	/**
	 * Returns whether a given block must render with snow
	 * @param rb	Instance of BLRenderBlocks doing the rendering
	 * @param block	The Block
	 * @param x		X coordinate
	 * @param y		Y coordinate
	 * @param z		Z coordinate
	 * @return boolean whether this block must be rendered with overlays
	 */
	public static boolean getHasSnow(BLRenderBlocks rb, Block block, int x, int y, int z) {
		return HasGrassRender(block) ? ((IGrassBlock)block).HasSnow(rb.getBlockAccess(), x, y, z) : false;
	}
	
	/**
	 * Returns whether a given block must render with grass overlays when in the inventory
	 * @param rb	Instance of RenderBlocks doing the rendering
	 * @param block	The Block
	 * @param metadat
	 * @return boolean whether this block must be rendered with overlays in the inventory
	 */
	public static boolean getRenderGrassInv(Block block, int Metadata) {
		return HasGrassRender(block) ? ((IGrassBlock)block).IsGrassBlockInv(Metadata) : false;
	}
	
	/**
	 * Gets the color tint applied to the block when rendering in the world
	 */
	public static int getWorldRenderColor(Block block, IBlockAccess access, int x, int y, int z) {
		return HasGrassRender(block) ? ((IGrassBlock)block).getWorldRenderColor(access, x, y, z) : block.colorMultiplier(access, x, y, z);
	}
	
	/**
	 * Gets the color tint applied to the block when rendering in the inventory
	 */
	public static int getInventoryRenderColor(Block block, int metadata) {
		return HasGrassRender(block) ? ((IGrassBlock)block).getInventoryRenderColor(metadata) : block.getRenderColor(metadata);
	}
}
