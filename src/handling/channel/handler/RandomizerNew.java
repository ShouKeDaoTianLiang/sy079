package handling.channel.handler;

import java.util.Random;

public class RandomizerNew
{
    private static final Random rand;
    
    public static int nextInt() {
        return RandomizerNew.rand.nextInt();
    }
    
    public static int nextInt(final int arg0) {
        return RandomizerNew.rand.nextInt(arg0);
    }
    
    public static void nextBytes(final byte[] bytes) {
        RandomizerNew.rand.nextBytes(bytes);
    }
    
    public static boolean nextBoolean() {
        return RandomizerNew.rand.nextBoolean();
    }
    
    public static double nextDouble() {
        return RandomizerNew.rand.nextDouble();
    }
    
    public static float nextFloat() {
        return RandomizerNew.rand.nextFloat();
    }
    
    public static long nextLong() {
        return RandomizerNew.rand.nextLong();
    }
    
    public static int rand(final int lbound, final int ubound) {
        return (int)(RandomizerNew.rand.nextDouble() * (ubound - lbound + 1) + lbound);
    }
    
    static {
        rand = new Random();
    }
}
