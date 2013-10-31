package net.minecraft.src;

import com.google.common.base.Charsets;
import net.minecraft.client.ClientBrandRetriever;
import org.lwjgl.input.Keyboard;

import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.*;

/**
 * Client-side network handler.
 */
public class NetClientHandler extends NetHandler
{
    /** True if kicked or disconnected from the server. */
    private boolean disconnected;

    /** Reference to the NetworkManager object. */
    private INetworkManager netManager;
    public String field_72560_a;

    /** Reference to the Minecraft object. */
    private Minecraft mc;
    private WorldClient worldClient;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of a player position packet,
     * reset upon respawning.
     */
    private boolean doneLoadingTerrain;
    public MapStorage mapStorage = new MapStorage((ISaveHandler)null);

    /** A HashMap of all player names and their player information objects */
    private Map playerInfoMap = new HashMap();

    /**
     * An ArrayList of GuiPlayerInfo (includes all the players' GuiPlayerInfo on the current server)
     */
    public List playerInfoList = new ArrayList();
    public int currentServerMaxPlayers = 20;
    private GuiScreen field_98183_l;

    /** RNG. */
    Random rand = new Random();

    public NetClientHandler(Minecraft minecraft, String address, int port) throws IOException
    {
        this.mc = minecraft;
        Socket var4 = new Socket(InetAddress.getByName(address), port);
        this.netManager = new TcpConnection(minecraft.getLogAgent(), var4, "Client", this);
    }

    public NetClientHandler(Minecraft minecraft, String address, int port, GuiScreen gui) throws IOException
    {
        this.mc = minecraft;
        this.field_98183_l = gui;
        Socket var5 = new Socket(InetAddress.getByName(address), port);
        this.netManager = new TcpConnection(minecraft.getLogAgent(), var5, "Client", this);
    }

    public NetClientHandler(Minecraft minecraft, IntegratedServer server) throws IOException
    {
        this.mc = minecraft;
        this.netManager = new MemoryConnection(minecraft.getLogAgent(), this);
        server.getServerListeningThread().func_71754_a((MemoryConnection)this.netManager, minecraft.getSession().getUsername());
    }

    /**
     * sets netManager and worldClient to null
     */
    public void cleanup()
    {
        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }

