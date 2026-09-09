<template>
  <view class="container">
    <view class="head">
      <view class="title">座位预约</view>
      <view class="sub">校园自习室 · 实时余位</view>
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

    <view v-if="loading" class="tip">加载中...</view>
    <view v-else-if="areas.length === 0" class="empty">
      <uni-icons type="info" size="28" color="#8A806F"></uni-icons>
      <text class="empty-text">暂无阅览区</text>
    </view>

    <view v-for="item in areas" :key="item.areaId" class="card" @tap="goSeat(item)">
      <view class="card-icon" :class="iconClass(item)">
        <uni-icons type="home" size="22" :color="iconColor(item)"></uni-icons>
      </view>
      <view class="card-body">
        <view class="card-name">
          {{ item.areaName }}
          <text v-if="item.floor" class="card-floor">{{ item.floor }}</text>
        </view>
        <view class="card-meta">座位 {{ item.totalSeats }} · 剩余 {{ item.availableSeats }}</view>
        <view class="bar">
          <view class="bar-in" :class="barClass(item)" :style="{ width: barWidth(item) }"></view>
        </view>
      </view>
      <view class="chip" :class="chipClass(item)">{{ chipText(item) }}</view>
      <uni-icons class="card-arrow" type="arrowright" size="16" color="#8A806F"></uni-icons>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { request } from '@/utils/request'
import { TIME_SLOTS, todayStr, isPast, firstAvailableSlot } from '@/common/slots'

interface Area {
  areaId: number
  areaName: string
  floor: string
  totalSeats: number
  availableSeats: number
}

const areas = ref<Area[]>([])
const slotIndex = ref(firstAvailableSlot())
const loading = ref(false)

function ratio(item: Area): number {
  if (!item.totalSeats) return 0
  return item.availableSeats / item.totalSeats
}

// 余位状态：满 / 紧张（<=20%）/ 可约
function levelOf(item: Area): 'full' | 'tight' | 'free' {
  if (item.availableSeats <= 0) return 'full'
  if (ratio(item) <= 0.2) return 'tight'
  return 'free'
}
function chipText(item: Area): string {
  return { full: '满', tight: '紧张', free: '可约' }[levelOf(item)]
}
function chipClass(item: Area): string {
  return { full: 'chip-full', tight: 'chip-tight', free: 'chip-free' }[levelOf(item)]
}
function barClass(item: Area): string {
  return { full: 'bar-full', tight: 'bar-tight', free: 'bar-free' }[levelOf(item)]
}
function barWidth(item: Area): string {
  return Math.round(ratio(item) * 100) + '%'
}
function iconColor(item: Area): string {
  return { full: '#A8503A', tight: '#9A7320', free: '#29231D' }[levelOf(item)]
}
function iconClass(item: Area): string {
  return { full: 'icon-full', tight: 'icon-tight', free: 'icon-free' }[levelOf(item)]
}

function goSeat(item: Area) {
  uni.navigateTo({
    url: '/pages/seat/seat?areaId=' + item.areaId
      + '&areaName=' + encodeURIComponent(item.areaName || '')
  })
}

function onSlotTap(i: number) {
  if (isPast(TIME_SLOTS[i])) {
    uni.showToast({ title: '该时段已开始，无法预约', icon: 'none' })
    return
  }
  if (i === slotIndex.value) return
  slotIndex.value = i
  loadAreas()
}

async function loadAreas() {
  const s = TIME_SLOTS[slotIndex.value]
  const url = '/api/area/list?bookDate=' + todayStr()
    + '&startTime=' + encodeURIComponent(s.startTime)
    + '&endTime=' + encodeURIComponent(s.endTime)
  loading.value = true
  try {
    areas.value = await request<Area[]>(url)
  } catch (e) {
    console.error('Error fetching areas:', e)
    uni.showToast({ title: '加载失败，请重试', icon: 'none' })
  } finally {
    loading.value = false
  }
}

onShow(() => { loadAreas() })
</script>

<style>
.container {
  padding: 24rpx 24rpx 40rpx;
  min-height: 100vh;
  background: var(--color-bg, #E8E0CF);
  box-sizing: border-box;
}
.head { margin-bottom: 20rpx; }
.title {
  font-size: var(--font-title, 40rpx);
  font-weight: bold;
  color: var(--color-ink, #29231D);
}
.sub {
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

.tip, .empty {
  padding: 80rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: var(--color-ink-faint, #8A806F);
}
.empty { display: flex; flex-direction: column; align-items: center; gap: 12rpx; }
.empty-text { font-size: 26rpx; }

/* 馆区卡片 */
.card {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: var(--color-surface, #F4EDDF);
  border: 2rpx solid var(--color-ink, #29231D);
  border-radius: var(--radius-lg, 16rpx);
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: var(--shadow-card, 3rpx 3rpx 0 #29231D);
}
.card-icon {
  width: 76rpx;
  height: 76rpx;
  flex-shrink: 0;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-accent-soft, #E1E9D8);
}
.icon-tight { background: var(--color-warning-soft, #F0E6C4); }
.icon-full { background: var(--color-danger-soft, #ECD9CF); }

.card-body { flex: 1; min-width: 0; }
.card-name {
  font-size: 30rpx;
  font-weight: 700;
  color: var(--color-ink, #29231D);
}
.card-floor {
  margin-left: 10rpx;
  font-size: 22rpx;
  font-weight: 400;
  color: var(--color-ink-soft, #6E6659);
}
.card-meta {
  margin-top: 6rpx;
  font-size: 24rpx;
  color: var(--color-ink-soft, #6E6659);
}
.bar {
  margin-top: 12rpx;
  width: 240rpx;
  height: 10rpx;
  border-radius: 999rpx;
  background: var(--color-neutral-weak, #DDD2BB);
  overflow: hidden;
}
.bar-in { height: 100%; border-radius: 999rpx; }
.bar-free { background: var(--color-accent, #547044); }
.bar-tight { background: var(--color-warning, #9A7320); }
.bar-full { background: var(--color-danger, #A8503A); }

.chip {
  flex-shrink: 0;
  padding: 6rpx 20rpx;
  font-size: 22rpx;
  font-weight: 700;
  border-radius: 999rpx;
  border: 2rpx solid var(--color-ink, #29231D);
}
.chip-free { background: var(--color-accent-soft, #E1E9D8); color: var(--color-accent, #547044); }
.chip-tight { background: var(--color-warning-soft, #F0E6C4); color: var(--color-warning, #9A7320); }
.chip-full { background: var(--color-danger-soft, #ECD9CF); color: var(--color-danger, #A8503A); }

.card-arrow { flex-shrink: 0; }
</style>
