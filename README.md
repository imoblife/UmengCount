UmengCount
==========

1 原理：基于之前的有效性验证结果，采用时间切分机制，将不同时间段的数据，分别提交到相应的项目：

时间      ［0，1，2，3，4，5，6，7，8，9，10，11，12，13，14，15，16，17，18，19，20，21，22，23，24］...
KEY       ［         KEY1          ］［        KEY2        ］［       KEY3       ］［     KEY4      ］...

2 集成
2.1 引用UmengCount库

2.2 Activity改为继承UmengActivity

2.3 Manifest

       <!-- 测试2 -->
        <meta-data
            android:name="umeng_debug"
            android:value="是否调试" />
        <meta-data
            android:name="umeng_key"
            android:value="友盟APPKEY" />
        <meta-data
            android:name="umeng_url"
            android:value="服务器配置文件路径" />

        <receiver
            android:name="com.umeng.count.PresentReceiver"
            android:enabled="true"
            android:exported="true" >
            <intent-filter>
                <action android:name="android.intent.action.USER_PRESENT" />
                <action android:name="count_action_rotation_newuser" />
                <action android:name="count_action_rotation_odleuser" />
                <action android:name="count_action_end_newuser" />
                <action android:name="count_action_end_odleuser" />
            </intent-filter>
        </receiver>


3 服务器配置文件

key
keyWeight
countOfDay
sessionDuration

{
        "countOfDay": "6",
        "sessionDuration": "5",
        "applist": [
             {
                "key": "53fd4119fd98c56714026100",
                "keyWeight": "50"
            },
            {
                "key": "53fd4139fd98c561b9033823",
                "keyWeight": "50"
            }
        ]
}

4 问题
8月29：测试发现我们要求它发送的KEY，和它实际发送的KEY不一致。尚未测出实际发送的KEY多久更换。

4 流程

获取服务器配置文件版本
       相等：
       不相等：更新本地数据库
