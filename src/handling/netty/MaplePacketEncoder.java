package handling.netty;

import org.slf4j.LoggerFactory;
import tools.data.input.ByteInputStream;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.ByteArrayByteStream;
import handling.SendPacketOpcode;
import java.util.concurrent.locks.Lock;
import tools.MapleAESOFB;
import tools.MapleCustomEncryption;
import tools.FileoutputUtil;
import tools.HexTool;
import constants.ServerConstants;
import handling.MaplePacket;
import io.netty.util.AttributeKey;
import client.MapleClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import io.netty.handler.codec.MessageToByteEncoder;

public class MaplePacketEncoder extends MessageToByteEncoder<Object>
{
    private static Logger log;
    
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf buffer) throws Exception {
        final MapleClient client = (MapleClient)ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).get();
        if (client != null) {
            final MapleAESOFB send_crypto = client.getSendCrypto();
            final byte[] inputInitialPacket = ((MaplePacket)message).getBytes();
            if (ServerConstants.封包显示) {
                final int packetLen = inputInitialPacket.length;
                final int pHeader = this.readFirstShort(inputInitialPacket);
                final String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                final String op = this.lookupRecv(pHeader);
                final String Recv = "服务端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 50000) {
                    final String RecvTo = Recv + HexTool.toString(inputInitialPacket) + "\r\n" + HexTool.toStringFromAscii(inputInitialPacket);
                    FileoutputUtil.packetLog("log\\服务端封包.log", RecvTo);
                    System.out.println(RecvTo);
                }
                else {
                    MaplePacketEncoder.log.info(HexTool.toString(new byte[] { inputInitialPacket[0], inputInitialPacket[1] }) + " ...");
                }
            }
            final byte[] unencrypted = new byte[inputInitialPacket.length];
            System.arraycopy(inputInitialPacket, 0, unencrypted, 0, inputInitialPacket.length);
            final byte[] ret = new byte[unencrypted.length + 4];
            final Lock mutex = client.getLock();
            mutex.lock();
            try {
                final byte[] header = send_crypto.getPacketHeader(unencrypted.length);
                MapleCustomEncryption.encryptData(unencrypted);
                send_crypto.crypt(unencrypted);
                System.arraycopy(header, 0, ret, 0, 4);
                System.arraycopy(unencrypted, 0, ret, 4, unencrypted.length);
                buffer.writeBytes(ret);
            }
            finally {
                mutex.unlock();
            }
        }
        else {
            buffer.writeBytes(((MaplePacket)message).getBytes());
        }
    }
    
    private String lookupRecv(final int val) {
        for (final SendPacketOpcode op : SendPacketOpcode.values()) {
            if (op.getValue() == val) {
                return op.name();
            }
        }
        return "UNKNOWN";
    }
    
    private int readFirstShort(final byte[] arr) {
        return new GenericLittleEndianAccessor(new ByteArrayByteStream(arr)).readShort();
    }
    
    static {
        MaplePacketEncoder.log = LoggerFactory.getLogger(MaplePacketEncoder.class);
    }
}
