<template>
	<view class="content">
		<scroll-view :refresher-enabled="isUseRefresh" :refresher-threshold="100" refresher-default-style="white"
			:refresher-triggered="isFresh" refresher-background="#F2F2F2" @refresherrestore="onRestore"
			@refresherrefresh="scrollRefresh" @tap="changeIs" :enable-back-to-top="true" :show-scrollbar="false"
			:scroll-y="true" class="cola-message" :scroll-into-view="chatViewId">
			<!-- <view v-for="(item, index) in msgData" :key="index" :id="'id-'+item.time" class="cola-message__msg"> -->
			<view v-for="(msgItem, msgIndex) in selfMsgData" :key="msgIndex" :id="'id-' + msgIndex"
				:style="{ 'flex-direction': msgItem[dataKeys['userId']] === userId ? 'row-reverse' : 'row' }"
				class="cola-message__msg__line">
				<image class="cola-message__msg__line__photo" :src="msgItem[dataKeys['avator']]" mode=""></image>
				<view v-if="msgItem[dataKeys['message']]!==''" :class="[
						msgItem[dataKeys['userId']] === userId ? 'self-body' : 'other-body',
						msgItem[dataKeys['userId']] === userId ? 'msgBody-rg' : 'msgBody-lf'
					]">
					{{ msgItem[dataKeys['message']] }}
				</view>
				<image @tap="imgTap(msgItem[dataKeys['img']])" v-else :src="msgItem[dataKeys['img']]" class="self-img"
					mode=""></image>
				<view v-if="false" class="isRead">
					{{msgItem[dataKeys['read']]?'已读':'未读'}}
				</view>
			</view>
			<!-- </view> -->
		</scroll-view>
		<view :style="{'bottom':(keybordMove?'400':'0')+'rpx'}" class="bottom-keyboard">
			<view class="cola-keyboard">
				<!-- <image v-if="isUseVoice" src="" class="cola-keyboard__voice" mode=""></image> -->
				<uni-icons type="mic" size="30"></uni-icons>
				<view class="cola-keyboard__input">
					<uni-easyinput :focus="inputFocus" @tap="clickInput" @confirm="inputConfirm"
						:disabled="inputDisabled" v-model="text" placeholder="" type="text" />
				</view>
				<!-- <image class="cola-keyboard__expression" src="" mode=""></image> -->
				<!-- <uni-icons class="cola-keyboard__expression" type="star" size="30"></uni-icons> -->
				<!-- <image class="cola-keyboard__more" src="" mode=""></image> -->
				<uni-icons @tap="moreButton" class="cola-keyboard__more" type="plus" size="30"></uni-icons>
				<button @tap="inputConfirm" class="cola-keyboard__send">发送</button>
			</view>
		</view>
		<view class="bottom-more" :style="{'height':(moreIsShow? '400' : '0')+'rpx'}">
			<view @tap="buttonClick(index)" v-for="(item,index) in moreFunction" :key="index"
				class="bottom-more__block">
				<view class="bottom-more__block__img">
					<image class="bottom-more__block__img__icon" v-if="item.img!==''" src="" mode=""></image>
					<uni-icons :size="iconSize" class="bottom-more__block__img__icon" v-else
						:type="item.icon"></uni-icons>
				</view>
				<text>
					{{item.name}}
				</text>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		onMounted,
		ref,
		toRef,
		toRefs,
		watch
	} from 'vue';

	/*
	 * 接收值：
	 * userId：用户的id
	 * userIdName：用户ID的键名
	 * IDType：设置要绑定的消息ID，滚动需要
	 * msgName：消息数据的绑定键名
	 * messageData：基础展示数据
	 * updateMsg：更新的数据，对象形式
	 * historyMsg：历史数据，对象或数组形式
	 * moreFunction：更多功能的显示，数组对象形式
	 * iconSize：图标显示大小，默认40rpx
	 * isUseRefresh：是否开启下拉刷新
	 * 
	 * 
	 * 数据：
	 * text：文本框输入内容
	 * chatViewId：如果超出当前屏幕，翻滚跟踪到最后一条
	 * inputFocus：光标是否集中
	 * inputDisabled：是否禁止使用输入框
	 * TODO 没有语音输入
	 * isUseVoice：是否使用语音输入
	 * selfMsgData：内部的数据保存
	 * moreIsShow：是否显示更多功能
	 * keybordMove：输入框键盘动画
	 * isFresh：是否触发下拉刷新
	 * 
	 * 
	 * 功能：
	 * moreClick：更多功能的点击项
	 * sendMsg：发送消息
	 */

	const props = defineProps({
		isUseRead: {
			type: Boolean,
			default: false
		},
		dataKeys: {
			type: Object,
			default: () => {
				return {
					userId: 'userId',
					msgId: 'msgId',
					name: 'name',
					message: 'message',
					img: 'img',
					avator: 'avator',
					read: 'read'
				}
			}
		},
		userId: {
			type: String || Number,
			default: ''
		},
		// 已废弃
		userIdName: {
			type: String
		},
		IDType: {
			type: String,
			default: 'id'
		},
		msgName: {
			type: String,
			default: 'message'
		},
		messageData: {
			type: Array,
			default: () => {
				return []
			}
		},
		updateMsg: {
			type: Object,
			default: () => {
				return {}
			}
		},
		historyMsg: {
			type: [Object, Array],
			default: () => {
				return {}
			}
		},
		moreFunction: {
			type: Array,
			default: () => {
				return [{
					name: '照片',
					icon: 'photo',
					img: ''
				}, {
					name: '拍摄',
					icon: 'camera',
					img: ''
				}]
			}
		},
		iconSize: {
			type: String,
			default: '40'
		},
		isUseRefresh: {
			type: Boolean,
			default: false
		}
	})


	const {
		updateMsg,
		historyMsg,
		messageData,
		dataKeys,
		IDType
	} = toRefs(props)


	const emit = defineEmits(['refresh', 'sendMsg', 'moreClick'])

	const text = ref("")
	const chatViewId = ref("")
	const inputFocus = ref(false)
	const inputDisabled = ref(false)
	const isUseVoice = ref(false)
	const selfMsgData = ref([])
	const moreIsShow = ref(false)
	const keybordMove = ref(false)
	const isFresh = ref(false)
	const freshing = ref(false)

	onMounted(() => {
		/*
		userId:'101',
		msgId: Math.random().toFixed(4)*10000,
		name: '我的大聪明啊',
		message: '',
		img:'https://iknow-pic.cdn.bcebos.com/d833c895d143ad4bb4400a7d8c025aafa40f0624',
		avator: ''
		*/
		selfMsgData.value = messageData.value
		setScroll()
	})

	watch(updateMsg.value, (val) => {
		// if (Object.keys(val) === 0) {
		// 	return
		// }
		selfMsgData.value.push(val.value)
		setScroll();

	}, {
		deep: true
	})

	watch(historyMsg.value, (val, oldVal) => {
		if (val === oldVal) {
			return
		}
		setTimeout(() => {
			isFresh.value = false
			freshing.value = false
		}, 500)
		const valueType = Object.prototype.toString.call(val.value);
		if (valueType === '[object Object]') {
			selfMsgData.value.unshift(val.value)
		} else if (valueType === '[object Array]') {
			// val = val.reverse()
			// val = fixSelfData(val)
			for (let num = 0; num < val.length; num++) {
				selfMsgData.value.unshift(val.value[num])
			}
		}
	}, {
		immediate: true,
		deep: true
	})



	const scrollRefresh = (e) => {
		if (freshing.value) return;
		freshing.value = true;
		isFresh.value = true;
		emit('refresh');
	}
	const onRestore = () => {
		isFresh.value = 'restore'
	}
	const changeIs = () => {
		moreIsShow.value = false
		keybordMove.value = false
	}
	const voiceLong = () => {
		console.log('录音')
	}
	const clickInput = () => {
		inputFocus.value = false;
	}
	const inputConfirm = () => {
		if (text.value === '') {
			return
		}
		inputFocus.value = false
		emit('sendMsg', text.value)
		text.value = ''
		// this.inputFocus = true;
	}

	const setScroll = () => {
		let id = selfMsgData.value.length - 1;
		// const id = selfMsgData.value[len][IDType];
		// 我这里的id的设置规则是自己想的
		// 只要不会重复 并且不事纯number就行
		chatViewId.value = 'id-' + id;
		// #ifdef H5
		uni.pageScrollTo({
			selector: '#' + chatViewId.value
		})
		// #endif
	}
	const imgTap = (img) => {
		uni.previewImage({
			urls: [img],
			longPressActions: {
				itemList: ['发送给朋友', '保存图片', '收藏'],
				success: function(data) {
					console.log('选中了第' + (data.tapIndex + 1) + '个按钮,第' + (data.index + 1) + '张图片');
				},
				fail: function(err) {
					console.log(err.errMsg);
				}
			}
		});
	}
	const moreButton = () => {
		uni.hideKeyboard()
		keybordMove.value = true
		moreIsShow.value = true
	}

	const buttonClick = (index) => {
		emit('moreClick', index)
	}

	// const fixSelfData = (arr) => {
	// 	let keyArr = Object.keys(dataKeys.value)
	// 	let valueArr = Object.values(dataKeys.value)
	// 	const valueType = Object.prototype.toString.call(arr);
	// 	if (valueType === '[object Object]') {
	// 		let resultObj = {}
	// 		keyArr.forEach((keyItem, keyIndex) => {
	// 			resultObj[valueArr[keyIndex]] = arr[valueArr[keyIndex]]
	// 		})
	// 		return resultObj
	// 	} else if (valueType === '[object Array]') {
	// 		let resultArr = []
	// 		arr.forEach((item, index) => {
	// 			let resultObj = {}
	// 			keyArr.forEach((keyItem, keyIndex) => {
	// 				resultObj[valueArr[keyIndex]] = item[valueArr[keyIndex]]
	// 			})
	// 			resultArr.push(resultObj)
	// 		})
	// 		return resultArr
	// 	}
	// }
