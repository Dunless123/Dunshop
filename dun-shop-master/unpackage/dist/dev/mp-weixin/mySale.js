"use strict";
const common_vendor = require("./common/vendor.js");
const common_assets = require("./common/assets.js");
if (!Array) {
  const _easycom_uni_popup2 = common_vendor.resolveComponent("uni-popup");
  _easycom_uni_popup2();
}
const _easycom_uni_popup = () => "./uni_modules/uni-popup/components/uni-popup/uni-popup.js";
if (!Math) {
  (ListItem + _easycom_uni_popup)();
}
const ListItem = () => "./components/listItem.js";
const _sfc_main = {
  __name: "mySale",
  setup(__props) {
    const popup = common_vendor.ref(null);
    common_vendor.ref(null);
    const popSelectStatus = common_vendor.ref(false);
    const allSelectStatus = common_vendor.ref(false);
    const selectData = common_vendor.ref([]);
    const listData = common_vendor.ref([{
      id: 1,
      checked: false
    }, {
      id: 2,
      checked: false
    }, {
      id: 3,
      checked: false
    }]);
    const navigato = () => {
      common_vendor.index.navigateTo({
        url: "/pages/itemDetail/itemDetail"
      });
    };
    common_vendor.watch(selectData.value, (newValue) => {
      if (newValue.length == listData.value.length) {
        allSelectStatus.value = true;
      } else {
        allSelectStatus.value = false;
      }
    });
    const selectTap = (item) => {
      if (selectData.value.length == 0) {
        popSelectStatus.value = true;
        popup.value.open();
        popup.value.closeMask();
        common_vendor.index.hideTabBar({
          animation: true
        });
      }
      if (item.checked) {
        item.checked = false;
        selectData.value.pop(item);
      } else {
        item.checked = true;
        selectData.value.push(item);
      }
      if (selectData.value.length == 0) {
        pCancel();
      }
    };
    const pCancel = () => {
      popSelectStatus.value = false;
      popup.value.close();
      setTimeout(() => {
        common_vendor.index.showTabBar({
          animation: true
        });
      }, 200);
      listData.value.forEach((item) => {
        item.checked = false;
      });
      selectData.value.length = 0;
    };
    const allSelect = () => {
      if (allSelectStatus.value) {
        allSelectStatus.value = false;
        pCancel();
      } else {
        allSelectStatus.value = true;
        listData.value.forEach((item) => {
          if (selectData.value.includes(item)) {
            return;
          } else {
            item.checked = true;
            selectData.value.push(item);
          }
        });
      }
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(listData.value, (item, id, i0) => {
          return {
            a: id,
            b: common_vendor.o(($event) => selectTap(item), id),
            c: common_vendor.o(navigato, id),
            d: "e1a246d8-0-" + i0,
            e: common_vendor.p({
              popSelect: item.checked,
              noNaviEle: true
            })
          };
        }),
        b: common_vendor.p({
          isAdd: true
        }),
        c: allSelectStatus.value,
        d: common_vendor.o(allSelect),
        e: common_vendor.t(selectData.value.length),
        f: common_vendor.t(listData.value.length),
        g: common_vendor.o(pCancel),
        h: common_assets._imports_0,
        i: common_assets._imports_0,
        j: common_vendor.sr(popup, "e1a246d8-2", {
          "k": "popup"
        }),
        k: common_vendor.p({
          type: "bottom",
          ["background-color"]: "#fff",
          ["is-mask-click"]: false,
          backgroundColor: "rgba(247, 247, 247, 0.3)"
        })
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-e1a246d8"]]);
exports.MiniProgramPage = MiniProgramPage;
//# sourceMappingURL=../.sourcemap/mp-weixin/mySale.js.map
