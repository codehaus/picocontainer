package org.nanocontainer.picometer;

import java.io.IOException;
import java.io.LineNumberReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class Instantiation implements Comparable {
    private final String className;
    private final PicoMeterClass picoMeterClass;
    private int bytecodeLine;
    private int startLine = -1;
    private int startColumn;
    private int endColumn;

    public Instantiation(String className, PicoMeterClass picoMeterClass) {
        this.className = className;
        this.picoMeterClass = picoMeterClass;
    }

    public String getClassName() {
        return className;
    }

    public void setBytecodeLine(int bytecodeLine) throws IOException {
        this.bytecodeLine = bytecodeLine;
        locatePosition();
    }

    private void locatePosition() throws IOException {
        LineNumberReader reader = picoMeterClass.getSource();
        for (int i = 0; i < bytecodeLine - 1; i++) {
            reader.readLine();
        }

        while (true) {
            String line = reader.readLine();
            int indexOfNew = line.indexOf("new");
            if (indexOfNew != -1) {
                startLine = reader.getLineNumber();
                startColumn = indexOfNew;
                String rest = line.substring(startColumn);
                endColumn = startColumn + rest.indexOf(')') + 1;
                return;
            }
        }
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int compareTo(Object o) {
        return getStartLine() - ((Instantiation) o).getStartLine();
    }

    public int getEndColumn() {
        return endColumn;
    }
}
