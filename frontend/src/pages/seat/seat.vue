<template>
	<view class="seat-page">
		<view class="page-title">选择座位</view>
		<view class="seat-grid">
			<view v-for="seat in seats" 
			:key="seat.seatId" 
			class="seat" 
			:class="seatClass(seat)" 
			@tap="onSeatTap(seat)">
				<text class="seat-no">{{ seat.seatNo }}</text>
				<uni-icons v-if="seat.hasSocket === 1" type="power" size="12" color="#547044"></uni-icons>
			</view>
		</view>

		<view class="footer">
			<text class="selected-info">已选座位 <text class="selected-no">{{ selectedSeatNo }}</text></text>
			<view class="confirm-btn" @tap="onConfirm">下一步：选时段</view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import {request} from '@/utils/request'
import { ref, computed } from 'vue'
// 座位接口
interface Seat {
	seatId: number;
	areaId: number;
	seatNo: string;
	hasSocket: number;
	status: number; // 0: 可选, 1: 已占用, 2: 不可选
}
// 时间槽接口(工具类)
const TIME_SLOTS =[
	{label:'08:00-12:00',startTime:'08:00',endTime:'12:00'},
	{label:'12:00-15:00',startTime:'12:00',endTime:'15:00'},
	{label:'15:00-19:00',startTime:'15:00',endTime:'19:00'},
	{label:'19:00-22:00',startTime:'19:00',endTime:'22:00'},
]
// 日期格式化工具类
function todayStr(){
	const d= new Date()
	const m=String(d.getMonth()+1).padStart(2,'0')
	const day=String(d.getDate()).padStart(2,'0')
	return `${d.getFullYear()}-${m}-${day}`
}

// 定义响应式变量
const seats = ref<Seat[]>([])
const selectedSeatId = ref<number | null>(null)
const areaId = ref<number>(0)

// 计算属性，获取已选座位号
const selectedSeatNo = computed(() => {
	const s = seats.value.find(x => x.seatId === selectedSeatId.value)
	return s ? s.seatNo : '—'
})

// 页面加载时获取座位列表
onLoad((options) => {
  areaId.value = Number(options?.areaId || 0)
	loadSeats()
})

// 从后端获取座位列表
async function loadSeats() {
	try {
		const data = await request<Seat[]>(`/api/seat/list?areaId=${areaId.value}`)
		seats.value = data
	} catch (error) {
		console.error('Error fetching seats:', error)
	}
}
// 座位状态类名
function seatClass(seat: Seat) {
	return {
		'seat-available': seat.status === 0,
		'seat-occupied': seat.status === 1,
		'seat-disabled': seat.status === 2,
		'seat-selected': selectedSeatId.value === seat.seatId,
	}
}
// 座位点击事件
function onSeatTap(seat: Seat) {
	if (seat.status === 0) {
		selectedSeatId.value = seat.seatId
	} else if(seat.status ===1){
		uni.showToast({
			title: '该座位已有人入座,请选择其他座位',
			icon: 'error'
		})
	}else(
		uni.showToast({
			title:'该位置损坏或不可使用，请选择其他座位',
			icon:'error'
		})
	)
}
// 确认预约事件
async function onConfirm(){
	if (!selectedSeatId.value) {
		uni.showToast({ title: '请先选择一个座位', icon: 'error' })
		return
	}
	uni.showActionSheet({
		itemList:TIME_SLOTS.map(x=>x.label),
		success:async (res)=>{
			const slot=TIME_SLOTS[res.tapIndex]
	
		try {
			const r=await request(
				'/api/booking/create',
				'POST',
				{
					seatId:selectedSeatId.value,
					areaId:areaId.value,
					bookDate:todayStr(),
					startTime:slot.startTime,
					endTime:slot.endTime,
				}
			)
			if(r === '预约成功'){
				uni.showToast({
					title:'预约成功',
					icon:'success'
				})
				setTimeout(()=>{
			       uni.navigateBack()
				},1200)
			}else{
				uni.showToast({
					title:String(r),
					icon:'error'
				})
			}
		} catch (e) {
			console.error(e)
			uni.showToast({
				title:'预约异常！请重试',
				icon:'error'
			})
		}
	}
})
	
}
	
</script>

<style>
.seat-page { 
padding: 24rpx; 
background: var(--color-bg, #E8E0CF);
 min-height: 100vh; }
.page-title {
 font-size: 36rpx; 
 font-weight: bold; 
 color: var(--color-ink, #29231D); 
 margin-bottom: 24rpx; }
.seat-grid { 
display: flex; 
flex-wrap: wrap; 
gap: 16rpx; }
.seat {
	width: 120rpx; 
	height: 120rpx;
	display: flex; 
	flex-direction: column; 
	align-items: center; 
	justify-content: center;
	border: 2rpx solid var(--color-ink, #29231D); 
	border-radius: 8rpx;
	background: var(--color-surface, #F4EDDF);
}
/* 可选：中性米色（森绿留给插座图标与 CTA，避免一屏绿块刺眼） */
.seat-available { background: var(--color-surface, #F4EDDF); color: var(--color-ink, #29231D); }
.seat-occupied { background: #DDD2BB; color: var(--color-ink, #29231D); }
.seat-disabled { background: #ECD9CF; color: #999; }
.seat-selected { background: var(--color-ink, #29231D); color: #fff; }
.seat-no { font-size: 28rpx; }
.footer { margin-top: 40rpx; display: flex; align-items: center; justify-content: space-between; }
.selected-info { font-size: 26rpx; color: var(--color-ink, #29231D); }
.selected-no { font-weight: bold; }
.confirm-btn {
	background: var(--color-accent, #547044); color: #fff;
	border: 2rpx solid var(--color-ink, #29231D); border-radius: 8rpx;
	padding: 16rpx 32rpx; font-size: 28rpx; font-weight: bold;
	box-shadow: 4rpx 4rpx 0 var(--color-ink, #29231D);
}
</style>
