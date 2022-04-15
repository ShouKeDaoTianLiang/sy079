package tools.packet;

import handling.SendPacketOpcode;
import constants.ServerConstants;
import tools.data.output.MaplePacketLittleEndianWriter;
import handling.MaplePacket;

public class MonsterBookPacket
{
    public static MaplePacket addCard(final boolean full, final int cardid, final int level) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("addCard--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTERBOOK_ADD.getValue());
        if (!full) {
            mplew.write(1);
            mplew.writeInt(cardid);
            mplew.writeInt(level);
        }
        else {
            mplew.write(0);
        }
        if (ServerConstants.PACKET_ERROR_OFF) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("addCard-48：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showGainCard(final int itemid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showGainCard--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.writeInt(itemid);
        if (ServerConstants.PACKET_ERROR_OFF) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showGainCard-66：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket showForeginCardEffect(final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("showForeginCardEffect--------------------");
        }
        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(id);
        mplew.write(13);
        if (ServerConstants.PACKET_ERROR_OFF) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("showForeginCardEffect-83：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
    
    public static MaplePacket changeCover(final int cardid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (ServerConstants.调试输出封包) {
            System.out.println("changeCover--------------------");
        }
        mplew.writeShort(SendPacketOpcode.MONSTERBOOK_CHANGE_COVER.getValue());
        mplew.writeInt(cardid);
        if (ServerConstants.PACKET_ERROR_OFF) {
            final ServerConstants ERROR = new ServerConstants();
            ERROR.setPACKET_ERROR("changeCover-99：\r\n" + mplew.getPacket() + "\r\n\r\n");
        }
        return mplew.getPacket();
    }
}
