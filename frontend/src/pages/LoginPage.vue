<template>
  <div id="bigg">
    <div id="main">
      <div id="left">
        <!-- LOGO区域 -->
        <div id="logo">
          <div id="title">
            <a href="/">
              <div id="text">SUSTech Regency</div>
            </a>
          </div>
          <div id="hotel">
            <div id="logoimg"></div>
          </div>
        </div>
        <div class="input_login">
          <div id="username">用户名/邮箱</div>
          <el-input v-model="username" placeholder="请输入用户名/邮箱" :prefix-icon="User" clearable id="input_username" />
          <div id="pwd">密码</div>
          <el-input v-model="pwd" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password id="input_pwd" @keyup.enter="Login" />

          <div id="btn_area">
            <el-button type="primary" round id="btn_login" @click="Login">登录账号</el-button>
            <el-button type="primary" round id="btn_sign" @click="SignUp">注册账号</el-button>
            <el-button type="primary" round id="btn_get_pwd_back" @click="ChangePwd">忘记密码</el-button>
          </div>
        </div>
      </div>
      <div id="right">
        <div id="bgi"></div>
        <div id="content">
          <div id="welcome">Welcome to SUSTech Regency</div>
          <div id="slogn">Feel better than yesterday</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* body {
  background-image: url("https://withpinbox.com/static/media/bg.aab24a9d.png");
} */
#bigg {
  background-image: url('https://withpinbox.com/static/media/bg.aab24a9d.png');
  width: auto;
  height: 100vh;
}

#main {
  position: absolute;
  width: 80%;
  height: 80%;
  top: 10%;
  left: 10%;
  display: flex;
  align-items: flex-start;
  padding: 0px;
  border-radius: 30px;
  background-color: rgb(255, 255, 255);
  filter: drop-shadow(10px 10px 4px rgba(0, 0, 0, 0.1));
}

/* 在850px以下直接用left */
@media screen and (max-width: 850px) {
  #btn_sign,
  #btn_get_pwd_back {
    margin: 0;
  }

  #right {
    display: none;
  }

  #left {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    /* 主轴居中 */
    justify-content: center;
    /* 交叉轴居中 */
    align-items: center;
    border-radius: 30px;
  }

  #username,
  #pwd {
    color: rgb(118, 171, 249);
  }

  #username {
    margin-top: 1rem;
  }

  #pwd {
    margin-top: 0.7rem;
  }

  .input_login {
    width: 80%;
    height: 60%;
    display: table;
    margin: 0 auto;
  }

  #logo {
    width: 100%;
    height: 20%;
    display: flex;
    flex-direction: row;
    padding: 0;
    margin: 0;
    margin-top: 0;
    gap: 0;
    justify-content: center;
    align-content: center;
    /* background-color: red; */
  }

  #title {
    order: 0;
    width: 55%;
    text-align: center;
    line-height: 50px;
    /* background-color: red; */
  }

  #hotel {
    order: 1;
    width: 100px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  #text {
    margin: 0;
    position: relative;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  #text {
    font-size: 2.1rem;
  }

  #logoimg {
    width: 100px;
    height: 100px;
    background: url(../assets/hotel.png) 0% 0% / cover no-repeat;
    opacity: 0.75;
  }

  #btn_area {
    margin: 1vh;
    margin-top: 2vh;
    padding: 1.5vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2vh;
  }

  #btn_get_pwd_back,
  #btn_sign,
  #btn_login {
    min-width: 25vw;
    max-width: 45vw;
  }
}

