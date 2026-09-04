const BASE_URL = "http://10.131.241.229:8080"

export function request<T>(url: string, method: "GET" | "POST" = "GET", data?: any): Promise<T> {
  //1.用户登录后，获取到token
  //解释代码: 从本地存储中获取token，如果存在则添加到请求头中
  const token = uni.getStorageSync("token")
  const header: any = {
    "Content-Type": "application/json",
  }
  if (token) {
    header["Authorization"] = `Bearer ${token}`
  }
   //发送请求
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: header,
      success: (res) => {
        if (res.statusCode === 401) {
          uni.removeStorageSync("token")
          uni.showToast({
            title: "登录已过期，请重新登录",
            icon: "none",
            duration: 2000,
          })
          return reject(res)
        }
        resolve(res.data as T) 
      },
      fail: (err) => { reject(err) }
    })
  })
  
}
