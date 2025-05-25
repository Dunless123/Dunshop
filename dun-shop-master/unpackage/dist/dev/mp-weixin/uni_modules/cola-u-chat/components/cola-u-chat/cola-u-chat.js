"use strict";
const common_vendor = require("../../../../common/vendor.js");
if (!Array) {
  const _easycom_uni_icons2 = common_vendor.resolveComponent("uni-icons");
  const _easycom_uni_easyinput2 = common_vendor.resolveComponent("uni-easyinput");
  (_easycom_uni_icons2 + _easycom_uni_easyinput2)();
}
const _easycom_uni_icons = () => "../../../uni-icons/components/uni-icons/uni-icons.js";
const _easycom_uni_easyinput = () => "../../../uni-easyinput/components/uni-easyinput/uni-easyinput.js";
if (!Math) {
  (_easycom_uni_icons + _easycom_uni_easyinput)();
}
const _sfc_main = {
  __name: "cola-u-chat",
  props: {
    isUseRead: {
      type: Boolean,
      default: false
    },
    dataKeys: {
      type: Object,
      default: () => {
        return {
          userId: "userId",
          msgId: "msgId",
          name: "name",
          message: "message",
          img: "img",
          avator: "avator",
          read: "read"
        };
      }
    },
    userId: {
      type: String || Number,
      default: ""
    },
    // 已废弃
    userIdName: {
      type: String
    },
    IDType: {
      type: String,
      default: "id"
    },
    msgName: {
      type: String,
      default: "message"
    },
    messageData: {
      type: Array,
      default: () => {
        return [];
      }
    },
    updateMsg: {
      type: Object,
      default: () => {
        return {};
      }
    },
    historyMsg: {
      type: [Object, Array],
      default: () => {
        return {};
      }
    },
    moreFunction: {
      type: Array,
      default: () => {
        return [{
          name: "照片",
          icon: "photo",
          img: ""
        }, {
          name: "拍摄",
          icon: "camera",
          img: ""
        }];
      }
    },
    iconSize: {
      type: String,
      default: "40"
    },
    isUseRefresh: {
      type: Boolean,
      default: false
    }
  },
  emits: ["refresh", "sendMsg", "moreClick"],
  setup(__props, { emit: __emit }) {
    const props = __props;
    const {
      updateMsg,
      historyMsg,
      messageData,
      dataKeys,
      IDType
    } = common_vendor.toRefs(props);
    const emit = __emit;
    const text = common_vendor.ref("");
    const chatViewId = common_vendor.ref("");
    const inputFocus = common_vendor.ref(false);
    const inputDisabled = common_vendor.ref(false);
    common_vendor.ref(false);
    const selfMsgData = common_vendor.ref([]);
    const moreIsShow = common_vendor.ref(false);
    const keybordMove = common_vendor.ref(false);
    const isFresh = common_vendor.ref(false);
    const freshing = common_vendor.ref(false);
    common_vendor.onMounted(() => {
      selfMsgData.value = messageData.value;
      setScroll();
    });
    common_vendor.watch(updateMsg.value, (val) => {
      selfMsgData.value.push(val.value);
      setScroll();
    }, {
      deep: true
    });
    common_vendor.watch(historyMsg.value, (val, oldVal) => {
      if (val === oldVal) {
        return;
      }
      setTimeout(() => {
        isFresh.value = false;
        freshing.value = false;
      }, 500);
      const valueType = Object.prototype.toString.call(val.value);
      if (valueType === "[object Object]") {
        selfMsgData.value.unshift(val.value);
      } else if (valueType === "[object Array]") {
        for (let num = 0; num < val.length; num++) {
          selfMsgData.value.unshift(val.value[num]);
        }
      }
    }, {
      immediate: true,
      deep: true
    });
    const scrollRefresh = (e) => {
      if (freshing.value)
        return;
      freshing.value = true;
      isFresh.value = true;
      emit("refresh");
    };
    const onRestore = () => {
      isFresh.value = "restore";
    };
    const changeIs = () => {
      moreIsShow.value = false;
      keybordMove.value = false;
    };
    const clickInput = () => {
      inputFocus.value = false;
    };
    const inputConfirm = () => {
      if (text.value === "") {
        return;
      }
      inputFocus.value = false;
      emit("sendMsg", text.value);
      text.value = "";
    };
    const setScroll = () => {
      let id = selfMsgData.value.length - 1;
      chatViewId.value = "id-" + id;
    };
    const imgTap = (img) => {
      common_vendor.index.previewImage({
        urls: [img],
        longPressActions: {
          itemList: ["发送给朋友", "保存图片", "收藏"],
          success: function(data) {
            common_vendor.index.__f__("log", "at uni_modules/cola-u-chat/components/cola-u-chat/cola-u-chat.vue:294", "选中了第" + (data.tapIndex + 1) + "个按钮,第" + (data.index + 1) + "张图片");
          },
          fail: function(err) {
            common_vendor.index.__f__("log", "at uni_modules/cola-u-chat/components/cola-u-chat/cola-u-chat.vue:297", err.errMsg);
          }
        }
      });
    };
    const moreButton = () => {
      common_vendor.index.hideKeyboard();
      keybordMove.value = true;
      moreIsShow.value = true;
    };
    const buttonClick = (index) => {
      emit("moreClick", index);
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(selfMsgData.value, (msgItem, msgIndex, i0) => {
          return common_vendor.e({
            a: msgItem[common_vendor.unref(dataKeys)["avator"]],
            b: msgItem[common_vendor.unref(dataKeys)["message"]] !== ""
          }, msgItem[common_vendor.unref(dataKeys)["message"]] !== "" ? {
            c: common_vendor.t(msgItem[common_vendor.unref(dataKeys)["message"]]),
            d: common_vendor.n(msgItem[common_vendor.unref(dataKeys)["userId"]] === __props.userId ? "self-body" : "other-body"),
            e: common_vendor.n(msgItem[common_vendor.unref(dataKeys)["userId"]] === __props.userId ? "msgBody-rg" : "msgBody-lf")
          } : {
            f: common_vendor.o(($event) => imgTap(msgItem[common_vendor.unref(dataKeys)["img"]]), msgIndex),
            g: msgItem[common_vendor.unref(dataKeys)["img"]]
          }, {}, {
            i: msgIndex,
            j: "id-" + msgIndex,
            k: msgItem[common_vendor.unref(dataKeys)["userId"]] === __props.userId ? "row-reverse" : "row"
          });
        }),
        b: __props.isUseRefresh,
        c: isFresh.value,
        d: common_vendor.o(onRestore),
        e: common_vendor.o(scrollRefresh),
        f: common_vendor.o(changeIs),
        g: chatViewId.value,
        h: common_vendor.p({
          type: "mic",
          size: "30"
        }),
        i: common_vendor.o(clickInput),
        j: common_vendor.o(inputConfirm),
        k: common_vendor.o(($event) => text.value = $event),
        l: common_vendor.p({
          focus: inputFocus.value,
          disabled: inputDisabled.value,
          placeholder: "",
          type: "text",
          modelValue: text.value
        }),
        m: common_vendor.o(moreButton),
        n: common_vendor.p({
          type: "plus",
          size: "30"
        }),
        o: common_vendor.o(inputConfirm),
        p: (keybordMove.value ? "400" : "0") + "rpx",
        q: common_vendor.f(__props.moreFunction, (item, index, i0) => {
          return common_vendor.e({
            a: item.img !== ""
          }, item.img !== "" ? {} : {
            b: "66d791ff-3-" + i0,
            c: common_vendor.p({
              size: __props.iconSize,
              type: item.icon
            })
          }, {
            d: common_vendor.t(item.name),
            e: common_vendor.o(($event) => buttonClick(index), index),
            f: index
          });
        }),
        r: (moreIsShow.value ? "400" : "0") + "rpx"
      };
    };
  }
};
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-66d791ff"]]);
wx.createComponent(Component);
//# sourceMappingURL=../../../../../.sourcemap/mp-weixin/uni_modules/cola-u-chat/components/cola-u-chat/cola-u-chat.js.map
