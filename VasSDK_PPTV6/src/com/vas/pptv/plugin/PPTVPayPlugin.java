package com.vas.pptv.plugin;

import android.app.Activity;

import com.vas.pptv.PPTVSDK;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.plugin.IPayPlugin;

public class PPTVPayPlugin implements IPayPlugin
{

    private Activity mActivity;
    
    public PPTVPayPlugin(Activity activity){
        this.mActivity = activity;
    }
    
    @Override
    public String getPayParams()
    {
        return null;
    }

    @Override
    public void pay(VasOrderInfo arg0, VasRoleInfo arg1)
    {
        PPTVSDK.getInstance().pay(arg0, arg1);
    }

}