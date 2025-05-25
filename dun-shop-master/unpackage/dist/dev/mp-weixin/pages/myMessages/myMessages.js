"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _easycom_uni_list_chat2 = common_vendor.resolveComponent("uni-list-chat");
  const _easycom_uni_list2 = common_vendor.resolveComponent("uni-list");
  (_easycom_uni_list_chat2 + _easycom_uni_list2)();
}
const _easycom_uni_list_chat = () => "../../uni_modules/uni-list/components/uni-list-chat/uni-list-chat.js";
const _easycom_uni_list = () => "../../uni_modules/uni-list/components/uni-list/uni-list.js";
if (!Math) {
  (_easycom_uni_list_chat + _easycom_uni_list)();
}
const _sfc_main = {
  __name: "myMessages",
  setup(__props) {
    return (_ctx, _cache) => {
      return {
        a: common_vendor.p({
          title: "uni-app",
          avatar: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          note: "您收到一条新的消息",
          time: "2020-02-02 20:20",
          ["badge-text"]: "12",
          link: "navigateTo",
          to: "/pages/chat/chat?id=1&myid=2"
        }),
        b: common_vendor.p({
          title: "uni-app",
          avatar: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          note: "您收到一条新的消息",
          time: "2020-02-02 20:20",
          ["badge-text"]: "12",
          link: "navigateTo",
          to: "/pages/chat/chat"
        }),
        c: common_vendor.p({
          title: "uni-app",
          avatar: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          note: "您收到一条新的消息",
          time: "2020-02-02 20:20",
          ["badge-text"]: "12",
          link: "navigateTo",
          to: "/pages/chat/chat"
        }),
        d: common_vendor.p({
          title: "uni-app",
          avatar: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          note: "您收到一条新的消息",
          time: "2020-02-02 20:20",
          ["badge-text"]: "12",
          link: "navigateTo",
          to: "/pages/chat/chat"
        }),
        e: common_vendor.p({
          title: "uni-app",
          avatar: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          note: "您收到一条新的消息",
          time: "2020-02-02 20:20",
          ["badge-text"]: "12",
          link: "navigateTo",
          to: "/pages/chat/chat"
        }),
        f: common_vendor.p({
          border: true
        })
      };
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/myMessages/myMessages.js.map
