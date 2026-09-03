<script setup>
	import { onMounted, ref } from 'vue'
	import { onShow, onHide } from '@dcloudio/uni-app'

	const darkMode = ref(false)

	onMounted(() => {
		console.log('App Launch')
		const savedDarkMode = uni.getStorageSync('darkMode')
		if (savedDarkMode !== '') {
			darkMode.value = savedDarkMode === 'true'
		}
		updateTheme(darkMode.value)
	})

	onShow(() => {
		console.log('App Show')
	})

	onHide(() => {
		console.log('App Hide')
	})

	uni.$on('darkModeChange', (isDark) => {
		darkMode.value = isDark
		updateTheme(isDark)
	})

	const updateTheme = (isDark) => {
		const page = getCurrentPages()[getCurrentPages().length - 1]
		if (page && page.$el) {
			if (isDark) {
				page.$el.classList.add('dark')
			} else {
				page.$el.classList.remove('dark')
			}
		}
		
		uni.setNavigationBarColor({
			frontColor: isDark ? '#ffffff' : '#000000',
			backgroundColor: isDark ? '#1a1a1a' : '#f8f8f8'
		})
	}
</script>

<style>
	page {
		--bg-color: #f5f5f5;
		--text-color: #333;
		--text-color-secondary: #666;
		--text-color-muted: #999;
		--card-bg: #fff;
		--border-color: #f0f0f0;
		--primary-color: #ff4444;
	}

	page.dark,
	.dark page {
		--bg-color: #1a1a1a;
		--text-color: #fff;
		--text-color-secondary: #aaa;
		--text-color-muted: #666;
		--card-bg: #2d2d2d;
		--border-color: #3d3d3d;
		--primary-color: #ff4444;
	}

	.container {
		background-color: var(--bg-color);
		min-height: 100vh;
	}

	.card {
		background-color: var(--card-bg);
	}

	.text-primary {
		color: var(--text-color);
	}

	.text-secondary {
		color: var(--text-color-secondary);
	}

	.text-muted {
		color: var(--text-color-muted);
	}
</style>