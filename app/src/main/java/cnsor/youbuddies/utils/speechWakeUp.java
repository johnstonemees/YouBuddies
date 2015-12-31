package cnsor.youbuddies.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;


import cnsor.youbuddies.interfaces.CallBack;

/**
 * Created by Administrator on 15-12-20.
 */
public class speechWakeUp implements CallBack,WakeuperListener {
    private Context context;
    private util mUtil;
    //唤醒
    private String TAG = "ivw";

    private VoiceWakeuper mIvw;
    private int curThresh = 0;
    // 唤醒结果内容
    private String resultString;

    private speechSynthesizer mSpeechSyn;


    private static class speechWakeUpOnHolder{
        static final speechWakeUp instance = new speechWakeUp();
    }

    public static speechWakeUp getInstance(){
        return speechWakeUpOnHolder.instance;
    }

    private speechWakeUp() {}

    /*初始化监听类*/
    public void initWakeupEngine(Context context) {
        this.context = context;
        mUtil = new util();
        // 加载识唤醒地资源，resPath为本地识别资源路径
        StringBuffer param = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(context,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/56516737.jet");

        param.append(SpeechConstant.IVW_RES_PATH + "=" + resPath);
        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        boolean ret = SpeechUtility.getUtility().setParameter(
                ResourceUtil.ENGINE_START, param.toString());
        if (!ret) {
            Log.d(TAG, "启动本地引擎失败！");
        }
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    /*开始监听唤醒*/
    public void startWakeupListen() {
        //非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            /**
             * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
             * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
             */
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            mIvw.startListening(this);
        } else {
            mUtil.showTip(this.context, "唤醒未初始化");
        }
    }

    @Override
    public void onBeginOfSpeech() {
        mUtil.showTip(this.context,"开始说话");
    }

    @Override
    public void onResult(WakeuperResult wakeuperResult) {
        try {
            String text = wakeuperResult.getResultString();
            JSONObject object;
            object = new JSONObject(text);
            StringBuffer buffer = new StringBuffer();
            buffer.append("【RAW】 " + text);
            buffer.append("\n");
            buffer.append("【操作类型】" + object.optString("sst"));
            buffer.append("\n");
            buffer.append("【唤醒词id】" + object.optString("id"));
            buffer.append("\n");
            buffer.append("【得分】" + object.optString("score"));
            buffer.append("\n");
            buffer.append("【前端点】" + object.optString("bos"));
            buffer.append("\n");
            buffer.append("【尾端点】" + object.optString("eos"));
            resultString = buffer.toString();
        } catch (JSONException e) {
            resultString = "结果解析出错";
            e.printStackTrace();
        }
        mUtil.showTip(this.context, resultString);
        mIvw.stopListening();

        if (mSpeechSyn!=null){
            mSpeechSyn.startSpeechSynthesizer("好啊");
        }else {
            mSpeechSyn = speechSynthesizer.getInstance();
            mSpeechSyn.initSpeechSynthesizer(this.context);
            mSpeechSyn.startSpeechSynthesizer("您好");
        }


    }

    @Override
    public void onError(SpeechError speechError) {
        mUtil.showTip(this.context, speechError.getPlainDescription(true));
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    @Override
    public void onVolumeChanged(int i) {

    }

    /*
    * 回调函数*/
    @Override
    public void runListen() {
        mIvw.startListening(this);
    }

    public void destroy(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        } else {
            mUtil.showTip(this.context, "唤醒未初始化");
        }
        if (mSpeechSyn != null) {
            mSpeechSyn.destroy();
        } else {
            mUtil.showTip(this.context, "语音合成未初始化");
        }
    }
}
