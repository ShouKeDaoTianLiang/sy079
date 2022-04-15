package client;

public class BuddylistEntry
{
    private String name;
    private String group;
    private int cid;
    private int channel;
    private int level;
    private int job;
    private boolean visible;
    
    public BuddylistEntry(final String name, final int characterId, final String group, final int channel, final boolean visible, final int level, final int job) {
        this.name = name;
        this.cid = characterId;
        this.group = group;
        this.channel = channel;
        this.visible = visible;
        this.level = level;
        this.job = job;
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public void setChannel(final int channel) {
        this.channel = channel;
    }
    
    public boolean isOnline() {
        return this.channel >= 0;
    }
    
    public void setOffline() {
        this.channel = -1;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getCharacterId() {
        return this.cid;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getJob() {
        return this.job;
    }
    
    public void setGroup(final String g) {
        this.group = g;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.cid;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final BuddylistEntry other = (BuddylistEntry)obj;
        return this.cid == other.cid;
    }
}
