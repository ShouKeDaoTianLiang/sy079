package tools;

import handling.SendPacketOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import handling.MaplePacket;

public class OumxdPacketCreator
{
    public static MaplePacket sendOumxdLogin(final int reason, final String text) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OUMXD_LOGIN.getValue());
        mplew.write(reason);
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }
    
    public static MaplePacket sendOumxdAnticheatCode(final String textC, final String textW) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.OUMXD_ANTICHEAT_CODE.getValue());
        mplew.writeMapleAsciiString(textC);
        mplew.writeMapleAsciiString(textW);
        return mplew.getPacket();
    }
}