        this.netManager = null;
        this.worldClient = null;
    }

    /**
     * Processes the packets that have been read since the last call to this function.
     */
    public void processReadPackets()
    {
        if (!this.disconnected && this.netManager != null)
        {
            this.netManager.processReadPackets();
        }

        if (this.netManager != null)
        {
            this.netManager.wakeThreads();
        }
    }

    public void handleServerAuthData(Packet253ServerAuthData serverAuthData)
    {
        String var2 = serverAuthData.getServerId().trim();
        PublicKey var3 = serverAuthData.getPublicKey();
        SecretKey var4 = CryptManager.createNewSharedKey();

        if (!"-".equals(var2))
        {
            String var5 = (new BigInteger(CryptManager.getServerIdHash(var2, var3, var4))).toString(16);
            String var6 = this.sendSessionRequest(this.mc.getSession().getUsername(), this.mc.getSession().getSessionID(), var5);

            if (!"ok".equalsIgnoreCase(var6))
            {
                this.netManager.networkShutdown("disconnect.loginFailedInfo", var6);
                return;
            }
        }

        this.addToSendQueue(new Packet252SharedKey(var4, var3, serverAuthData.getVerifyToken()));
    }

    /**
     * Send request to http://session.minecraft.net with user's sessionId and serverId hash
     */
    private String sendSessionRequest(String username, String sessionID, String serverID)
    {
        try
        {
            URL var4 = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + urlEncode(username) + "&sessionId=" + urlEncode(sessionID) + "&serverId=" + urlEncode(serverID));
            InputStream var5 = var4.openConnection(this.mc.getProxy()).getInputStream();
            BufferedReader var6 = new BufferedReader(new InputStreamReader(var5));
            String var7 = var6.readLine();
            var6.close();
            return var7;
        }
        catch (IOException var8)
        {
            return var8.toString();
        }
    }

    /**
     * Encode the given string for insertion into a URL
     */
    private static String urlEncode(String message) throws IOException
    {
        return URLEncoder.encode(message, "UTF-8");
    }

    public void handleSharedKey(Packet252SharedKey sharedKey)
    {
        this.addToSendQueue(new Packet205ClientCommand(0));
    }

    public void handleLogin(Packet1Login packet)
    {
        this.mc.playerController = new PlayerControllerMP(this.mc, this);
        this.mc.statFileWriter.readStat(StatList.joinMultiplayerStat, 1);
        this.worldClient = new WorldClient(this, new WorldSettings(0L, packet.gameType, false, packet.hardcoreMode, packet.terrainType), packet.dimension, packet.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent());
        this.worldClient.isRemote = true;
        this.mc.loadWorld(this.worldClient);
        this.mc.thePlayer.dimension = packet.dimension;
        this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        this.mc.thePlayer.entityId = packet.clientEntityId;
        this.currentServerMaxPlayers = packet.maxPlayers;
        this.mc.playerController.setGameType(packet.gameType);
        this.mc.gameSettings.sendSettingsToServer();
        this.netManager.addToSendQueue(new Packet250CustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
    }

    public void handleVehicleSpawn(Packet23VehicleSpawn packet)
    {
        double var2 = (double)packet.xPosition / 32.0D;
        double var4 = (double)packet.yPosition / 32.0D;
        double var6 = (double)packet.zPosition / 32.0D;
        Object var8 = null;

        if (packet.type == 10)
        {
            var8 = EntityMinecart.createMinecart(this.worldClient, var2, var4, var6, packet.throwerEntityId);
        }
        else if (packet.type == 90)
        {
            Entity var9 = this.getEntityByID(packet.throwerEntityId);

            if (var9 instanceof EntityPlayer)
            {
                var8 = new EntityFishHook(this.worldClient, var2, var4, var6, (EntityPlayer)var9);
            }

            packet.throwerEntityId = 0;
        }
        else if (packet.type == 60)
        {
            var8 = new EntityArrow(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 61)
        {
            var8 = new EntitySnowball(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 71)
        {
            var8 = new EntityItemFrame(this.worldClient, (int)var2, (int)var4, (int)var6, packet.throwerEntityId);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 77)
        {
            var8 = new EntityLeashKnot(this.worldClient, (int)var2, (int)var4, (int)var6);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 65)
        {
            var8 = new EntityEnderPearl(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 72)
        {
            var8 = new EntityEnderEye(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 76)
        {
            var8 = new EntityFireworkRocket(this.worldClient, var2, var4, var6, null);
        }
        else if (packet.type == 63)
        {
            var8 = new EntityLargeFireball(this.worldClient, var2, var4, var6, (double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 64)
        {
            var8 = new EntitySmallFireball(this.worldClient, var2, var4, var6, (double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 66)
        {
            var8 = new EntityWitherSkull(this.worldClient, var2, var4, var6, (double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 62)
        {
            var8 = new EntityEgg(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 73)
        {
            var8 = new EntityPotion(this.worldClient, var2, var4, var6, packet.throwerEntityId);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 75)
        {
            var8 = new EntityExpBottle(this.worldClient, var2, var4, var6);
            packet.throwerEntityId = 0;
        }
        else if (packet.type == 1)
        {
            var8 = new EntityBoat(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 50)
        {
            var8 = new EntityTNTPrimed(this.worldClient, var2, var4, var6, null);
        }
        else if (packet.type == 51)
        {
            var8 = new EntityEnderCrystal(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 2)
        {
            var8 = new EntityItem(this.worldClient, var2, var4, var6);
        }
        else if (packet.type == 70)
        {
            var8 = new EntityFallingSand(this.worldClient, var2, var4, var6, packet.throwerEntityId & 65535, packet.throwerEntityId >> 16);
            packet.throwerEntityId = 0;
        }else{
            EntityList.createEntityByID(packet.type, this.worldClient);
        }

        if (var8 != null)
        {
            ((Entity)var8).serverPosX = packet.xPosition;
            ((Entity)var8).serverPosY = packet.yPosition;
            ((Entity)var8).serverPosZ = packet.zPosition;
            ((Entity)var8).rotationPitch = (float)(packet.pitch * 360) / 256.0F;
            ((Entity)var8).rotationYaw = (float)(packet.yaw * 360) / 256.0F;
            Entity[] var12 = ((Entity)var8).getParts();

            if (var12 != null)
            {
                int var10 = packet.entityId - ((Entity)var8).entityId;

                for (Entity aVar12 : var12) {
                    aVar12.entityId += var10;
                }
            }

            ((Entity)var8).entityId = packet.entityId;
            this.worldClient.addEntityToWorld(packet.entityId, (Entity)var8);

            if (packet.throwerEntityId > 0)
            {
                if (packet.type == 60)
                {
                    Entity var13 = this.getEntityByID(packet.throwerEntityId);

                    if (var13 instanceof EntityLivingBase)
                    {
                        EntityArrow var14 = (EntityArrow)var8;
                        var14.shootingEntity = var13;
                    }
                }

                ((Entity)var8).setVelocity((double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
            }
        }
    }

    /**
     * Handle a entity experience orb packet.
     */
    public void handleEntityExpOrb(Packet26EntityExpOrb packet)
    {
        EntityXPOrb var2 = new EntityXPOrb(this.worldClient, (double)packet.posX, (double)packet.posY, (double)packet.posZ, packet.xpValue);
        var2.serverPosX = packet.posX;
        var2.serverPosY = packet.posY;
        var2.serverPosZ = packet.posZ;
        var2.rotationYaw = 0.0F;
        var2.rotationPitch = 0.0F;
        var2.entityId = packet.entityId;
        this.worldClient.addEntityToWorld(packet.entityId, var2);
    }

    /**
     * Handles weather packet
     */
    public void handleWeather(Packet71Weather packet)
    {
        double var2 = (double)packet.posX / 32.0D;
        double var4 = (double)packet.posY / 32.0D;
        double var6 = (double)packet.posZ / 32.0D;
        EntityLightningBolt var8 = null;

        if (packet.isLightningBolt == 1)
        {
            var8 = new EntityLightningBolt(this.worldClient, var2, var4, var6);
        }

        if (var8 != null)
        {
            var8.serverPosX = packet.posX;
            var8.serverPosY = packet.posY;
            var8.serverPosZ = packet.posZ;
            var8.rotationYaw = 0.0F;
            var8.rotationPitch = 0.0F;
            var8.entityId = packet.entityID;
            this.worldClient.addWeatherEffect(var8);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityPainting(Packet25EntityPainting packet)
    {
        EntityPainting var2 = new EntityPainting(this.worldClient, packet.xPosition, packet.yPosition, packet.zPosition, packet.direction, packet.title);
        this.worldClient.addEntityToWorld(packet.entityId, var2);
    }

    /**
     * Packet handler
     */
    public void handleEntityVelocity(Packet28EntityVelocity packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            var2.setVelocity((double)packet.motionX / 8000.0D, (double)packet.motionY / 8000.0D, (double)packet.motionZ / 8000.0D);
        }
    }

    /**
     * Packet handler
     */
    public void handleEntityMetadata(Packet40EntityMetadata packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null && packet.getMetadata() != null)
        {
            var2.getDataWatcher().updateWatchedObjectsFromList(packet.getMetadata());
        }
    }

    public void handleNamedEntitySpawn(Packet20NamedEntitySpawn packet)
    {
        double var2 = (double)packet.xPosition / 32.0D;
        double var4 = (double)packet.yPosition / 32.0D;
        double var6 = (double)packet.zPosition / 32.0D;
        float var8 = (float)(packet.rotation * 360) / 256.0F;
        float var9 = (float)(packet.pitch * 360) / 256.0F;
        EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.mc.theWorld, packet.name);
        var10.prevPosX = var10.lastTickPosX = (double)(var10.serverPosX = packet.xPosition);
        var10.prevPosY = var10.lastTickPosY = (double)(var10.serverPosY = packet.yPosition);
        var10.prevPosZ = var10.lastTickPosZ = (double)(var10.serverPosZ = packet.zPosition);
        int var11 = packet.currentItem;

        if (var11 == 0)
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = null;
        }
        else
        {
            var10.inventory.mainInventory[var10.inventory.currentItem] = new ItemStack(var11, 1, 0);
        }

        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        this.worldClient.addEntityToWorld(packet.entityId, var10);
        List var12 = packet.getWatchedMetadata();

        if (var12 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var12);
        }
    }

    public void handleEntityTeleport(Packet34EntityTeleport packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            var2.serverPosX = packet.xPosition;
            var2.serverPosY = packet.yPosition;
            var2.serverPosZ = packet.zPosition;
            double var3 = (double)var2.serverPosX / 32.0D;
            double var5 = (double)var2.serverPosY / 32.0D + 0.015625D;
            double var7 = (double)var2.serverPosZ / 32.0D;
            float var9 = (float)(packet.yaw * 360) / 256.0F;
            float var10 = (float)(packet.pitch * 360) / 256.0F;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    public void handleBlockItemSwitch(Packet16BlockItemSwitch packet)
    {
        if (packet.id >= 0 && packet.id < InventoryPlayer.getHotbarSize())
        {
            this.mc.thePlayer.inventory.currentItem = packet.id;
        }
    }

    public void handleEntity(Packet30Entity packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            var2.serverPosX += packet.xPosition;
            var2.serverPosY += packet.yPosition;
            var2.serverPosZ += packet.zPosition;
            double var3 = (double)var2.serverPosX / 32.0D;
            double var5 = (double)var2.serverPosY / 32.0D;
            double var7 = (double)var2.serverPosZ / 32.0D;
            float var9 = packet.rotating ? (float)(packet.yaw * 360) / 256.0F : var2.rotationYaw;
            float var10 = packet.rotating ? (float)(packet.pitch * 360) / 256.0F : var2.rotationPitch;
            var2.setPositionAndRotation2(var3, var5, var7, var9, var10, 3);
        }
    }

    public void handleEntityHeadRotation(Packet35EntityHeadRotation packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            float var3 = (float)(packet.headRotationYaw * 360) / 256.0F;
            var2.setRotationYawHead(var3);
        }
    }

    public void handleDestroyEntity(Packet29DestroyEntity packet)
    {
        for (int var2 = 0; var2 < packet.entityId.length; ++var2)
        {
            this.worldClient.removeEntityFromWorld(packet.entityId[var2]);
        }
    }

    public void handleFlying(Packet10Flying packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;
        double var3 = var2.posX;
        double var5 = var2.posY;
        double var7 = var2.posZ;
        float var9 = var2.rotationYaw;
        float var10 = var2.rotationPitch;

        if (packet.moving)
        {
            var3 = packet.xPosition;
            var5 = packet.yPosition;
            var7 = packet.zPosition;
        }

        if (packet.rotating)
        {
            var9 = packet.yaw;
            var10 = packet.pitch;
        }

        var2.ySize = 0.0F;
        var2.motionX = var2.motionY = var2.motionZ = 0.0D;
        var2.setPositionAndRotation(var3, var5, var7, var9, var10);
        packet.xPosition = var2.posX;
        packet.yPosition = var2.boundingBox.minY;
        packet.zPosition = var2.posZ;
        packet.stance = var2.posY;
        this.netManager.addToSendQueue(packet);

        if (!this.doneLoadingTerrain)
        {
            this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
            this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
            this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.mc.displayGuiScreen(null);
        }
    }

    public void handleMultiBlockChange(Packet52MultiBlockChange packet)
    {
        int var2 = packet.xPosition * 16;
        int var3 = packet.zPosition * 16;

        if (packet.metadataArray != null)
        {
            DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(packet.metadataArray));

            try
            {
                for (int var5 = 0; var5 < packet.size; ++var5)
                {
                    short var6 = var4.readShort();
                    short var7 = var4.readShort();
                    int var8 = var7 >> 4 & 4095;
                    int var9 = var7 & 15;
                    int var10 = var6 >> 12 & 15;
                    int var11 = var6 >> 8 & 15;
                    int var12 = var6 & 255;
                    this.worldClient.setBlockAndMetadataAndInvalidate(var10 + var2, var12, var11 + var3, var8, var9);
                }
            }
            catch (IOException ignored){

            }
        }
    }

    /**
     * Handle Packet51MapChunk (full chunk update of blocks, metadata, light levels, and optionally biome data)
     */
    public void handleMapChunk(Packet51MapChunk packet)
    {
        if (packet.includeInitialize)
        {
            if (packet.yChMin == 0)
            {
                this.worldClient.doPreChunk(packet.xCh, packet.zCh, false);
                return;
            }

            this.worldClient.doPreChunk(packet.xCh, packet.zCh, true);
        }

        this.worldClient.invalidateBlockReceiveRegion(packet.xCh << 4, 0, packet.zCh << 4, (packet.xCh << 4) + 15, 256, (packet.zCh << 4) + 15);
        Chunk var2 = this.worldClient.getChunkFromChunkCoords(packet.xCh, packet.zCh);

        if (packet.includeInitialize && var2 == null)
        {
            this.worldClient.doPreChunk(packet.xCh, packet.zCh, true);
            var2 = this.worldClient.getChunkFromChunkCoords(packet.xCh, packet.zCh);
        }

        if (var2 != null)
        {
            var2.fillChunk(packet.getCompressedChunkData(), packet.yChMin, packet.yChMax, packet.includeInitialize);
            this.worldClient.markBlockRangeForRenderUpdate(packet.xCh << 4, 0, packet.zCh << 4, (packet.xCh << 4) + 15, 256, (packet.zCh << 4) + 15);

            if (!packet.includeInitialize || !(this.worldClient.provider instanceof WorldProviderSurface))
            {
                var2.resetRelightChecks();
            }
        }
    }

    public void handleBlockChange(Packet53BlockChange packet)
    {
        this.worldClient.setBlockAndMetadataAndInvalidate(packet.xPosition, packet.yPosition, packet.zPosition, packet.type, packet.metadata);
    }

    public void handleKickDisconnect(Packet255KickDisconnect packet)
    {
        this.netManager.networkShutdown("disconnect.kicked");
        this.disconnected = true;
        this.mc.loadWorld(null);

        if (this.field_98183_l != null)
        {
            this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.disconnected", "disconnect.genericReason", packet.reason));
        }
        else
        {
            this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.disconnected", "disconnect.genericReason", packet.reason));
        }
    }

    public void handleErrorMessage(String errorMessage, Object[] extendedData)
    {
        if (!this.disconnected)
        {
            this.disconnected = true;
            this.mc.loadWorld(null);

            if (this.field_98183_l != null)
            {
                this.mc.displayGuiScreen(new GuiScreenDisconnectedOnline(this.field_98183_l, "disconnect.lost", errorMessage, extendedData));
            }
            else
            {
                this.mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", errorMessage, extendedData));
            }
        }
    }

    public void quitWithPacket(Packet packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(packet);
            this.netManager.serverShutdown();
        }
    }

    /**
     * Adds the packet to the send queue
     */
    public void addToSendQueue(Packet packet)
    {
        if (!this.disconnected)
        {
            this.netManager.addToSendQueue(packet);
        }
    }

    public void handleCollect(Packet22Collect packet)
    {
        Entity var2 = this.getEntityByID(packet.collectedEntityId);
        Object var3 = this.getEntityByID(packet.collectorEntityId);

        if (var3 == null)
        {
            var3 = this.mc.thePlayer;
        }

        if (var2 != null)
        {
            if (var2 instanceof EntityXPOrb)
            {
                this.worldClient.playSoundAtEntity(var2, "random.orb", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.worldClient.playSoundAtEntity(var2, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var2, (Entity)var3, -0.5F));
            this.worldClient.removeEntityFromWorld(packet.collectedEntityId);
        }
    }

    public void handleChat(Packet3Chat packet)
    {
        this.mc.ingameGUI.getChatGUI().printChatMessage(ChatMessageComponent.createFromJson(packet.message).toStringWithFormatting(true));
    }

    public void handleAnimation(Packet18Animation packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            if (packet.animate == 1)
            {
                EntityLivingBase var3 = (EntityLivingBase)var2;
                var3.swingItem();
            }
            else if (packet.animate == 2)
            {
                var2.performHurtAnimation();
            }
            else if (packet.animate == 3)
            {
                EntityPlayer var4 = (EntityPlayer)var2;
                var4.wakeUpPlayer(false, false, false);
            }
            else if (packet.animate != 4)
            {
                if (packet.animate == 6)
                {
                    this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, var2));
                }
                else if (packet.animate == 7)
                {
                    EntityCrit2FX var5 = new EntityCrit2FX(this.mc.theWorld, var2, "magicCrit");
                    this.mc.effectRenderer.addEffect(var5);
                }
            }
        }
    }

    public void handleSleep(Packet17Sleep packet)
    {
        Entity var2 = this.getEntityByID(packet.entityID);

        if (var2 != null)
        {
            if (packet.field_73622_e == 0)
            {
                EntityPlayer var3 = (EntityPlayer)var2;
                var3.sleepInBedAt(packet.bedX, packet.bedY, packet.bedZ);
            }
        }
    }

    /**
     * Disconnects the network connection.
     */
    public void disconnect()
    {
        this.disconnected = true;
        this.netManager.wakeThreads();
        this.netManager.networkShutdown("disconnect.closed");
    }

    public void handleMobSpawn(Packet24MobSpawn packet)
    {
        double var2 = (double)packet.xPosition / 32.0D;
        double var4 = (double)packet.yPosition / 32.0D;
        double var6 = (double)packet.zPosition / 32.0D;
        float var8 = (float)(packet.yaw * 360) / 256.0F;
        float var9 = (float)(packet.pitch * 360) / 256.0F;
        EntityLivingBase var10 = (EntityLivingBase)EntityList.createEntityByID(packet.type, this.mc.theWorld);
        var10.serverPosX = packet.xPosition;
        var10.serverPosY = packet.yPosition;
        var10.serverPosZ = packet.zPosition;
        var10.rotationYawHead = (float)(packet.headYaw * 360) / 256.0F;
        Entity[] var11 = var10.getParts();

        if (var11 != null)
        {
            int var12 = packet.entityId - var10.entityId;

            for (Entity aVar11 : var11) {
                aVar11.entityId += var12;
            }
        }

        var10.entityId = packet.entityId;
        var10.setPositionAndRotation(var2, var4, var6, var8, var9);
        var10.motionX = (double)((float)packet.velocityX / 8000.0F);
        var10.motionY = (double)((float)packet.velocityY / 8000.0F);
        var10.motionZ = (double)((float)packet.velocityZ / 8000.0F);
        this.worldClient.addEntityToWorld(packet.entityId, var10);
        List var14 = packet.getMetadata();

        if (var14 != null)
        {
            var10.getDataWatcher().updateWatchedObjectsFromList(var14);
        }
    }

    public void handleUpdateTime(Packet4UpdateTime packet)
    {
        this.mc.theWorld.func_82738_a(packet.worldAge);
        this.mc.theWorld.setWorldTime(packet.time);
    }

    public void handleSpawnPosition(Packet6SpawnPosition packet)
    {
        this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(packet.xPosition, packet.yPosition, packet.zPosition), true);
        this.mc.theWorld.getWorldInfo().setSpawnPosition(packet.xPosition, packet.yPosition, packet.zPosition);
    }

    /**
     * Packet handler
     */
    public void handleAttachEntity(Packet39AttachEntity packet)
    {
        Object var2 = this.getEntityByID(packet.ridingEntityId);
        Entity var3 = this.getEntityByID(packet.vehicleEntityId);

        if (packet.attachState == 0)
        {
            boolean var4 = false;

            if (packet.ridingEntityId == this.mc.thePlayer.entityId)
            {
                var2 = this.mc.thePlayer;

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
                GameSettings var5 = this.mc.gameSettings;
                this.mc.ingameGUI.func_110326_a(I18n.getStringParams("mount.onboard", GameSettings.getKeyDisplayString(var5.keyBindSneak.keyCode)), false);
            }
        }
        else if (packet.attachState == 1 && var2 != null && var2 instanceof EntityLiving)
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

    /**
     * Packet handler
     */
    public void handleEntityStatus(Packet38EntityStatus packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 != null)
        {
            var2.handleHealthUpdate(packet.entityStatus);
        }
    }

    private Entity getEntityByID(int entityID)
    {
        return entityID == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(entityID);
    }

    /**
     * Recieves player health from the server and then proceeds to set it locally on the client.
     */
    public void handleUpdateHealth(Packet8UpdateHealth packet)
    {
        this.mc.thePlayer.setPlayerSPHealth(packet.healthMP);
        this.mc.thePlayer.getFoodStats().setFoodLevel(packet.food);
        this.mc.thePlayer.getFoodStats().setFoodSaturationLevel(packet.foodSaturation);
    }

    /**
     * Handle an experience packet.
     */
    public void handleExperience(Packet43Experience packet)
    {
        this.mc.thePlayer.setXPStats(packet.experience, packet.experienceTotal, packet.experienceLevel);
    }

    /**
     * respawns the player
     */
    public void handleRespawn(Packet9Respawn packet)
    {
        if (packet.respawnDimension != this.mc.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard var2 = this.worldClient.getScoreboard();
            this.worldClient = new WorldClient(this, new WorldSettings(0L, packet.gameType, false, this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled(), packet.terrainType), packet.respawnDimension, packet.difficulty, this.mc.mcProfiler, this.mc.getLogAgent());
            this.worldClient.func_96443_a(var2);
            this.worldClient.isRemote = true;
            this.mc.loadWorld(this.worldClient);
            this.mc.thePlayer.dimension = packet.respawnDimension;
            this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.mc.setDimensionAndSpawnPlayer(packet.respawnDimension);
        this.mc.playerController.setGameType(packet.gameType);
    }

    public void handleExplosion(Packet60Explosion packet)
    {
        Explosion var2 = new Explosion(this.mc.theWorld, null, packet.explosionX, packet.explosionY, packet.explosionZ, packet.explosionSize);
        var2.affectedBlockPositions = packet.chunkPositionRecords;
        var2.doExplosionB(true);
        this.mc.thePlayer.motionX += (double)packet.getPlayerVelocityX();
        this.mc.thePlayer.motionY += (double)packet.getPlayerVelocityY();
        this.mc.thePlayer.motionZ += (double)packet.getPlayerVelocityZ();
    }

    public void handleOpenWindow(Packet100OpenWindow packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;

        switch (packet.inventoryType)
        {
            case 0:
                var2.displayGUIChest(new InventoryBasic(packet.windowTitle, packet.useProvidedWindowTitle, packet.slotsCount));
                var2.openContainer.windowId = packet.windowId;
                break;

            case 1:
                var2.displayGUIWorkbench(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = packet.windowId;
                break;

            case 2:
                TileEntityFurnace var4 = new TileEntityFurnace();

                if (packet.useProvidedWindowTitle)
                {
                    var4.setGuiDisplayName(packet.windowTitle);
                }

                var2.displayGUIFurnace(var4);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 3:
                TileEntityDispenser var7 = new TileEntityDispenser();

                if (packet.useProvidedWindowTitle)
                {
                    var7.setCustomName(packet.windowTitle);
                }

                var2.displayGUIDispenser(var7);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 4:
                var2.displayGUIEnchantment(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ), packet.useProvidedWindowTitle ? packet.windowTitle : null);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 5:
                TileEntityBrewingStand var5 = new TileEntityBrewingStand();

                if (packet.useProvidedWindowTitle)
                {
                    var5.func_94131_a(packet.windowTitle);
                }

                var2.displayGUIBrewingStand(var5);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 6:
                var2.displayGUIMerchant(new NpcMerchant(var2), packet.useProvidedWindowTitle ? packet.windowTitle : null);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 7:
                TileEntityBeacon var8 = new TileEntityBeacon();
                var2.displayGUIBeacon(var8);

                if (packet.useProvidedWindowTitle)
                {
                    var8.func_94047_a(packet.windowTitle);
                }

                var2.openContainer.windowId = packet.windowId;
                break;

            case 8:
                var2.displayGUIAnvil(MathHelper.floor_double(var2.posX), MathHelper.floor_double(var2.posY), MathHelper.floor_double(var2.posZ));
                var2.openContainer.windowId = packet.windowId;
                break;

            case 9:
                TileEntityHopper var3 = new TileEntityHopper();

                if (packet.useProvidedWindowTitle)
                {
                    var3.setInventoryName(packet.windowTitle);
                }

                var2.displayGUIHopper(var3);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 10:
                TileEntityDropper var6 = new TileEntityDropper();

                if (packet.useProvidedWindowTitle)
                {
                    var6.setCustomName(packet.windowTitle);
                }

                var2.displayGUIDispenser(var6);
                var2.openContainer.windowId = packet.windowId;
                break;

            case 11:
                Entity var9 = this.getEntityByID(packet.field_111008_f);

                if (var9 != null && var9 instanceof EntityHorse)
                {
                    var2.displayGUIHorse((EntityHorse)var9, new AnimalChest(packet.windowTitle, packet.useProvidedWindowTitle, packet.slotsCount));
                    var2.openContainer.windowId = packet.windowId;
                }
        }
    }

    public void handleSetSlot(Packet103SetSlot packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;

        if (packet.windowId == -1)
        {
            var2.inventory.setItemStack(packet.myItemStack);
        }
        else
        {
            boolean var3 = false;

            if (this.mc.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative var4 = (GuiContainerCreative)this.mc.currentScreen;
                var3 = var4.getCurrentTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (packet.windowId == 0 && packet.itemSlot >= 36 && packet.itemSlot < 45)
            {
                ItemStack var5 = var2.inventoryContainer.getSlot(packet.itemSlot).getStack();

                if (packet.myItemStack != null && (var5 == null || var5.stackSize < packet.myItemStack.stackSize))
                {
                    packet.myItemStack.animationsToGo = 5;
                }

                var2.inventoryContainer.putStackInSlot(packet.itemSlot, packet.myItemStack);
            }
            else if (packet.windowId == var2.openContainer.windowId && (packet.windowId != 0 || !var3))
            {
                var2.openContainer.putStackInSlot(packet.itemSlot, packet.myItemStack);
            }
        }
    }

    public void handleTransaction(Packet106Transaction packet)
    {
        Container var2 = null;
        EntityClientPlayerMP var3 = this.mc.thePlayer;

        if (packet.windowId == 0)
        {
            var2 = var3.inventoryContainer;
        }
        else if (packet.windowId == var3.openContainer.windowId)
        {
            var2 = var3.openContainer;
        }

        if (var2 != null && !packet.accepted)
        {
            this.addToSendQueue(new Packet106Transaction(packet.windowId, packet.shortWindowId, true));
        }
    }

    public void handleWindowItems(Packet104WindowItems packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;

        if (packet.windowId == 0)
        {
            var2.inventoryContainer.putStacksInSlots(packet.itemStack);
        }
        else if (packet.windowId == var2.openContainer.windowId)
        {
            var2.openContainer.putStacksInSlots(packet.itemStack);
        }
    }

    public void func_142031_a(Packet133TileEditorOpen packet)
    {
        TileEntity var2 = this.worldClient.getBlockTileEntity(packet.field_142035_b, packet.field_142036_c, packet.field_142034_d);

        if (var2 != null)
        {
            this.mc.thePlayer.displayGUIEditSign(var2);
        }
        else if (packet.field_142037_a == 0)
        {
            TileEntitySign var3 = new TileEntitySign();
            var3.setWorldObj(this.worldClient);
            var3.xCoord = packet.field_142035_b;
            var3.yCoord = packet.field_142036_c;
            var3.zCoord = packet.field_142034_d;
            this.mc.thePlayer.displayGUIEditSign(var3);
        }
    }

    /**
     * Updates Client side signs
     */
    public void handleUpdateSign(Packet130UpdateSign packet)
    {
        boolean var2 = false;

        if (this.mc.theWorld.blockExists(packet.xPosition, packet.yPosition, packet.zPosition))
        {
            TileEntity var3 = this.mc.theWorld.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);

            if (var3 instanceof TileEntitySign)
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (var4.isEditable())
                {
                    System.arraycopy(packet.signLines, 0, var4.signText, 0, 4);

                    var4.onInventoryChanged();
                }

                var2 = true;
            }
        }

        if (!var2 && this.mc.thePlayer != null)
        {
            this.mc.thePlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Unable to locate sign at " + packet.xPosition + ", " + packet.yPosition + ", " + packet.zPosition));
        }
    }

    public void handleTileEntityData(Packet132TileEntityData packet)
    {
        if (this.mc.theWorld.blockExists(packet.xPosition, packet.yPosition, packet.zPosition))
        {
            TileEntity var2 = this.mc.theWorld.getBlockTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);

            if (var2 != null)
            {
                if (packet.actionType == 1 && var2 instanceof TileEntityMobSpawner)
                {
                    var2.readFromNBT(packet.data);
                }
                else if (packet.actionType == 2 && var2 instanceof TileEntityCommandBlock)
                {
                    var2.readFromNBT(packet.data);
                }
                else if (packet.actionType == 3 && var2 instanceof TileEntityBeacon)
                {
                    var2.readFromNBT(packet.data);
                }
                else if (packet.actionType == 4 && var2 instanceof TileEntitySkull)
                {
                    var2.readFromNBT(packet.data);
                }
            }
        }
    }

    public void handleUpdateProgressbar(Packet105UpdateProgressbar packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;
        this.unexpectedPacket(packet);

        if (var2.openContainer != null && var2.openContainer.windowId == packet.windowId)
        {
            var2.openContainer.updateProgressBar(packet.progressBar, packet.progressBarValue);
        }
    }

    public void handlePlayerInventory(Packet5PlayerInventory packet)
    {
        Entity var2 = this.getEntityByID(packet.entityID);

        if (var2 != null)
        {
            var2.setCurrentItemOrArmor(packet.slot, packet.getItemSlot());
        }
    }

    public void handleCloseWindow(Packet101CloseWindow packet)
    {
        this.mc.thePlayer.func_92015_f();
    }

    public void handleBlockEvent(Packet54PlayNoteBlock packet)
    {
        this.mc.theWorld.addBlockEvent(packet.xLocation, packet.yLocation, packet.zLocation, packet.blockId, packet.instrumentType, packet.pitch);
    }

    public void handleBlockDestroy(Packet55BlockDestroy packet)
    {
        this.mc.theWorld.destroyBlockInWorldPartially(packet.getEntityId(), packet.getPosX(), packet.getPosY(), packet.getPosZ(), packet.getDestroyedStage());
    }

    public void handleMapChunks(Packet56MapChunks packet)
    {
        for (int var2 = 0; var2 < packet.getNumberOfChunkInPacket(); ++var2)
        {
            int var3 = packet.getChunkPosX(var2);
            int var4 = packet.getChunkPosZ(var2);
            this.worldClient.doPreChunk(var3, var4, true);
            this.worldClient.invalidateBlockReceiveRegion(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
            Chunk var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);

            if (var5 == null)
            {
                this.worldClient.doPreChunk(var3, var4, true);
                var5 = this.worldClient.getChunkFromChunkCoords(var3, var4);
            }

            if (var5 != null)
            {
                var5.fillChunk(packet.getChunkCompressedData(var2), packet.field_73590_a[var2], packet.field_73588_b[var2], true);
                this.worldClient.markBlockRangeForRenderUpdate(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);

                if (!(this.worldClient.provider instanceof WorldProviderSurface))
                {
                    var5.resetRelightChecks();
                }
            }
        }
    }

    /**
     * If this returns false, all packets will be queued for the main thread to handle, even if they would otherwise be
     * processed asynchronously. Used to avoid processing packets on the client before the world has been downloaded
     * (which happens on the main thread)
     */
    public boolean canProcessPacketsAsync()
    {
        return this.mc != null && this.mc.theWorld != null && this.mc.thePlayer != null && this.worldClient != null;
    }

    public void handleGameEvent(Packet70GameEvent packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;
        int var3 = packet.eventType;
        int var4 = packet.gameMode;

        if (var3 >= 0 && var3 < Packet70GameEvent.clientMessage.length && Packet70GameEvent.clientMessage[var3] != null)
        {
            var2.addChatMessage(Packet70GameEvent.clientMessage[var3]);
        }

        if (var3 == 1)
        {
            this.worldClient.getWorldInfo().setRaining(true);
            this.worldClient.setRainStrength(0.0F);
        }
        else if (var3 == 2)
        {
            this.worldClient.getWorldInfo().setRaining(false);
            this.worldClient.setRainStrength(1.0F);
        }
        else if (var3 == 3)
        {
            this.mc.playerController.setGameType(EnumGameType.getByID(var4));
        }
        else if (var3 == 4)
        {
            this.mc.displayGuiScreen(new GuiWinGame());
        }
        else if (var3 == 5)
        {
            GameSettings var5 = this.mc.gameSettings;

            if (var4 == 0)
            {
                this.mc.displayGuiScreen(new GuiScreenDemo());
            }
            else if (var4 == 101)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.movement", Keyboard.getKeyName(var5.keyBindForward.keyCode), Keyboard.getKeyName(var5.keyBindLeft.keyCode), Keyboard.getKeyName(var5.keyBindBack.keyCode), Keyboard.getKeyName(var5.keyBindRight.keyCode));
            }
            else if (var4 == 102)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.jump", Keyboard.getKeyName(var5.keyBindJump.keyCode));
            }
            else if (var4 == 103)
            {
                this.mc.ingameGUI.getChatGUI().addTranslatedMessage("demo.help.inventory", Keyboard.getKeyName(var5.keyBindInventory.keyCode));
            }
        }
        else if (var3 == 6)
        {
            this.worldClient.playSound(var2.posX, var2.posY + (double)var2.getEyeHeight(), var2.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
    }

    /**
     * Contains logic for handling packets containing arbitrary unique item data. Currently this is only for maps.
     */
    public void handleMapData(Packet131MapData packet)
    {
        if (packet.itemID == Item.map.itemID)
        {
            ItemMap.getMPMapData(packet.uniqueID, this.mc.theWorld).updateMPMapData(packet.itemData);
        }
        else
        {
            this.mc.getLogAgent().logWarning("Unknown itemid: " + packet.uniqueID);
        }
    }

    public void handleDoorChange(Packet61DoorChange packet)
    {
        if (packet.getRelativeVolumeDisabled())
        {
            this.mc.theWorld.func_82739_e(packet.sfxID, packet.posX, packet.posY, packet.posZ, packet.auxData);
        }
        else
        {
            this.mc.theWorld.playAuxSFX(packet.sfxID, packet.posX, packet.posY, packet.posZ, packet.auxData);
        }
    }

    /**
     * Increment player statistics
     */
    public void handleStatistic(Packet200Statistic packet)
    {
        this.mc.thePlayer.incrementStat(StatList.getOneShotStat(packet.statisticId), packet.amount);
    }

    /**
     * Handle an entity effect packet.
     */
    public void handleEntityEffect(Packet41EntityEffect packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 instanceof EntityLivingBase)
        {
            PotionEffect var3 = new PotionEffect(packet.effectId, packet.duration, packet.effectAmplifier);
            var3.setPotionDurationMax(packet.isDurationMax());
            ((EntityLivingBase)var2).addPotionEffect(var3);
        }
    }

    /**
     * Handle a remove entity effect packet.
     */
    public void handleRemoveEntityEffect(Packet42RemoveEntityEffect packet)
    {
        Entity var2 = this.getEntityByID(packet.entityId);

        if (var2 instanceof EntityLivingBase)
        {
            ((EntityLivingBase)var2).removePotionEffectClient(packet.effectId);
        }
    }

    /**
     * determine if it is a server handler
     */
    public boolean isServerHandler()
    {
        return false;
    }

    /**
     * Handle a player information packet.
     */
    public void handlePlayerInfo(Packet201PlayerInfo packet)
    {
        GuiPlayerInfo var2 = (GuiPlayerInfo)this.playerInfoMap.get(packet.playerName);

        if (var2 == null && packet.isConnected)
        {
            var2 = new GuiPlayerInfo(packet.playerName);
            this.playerInfoMap.put(packet.playerName, var2);
            this.playerInfoList.add(var2);
        }

        if (var2 != null && !packet.isConnected)
        {
            this.playerInfoMap.remove(packet.playerName);
            this.playerInfoList.remove(var2);
        }

        if (packet.isConnected && var2 != null)
        {
            var2.responseTime = packet.ping;
        }
    }

    /**
     * Handle a keep alive packet.
     */
    public void handleKeepAlive(Packet0KeepAlive packet)
    {
        this.addToSendQueue(new Packet0KeepAlive(packet.randomId));
    }

    /**
     * Handle a player abilities packet.
     */
    public void handlePlayerAbilities(Packet202PlayerAbilities packet)
    {
        EntityClientPlayerMP var2 = this.mc.thePlayer;
        var2.capabilities.isFlying = packet.getFlying();
        var2.capabilities.isCreativeMode = packet.isCreativeMode();
        var2.capabilities.disableDamage = packet.getDisableDamage();
        var2.capabilities.allowFlying = packet.getAllowFlying();
        var2.capabilities.setFlySpeed(packet.getFlySpeed());
        var2.capabilities.setPlayerWalkSpeed(packet.getWalkSpeed());
    }

    public void handleAutoComplete(Packet203AutoComplete packet)
    {
        String[] var2 = packet.getText().split("\u0000");

        if (this.mc.currentScreen instanceof GuiChat)
        {
            GuiChat var3 = (GuiChat)this.mc.currentScreen;
            var3.func_73894_a(var2);
        }
    }

    public void handleLevelSound(Packet62LevelSound packet)
    {
        this.mc.theWorld.playSound(packet.getEffectX(), packet.getEffectY(), packet.getEffectZ(), packet.getSoundName(), packet.getVolume(), packet.getPitch(), false);
    }

    public void handleCustomPayload(Packet250CustomPayload packet)
    {
        if ("MC|TrList".equals(packet.channel))
        {
            DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(packet.data));

            try
            {
                int var3 = var2.readInt();
                GuiScreen var4 = this.mc.currentScreen;

                if (var4 != null && var4 instanceof GuiMerchant && var3 == this.mc.thePlayer.openContainer.windowId)
                {
                    IMerchant var5 = ((GuiMerchant)var4).getIMerchant();
                    MerchantRecipeList var6 = MerchantRecipeList.readRecipiesFromStream(var2);
                    var5.setRecipes(var6);
                }
            }
            catch (IOException var7)
            {
                var7.printStackTrace();
            }
        }
        else if ("MC|Brand".equals(packet.channel))
        {
            this.mc.thePlayer.func_142020_c(new String(packet.data, Charsets.UTF_8));
        }
    }

    /**
     * Handle a set objective packet.
     */
    public void handleSetObjective(Packet206SetObjective packet)
    {
        Scoreboard var2 = this.worldClient.getScoreboard();
        ScoreObjective var3;

        if (packet.change == 0)
        {
            var3 = var2.func_96535_a(packet.objectiveName, ScoreObjectiveCriteria.field_96641_b);
            var3.setDisplayName(packet.objectiveDisplayName);
        }
        else
        {
            var3 = var2.getObjective(packet.objectiveName);

            if (packet.change == 1)
            {
                var2.func_96519_k(var3);
            }
            else if (packet.change == 2)
            {
                var3.setDisplayName(packet.objectiveDisplayName);
            }
        }
    }

    /**
     * Handle a set score packet.
     */
    public void handleSetScore(Packet207SetScore packet)
    {
        Scoreboard var2 = this.worldClient.getScoreboard();
        ScoreObjective var3 = var2.getObjective(packet.scoreName);

        if (packet.updateOrRemove == 0)
        {
            Score var4 = var2.func_96529_a(packet.itemName, var3);
            var4.func_96647_c(packet.value);
        }
        else if (packet.updateOrRemove == 1)
        {
            var2.func_96515_c(packet.itemName);
        }
    }

    /**
     * Handle a set display objective packet.
     */
    public void handleSetDisplayObjective(Packet208SetDisplayObjective packet)
    {
        Scoreboard var2 = this.worldClient.getScoreboard();

        if (packet.scoreName.length() == 0)
        {
            var2.func_96530_a(packet.scoreboardPosition, null);
        }
        else
        {
            ScoreObjective var3 = var2.getObjective(packet.scoreName);
            var2.func_96530_a(packet.scoreboardPosition, var3);
        }
    }

    /**
     * Handle a set player team packet.
     */
    public void handleSetPlayerTeam(Packet209SetPlayerTeam packet)
    {
        Scoreboard var2 = this.worldClient.getScoreboard();
        ScorePlayerTeam var3;

        if (packet.mode == 0)
        {
            var3 = var2.func_96527_f(packet.teamName);
        }
        else
        {
            var3 = var2.func_96508_e(packet.teamName);
        }

        if (packet.mode == 0 || packet.mode == 2)
        {
            var3.func_96664_a(packet.teamDisplayName);
            var3.func_96666_b(packet.teamPrefix);
            var3.func_96662_c(packet.teamSuffix);
            var3.func_98298_a(packet.friendlyFire);
        }

        Iterator var4;
        String var5;

        if (packet.mode == 0 || packet.mode == 3)
        {
            var4 = packet.playerNames.iterator();

            while (var4.hasNext())
            {
                var5 = (String)var4.next();
                var2.func_96521_a(var5, var3);
            }
        }

        if (packet.mode == 4)
        {
            var4 = packet.playerNames.iterator();

            while (var4.hasNext())
            {
                var5 = (String)var4.next();
                var2.removePlayerFromTeam(var5, var3);
            }
        }

        if (packet.mode == 1)
        {
            var2.func_96511_d(var3);
        }
    }

    /**
     * Handle a world particles packet.
     */
    public void handleWorldParticles(Packet63WorldParticles packet)
    {
        for (int var2 = 0; var2 < packet.getQuantity(); ++var2)
        {
            double var3 = this.rand.nextGaussian() * (double)packet.getOffsetX();
            double var5 = this.rand.nextGaussian() * (double)packet.getOffsetY();
            double var7 = this.rand.nextGaussian() * (double)packet.getOffsetZ();
            double var9 = this.rand.nextGaussian() * (double)packet.getSpeed();
            double var11 = this.rand.nextGaussian() * (double)packet.getSpeed();
            double var13 = this.rand.nextGaussian() * (double)packet.getSpeed();
            this.worldClient.spawnParticle(packet.getParticleName(), packet.getPositionX() + var3, packet.getPositionY() + var5, packet.getPositionZ() + var7, var9, var11, var13);
        }
    }

    public void func_110773_a(Packet44UpdateAttributes packet)
    {
        Entity var2 = this.getEntityByID(packet.func_111002_d());

        if (var2 != null)
        {
            if (!(var2 instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
            }
            else
            {
                BaseAttributeMap var3 = ((EntityLivingBase)var2).getAttributeMap();

                for (Object o : packet.func_111003_f()) {
                    Packet44UpdateAttributesSnapshot var5 = (Packet44UpdateAttributesSnapshot) o;
                    AttributeInstance var6 = var3.getAttributeInstanceByName(var5.func_142040_a());

                    if (var6 == null) {
                        var6 = var3.func_111150_b(new RangedAttribute(var5.func_142040_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    var6.setAttribute(var5.func_142041_b());
                    var6.func_142049_d();

                    for (Object o1 : var5.func_142039_c()) {
                        AttributeModifier var8 = (AttributeModifier) o1;
                        var6.applyModifier(var8);
                    }
                }
            }
        }
    }

    /**
     * Return the NetworkManager instance used by this NetClientHandler
     */
    public INetworkManager getNetManager()
    {
        return this.netManager;
    }
}
