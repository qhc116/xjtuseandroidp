# xjtuseandroidp

# 爬坑记录


# 安卓端


## 使用到的组件或技术
轮播图、RecyclerView、百度SDK、SharePrefrernce、ContentProvider、OkHttp、Canvas、Decorator	

## 登录注册
- 使用OkHttp调用后端接口，完成登录、注册功能，并用SharePrefrernce存储用户登录状态以及用户信息
- 使用轮播图美化登录界面

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
nodejs、exoress、multer、redis、JWT、mongodb

## 登录注册
- 使用express实现restful模式的用户登录注册功能
- 使用mogodb存储用户账户密码以及人脸信息

## 文件上传下载
- 使用exoress、multer和mongodb存储图片信息以及Base64格式的图片
- 使用express提供文件下载接口

## 安全策略
- 使用JWT加密用户信息并返回给前端保存，完成用户身份的记载以及服务器权限认证
- 使用redis完成用户的单点登录功能

