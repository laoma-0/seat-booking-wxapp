const BASE_URL = "http://localhost:8080"

export function request<T>(url: string, method: "GET" | "POST" = "GET", data?: any): Promise<T> {
  // 请求拦截:你好，请出示证件
  const token = uni.getStorageSync("token")
  const header: any = {
    "Content-Type": "application/json",
  }
  if (token) {
    header["Authorization"] = `Bearer ${token}`
  }


  
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
