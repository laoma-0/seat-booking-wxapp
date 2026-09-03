const BASE_URL = "http://localhost:8080"

export function request<T>(url: string, method: "GET" | "POST" = "GET", data?: any): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      success: (res) => { resolve(res.data as T) },
      fail: (err) => { reject(err) }
    })
  })
}
