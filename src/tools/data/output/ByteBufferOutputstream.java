package tools.data.output;

import java.nio.ByteBuffer;

public class ByteBufferOutputstream implements ByteOutputStream
{
    private final ByteBuffer bb;
    
    public ByteBufferOutputstream(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    @Override
    public void writeByte(final byte b) {
        this.bb.put(b);
    }
}
