package tools;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.io.File;
import java.util.Date;
import client.MapleCharacter;
import java.text.SimpleDateFormat;

public class FileoutputUtil
{
    public static final String DC_Log = "日志/Logs/Log_DC.txt";
    private static final SimpleDateFormat sdfT;
    public static final String fixdam_mg = "日志/Logs/魔法伤害修正.rtf";
    public static final String fixdam_ph = "日志/Logs/物理伤害修正.rtf";
    public static final String MobVac_log = "日志/Logs/Log_吸怪.txt";
    public static final String hack_log = "日志/Logs/Log_怀疑外挂.rtf";
    public static final String ban_log = "日志/Logs/Log_封号.rtf";
    public static final String Acc_Stuck = "日志/Logs/Log_卡账号.rtf";
    public static final String Login_Error = "日志/Logs/Log_登录错误.txt";
    public static final String IP_Log = "日志/Logs/Log_账号IP.rtf";
    public static final String Zakum_Log = "日志/Logs/Log_扎昆.rtf";
    public static final String Horntail_Log = "日志/Logs/Log_暗黑龙王.rtf";
    public static final String Pinkbean_Log = "日志/Logs/Log_品克缤.rtf";
    public static final String ScriptEx_Log = "日志/Logs/Log_Script_脚本异常.rtf";
    public static final String PacketEx_Log = "日志/Logs/Log_Packet_封包异常.rtf";
    public static final String 复制装备 = "日志/复制装备.txt";
    private static final SimpleDateFormat sdf;
    private static final SimpleDateFormat sdf_;
    private static final SimpleDateFormat sdfGMT;
    
    public static void logToFile_chr(final MapleCharacter chr, final String file, final String msg) {
        logToFile(file, "\r\n" + CurrentReadable_Time() + " 账号 " + chr.getClient().getAccountName() + " 名称 " + chr.getName() + " (" + chr.getId() + ") 等级 " + chr.getLevel() + " 地图 " + chr.getMapId() + " " + msg, false);
    }
    
    public static void log(final String file, final String msg, final boolean warp) {
        logToFile(file, (warp ? ("\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n") : "") + msg);
    }
    
    public static void logToFile(final String file, final String msg) {
        logToFile(file, msg, false);
    }
    
    public static String CurrentReadable_TimeGMT() {
        return FileoutputUtil.sdfGMT.format(new Date());
    }
    
    public static void logToFile(final String file, final String msg, final boolean notExists) {
        FileOutputStream out = null;
        try {
            File outputFile = new File(file);
            if (outputFile.exists() && outputFile.isFile() && outputFile.length() >= 10240000L) {
                outputFile.renameTo(new File(file.substring(0, file.length() - 4) + "_" + FileoutputUtil.sdfT.format(Calendar.getInstance().getTime()) + file.substring(file.length() - 4, file.length())));
                outputFile = new File(file);
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            if (!out.toString().contains(msg) || !notExists) {
                final OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(msg);
                osw.flush();
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
    
    public static void packetLog(final String file, final String msg) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(msg.getBytes());
            out.write("\r\n\r\n".getBytes());
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
    
    public static void log(final String file, final String msg) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n").getBytes());
            out.write(msg.getBytes());
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
    
    public static void outputFileError(final String file, final Throwable t) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            out.write(("\n------------------------ " + CurrentReadable_Time() + " ------------------------\n").getBytes());
            out.write(getString(t).getBytes());
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
    
    public static String CurrentReadable_Date() {
        return FileoutputUtil.sdf_.format(Calendar.getInstance().getTime());
    }
    
    public static String CurrentReadable_Time() {
        return FileoutputUtil.sdf.format(Calendar.getInstance().getTime());
    }
    
    public static String getString(final Throwable e) {
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
    
    public static String NowTime() {
        final Date now = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final String hehe = dateFormat.format(now);
        return hehe;
    }
    
    static {
        sdfT = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf_ = new SimpleDateFormat("yyyy-MM-dd");
        sdfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
