package cnsor.youbuddies.utils;

import android.content.Context;
import android.widget.Toast;
import java.io.InputStream;


/**
 * Created by Administrator on 15-12-19.
 */
public class util {

    public void showTip(Context context,String str) {
        Toast.makeText(context,str, Toast.LENGTH_SHORT).show();
    }



    /**
     * 功能性函数扩展类
     */
        /**
         * 读取asset目录下文件。
         * @return content
         */
        public static String readFile(Context mContext,String file,String code)
        {
            int len = 0;
            byte []buf = null;
            String result = "";
            try {
                InputStream in = mContext.getAssets().open(file);
                len  = in.available();
                buf = new byte[len];
                in.read(buf, 0, len);

                result = new String(buf,code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
}
