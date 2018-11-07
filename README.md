## 一个视频播放器
- 支持列图表中播放
- 支持无缝切换至全屏播放

## 支持功能
- RecycleView自动播放辅助类
- 小屏/全屏（垂直）/全屏（横屏） 无缝切换
- 播放暂停
- 静音实现(列表中播放静音 进入全屏有声音)
- 滑动调节声音
- 滑动调节亮度
- wifi自动播放流量提示播放
- 播放失败提示

## 使用

### 权限

需要如下两个权限:
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 配置
Acitivity需要配置:
```
android:configChanges="orientation|keyboardHidden|screenSize"
```

具体使用请看demo app 只有一个acitivty 非常的简单