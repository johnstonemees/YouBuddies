package cnsor.youbuddies.utils;

import android.app.Application;
import android.content.Context;


/**
 * Created by Administrator on 15-12-20.
 * 在这里创建语音合成，识别，唤醒单例
 */
public class CommonApplication extends Application {
    private Context context;
    private speechSynthesizer mTts;
    private speechRecognizer mAsr;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //创建语音合成，唤醒，识别单例
        //mTts = new speechSynthesizer(context);
        //mAsr = new speechRecognizer(context);
        //mTts.initSpeechSynthesizer();
       // mAsr.initAmr();
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        //释放语音资源
        if(mTts!=null) {
            mTts.destroy();
        }
        if(mAsr!=null) {
            mAsr.destroy();
        }
    }
}
