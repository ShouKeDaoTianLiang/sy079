package handling.netty;

import java.net.SocketAddress;
import io.netty.channel.ChannelFuture;
import io.netty.channel.Channel;

public class MapleSession
{
    private Channel channel;
    
    public MapleSession(final Channel session) {
        this.channel = session;
    }
    
    public ChannelFuture write(final Object o) {
        return this.channel.writeAndFlush(o);
    }
    
    public void close() {
        this.channel.close();
    }
    
    public SocketAddress getRemoteAddress() {
        return this.channel.remoteAddress();
    }
    
    public boolean isActive() {
        return this.channel.isActive();
    }
    
    public boolean isOpen() {
        return this.channel.isOpen();
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }
}
