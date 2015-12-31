package cnsor.youbuddies.utils;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;



/**
 * Created by Administrator on 15-12-17.
 * 语音合成类，在Application里面创建一个单例，整个程序共享使用
 */
public class speechSynthesizer implements SynthesizerListener {
    private Context context;
    private util mUtil;
    private SpeechSynthesizer mTts;
    private speechRecognizer mSpeechRec;

    private static class speechSynthesizerOnHolder{
        static final speechSynthesizer instance = new speechSynthesizer();
    }

    public static speechSynthesizer getInstance(){
        return speechSynthesizerOnHolder.instance;
    }

    private speechSynthesizer() {
    }

    //语音合成
    public void initSpeechSynthesizer(Context context){
        this.context = context;
        mUtil = new util();
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixx");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    public void startSpeechSynthesizer(String speechStr){
        mTts.startSpeaking(speechStr,this);
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (mSpeechRec!=null){
            mSpeechRec.startRecognizer();
        }else {
            mSpeechRec = speechRecognizer.getInstance();
            mSpeechRec.initAmr(this.context);
            mSpeechRec.startRecognizer();
        }
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    public void destroy(){
        if (mSpeechRec != null) {
            mSpeechRec.destroy();
        } else {
            mUtil.showTip(this.context, "命令识别未初始化");
        }
        if(mTts!=null){
            mTts.destroy();
        }
    }
}
