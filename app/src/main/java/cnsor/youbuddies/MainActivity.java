package cnsor.youbuddies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cnsor.youbuddies.utils.speechSynthesizer;

/*
* 初始化语音唤醒、语音识别和语音合成
* 先通过语音唤醒，唤醒词为“讯飞语音”，唤醒后开始进行用户语音识别，并将识别结果合成语音反馈给用户*/
public class MainActivity extends AppCompatActivity {
    private int ret;
    private String grammarIdFinal;
    //1.创建SpeechRecognizer对象
    private SpeechRecognizer mAsr;

    private  final String GRAMMAR_TYPE_ABNF = "abnf";

    //唤醒
    private String TAG = "ivw";
    private TextView wakeupResult;
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;
    //门限值 越小就越容易被唤醒 不懂啊
    private int curThresh = 0;

    private  speechSynthesizer mTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTts = (speechSynthesizer)getApplication();
        initResource();
        // 初始化讯飞接口
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=" + getString(R.string.app_id));
        //初始化语音唤醒引擎
        initWakeupEngine();
        //初始化语音识别引擎
        initAmr();
        //初始化语音合成
        mTts.initSpeechSynthesizer();
        //开始语音唤醒
        startWakeupListen();


    }

    private void initResource() {
        wakeupResult = (TextView) findViewById(R.id.textView);
        wakeupResult.setText("等待唤醒中....");
    }
    /*初始化监听类*/
    private void initWakeupEngine() {
        // 加载识唤醒地资源，resPath为本地识别资源路径
        StringBuffer param = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(MainActivity.this,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/56516737.jet");

        param.append(SpeechConstant.IVW_RES_PATH + "=" + resPath);
        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        boolean ret = SpeechUtility.getUtility().setParameter(
                ResourceUtil.ENGINE_START, param.toString());
        if (!ret) {
            Log.d(TAG, "启动本地引擎失败！");
        }
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(MainActivity.this, null);
    }

    /*开始监听唤醒*/
    private void startWakeupListen() {
//非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            wakeupResult.setText(resultString);
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
            mIvw.startListening(mWakeuperListener);
        } else {
            showTip("唤醒未初始化");
        }
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
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
            wakeupResult.setText(resultString);
            mIvw.stopListening();
            mTts.startSpeechSynthesizer("您好！主人，请吩咐命令。");
            startMASRListen();
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }


        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };

    private void showTip(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    public void startMASRListen() {

        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
                    Log.d(MainActivity.ACTIVITY_SERVICE, "识别失败,错误码: " + ret);
        }else{
            showTip("命令识别已初始化，请说出您的命令。");
        }
    }

    public void initAmr(){
        //云端语法识别：如需本地识别请参照本地识别
        mAsr =  SpeechRecognizer.createRecognizer(MainActivity.this, null);
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
            Log.d(MainActivity.ACTIVITY_SERVICE, "语法构建失败,错误码：" + ret);
        }else {
            Log.d(MainActivity.ACTIVITY_SERVICE,"语法构建成功");
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
                    Log.d(MainActivity.ACTIVITY_SERVICE, "语法构建失败,错误码：" + error.getErrorCode());
                }
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener(){
        String tempResult;
        @Override
        public void onBeginOfSpeech() {
            // TODO Auto-generated method stub

        }
        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub
            wakeupResult.setText(tempResult);
        }
        @Override
        public void onError(SpeechError arg0) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            // TODO Auto-generated method stub

        }
        @Override
        public void onResult(RecognizerResult arg0, boolean arg1) {
            // TODO Auto-generated method stub
            Log.d("语音识别结果为：", arg0.getResultString());
            showTip(arg0.getResultString());
            tempResult = arg0.getResultString();
            mAsr.stopListening();
            //重新启动语音唤醒
            if(mIvw!=null){
                mIvw.startListening(mWakeuperListener);
            }
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        if (mAsr != null) {
            mAsr.cancel();
            mAsr.destroy();
        } else {
            showTip("命令识别未初始化");
        }
        if (mTts != null) {
            mTts.destroy();
        } else {
            showTip("语音合成未初始化");
        }
        Log.d(TAG, "onDestroy WakeDemo");
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        } else {
            showTip("唤醒未初始化");
        }
    }
}
