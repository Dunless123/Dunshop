<template>
	<view class="container">
		<view class="page-body">
			<view class='wrapper'>
				<view class='toolbar' @tap="format" style="height: 120px;overflow-y: auto;">
					<view :class="formats.bold ? 'ql-active' : ''" class="iconfont icon-zitijiacu" data-name="bold">
					</view>
					<view :class="formats.italic ? 'ql-active' : ''" class="iconfont icon-zitixieti" data-name="italic">
					</view>
					<view :class="formats.underline ? 'ql-active' : ''" class="iconfont icon-zitixiahuaxian"
						data-name="underline"></view>
					<view :class="formats.strike ? 'ql-active' : ''" class="iconfont icon-zitishanchuxian"
						data-name="strike"></view>
					<view :class="formats.align === 'center' ? 'ql-active' : ''" class="iconfont icon-juzhongduiqi"
						data-name="align" data-value="center"></view>
					<view :class="formats.align === 'right' ? 'ql-active' : ''" class="iconfont icon-youduiqi"
						data-name="align" data-value="right"></view>
					<view :class="formats.align === 'justify' ? 'ql-active' : ''" class="iconfont icon-zuoyouduiqi"
						data-name="align" data-value="justify"></view>
					<view class="iconfont icon-clearedformat" @tap="removeFormat"></view>
					<view :class="formats.color === '#0000ff' ? 'ql-active' : ''" class="iconfont icon-text_color"
						data-name="color" data-value="#0000ff"></view>
					<view :class="formats.backgroundColor === '#00ff00' ? 'ql-active' : ''"
						class="iconfont icon-fontbgcolor" data-name="backgroundColor" data-value="#00ff00">
					</view>
					<view class="iconfont icon-date" @tap="insertDate"></view>
					<view class="iconfont icon--checklist" data-name="list" data-value="check"></view>
					<view :class="formats.list === 'ordered' ? 'ql-active' : ''" class="iconfont icon-youxupailie"
						data-name="list" data-value="ordered"></view>
					<view :class="formats.list === 'bullet' ? 'ql-active' : ''" class="iconfont icon-wuxupailie"
						data-name="list" data-value="bullet"></view>
					<view class="iconfont icon-undo" @tap="undo"></view>
					<view class="iconfont icon-redo" @tap="redo"></view>
					<view class="iconfont icon-outdent" data-name="indent" data-value="-1"></view>
					<view class="iconfont icon-indent" data-name="indent" data-value="+1"></view>
					<view class="iconfont icon-fengexian" @tap="insertDivider"></view>
					<view class="iconfont icon-charutupian" @tap="insertImage"></view>
					<view :class="formats.header === 1 ? 'ql-active' : ''" class="iconfont icon-format-header-1"
						data-name="header" :data-value="1"></view>
					<view :class="formats.script === 'sub' ? 'ql-active' : ''" class="iconfont icon-zitixiabiao"
						data-name="script" data-value="sub"></view>
					<view :class="formats.script === 'super' ? 'ql-active' : ''" class="iconfont icon-zitishangbiao"
						data-name="script" data-value="super"></view>
					<view class="iconfont icon-shanchu" @tap="clear"></view>
					<view :class="formats.direction === 'rtl' ? 'ql-active' : ''" class="iconfont icon-direction-rtl"
						data-name="direction" data-value="rtl"></view>
				</view>
				<view class="editor-wrapper">
					<editor id="editor" class="ql-container" placeholder="开始输入..." show-img-size show-img-toolbar
						show-img-resize @statuschange="onStatusChange" :read-only="readOnly" @ready="onEditorReady">
					</editor>
				</view>
				<button type="default" @tap="getContents">获取内容</button>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'

	const readOnly = ref(false)
	const formats = ref({})
	const editorCtx = ref()
	const html = ref()

	const instance = getCurrentInstance();
	const query = uni.createSelectorQuery().in(instance.proxy);
	query
		.select("#editor")
		.context((res) => {
			editorCtx.value = res.context
		})
		.exec();

	const readOnlyChange = () => {
		readOnly.value = !readOnly.value
	}
	const onEditorReady = () => {
		// uni.createSelectorQuery().select('#editor').context((res) => {
		// 	console.log(res);
		// 	editorCtx.value = res.context
		// }).exec()

	}
	const getContents = () => {
		editorCtx.value.getContents({
			success: (res) => {
				html.value = res.html
				uni.setStorageSync("html", html.value)
			}
		})
	}
	const undo = () => {
		editorCtx.value.undo()
	}
	const redo = () => {
		editorCtx.value.redo()
	}
	const format = (e) => {
		let {
			name,
			value
		} = e.target.dataset
		if (!name) return
		// console.log('format', name, value)
		editorCtx.value.format(name, value)
	}
	const onStatusChange = (e) => {
		formats.value = e.detail
	}
	const insertDivider = () => {
		editorCtx.value.insertDivider({
			success: function() {
				console.log('insert divider success')
			}
		})
	}
	const clear = () => {
		uni.showModal({
			title: '清空编辑器',
			content: '确定清空编辑器全部内容？',
			success: res => {
				if (res.confirm) {
					editorCtx.value.clear({
						success: function(res) {
							console.log("clear success")
						}
					})
				}
			}
		})
	}
	const removeFormat = () => {
		editorCtx.value.removeFormat()
	}
	const insertDate = () => {
		const date = new Date()
		const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
		editorCtx.value.insertText({
			text: formatDate
		})
	}
	const insertImage = () => {
		uni.chooseImage({
			count: 1,
			success: (res) => {
				editorCtx.value.insertImage({
					src: res.tempFilePaths[0],
					alt: '图像',
					success: function() {
						console.log('insert image success')
					}
				})
			}
		})
	}
</script>

<style lang="scss" scoped>
	@import "./editor-icon.css";

	.page-body {
		height: calc(100vh - var(--window-top) - var(--status-bar-height));
	}

	.wrapper {
		height: 100%;
	}

	.editor-wrapper {
		height: calc(100vh - var(--window-top) - var(--status-bar-height) - 140px);
		background: #fff;
	}

	.iconfont {
		display: inline-block;
		padding: 8px 8px;
		width: 24px;
		height: 24px;
		cursor: pointer;
		font-size: 20px;
	}

	.toolbar {
		box-sizing: border-box;
		border-bottom: 0;
		font-family: 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
		display: flex;
		justify-content: space-around;
		flex-wrap: wrap;
	}

	.ql-container {
		box-sizing: border-box;
		padding: 12px 15px;
		width: 100%;
		min-height: 30vh;
		height: 100%;
		margin-top: 20px;
		font-size: 16px;
		line-height: 1.5;
	}

	.ql-active {
		color: #06c;
	}
</style>