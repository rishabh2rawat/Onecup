package com.rishabhrawat.fightforhunger;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rishabh on 10-Jul-17.
 */

public class Intromanager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public Intromanager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("first", Context.MODE_PRIVATE);
        editor = pref.edit();

    }

    public void setFirst(boolean isFirst) {
        editor.putBoolean("check", isFirst);
        editor.commit();
    }

    public boolean Check() {

        return pref.getBoolean("check", true);
    }
}
