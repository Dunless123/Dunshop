"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
if (!Array) {
  const _easycom_uni_card2 = common_vendor.resolveComponent("uni-card");
  const _easycom_uni_goods_nav2 = common_vendor.resolveComponent("uni-goods-nav");
  (_easycom_uni_card2 + _easycom_uni_goods_nav2)();
}
const _easycom_uni_card = () => "../../uni_modules/uni-card/components/uni-card/uni-card.js";
const _easycom_uni_goods_nav = () => "../../uni_modules/uni-goods-nav/components/uni-goods-nav/uni-goods-nav.js";
if (!Math) {
  (_easycom_uni_card + _easycom_uni_goods_nav)();
}
const _sfc_main = {
  __name: "itemDetail",
  setup(__props) {
    const html = common_vendor.ref();
    const options = common_vendor.ref([{
      icon: "headphones",
      text: "客服"
    }, {
      icon: "shop",
      text: "店铺",
      infoBackgroundColor: "#007aff",
      infoColor: "red"
    }]);
    const buttonGroup = common_vendor.ref([
      {
        text: "聊一聊",
        backgroundColor: "#ff0000",
        color: "#fff"
      },
      {
        text: "我想要",
        backgroundColor: "#ffa200",
        color: "#fff"
      }
    ]);
    const onClick = (e) => {
      common_vendor.index.showToast({
        title: e.content.text,
        icon: "none"
      });
    };
    const buttonClick = (e) => {
      common_vendor.index.showToast({
        title: e.content.text,
        icon: "none"
      });
    };
    const filesList = common_vendor.ref([{
      url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
    }, {
      url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
    }, {
      url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
    }, {
      url: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
    }]);
    const prviewImage = (img, index) => {
      let urls = [];
      filesList.value.forEach((i) => {
        urls.push(i.url);
      });
      common_vendor.index.previewImage({
        urls,
        current: index
      });
    };
    common_vendor.onMounted(() => {
      html.value = common_vendor.index.getStorageSync("html");
    });
    return (_ctx, _cache) => {
      return {
        a: common_assets._imports_0,
        b: html.value,
        c: common_vendor.f(filesList.value, (item, index, i0) => {
          return {
            a: item.url,
            b: common_vendor.o(($event) => prviewImage(item, index), index),
            c: index
          };
        }),
        d: common_vendor.o(onClick),
        e: common_vendor.o(buttonClick),
        f: common_vendor.p({
          options: options.value,
          fill: true,
          ["button-group"]: buttonGroup.value
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-a2cf8634"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/itemDetail/itemDetail.js.map
