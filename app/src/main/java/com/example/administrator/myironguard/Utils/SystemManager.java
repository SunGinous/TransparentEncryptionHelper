package com.example.administrator.myironguard.Utils;

import android.app.Activity;

/**
 * Created by sunginous on 2017/3/6.
 */


import java.io.DataOutputStream;
import android.util.Log;

public class SystemManager extends Activity {

    public static boolean RootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try
            {                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }

}

