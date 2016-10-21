package cnsor.youbuddies.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Json
 */
public class jsonParser {

    public static JSONArray parseIatResult(String json) {
        //StringBuffer ret = new StringBuffer();
        JSONArray orders = new JSONArray();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray items;
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0;j < items.length() - 1; j++){
                    orders.put(j,items.getJSONObject(j).getJSONArray("w"));
                }
                //JSONObject obj = items.getJSONObject(0);
                //ret.append(obj.getString("w"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for(int j = 0; j < items.length(); j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if(obj.getString("w").contains("nomatch"))
                    {
                        ret.append("û��ƥ����.");
                        return ret.toString();
                    }
                    ret.append("�������" + obj.getString("w"));
                    ret.append("�����Ŷȡ�" + obj.getInt("sc"));
                    ret.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("û��ƥ����.");
        }
        return ret.toString();
    }
}