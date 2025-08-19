const app = getApp();

Page({
  data: {
    categoryId: null,
    categoryName: '',
    knots: [],
    page: 0,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    const { id, name } = options || {};
    this.setData({
      categoryId: Number(id),
      categoryName: name || ''
    });
    this.loadKnots(true);
  },

  loadKnots(reset = false) {
    if (this.data.loading) return;
    const nextPage = reset ? 0 : this.data.page;
    this.setData({ loading: true });

    app.request({
      url: `/knots?categoryId=${this.data.categoryId}&page=${nextPage}&size=${this.data.size}`
    }).then(res => {
      const newList = reset ? res.content : [...this.data.knots, ...res.content];
      this.setData({
        knots: newList,
        page: nextPage + 1,
        hasMore: !res.last,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
      wx.showToast({ title: '加载失败', icon: 'none' });
    });
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.loadKnots(false);
    }
  },

  goToDetail(e) {
    const knotId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/detail/detail?id=${knotId}` });
  }
});


