<template>
	<view class="seat-page">
		<!-- 区域标题 -->
		<view class="page-head">
			<view class="page-title">{{ areaName || '选择座位' }}</view>
			<view class="page-sub">先选时段，再挑座位 · 实时余位</view>
		</view>

		<!-- 时段切换 -->
		<view class="slot-bar">
			<view v-for="(s, i) in TIME_SLOTS" :key="i"
				class="slot"
				:class="{ 'slot-on': i === slotIndex, 'slot-off': isPast(s) }"
				@tap="onSlotTap(i)">
				<text class="slot-label">{{ s.label }}</text>
				<text v-if="isPast(s)" class="slot-tag">已开始</text>
				<text v-else-if="i === slotIndex" class="slot-tag slot-tag-on">当前</text>
			</view>
		</view>

		<!-- 阅览区平面图 -->
		<view class="plan">
			<view v-if="loading" class="plan-tip">加载中...</view>
			<view v-else-if="seats.length === 0" class="plan-tip">该区域暂无座位</view>
			<block v-else>
				<!-- 列号 -->
				<view class="plan-row">
					<view class="row-num"></view>
					<view v-for="c in colCount" :key="'col' + c" class="col-num">{{ c }}</view>
				</view>
				<!-- 座位行 -->
				<view v-for="row in rows" :key="row.label" class="plan-row">
					<view class="row-num">{{ row.label }}</view>
					<view v-for="cell in row.cells" :key="cell.seat.seatId" class="cell">
						<view class="seat" :class="seatClass(cell.seat)" @tap="onSeatTap(cell.seat)">
							<text class="seat-no">{{ shortNo(cell.seat.seatNo) }}</text>
							<uni-icons v-if="cell.seat.hasSocket === 1 && !isSelected(cell.seat) && !isBroken(cell.seat)"
								class="seat-power" type="power" size="11" color="#547044"></uni-icons>
							<uni-icons v-if="isBroken(cell.seat)" class="seat-mark" type="close" size="16" color="#A8503A"></uni-icons>
							<uni-icons v-else-if="isSelected(cell.seat)" class="seat-mark" type="checkmarkempty" size="16" color="#9DBE7E"></uni-icons>
						</view>
						<view v-if="cell.aisleAfter" class="aisle"></view>
					</view>
				</view>
				<!-- 入口 -->
				<view class="entrance">↓ 入口</view>
			</block>
		</view>

		<!-- 图例 -->
		<view class="legend">
			<view class="lg-item"><view class="lg-box lg-free"></view><text>可选</text></view>
			<view class="lg-item"><view class="lg-box lg-busy"></view><text>已占</text></view>
			<view class="lg-item"><view class="lg-box lg-free"><uni-icons type="power" size="9" color="#547044"></uni-icons></view><text>有插座</text></view>
			<view class="lg-item"><view class="lg-box lg-sel"></view><text>已选</text></view>
			<view class="lg-item"><view class="lg-box lg-bad"></view><text>维护</text></view>
		</view>

		<!-- 底部操作 -->
		<view class="footer">
			<view class="selected-info">已选座位 <text class="selected-no">{{ selectedSeatNo }}</text></view>
			<view class="confirm-btn" :class="{ 'confirm-off': !selectedSeatId }" @tap="onConfirm">确认预约</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { request } from '@/utils/request'
import { ref, computed } from 'vue'
import { TIME_SLOTS, todayStr, isPast, firstAvailableSlot } from '@/common/slots'

interface Seat {
	seatId: number
	areaId: number
	seatNo: string
	hasSocket: number
	status: number      // 物理状态：0 正常 / 1 长期停用 / 2 损坏
	occupied?: boolean  // 该时段是否已被预约（后端按 t_booking 算好）
}

const seats = ref<Seat[]>([])
const selectedSeatId = ref<number | null>(null)
const areaId = ref<number>(0)
const areaName = ref<string>('')
const slotIndex = ref(0)
const loading = ref(false)

const selectedSeatNo = computed(() => {
	const s = seats.value.find(x => x.seatId === selectedSeatId.value)
	return s ? shortNo(s.seatNo) : '—'
})

// 座位号解析：'A-01' -> 行 A，序号 1，短号 A1
function numOf(no: string): number {
	const m = /(\d+)/.exec(no)
	return m ? Number(m[1]) : 0
}
function prefixOf(no: string): string {
	const m = /^([A-Za-z]+)/.exec(no.trim())
	return m ? m[1].toUpperCase() : 'A'
}
function shortNo(no: string): string {
	const m = /^([A-Za-z]+)-?(\d+)$/.exec(no.trim())
	return m ? m[1].toUpperCase() + Number(m[2]) : no
}

