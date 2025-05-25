"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
require("../../utils/request.js");
if (!Array) {
  const _component_template = common_vendor.resolveComponent("template");
  _component_template();
}
const _sfc_main = {
  __name: "login",
  setup(__props) {
    const avatar = common_vendor.ref("");
    const name = common_vendor.ref("");
    const login = () => {
      common_vendor.index.login({
        "provider": "weixin",
        "onlyAuthorize": true,
        // 微信登录仅请求授权认证
        success: async function(event) {
          common_vendor.index.getUserInfo({
            provider: "weixin",
            success: function(infoRes) {
              common_vendor.index.__f__("log", "at pages/login/login.vue:50", infoRes.userInfo);
              avatar.value = infoRes.userInfo.avatarUrl;
              name.value = infoRes.userInfo.nickName;
            }
          });
        },
        fail: function(err) {
          common_vendor.index.showToast({
            title: err.data,
            icon: "error"
          });
        }
      });
    };
    return (_ctx, _cache) => {
      return {
        a: avatar.value,
        b: common_vendor.t(name.value),
        c: common_vendor.o(login),
        d: common_assets._imports_0$1
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
