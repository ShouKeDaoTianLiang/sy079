package handling.netty;

import io.netty.channel.ChannelHandler;
import server.ServerProperties;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import handling.ServerType;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;

public class ServerConnection
{
    private final int port;
    private int world;
    private int channels;
    private boolean cs;
    private ServerBootstrap boot;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel channel;
    private ServerType type;
    
    public ServerConnection(final int port) {
        this.world = -1;
        this.channels = -1;
        this.bossGroup = (EventLoopGroup)new NioEventLoopGroup(1);
        this.workerGroup = (EventLoopGroup)new NioEventLoopGroup();
        this.type = null;
        this.port = port;
    }
    
    public ServerConnection(final ServerType type, final int port, final int world, final int channels) {
        this.world = -1;
        this.channels = -1;
        this.bossGroup = (EventLoopGroup)new NioEventLoopGroup(1);
        this.workerGroup = (EventLoopGroup)new NioEventLoopGroup();
        this.type = null;
        this.port = port;
        this.world = world;
        this.channels = channels;
        this.type = type;
    }
    
    public void run() {
        try {
            this.boot = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).option(ChannelOption.SO_BACKLOG, Integer.parseInt(ServerProperties.getProperty("tms.UserLimit")))).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true).childHandler((ChannelHandler)new ServerInitializer(this.world, this.channels, this.port, this.type));
            this.channel = this.boot.bind(this.port).sync().channel().closeFuture().channel();
            System.out.printf("正在启动 - %s 端口: %s\r\n", this.type.name(), this.port);
        }
        catch (Exception e) {
            throw new RuntimeException("启动失败 - " + this.type.name() + ":" + this.channel.remoteAddress());
        }
    }
    
    public void close() {
        if (this.channel != null) {
            this.channel.close();
        }
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
