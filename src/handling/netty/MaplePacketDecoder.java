package handling.netty;

import org.slf4j.LoggerFactory;
import tools.data.input.ByteInputStream;
import tools.data.input.GenericLittleEndianAccessor;
import tools.data.input.ByteArrayByteStream;
import handling.RecvPacketOpcode;
import tools.FileoutputUtil;
import tools.HexTool;
import constants.ServerConstants;
import tools.MapleCustomEncryption;
import tools.MapleAESOFB;
import client.MapleClient;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import io.netty.util.AttributeKey;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MaplePacketDecoder extends ByteToMessageDecoder
{
    public static final AttributeKey<DecoderState> DECODER_STATE_KEY;
    private static Logger log;
    
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> message) throws Exception {
        final DecoderState decoderState = (DecoderState)ctx.channel().attr((AttributeKey)MaplePacketDecoder.DECODER_STATE_KEY).get();
        final MapleClient client = (MapleClient)ctx.channel().attr((AttributeKey)MapleClient.CLIENT_KEY).get();
        if (decoderState.packetlength == -1 && in.readableBytes() >= 4) {
            final int packetHeader = in.readInt();
            if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                ctx.channel().disconnect();
                return;
            }
            decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
        }
        if (in.readableBytes() >= decoderState.packetlength) {
            final byte[] decryptedPacket = new byte[decoderState.packetlength];
            in.readBytes(decryptedPacket);
            decoderState.packetlength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            MapleCustomEncryption.decryptData(decryptedPacket);
            message.add(decryptedPacket);
            if (ServerConstants.封包显示) {
                final int packetLen = decryptedPacket.length;
                final int pHeader = this.readFirstShort(decryptedPacket);
                final String pHeaderStr = Integer.toHexString(pHeader).toUpperCase();
                final String op = this.lookupSend(pHeader);
                boolean show = true;
                final String s = op;
                switch (s) {
                    case "PONG":
                    case "NPC_ACTION":
                    case "MOVE_LIFE":
                    case "MOVE_PLAYER":
                    case "MOVE_ANDROID":
                    case "MOVE_SUMMON":
                    case "AUTO_AGGRO":
                    case "HEAL_OVER_TIME":
                    case "BUTTON_PRESSED":
                    case "STRANGE_DATA": {
                        show = false;
                        break;
                    }
                }
                final String Send = "客户端发送 " + op + " [" + pHeaderStr + "] (" + packetLen + ")\r\n";
                if (packetLen <= 3000) {
                    final String SendTo = Send + HexTool.toString(decryptedPacket) + "\r\n" + HexTool.toStringFromAscii(decryptedPacket);
                    if (show) {
                        FileoutputUtil.packetLog("log\\客户端封包.log", SendTo);
                        System.out.println(SendTo);
                    }
                    final String SendTos = "\r\n时间：" + FileoutputUtil.CurrentReadable_Time() + "  ";
                    if (op.equals("UNKNOWN")) {
                        FileoutputUtil.packetLog("log\\未知客服端封包.log", SendTos + SendTo);
                    }
                }
                else {
                    MaplePacketDecoder.log.info(HexTool.toString(new byte[] { decryptedPacket[0], decryptedPacket[1] }) + "...");
                }
            }
        }
    }
    
    private String lookupSend(final int val) {
        for (final RecvPacketOpcode op : RecvPacketOpcode.values()) {
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
        DECODER_STATE_KEY = AttributeKey.newInstance(MaplePacketDecoder.class.getName() + ".STATE");
        MaplePacketDecoder.log = LoggerFactory.getLogger(MaplePacketDecoder.class);
    }
    
    public static class DecoderState
    {
        public int packetlength;
        
        public DecoderState() {
            this.packetlength = -1;
        }
    }
}
