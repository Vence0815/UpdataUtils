[![](https://jitpack.io/v/Vence0815/UpdataUtils.svg)](https://jitpack.io/#Vence0815/UpdataUtils)
# UpdataUtils
更新功能模块,以后会持续更新

## 使用步骤
### android studio导入
`compile 'com.github.Vence0815:UpdataUtils:master-SNAPSHOT'`

传入参数： Context，url(请求更新的接口)，当前版本号，appid，at，是否弹提示，下载文件名

示例代码:
           
	     
	     UpdateUtils.checkUpdate(this, ProjectConfig.NAMESPACE+"/App/version",
                AndroidUtils.getVersionCode(this), ProjectConfig.appid, myApplication.getAccessToken(),
                false, ProjectConfig.SHP_NAME+ AndroidUtils.getAppVersionName(this)+"update.apk");
	     
