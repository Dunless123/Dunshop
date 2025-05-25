"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Math) {
  MySale();
}
const MySale = () => "../mySale/mySale2.js";
const _sfc_main = {
  __name: "myShop",
  setup(__props) {
    const items = common_vendor.ref([{
      name: "我的出售",
      id: "op1"
    }, {
      name: "待发货",
      id: "op2"
    }, {
      name: "还价管理",
      id: "op3"
    }]);
    const scrollViewId = common_vendor.ref("");
    const current = common_vendor.ref(0);
    const onClickItem = (e) => {
      if (current.value != e) {
        current.value = e;
      }
    };
    const onSwiperItem = (e) => {
      scrollViewId.value = items.value[e.detail.current].id;
      current.value = e.detail.current;
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(items.value, (item, index, i0) => {
          return {
            a: common_vendor.t(item.name),
            b: items.value[index].id,
            c: common_vendor.n(current.value == index ? "tabar active" : "tabar"),
            d: index,
            e: common_vendor.o(($event) => onClickItem(index), index)
          };
        }),
        b: scrollViewId.value,
        c: common_vendor.o(onSwiperItem),
        d: current.value
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-54163c1c"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/myShop/myShop.js.map
