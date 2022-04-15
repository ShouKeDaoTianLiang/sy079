package handling.channel.handler;

import java.util.Random;

public class NewRandom
{
    private static final Random rand;
    
    public static int nextInt() {
        return NewRandom.rand.nextInt();
    }
    
    public static int nextInt(final int arg0) {
        return NewRandom.rand.nextInt(arg0);
    }
    
    public static void nextBytes(final byte[] bytes) {
        NewRandom.rand.nextBytes(bytes);
    }
    
    public static boolean nextBoolean() {
        return NewRandom.rand.nextBoolean();
    }
    
    public static double nextDouble() {
        return NewRandom.rand.nextDouble();
    }
    
    public static float nextFloat() {
        return NewRandom.rand.nextFloat();
    }
    
    public static long nextLong() {
        return NewRandom.rand.nextLong();
    }
    
    public static int rand(final int 基数, final int 进数) {
        return (int)(NewRandom.rand.nextDouble() * (进数 - 基数 + 1) + 基数);
    }
    
    static {
        rand = new Random();
    }
}
