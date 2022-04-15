package client;

import java.io.Serializable;

public class CardData implements Serializable
{
    private static final long serialVersionUID = 2550550428979893978L;
    public int cid;
    public short job;
    public short level;
    
    public CardData(final int cid, final short level, final short job) {
        this.cid = cid;
        this.level = level;
        this.job = job;
    }
    
    @Override
    public String toString() {
        return "CID: " + this.cid + ", Job: " + this.job + ", Level: " + this.level;
    }
}
