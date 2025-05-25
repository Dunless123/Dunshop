<template>
	<view class="container">
		<uni-nav-bar left-icon="left" @clickLeft="back" title="发布宝贝" :shadow="true" :border="false" :stat="false"
			@clickRight="btn">
			<template v-slot:right>
				<view class="fabu">
					发布
				</view>
			</template>
		</uni-nav-bar>
		<uni-card :is-shadow="true">
			<!-- <uni-easyinput type="textarea" autoHeight v-model="describeValue" placeholder="描述一下宝贝"></uni-easyinput> -->
			<editorVue></editorVue>
			<uni-file-picker limit="9" title="最多选择9张图片"></uni-file-picker>
		</uni-card>
		<uni-card :is-shadow="true" padding="0" spacing="0">
			<uni-list>
				<uni-list-item title="价格" showArrow rightText="0.00" clickable @click="price"></uni-list-item>
				<uni-list-item title="发货方式" showArrow rightText="下线交易" clickable @click="tradeWay"></uni-list-item>
			</uni-list>
		</uni-card>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'
	import editorVue from '../../components/editor/editor.vue'

	const describeValue = ref("")

	const btn = () => {
		uni.showToast({
			title: "fabu"
		})
	}
	const back = () => {
		uni.navigateBack({
			delta: 1
		})
	}
	const price = () => {
		uni.showToast({
			title: "price"
		})
	}
	const tradeWay = () => {
		uni.showToast({
			title: "tradeWay"
		})
	}
</script>

<style scoped lang="scss">

</style>