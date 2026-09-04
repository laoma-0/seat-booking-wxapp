<template>
	<view class="mybooking-page">
		<view class="mybooking-header">
			<view class="mybooking-header-title">我的预约</view>
		</view>
		<view v-if="loading" class="tip">加载中...</view>
		<view v-else-if="list.length === 0" class="tip">你还没预约记录</view>
		 <view v-else class="list">
			<view v-for="b in list" :key="b.bookingId" class="card">
				<view class="card-top">
					<text class="area">{{b.areaName}}</text>
					<text class="chip" :class="statusClass(b.status)">{{statusText(b.status)}}</text>
				</view>
				<view class="seat-no">{{ b.seatNo }}</view>      <!-- ← 补这行 -->
                    <view class="meta">                              <!-- ← 补这块 -->
                      <text>{{ b.bookDate }}</text>
                      <text>{{ b.startTime }} - {{ b.endTime }}</text>
                   </view>
            </view>
		</view>
	</view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app';
import { ref } from 'vue';
import { request } from '@/utils/request';

interface BookingItem {
	 bookingId: number; 
	 seatId: number; 
	 areaId: number; 
	 status: number
     bookDate: string; 
	 startTime: string; 
	 endTime: string
     createTime: string; 
	 seatNo: string; 
	 areaName: string
}

const list = ref<BookingItem[]>([])
const loading = ref(false)


// 加载我的预约记录
async function load(){
	loading.value=true
	try {
		const r=await request<BookingItem[]>('/api/booking/my')
		if(r instanceof Array){
			list.value=r
		}else{
			uni.showToast({
				title:String(r),
				icon:'error'
			})
		}
	} catch (e) {
		console.error(e)
		uni.showToast({
			title:'加载异常！请重试',
			icon:'error'
		})
	} finally {
		loading.value=false
	}
}
// 状态文本
function statusText(s: number): string {
  return ['待签到', '已签到', '已完成', '已爽约'][s] ?? '未知状态'
}
// 状态类名
function statusClass(s: number): string {
  return ['st-wait', 'st-done', 'st-finish', 'st-broken'][s] ?? 'st-wait'
}

// 页面显示时加载预约记录
onShow(()=>{load()})
	
</script>


<style scoped>
.mybooking-page { min-height: 100vh; 
  background: #E8E0CF; padding: 
  24rpx; box-sizing: border-box; 
}
.mybooking-header { 
  padding: 16rpx 8rpx 24rpx; 
}
.mybooking-header-title { 
  font-size: 40rpx; 
  font-weight: 700; 
  color: #29231D; 
}
.tip { 
  text-align: center; 
  color: #29231D; 
  opacity: .6; 
  margin-top: 120rpx; 
  font-size: 28rpx; }
.list { 
  display: flex; 
  flex-direction: 
  column; gap: 20rpx; }
.card { 
  background: #F4EDDF; 
  border: 2rpx solid #29231D; 
  box-shadow: 3rpx 3rpx 0 #29231D; 
  border-radius: 8rpx; padding: 24rpx; }
.card-top { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; }
.area { 
  font-size: 30rpx; 
  font-weight: 700; 
  color: #29231D; }
.chip { 
	font-size: 22rpx; 
	padding: 4rpx 14rpx; 
	border: 2rpx solid #29231D; 
	border-radius: 20rpx; }
.seat-no { 
	font-size: 34rpx; 
	font-weight: 700; 
	color: #547044; 
	margin: 12rpx 0; }
.meta { 
	display: flex; 
	justify-content: 
	space-between; 
	font-size: 24rpx; 
	color: #29231D; 
	opacity: .75; }
.st-wait   { 
	background: #F0E6C4; }
.st-done   { 
	background: #547044; 
	color: #F4EDDF; }
.st-finish { 
	background: #DDD2BB; }
.st-broken { 
	background: #ECD9CF; }
</style>

