"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  __name: "editor",
  setup(__props) {
    const readOnly = common_vendor.ref(false);
    const formats = common_vendor.ref({});
    const editorCtx = common_vendor.ref();
    const html = common_vendor.ref();
    const instance = common_vendor.getCurrentInstance();
    const query = common_vendor.index.createSelectorQuery().in(instance.proxy);
    query.select("#editor").context((res) => {
      editorCtx.value = res.context;
    }).exec();
    const onEditorReady = () => {
    };
    const getContents = () => {
      editorCtx.value.getContents({
        success: (res) => {
          html.value = res.html;
          common_vendor.index.setStorageSync("html", html.value);
        }
      });
    };
    const undo = () => {
      editorCtx.value.undo();
    };
    const redo = () => {
      editorCtx.value.redo();
    };
    const format = (e) => {
      let {
        name,
        value
      } = e.target.dataset;
      if (!name)
        return;
      editorCtx.value.format(name, value);
    };
    const onStatusChange = (e) => {
      formats.value = e.detail;
    };
    const insertDivider = () => {
      editorCtx.value.insertDivider({
        success: function() {
          common_vendor.index.__f__("log", "at components/editor/editor.vue:118", "insert divider success");
        }
      });
    };
    const clear = () => {
      common_vendor.index.showModal({
        title: "清空编辑器",
        content: "确定清空编辑器全部内容？",
        success: (res) => {
          if (res.confirm) {
            editorCtx.value.clear({
              success: function(res2) {
                common_vendor.index.__f__("log", "at components/editor/editor.vue:130", "clear success");
              }
            });
          }
        }
      });
    };
    const removeFormat = () => {
      editorCtx.value.removeFormat();
    };
    const insertDate = () => {
      const date = /* @__PURE__ */ new Date();
      const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
      editorCtx.value.insertText({
        text: formatDate
      });
    };
    const insertImage = () => {
      common_vendor.index.chooseImage({
        count: 1,
        success: (res) => {
          editorCtx.value.insertImage({
            src: res.tempFilePaths[0],
            alt: "图像",
            success: function() {
              common_vendor.index.__f__("log", "at components/editor/editor.vue:155", "insert image success");
            }
          });
        }
      });
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.n(formats.value.bold ? "ql-active" : ""),
        b: common_vendor.n(formats.value.italic ? "ql-active" : ""),
        c: common_vendor.n(formats.value.underline ? "ql-active" : ""),
        d: common_vendor.n(formats.value.strike ? "ql-active" : ""),
        e: common_vendor.n(formats.value.align === "center" ? "ql-active" : ""),
        f: common_vendor.n(formats.value.align === "right" ? "ql-active" : ""),
        g: common_vendor.n(formats.value.align === "justify" ? "ql-active" : ""),
        h: common_vendor.o(removeFormat),
        i: common_vendor.n(formats.value.color === "#0000ff" ? "ql-active" : ""),
        j: common_vendor.n(formats.value.backgroundColor === "#00ff00" ? "ql-active" : ""),
        k: common_vendor.o(insertDate),
        l: common_vendor.n(formats.value.list === "ordered" ? "ql-active" : ""),
        m: common_vendor.n(formats.value.list === "bullet" ? "ql-active" : ""),
        n: common_vendor.o(undo),
        o: common_vendor.o(redo),
        p: common_vendor.o(insertDivider),
        q: common_vendor.o(insertImage),
        r: common_vendor.n(formats.value.header === 1 ? "ql-active" : ""),
        s: common_vendor.n(formats.value.script === "sub" ? "ql-active" : ""),
        t: common_vendor.n(formats.value.script === "super" ? "ql-active" : ""),
        v: common_vendor.o(clear),
        w: common_vendor.n(formats.value.direction === "rtl" ? "ql-active" : ""),
        x: common_vendor.o(format),
        y: common_vendor.o(onStatusChange),
        z: readOnly.value,
        A: common_vendor.o(onEditorReady),
        B: common_vendor.o(getContents)
      };
    };
  }
};
const Component = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-60314c1e"]]);
wx.createComponent(Component);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/components/editor/editor.js.map
