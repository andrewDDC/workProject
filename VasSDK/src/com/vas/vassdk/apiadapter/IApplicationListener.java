package com.vas.vassdk.apiadapter;

import android.content.Context;
import android.content.res.Configuration;

public interface IApplicationListener
{
    public void onProxyCreate();

    public void onProxyAttachBaseContext(Context base);

    public void onProxyConfigurationChanged(Configuration config);

}