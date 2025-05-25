"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _easycom_uni_nav_bar2 = common_vendor.resolveComponent("uni-nav-bar");
  const _easycom_uni_file_picker2 = common_vendor.resolveComponent("uni-file-picker");
  const _easycom_uni_card2 = common_vendor.resolveComponent("uni-card");
  const _easycom_uni_list_item2 = common_vendor.resolveComponent("uni-list-item");
  const _easycom_uni_list2 = common_vendor.resolveComponent("uni-list");
  (_easycom_uni_nav_bar2 + _easycom_uni_file_picker2 + _easycom_uni_card2 + _easycom_uni_list_item2 + _easycom_uni_list2)();
}
const _easycom_uni_nav_bar = () => "../../uni_modules/uni-nav-bar/components/uni-nav-bar/uni-nav-bar.js";
const _easycom_uni_file_picker = () => "../../uni_modules/uni-file-picker/components/uni-file-picker/uni-file-picker.js";
const _easycom_uni_card = () => "../../uni_modules/uni-card/components/uni-card/uni-card.js";
const _easycom_uni_list_item = () => "../../uni_modules/uni-list/components/uni-list-item/uni-list-item.js";
const _easycom_uni_list = () => "../../uni_modules/uni-list/components/uni-list/uni-list.js";
if (!Math) {
  (_easycom_uni_nav_bar + editorVue + _easycom_uni_file_picker + _easycom_uni_card + _easycom_uni_list_item + _easycom_uni_list)();
}
const editorVue = () => "../../components/editor/editor.js";
const _sfc_main = {
  __name: "addSale",
  setup(__props) {
    common_vendor.ref("");
    const btn = () => {
      common_vendor.index.showToast({
        title: "fabu"
      });
    };
    const back = () => {
      common_vendor.index.navigateBack({
        delta: 1
      });
    };
    const price = () => {
      common_vendor.index.showToast({
        title: "price"
      });
    };
    const tradeWay = () => {
      common_vendor.index.showToast({
        title: "tradeWay"
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(back),
        b: common_vendor.o(btn),
        c: common_vendor.p({
          ["left-icon"]: "left",
          title: "发布宝贝",
          shadow: true,
          border: false,
          stat: false
        }),
        d: common_vendor.p({
          limit: "9",
          title: "最多选择9张图片"
        }),
        e: common_vendor.p({
          ["is-shadow"]: true
        }),
        f: common_vendor.o(price),
        g: common_vendor.p({
          title: "价格",
          showArrow: true,
          rightText: "0.00",
          clickable: true
        }),
        h: common_vendor.o(tradeWay),
        i: common_vendor.p({
          title: "发货方式",
          showArrow: true,
          rightText: "下线交易",
          clickable: true
        }),
        j: common_vendor.p({
          ["is-shadow"]: true,
          padding: "0",
          spacing: "0"
        })
      };
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/addSale/addSale.js.map
