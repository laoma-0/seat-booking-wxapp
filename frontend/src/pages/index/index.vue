<template>
  <view class="container">
    <view class="title">自习室座位预约</view>
    <view v-for="item in areas" :key="item.areaId" class="card" @tap="goSeat(item)">
      <text>{{ item.areaName }}</text>
      <text>{{ item.floor }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { request } from '@/utils/request'

interface Area {
  areaId: number
  areaName: string
  floor: number
}

const areas = ref<Area[]>([])

function goSeat(item: Area) {
  uni.navigateTo({
    url: '/pages/seat/seat?areaId=' + item.areaId
  })
}

onMounted(async () => {
  try {
    const data = await request<Area[]>('/api/area/list')
    areas.value = data
  } catch (error) {
    console.error('Error fetching areas:', error)
  }
})
</script>
<style scoped>
.container {
  padding: 24rpx;
  background: var(--color-bg, #E8E0CF);
  min-height: 100vh;
}
.title {
  font-size: 40rpx;
  font-weight: bold;
  color: var(--color-ink, #29231D);
  margin-bottom: 32rpx;
}
.card {
  background: var(--color-surface, #F4EDDF);
  border: 2rpx solid var(--color-ink, #29231D);
  border-radius: 12rpx;
  padding: 24rpx;
  margin-bottom: 16rpx;
  box-shadow: 3rpx 3rpx 0 var(--color-ink, #29231D);
}
</style>
