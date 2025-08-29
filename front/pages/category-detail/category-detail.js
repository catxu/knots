const app = getApp();

Page({
  data: {
    categoryId: null,
    categoryName: '',
    knots: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    const { id, name } = options || {};
    this.setData({
      categoryId: Number(id),
      categoryName: decodeURIComponent(name) || ''
    });
    this.loadKnots(true);
  },

  loadKnots(reset = false) {
    if (this.data.loading) return;
    const nextPage = reset ? 1 : this.data.page;
    this.setData({ loading: true });

    app.request({
      url: `/knots?categoryId=${this.data.categoryId}&page=${nextPage}&pageSize=${this.data.size}`
    }).then(res => {
      const list = res.data || [];
      // 构建完整的图片URL
      list.forEach(knot => {
        if (knot.coverImage) {
          knot.coverImage = app.buildImageUrl(knot.coverImage);
        }
      });
      const newList = reset ? list : [...this.data.knots, ...list];
      this.setData({
        knots: newList,
        page: nextPage + 1,
        hasMore: (res.page || 1) * this.data.size < (res.totalCount || newList.length),
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


