package tools;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;

public class FilePrinter1
{
    public static final String ACCOUNT_STUCK = "accountStuck.txt";
    public static final String EXCEPTION_CAUGHT = "exceptionCaught.txt";
    public static final String CLIENT_START = "clientStartError.txt";
    public static final String ADD_PLAYER = "addPlayer.txt";
    public static final String MAPLE_MAP = "mapleMap.txt";
    public static final String ERROR38 = "error38.txt";
    public static final String PACKET_LOG = "log.txt";
    public static final String EXCEPTION = "exceptions.txt";
    public static final String PACKET_HANDLER = "PacketHandler/";
    public static final String PORTAL = "portals/";
    public static final String NPC = "npcs/";
    public static final String INVOCABLE = "invocable/";
    public static final String REACTOR = "reactors/";
    public static final String QUEST = "quests/";
    public static final String ITEM = "items/";
    public static final String MOB_MOVEMENT = "mobmovement.txt";
    public static final String MAP_SCRIPT = "mapscript/";
    public static final String DIRECTION = "directions/";
    public static final String SAVE_CHAR = "saveToDB.txt";
    public static final String INSERT_CHAR = "insertCharacter.txt";
    public static final String LOAD_CHAR = "loadCharFromDB.txt";
    public static final String ScriptEx_Log = "Log_Script_Except.txt";
    public static final String SESSION = "sessions.txt";
    public static final String 创建人物 = "创建人物/";
    private static final SimpleDateFormat sdf;
    private static final String 错误;
    private static final String FILE_PATH;
    private static final String FILE_PATH1;
    private static final String FILE_PATH2;
    private static final String FILE_PATH3;
    private static final String ERROR = "error/";
    
    public static void printError(final String name, final Throwable t) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH + "error/" + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(getString(t).getBytes());
            out.write("\n---------------------------------\r\n".getBytes());
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void printError(final String name, final Throwable t, final String info) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH + "error/" + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write((info + "\r\n").getBytes());
            out.write(getString(t).getBytes());
            out.write("\n---------------------------------\r\n".getBytes());
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void printError(final String name, final String s) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH + "error/" + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void print1(final String name, final String s) {
        print1(name, s, true);
    }
    
    public static void print(final String name, final String s) {
        print(name, s, true);
    }
    
    public static String CurrentReadable_Time() {
        return FilePrinter1.sdf.format(Calendar.getInstance().getTime());
    }
    
    public static void print1(final String name, final String s, final boolean line) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH1 + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
            out.write("\r\n".getBytes());
            if (line) {
                out.write("------------------------------------ \r\n".getBytes());
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void 雇佣(final String name, final String s, final boolean line) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH2 + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
            out.write("\r\n".getBytes());
            if (line) {
                out.write("------------------------------------ \r\n".getBytes());
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void 砸卷(final String name, final String s) {
        砸卷(name, s, true);
    }
    
    public static void 砸卷(final String name, final String s, final boolean line) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH3 + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
            out.write("\r\n".getBytes());
            if (line) {
                out.write("------------------------------------ \r\n".getBytes());
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void 雇佣(final String name, final String s) {
        雇佣(name, s, true);
    }
    
    public static void print(final String name, final String s, final boolean line) {
        FileOutputStream out = null;
        final String file = FilePrinter1.FILE_PATH + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
            out.write("\r\n".getBytes());
            if (line) {
                out.write("------------------------------------ \r\n".getBytes());
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void 错误(final String name, final String s, final boolean line) {
        FileOutputStream out = null;
        final String file = FilePrinter1.错误 + name;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            out.write(s.getBytes());
            out.write("\r\n".getBytes());
            if (line) {
                out.write("------------------------------------ \r\n".getBytes());
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private static String getString(final Throwable e) {
        String retValue = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            retValue = sw.toString();
        }
        finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sw != null) {
                    sw.close();
                }
            }
            catch (IOException ex) {}
        }
        return retValue;
    }
    
    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        错误 = "日志/Logs/错误记录/" + FilePrinter1.sdf.format(Calendar.getInstance().getTime()) + "/";
        FILE_PATH = "日志/Logs/玩家谈话记录/" + FilePrinter1.sdf.format(Calendar.getInstance().getTime()) + "/";
        FILE_PATH1 = "日志/Logs/商城购买记录/" + FilePrinter1.sdf.format(Calendar.getInstance().getTime()) + "/";
        FILE_PATH2 = "日志/Logs/雇佣购买记录/" + FilePrinter1.sdf.format(Calendar.getInstance().getTime()) + "/";
        FILE_PATH3 = "日志/Logs/玩家砸卷记录/" + FilePrinter1.sdf.format(Calendar.getInstance().getTime()) + "/";
    }
}
