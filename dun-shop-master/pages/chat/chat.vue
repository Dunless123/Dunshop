<template>
	<view class="">
		<colaUChat @refresh="refreshNow" :isUseRefresh="true" userIdName="userId" userId="101"
			:moreFunction="moreFunction" :historyMsg="historyMsg" :updateMsg="updateData" :messageData="msgData"
			:isUseRead="true" :dataKeys="dataKey" IDType="msgId" @sendMsg="send" @moreClick="clickMore"></colaUChat>
	</view>
</template>

<script setup>
	import {
		onMounted,
		reactive,
		ref
	} from 'vue';
	import colaUChat from '../../uni_modules/cola-u-chat/components/cola-u-chat/cola-u-chat.vue';


	const dataKey = ref({
		userId: 'selfUserId',
		msgId: 'selfMsgId',
		name: 'userName',
		message: 'msg',
		avator: 'avator',
		img: 'img',
		read: 'read'
	})

	const msgData = ref([{
			selfUserId: '101',
			selfMsgId: '',
			userName: '我的大聪明啊',
			msg: '我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
		},
		{
			selfUserId: '202',
			selfMsgId: '',
			userName: 'xixixi',
			msg: 'hhh',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		}, {
			selfUserId: '202',
			selfMsgId: '',
			userName: 'xixixi',
			msg: 'hhh',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		},
		{
			selfUserId: '202',
			selfMsgId: '',
			userName: 'xixixi',
			msg: '',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
			img: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		},
		{
			selfUserId: '101',
			selfMsgId: '',
			userName: 'xixixi',
			msg: 'hhh',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		},
		{
			selfUserId: '101',
			selfMsgId: '',
			userName: 'xixixi',
			msg: '我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		},
		{
			selfUserId: '101',
			selfMsgId: '',
			userName: 'xixixi',
			msg: '',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
			img: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png'
		}
	])
	const historyMsg = ref({})
	const updateData = reactive({})
	const moreFunction = ref([{
			name: '照片',
			icon: 'image',
			img: ''
		},
		{
			name: '拍摄',
			icon: 'camera',
			img: ''
		},
		{
			name: '语音通话',
			icon: 'phone-filled',
			img: ''
		},
		{
			name: '位置',
			icon: 'location-filled',
			img: ''
		},
		{
			name: '红包',
			icon: 'wallet',
			img: ''
		},
		{
			name: '群工具',
			icon: 'list',
			img: ''
		}
	])


	const send = (e) => {
		updateData.value = {
			selfUserId: '101',
			selfMsgId: '',
			userName: '我的大聪明啊',
			msg: e,
			img: '',
			avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
		};
		// console.log(updateData.value);
	}
	const clickMore = (e) => {
		console.log(e);
	}
	const refreshNow = (e) => {
		console.log(e);
	}

	onMounted(() => {
		setTimeout(() => {
			updateData.value = {
				selfUserId: '101',
				selfMsgId: '',
				userName: '我的大聪明啊',
				msg: '我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪',
				img: '',
				avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
			};
		}, 3000)
		setTimeout(() => {
			updateData.value = {
				selfUserId: '202',
				selfMsgId: '',
				userName: 'xixixi',
				msg: '',
				img: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
				avator: 'https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png',
			};
		}, 5000)
	})
</script>

<style></style>