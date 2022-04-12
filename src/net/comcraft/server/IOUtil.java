package net.comcraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
    public static void copy(File in, File out) throws IOException {
        InputStream is = new FileInputStream(in);
        OutputStream os = new FileOutputStream(out);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) > 0) {
            os.write(buffer, 0, read);
        }
        os.close();
        is.close();
    }
}
