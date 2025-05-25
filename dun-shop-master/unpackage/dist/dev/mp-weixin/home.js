"use strict";
const common_vendor = require("./common/vendor.js");
if (!Array) {
  const _easycom_uni_search_bar2 = common_vendor.resolveComponent("uni-search-bar");
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  (_easycom_uni_search_bar2 + _easycom_uni_icons2)();
}
const _easycom_uni_search_bar = () => "./uni_modules/uni-search-bar/components/uni-search-bar/uni-search-bar.js";
const _easycom_uni_icons = () => "./uni_modules/uni-icons/components/uni-icons/uni-icons.js";
if (!Math) {
  (_easycom_uni_search_bar + _easycom_uni_icons + ListItem)();
}
const ListItem = () => "./components/listItem.js";
const _sfc_main = {
  __name: "home",
  setup(__props) {
    const focus = (e) => {
      common_vendor.index.navigateTo({
        url: "../../pages/search/search"
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.p({
          cancelButton: "none",
          placeholder: "请输入查询信息"
        }),
        b: common_vendor.o(focus),
        c: common_vendor.p({
          type: "right"
        }),
        d: common_vendor.f(8, (item, index, i0) => {
          return {
            a: index,
            b: "07e72d3c-2-" + i0
          };
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-07e72d3c"]]);
exports.MiniProgramPage = MiniProgramPage;
//# sourceMappingURL=../.sourcemap/mp-weixin/home.js.map
