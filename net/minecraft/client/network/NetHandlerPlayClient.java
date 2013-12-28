package net.minecraft.client.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.*;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger field_147301_d = LogManager.getLogger();
    private final NetworkManager field_147302_e;
    private Minecraft field_147299_f;
    private WorldClient field_147300_g;
    private boolean field_147309_h;
    public MapStorage field_147305_a = new MapStorage((ISaveHandler)null);
    private Map field_147310_i = new HashMap();
    public List field_147303_b = new ArrayList();
    public int field_147304_c = 20;
    private GuiScreen field_147307_j;
    private boolean field_147308_k = false;
    private Random field_147306_l = new Random();
    private static final String __OBFID = "CL_00000878";

    public NetHandlerPlayClient(Minecraft p_i45061_1_, GuiScreen p_i45061_2_, NetworkManager p_i45061_3_)
    {
        this.field_147299_f = p_i45061_1_;
        this.field_147307_j = p_i45061_2_;
        this.field_147302_e = p_i45061_3_;
    }

    public void func_147296_c()
    {
        this.field_147300_g = null;
    }

    public void func_147233_a() {}

    public void func_147282_a(S01PacketJoinGame p_147282_1_)
    {
        this.field_147299_f.playerController = new PlayerControllerMP(this.field_147299_f, this);
        this.field_147300_g = new WorldClient(this, new WorldSettings(0L, p_147282_1_.func_149198_e(), false, p_147282_1_.func_149195_d(), p_147282_1_.func_149196_i()), p_147282_1_.func_149194_f(), p_147282_1_.func_149192_g(), this.field_147299_f.mcProfiler);
        this.field_147300_g.isRemote = true;
        this.field_147299_f.loadWorld(this.field_147300_g);
        this.field_147299_f.thePlayer.dimension = p_147282_1_.func_149194_f();
        this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
        this.field_147299_f.thePlayer.func_145769_d(p_147282_1_.func_149197_c());
        this.field_147304_c = p_147282_1_.func_149193_h();
        this.field_147299_f.playerController.setGameType(p_147282_1_.func_149198_e());
        this.field_147299_f.gameSettings.sendSettingsToServer();
        this.field_147302_e.func_150725_a(new C17PacketCustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
    }

    public void func_147235_a(S0EPacketSpawnObject p_147235_1_)
    {
        double var2 = (double)p_147235_1_.func_148997_d() / 32.0D;
        double var4 = (double)p_147235_1_.func_148998_e() / 32.0D;
        double var6 = (double)p_147235_1_.func_148994_f() / 32.0D;
        Object var8 = null;

        if (p_147235_1_.func_148993_l() == 10)
        {
            var8 = EntityMinecart.createMinecart(this.field_147300_g, var2, var4, var6, p_147235_1_.func_149009_m());
        }
        else if (p_147235_1_.func_148993_l() == 90)
        {
            Entity var9 = this.field_147300_g.getEntityByID(p_147235_1_.func_149009_m());

            if (var9 instanceof EntityPlayer)
            {
                var8 = new EntityFishHook(this.field_147300_g, var2, var4, var6, (EntityPlayer)var9);
            }

            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 60)
        {
            var8 = new EntityArrow(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 61)
        {
            var8 = new EntitySnowball(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 71)
        {
            var8 = new EntityItemFrame(this.field_147300_g, (int)var2, (int)var4, (int)var6, p_147235_1_.func_149009_m());
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 77)
        {
            var8 = new EntityLeashKnot(this.field_147300_g, (int)var2, (int)var4, (int)var6);
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 65)
        {
            var8 = new EntityEnderPearl(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 72)
        {
            var8 = new EntityEnderEye(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 76)
        {
            var8 = new EntityFireworkRocket(this.field_147300_g, var2, var4, var6, null);
        }
        else if (p_147235_1_.func_148993_l() == 63)
        {
            var8 = new EntityLargeFireball(this.field_147300_g, var2, var4, var6, (double)p_147235_1_.func_149010_g() / 8000.0D, (double)p_147235_1_.func_149004_h() / 8000.0D, (double)p_147235_1_.func_148999_i() / 8000.0D);
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 64)
        {
            var8 = new EntitySmallFireball(this.field_147300_g, var2, var4, var6, (double)p_147235_1_.func_149010_g() / 8000.0D, (double)p_147235_1_.func_149004_h() / 8000.0D, (double)p_147235_1_.func_148999_i() / 8000.0D);
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 66)
        {
            var8 = new EntityWitherSkull(this.field_147300_g, var2, var4, var6, (double)p_147235_1_.func_149010_g() / 8000.0D, (double)p_147235_1_.func_149004_h() / 8000.0D, (double)p_147235_1_.func_148999_i() / 8000.0D);
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 62)
        {
            var8 = new EntityEgg(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 73)
        {
            var8 = new EntityPotion(this.field_147300_g, var2, var4, var6, p_147235_1_.func_149009_m());
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 75)
        {
            var8 = new EntityExpBottle(this.field_147300_g, var2, var4, var6);
            p_147235_1_.func_149002_g(0);
        }
        else if (p_147235_1_.func_148993_l() == 1)
        {
            var8 = new EntityBoat(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 50)
        {
            var8 = new EntityTNTPrimed(this.field_147300_g, var2, var4, var6, null);
        }
        else if (p_147235_1_.func_148993_l() == 51)
        {
            var8 = new EntityEnderCrystal(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 2)
        {
            var8 = new EntityItem(this.field_147300_g, var2, var4, var6);
        }
        else if (p_147235_1_.func_148993_l() == 70)
        {
            var8 = new EntityFallingBlock(this.field_147300_g, var2, var4, var6, Block.func_149729_e(p_147235_1_.func_149009_m() & 65535), p_147235_1_.func_149009_m() >> 16);
            p_147235_1_.func_149002_g(0);
		} else {
            var8 = EntityList.createEntityByID(p_147235_1_.func_148993_l(), this.field_147300_g);
        }

        if (var8 != null)
        {
            ((Entity)var8).serverPosX = p_147235_1_.func_148997_d();
            ((Entity)var8).serverPosY = p_147235_1_.func_148998_e();
            ((Entity)var8).serverPosZ = p_147235_1_.func_148994_f();
            ((Entity)var8).rotationPitch = (float)(p_147235_1_.func_149008_j() * 360) / 256.0F;
            ((Entity)var8).rotationYaw = (float)(p_147235_1_.func_149006_k() * 360) / 256.0F;
            Entity[] var12 = ((Entity)var8).getParts();

            if (var12 != null)
            {
                int var10 = p_147235_1_.func_149001_c() - ((Entity)var8).func_145782_y();

                for (Entity aVar12 : var12) {
                    aVar12.func_145769_d(aVar12.func_145782_y() + var10);
                }
            }

            ((Entity)var8).func_145769_d(p_147235_1_.func_149001_c());
            this.field_147300_g.addEntityToWorld(p_147235_1_.func_149001_c(), (Entity)var8);

            if (p_147235_1_.func_149009_m() > 0)
            {
                if (p_147235_1_.func_148993_l() == 60)
                {
                    Entity var13 = this.field_147300_g.getEntityByID(p_147235_1_.func_149009_m());

                    if (var13 instanceof EntityLivingBase)
                    {
                        EntityArrow var14 = (EntityArrow)var8;
                        var14.shootingEntity = var13;
                    }
                }

                ((Entity)var8).setVelocity((double)p_147235_1_.func_149010_g() / 8000.0D, (double)p_147235_1_.func_149004_h() / 8000.0D, (double)p_147235_1_.func_148999_i() / 8000.0D);
            }
        }
    }

    public void func_147286_a(S11PacketSpawnExperienceOrb p_147286_1_)
    {
        EntityXPOrb var2 = new EntityXPOrb(this.field_147300_g, (double)p_147286_1_.func_148984_d(), (double)p_147286_1_.func_148983_e(), (double)p_147286_1_.func_148982_f(), p_147286_1_.func_148986_g());
        var2.serverPosX = p_147286_1_.func_148984_d();
        var2.serverPosY = p_147286_1_.func_148983_e();
        var2.serverPosZ = p_147286_1_.func_148982_f();
        var2.rotationYaw = 0.0F;
        var2.rotationPitch = 0.0F;
        var2.func_145769_d(p_147286_1_.func_148985_c());
        this.field_147300_g.addEntityToWorld(p_147286_1_.func_148985_c(), var2);
    }

    public void func_147292_a(S2CPacketSpawnGlobalEntity p_147292_1_)
    {
        double var2 = (double)p_147292_1_.func_149051_d() / 32.0D;
        double var4 = (double)p_147292_1_.func_149050_e() / 32.0D;
        double var6 = (double)p_147292_1_.func_149049_f() / 32.0D;
        EntityLightningBolt var8 = null;

        if (p_147292_1_.func_149053_g() == 1)
        {
            var8 = new EntityLightningBolt(this.field_147300_g, var2, var4, var6);
        }

        if (var8 != null)
        {
            var8.serverPosX = p_147292_1_.func_149051_d();
            var8.serverPosY = p_147292_1_.func_149050_e();
            var8.serverPosZ = p_147292_1_.func_149049_f();
            var8.rotationYaw = 0.0F;
            var8.rotationPitch = 0.0F;
            var8.func_145769_d(p_147292_1_.func_149052_c());
            this.field_147300_g.addWeatherEffect(var8);
        }
    }

    public void func_147288_a(S10PacketSpawnPainting p_147288_1_)
    {
        EntityPainting var2 = new EntityPainting(this.field_147300_g, p_147288_1_.func_148964_d(), p_147288_1_.func_148963_e(), p_147288_1_.func_148962_f(), p_147288_1_.func_148966_g(), p_147288_1_.func_148961_h());
        this.field_147300_g.addEntityToWorld(p_147288_1_.func_148965_c(), var2);
    }

    public void func_147244_a(S12PacketEntityVelocity p_147244_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147244_1_.func_149412_c());

        if (var2 != null)
        {
            var2.setVelocity((double)p_147244_1_.func_149411_d() / 8000.0D, (double)p_147244_1_.func_149410_e() / 8000.0D, (double)p_147244_1_.func_149409_f() / 8000.0D);
        }
    }

    public void func_147284_a(S1CPacketEntityMetadata p_147284_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147284_1_.func_149375_d());

        if (var2 != null && p_147284_1_.func_149376_c() != null)
        {
            var2.getDataWatcher().updateWatchedObjectsFromList(p_147284_1_.func_149376_c());
        }
    }

    public void func_147237_a(S0CPacketSpawnPlayer p_147237_1_)
    {
        double var2 = (double)p_147237_1_.func_148942_f() / 32.0D;
        double var4 = (double)p_147237_1_.func_148949_g() / 32.0D;
        double var6 = (double)p_147237_1_.func_148946_h() / 32.0D;
        float var8 = (float)(p_147237_1_.func_148941_i() * 360) / 256.0F;
        float var9 = (float)(p_147237_1_.func_148945_j() * 360) / 256.0F;
        EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.field_147299_f.theWorld, p_147237_1_.func_148948_e());
        var10.prevPosX = var10.lastTickPosX = (double)(var10.serverPosX = p_147237_1_.func_148942_f());
        var10.prevPosY = var10.lastTickPosY = (double)(var10.serverPosY = p_147237_1_.func_148949_g());
        var10.prevPosZ = var10.lastTickPosZ = (double)(var10.serverPosZ = p_147237_1_.func_148946_h());
        int var11 = p_147237_1_.func_148947_k();

        if (var11 == 0)
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = null;
        }
        else
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(Item.func_150899_d(var11), 1, 0);
        }

        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        this.field_147300_g.addEntityToWorld(p_147237_1_.func_148943_d(), var10);
        List var12 = p_147237_1_.func_148944_c();

        if (var12 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var12);
        }
    }

    public void func_147275_a(S18PacketEntityTeleport p_147275_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147275_1_.func_149451_c());

        if (var2 != null)
        {
            var2.serverPosX = p_147275_1_.func_149449_d();
            var2.serverPosY = p_147275_1_.func_149448_e();
            var2.serverPosZ = p_147275_1_.func_149446_f();
            double var3 = (double)var2.serverPosX / 32.0D;
            double var5 = (double)var2.serverPosY / 32.0D + 0.015625D;
            double var7 = (double)var2.serverPosZ / 32.0D;
            float var9 = (float)(p_147275_1_.func_149450_g() * 360) / 256.0F;
            float var10 = (float)(p_147275_1_.func_149447_h() * 360) / 256.0F;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    public void func_147257_a(S09PacketHeldItemChange p_147257_1_)
    {
        if (p_147257_1_.func_149385_c() >= 0 && p_147257_1_.func_149385_c() < InventoryPlayer.getHotbarSize())
        {
            this.field_147299_f.thePlayer.inventory.currentItem = p_147257_1_.func_149385_c();
        }
    }

    public void func_147259_a(S14PacketEntity p_147259_1_)
    {
        Entity var2 = p_147259_1_.func_149065_a(this.field_147300_g);

        if (var2 != null)
        {
            var2.serverPosX += p_147259_1_.func_149062_c();
            var2.serverPosY += p_147259_1_.func_149061_d();
            var2.serverPosZ += p_147259_1_.func_149064_e();
            double var3 = (double)var2.serverPosX / 32.0D;
            double var5 = (double)var2.serverPosY / 32.0D;
            double var7 = (double)var2.serverPosZ / 32.0D;
            float var9 = p_147259_1_.func_149060_h() ? (float)(p_147259_1_.func_149066_f() * 360) / 256.0F : var2.rotationYaw;
            float var10 = p_147259_1_.func_149060_h() ? (float)(p_147259_1_.func_149063_g() * 360) / 256.0F : var2.rotationPitch;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    public void func_147267_a(S19PacketEntityHeadLook p_147267_1_)
    {
        Entity var2 = p_147267_1_.func_149381_a(this.field_147300_g);

        if (var2 != null)
        {
            float var3 = (float)(p_147267_1_.func_149380_c() * 360) / 256.0F;
            var2.setRotationYawHead(var3);
        }
    }

    public void func_147238_a(S13PacketDestroyEntities p_147238_1_)
    {
        for (int var2 = 0; var2 < p_147238_1_.func_149098_c().length; ++var2)
        {
            this.field_147300_g.removeEntityFromWorld(p_147238_1_.func_149098_c()[var2]);
        }
    }

    public void func_147258_a(S08PacketPlayerPosLook p_147258_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;
        double var3 = p_147258_1_.func_148932_c();
        double var5 = p_147258_1_.func_148928_d();
        double var7 = p_147258_1_.func_148933_e();
        float var9 = p_147258_1_.func_148931_f();
        float var10 = p_147258_1_.func_148930_g();
        var2.ySize = 0.0F;
        var2.motionX = var2.motionY = var2.motionZ = 0.0D;
        var2.setPositionAndRotation(var3, var5, var7, var9, var10);
        this.field_147302_e.func_150725_a(new C03PacketPlayer.C06PacketPlayerPosLook(var2.posX, var2.boundingBox.minY, var2.posY, var2.posZ, p_147258_1_.func_148931_f(), p_147258_1_.func_148930_g(), p_147258_1_.func_148929_h()));

        if (!this.field_147309_h)
        {
            this.field_147299_f.thePlayer.prevPosX = this.field_147299_f.thePlayer.posX;
            this.field_147299_f.thePlayer.prevPosY = this.field_147299_f.thePlayer.posY;
            this.field_147299_f.thePlayer.prevPosZ = this.field_147299_f.thePlayer.posZ;
            this.field_147309_h = true;
            this.field_147299_f.func_147108_a(null);
        }
    }

    public void func_147287_a(S22PacketMultiBlockChange p_147287_1_)
    {
        int var2 = p_147287_1_.func_148920_c().chunkXPos * 16;
        int var3 = p_147287_1_.func_148920_c().chunkZPos * 16;

        if (p_147287_1_.func_148921_d() != null)
        {
            DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(p_147287_1_.func_148921_d()));

            try
            {
                for (int var5 = 0; var5 < p_147287_1_.func_148922_e(); ++var5)
                {
                    short var6 = var4.readShort();
                    short var7 = var4.readShort();
                    int var8 = var7 >> 4 & 4095;
                    int var9 = var7 & 15;
                    int var10 = var6 >> 12 & 15;
                    int var11 = var6 >> 8 & 15;
                    int var12 = var6 & 255;
                    this.field_147300_g.func_147492_c(var10 + var2, var12, var11 + var3, Block.func_149729_e(var8), var9);
                }
            }
            catch (IOException ignored)
            {
            }
        }
    }

    public void func_147263_a(S21PacketChunkData p_147263_1_)
    {
        if (p_147263_1_.func_149274_i())
        {
            if (p_147263_1_.func_149276_g() == 0)
            {
                this.field_147300_g.doPreChunk(p_147263_1_.func_149273_e(), p_147263_1_.func_149271_f(), false);
                return;
            }

            this.field_147300_g.doPreChunk(p_147263_1_.func_149273_e(), p_147263_1_.func_149271_f(), true);
        }

        this.field_147300_g.invalidateBlockReceiveRegion(p_147263_1_.func_149273_e() << 4, 0, p_147263_1_.func_149271_f() << 4, (p_147263_1_.func_149273_e() << 4) + 15, 256, (p_147263_1_.func_149271_f() << 4) + 15);
        Chunk var2 = this.field_147300_g.getChunkFromChunkCoords(p_147263_1_.func_149273_e(), p_147263_1_.func_149271_f());
        var2.fillChunk(p_147263_1_.func_149272_d(), p_147263_1_.func_149276_g(), p_147263_1_.func_149270_h(), p_147263_1_.func_149274_i());
        this.field_147300_g.func_147458_c(p_147263_1_.func_149273_e() << 4, 0, p_147263_1_.func_149271_f() << 4, (p_147263_1_.func_149273_e() << 4) + 15, 256, (p_147263_1_.func_149271_f() << 4) + 15);

        if (!p_147263_1_.func_149274_i() || !(this.field_147300_g.provider instanceof WorldProviderSurface))
        {
            var2.resetRelightChecks();
        }
    }

    public void func_147234_a(S23PacketBlockChange p_147234_1_)
    {
        this.field_147300_g.func_147492_c(p_147234_1_.func_148879_d(), p_147234_1_.func_148878_e(), p_147234_1_.func_148877_f(), p_147234_1_.func_148880_c(), p_147234_1_.func_148881_g());
    }

    public void func_147253_a(S40PacketDisconnect p_147253_1_)
    {
        this.field_147302_e.func_150718_a(p_147253_1_.func_149165_c());
    }

    public void func_147231_a(IChatComponent p_147231_1_)
    {
        this.field_147299_f.loadWorld(null);

        if (this.field_147307_j != null)
        {
            this.field_147299_f.func_147108_a(new GuiScreenDisconnectedOnline(this.field_147307_j, "disconnect.lost", p_147231_1_));
        }
        else
        {
            this.field_147299_f.func_147108_a(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", p_147231_1_));
        }
    }

    public void func_147297_a(Packet p_147297_1_)
    {
        this.field_147302_e.func_150725_a(p_147297_1_);
    }

    public void func_147246_a(S0DPacketCollectItem p_147246_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147246_1_.func_149354_c());
        Object var3 = this.field_147300_g.getEntityByID(p_147246_1_.func_149353_d());

        if (var3 == null)
        {
            var3 = this.field_147299_f.thePlayer;
        }

        if (var2 != null)
        {
            if (var2 instanceof EntityXPOrb)
            {
                this.field_147300_g.playSoundAtEntity(var2, "random.orb", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.field_147300_g.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.field_147299_f.effectRenderer.addEffect(new EntityPickupFX(this.field_147299_f.theWorld, var2, (Entity)var3, -0.5F));
            this.field_147300_g.removeEntityFromWorld(p_147246_1_.func_149354_c());
        }
    }

    public void func_147251_a(S02PacketChat p_147251_1_)
    {
        this.field_147299_f.ingameGUI.func_146158_b().func_146227_a(p_147251_1_.func_148915_c());
    }

    public void func_147279_a(S0BPacketAnimation p_147279_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147279_1_.func_148978_c());

        if (var2 != null)
        {
            if (p_147279_1_.func_148977_d() == 0)
            {
                EntityLivingBase var3 = (EntityLivingBase)var2;
                var3.swingItem();
            }
            else if (p_147279_1_.func_148977_d() == 1)
            {
                var2.performHurtAnimation();
            }
            else if (p_147279_1_.func_148977_d() == 2)
            {
                EntityPlayer var4 = (EntityPlayer)var2;
                var4.wakeUpPlayer(false, false, false);
            }
            else if (p_147279_1_.func_148977_d() == 4)
            {
                this.field_147299_f.effectRenderer.addEffect(new EntityCrit2FX(this.field_147299_f.theWorld, var2));
            }
            else if (p_147279_1_.func_148977_d() == 5)
            {
                EntityCrit2FX var5 = new EntityCrit2FX(this.field_147299_f.theWorld, var2, "magicCrit");
                this.field_147299_f.effectRenderer.addEffect(var5);
            }
        }
    }

    public void func_147278_a(S0APacketUseBed p_147278_1_)
    {
        p_147278_1_.func_149091_a(this.field_147300_g).sleepInBedAt(p_147278_1_.func_149092_c(), p_147278_1_.func_149090_d(), p_147278_1_.func_149089_e());
    }

    public void func_147281_a(S0FPacketSpawnMob p_147281_1_)
    {
        double var2 = (double)p_147281_1_.func_149023_f() / 32.0D;
        double var4 = (double)p_147281_1_.func_149034_g() / 32.0D;
        double var6 = (double)p_147281_1_.func_149029_h() / 32.0D;
        float var8 = (float)(p_147281_1_.func_149028_l() * 360) / 256.0F;
        float var9 = (float)(p_147281_1_.func_149030_m() * 360) / 256.0F;
        EntityLivingBase var10 = (EntityLivingBase)EntityList.createEntityByID(p_147281_1_.func_149025_e(), this.field_147299_f.theWorld);
        var10.serverPosX = p_147281_1_.func_149023_f();
        var10.serverPosY = p_147281_1_.func_149034_g();
        var10.serverPosZ = p_147281_1_.func_149029_h();
        var10.rotationYawHead = (float)(p_147281_1_.func_149032_n() * 360) / 256.0F;
        Entity[] var11 = var10.getParts();

        if (var11 != null)
        {
            int var12 = p_147281_1_.func_149024_d() - var10.func_145782_y();

            for (Entity aVar11 : var11) {
                aVar11.func_145769_d(aVar11.func_145782_y() + var12);
            }
        }

        var10.func_145769_d(p_147281_1_.func_149024_d());
        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        var10.motionX = (double)((float)p_147281_1_.func_149026_i() / 8000.0F);
        var10.motionY = (double)((float)p_147281_1_.func_149033_j() / 8000.0F);
        var10.motionZ = (double)((float)p_147281_1_.func_149031_k() / 8000.0F);
        this.field_147300_g.addEntityToWorld(p_147281_1_.func_149024_d(), var10);
        List var14 = p_147281_1_.func_149027_c();

        if (var14 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var14);
        }
    }

    public void func_147285_a(S03PacketTimeUpdate p_147285_1_)
    {
        this.field_147299_f.theWorld.func_82738_a(p_147285_1_.func_149366_c());
        this.field_147299_f.theWorld.setWorldTime(p_147285_1_.func_149365_d());
    }

    public void func_147271_a(S05PacketSpawnPosition p_147271_1_)
    {
        this.field_147299_f.thePlayer.setSpawnChunk(new ChunkCoordinates(p_147271_1_.func_149360_c(), p_147271_1_.func_149359_d(), p_147271_1_.func_149358_e()), true);
        this.field_147299_f.theWorld.getWorldInfo().setSpawnPosition(p_147271_1_.func_149360_c(), p_147271_1_.func_149359_d(), p_147271_1_.func_149358_e());
    }

    public void func_147243_a(S1BPacketEntityAttach p_147243_1_)
    {
        Object var2 = this.field_147300_g.getEntityByID(p_147243_1_.func_149403_d());
        Entity var3 = this.field_147300_g.getEntityByID(p_147243_1_.func_149402_e());

        if (p_147243_1_.func_149404_c() == 0)
        {
            boolean var4 = false;

            if (p_147243_1_.func_149403_d() == this.field_147299_f.thePlayer.func_145782_y())
            {
                var2 = this.field_147299_f.thePlayer;

                if (var3 instanceof EntityBoat)
                {
                    ((EntityBoat)var3).func_70270_d(false);
                }

                var4 = ((Entity)var2).ridingEntity == null && var3 != null;
            }
            else if (var3 instanceof EntityBoat)
            {
                ((EntityBoat)var3).func_70270_d(true);
            }

            if (var2 == null)
            {
                return;
            }

            ((Entity)var2).mountEntity(var3);

            if (var4)
            {
                GameSettings var5 = this.field_147299_f.gameSettings;
                this.field_147299_f.ingameGUI.func_110326_a(I18n.getStringParams("mount.onboard", GameSettings.getKeyDisplayString(var5.keyBindSneak.func_151463_i())), false);
            }
        }
        else if (p_147243_1_.func_149404_c() == 1 && var2 != null && var2 instanceof EntityLiving)
        {
            if (var3 != null)
            {
                ((EntityLiving)var2).setLeashedToEntity(var3, false);
            }
            else
            {
                ((EntityLiving)var2).clearLeashed(false, false);
            }
        }
    }

    public void func_147236_a(S19PacketEntityStatus p_147236_1_)
    {
        Entity var2 = p_147236_1_.func_149161_a(this.field_147300_g);

        if (var2 != null)
        {
            var2.handleHealthUpdate(p_147236_1_.func_149160_c());
        }
    }

    public void func_147249_a(S06PacketUpdateHealth p_147249_1_)
    {
        this.field_147299_f.thePlayer.setPlayerSPHealth(p_147249_1_.func_149332_c());
        this.field_147299_f.thePlayer.getFoodStats().setFoodLevel(p_147249_1_.func_149330_d());
        this.field_147299_f.thePlayer.getFoodStats().setFoodSaturationLevel(p_147249_1_.func_149331_e());
    }

    public void func_147295_a(S1FPacketSetExperience p_147295_1_)
    {
        this.field_147299_f.thePlayer.setXPStats(p_147295_1_.func_149397_c(), p_147295_1_.func_149396_d(), p_147295_1_.func_149395_e());
    }

    public void func_147280_a(S07PacketRespawn p_147280_1_)
    {
        if (p_147280_1_.func_149082_c() != this.field_147299_f.thePlayer.dimension)
        {
            this.field_147309_h = false;
            Scoreboard var2 = this.field_147300_g.getScoreboard();
            this.field_147300_g = new WorldClient(this, new WorldSettings(0L, p_147280_1_.func_149083_e(), false, this.field_147299_f.theWorld.getWorldInfo().isHardcoreModeEnabled(), p_147280_1_.func_149080_f()), p_147280_1_.func_149082_c(), p_147280_1_.func_149081_d(), this.field_147299_f.mcProfiler);
            this.field_147300_g.func_96443_a(var2);
            this.field_147300_g.isRemote = true;
            this.field_147299_f.loadWorld(this.field_147300_g);
            this.field_147299_f.thePlayer.dimension = p_147280_1_.func_149082_c();
            this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
        }

        this.field_147299_f.setDimensionAndSpawnPlayer(p_147280_1_.func_149082_c());
        this.field_147299_f.playerController.setGameType(p_147280_1_.func_149083_e());
    }

    public void func_147283_a(S27PacketExplosion p_147283_1_)
    {
        Explosion var2 = new Explosion(this.field_147299_f.theWorld, null, p_147283_1_.func_149148_f(), p_147283_1_.func_149143_g(), p_147283_1_.func_149145_h(), p_147283_1_.func_149146_i());
        var2.affectedBlockPositions = p_147283_1_.func_149150_j();
        var2.doExplosionB(true);
        this.field_147299_f.thePlayer.motionX += (double)p_147283_1_.func_149149_c();
        this.field_147299_f.thePlayer.motionY += (double)p_147283_1_.func_149144_d();
        this.field_147299_f.thePlayer.motionZ += (double)p_147283_1_.func_149147_e();
    }

    public void func_147265_a(S2DPacketOpenWindow p_147265_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;

        switch (p_147265_1_.func_148899_d())
        {
            case 0:
                var2.displayGUIChest(new InventoryBasic(p_147265_1_.func_148902_e(), p_147265_1_.func_148900_g(), p_147265_1_.func_148898_f()));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 1:
                var2.displayGUIWorkbench(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 2:
                TileEntityFurnace var4 = new TileEntityFurnace();

                if (p_147265_1_.func_148900_g())
                {
                    var4.func_145951_a(p_147265_1_.func_148902_e());
                }

                var2.func_146101_a(var4);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 3:
                TileEntityDispenser var7 = new TileEntityDispenser();

                if (p_147265_1_.func_148900_g())
                {
                    var7.func_146018_a(p_147265_1_.func_148902_e());
                }

                var2.func_146102_a(var7);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 4:
                var2.displayGUIEnchantment(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ), p_147265_1_.func_148900_g() ? p_147265_1_.func_148902_e() : null);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 5:
                TileEntityBrewingStand var5 = new TileEntityBrewingStand();

                if (p_147265_1_.func_148900_g())
                {
                    var5.func_145937_a(p_147265_1_.func_148902_e());
                }

                var2.func_146098_a(var5);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 6:
                var2.displayGUIMerchant(new NpcMerchant(var2), p_147265_1_.func_148900_g() ? p_147265_1_.func_148902_e() : null);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 7:
                TileEntityBeacon var8 = new TileEntityBeacon();
                var2.func_146104_a(var8);

                if (p_147265_1_.func_148900_g())
                {
                    var8.func_145999_a(p_147265_1_.func_148902_e());
                }

                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 8:
                var2.displayGUIAnvil(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 9:
                TileEntityHopper var3 = new TileEntityHopper();

                if (p_147265_1_.func_148900_g())
                {
                    var3.func_145886_a(p_147265_1_.func_148902_e());
                }

                var2.func_146093_a(var3);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 10:
                TileEntityDropper var6 = new TileEntityDropper();

                if (p_147265_1_.func_148900_g())
                {
                    var6.func_146018_a(p_147265_1_.func_148902_e());
                }

                var2.func_146102_a(var6);
                var2.openContainer.windowId = p_147265_1_.func_148901_c();
                break;

            case 11:
                Entity var9 = this.field_147300_g.getEntityByID(p_147265_1_.func_148897_h());

                if (var9 != null && var9 instanceof EntityHorse)
                {
                    var2.displayGUIHorse((EntityHorse)var9, new AnimalChest(p_147265_1_.func_148902_e(), p_147265_1_.func_148900_g(), p_147265_1_.func_148898_f()));
                    var2.openContainer.windowId = p_147265_1_.func_148901_c();
                }
        }
    }

    public void func_147266_a(S2FPacketSetSlot p_147266_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;

        if (p_147266_1_.func_149175_c() == -1)
        {
            var2.inventory.setItemStack(p_147266_1_.func_149174_e());
        }
        else
        {
            boolean var3 = false;

            if (this.field_147299_f.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative var4 = (GuiContainerCreative)this.field_147299_f.currentScreen;
                var3 = var4.func_147056_g() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (p_147266_1_.func_149175_c() == 0 && p_147266_1_.func_149173_d() >= 36 && p_147266_1_.func_149173_d() < 45)
            {
                ItemStack var5 = var2.inventoryContainer.getSlot(p_147266_1_.func_149173_d()).getStack();

                if (p_147266_1_.func_149174_e() != null && (var5 == null || var5.stackSize < p_147266_1_.func_149174_e().stackSize))
                {
                    p_147266_1_.func_149174_e().animationsToGo = 5;
                }

                var2.inventoryContainer.putStackInSlot(p_147266_1_.func_149173_d(), p_147266_1_.func_149174_e());
            }
            else if (p_147266_1_.func_149175_c() == var2.openContainer.windowId && (p_147266_1_.func_149175_c() != 0 || !var3))
            {
                var2.openContainer.putStackInSlot(p_147266_1_.func_149173_d(), p_147266_1_.func_149174_e());
            }
        }
    }

    public void func_147239_a(S32PacketConfirmTransaction p_147239_1_)
    {
        Container var2 = null;
        EntityClientPlayerMP var3 = this.field_147299_f.thePlayer;

        if (p_147239_1_.func_148889_c() == 0)
        {
            var2 = var3.inventoryContainer;
        }
        else if (p_147239_1_.func_148889_c() == var3.openContainer.windowId)
        {
            var2 = var3.openContainer;
        }

        if (var2 != null && !p_147239_1_.func_148888_e())
        {
            this.func_147297_a(new C0FPacketConfirmTransaction(p_147239_1_.func_148889_c(), p_147239_1_.func_148890_d(), true));
        }
    }

    public void func_147241_a(S30PacketWindowItems p_147241_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;

        if (p_147241_1_.func_148911_c() == 0)
        {
            var2.inventoryContainer.putStacksInSlots(p_147241_1_.func_148910_d());
        }
        else if (p_147241_1_.func_148911_c() == var2.openContainer.windowId)
        {
            var2.openContainer.putStacksInSlots(p_147241_1_.func_148910_d());
        }
    }

    public void func_147268_a(S36PacketSignEditorOpen p_147268_1_)
    {
        Object var2 = this.field_147300_g.func_147438_o(p_147268_1_.func_149129_c(), p_147268_1_.func_149128_d(), p_147268_1_.func_149127_e());

        if (var2 == null)
        {
            var2 = new TileEntitySign();
            ((TileEntity)var2).func_145834_a(this.field_147300_g);
            ((TileEntity)var2).field_145851_c = p_147268_1_.func_149129_c();
            ((TileEntity)var2).field_145848_d = p_147268_1_.func_149128_d();
            ((TileEntity)var2).field_145849_e = p_147268_1_.func_149127_e();
        }

        this.field_147299_f.thePlayer.func_146100_a((TileEntity)var2);
    }

    public void func_147248_a(S33PacketUpdateSign p_147248_1_)
    {
        boolean var2 = false;

        if (this.field_147299_f.theWorld.blockExists(p_147248_1_.func_149346_c(), p_147248_1_.func_149345_d(), p_147248_1_.func_149344_e()))
        {
            TileEntity var3 = this.field_147299_f.theWorld.func_147438_o(p_147248_1_.func_149346_c(), p_147248_1_.func_149345_d(), p_147248_1_.func_149344_e());

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (var4.func_145914_a())
                {
                    for (int var5 = 0; var5 < 4; ++var5)
                    {
                        var4.field_145915_a[var5] = p_147248_1_.func_149347_f()[var5];
                    }

                    var4.onInventoryChanged();
                }

                var2 = true;
            }
        }

        if (!var2 && this.field_147299_f.thePlayer != null)
        {
            this.field_147299_f.thePlayer.func_145747_a(new ChatComponentText("Unable to locate sign at " + p_147248_1_.func_149346_c() + ", " + p_147248_1_.func_149345_d() + ", " + p_147248_1_.func_149344_e()));
        }
    }

    public void func_147273_a(S35PacketUpdateTileEntity p_147273_1_)
    {
        if (this.field_147299_f.theWorld.blockExists(p_147273_1_.func_148856_c(), p_147273_1_.func_148855_d(), p_147273_1_.func_148854_e()))
        {
            TileEntity var2 = this.field_147299_f.theWorld.func_147438_o(p_147273_1_.func_148856_c(), p_147273_1_.func_148855_d(), p_147273_1_.func_148854_e());

            if (var2 != null)
            {
                if (p_147273_1_.func_148853_f() == 1 && var2 instanceof TileEntityMobSpawner)
                {
                    var2.func_145839_a(p_147273_1_.func_148857_g());
                }
                else if (p_147273_1_.func_148853_f() == 2 && var2 instanceof TileEntityCommandBlock)
                {
                    var2.func_145839_a(p_147273_1_.func_148857_g());
                }
                else if (p_147273_1_.func_148853_f() == 3 && var2 instanceof TileEntityBeacon)
                {
                    var2.func_145839_a(p_147273_1_.func_148857_g());
                }
                else if (p_147273_1_.func_148853_f() == 4 && var2 instanceof TileEntitySkull)
                {
                    var2.func_145839_a(p_147273_1_.func_148857_g());
                }
                else if (p_147273_1_.func_148853_f() == 5 && var2 instanceof TileEntityFlowerPot)
                {
                    var2.func_145839_a(p_147273_1_.func_148857_g());
                }
            }
        }
    }

    public void func_147245_a(S31PacketWindowProperty p_147245_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;

        if (var2.openContainer != null && var2.openContainer.windowId == p_147245_1_.func_149182_c())
        {
            var2.openContainer.updateProgressBar(p_147245_1_.func_149181_d(), p_147245_1_.func_149180_e());
        }
    }

    public void func_147242_a(S04PacketEntityEquipment p_147242_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147242_1_.func_149389_d());

        if (var2 != null)
        {
            var2.setCurrentItemOrArmor(p_147242_1_.func_149388_e(), p_147242_1_.func_149390_c());
        }
    }

    public void func_147276_a(S2EPacketCloseWindow p_147276_1_)
    {
        this.field_147299_f.thePlayer.func_92015_f();
    }

    public void func_147261_a(S24PacketBlockAction p_147261_1_)
    {
        this.field_147299_f.theWorld.func_147452_c(p_147261_1_.func_148867_d(), p_147261_1_.func_148866_e(), p_147261_1_.func_148865_f(), p_147261_1_.func_148868_c(), p_147261_1_.func_148869_g(), p_147261_1_.func_148864_h());
    }

    public void func_147294_a(S25PacketBlockBreakAnim p_147294_1_)
    {
        this.field_147299_f.theWorld.func_147443_d(p_147294_1_.func_148845_c(), p_147294_1_.func_148844_d(), p_147294_1_.func_148843_e(), p_147294_1_.func_148842_f(), p_147294_1_.func_148846_g());
    }

    public void func_147269_a(S26PacketMapChunkBulk p_147269_1_)
    {
        for (int var2 = 0; var2 < p_147269_1_.func_149254_d(); ++var2)
        {
            int var3 = p_147269_1_.func_149255_a(var2);
            int var4 = p_147269_1_.func_149253_b(var2);
            this.field_147300_g.doPreChunk(var3, var4, true);
            this.field_147300_g.invalidateBlockReceiveRegion(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
            Chunk var5 = this.field_147300_g.getChunkFromChunkCoords(var3, var4);
            var5.fillChunk(p_147269_1_.func_149256_c(var2), p_147269_1_.func_149252_e()[var2], p_147269_1_.func_149257_f()[var2], true);
            this.field_147300_g.func_147458_c(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);

            if (!(this.field_147300_g.provider instanceof WorldProviderSurface))
            {
                var5.resetRelightChecks();
            }
        }
    }

    public void func_147252_a(S2BPacketChangeGameState p_147252_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;
        int var3 = p_147252_1_.func_149138_c();
        float var4 = p_147252_1_.func_149137_d();
        int var5 = MathHelper.floor_float(var4 + 0.5F);

        if (var3 >= 0 && var3 < S2BPacketChangeGameState.field_149142_a.length && S2BPacketChangeGameState.field_149142_a[var3] != null)
        {
            var2.func_146105_b(new ChatComponentTranslation(S2BPacketChangeGameState.field_149142_a[var3]));
        }

        if (var3 == 1)
        {
            this.field_147300_g.getWorldInfo().setRaining(true);
            this.field_147300_g.setRainStrength(0.0F);
        }
        else if (var3 == 2)
        {
            this.field_147300_g.getWorldInfo().setRaining(false);
            this.field_147300_g.setRainStrength(1.0F);
        }
        else if (var3 == 3)
        {
            this.field_147299_f.playerController.setGameType(WorldSettings.GameType.getByID(var5));
        }
        else if (var3 == 4)
        {
            this.field_147299_f.func_147108_a(new GuiWinGame());
        }
        else if (var3 == 5)
        {
            GameSettings var6 = this.field_147299_f.gameSettings;

            if (var4 == 0.0F)
            {
                this.field_147299_f.func_147108_a(new GuiScreenDemo());
            }
            else if (var4 == 101.0F)
            {
                this.field_147299_f.ingameGUI.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.movement", GameSettings.getKeyDisplayString(var6.keyBindForward.func_151463_i()), GameSettings.getKeyDisplayString(var6.keyBindLeft.func_151463_i()), GameSettings.getKeyDisplayString(var6.keyBindBack.func_151463_i()), GameSettings.getKeyDisplayString(var6.keyBindRight.func_151463_i())));
            }
            else if (var4 == 102.0F)
            {
                this.field_147299_f.ingameGUI.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(var6.keyBindJump.func_151463_i())));
            }
            else if (var4 == 103.0F)
            {
                this.field_147299_f.ingameGUI.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(var6.field_151445_Q.func_151463_i())));
            }
        }
        else if (var3 == 6)
        {
            this.field_147300_g.playSound(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
        else if (var3 == 7)
        {
            this.field_147300_g.setRainStrength(var4);
        }
        else if (var3 == 8)
        {
            this.field_147300_g.func_147442_i(var4);
        }
    }

    public void func_147264_a(S34PacketMaps p_147264_1_)
    {
        MapData var2 = ItemMap.func_150912_a(p_147264_1_.func_149188_c(), this.field_147299_f.theWorld);
        var2.updateMPMapData(p_147264_1_.func_149187_d());
        this.field_147299_f.entityRenderer.func_147701_i().func_148246_a(var2);
    }

    public void func_147277_a(S28PacketEffect p_147277_1_)
    {
        if (p_147277_1_.func_149244_c())
        {
            this.field_147299_f.theWorld.func_82739_e(p_147277_1_.func_149242_d(), p_147277_1_.func_149240_f(), p_147277_1_.func_149243_g(), p_147277_1_.func_149239_h(), p_147277_1_.func_149241_e());
        }
        else
        {
            this.field_147299_f.theWorld.playAuxSFX(p_147277_1_.func_149242_d(), p_147277_1_.func_149240_f(), p_147277_1_.func_149243_g(), p_147277_1_.func_149239_h(), p_147277_1_.func_149241_e());
        }
    }

    public void func_147293_a(S37PacketStatistics p_147293_1_)
    {
        boolean var2 = false;
        StatBase var5;
        int var6;

        for (Iterator var3 = p_147293_1_.func_148974_c().entrySet().iterator(); var3.hasNext(); this.field_147299_f.thePlayer.func_146107_m().func_150873_a(this.field_147299_f.thePlayer, var5, var6))
        {
            Entry var4 = (Entry)var3.next();
            var5 = (StatBase)var4.getKey();
            var6 = (Integer) var4.getValue();

            if (var5.isAchievement() && var6 > 0)
            {
                if (this.field_147308_k && this.field_147299_f.thePlayer.func_146107_m().writeStat(var5) == 0)
                {
                    this.field_147299_f.guiAchievement.func_146256_a((Achievement)var5);

                    if (var5 == AchievementList.openInventory)
                    {
                        this.field_147299_f.gameSettings.field_151441_H = false;
                        this.field_147299_f.gameSettings.saveOptions();
                    }
                }

                var2 = true;
            }
        }

        if (!this.field_147308_k && !var2 && this.field_147299_f.gameSettings.field_151441_H)
        {
            this.field_147299_f.guiAchievement.func_146255_b(AchievementList.openInventory);
        }

        this.field_147308_k = true;

        if (this.field_147299_f.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter)this.field_147299_f.currentScreen).func_146509_g();
        }
    }

    public void func_147260_a(S1DPacketEntityEffect p_147260_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147260_1_.func_149426_d());

        if (var2 instanceof EntityLivingBase)
        {
            PotionEffect var3 = new PotionEffect(p_147260_1_.func_149427_e(), p_147260_1_.func_149425_g(), p_147260_1_.func_149428_f());
            var3.setPotionDurationMax(p_147260_1_.func_149429_c());
            ((EntityLivingBase)var2).addPotionEffect(var3);
        }
    }

    public void func_147262_a(S1EPacketRemoveEntityEffect p_147262_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147262_1_.func_149076_c());

        if (var2 instanceof EntityLivingBase)
        {
            ((EntityLivingBase)var2).removePotionEffectClient(p_147262_1_.func_149075_d());
        }
    }

    public void func_147256_a(S38PacketPlayerListItem p_147256_1_)
    {
        GuiPlayerInfo var2 = (GuiPlayerInfo)this.field_147310_i.get(p_147256_1_.func_149122_c());

        if (var2 == null && p_147256_1_.func_149121_d())
        {
            var2 = new GuiPlayerInfo(p_147256_1_.func_149122_c());
            this.field_147310_i.put(p_147256_1_.func_149122_c(), var2);
            this.field_147303_b.add(var2);
        }

        if (var2 != null && !p_147256_1_.func_149121_d())
        {
            this.field_147310_i.remove(p_147256_1_.func_149122_c());
            this.field_147303_b.remove(var2);
        }

        if (var2 != null && p_147256_1_.func_149121_d())
        {
            var2.responseTime = p_147256_1_.func_149120_e();
        }
    }

    public void func_147272_a(S00PacketKeepAlive p_147272_1_)
    {
        this.func_147297_a(new C00PacketKeepAlive(p_147272_1_.func_149134_c()));
    }

    public void func_147232_a(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_)
    {
        throw new IllegalStateException("Unexpected protocol change!");
    }

    public void func_147270_a(S39PacketPlayerAbilities p_147270_1_)
    {
        EntityClientPlayerMP var2 = this.field_147299_f.thePlayer;
        var2.capabilities.isFlying = p_147270_1_.func_149106_d();
        var2.capabilities.isCreativeMode = p_147270_1_.func_149103_f();
        var2.capabilities.disableDamage = p_147270_1_.func_149112_c();
        var2.capabilities.allowFlying = p_147270_1_.func_149105_e();
        var2.capabilities.setFlySpeed(p_147270_1_.func_149101_g());
        var2.capabilities.setPlayerWalkSpeed(p_147270_1_.func_149107_h());
    }

    public void func_147274_a(S3APacketTabComplete p_147274_1_)
    {
        String[] var2 = p_147274_1_.func_149630_c();

        if (this.field_147299_f.currentScreen instanceof GuiChat)
        {
            GuiChat var3 = (GuiChat)this.field_147299_f.currentScreen;
            var3.func_146406_a(var2);
        }
    }

    public void func_147255_a(S29PacketSoundEffect p_147255_1_)
    {
        this.field_147299_f.theWorld.playSound(p_147255_1_.func_149207_d(), p_147255_1_.func_149211_e(), p_147255_1_.func_149210_f(), p_147255_1_.func_149212_c(), p_147255_1_.func_149208_g(), p_147255_1_.func_149209_h(), false);
    }

    public void func_147240_a(S3FPacketCustomPayload p_147240_1_)
    {
        if ("MC|TrList".equals(p_147240_1_.func_149169_c()))
        {
            ByteBuf var2 = Unpooled.wrappedBuffer(p_147240_1_.func_149168_d());

            try
            {
                int var3 = var2.readInt();
                GuiScreen var4 = this.field_147299_f.currentScreen;

                if (var4 != null && var4 instanceof GuiMerchant && var3 == this.field_147299_f.thePlayer.openContainer.windowId)
                {
                    IMerchant var5 = ((GuiMerchant)var4).func_147035_g();
                    MerchantRecipeList var6 = MerchantRecipeList.func_151390_b(new PacketBuffer(var2));
                    var5.setRecipes(var6);
                }
            }
            catch (IOException var7)
            {
                field_147301_d.error("Couldn\'t load trade info", var7);
            }
        }
        else if ("MC|Brand".equals(p_147240_1_.func_149169_c()))
        {
            this.field_147299_f.thePlayer.func_142020_c(new String(p_147240_1_.func_149168_d(), Charsets.UTF_8));
        }
        else if ("MC|RPack".equals(p_147240_1_.func_149169_c()))
        {
            final String var8 = new String(p_147240_1_.func_149168_d(), Charsets.UTF_8);

            if (this.field_147299_f.gameSettings.serverTextures)
            {
                if (this.field_147299_f.func_147104_D() != null && this.field_147299_f.func_147104_D().func_147408_b())
                {
                    this.field_147299_f.getResourcePackRepository().func_148526_a(var8);
                }
                else if (this.field_147299_f.func_147104_D() == null || this.field_147299_f.func_147104_D().func_147410_c())
                {
                    this.field_147299_f.func_147108_a(new GuiYesNo(new GuiScreen()
                    {
                        private static final String __OBFID = "CL_00000879";
                        public void confirmClicked(boolean par1, int par2)
                        {
                            this.field_146297_k = Minecraft.getMinecraft();

                            if (this.field_146297_k.func_147104_D() != null)
                            {
                                this.field_146297_k.func_147104_D().setAcceptsTextures(par1);
                                ServerList.func_147414_b(this.field_146297_k.func_147104_D());
                            }

                            if (par1)
                            {
                                this.field_146297_k.getResourcePackRepository().func_148526_a(var8);
                            }

                            this.field_146297_k.func_147108_a(null);
                        }
                    }, I18n.getStringParams("multiplayer.texturePrompt.line1"), I18n.getStringParams("multiplayer.texturePrompt.line2"), 0));
                }
            }
        }
    }

    public void func_147291_a(S3BPacketScoreboardObjective p_147291_1_)
    {
        Scoreboard var2 = this.field_147300_g.getScoreboard();
        ScoreObjective var3;

        if (p_147291_1_.func_149338_e() == 0)
        {
            var3 = var2.func_96535_a(p_147291_1_.func_149339_c(), IScoreObjectiveCriteria.field_96641_b);
            var3.setDisplayName(p_147291_1_.func_149337_d());
        }
        else
        {
            var3 = var2.getObjective(p_147291_1_.func_149339_c());

            if (p_147291_1_.func_149338_e() == 1)
            {
                var2.func_96519_k(var3);
            }
            else if (p_147291_1_.func_149338_e() == 2)
            {
                var3.setDisplayName(p_147291_1_.func_149337_d());
            }
        }
    }

    public void func_147250_a(S3CPacketUpdateScore p_147250_1_)
    {
        Scoreboard var2 = this.field_147300_g.getScoreboard();
        ScoreObjective var3 = var2.getObjective(p_147250_1_.func_149321_d());

        if (p_147250_1_.func_149322_f() == 0)
        {
            Score var4 = var2.func_96529_a(p_147250_1_.func_149324_c(), var3);
            var4.func_96647_c(p_147250_1_.func_149323_e());
        }
        else if (p_147250_1_.func_149322_f() == 1)
        {
            var2.func_96515_c(p_147250_1_.func_149324_c());
        }
    }

    public void func_147254_a(S3DPacketDisplayScoreboard p_147254_1_)
    {
        Scoreboard var2 = this.field_147300_g.getScoreboard();

        if (p_147254_1_.func_149370_d().length() == 0)
        {
            var2.func_96530_a(p_147254_1_.func_149371_c(), null);
        }
        else
        {
            ScoreObjective var3 = var2.getObjective(p_147254_1_.func_149370_d());
            var2.func_96530_a(p_147254_1_.func_149371_c(), var3);
        }
    }

    public void func_147247_a(S3EPacketTeams p_147247_1_)
    {
        Scoreboard var2 = this.field_147300_g.getScoreboard();
        ScorePlayerTeam var3;

        if (p_147247_1_.func_149307_h() == 0)
        {
            var3 = var2.func_96527_f(p_147247_1_.func_149312_c());
        }
        else
        {
            var3 = var2.func_96508_e(p_147247_1_.func_149312_c());
        }

        if (p_147247_1_.func_149307_h() == 0 || p_147247_1_.func_149307_h() == 2)
        {
            var3.func_96664_a(p_147247_1_.func_149306_d());
            var3.func_96666_b(p_147247_1_.func_149311_e());
            var3.func_96662_c(p_147247_1_.func_149309_f());
            var3.func_98298_a(p_147247_1_.func_149308_i());
        }

        Iterator var4;
        String var5;

        if (p_147247_1_.func_149307_h() == 0 || p_147247_1_.func_149307_h() == 3)
        {
            var4 = p_147247_1_.func_149310_g().iterator();

            while (var4.hasNext())
            {
                var5 = (String)var4.next();
                var2.func_151392_a(var5, p_147247_1_.func_149312_c());
            }
        }

        if (p_147247_1_.func_149307_h() == 4)
        {
            var4 = p_147247_1_.func_149310_g().iterator();

            while (var4.hasNext())
            {
                var5 = (String)var4.next();
                var2.removePlayerFromTeam(var5, var3);
            }
        }

        if (p_147247_1_.func_149307_h() == 1)
        {
            var2.func_96511_d(var3);
        }
    }

    public void func_147289_a(S2APacketParticles p_147289_1_)
    {
        if (p_147289_1_.func_149222_k() == 0)
        {
            double var2 = (double)(p_147289_1_.func_149227_j() * p_147289_1_.func_149221_g());
            double var4 = (double)(p_147289_1_.func_149227_j() * p_147289_1_.func_149224_h());
            double var6 = (double)(p_147289_1_.func_149227_j() * p_147289_1_.func_149223_i());
            this.field_147300_g.spawnParticle(p_147289_1_.func_149228_c(), p_147289_1_.func_149220_d(), p_147289_1_.func_149226_e(), p_147289_1_.func_149225_f(), var2, var4, var6);
        }
        else
        {
            for (int var15 = 0; var15 < p_147289_1_.func_149222_k(); ++var15)
            {
                double var3 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149221_g();
                double var5 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149224_h();
                double var7 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149223_i();
                double var9 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149227_j();
                double var11 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149227_j();
                double var13 = this.field_147306_l.nextGaussian() * (double)p_147289_1_.func_149227_j();
                this.field_147300_g.spawnParticle(p_147289_1_.func_149228_c(), p_147289_1_.func_149220_d() + var3, p_147289_1_.func_149226_e() + var5, p_147289_1_.func_149225_f() + var7, var9, var11, var13);
            }
        }
    }

    public void func_147290_a(S20PacketEntityProperties p_147290_1_)
    {
        Entity var2 = this.field_147300_g.getEntityByID(p_147290_1_.func_149442_c());

        if (var2 != null)
        {
            if (!(var2 instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
            }
            else
            {
                BaseAttributeMap var3 = ((EntityLivingBase)var2).getAttributeMap();

                for (Object o : p_147290_1_.func_149441_d()) {
                    S20PacketEntityProperties.Snapshot var5 = (S20PacketEntityProperties.Snapshot) o;
                    IAttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_151409_a());

                    if (var6 == null) {
                        var6 = var3.func_111150_b(new RangedAttribute(var5.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    var6.setAttribute(var5.func_151410_b());
                    var6.func_142049_d();

                    for (Object o1 : var5.func_151408_c()) {
                        AttributeModifier var8 = (AttributeModifier) o1;
                        var6.applyModifier(var8);
                    }
                }
            }
        }
    }

    public NetworkManager func_147298_b()
    {
        return this.field_147302_e;
    }
}
