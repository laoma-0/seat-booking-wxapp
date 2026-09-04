<template>
	<view class="login-page">
		<view class="title">自习室座位预约</view>
		<button type="button" @click="doLogin" class="primary-btn">微信一键登录</button>
	</view>
</template>

<script setup lang="ts">
import {request} from '@/utils/request'
async function doLogin(){
	try{
		// 1. 拿微信登录 code（小程序环境）查阅证件
		const loginRes=await uni.login({provider:'weixin'})
	    const code=(loginRes as any).code
		if(!code){
			uni.showToast({
				title:'登录失败',
				icon:'error'
			})
			return
		}
	
	    //2.调用后端接口，拿到token
	    const token=await request(
			'/api/auth/login',
			'POST',	
			{code}
			
		)
	   if(token){
	     	uni.setStorageSync('token',token)
		    uni.showToast({
			title:'登录成功',
			icon:'success'
		})
		uni.switchTab({
			url:'/pages/index/index'
		})
	    }else{
		   uni.showToast({
			title:'登录失败',
			icon:'error'
		 })
		}
	}catch (e){
		console.error(e)
		uni.showToast({
			title:'登录异常！',
			icon:'error'
		})
	}
}

	

</script>

<style>
.login-page{
	display:flex;
	flex-direction:column;
	justify-content:center;
	align-items:center;
	padding-top: 200rpx;
}
.title{
	font-size: 36rpx;
	font-weight: bold;
	margin-bottom: 80rpx;
}
.primary-btn {
  background-color: #547044;
  color: #fff;
  border: 2rpx solid #29231D;
  box-shadow: 4rpx 4rpx 0 #29231D;
}

</style>