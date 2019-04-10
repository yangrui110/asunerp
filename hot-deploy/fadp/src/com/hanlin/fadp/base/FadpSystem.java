package com.hanlin.fadp.base;

import com.hanlin.fadp.base.start.Start;
import com.hanlin.fadp.base.start.StartupException;

/**
 * Created by cl on 2017/4/15.
 */
public class FadpSystem {
    public static void shutdown(){
        try {
            Start.main(new String[]{"-shutdown"});
        } catch (StartupException e1) {
            e1.printStackTrace();
        }
    }
}
