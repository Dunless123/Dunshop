import request from "../utils/request";

export const getSessionIdLogin = async (data = {}) => {
	return request({
		url: '/user/getSessionId',
		method: "get",
		data
	})
}