const colCount = computed(() => {
	let max = 0
	for (const s of seats.value) max = Math.max(max, numOf(s.seatNo))
	return max
})

// 按座位号前缀分行，行内按序号排序；每满 3 列在第 2 列后插入过道
const rows = computed(() => {
	const map = new Map<string, Seat[]>()
	for (const s of seats.value) {
		const key = prefixOf(s.seatNo)
		if (!map.has(key)) map.set(key, [])
		map.get(key)!.push(s)
	}
	const aisleIdx = colCount.value >= 3 ? 1 : -1
	return [...map.entries()]
		.sort((a, b) => a[0].localeCompare(b[0]))
		.map(([label, list]) => ({
			label,
			cells: list
				.slice()
				.sort((a, b) => numOf(a.seatNo) - numOf(b.seatNo))
				.map((seat, i) => ({ seat, aisleAfter: i === aisleIdx })),
		}))
})

onLoad((options) => {
	areaId.value = Number(options?.areaId || 0)
	areaName.value = options?.areaName ? decodeURIComponent(options.areaName) : ''
	slotIndex.value = firstAvailableSlot()
	loadSeats()
})

async function loadSeats() {
	const s = TIME_SLOTS[slotIndex.value]
	const url = '/api/seat/list?areaId=' + areaId.value
		+ '&bookDate=' + todayStr()
		+ '&startTime=' + encodeURIComponent(s.startTime)
		+ '&endTime=' + encodeURIComponent(s.endTime)
	loading.value = true
	try {
		seats.value = await request<Seat[]>(url)
	} catch (e) {
		console.error('Error fetching seats:', e)
		uni.showToast({ title: '座位加载失败，请重试', icon: 'none' })
	} finally {
		loading.value = false
	}
}

function onSlotTap(i: number) {
	if (isPast(TIME_SLOTS[i])) {
		uni.showToast({ title: '该时段已开始，无法预约', icon: 'none' })
		return
	}
	if (i === slotIndex.value) return
	slotIndex.value = i
	selectedSeatId.value = null   // 换时段必须清空已选，否则会约到已占座位
	loadSeats()
}

function isBroken(seat: Seat): boolean {
	return seat.status === 2
}
function isSelected(seat: Seat): boolean {
	return selectedSeatId.value === seat.seatId
}
function isBusy(seat: Seat): boolean {
	return !!seat.occupied || seat.status === 1
}

function seatClass(seat: Seat) {
	return {
		'seat-available': !isBusy(seat) && !isBroken(seat),
		'seat-occupied': isBusy(seat) && !isBroken(seat),
		'seat-disabled': isBroken(seat),
		'seat-selected': isSelected(seat),
	}
}

function onSeatTap(seat: Seat) {
	if (isBroken(seat)) {
		uni.showToast({ title: '该座位维护中，请选择其他座位', icon: 'none' })
		return
	}
	if (isBusy(seat)) {
		uni.showToast({ title: '该时段已被占用，请选择其他座位', icon: 'none' })
		return
	}
	selectedSeatId.value = seat.seatId
}

async function onConfirm() {
	if (!selectedSeatId.value) {
		uni.showToast({ title: '请先选择一个座位', icon: 'none' })
		return
	}
	const slot = TIME_SLOTS[slotIndex.value]
	try {
		const r = await request('/api/booking/create', 'POST', {
			seatId: selectedSeatId.value,
			areaId: areaId.value,
			bookDate: todayStr(),
			startTime: slot.startTime,
			endTime: slot.endTime,
		})
		if (r === '预约成功') {
			uni.showToast({ title: '预约成功', icon: 'success' })
			setTimeout(() => uni.navigateBack(), 1200)
		} else {
			uni.showToast({ title: String(r), icon: 'none' })
			loadSeats()
		}
	} catch (e) {
		console.error(e)
		uni.showToast({ title: '预约异常！请重试', icon: 'none' })
	}
}
</script>

