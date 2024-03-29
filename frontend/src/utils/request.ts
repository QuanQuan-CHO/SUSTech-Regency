import axios from "axios";
import { ElNotification } from "element-plus";
import { h } from "vue";

const request = axios.create({
  baseURL: "https://quanquancho.com:8080"
  // baseURL: "http://127.0.0.1:8080"
});

request.interceptors.request.use(function (config) {
  const token = localStorage.token ? JSON.parse(localStorage.token) : '';
  if (config && config.headers) {
    if (token) {
      config.headers['token'] = token
    }
  }
  return config;
}, function (error) {
  return Promise.reject(error);
})

request.interceptors.response.use(function (response) {
  return response;
}, function (error) {
  return Promise.reject(error);
})

export default request;