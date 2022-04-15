package tools;

import java.util.TimeZone;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Calendar;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;

public class FileoutputUtil1
{
    public static final String HiredMerchDir = "日志/HiredMerch/";
    private static final SimpleDateFormat sdfT;
    private static final SimpleDateFormat sdf;
    private static final SimpleDateFormat sdfGMT;
    private static final SimpleDateFormat sdf_;
    private static final String FILE_PATH = "日志/";
    private static final String ERROR;
    private static final String 交易输出;
    private static final String 谈话记录;
    private static final String 离开商城;
    private static final String 雇佣购买;
    private static final String 玩家强化记录;
    private static final String 玩家兑换记录;
    private static final String GeneralLog = "日志/运行日志.log";
    private static final String 全屏打怪;
    private static final String 外挂记录;
    
    public static void 全屏打怪(final String name, final String msg) {
        全屏打怪(name, null, msg);
    }
    
    public static void 全屏打怪(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.全屏打怪 + name, t, info);
    }
    
    public static void 外挂记录(final String name, final String msg) {
        外挂记录(name, null, msg);
    }
    
    public static void 外挂记录(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.外挂记录 + name, t, info);
    }
    
    public static void 玩家兑换记录(final String name, final String msg) {
        玩家兑换记录(name, null, msg);
    }
    
    public static void 玩家兑换记录(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.玩家兑换记录 + name, t, info);
    }
    
    public static void 玩家强化记录(final String name, final String msg) {
        玩家强化记录(name, null, msg);
    }
    
    public static void 玩家强化记录(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.玩家强化记录 + name, t, info);
    }
    
    public static void 雇佣购买(final String name, final String msg) {
        雇佣购买(name, null, msg);
    }
    
    public static void 雇佣购买(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.雇佣购买 + name, t, info);
    }
    
    public static void 离开商城(final String name, final String msg) {
        离开商城(name, null, msg);
    }
    
    public static void 离开商城(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.离开商城 + name, t, info);
    }
    
    public static void 玩家谈话(final String name, final String msg) {
        玩家谈话(name, null, msg);
    }
    
    public static void 玩家谈话(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.谈话记录 + name, t, info);
    }
    
    public static void 玩家交易(final String name, final String msg) {
        玩家交易(name, null, msg);
    }
    
    public static void 玩家交易(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.交易输出 + name, t, info);
    }
    
    public static void printError(final String name, final String msg) {
        printError(name, null, msg);
    }
    
    public static void printError(final String name, final Throwable t) {
        printError(name, t, null);
    }
    
    public static void printError(final String name, final Throwable t, final String info) {
        outputFileError("日志/" + FileoutputUtil1.ERROR + name, t, info);
    }
    
    public static void outputFileError(final String name, final String msg) {
        outputFileError(name, null, msg);
    }
    
    public static void outputFileError(final String file, final Throwable t) {
        outputFileError(file, t, null);
    }
    
    public static void outputFileError(final String file, final Throwable t, final String info) {
        logToFile(file, "\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n" + ((info != null) ? (info + "\r\n") : "") + ((t != null) ? getString(t) : ""));
    }
    
    public static void log(final String msg) {
        logToFile("日志/运行日志.log", msg + "\r\n");
    }
    
    public static void log(final String file, final String msg) {
        log(file, msg, true);
    }
    
    public static void log(final String file, final String msg, final boolean warp) {
        logToFile(file, (warp ? ("\r\n------------------------ " + CurrentReadable_Time() + " ------------------------\r\n") : "") + msg);
    }
    
    public static void packetLog(final String file, final String msg) {
        logToFile(file, msg + "\r\n\r\n");
    }
    
    public static void hiredMerchLog(final String name, final String msg) {
        logToFile("日志/HiredMerch/" + name + ".txt", "[" + CurrentReadable_Time() + "] " + msg + "\r\n");
    }
    
    public static void logToFile(final String file, final String msg) {
        FileOutputStream out = null;
        try {
            final File outputFile = new File(file);
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            final OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            osw.write(msg);
            osw.flush();
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
        return FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime());
    }
    
    public static String CurrentReadable_Time() {
        return FileoutputUtil1.sdf.format(Calendar.getInstance().getTime());
    }
    
    public static String CurrentReadable_TimeGMT() {
        return FileoutputUtil1.sdfGMT.format(new Date());
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
    
    static {
        sdfT = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf_ = new SimpleDateFormat("yyyy-MM-dd");
        ERROR = "logs/商城购买记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        交易输出 = "logs/玩家交易记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        谈话记录 = "logs/玩家谈话记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        离开商城 = "logs/离开商城/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        雇佣购买 = "logs/雇佣购买记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        玩家强化记录 = "logs/玩家强化记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        玩家兑换记录 = "logs/玩家兑换记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        全屏打怪 = "logs/全屏打怪/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        外挂记录 = "logs/外挂记录/" + FileoutputUtil1.sdf_.format(Calendar.getInstance().getTime()) + "/";
        FileoutputUtil1.sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}