<style>
.seat-page {
	padding: 24rpx 24rpx 220rpx;
	min-height: 100vh;
	background: var(--color-bg, #E8E0CF);
	box-sizing: border-box;
}
.page-head { margin-bottom: 20rpx; }
.page-title {
	font-size: var(--font-title, 40rpx);
	font-weight: bold;
	color: var(--color-ink, #29231D);
}
.page-sub {
	margin-top: 6rpx;
	font-size: var(--font-caption, 24rpx);
	color: var(--color-ink-soft, #6E6659);
}

/* 时段切换 */
.slot-bar {
	display: flex;
	gap: 12rpx;
	margin-bottom: 24rpx;
	flex-wrap: wrap;
}
.slot {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 12rpx 20rpx;
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-md, 10rpx);
	background: var(--color-surface, #F4EDDF);
	color: var(--color-ink, #29231D);
}
.slot-label { font-size: 24rpx; font-weight: 700; }
.slot-tag { margin-top: 4rpx; font-size: 18rpx; color: var(--color-ink-faint, #8A806F); }
.slot-tag-on { color: var(--color-accent, #547044); font-weight: 700; }
.slot-on { background: var(--color-ink, #29231D); color: var(--color-surface-raised, #FFFAF0); }
.slot-on .slot-tag-on { color: #9DBE7E; }
.slot-off { background: var(--color-neutral-weak, #DDD2BB); color: var(--color-ink-faint, #8A806F); }

/* 平面图 */
.plan {
	padding: 20rpx 20rpx 12rpx;
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-lg, 16rpx);
	background: var(--color-surface, #F4EDDF);
	box-shadow: var(--shadow-card, 3rpx 3rpx 0 #29231D);
}
.plan-tip {
	padding: 60rpx 0;
	text-align: center;
	font-size: 26rpx;
	color: var(--color-ink-faint, #8A806F);
}
.plan-row {
	display: flex;
	align-items: center;
	gap: 14rpx;
	margin-bottom: 14rpx;
}
.row-num {
	width: 32rpx;
	text-align: center;
	font-size: 22rpx;
	font-weight: 700;
	color: var(--color-ink-soft, #6E6659);
}
.col-num {
	width: 92rpx;
	height: 32rpx;
	line-height: 32rpx;
	text-align: center;
	font-size: 20rpx;
	color: var(--color-ink-soft, #6E6659);
}
.cell { display: flex; align-items: center; gap: 14rpx; }

.seat {
	position: relative;
	width: 92rpx;
	height: 92rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-md, 10rpx);
	background: var(--color-surface, #F4EDDF);
	box-sizing: border-box;
}
.seat-no { font-size: 24rpx; font-weight: 700; color: var(--color-ink, #29231D); }
.seat-power { position: absolute; top: 4rpx; right: 4rpx; }
.seat-mark { position: absolute; }

/* 状态：定义顺序即优先级，selected 放最后 */
.seat-available { background: var(--color-surface, #F4EDDF); }
.seat-occupied {
	background: var(--color-neutral-weak, #DDD2BB);
	border-color: var(--color-border, #8A806F);
}
.seat-occupied .seat-no { color: var(--color-ink-faint, #8A806F); font-weight: 400; }
.seat-disabled {
	background: var(--color-danger-soft, #ECD9CF);
	border-color: var(--color-danger, #A8503A);
}
.seat-selected {
	background: var(--color-ink, #29231D);
	border-color: var(--color-ink, #29231D);
}
.seat-selected .seat-no { color: var(--color-surface-raised, #FFFAF0); }

/* 过道 */
.aisle {
	width: 30rpx;
	height: 92rpx;
	border-left: 2rpx dashed var(--color-border, #8A806F);
}
.entrance {
	margin-top: 4rpx;
	text-align: center;
	font-size: 22rpx;
	color: var(--color-ink-soft, #6E6659);
}

/* 图例 */
.legend {
	display: flex;
	flex-wrap: wrap;
	gap: 18rpx;
	margin-top: 20rpx;
	padding: 0 4rpx;
}
.lg-item { display: flex; align-items: center; gap: 6rpx; }
.lg-item text { font-size: 22rpx; color: var(--color-ink-soft, #6E6659); }
.lg-box {
	width: 22rpx;
	height: 22rpx;
	border-radius: var(--radius-xs, 4rpx);
	border: 2rpx solid var(--color-ink, #29231D);
	display: flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
}
.lg-free { background: var(--color-surface, #F4EDDF); }
.lg-busy { background: var(--color-neutral-weak, #DDD2BB); border-color: var(--color-border, #8A806F); }
.lg-sel { background: var(--color-ink, #29231D); }
.lg-bad { background: var(--color-danger-soft, #ECD9CF); border-color: var(--color-danger, #A8503A); }

/* 底部操作 */
.footer {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 24rpx;
	padding: 20rpx 24rpx 32rpx;
	background: var(--color-bg, #E8E0CF);
	border-top: 2rpx solid var(--color-ink, #29231D);
}
.selected-info { font-size: 26rpx; color: var(--color-ink, #29231D); }
.selected-no { margin-left: 8rpx; font-weight: 700; }
.confirm-btn {
	background: var(--color-accent, #547044);
	color: var(--color-surface-raised, #FFFAF0);
	border: 2rpx solid var(--color-ink, #29231D);
	border-radius: var(--radius-md, 10rpx);
	padding: 18rpx 36rpx;
	font-size: 28rpx;
	font-weight: bold;
	box-shadow: var(--shadow-hard, 4rpx 4rpx 0 #29231D);
}
.confirm-off { opacity: .5; }
</style>
