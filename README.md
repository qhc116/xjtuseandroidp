# 此处前半部记载亮点、坑、项目不足、项目总结，后半部详细介绍功能
# 亮点和坑
## 1.亮点：数据来源
- 亮点

使用python爬取bing图片15000+张图片，后调用百度人脸识别完善数据库
- 坑

百度的人脸接口是异步的，高并发执行的时候会导致回传信息和上传信息不一致，导致信息标记错误
后采用上传后等待500ms的方式解决问题

## 2.亮点：单点登录、用户授权
- 亮点

使用redis、jwt、mongodb配合做用户验证、接口授权、登录信息过期、加密加盐存储用户密码。

使用service请求授权接口实时认证用户信息是否异地登录，如果异地登录，发送广播清空所有Activity任务栈。

- 坑

1.express路有前需要拦截所有restful请求。此处对next和回调函数理解不足，导致拦截方面卡了很久。

2.无法外部访问阿里云上安装的redis，见小组成员简书
> https://www.jianshu.com/p/011dcec5e31e
## 3.坑：mongoose创建的数据找不到
见小组成员简书
> https://www.jianshu.com/p/8767fa0ebf0a?

## 4.亮点：云相册中实现人脸排序、人脸搜索
- 亮点

在云相册中实现人脸排序和人脸搜索功能
- 坑

上传时组织和存储相册、相片信息等数据结构的设计

## 5.坑：nodejs回传数据全部为undefined

对nodejs回调函数不熟悉，思维定性的想传出外边处理，结果前端一直收不到数据

## 6.亮点：跳转B站
- 亮点

识别出库中人物后可以跳转B站查看视频
- 坑

跳转B站后过两秒自动报错
原因：网页上js自动发起ajax请求B站APP特有协议视频地址"bilibili：//xxxxxx"导致loadUrl出错
解决：在加载的时候要拦截所有非http、https协议的地址

# 项目不足：
- 开发人员代码过于耦合，同步成本过高，甚至还会出现新的BUG
- 很多逻辑上的BUG来不及处理，导致程序FC
- 文档编写经验不足，三位组员都是首次接触文档编写，对文档过于轻视
- 对git不熟悉，在push的时候没有先同步代码，而是采用$git push -f导致代码库版本丢失
# 项目总结：
经过本次课程项目，我们学习到了许多新知识，完全不一样的理念，意识到了很多协同开发产生的问题，踩过了很多初学者的坑，也享受了探索中痛并快乐着的过程。首次接触到了全栈开发的理念，在做完项目之后真的感觉到了观念上发生的变化，敏捷开发的过程中也能时时刻刻感受到成长。
最后感谢余老师为我们三位跨专业的小白提供了新颖的概念和宽阔的视野！

# 以下为详细功能及介绍
# 安卓端

## 使用到的组件或技术
四大组件：Activity、BroadcastReceiver、ContentProvider、Service

WebView、第三方内核、轮播图、RecyclerView、SQLite、百度SDK、SharePrefrernce、OkHttp、Canvas、Decorator	

## 登录注册
- 使用OkHttp调用后端接口，完成登录、注册功能
- 使用百度离线SDK结合后端接口调用百度API实现人脸注册、人脸登录功能
- 使用轮播图美化登录界面
- 使用SharePrefrernce存储用户登录状态以及用户信息
- 使用Service、BroadcastReceiver完成SSO登录

### 人脸识别
- 使用OkHttp及百度SDK接口，完成人脸注册、人脸搜索功能
- 使用bitmap的compress完成文件的压缩
- 使用Canvas标注人脸信息

### 云相册
- 使用OkHttp及百度SDK接口，初始化图片信息，并上传到服务器
- 使用SharePrefrernce和Decorator完成相册界面的布局和照片展示
- 使用之前上传的的图片信息完成人脸排序功能
- 使用ContentProvider完成人脸搜索功能

# 服务器端
## 使用到的组件或技术
nodejs、express、multer、redis、JWT、mongodb、crypto

## 登录注册
- 使用express实现restful模式的用户登录注册功能
- 使用mogodb存储用户账户密码以及人脸信息

## 文件上传下载
- 使用express、multer和mongodb存储图片信息以及Base64格式的图片
- 使用express提供文件下载接口

## 安全策略
- MongoDB数据库中使用sha-256加密存储用户密码
- 使用JWT加密用户信息，完成用户身份的记载以及服务器认证
- 路由前拦截访问请求，确认权限及用户身份
- 使用redis配合JWT完成用户的单点登录及身份过期功能

