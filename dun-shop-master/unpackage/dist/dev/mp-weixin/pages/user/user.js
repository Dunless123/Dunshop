"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
if (!Array) {
  const _easycom_uni_col2 = common_vendor.resolveComponent("uni-col");
  const _easycom_uni_row2 = common_vendor.resolveComponent("uni-row");
  const _easycom_uni_card2 = common_vendor.resolveComponent("uni-card");
  (_easycom_uni_col2 + _easycom_uni_row2 + _easycom_uni_card2)();
}
const _easycom_uni_col = () => "../../uni_modules/uni-row/components/uni-col/uni-col.js";
const _easycom_uni_row = () => "../../uni_modules/uni-row/components/uni-row/uni-row.js";
const _easycom_uni_card = () => "../../uni_modules/uni-card/components/uni-card/uni-card.js";
if (!Math) {
  (_easycom_uni_col + _easycom_uni_row + _easycom_uni_card)();
}
const _sfc_main = {
  __name: "user",
  setup(__props) {
    const login = () => {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(login),
        b: common_vendor.p({
          span: 8
        }),
        c: common_vendor.p({
          span: 8
        }),
        d: common_vendor.p({
          span: 8
        }),
        e: common_assets._imports_0,
        f: common_assets._imports_0,
        g: common_assets._imports_0,
        h: common_vendor.p({
          span: 8
        }),
        i: common_vendor.p({
          span: 8
        }),
        j: common_vendor.p({
          span: 8
        }),
        k: common_vendor.p({
          title: "我的交易"
        }),
        l: common_vendor.p({
          span: 8
        }),
        m: common_vendor.p({
          span: 8
        }),
        n: common_vendor.p({
          span: 8
        }),
        o: common_vendor.p({
          title: "我的服务"
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-0f7520f0"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/user/user.js.map
