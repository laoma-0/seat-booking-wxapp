<template>
  <view class="container">
    <view v-for="item in areas" :key="item.areaId" class="card">
      <text>{{ item.areaName }}</text>
      <text>{{ item.floor }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref,onMounted} from 'vue'
import {request} from '@/utils/request'

interface Area {
  areaId: number;
  areaName: string;
  floor: number;
}

const areas = ref<Area[]>([])

onMounted(async() => {
  try {
    const data = await request<Area[]>('/api/area/list')
    areas.value = data
  } catch (error) {
    console.error('Error fetching areas:据报错啦，这里是index.vue', error)
  }
})
</script>


