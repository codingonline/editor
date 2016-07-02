package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SingleFileCopy {

	public static boolean fileChannelCopy(File src, File dst) {
		if(!src.exists() || !src.isFile())
			return false;
		boolean ret;
		FileChannel in = null;
		FileChannel out = null;
        try {
            in = new FileInputStream(src).getChannel();
            out = new FileOutputStream(dst).getChannel();
            in.transferTo(0, in.size(), out);
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return ret;
	}
}
