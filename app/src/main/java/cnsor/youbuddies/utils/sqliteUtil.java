package cnsor.youbuddies.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 15-12-15.
 */
public class sqliteUtil extends SQLiteOpenHelper {
    final String CREATE_TABLE = "Create table schedule(sche_id integer primary key autoincrement,sche_content varchar(255),create_date datetime,alert_date datetime)";
    public sqliteUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
