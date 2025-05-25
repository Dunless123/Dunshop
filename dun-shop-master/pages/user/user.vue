<template>
	<view>
		<uni-card>
			<template v-slot:title>
				<view class="header">
					<!-- 卡片标题 -->
					<view class="header-box">
						<view class="header-avatar">
							<image class="header-avatar-image"
								:src="'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'"
								mode="aspectFit" />
						</view>
						<view class="header-content">
							<text class="header-content-title uni-ellipsis"> 名字 </text>
							<text class="header-content-subtitle uni-ellipsis"> 等级 </text>
						</view>
					</view>
					<view class="header-extra">
						<button class="header-extra-text" type="default" @click="login">点击登录</button>
					</view>
				</view>
			</template>
			<uni-row>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">订单</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">钱包</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">收藏</text>
					</view>
				</uni-col>
			</uni-row>
		</uni-card>

		<swiper class="adSwiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000"
			:circular="true">
			<swiper-item>
				<view class="swiper-item">
					<image src="/static/logo.png" class="image" mode="scaleToFill" />
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="/static/logo.png" class="image" mode="scaleToFill" />
				</view>
			</swiper-item>
			<swiper-item>
				<view class="swiper-item">
					<image src="/static/logo.png" class="image" mode="scaleToFill" />
				</view>
			</swiper-item>
		</swiper>

		<uni-card title="我的交易">
			<uni-row>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFit" />
						<text class="t">我的购买</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">我的售出</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">我的还价</text>
					</view>
				</uni-col>
			</uni-row>
		</uni-card>

		<uni-card title="我的服务">
			<uni-row>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">帮助中心</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">问题反馈</text>
					</view>
				</uni-col>
				<uni-col :span="8">
					<view class="item">
						<image src="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" class="image"
							mode="aspectFill" />
						<text class="t">裁判中心</text>
					</view>
				</uni-col>
			</uni-row>
		</uni-card>


	</view>
</template>

<script setup>
	const login = () => {
		uni.navigateTo({
			url: "/pages/login/login"
		})
	}
</script>

<style scoped lang="scss">
	.header {
		display: flex;
		border-bottom: 1px #EBEEF5 solid;
		flex-direction: row;
		align-items: center;
		padding: 10px;
		overflow: hidden;

		.header-box {
			display: flex;
			flex: 1;
			flex-direction: row;
			align-items: center;
			overflow: hidden;
		}

		.header-avatar {
			width: 40px;
			height: 40px;
			overflow: hidden;
			border-radius: 5px;
			margin-right: 10px;

			.header-avatar-image {
				flex: 1;
				width: 40px;
				height: 40px;
			}
		}

		.header-content {
			display: flex;
			flex-direction: column;
			justify-content: center;
			flex: 1;
			// height: 40px;
			overflow: hidden;

			.header-content-title {
				font-size: 15px;
				color: #3a3a3a;
				// line-height: 22px;
			}

			.header-content-subtitle {
				font-size: 12px;
				margin-top: 5px;
				color: #909399;
			}
		}

		.header-extra {
			line-height: 12px;

			.header-extra-text {
				font-size: 12px;
				color: #909399;
			}
		}
	}

	.item {
		width: 100%;
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin: auto;

		.image {
			width: 70rpx;
			height: 70rpx;

		}
	}

	.adSwiper {
		// margin: 0 10rpx;

		.swiper-item {
			width: 90%;
			height: 100%;
			margin: auto;

			.image {
				width: 100%;
				height: 100%;

			}
		}
	}
</style>