# OneX Shop API
OneX商城首页

## description
为商城提供用户端接口,功能包括
- [] 微信登录
- [] 短信登录
- [] 商城首页配置信息,包括搜索功能的开放、滚动跑马灯等
- [] 商品分类的查看
- [] 商品列表和详情的查看
- [] 内容管理,以支持相关协议、文章展示等
- [] ......

## 授权相关
所有与用户相关的业务请求，都需要带上授权key,具体见左侧[Authorize]
否则会返回code为401的授权码。
授权策略可以在onex.yml中自定义配置