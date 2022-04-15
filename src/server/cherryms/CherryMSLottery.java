package server.cherryms;

import server.maps.MapleMapFactory;
import java.util.Collection;
import handling.channel.ChannelServer;
import client.MapleCharacter;

public interface CherryMSLottery
{
    void addChar(final MapleCharacter p0);
    
    void doLottery();
    
    void drawalottery();
    
    long getAllpeichu();
    
    long getAlltouzhu();
    
    ChannelServer getChannelServer();
    
    Collection<MapleCharacter> getCharacters();
    
    MapleMapFactory getMapleMapFactory();
    
    int getTouNumbyType(final int p0);
    
    int getZjNum();
    
    void setAllpeichu(final long p0);
    
    void setAlltouzhu(final long p0);
    
    void setCharacters(final Collection<MapleCharacter> p0);
    
    void setZjNum(final int p0);
    
    void warp(final int p0, final MapleCharacter p1);
}
