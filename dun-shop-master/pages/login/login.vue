<!-- 免密登录页 -->
<template>
	<view class="uni-content">
		<!-- 顶部文字 -->
		<text class="title">请选择登录方式</text>
		<!-- 快捷登录框 当url带参数时有效 -->
		<template>
			<text class="tip">将根据第三方账号服务平台的授权范围获取你的信息</text>
			<view class="login-logo">
				<image :src="avatar"></image>
				<text>{{name}}</text>
			</view>
			<view class="quickLogin">
				<image @click="login" src="/static/weixin.png" mode="widthFix" class="quickLoginBtn"></image>
			</view>
		</template>
		<!-- 固定定位的快捷登录按钮 -->

	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'
	import {
		getSessionIdLogin
	} from '../../api/api'

	const avatar = ref("")
	const name = ref("")

	const login = () => {
		uni.login({
			"provider": "weixin",
			"onlyAuthorize": true, // 微信登录仅请求授权认证
			success: async function(event) {
				const {
					code
				} = event

				// let res = await getSessionIdLogin({
				// 	code
				// })
				// console.log(res);

				uni.getUserInfo({
					provider: 'weixin',
					success: function(infoRes) {
						console.log(infoRes.userInfo);
						avatar.value = infoRes.userInfo.avatarUrl
						name.value = infoRes.userInfo.nickName
					}
				});
				//客户端成功获取授权临时票据（code）,向业务服务器发起登录请求。
				// uni.request({
				// 	url: 'http://www.dundunteam.com:8080/user/getSessionId',
				// 	method: "GET",
				// 	data: {
				// 		code: event.code
				// 	},
				// 	success: (res) => {
				// 		//获得token完成登录
				// 		console.log(res.data);
				// 		// uni.setStorageSync('token', res.token)
				// 	}
				// });
			},
			fail: function(err) {
				// 登录授权失败
				// err.code是错误码
				uni.showToast({
					title: err.data,
					icon: "error"
				})
			}
		})
	}
</script>

<style lang="scss" scoped>
	.uni-content {
		padding: 0 60rpx;
	}

	.login-logo {
		display: flex;
		flex-direction: column;
		margin: auto;
		align-items: center;

		& image {
			width: 100rpx;
			height: 100rpx;
		}

		& text {
			font-size: 30rpx;
		}
	}

	.title {
		padding: 18px 0;
		font-weight: 800;
		flex-direction: column;
	}

	.tip {
		color: #BDBDC0;
		font-size: 11px;
		margin: 6px 0;
	}

	.input-box {
		height: 44px;
		background-color: #F8F8F8 !important;
		border-radius: 0;
		font-size: 14px;
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		flex: 1;
	}


	.uni-content ::v-deep .uni-forms-item__inner {
		padding-bottom: 8px;
	}

	@media screen and (min-width: 690px) {
		.uni-content {
			height: 350px;
		}
	}


	.uni-content,
	.quickLogin {
		/* #ifndef APP-NVUE */
		display: flex;
		flex-direction: column;
		/* #endif */
	}

	/* #ifdef MP */
	// 解决小程序端开启虚拟节点virtualHost引起的 class = input-box丢失的问题 [详情参考](https://uniapp.dcloud.net.cn/matter.html#%E5%90%84%E5%AE%B6%E5%B0%8F%E7%A8%8B%E5%BA%8F%E5%AE%9E%E7%8E%B0%E6%9C%BA%E5%88%B6%E4%B8%8D%E5%90%8C-%E5%8F%AF%E8%83%BD%E5%AD%98%E5%9C%A8%E7%9A%84%E5%B9%B3%E5%8F%B0%E5%85%BC%E5%AE%B9%E9%97%AE%E9%A2%98)
	.phone-box ::v-deep .uni-easyinput__content,
	/* #endif */
	.input-box {
		/* #ifndef APP-NVUE */
		box-sizing: border-box;
		/* #endif */
		flex: 1;
		padding-left: 45px;
		margin-bottom: 10px;
		border-radius: 0;
	}

	.quickLogin {
		height: 350px;
		align-items: center;
		justify-content: center;
	}

	.quickLoginBtn {
		margin: 20px 0;
		width: 450rpx;
		background-color: transparent;
		border: none;
		box-shadow: none;
		/* #ifndef APP-NVUE */
		max-width: 230px;
		/* #endif */
		height: 82rpx;
	}

	.tip {
		margin-top: -15px;
		margin-bottom: 20px;
	}

	@media screen and (min-width: 690px) {
		.quickLogin {
			height: auto;
		}
	}
</style>