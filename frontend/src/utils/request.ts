import axios from "axios";

const request = axios.create({
  baseURL: "https://quanquancho.com:8080"
});

request.interceptors.request.use(function (config) {
  const token = localStorage.getItem("token");
  if (config && config.headers) {
    if (token) {
      config.headers['Authorization'] = token;
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