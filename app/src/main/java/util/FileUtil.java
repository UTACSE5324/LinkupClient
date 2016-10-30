package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhang on 2016/10/30 0030.
 */

public class FileUtil {

    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    public static byte[] readTextFileToByte(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text.getBytes();
    }

    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }
}
