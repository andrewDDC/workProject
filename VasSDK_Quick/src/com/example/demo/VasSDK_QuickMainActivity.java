package com.example.demo;

import java.util.TreeMap;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vas.vassdk.VasSDK;
import com.vas.vassdk.VasSDKConfig;
import com.vas.vassdk.bean.VasOrderInfo;
import com.vas.vassdk.bean.VasRoleInfo;
import com.vas.vassdk.bean.VasUserInfo;
import com.vas.vassdk.callback.VasExitCallback;
import com.vas.vassdk.callback.VasInitCallback;
import com.vas.vassdk.callback.VasLoginCallback;
import com.vas.vassdk.callback.VasLogoutCallback;
import com.vas.vassdk.callback.VasPayCallback;
import com.vas.vassdk.callback.VasSwitchAccountCallback;
import com.vas.vassdk.http.VasHttpUtil;
import com.vas.vassdk.util.VasMD5Util;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.StringRequest;

public class VasSDK_QuickMainActivity extends Activity
{

    private Activity mActivity;

    private Button mLoginBtn;

    private Button mLogoutBtn;

    private Button mPayBtn;

    private Button mExitBtn;

    private Button mCreatRoleBtn;

    private Button mCommitRoleInfoBtn;

    private TextView minfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getResId("activity_main", "layout"));
        initView();
        setSDKlistener();
        setListener();
        VasSDK.getInstance().init(mActivity);
        VasSDK.getInstance().setDebugMode(true);
        VasSDK.getInstance().setIsLandScape(false);
        VasSDK.getInstance().onCreate(savedInstanceState);
        
    }

    private void initView()
    {
        mLoginBtn = (Button) findViewById(getResId("btn_login", "id"));
        mLogoutBtn = (Button) findViewById(getResId("btn_logout", "id"));
        mPayBtn = (Button) findViewById(getResId("btn_pay", "id"));
        mExitBtn = (Button) findViewById(getResId("btn_exit", "id"));
        mCreatRoleBtn = (Button) findViewById(getResId("btn_createRole", "id"));
        mCommitRoleInfoBtn = (Button) findViewById(getResId("btn_commitRoleInfo", "id"));
        minfoTv = (TextView) findViewById(getResId("tv_info", "id"));
    }

    private void setSDKlistener()
    {
        // 设置初始化通知(必接)
        VasSDK.getInstance().setVasInitCallback(new VasInitCallback()
        {

            @Override
            public void onSuccess()
            {
                Log.i("xxxx", "init onSuccess");
                Toast.makeText(mActivity, "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message, String trace)
            {
                Log.i("xxxx", "init onFailed : message = " + message + ",trace = " + trace);
                Toast.makeText(mActivity, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置登录通知(必接)
        VasSDK.getInstance().setVasLoginCallback(new VasLoginCallback()
        {

            @Override
            public void onSuccess(VasUserInfo paramUserInfo)
            {
                minfoTv.setText("登录成功" + "\n\r" + "UserID:  " + paramUserInfo.getUid() + "\n\r" + "UserName:  "
                        + paramUserInfo.getUserName() + "\n\r" + "Token:  " + paramUserInfo.getToken());
                Log.i("xxxx", "登录成功" + "\n\r" + "UserID:  " + paramUserInfo.getUid() + "\n\r" + "UserName:  "
                        + paramUserInfo.getUserName() + "\n\r" + "Token:  " + paramUserInfo.getToken() + paramUserInfo.getExtra());
                setUserInfo(false);
                
                
                checkLogin(paramUserInfo.getUid(), paramUserInfo.getUserName(), paramUserInfo.getToken(), paramUserInfo.getExtra());
                
            }

            @Override
            public void onFailed(String paramString1, String paramString2)
            {
                minfoTv.setText("登录失败" + "\n\r" + "paramString1 :  " + paramString1 + "\n\r" + "paramString2:  "
                        + paramString2);
            }

            @Override
            public void onCancel()
            {
                minfoTv.setText("登录取消");
            }
        });
        // 设置注销通知(必接)
        VasSDK.getInstance().setVasLogoutCallback(new VasLogoutCallback()
        {

            @Override
            public void onSuccess()
            {
                minfoTv.setText("注销成功");
            }

            @Override
            public void onFailed(String message, String trace)
            {
                minfoTv.setText("注销失败 ： " + "\n\r" + "message = " + message + ",trace = " + trace);
            }
        });
        // 设置支付通知(必接)
        VasSDK.getInstance().setVasPayCallback(new VasPayCallback()
        {

            @Override
            public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams)
            {
                minfoTv.setText("支付成功：" + "\n\r" + "sdkOrderID = " + sdkOrderID + "\n\r" + "cpOrderID = " + cpOrderID
                        + "\n\r" + "extrasParams = " + extrasParams);
            }

            @Override
            public void onFailed(String cpOrderID, String message, String trace)
            {
                minfoTv.setText("支付失败：" + "\n\r" + "cpOrderID = " + cpOrderID + "\n\r" + "message = " + message
                        + "\n\r" + "trace = " + trace);
            }

            @Override
            public void onCancel(String cpOrderID)
            {
                minfoTv.setText("支付取消：" + "\n\r" + "cpOrderID = " + cpOrderID);
            }
        });
        // 设置切换账号通知(必接)
        VasSDK.getInstance().setVasSwitchAccountCallback(new VasSwitchAccountCallback()
        {

            @Override
            public void onSuccess(VasUserInfo paramUserInfo)
            {
                minfoTv.setText("切换账号成功" + "\n\r" + "UserID:  " + paramUserInfo.getUid() + "\n\r" + "UserName:  "
                        + paramUserInfo.getUserName() + "\n\r" + "Token:  " + paramUserInfo.getToken());
            }

            @Override
            public void onFailed(String message, String trace)
            {
                minfoTv.setText("切换账号失败" + "\n\r" + "message :  " + message + "\n\r" + "trace:  " + trace);
            }

            @Override
            public void onCancel()
            {
                minfoTv.setText("切换账号取消");
            }
        });
        // 设置退出通知(必接)
        VasSDK.getInstance().setVasExitCallback(new VasExitCallback()
        {

            @Override
            public void onSuccess()
            {
                // 游戏本身的退出操作
                mActivity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }

            @Override
            public void onFailed(String message, String trace)
            {

            }
        });

    }

    private void setListener()
    {
        mLoginBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                VasSDK.getInstance().login();
            }
        });
        mLogoutBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                VasSDK.getInstance().logout();
            }
        });
        mPayBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                pay();
            }
        });
        mExitBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                exit();
            }
        });
        mCreatRoleBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                setUserInfo(true);
            }
        });
        mCommitRoleInfoBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                setUserInfo(false);
            }
        });
    }

    /**
     * 向渠道提交用户信息。 在创建游戏角色、进入游戏和角色升级3个地方调用此接口，当创建角色时最后一个参数值为true，其他两种情况为false。
     * GameRoleInfo所有字段均不能传null，游戏没有的字段请传一个默认值或空字符串。
     */
    public void setUserInfo(boolean isCreateRole)
    {
        VasRoleInfo roleInfo = new VasRoleInfo();
        roleInfo.setServerId("1");// 服务器ID
        roleInfo.setServerName("火星服务器");// 服务器名称
        roleInfo.setRoleName("裁决之剑");// 角色名称
        roleInfo.setRoleId("1121121");// 角色ID
        roleInfo.setRoleLevel("12");// 等级
        roleInfo.setVipLevel("Vip4");// VIP等级
        roleInfo.setGameBalance("5000");// 角色现有金额
        roleInfo.setPartyName(""); // 公会名字
        VasSDK.getInstance().setRoleInfo(mActivity, roleInfo, isCreateRole);
    }

    /**
     * 支付
     */
    private void pay()
    {
        VasRoleInfo roleInfo = new VasRoleInfo();
        roleInfo.setServerId("1");// 服务器ID，其值必须为数字字符串
        roleInfo.setServerName("火星服务器");// 服务器名称
        roleInfo.setRoleName("裁决之剑");// 角色名称
        roleInfo.setRoleId("1121121");// 角色ID
        roleInfo.setRoleLevel("12");// 等级
        roleInfo.setVipLevel("Vip4");// VIP等级
        roleInfo.setGameBalance("5000");// 角色现有金额
        roleInfo.setPartyName("");// 公会名字

        VasOrderInfo orderInfo = new VasOrderInfo();
        orderInfo.setCpOrderId(UUID.randomUUID().toString().replace("-", ""));// 游戏订单号
        orderInfo.setGoodsName("元宝");// 产品名称
        // orderInfo.setGoodsName("月卡");
        orderInfo.setCount(10);// 购买数量，如购买"10元宝"则传10
        // orderInfo.setCount(1);// 购买数量，如购买"月卡"则传1
        orderInfo.setAmount(1); // 总金额（单位为元）
        orderInfo.setGoodsId("101"); // 产品ID，用来识别购买的产品
        orderInfo.setExtrasParams("透传参数"); // 透传参数

        VasSDK.getInstance().pay(orderInfo, roleInfo);
        
    }

    /**
     * 退出
     */
    private void exit()
    {
        
        
        if (VasSDK.getInstance().isSDKShowExitDialog())
        {
            VasSDK.getInstance().exit();
        }
        else
        {
            // 游戏调用自身的退出对话框，点击确定后，调用quick的exit接口
            new AlertDialog.Builder(VasSDK_QuickMainActivity.this).setTitle("退出").setMessage("是否退出游戏?").setPositiveButton(
                    "确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1)
                        {
                            VasSDK.getInstance().exit();
                        }
                    }).setNegativeButton("取消", null).show();
        }

    }

    /**
     * 返回资源id
     */
    public int getResId(String name, String defType)
    {
        return mActivity.getResources().getIdentifier(name, defType, mActivity.getPackageName());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        VasSDK.getInstance().onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        VasSDK.getInstance().onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        VasSDK.getInstance().onResume();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        VasSDK.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        VasSDK.getInstance().onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        VasSDK.getInstance().onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        VasSDK.getInstance().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        VasSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        exit();
        VasSDK.getInstance().onBackPressed();
    }
    
    
    ////test
    private void checkLogin(String uid,String userName,String token,String ext){
        
        String time = System.currentTimeMillis() + "";
        
        Request<String> request = new StringRequest("http://game.g.pptv.com/api/sdk/integration/check_user_info.php", RequestMethod.POST);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("user_id", uid);
        treeMap.put("username", userName);
        treeMap.put("token", token);
        if(!TextUtils.isEmpty(ext)){
            treeMap.put("ext", ext);
        }
        treeMap.put("time", time);
        treeMap.put("gid", VasSDKConfig.VAS_GAMEID);
        treeMap.put("platform", VasSDKConfig.VAS_PLATFORMID);
        treeMap.put("sub_platform", VasSDK.getInstance().getSubPlatformId() + "");
        String sign = new VasMD5Util().MD5EncryptString(treeMap, "51d18dc3e04da20fbcb187da4d8a1a16");
        request.add(treeMap);
        request.add("sign", sign.toLowerCase());
        VasHttpUtil.getInstance().add(2, request, new OnResponseListener<String>(){

            @Override
            public void onFailed(int arg0, String arg1, Object arg2, Exception arg3, int arg4, long arg5)
            {
                // TODO Auto-generated method stub
                Log.i("xxxx", "checklogin failed");
            }

            @Override
            public void onFinish(int arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onStart(int arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onSucceed(int arg0, Response<String> response)
            {
                // TODO Auto-generated method stub
                if (response.getHeaders().getResponseCode() == 200)
                {// 请求成功。
                    String result = response.get();
                    Log.i("xxxx", result);
                }
            }
            
        });
        
        
    }
    
    

}
