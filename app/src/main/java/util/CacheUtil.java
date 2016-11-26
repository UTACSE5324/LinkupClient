package util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhang on 2016/11/26 0026.
 */

public class CacheUtil {

    public static String PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/.meiqu/catch/";


    public static void setConversationCache(String data, String uname){
        try {
            File path = new File(PATH );
            String filepath = PATH + "/" + uname + ".json";

            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileUtil.writeTextFile(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConversationCache(String uname){
        if (uname == null)
            return null;

        String result = null;

        File file = new File(PATH  + File.separator + uname + ".json");

        if (file.exists() && file.isFile()){
            // 读取缓存数据
            try {
                result = FileUtil.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static String clearCache(){
        String size = FileUtil.getFileSizeString(new File(PATH));

        FileUtil.deleteFile(new File(PATH));

        return size;
    }
}
