package server;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Properties;

public class ServerProperties1
{
    private static final Properties props;
    private static final String[] toLoad;
    
    private ServerProperties1() {
    }
    
    public static String getProperty(final String s) {
        return ServerProperties1.props.getProperty(s);
    }
    
    public static void setProperty(final String prop, final String newInf) {
        ServerProperties1.props.setProperty(prop, newInf);
    }
    
    public static String getProperty(final String s, final String def) {
        return ServerProperties1.props.getProperty(s, def);
    }
    
    static {
        props = new Properties();
        toLoad = new String[] { "破功伤害设置.ini" };
        for (final String s : ServerProperties1.toLoad) {
            try {
                final InputStreamReader fr = new InputStreamReader(new FileInputStream(s), "UTF-8");
                ServerProperties1.props.load(fr);
                fr.close();
            }
            catch (IOException ex) {
                System.out.println("加载服务端配置错误：" + ex);
            }
        }
    }
}
