package cnsor.youbuddies.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 15-12-19.
 */
public class util {

    public void showTip(Context context,String str) {
        Toast.makeText(context,str, Toast.LENGTH_SHORT).show();
    }
}
