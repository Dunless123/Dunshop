"use strict";
const common_vendor = require("../common/vendor.js");
const request = common_vendor.axios.create({
  baseURL: "http://www.dundunteam.com:8080",
  timeout: 1e4,
  adapter: common_vendor.UniAdapter
});
request.interceptors.request.use((config) => {
  config.headers["token"] = "1111";
  return config;
});
request.interceptors.response.use((response) => {
  if (response.status === 200) {
    const {
      data
    } = response;
    return Promise.resolve(data);
  } else {
    return Promise.reject(response);
  }
});
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
