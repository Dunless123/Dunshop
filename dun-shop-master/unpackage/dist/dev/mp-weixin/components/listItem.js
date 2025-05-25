"use strict";
const common_vendor = require("../common/vendor.js");
const common_assets = require("../common/assets.js");
if (!Array) {
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  _easycom_uni_icons2();
}
const _easycom_uni_icons = () => "../uni_modules/uni-icons/components/uni-icons/uni-icons.js";
if (!Math) {
  _easycom_uni_icons();
}
const _sfc_main = {
  __name: "listItem",
  props: {
    isMore: {
      type: Boolean,
      default: false
    },
    isAdd: {
      type: Boolean,
      default: false
    },
    popSelect: {
      type: Boolean,
      default: false
    },
    noNaviEle: {
      type: Boolean,
      default: false
    },
    item: {
      type: Object,
      default: () => {
        return {
          name: "可爱萌宠",
          picurl: "/static/logo.png"
        };
      }
    }
  },
  setup(__props) {
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: !__props.isMore && !__props.isAdd && !__props.noNaviEle
      }, !__props.isMore && !__props.isAdd && !__props.noNaviEle ? common_vendor.e({
        b: __props.item.picurl,
        c: __props.popSelect
      }, __props.popSelect ? {
        d: common_vendor.p({
          type: "checkmarkempty",
          size: "30"
        })
      } : {}, {
        e: common_vendor.t(__props.item.name)
      }) : {}, {
        f: __props.noNaviEle
      }, __props.noNaviEle ? common_vendor.e({
        g: __props.item.picurl,
        h: __props.popSelect
      }, __props.popSelect ? {
        i: common_vendor.p({
          type: "checkmarkempty",
          size: "30"
        })
      } : {}, {
        j: common_vendor.t(__props.item.name),
        k: common_vendor.o((...args) => _ctx.navigato && _ctx.navigato(...args))
      }) : {}, {
        l: __props.isMore
      }, __props.isMore ? {
        m: common_assets._imports_0,
        n: common_vendor.p({
          type: "more-filled",
          size: "34",
          color: "#fff"
        })
      } : {}, {
        o: __props.isAdd
      }, __props.isAdd ? {
        p: common_vendor.p({
          type: "plusempty",
          size: "40"
        })
      } : {});
    };
  }
};
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-b7a1f9ce"]]);
wx.createComponent(Component);
//# sourceMappingURL=../../.sourcemap/mp-weixin/components/listItem.js.map
