package handling.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import handling.MapleServerHandler;
import io.netty.channel.ChannelHandler;
import handling.ServerType;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;

public class ServerInitializer extends ChannelInitializer<SocketChannel>
{
    private int world;
    private int channels;
    private final int port;
    private ServerType type;
    
    public ServerInitializer(final int world, final int channels, final int port, final ServerType type) {
        this.world = world;
        this.channels = channels;
        this.port = port;
        this.type = type;
    }
    
    protected void initChannel(final SocketChannel channel) throws Exception {
        final ChannelPipeline pipe = channel.pipeline();
        pipe.addLast("decoder", (ChannelHandler)new MaplePacketDecoder());
        pipe.addLast("encoder", (ChannelHandler)new MaplePacketEncoder());
        switch (this.type) {
            case 频道服务器:
            case 登录服务器:
            case 世界服务器:
            case 商城服务器: {
                pipe.addLast("handler", (ChannelHandler)new MapleServerHandler(this.channels, this.type));
                break;
            }
        }
    }
}
