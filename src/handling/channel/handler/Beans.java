package handling.channel.handler;

public class Beans
{
    private final int number;
    private final int type;
    private final int pos;
    
    public Beans(final int pos, final int type, final int number) {
        this.pos = pos;
        this.number = number;
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public int getPos() {
        return this.pos;
    }
}
