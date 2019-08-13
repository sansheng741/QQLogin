# QQLogin
QQ登录功能

### 使用时需注意修改以下地方
**1**\
application.yml中qq.oauth.http的值修改为QQ互联中填写的网站地址

**2**\
QQHttpClient中

    public static final String APPID = "xxxxxxxx";
    public static final String APPKEY = "xxxxxxxxxx";
    
**3**\
本地调试需要修改host文件,将域名映射到本地

```
127.0.0.1 www.sansheng2019.cn
