<template>
  <div id="bgi">
    <el-row>
      <div id="head">
        <!-- 酒店名字 -->
        <span style="margin-left: 2vw; font-size: 27px; font-weight: 600; color: #fff">SUSTech Regency</span>
        <!-- 区域选择 -->
        <div style="width: 60vw; height: 100%; background-color: " id="area">
          <el-row justify="space-evenly">
            <el-col :span="4">
              <el-select v-model="province" placeholder="省">
                <el-option v-for="province in provinces" :key="province" :value="province"
                  @click="changeCity(province)" />
              </el-select>
            </el-col>

            <el-col :span="4">
              <el-select v-model="city" placeholder="市">
                <el-option v-for="city in cities" :key="city" :value="city" @click="changeRegion(province, city)" />
              </el-select>
            </el-col>

            <el-col :span="4">
              <el-select v-model="region" placeholder="区">
                <el-option v-for="region in regions" :key="region" :value="region" />
              </el-select>
            </el-col>

            <el-col :span="4">
              <el-input v-model="hotelName" placeholder="酒店名称">
              </el-input>
            </el-col>

            <el-col :span="4">
              <el-button type="primary" style="width: 100px; margin-left: 10px" :icon="Search" @click="search"
                :disabled="listLoading"> 查询 </el-button>
            </el-col>
          </el-row>
        </div>
        <!-- 用户头像 -->
        <div style="height: 100%; align-self: flex-end; display: inline-flex; align-items: center; margin-right: 2rem">
          <UserIcon />
        </div>
      </div>
    </el-row>

    <el-row>
      <div id="main">
        <el-scrollbar>
          <el-row style="height: 100%" justify="space-evenly">
            <el-col :span="5" v-for="hotelInfo in hotelInfos">
              <el-card class="box-card" shadow="hover" style="border-radius: 10px; margin-top: 10px; height: 33vh">
                <router-link :to="'/hotel/' + hotelInfo.id + '/introduction'" target="_blank">
                  <div>
                    <el-image :src="hotelInfo.coverUrl" style="width: 100%; height: 20vh" fit="contain" />
                  </div>
                  <el-row>
                    <el-col :span="12" :offset="0">
                      <div class="name" s>{{ hotelInfo.name }}</div>
                    </el-col>
                    <el-col :span="12" :offset="0">
                      <div class="rate">
                        <el-rate v-model="hotelInfo.stars" :colors="colors" disabled text-color="#ff9900" />
                      </div>
                    </el-col>
                    <el-col :span="8" :offset="0" class="comment">
                      <div class="comment">
                        <div>共{{ hotelInfo.commentNum }}条评论</div>
                      </div>
                    </el-col>
                    <el-col :span="7" :offset="1" class="comment">
                      <div class="comment">
                        <div>共{{ hotelInfo.likes_num }}人收藏</div>
                      </div>
                    </el-col>
                    <el-col :span="8" :offset="0" class="price">
                      ¥<span class="">{{ hotelInfo.minPrice }}</span>起
                    </el-col>
                  </el-row>
                </router-link>
              </el-card>
            </el-col>
          </el-row>
        </el-scrollbar>
      </div>
    </el-row>

    <el-row justify="center">
      <div id="pages">
        <el-pagination v-model:currentPage="pageNum" v-model:page-size="pageSize" :page-sizes="[5, 10, 15, 20]"
          :small="small" :disabled="disabled" :background="background" layout="total, sizes, prev, pager, next, jumper"
          :total="totalNum" @size-change="handleSizeChange" @current-change="handleCurrentChange"
          style="margin-top: 15px" />
      </div>
    </el-row>

    <el-row>
      <div id="foot">
        <p style="color: #fff">
          <span style="margin-right: 15px;">广东省深圳市南山区学苑大道1088号 &nbsp;</span>
          <span style="margin-right: 15px;">电话: +86-755-88010888 &nbsp;</span>
          <span>邮编: 518055 </span>
        </p>
        <p style="color: #fff">© 2022 All Rights Reserved.</p>
      </div>
    </el-row>
  </div>
