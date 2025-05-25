<template>
	<view class="container">
		<view class="tabars">
			<scroll-view scroll-x="true" class="tabarsScroll" :show-scrollbar="false" :scroll-into-view="scrollViewId"
				scroll-with-animation="true">
				<view :id="items[index].id" :class='current==index?"tabar active":"tabar"' v-for="(item,index) in items"
					:key="index" @click="onClickItem(index)">
					{{item.name}}
				</view>
			</scroll-view>
		</view>
		<view class="content">
			<swiper class="uniSwiper" :duration="500" @change="onSwiperItem" :current="current">
				<swiper-item>
					<view class="swiper-item">
						<view>
							<MySale></MySale>
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡2的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡3的内容
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue';
	import MySale from "../mySale/mySale.vue"

	//tabars的数据项
	const items = ref([{
		name: '我的出售',
		id: "op1"
	}, {
		name: '待发货',
		id: "op2"
	}, {
		name: '还价管理',
		id: "op3"
	}])
	//scrollView定位的子元素id
	const scrollViewId = ref("")

	const current = ref(0)

	const onClickItem = (e) => {
		if (current.value != e) {
			current.value = e
		}
	}

	const onSwiperItem = (e) => {
		scrollViewId.value = items.value[e.detail.current].id
		current.value = e.detail.current
	}
</script>

<style scoped lang="scss">
	.container {
		display: flex;
		flex-direction: column;
		justify-content: space-around;
		height: 100vh;

		.tabars {
			.tabarsScroll {

				white-space: nowrap;

				.tabar {
					width: 120rpx;
					height: 40rpx;
					font-size: 26rpx;
					display: inline-block;
					text-align: center;
					padding-top: 5rpx;
					color: rgb(157, 157, 157);

				}

				.active {
					font-size: 30rpx;
					color: rgb(70, 70, 70);
				}
			}
		}

		.content {
			flex: 1;

			.uniSwiper {
				width: 100%;
				height: 100%;

				.swiper-item {
					width: 100%;
					height: 100%;
				}
			}
		}
	}
</style>