package net.thegaminghuskymc.sandboxgame.network.play.server;

import io.netty.buffer.ByteBuf;
import net.thegaminghuskymc.sandboxgame.network.Packet;
import net.thegaminghuskymc.sandboxgame.network.PacketBuffer;
import net.thegaminghuskymc.sandboxgame.network.play.INetHandlerPlayClient;
import net.thegaminghuskymc.sgf.fml.relauncher.Side;
import net.thegaminghuskymc.sgf.fml.relauncher.SideOnly;

import java.io.IOException;

public class SPacketCustomPayload implements Packet<INetHandlerPlayClient>
{
    private String channel;
    private PacketBuffer data;

    public SPacketCustomPayload()
    {
    }

    public SPacketCustomPayload(String channelIn, PacketBuffer bufIn)
    {
        this.channel = channelIn;
        this.data = bufIn;

        if (bufIn.writerIndex() > 1048576)
        {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.channel = buf.readString(20);
        int i = buf.readableBytes();

        if (i >= 0 && i <= 1048576)
        {
            this.data = new PacketBuffer(buf.readBytes(i));
        }
        else
        {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeString(this.channel);
        synchronized(this.data) { //This may be access multiple times, from multiple threads, lets be safe.
        this.data.markReaderIndex();
        buf.writeBytes((ByteBuf)this.data);
        this.data.resetReaderIndex();
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleCustomPayload(this);
        // Forge: fix network buffer leaks (MC-121884)
        if (this.data != null) this.data.release();
    }

    @SideOnly(Side.CLIENT)
    public String getChannelName()
    {
        return this.channel;
    }

    @SideOnly(Side.CLIENT)
    public PacketBuffer getBufferData()
    {
        return this.data;
    }
}