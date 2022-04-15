package gui;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import java.awt.Color;
import java.io.OutputStream;
import javax.swing.JTextPane;
import java.io.PrintStream;

public class GUIPrintStream extends PrintStream
{
    private final JTextPane mainComponent;
    private final JTextPane component;
    private final int type;
    private final int lineLimit;
    public static final int OUT = 0;
    public static final int ERR = 1;
    public static final int NOTICE = 2;
    public static final int PACKET = 3;
    
    public GUIPrintStream(final OutputStream out, final JTextPane mainComponent, final JTextPane component, final int type) {
        super(out);
        this.mainComponent = mainComponent;
        this.component = component;
        this.type = type;
        this.lineLimit = 100;
    }
    
    public GUIPrintStream(final OutputStream out, final JTextPane mainComponent, final JTextPane component, final int type, final int lineLimit) {
        super(out);
        this.mainComponent = mainComponent;
        this.component = component;
        this.type = type;
        this.lineLimit = lineLimit;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) {
        super.write(buf, off, len);
        final String message = new String(buf, off, len);
        final Color col;
        switch (this.type) {
            case 0: {
                col = Color.BLACK;
                break;
            }
            case 1: {
                col = Color.RED;
                break;
            }
            case 2: {
                col = Color.BLUE;
                break;
            }
            case 3: {
                col = Color.GRAY;
                break;
            }
            default: {
                col = Color.BLACK;
                break;
            }
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final SimpleAttributeSet attrSet = new SimpleAttributeSet();
                StyleConstants.setForeground(attrSet, col);
                final Document doc = component.getDocument();
                final Document docMain = mainComponent.getDocument();
                try {
                    final String[] docMainInfo = docMain.getText(0, docMain.getLength()).split("\r\n");
                    final String[] docInfo = doc.getText(0, doc.getLength()).split("\r\n");
                    if (docMainInfo.length >= lineLimit + 1) {
                        for (int i = 0; i <= docMainInfo.length - lineLimit - 1; ++i) {
                            docMain.remove(0, docMainInfo[i].length() + 2);
                        }
                    }
                    if (docInfo.length >= lineLimit + 1) {
                        for (int i = 0; i <= docInfo.length - lineLimit - 1; ++i) {
                            doc.remove(0, docInfo[i].length() + 2);
                        }
                    }
                    docMain.insertString(docMain.getLength(), message, attrSet);
                    doc.insertString(doc.getLength(), message, attrSet);
                }
                catch (BadLocationException e) {
                    component.setText("输出出错:" + e + "\r\n內容:" + message + "\r\n类型:" + type);
                }
            }
        });
    }
}
