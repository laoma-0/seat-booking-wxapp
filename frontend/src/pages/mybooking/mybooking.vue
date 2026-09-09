<template>
	<view class="mybooking-page">
		<view class="head">
			<view class="head-title">我的预约</view>
			<view class="head-sub">按场次时间排序 · 未开始的在前</view>
		</view>

		<!-- 统计 -->
		<view v-if="list.length > 0" class="stat">
			<view class="stat-item">
				<text class="stat-num">{{ countOf(0) }}</text>
				<text class="stat-label">待签到</text>
			</view>
			<view class="stat-item">
				<text class="stat-num">{{ countOf(1) }}</text>
				<text class="stat-label">已签到</text>
			</view>
			<view class="stat-item">
				<text class="stat-num">{{ countOf(2) }}</text>
				<text class="stat-label">已完成</text>
			</view>
			<view class="stat-item">
				<text class="stat-num">{{ countOf(3) }}</text>
				<text class="stat-label">爽约</text>
			</view>
		</view>

		<view v-if="loading" class="tip">加载中...</view>
		<view v-else-if="list.length === 0" class="empty">
			<uni-icons type="list" size="28" color="#8A806F"></uni-icons>
			<text class="empty-text">你还没有预约记录</text>
		</view>

		<view v-else class="list">
			<view v-for="b in list" :key="b.bookingId" class="card">
				<view class="card-top">
					<view class="card-title">{{ b.areaName }} · {{ b.seatNo }}</view>
					<view class="chip" :class="statusClass(b.status)">{{ statusText(b.status) }}</view>
				</view>
				<view class="meta">
					<text>{{ b.bookDate }}</text>
					<text>{{ slotName(b.startTime) }} {{ fmtTime(b.startTime) }}-{{ fmtTime(b.endTime) }}</text>
				</view>
				<view v-if="b.status === 0" class="btn-group">
					<view class="btn" @tap="sign(b.bookingId)">签到</view>
					<view class="btn-ghost" @tap="cancel(b.bookingId)">取消预约</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { request } from '@/utils/request'

interface BookingItem {
	bookingId: number
	seatId: number
	areaId: number
	status: number      // 0 待签到 / 1 已签到 / 2 已完成 / 3 已爽约 / 4 已取消
	bookDate: string
	startTime: string
	endTime: string
	createTime: string
	seatNo: string
	areaName: string
}

const list = ref<BookingItem[]>([])
const loading = ref(false)

// 08:00:00 -> 08:00
function fmtTime(t: string): string {
	return (t || '').slice(0, 5)
}
// 开始时间 -> 时段名
function slotName(t: string): string {
	const h = Number((t || '').slice(0, 2))
	if (h < 12) return '上午'
	if (h < 15) return '中午'
	if (h < 19) return '下午'
	return '晚上'
}
function countOf(s: number): number {
	return list.value.filter(x => x.status === s).length
}

function statusText(s: number): string {
	return ['待签到', '已签到', '已完成', '已爽约', '已取消'][s] ?? '未知状态'
}
function statusClass(s: number): string {
	return ['st-wait', 'st-done', 'st-finish', 'st-broken', 'st-cancel'][s] ?? 'st-wait'
}

async function load() {
	loading.value = true
	try {
		const r = await request<BookingItem[]>('/api/booking/my')
		if (r instanceof Array) {
			list.value = r
		} else {
			uni.showToast({ title: String(r), icon: 'none' })
		}
	} catch (e) {
		console.error(e)
		uni.showToast({ title: '加载异常！请重试', icon: 'none' })
	} finally {
		loading.value = false
	}
}

async function sign(bid: number) {
	try {
		const r = await request('/api/booking/sign', 'POST', { bookingId: bid })
		if (r === '签到成功') {
			uni.showToast({ title: '签到成功', icon: 'success' })
			load()
		} else {
			uni.showToast({ title: String(r), icon: 'none' })
		}
	} catch (e) {
		console.error(e)
		uni.showToast({ title: '签到异常！请重试', icon: 'none' })
	}
}

