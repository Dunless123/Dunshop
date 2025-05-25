<template>
	<view class="searchLayout">
		<uni-search-bar class="search" :focus="true" v-model="searchValue" @blur="blur" @focus="focus" @input="input"
			cancelButton="none">
		</uni-search-bar>
		<view v-if="!classList.length || noSearch">
			<view class="history" v-if="historySearch.length">
				<view class="topTitle">
					<view class="text">最近搜索</view>
					<view class="icon" @click="removeHistory">
						<uni-icons type="trash" size="25"></uni-icons>
					</view>
				</view>
				<view class="tabs">
					<view class="tab" v-for="tab in historySearch" :key="tab" @click="clickTab(tab)">{{tab}}</view>
				</view>
			</view>

			<view class="recommend">
				<view class="topTitle">
					<view class="text">热门搜索</view>
				</view>
				<view class="tabs">
					<view class="tab" v-for="tab in recommendList" :key="tab" @click="clickTab(tab)">{{tab}}</view>
				</view>
			</view>
		</view>


		<view class="noSearch" v-if="noSearch">
			<uv-empty mode="search" icon="http://cdn.uviewui.com/uview/empty/search.png"></uv-empty>
		</view>


		<view v-else>
			<view class="list">
				<navigator :url="`/pages/preview/preview?id=${item._id}`" class="item" v-for="item in classList"
					:key="item._id">
					<image :src="item.smallPicurl" mode="aspectFill"></image>
				</navigator>
			</view>
			<view class="loadingLayout" v-if="noData || classList.length">
				<uni-load-more :status="noData?'noMore':'loading'" />
			</view>
		</view>

	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue';
	import {
		onLoad,
		onUnload,
		onReachBottom
	} from "@dcloudio/uni-app";
	// import {
	// 	apiSearchData
	// } from "../../aip/aip";
	//查询参数
	const queryParams = ref({
		pageNum: 1,
		pageSize: 12,
		keyword: ""
	})
	const searchValue = ref('0')
	const search = (res) => {
		uni.showToast({
			title: '搜索：' + res.value,
			icon: 'none'
		})
	}
	const input = (res) => {
		// console.log('----input:', res)
	}
	const blur = (res) => {
		uni.showToast({
			title: 'blur事件，输入值为：' + res.value,
			icon: 'none'
		})
	}
	const focus = (e) => {
		uni.showToast({
			title: 'focus事件，输出值为：' + e.value,
			icon: 'none'
		})
	}
	//搜索历史词
	const historySearch = ref(uni.getStorageSync("historySearch") || []);

	//热门搜索词
	const recommendList = ref(["美女", "帅哥", "宠物", "卡通"]);

	//没有更多
	const noData = ref(false);
	//没有搜索结果
	const noSearch = ref(false);

	//搜索结果列表
	const classList = ref([]);


	//点击搜索
	const onSearch = () => {
		uni.showLoading()
		historySearch.value = [...new Set([queryParams.value.keyword, ...historySearch.value])].slice(0, 10);

		uni.setStorageSync("historySearch", historySearch.value);
		initParams(queryParams.value.keyword);
		searchData();
		console.log(queryParams.value.keyword);
	}

	//点击清除按钮
	const onClear = () => {
		initParams();
	}



	//点击标签进行搜索
	const clickTab = (value) => {
		initParams(value);

		onSearch();
	}


	//点击清空搜索记录
	const removeHistory = () => {
		uni.showModal({
			title: "是否清空历史搜索？",
			success: res => {
				if (res.confirm) {
					uni.removeStorageSync("historySearch");
					historySearch.value = []
				}
			}
		})
	}

	const searchData = async () => {
		try {
			// let res = await apiSearchData(queryParams.value);
			classList.value = [...classList.value, ...res.data];
			uni.setStorageSync("storgClassList", classList.value);
			if (queryParams.value.pageSize > res.data.length) noData.value = true;
			if (res.data.length == 0 && classList.value.length == 0) noSearch.value = true;
			console.log(res);
		} finally {
			uni.hideLoading()
		}

	}

	const initParams = (value = '') => {
		classList.value = [];
		noData.value = false;
		noSearch.value = false;
		queryParams.value = {
			pageNum: 1,
			pageSize: 12,
			keyword: value || ""
		}
	}


	//触底加载更多
	onReachBottom(() => {
		if (noData.value) return;
		queryParams.value.pageNum++
		searchData();
	})

	//关闭有页面
	onUnload(() => {
		uni.removeStorageSync("storgClassList", classList.value);
	})
</script>

<style scoped lang="scss">
	.searchLayout {
		.search {
			// padding: 5rpx;

		}

		.topTitle {
			display: flex;
			justify-content: space-between;
			align-items: center;
			font-size: 32rpx;
			color: #999;
		}

		.history {
			padding: 10rpx;
		}

		.recommend {
			padding: 10rpx;
		}

		.tabs {
			display: flex;
			align-items: center;
			flex-wrap: wrap;
			padding-top: 10rpx;

			.tab {
				background: #F4F4F4;
				font-size: 28rpx;
				color: #333;
				padding: 10rpx 28rpx;
				border-radius: 50rpx;
				margin-right: 20rpx;
				margin-top: 20rpx;
			}
		}

		.list {
			display: grid;
			grid-template-columns: repeat(3, 1fr);
			gap: 5rpx;
			padding: 20rpx 5rpx;

			.item {
				height: 440rpx;

				image {
					width: 100%;
					height: 100%;
					display: block;
				}
			}
		}
	}
</style>