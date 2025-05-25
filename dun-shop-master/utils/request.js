import axios from "axios";
import {
	UniAdapter
} from "uniapp-axios-adapter";

const request = axios.create({
	baseURL: "http://www.dundunteam.com:8080",
	timeout: 10000,
	adapter: UniAdapter
})

request.interceptors.request.use((config) => {
	config.headers["token"] = "1111";

	return config;
})

request.interceptors.response.use((response) => {
	if (response.status === 200) {
		const {
			data
		} = response;
		return Promise.resolve(data);
	} else {
		return Promise.reject(response);
	}
})

export default request;