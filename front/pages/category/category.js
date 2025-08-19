const app = getApp();

Page({
  data: {
    categories: [],
    loading: true
  },

  onLoad() {
    this.loadCategories();
  },

  onShow() {
    // 页面显示时刷新数据
  },

  // 加载分类数据
  loadCategories() {
    this.setData({
      loading: true
    });

    app.request({
      url: '/categories'
    }).then(res => {
      this.setData({
        categories: res,
        loading: false
      });
    }).catch(err => {
      console.error('加载分类失败:', err);
      wx.showToast({
        title: '加载分类失败',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
    });
  },

  // 跳转到分类详情
  goToCategoryDetail(e) {
    const categoryId = Number(e.currentTarget.dataset.id);
    const category = this.data.categories.find(c => Number(c.id) === categoryId);
    const name = category ? encodeURIComponent(category.name) : '';
    wx.navigateTo({
      url: `/pages/category-detail/category-detail?id=${categoryId}&name=${name}`
    });
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadCategories();
    wx.stopPullDownRefresh();
  }
});