/* 在850px以上，用left+right */
@media screen and (min-width: 850px) {
  #right {
    position: relative;
    width: 60%;
    height: 100%;
    display: block;
    border-radius: 0px 30px 30px 0px;
  }

  #bgi {
    position: absolute;
    width: 100%;
    height: 100%;
    display: block;
    border-radius: 0px 30px 30px 0px;
    background: url(../assets/background-img.jpg) 0% 0% / cover no-repeat;
    opacity: 0.75;
  }

  #content {
    position: absolute;
    display: block;
    width: 100%;
  }

  #welcome {
    font-family: 'Inter';
    font-style: normal;
    font-weight: 400;
    font-size: 3rem;
    line-height: 3rem;
    color: #fff;
    margin-top: 15%;
    margin-left: 5%;
    text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.3);
    opacity: 1;
  }

  #slogn {
    font-family: 'Inter';
    font-style: normal;
    font-weight: 200;
    font-size: 1.7rem;
    line-height: 1.7rem;
    color: #fff;
    margin-top: 2rem;
    margin-left: 5%;
    text-shadow: 4px 4px 4px rgba(0, 0, 0, 0.3);
    opacity: 1;
  }

  #left {
    width: 40%;
    height: 100%;
    display: block;
    border-radius: 30px 0px 0px 30px;
  }

  #err_msg {
    color: red;
  }

  #username,
  #pwd {
    color: rgb(118, 171, 249);
  }

  #username {
    margin-top: 2rem;
  }

  #pwd {
    margin-top: 0.7rem;
  }

  .input_login {
    width: 67%;
    display: table;
    margin: 0 auto;
  }

  #logo {
    width: 100%;
    height: 15%;
    display: flex;
    flex-direction: row;
    padding: 0;
    margin: 0;
    margin-top: 19%;
    gap: 0;
    justify-content: center;
    align-content: center;
    /* background-color: red; */
  }

  #title {
    order: 0;
    width: 55%;
    text-align: center;
    line-height: 50px;
    /* background-color: red; */
  }

  #hotel {
    order: 1;
    width: 100px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    /* background-color: red; */
  }

  #text {
    font-size: 2.1rem;
    margin: 0;
    position: relative;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  #logoimg {
    width: 80px;
    height: 80px;
    background: url(../assets/hotel.png) 0% 0% / cover no-repeat;
    opacity: 0.75;
  }

  #btn_area {
    margin: 0.5vw;
    padding: 1.5vw;
    display: flex;
    flex-direction: row;
    gap: 1vw;
  }

  #btn_get_pwd_back,
  #btn_sign,
  #btn_login {
    min-width: 5vw;
    max-width: 7vw;
  }
}
</style>

<script lang="ts" setup>
import { ElNotification } from 'element-plus'
import { ref, h } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request'
import router from '../router'

interface User {
  id: number
  name: string
  email: string
  headshotUrl: string
  isConsumer: boolean
  isMerchant: boolean
  merchantHotelIds: number[]
}

const username = ref('')
const pwd = ref('')
const dialog_visible = ref(false)
const show_err_msg = ref(false)
const Login = () => {
  request
    .post(`/user/login`, {
      password: pwd.value,
      usernameOrEmail: username.value
    })
    .then((res) => {
      if (res.data.code === 400) {
        ElNotification({
          title: 'Failed',
          message: h('i', { style: 'color: red' }, res.data.message)
        })
      } else {
        const user: User = {
          id: res.data.data.id,
          name: res.data.data.name,
          email: res.data.data.email,
          headshotUrl: res.data.data.headshotUrl,
          isConsumer: res.data.data.isConsumer,
          isMerchant: res.data.data.isMerchant,
          merchantHotelIds: res.data.data.merchantHotelIds
        }
        console.log('user', user)
        localStorage.setItem('token', JSON.stringify(res.data.data.token))
        localStorage.setItem('user', JSON.stringify(user))
        ElNotification({
          title: 'Success',
          message: h('i', { style: 'color: teal' }, 'Login success'),
          position: 'top-left'
        })
        router.push('/')
      }
    })
}
const SignUp = () => {
  router.push('/signup')
}
const ChangePwd = () => {
  console.log('change page to change pwd')
  router.push('/changepassword')
}
</script>
