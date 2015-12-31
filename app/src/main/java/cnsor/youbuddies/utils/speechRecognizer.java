package cnsor.youbuddies.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import cnsor.youbuddies.interfaces.CallBack;

/**
 * Created by Administrator on 15-12-17.
 */
public class speechRecognizer implements RecognizerListener,CallBack {
    private int ret;
    private String TAG = "speechRecognizer";
    private int flag = 0; //语音标识，判断语音识别两种情况，
                          // 1、规定的时间内未接收到语音信息flag=0,
                          // 2、语音得到正确识别flag=1
    private Context context;
    private util mUtil;
    private String grammarIdFinal;
    private final String GRAMMAR_TYPE_ABNF = "abnf";

    private SpeechRecognizer mAsr;
    private speechWakeUp mSpeechWake;


    private speechRecognizer() {}

    private static class speechRecognizerOnHolder{
        static final speechRecognizer instance = new speechRecognizer();
    }

    public static speechRecognizer getInstance(){
        return speechRecognizerOnHolder.instance;
    }

    @Override
    public void runListen() {

    }

    public void initAmr(Context context){
        this.context = context;
        mUtil = new util();
        //云端语法识别：如需本地识别请参照本地识别
        mAsr =  SpeechRecognizer.createRecognizer(context, null);
        // ABNF语法示例，可以说”北京到上海”
        String mCloudGrammar = "#ABNF 1.0 UTF-8;"
                +"languagezh-CN;"
                +"mode voice;root $main;$main = $place1 到$place2 ;$place1 = 北京 | 武汉 | 南京 | 天津 | 天京 | 东京;$place2 = 上海 | 合肥; ";
        //2.构建语法文件
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //3.开始识别,设置引擎类型为云端
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
        //设置grammarId
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarIdFinal);

        ret = mAsr.buildGrammar(GRAMMAR_TYPE_ABNF, mCloudGrammar , grammarListener);
        if (ret != ErrorCode.SUCCESS){
            Log.d(TAG, "语法构建失败,错误码：" + ret);
        }else {
            Log.d(TAG,"语法构建成功");
        }


    }

    //构建语法监听器
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (!TextUtils.isEmpty(grammarId)) {
                    //构建语法成功，请保存grammarId用于识别
                    grammarIdFinal = grammarId;
                } else {
                    Log.d(TAG, "语法构建失败,错误码：" + error.getErrorCode());
                }
            }
        }
    };

    /**
     * */
    public void startRecognizer(){
        ret = mAsr.startListening(this);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "识别失败,错误码: " + ret);
        }else{
            mUtil.showTip(context,"命令识别已初始化，请说出您的命令。");
        }
    }
    @Override
    public void onBeginOfSpeech() {
        //从语音唤醒跳入，播放语音识别开始声音ding
    }

    @Override
    public void onEndOfSpeech() {
        //判断flag值，
        // flag=0未接受到语音信号，跳转到重新唤醒，
        // flag=1接收语音信号执行语音结果合成，并重新开启语音识别onBeginOfSpeech
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        Log.d("语音识别结果为：", recognizerResult.getResultString());
        mUtil.showTip(context, recognizerResult.getResultString());
        mAsr.stopListening();
        if (mSpeechWake!=null){
            mSpeechWake.startWakeupListen();
        }else {
            mSpeechWake = speechWakeUp.getInstance();
            mSpeechWake.initWakeupEngine(context);
            mSpeechWake.startWakeupListen();
        }
    }

    @Override
    public void onError(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    public void destroy(){
        if (mAsr!=null){
            mAsr.cancel();
            mAsr.destroy();
        }else {
            Log.d(TAG, "mAsr is not exist.");
        }
    }
}
