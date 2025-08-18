const app = getApp();

Page({
  data: {
    keyword: '',
    searchHistory: [],
    hotKeywords: ['平结', '八字结', '双套结', '蝴蝶结', '渔人结', '绳结'],
    knots: [],
    total: 0,
    page: 0,
    size: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadSearchHistory();
  },

  // 加载搜索历史
  loadSearchHistory() {
    const history = wx.getStorageSync('searchHistory') || [];
    this.setData({
      searchHistory: history
    });
  },

  // 输入框变化
  onInputChange(e) {
    this.setData({
      keyword: e.detail.value
    });
  },

  // 执行搜索
  onSearch() {
    if (!this.data.keyword.trim()) {
      return;
    }
    
    this.saveSearchHistory(this.data.keyword);
    this.searchKnots();
  },

  // 保存搜索历史
  saveSearchHistory(keyword) {
    let history = wx.getStorageSync('searchHistory') || [];
    // 移除重复项
    history = history.filter(item => item !== keyword);
    // 添加到开头
    history.unshift(keyword);
    // 限制数量
    if (history.length > 10) {
      history = history.slice(0, 10);
    }
    
    wx.setStorageSync('searchHistory', history);
    this.setData({
      searchHistory: history
    });
  },

  // 搜索绳结
  searchKnots() {
    if (this.data.loading) return;
    
    this.setData({
      loading: true,
      page: 0,
      hasMore: true
    });

    app.request({
      url: `/search?keyword=${encodeURIComponent(this.data.keyword)}&page=0&size=${this.data.size}`
    }).then(res => {
      this.setData({
        knots: res.content,
        total: res.totalElements,
        hasMore: !res.last,
        loading: false
      });
    }).catch(err => {
      console.error('搜索失败:', err);
      wx.showToast({
        title: '搜索失败',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
    });
  },

  // 加载更多
  loadMore() {
    if (this.data.loading || !this.data.hasMore) return;
    
    this.setData({
      loading: true,
      page: this.data.page + 1
    });

    app.request({
      url: `/search?keyword=${encodeURIComponent(this.data.keyword)}&page=${this.data.page}&size=${this.data.size}`
    }).then(res => {
      this.setData({
        knots: [...this.data.knots, ...res.content],
        hasMore: !res.last,
        loading: false
      });
    }).catch(err => {
      console.error('加载更多失败:', err);
      wx.showToast({
        title: '加载更多失败',
        icon: 'none'
      });
      this.setData({
        loading: false
      });
    });
  },

  // 点击历史记录
  onHistoryTap(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({
      keyword: keyword
    });
    this.searchKnots();
  },

  // 点击热门搜索
  onHotTap(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({
      keyword: keyword
    });
    this.searchKnots();
  },

  // 清空搜索
  clearSearch() {
    this.setData({
      keyword: '',
      knots: [],
      total: 0
    });
  },

  // 清空历史
  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('searchHistory');
          this.setData({
            searchHistory: []
          });
        }
      }
    });
  },

  // 返回
  goBack() {
    wx.navigateBack();
  },

  // 跳转到详情
  goToDetail(e) {
    const knotId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${knotId}`
    });
  }
});

