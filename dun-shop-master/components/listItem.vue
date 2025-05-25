<template>
	<view class="themeItem">
		<navigator url="/pages/itemDetail/itemDetail" class="box" v-if="!isMore&&!isAdd&&!noNaviEle">
			<image class="pic" :src="item.picurl" mode="aspectFill"></image>
			<uni-icons class="icon" type="checkmarkempty" size="30" v-if="popSelect"></uni-icons>
			<view class="mask">{{item.name}}</view>
		</navigator>

		<view class="box" v-if="noNaviEle">
			<image class="pic" :src="item.picurl" mode="aspectFill"></image>
			<uni-icons class="icon" type="checkmarkempty" size="30" v-if="popSelect"></uni-icons>
			<view class="mask" @tap="navigato">{{item.name}}</view>
		</view>

		<navigator url="/pages/classlist/classlist" open-type="reLaunch" class="box more" v-if="isMore">
			<image class="pic" src="/static/logo.png" mode="aspectFill"></image>
			<view class="mask">
				<uni-icons type="more-filled" size="34" color="#fff"></uni-icons>
				<view class="text">
					更多
				</view>
			</view>
		</navigator>

		<navigator url="/pages/addSale/addSale" open-type="navigate" class="box more" v-if="isAdd">
			<view class="mask">
				<uni-icons type="plusempty" size="40"></uni-icons>
			</view>
		</navigator>

	</view>
</template>

<script setup>
	defineProps({
		isMore: {
			type: Boolean,
			default: false
		},
		isAdd: {
			type: Boolean,
			default: false
		},
		popSelect: {
			type: Boolean,
			default: false
		},
		noNaviEle: {
			type: Boolean,
			default: false
		},
		item: {
			type: Object,
			default: () => {
				return {
					name: "可爱萌宠",
					picurl: "/static/logo.png",
				}
			}
		}
	})
</script>

<style scoped lang="scss">
	.themeItem {

		.box {
			height: 340rpx;
			border-radius: 10rpx;
			overflow: hidden;
			position: relative;
			z-index: 0;

			.pic {
				width: 100%;
				height: 100%;
			}

			.icon {
				position: absolute;
				right: 15rpx;
				bottom: 80rpx;
				background: rgba(255, 255, 0, 0.8);
				border-radius: 50%;
			}

			.mask {
				width: 100%;
				height: 70rpx;
				position: absolute;
				left: 0;
				bottom: 0;
				background: rgba(0, 0, 0, 0.2);
				color: #fff;
				display: flex;
				align-items: center;
				justify-content: center;
				backdrop-filter: blur(20rpx);
				font-weight: 600;
			}


		}

		.box.more {
			.mask {
				width: 100%;
				height: 100%;
				flex-direction: column;
			}

		}
	}
</style>