</script>

<style lang="scss" scoped>
	.self-body {
		color: #fff;
		background-color: #19be6b;
	}

	.other-body {
		color: #000000;
		background-color: #fff;
	}

	@mixin msgBody {
		max-width: 62%;
		padding: 15rpx;
		margin: 0 30rpx;
		border-radius: 10rpx;
		position: relative;
		word-spacing: normal;
		word-break: break-all;
		word-wrap: break-word;
	}

	.content {
		width: 100vw;
		height: 100vh;

		.cola-message {
			width: 100%;
			height: calc(100vh - 120rpx);
			padding: 30rpx;
			background-color: #f2f2f2;
			box-sizing: border-box;


			&__msg {
				width: 100%;
				min-height: 40rpx;

				&__line {
					width: 100%;
					display: flex;
					align-items: flex-start;
					position: relative;

					&:nth-child(n + 2) {
						margin-top: 40rpx;
					}

					.self-img {
						width: 340rpx;
						height: 260rpx;
						margin: 0 30rpx;
						border-radius: 10rpx;
					}

					&__photo {
						width: 100rpx;
						height: 100rpx;
						background-color: red;
						border-radius: 10rpx;
					}

					.msgBody-lf {
						@include msgBody();

						&::before {
							position: absolute;
							content: '';
							width: 0;
							height: 0;
							overflow: hidden;
							border: 15rpx solid transparent;
							border-right-color: #fff;
							left: -26rpx;
							top: 20rpx;
						}
					}

					.msgBody-rg {
						@include msgBody();

						&::before {
							position: absolute;
							content: '';
							width: 0;
							height: 0;
							overflow: hidden;
							border: 15rpx solid transparent;
							border-left-color: #19BE6B;
							right: -26rpx;
							top: 20rpx;
						}
					}

					.isRead {
						font-size: 24rpx;
						color: #B2B2B2;
					}

					.noRead {
						font-size: 24rpx;
						color: #00bc43;
					}
				}
			}
		}

		.bottom-keyboard {
			width: 100%;
			min-height: 100rpx;
			padding: 10rpx 10rpx 0rpx 0rpx;
			// padding-bottom: constant(safe-area-inset-bottom); // 核心代码
			padding-bottom: 20rpx; // 核心代码
			background-color: #f6f6f6;
			position: fixed;
			bottom: 0;
			left: 0;
			transition: bottom 0.5s;
			z-index: 99;

			.cola-keyboard {
				display: flex;
				flex-direction: row;
				align-items: center;
				justify-content: space-around;
				padding: 0rpx 0rpx 5rpx 0rpx;

				&__voice {
					width: 60rpx;
					height: 60rpx;
					background-color: blue;
				}

				&__input {
					// margin-left: 30rpx;
					height: 100%;
					width: 65%;
					background-color: #fff;
					border-radius: 10rpx;
					box-sizing: border-box;


				}

				&__expression {
					// width: 60rpx;
					// height: 60rpx;
					// margin-left: 30rpx;
				}

				&__more {
					// width: 60rpx;
					// height: 60rpx;
					// margin-left: 30rpx;
				}

				&__send {
					width: 90rpx;
					height: 60rpx;
					font-size: 25rpx;
					padding: 0;
					margin: 0;
				}
			}
		}

		.bottom-more {
			width: 100%;
			display: grid;
			grid-template-columns: repeat(5, 1fr);
			justify-content: space-around;
			position: fixed;
			bottom: 0;
			left: 0;
			transition: height 0.5s;
			background-color: #F6F6F6;

			&__block {
				// width: 120rpx;
				// height: 200rpx;
				margin-top: 5rpx;
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;
				text-align: center;

				// &:nth-child(n+2) {
				// 	margin: 5rpx 0 0 30rpx;
				// }

				&__img {
					width: 100rpx;
					height: 100rpx;
					line-height: 100rpx;
					text-align: center;
					background-color: #FFF;
					border-radius: 20rpx;
				}

				text {
					width: 100rpx;
					margin-top: 10rpx;
					font-size: 24rpx;
					color: #848588;
				}
			}

			&::after {
				display: block;
				content: '';
				flex: 1;
			}
		}
	}
</style>