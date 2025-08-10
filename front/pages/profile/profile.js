const app = getApp();

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    this.checkLoginStatus();
  },

  onShow() {
    this.checkLoginStatus();
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = wx.getStorageSync('userInfo');
    this.setData({
      userInfo: userInfo
    });
  },

  // 微信登录
  login() {
    app.wxLogin().then(userInfo => {
      this.setData({
        userInfo: userInfo
      });
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
    }).catch(err => {
      console.error('登录失败:', err);
      wx.showToast({
        title: '登录失败',
        icon: 'none'
      });
    });
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout();
          this.setData({
            userInfo: null
          });
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }
    });
  },

  // 跳转到收藏页面
  goToFavorites() {
    if (!this.data.userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    wx.navigateTo({
      url: '/pages/favorites/favorites'
    });
  },

  // 跳转到历史记录
  goToHistory() {
    wx.navigateTo({
      url: '/pages/history/history'
    });
  },

  // 跳转到设置页面
  goToSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings'
    });
  },

  // 跳转到关于页面
  goToAbout() {
    wx.navigateTo({
      url: '/pages/about/about'
    });
  }
});