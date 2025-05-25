<template>
	<view class="container">
		<uni-card>
			<view class="userInfo">
				<view class="left">
					<image src="/static/logo.png" mode="widthFix"></image>
				</view>
				<view class="right">
					<text class="name">我的天哪我的天哪我的天哪</text>
					<text class="level">等级</text>
				</view>
			</view>
		</uni-card>
		<uni-card>
			<view class="price">
				<text>￥999</text>
			</view>
		</uni-card>
		<uni-card>
			<view class="q-container">
				<rich-text class="q-editor" :nodes="html"></rich-text>
			</view>
		</uni-card>
		<uni-card>
			<view class="images">
				<view class="uni-file-picker__container">
					<view class="file-picker__box" v-for="(item,index) in filesList" :key="index">
						<view class="file-picker__box-content">
							<image class="file-image" :src="item.url" mode="aspectFill" @tap="prviewImage(item,index)">
							</image>
						</view>
					</view>
				</view>
			</view>
		</uni-card>
		<view class="fill"></view>
		<view class="goods-carts">
			<uni-goods-nav :options="options" :fill="true" :button-group="buttonGroup" @click="onClick"
				@buttonClick="buttonClick" />
		</view>

	</view>
</template>

<script setup>
	import {
		onMounted,
		ref
	} from 'vue'

	const html = ref()

	const options = ref([{
		icon: 'headphones',
		text: '客服'
	}, {
		icon: 'shop',
		text: '店铺',
		infoBackgroundColor: '#007aff',
		infoColor: "red"
	}])
	const buttonGroup = ref([{
			text: '聊一聊',
			backgroundColor: '#ff0000',
			color: '#fff'
		},
		{
			text: '我想要',
			backgroundColor: '#ffa200',
			color: '#fff'
		}
	])

	const onClick = (e) => {
		uni.showToast({
			title: e.content.text,
			icon: 'none'
		})
	}
	const buttonClick = (e) => {
		uni.showToast({
			title: e.content.text,
			icon: 'none'
		})
	}

	const filesList = ref([{
		url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
	}, {
		url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
	}, {
		url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
	}, {
		url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
	}])

	const prviewImage = (img, index) => {
		let urls = []

		filesList.value.forEach(i => {
			urls.push(i.url)
		})

		uni.previewImage({
			urls: urls,
			current: index
		});
	}

	onMounted(() => {
		html.value = uni.getStorageSync("html")
	})
</script>

<style scoped lang="scss">
	.container {
		// padding: 15rpx;

		.userInfo {
			width: 100%;
			height: 100rpx;
			display: flex;
			align-items: center;

			.left {
				display: flex;
				align-items: center;

				& image {
					width: 80rpx;
					height: 80rpx;
				}
			}

			.right {
				margin-left: 20rpx;
				display: flex;
				flex-direction: column;

				.name {
					font-weight: 1000;
					font-size: 28rpx;
				}

				.level {
					font-size: 22rpx;
				}
			}
		}

		.price {
			display: flex;
			align-items: center;

			& text {
				color: red;
				font-size: 40rpx;
			}
		}

		.images {
			.uni-file-picker__container {
				/* #ifndef APP-NVUE */
				display: flex;
				box-sizing: border-box;
				/* #endif */
				flex-wrap: wrap;
				margin: -5px;
			}

			.file-picker__box {
				position: relative;
				// flex: 0 0 33.3%;
				width: 33.3%;
				height: 0;
				padding-top: 33.33%;
				/* #ifndef APP-NVUE */
				box-sizing: border-box;
				/* #endif */
			}

			.file-picker__box-content {
				position: absolute;
				top: 0;
				right: 0;
				bottom: 0;
				left: 0;
				margin: 5px;
				border: 1px #eee solid;
				border-radius: 5px;
				overflow: hidden;
			}

			.file-picker__mask {
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				justify-content: center;
				align-items: center;
				position: absolute;
				right: 0;
				top: 0;
				bottom: 0;
				left: 0;
				color: #fff;
				font-size: 12px;
				background-color: rgba(0, 0, 0, 0.4);
			}

			.file-image {
				width: 100%;
				height: 100%;
			}

		}

		.fill {
			width: 100%;
			height: 100rpx;
		}

		.goods-carts {
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			flex-direction: column;
			position: fixed;
			left: 0;
			right: 0;
			/* #ifdef H5 */
			left: var(--window-left);
			right: var(--window-right);
			/* #endif */
			bottom: 0;
		}


	}
</style>