package xuyihao.rongyiclient.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Xuyh at 2016/10/7 15:07.
 */

public class DatabaseOperator extends SQLiteOpenHelper {

    public DatabaseOperator(Context context){
        super(context, "cache.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Accounts(_id bigint primary key, Acc_ID varchar(20) not null, Acc_name varchar(100) not null, Acc_pwd varchar(20) not null, Acc_sex char(4), Acc_loc varchar(200) not null, Acc_lvl int not null, Acc_ryb int not null, Acc_shop bool not null, Acc_atn int not null, Acc_atnd int not null, Acc_pub int not null, Acc_no varchar(20), Acc_name2 varchar(100), Acc_tel varchar(20), Acc_addTime datetime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
