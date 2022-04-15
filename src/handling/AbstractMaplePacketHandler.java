package handling;

import client.MapleClient;

public abstract class AbstractMaplePacketHandler extends MapleServerHandler
{
    public boolean validateState(final MapleClient c) {
        return c.isLoggedIn();
    }
}