</template>

<style scoped>
#pages {
  height: 7vh;
}

.name {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  font-size: 20px;
  font-weight: 600;
}

.rate {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-end;
}

.comment {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  font-size: 14px;
  color: rgb(66, 66, 66);
}

.price {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  text-align: center;
  align-items: center;
  font-size: 17px;
  color: #2a598a;
}

.null {
  margin-left: 10px;
  margin-right: 10px;
  height: auto;
}

.info {
  margin-left: 1vw;
  margin-right: 1vw;
  color: rgb(205, 204, 204);
}

#head {
  width: 100%;
  height: 8vh;
  margin-bottom: 1vh;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  background-color: #2a598a;
}

#area {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#bgi {
  background-image: url('https://withpinbox.com/static/media/bg.aab24a9d.png');
}

#main {
  width: 100%;
  height: 74vh;
}

#foot {
  width: 100%;
  height: 9vh;
  margin-top: 1vh;
  background-color: #2a598a;

  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;

  font-weight: 350;
  font: #fff;
}
</style>

<script setup lang="ts">
import UserIcon from '../../components/UserIcon.vue'
import request from '../../utils/request'
import { Search } from '@element-plus/icons-vue'
import { HotelInfo } from '../../type/type';

interface Province {
  id: number
  name: string
  abbreviation: string
}

interface City {
  id: number
  name: string
  provinceId: number
  isProvincialCapital: boolean
}

interface Region {
  id: number
  name: string
  cityId: number
}

interface IPage {
  records: HotelInfo[]
  total: string
}

const colors = $ref(['#99A9BF', '#F7BA2A', '#FF9900'])

let pages = $ref<IPage>()
let hotelInfos = $ref<HotelInfo[]>()

let province = $ref('')
let city = $ref('')
let region = $ref('')
let hotelName = $ref('')

let provinces = $ref<string[]>([])
let cities = $ref<string[]>([])
let regions = $ref<string[]>([])
let totalNum = $ref(0)

let listLoading = $ref(false)

const pageNum = $ref(1)
const pageSize = $ref(5)
const small = $ref(false)
const background = $ref(false)
const disabled = $ref(false)

const load = () => {
  listLoading = true
  if (province === '全部') {
    request.get(`/public/get-hotels-by-location?pageNum=${pageNum}&pageSize=${pageSize}`).then(res => {
      console.log(res.data.data)
      pages = res.data.data
      hotelInfos = pages.records
      totalNum = parseInt(pages.total)
    }).finally(() => {
      listLoading = false
    })
  } else {
    request.get(`/public/get-hotels-by-location?CityName=${city}&ProvinceName=${province}&RegionName=${region}&HotelName=${hotelName}&pageNum=${pageNum}&pageSize=${pageSize}`)
      .then(res => {
        console.log(res.data.data)
        pages = res.data.data
        hotelInfos = pages.records
        totalNum = parseInt(pages.total)
      })
      .finally(() => {
        listLoading = false
      })
  }
}

load()

const search = () => {
  load()
}

request.get('/public/province/all').then((res) => {
  const provinceList = res.data.data as City[]
  provinces = provinceList.map((p) => p.name)
  provinces.unshift('全部')
})

const changeCity = (curProvince: string) => {
  console.log('myprovince: ' + curProvince)
  if (curProvince === '全部') {
    province = '全部'
    city = ''
    region = ''
  } else {
    request.get(`/public/city/all?province=${curProvince}`).then(res => {
      const cityList = res.data.data as Province[]
      cities = cityList.map(c => c.name)
      city = ''
      region = ''
      console.log(cities)
    })
  }
}

const changeRegion = (province: string, city: string) => {
  request.get(`/public/region/all?province=${province}&&city=${city}`).then((res) => {
    const regionList = res.data.data as Region[]
    regions = regionList.map((c) => c.name)
    region = ''
    console.log(cities)
  })
}

const handleSizeChange = (val: number) => {
  load()
  console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
  load()
  console.log(`current page: ${val}`)
}
</script>
