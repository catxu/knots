const app = getApp();

Page({
  data: {
    knot: {},
    steps: [],
    relatedKnots: [],
    imageUrls: [],
    isFavorite: false
  },

  onLoad(options) {
    const knotId = options.id;
    if (knotId) {
      this.loadKnotDetail(knotId);
      this.loadRelatedKnots(knotId);
      this.checkFavoriteStatus(knotId);
    }
  },

  // 加载绳结详情
  loadKnotDetail(knotId) {
    app.request({
      url: `/knots/${knotId}`
    }).then(res => {
      const data = res.data || {};
      // 构建完整的图片URL
      if (data.coverImage) {
        data.coverImage = app.buildImageUrl(data.coverImage);
      }
      if (data.images) {
        data.images.forEach(img => {
          if (img.imageUrl) {
            img.imageUrl = app.buildImageUrl(img.imageUrl);
          }
        });
      }
      this.setData({
        knot: data,
        steps: this.parseSteps(data.steps),
        imageUrls: data.images ? data.images.map(img => img.imageUrl) : []
      });
      
      // 设置页面标题
      wx.setNavigationBarTitle({
        title: data.name
      });
    }).catch(err => {
      console.error('加载绳结详情失败:', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    });
  },

  // 解析步骤数据
  parseSteps(stepsJson) {
    try {
      const steps = JSON.parse(stepsJson);
      return Array.isArray(steps) ? steps : [];
    } catch (e) {
      console.error('解析步骤数据失败:', e);
      return [];
    }
  },

  // 加载相关推荐
  loadRelatedKnots(knotId) {
    app.request({
      url: `/knots?page=1&pageSize=4`
    }).then(res => {
      // 过滤掉当前绳结
      const list = res.data || [];
      const related = list.filter(knot => knot.id != knotId);
      // 构建完整的图片URL
      related.forEach(knot => {
        if (knot.coverImage) {
          knot.coverImage = app.buildImageUrl(knot.coverImage);
        }
      });
      this.setData({
        relatedKnots: related
      });
    }).catch(err => {
      console.error('加载相关推荐失败:', err);
    });
  },

  // 检查收藏状态
  checkFavoriteStatus(knotId) {
    const favorites = wx.getStorageSync('favorites') || [];
    const isFavorite = favorites.includes(parseInt(knotId));
    this.setData({
      isFavorite: isFavorite
    });
  },

  // 切换收藏状态
  toggleFavorite() {
    const knotId = this.data.knot.id;
    let favorites = wx.getStorageSync('favorites') || [];
    
    if (this.data.isFavorite) {
      // 取消收藏
      favorites = favorites.filter(id => id !== knotId);
      wx.showToast({
        title: '已取消收藏',
        icon: 'success'
      });
    } else {
      // 添加收藏
      favorites.push(knotId);
      wx.showToast({
        title: '已添加到收藏',
        icon: 'success'
      });
    }
    
    wx.setStorageSync('favorites', favorites);
    this.setData({
      isFavorite: !this.data.isFavorite
    });
  },

  // 分享绳结
  shareKnot() {
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    });
  },

  // 预览图片
  previewImage(e) {
    const urls = e.currentTarget.dataset.urls;
    const current = e.currentTarget.dataset.current;
    
    wx.previewImage({
      urls: urls,
      current: current
    });
  },

  // 跳转到其他详情页
  goToDetail(e) {
    const knotId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${knotId}`
    });
  },

  // 分享给朋友
  onShareAppMessage() {
    return {
      title: this.data.knot.name,
      desc: this.data.knot.description,
      path: `/pages/detail/detail?id=${this.data.knot.id}`,
      imageUrl: this.data.knot.coverImage
    };
  },

  // 分享到朋友圈
  onShareTimeline() {
    return {
      title: this.data.knot.name,
      imageUrl: this.data.knot.coverImage
    };
  }
});

