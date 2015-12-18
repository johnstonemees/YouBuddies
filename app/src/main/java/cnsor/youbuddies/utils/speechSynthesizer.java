package cnsor.youbuddies.utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by Administrator on 15-12-17.
 */
public class speechSynthesizer extends Application {
    private static SpeechSynthesizer mTts;
    private static Context context;
    public void onCreate() {
        super.onCreate();
        speechSynthesizer.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return speechSynthesizer.context;
    }
    //语音合成
    public void initSpeechSynthesizer(){
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixx");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    public void startSpeechSynthesizer(String speechStr){
        mTts.startSpeaking(speechStr,mSynListener);
    }

    private SynthesizerListener mSynListener = new SynthesizerListener(){

        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCompleted(SpeechError arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakBegin() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakPaused() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakResumed() {
            // TODO Auto-generated method stub

        }

    };

    public void destroy(){
        if(mTts!=null){
            mTts.destroy();
        }
    }
}
