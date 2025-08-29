App({
  globalData: {
    userInfo: null,
    baseUrl: 'http://localhost:8080/api',
    imageBaseUrl: 'http://localhost:8080',
    isLogin: false
  },

  // 构建图片完整URL
  buildImageUrl(imagePath) {
    if (!imagePath) return '';
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
      return imagePath;
    }
    return this.globalData.imageBaseUrl + imagePath;
  },

  onLaunch() {
    // 检查登录状态
    this.checkLoginStatus();
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.globalData.userInfo = userInfo;
      this.globalData.isLogin = true;
    }
  },

  // 微信登录
  wxLogin() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            // 获取用户信息
            wx.getUserProfile({
              desc: '用于完善用户资料',
              success: (userRes) => {
                const loginData = {
                  openId: res.code, // 实际项目中需要通过后端获取openId
                  nickName: userRes.userInfo.nickName,
                  avatarUrl: userRes.userInfo.avatarUrl
                };
                
                // 调用后端登录接口
                wx.request({
                  url: this.globalData.baseUrl + '/wechat/login',
                  method: 'POST',
                  data: loginData,
                  success: (loginRes) => {
                    if (loginRes.data.success) {
                      this.globalData.userInfo = loginRes.data.user;
                      this.globalData.isLogin = true;
                      wx.setStorageSync('userInfo', loginRes.data.user);
                      resolve(loginRes.data.user);
                    } else {
                      reject(new Error('登录失败'));
                    }
                  },
                  fail: reject
                });
              },
              fail: reject
            });
          } else {
            reject(new Error('获取code失败'));
          }
        },
        fail: reject
      });
    });
  },

  // 退出登录
  logout() {
    this.globalData.userInfo = null;
    this.globalData.isLogin = false;
    wx.removeStorageSync('userInfo');
  },

  // 通用请求方法
  request(options) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'content-type': 'application/json',
          ...options.header
        },
        success: (res) => {
          if (res.statusCode === 200 && res.data) {
            const body = res.data;
            if (typeof body.success === 'boolean') {
              if (body.success) {
                resolve(body);
              } else {
                wx.showToast({ title: body.message || '请求失败', icon: 'none' });
                reject(new Error(body.message || '请求失败'));
              }
            } else {
              resolve(body);
            }
          } else {
            wx.showToast({ title: '网络错误', icon: 'none' });
            reject(new Error('网络错误'));
          }
        },
        fail: (err) => {
          wx.showToast({ title: '网络异常', icon: 'none' });
          reject(err);
        }
      });
    });
  }
});

