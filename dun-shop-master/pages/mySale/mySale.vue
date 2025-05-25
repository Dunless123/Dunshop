<template>
	<view class="container">
		<scroll-view class="scrollView" scroll-y="true" :show-scrollbar="false" scroll-with-animation="true">
			<view class="gridSale">
				<view class="gridDetail">
					<ListItem v-for="(item,id) in listData" :key="id" :popSelect="item.checked" :noNaviEle="true"
						@tap="selectTap(item)" @longpress="navigato">
					</ListItem>
					<ListItem :isAdd="true"></ListItem>
				</view>
			</view>
			<view class="bottomLine">
				<text class="bottomText">没有更多数据</text>
			</view>
		</scroll-view>
		<uni-popup ref="popup" type="bottom" background-color="#fff" :is-mask-click="false"
			backgroundColor="rgba(247, 247, 247, 0.3)">
			<view class="AllandCancel">
				<view class="all">
					<checkbox-group>
						<label>
							<checkbox :checked="allSelectStatus" @click="allSelect" />
							全选<text>({{selectData.length}}/{{listData.length}})</text>
						</label>
					</checkbox-group>
				</view>
				<view class="cancel">
					<button type="default" size="default" @click="pCancel">取消</button>
				</view>
			</view>
			<view class="popGrid">
				<view class="popGridItem1">
					<image src="/static/logo.png" mode=""></image>
					<text>改价</text>
				</view>
				<view class="popGridItem2">
					<image src="/static/logo.png" mode=""></image>
					<text>下架</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script setup>
	import {
		onMounted,
		ref,
		watch
	} from 'vue';
	import ListItem from '../../components/listItem.vue';

	const popup = ref(null)
	const listItem = ref(null)
	const popSelectStatus = ref(false)
	const allSelectStatus = ref(false)
	const selectData = ref([])
	const listData = ref([{
		id: 1,
		checked: false
	}, {
		id: 2,
		checked: false
	}, {
		id: 3,
		checked: false
	}])

	const navigato = () => {
		uni.navigateTo({
			url: "/pages/itemDetail/itemDetail"
		})
	}

	//检测全选状态
	watch(selectData.value, (newValue) => {
		if (newValue.length == listData.value.length) {
			allSelectStatus.value = true
		} else {
			allSelectStatus.value = false
		}
	})

	const selectTap = (item) => {
		if (selectData.value.length == 0) { //避免重复打开关闭弹窗
			popSelectStatus.value = true
			popup.value.open()
			popup.value.closeMask()
			uni.hideTabBar({
				animation: true
			});
		}
		//勾选状态
		if (item.checked) {
			item.checked = false
			selectData.value.pop(item)
		} else {
			item.checked = true
			selectData.value.push(item)
		}
		//没有选择则取消
		if (selectData.value.length == 0) {
			pCancel()
		}

	}
	const pCancel = () => {
		popSelectStatus.value = false
		popup.value.close()
		setTimeout(() => {
			uni.showTabBar({
				animation: true
			});
		}, 200)
		listData.value.forEach((item) => {
			item.checked = false
		})
		selectData.value.length = 0

	}
	const allSelect = () => {
		if (allSelectStatus.value) {
			allSelectStatus.value = false
			pCancel()
		} else {
			allSelectStatus.value = true
			listData.value.forEach((item) => {
				if (selectData.value.includes(item)) {
					return
				} else {
					item.checked = true
					selectData.value.push(item)
				}
			})
		}
	}
</script>

<style scoped lang="scss">
	.container {
		.scrollView {
			height: 100vh;

			::-webkit-scrollbar {
				display: none;
			}

			.gridSale {
				margin-top: 15rpx;

				.gridDetail {
					padding: 30rpx;
					display: grid;
					grid-template-columns: repeat(2, 1fr);
					gap: 15px;
				}

			}

			.bottomLine {
				width: 100%;
				height: 120rpx;
				display: flex;
				align-items: center;
				justify-content: center;

				.bottomText {
					width: 100%;
					height: 80rpx;
					text-align: center;

				}

				padding-bottom: 20rpx;
			}

		}

		.AllandCancel {
			display: flex;
			justify-content: space-between;
			padding: 10rpx;

			.cancel {
				width: 100rpx;
				height: 50rpx;

				& button {
					width: 100%;
					height: 100%;
					font-size: 12px;
				}


			}
		}

		.popGrid {
			display: flex;
			justify-content: space-around;

			& view {
				width: 120rpx;
				height: 150rpx;
				display: flex;
				flex-direction: column;
				text-align: center;

				image {
					width: 120rpx;
					height: 120rpx;
				}
			}


		}



	}
</style>