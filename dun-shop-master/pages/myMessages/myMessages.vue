<template>
	<view>
		<uni-list>
			<uni-list :border="true">
				<uni-list-chat title="uni-app"
					avatar="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" note="您收到一条新的消息"
					time="2020-02-02 20:20" badge-text="12" link="navigateTo"
					to="/pages/chat/chat?id=1&myid=2"></uni-list-chat>
				<uni-list-chat title="uni-app"
					avatar="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" note="您收到一条新的消息"
					time="2020-02-02 20:20" badge-text="12" link="navigateTo" to="/pages/chat/chat"></uni-list-chat>
				<uni-list-chat title="uni-app"
					avatar="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" note="您收到一条新的消息"
					time="2020-02-02 20:20" badge-text="12" link="navigateTo" to="/pages/chat/chat"></uni-list-chat>
				<uni-list-chat title="uni-app"
					avatar="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" note="您收到一条新的消息"
					time="2020-02-02 20:20" badge-text="12" link="navigateTo" to="/pages/chat/chat"></uni-list-chat>
				<uni-list-chat title="uni-app"
					avatar="https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png" note="您收到一条新的消息"
					time="2020-02-02 20:20" badge-text="12" link="navigateTo" to="/pages/chat/chat"></uni-list-chat>

			</uni-list>
		</uni-list>
	</view>
</template>

<script setup>
	const chatDetail = () => {
		uni.navigateTo({
			url: "/pages/chat/chat"
		})
	}
</script>

<style scoped lang="scss">

</style>