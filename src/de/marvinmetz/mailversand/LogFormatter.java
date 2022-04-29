package de.marvinmetz.mailversand;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String  PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        DateFormat df = new SimpleDateFormat(PATTERN);
        sb.append(df.format(new Date(record.getMillis())))
            .append(" ")
            .append(record.getLevel().getName())
            .append(": ")
            .append(formatMessage(record))
            .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
    }
}