"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Math) {
  colaUChat();
}
const colaUChat = () => "../../uni_modules/cola-u-chat/components/cola-u-chat/cola-u-chat.js";
const _sfc_main = {
  __name: "chat",
  setup(__props) {
    const dataKey = common_vendor.ref({
      userId: "selfUserId",
      msgId: "selfMsgId",
      name: "userName",
      message: "msg",
      avator: "avator",
      img: "img",
      read: "read"
    });
    const msgData = common_vendor.ref([
      {
        selfUserId: "101",
        selfMsgId: "",
        userName: "我的大聪明啊",
        msg: "我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "202",
        selfMsgId: "",
        userName: "xixixi",
        msg: "hhh",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "202",
        selfMsgId: "",
        userName: "xixixi",
        msg: "hhh",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "202",
        selfMsgId: "",
        userName: "xixixi",
        msg: "",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
        img: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "101",
        selfMsgId: "",
        userName: "xixixi",
        msg: "hhh",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "101",
        selfMsgId: "",
        userName: "xixixi",
        msg: "我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      },
      {
        selfUserId: "101",
        selfMsgId: "",
        userName: "xixixi",
        msg: "",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
        img: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      }
    ]);
    const historyMsg = common_vendor.ref({});
    const updateData = common_vendor.reactive({});
    const moreFunction = common_vendor.ref([
      {
        name: "照片",
        icon: "image",
        img: ""
      },
      {
        name: "拍摄",
        icon: "camera",
        img: ""
      },
      {
        name: "语音通话",
        icon: "phone-filled",
        img: ""
      },
      {
        name: "位置",
        icon: "location-filled",
        img: ""
      },
      {
        name: "红包",
        icon: "wallet",
        img: ""
      },
      {
        name: "群工具",
        icon: "list",
        img: ""
      }
    ]);
    const send = (e) => {
      updateData.value = {
        selfUserId: "101",
        selfMsgId: "",
        userName: "我的大聪明啊",
        msg: e,
        img: "",
        avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
      };
    };
    const clickMore = (e) => {
      common_vendor.index.__f__("log", "at pages/chat/chat.vue:126", e);
    };
    const refreshNow = (e) => {
      common_vendor.index.__f__("log", "at pages/chat/chat.vue:129", e);
    };
    common_vendor.onMounted(() => {
      setTimeout(() => {
        updateData.value = {
          selfUserId: "101",
          selfMsgId: "",
          userName: "我的大聪明啊",
          msg: "我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪我的天哪",
          img: "",
          avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
        };
      }, 3e3);
      setTimeout(() => {
        updateData.value = {
          selfUserId: "202",
          selfMsgId: "",
          userName: "xixixi",
          msg: "",
          img: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png",
          avator: "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/unicloudlogo.png"
        };
      }, 5e3);
    });
    return (_ctx, _cache) => {
      return {
        a: common_vendor.o(refreshNow),
        b: common_vendor.o(send),
        c: common_vendor.o(clickMore),
        d: common_vendor.p({
          isUseRefresh: true,
          userIdName: "userId",
          userId: "101",
          moreFunction: moreFunction.value,
          historyMsg: historyMsg.value,
          updateMsg: updateData,
          messageData: msgData.value,
          isUseRead: true,
          dataKeys: dataKey.value,
          IDType: "msgId"
        })
      };
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/chat/chat.js.map
