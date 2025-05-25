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
							<Home></Home>
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
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡4的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡5的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡6的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡7的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡8的内容
						</view>
					</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item">
						<view>
							选项卡9的内容
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
	import Home from "../../pages/home/home.vue"

	//tabars的数据项
	const items = ref([{
		name: '首页',
		id: "op1"
	}, {
		name: '热门推荐',
		id: "op2"
	}, {
		name: '最新上架',
		id: "op3"
	}, {
		name: '热门关注',
		id: "op4"
	}, {
		name: '视频租赁',
		id: "op5"
	}, {
		name: '趋势榜单',
		id: "op6"
	}, {
		name: '超低略磨',
		id: "op7"
	}, {
		name: '金贴专区',
		id: "op8"
	}, {
		name: '近期降价',
		id: "op9"
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