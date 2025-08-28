const app = getApp();

Page({
  data: {
    banners: [],
    categories: [],
    popularKnots: [],
    latestKnots: []
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    // 页面显示时刷新数据
  },

  // 加载数据
  loadData() {
    this.loadBanners();
    this.loadCategories();
    this.loadPopularKnots();
    this.loadLatestKnots();
  },

  // 加载轮播图
  loadBanners() {
    // 模拟轮播图数据
    this.setData({
      banners: [
        {
          id: 1,
          imageUrl: '/images/banner1.jpg',
          knotId: 1
        },
        {
          id: 2,
          imageUrl: '/images/banner2.webp',
          knotId: 2
        }
      ]
    });
  },

  // 加载分类
  loadCategories() {
    app.request({
      url: '/categories'
    }).then(res => {
      this.setData({
        categories: res.data || []
      });
    }).catch(err => {
      console.error('加载分类失败:', err);
      wx.showToast({
        title: '加载分类失败',
        icon: 'none'
      });
    });
  },

  // 加载热门绳结
  loadPopularKnots() {
    app.request({
      url: '/knots/popular?limit=6'
    }).then(res => {
      this.setData({
        popularKnots: res.data || []
      });
    }).catch(err => {
      console.error('加载热门绳结失败:', err);
      wx.showToast({
        title: '加载热门绳结失败',
        icon: 'none'
      });
    });
  },

  // 加载最新绳结
  loadLatestKnots() {
    app.request({
      url: '/knots?page=1&pageSize=6'
    }).then(res => {
      this.setData({
        latestKnots: (res.data || [])
      });
    }).catch(err => {
      console.error('加载最新绳结失败:', err);
      wx.showToast({
        title: '加载最新绳结失败',
        icon: 'none'
      });
    });
  },

  // 跳转到搜索页面
  goToSearch() {
    wx.switchTab({
      url: '/pages/search/search'
    });
  },

  // 跳转到分类页面
  goToCategory(e) {
    const categoryId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/category/category?id=${categoryId}`
    });
  },

  // 跳转到绳结详情
  goToDetail(e) {
    const knotId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${knotId}`
    });
  },

  // 跳转到更多页面
  goToMore(e) {
    const type = e.currentTarget.dataset.type;
    wx.navigateTo({
      url: `/pages/more/more?type=${type}`
    });
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadData();
    wx.stopPullDownRefresh();
  }
});