async function cancel(bid: number) {
	uni.showModal({
		title: '确认取消',
		content: '取消后该时段会释放给其他同学，确定吗？',
		success: async (res: any) => {
			if (!res.confirm) return
			try {
				const r = await request('/api/booking/cancel', 'POST', { bookingId: bid })
				if (r === '取消成功') {
					uni.showToast({ title: '取消成功', icon: 'success' })
					load()
				} else {
					uni.showToast({ title: String(r), icon: 'none' })
				}
			} catch (e) {
				console.error(e)
				uni.showToast({ title: '取消异常！请重试', icon: 'none' })
			}
		},
	})
}

onShow(() => { load() })
</script>

<style>
.mybooking-page {
	min-height: 100vh;
	background: var(--color-bg, #E8E0CF);
	padding: 24rpx 24rpx 40rpx;
	box-sizing: border-box;
}
.head { margin-bottom: 20rpx; }
.head-title {
	font-size: var(--font-title, 40rpx);
	font-weight: 700;
	color: var(--color-ink, #29231D);
}
.head-sub {
	margin-top: 6rpx;
	font-size: var(--font-caption, 24rpx);
	color: var(--color-ink-soft, #6E6659);
}

/* 统计条 */
.stat {
	display: flex;
	gap: 12rpx;
	margin-bottom: 24rpx;
}
.stat-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 16rpx 0;
	background: var(--color-surface, #F4EDDF);
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-md, 10rpx);
}
.stat-num { font-size: 32rpx; font-weight: 700; color: var(--color-ink, #29231D); }
.stat-label { margin-top: 4rpx; font-size: 20rpx; color: var(--color-ink-soft, #6E6659); }

.tip, .empty {
	padding: 100rpx 0;
	text-align: center;
	font-size: 26rpx;
	color: var(--color-ink-faint, #8A806F);
}
.empty { display: flex; flex-direction: column; align-items: center; gap: 12rpx; }

.list { display: flex; flex-direction: column; gap: 20rpx; }
.card {
	background: var(--color-surface, #F4EDDF);
	border: 2rpx solid var(--color-ink, #29231D);
	box-shadow: var(--shadow-card, 3rpx 3rpx 0 #29231D);
	border-radius: var(--radius-lg, 16rpx);
	padding: 24rpx;
}
.card-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 16rpx;
}
.card-title {
	flex: 1;
	min-width: 0;
	font-size: 30rpx;
	font-weight: 700;
	color: var(--color-ink, #29231D);
}
.chip {
	flex-shrink: 0;
	font-size: 22rpx;
	font-weight: 700;
	padding: 6rpx 20rpx;
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-pill, 999rpx);
}
.st-wait { background: var(--color-warning-soft, #F0E6C4); color: var(--color-warning, #9A7320); }
.st-done { background: var(--color-accent, #547044); color: var(--color-surface-raised, #FFFAF0); }
.st-finish { background: var(--color-neutral-weak, #DDD2BB); color: var(--color-ink, #29231D); }
.st-broken { background: var(--color-danger-soft, #ECD9CF); color: var(--color-danger, #A8503A); }
.st-cancel { background: #E3DED2; color: var(--color-ink-soft, #6E6659); }

.meta {
	display: flex;
	justify-content: space-between;
	margin-top: 10rpx;
	font-size: 24rpx;
	color: var(--color-ink-soft, #6E6659);
}

.btn-group { display: flex; gap: 16rpx; margin-top: 20rpx; }
.btn {
	flex: 1;
	background: var(--color-accent, #547044);
	color: var(--color-surface-raised, #FFFAF0);
	border: 2rpx solid var(--color-ink, #29231D);
	box-shadow: var(--shadow-card, 3rpx 3rpx 0 #29231D);
	border-radius: var(--radius-sm, 6rpx);
	padding: 14rpx 0;
	text-align: center;
	font-size: 28rpx;
	font-weight: 700;
}
.btn-ghost {
	flex: 1;
	background: var(--color-surface, #F4EDDF);
	color: var(--color-ink, #29231D);
	border: 2rpx solid var(--color-ink, #29231D);
	box-shadow: var(--shadow-card, 3rpx 3rpx 0 #29231D);
	border-radius: var(--radius-sm, 6rpx);
	padding: 14rpx 0;
	text-align: center;
	font-size: 28rpx;
	font-weight: 700;
}
</style>
