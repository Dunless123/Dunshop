import {
  __toESM,
  require_axios
} from "./chunk-COICTRUR.js";

// E:/myresource/project/HBuilderProjects/DunShop/node_modules/uniapp-axios-adapter/lib/index.esm.js
var import_axios = __toESM(require_axios());
var import_axios2 = __toESM(require_axios());
var getResponse = (res, config) => {
  const { statusCode, errMsg } = res;
  const response = {
    ...res,
    status: statusCode,
    statusText: errMsg,
    config,
    request: null
  };
  return response;
};
var uniAdapter = (config) => {
  if (!uni) {
    throw new Error("please use this in uni-app project!");
  }
  return new Promise((resolve, reject) => {
    const { baseURL, url, headers, data, params } = config;
    const uniConfig = {
      ...config,
      url: baseURL + url,
      header: headers
    };
    if (data || params) {
      try {
        uniConfig.data = JSON.parse(data || params);
      } catch (e) {
        uniConfig.data = data || params;
      }
    }
    uni.request({
      ...uniConfig,
      success(res) {
        const response = getResponse(res, config);
        resolve(response);
      },
      fail(res) {
        const response = getResponse(res, config);
        reject(response);
      }
    });
  });
};
var UniAdapter = uniAdapter;
import_axios.default.defaults.adapter = uniAdapter;
var export_default = import_axios2.default;
export {
  UniAdapter,
  export_default as default
};
//# sourceMappingURL=uniapp-axios-adapter.js.map
