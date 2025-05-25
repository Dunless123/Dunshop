<template>
	<view class="container">
		<scroll-view class="scrollView" scroll-y="true" :show-scrollbar="false" scroll-with-animation="true">
			<view class="searchBar" @click="focus">
				<uni-search-bar cancelButton="none" placeholder="请输入查询信息">
				</uni-search-bar>
			</view>
			<view class="gridBottom">
				<view class="gridTitle">
					<view class="left">
						<text>推荐</text>
						<uni-icons type="right"></uni-icons>
					</view>
					<view class="right">
						<view class="rtext">
							<text>查看更多</text>
						</view>
					</view>
				</view>
				<view class="gridDetail">
					<ListItem v-for="(item,index) in 8" :key="index"></ListItem>
				</view>
			</view>
			<view class="bottomLine">
				<text class="bottomText">没有更多数据</text>
			</view>
		</scroll-view>

	</view>
</template>

<script setup>
	import ListItem from '../../components/listItem.vue'
	const focus = (e) => {
		uni.navigateTo({
			url: "../../pages/search/search"
		})
	}
	const change = (e) => {
		uni.showToast({
			title: `点击第${e.detail.index+1}个宫格`,
			icon: 'none'
		})
	}
</script>

<style scoped lang="scss">
	.container {
		.scrollView {
			height: 100vh;



			::-webkit-scrollbar {
				display: none;
			}

			.searchBar {
				margin-top: 10rpx;

			}

			// .gridCenter {
			// 	width: 100%;
			// 	margin-top: 15rpx;

			// 	.uni-row {

			// 		display: flex;
			// 		flex-direction: row;
			// 		justify-content: space-around;

			// 		.uni-col {
			// 			width: 170rpx;
			// 			height: 170rpx;
			// 			border: 1px black solid;
			// 		}
			// 	}
			// }

			.gridBottom {
				margin-top: 15rpx;


				.gridTitle {
					display: flex;
					flex-direction: row;
					justify-content: space-between;
					margin: 0 15rpx;

					.right {
						width: 100rpx;
						height: 40rpx;
						border: 1px black solid;
						border-radius: 25rpx;

						.rtext {
							text-align: center;
							line-height: 40rpx;
							font-size: 10px;
						}
					}
				}

				.gridDetail {
					display: grid;
					grid-template-columns: repeat(2, 1fr);
					gap: 15rpx;
					padding: 20rpx;
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


	}
</style>