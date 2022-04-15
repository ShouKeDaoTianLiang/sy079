package client.anticheat;

public enum ReportType
{
    Hacking(0, "hack"), 
    Botting(1, "bot"), 
    Scamming(2, "scam"), 
    FakeGM(3, "fake"), 
    Advertising(5, "ad");
    
    public byte i;
    public String theId;
    
    private ReportType(final int i, final String theId) {
        this.i = (byte)i;
        this.theId = theId;
    }
    
    public static ReportType getById(final int z) {
        for (final ReportType t : values()) {
            if (t.i == z) {
                return t;
            }
        }
        return null;
    }
    
    public static ReportType getByString(final String z) {
        for (final ReportType t : values()) {
            if (z.contains(t.theId)) {
                return t;
            }
        }
        return null;
    }
}
