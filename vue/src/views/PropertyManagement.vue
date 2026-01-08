<template>
  <div class="property-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><HomeFilled /></el-icon>
          <span>房产管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加房产记录
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" ref="searchForm" label-width="80px" class="search-form">
          <el-row :gutter="20">
            <el-col :span="6" :xs="24" :sm="12">
              <el-form-item label="居民姓名">
                <el-input 
                  v-model="searchForm.residentName" 
                  placeholder="请输入居民姓名" 
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6" :xs="24" :sm="12">
              <el-form-item label="房产类型">
                <el-select 
                  v-model="searchForm.propertyType" 
                  placeholder="请选择房产类型" 
                  clearable
                >
                  <el-option label="商品房" value="商品房"></el-option>
                  <el-option label="经济适用房" value="经济适用房"></el-option>
                  <el-option label="农村自建房" value="农村自建房"></el-option>
                  <el-option label="限价房" value="限价房"></el-option>
                  <el-option label="公租房" value="公租房"></el-option>
                  <el-option label="廉租房" value="廉租房"></el-option>
                  <el-option label="小产权房" value="小产权房"></el-option>
                  <el-option label="别墅" value="别墅"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="使用类型">
                <el-select 
                  v-model="searchForm.usageType" 
                  placeholder="请选择使用类型" 
                  clearable
                >
                  <el-option label="自住" value="自住"></el-option>
                  <el-option label="出租" value="出租"></el-option>
                  <el-option label="闲置" value="闲置"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="地址">
                <el-input 
                  v-model="searchForm.address" 
                  placeholder="请输入地址" 
                  clearable
                >
                  <template #prefix>
                    <el-icon><Location /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" class="search-buttons">
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 房产信息列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="propertyList"
          border
          stripe
          v-loading="loading"
          class="common-table"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: '600',
            textAlign: 'center'
          }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="residentName" label="居民姓名" width="100">
            <template #default="scope">
              <el-tag v-if="scope.row && scope.row.residentName" size="small">{{ scope.row.residentName }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="propertyType" label="房产类型" width="120">
            <template #default="scope">
              <el-tag 
                :type="getPropertyTypeType(scope.row.propertyType)" 
                v-if="scope.row && scope.row.propertyType" 
                size="small"
              >
                {{ scope.row.propertyType }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="地址" show-overflow-tooltip></el-table-column>
          <el-table-column prop="area" label="面积(㎡)" width="100" align="right">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.area !== null && scope.row.area !== undefined && !isNaN(scope.row.area)">
                {{ parseFloat(scope.row.area).toFixed(2) }} ㎡
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="value" label="估值(元)" width="120" align="right">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.value !== null && scope.row.value !== undefined && !isNaN(scope.row.value)">
                {{ parseFloat(scope.row.value).toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="propertyCertificate" label="产权证号" width="200">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.propertyCertificate">
                {{ scope.row.propertyCertificate }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="usageType" label="使用类型" width="100">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.usageType">
                <el-tag :type="getUsageTypeType(scope.row.usageType)">
                  {{ scope.row.usageType }}
                </el-tag>
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="acquisitionDate" label="取得日期" width="120">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.acquisitionDate">
                {{ scope.row.acquisitionDate }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="rooms" label="房间数" width="80">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.rooms">
                {{ scope.row.rooms }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <div class="operation-buttons" v-if="scope && scope.row">
                <el-button size="mini" @click="handleView(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button size="mini" @click="handleEdit(scope.row)" type="success" plain>
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button size="mini" type="danger" plain @click="handleDelete(scope.row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container common-pagination" v-if="propertyList.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageInfo.pageNum"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
          background>
        </el-pagination>
      </div>
      
      <!-- 删除错误对话框 -->
      <DeleteErrorDialog 
        v-model:visible="deleteErrorDialogVisible"
        :error-title="deleteErrorInfo.title"
        :error-message="deleteErrorInfo.message"
        :solution="deleteErrorInfo.solution"
        :related-data="deleteRelatedData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form 
        :model="propertyForm" 
        :rules="propertyRules" 
        ref="propertyForm" 
        label-width="120px"
        class="property-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input 
                v-model="propertyForm.residentName" 
                placeholder="请输入居民姓名"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input 
                v-model="propertyForm.idCard" 
                placeholder="请输入身份证号"
                @blur="handleIdCardInput"
                maxlength="18"
              >
                <template #prefix>
                  <el-icon><CreditCard /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房产类型" prop="propertyType">
              <el-select 
                v-model="propertyForm.propertyType" 
                placeholder="请选择房产类型" 
                style="width: 100%"
              >
                <el-option label="商品房" value="商品房"></el-option>
                <el-option label="经济适用房" value="经济适用房"></el-option>
                <el-option label="农村自建房" value="农村自建房"></el-option>
                <el-option label="限价房" value="限价房"></el-option>
                <el-option label="公租房" value="公租房"></el-option>
                <el-option label="廉租房" value="廉租房"></el-option>
                <el-option label="小产权房" value="小产权房"></el-option>
                <el-option label="别墅" value="别墅"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用类型" prop="usageType">
              <el-select 
                v-model="propertyForm.usageType" 
                placeholder="请选择使用类型" 
                style="width: 100%"
              >
                <el-option label="自住" value="自住"></el-option>
                <el-option label="出租" value="出租"></el-option>
                <el-option label="闲置" value="闲置"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input 
                v-model="propertyForm.address" 
                placeholder="请输入地址"
              >
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number 
                v-model="propertyForm.area" 
                :min="0" 
                :precision="2" 
                :step="10" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="估值(元)" prop="value">
              <el-input-number 
                v-model="propertyForm.value" 
                :min="0" 
                :precision="2" 
                :step="10000" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产权证号" prop="propertyCertificate">
              <el-input 
                v-model="propertyForm.propertyCertificate" 
                placeholder="请输入产权证号"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用类型" prop="usageType">
              <el-select 
                v-model="propertyForm.usageType" 
                placeholder="请选择使用类型" 
                style="width: 100%"
              >
                <el-option label="自住" value="自住"></el-option>
                <el-option label="出租" value="出租"></el-option>
                <el-option label="闲置" value="闲置"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="取得日期" prop="acquisitionDate">
              <el-input
                v-model="propertyForm.acquisitionDate"
                placeholder="请输入取得日期（格式：YYYY-MM-DD）"
                style="width: 100%"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间数" prop="rooms">
              <el-input-number 
                v-model="propertyForm.rooms" 
                :min="0" 
                :step="1" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="notes">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请输入备注"
                v-model="propertyForm.notes">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button type="primary" @click="handleSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      :title="'房产详情'"
      v-model="detailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentData" 
        label-width="120px"
        class="detail-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentData.id" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="居民ID">
              <el-input 
                v-model="currentData.residentId" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名">
              <el-input 
                v-model="currentData.residentName" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input 
                v-model="currentData.idCard" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><CreditCard /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房产类型">
              <el-input 
                v-model="currentData.propertyType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><HomeFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址">
              <el-input 
                v-model="currentData.address" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积(㎡)">
              <el-input 
                v-model="currentData.area" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="估值(元)">
              <el-input 
                v-model="currentData.value" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房产证号">
              <el-input 
                v-model="currentData.propertyCertificate" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="取得日期">
              <el-input 
                v-model="currentData.acquisitionDate" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用类型">
              <el-input 
                v-model="currentData.usageType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间数量">
              <el-input 
                v-model="currentData.rooms" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input 
                v-model="currentData.createTime" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Clock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="更新时间">
              <el-input 
                v-model="currentData.updateTime" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Clock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                type="textarea"
                :rows="3"
                v-model="currentData.notes"
                readonly
                style="background-color: #f5f7fa;"
              >
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { get, post, put, del } from '@/utils/request'
import { Plus, Delete, Search, Refresh, User, Location, HomeFilled, Edit, View, Document, Close, Check, CreditCard, Calendar, Clock } from '@element-plus/icons-vue'
import DeleteErrorDialog from '@/components/DeleteErrorDialog.vue'
import { getResidentByIdCard } from '@/api/data'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'

export default {
  name: 'PropertyManagement',
  components: {
    Plus,
    Delete,
    Search,
    Refresh,
    User,
    Location,
    HomeFilled,
    Edit,
    View,
    Document,
    Close,
    Check,
    CreditCard,
    Calendar,
    Clock,
    DeleteErrorDialog
  },
  data() {
    return {
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      propertyList: [],
      searchForm: {
        residentName: '',
        propertyType: '',
        usageType: '',
        address: ''
      },
      propertyForm: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        propertyType: '',
        address: '',
        area: null,
        value: null,
        propertyCertificate: '',
        acquisitionDate: '',
        usageType: '',
        rooms: null,
        notes: ''
      },
      // 删除错误对话框相关数据
      deleteErrorDialogVisible: false,
      deleteErrorInfo: '',
      deleteRelatedData: [],
      propertyRules: {
        residentName: [
          { required: true, message: '请输入居民姓名', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号码', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号码格式不正确', trigger: 'blur' }
        ],
        propertyType: [
          { required: true, message: '请选择房产类型', trigger: 'change' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ],
        area: [
          { required: true, message: '请输入面积', trigger: 'blur' }
        ],
        usageType: [
          { required: true, message: '请选择使用类型', trigger: 'change' }
        ],
        acquisitionDate: [
          { required: true, message: '请输入取得日期', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入取得日期'));
              } else {
                // 检查日期格式是否为YYYY-MM-DD
                const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
                if (!dateRegex.test(value)) {
                  callback(new Error('日期格式不正确，请使用YYYY-MM-DD格式'));
                } else {
                  // 检查日期是否有效
                  const selectedDate = new Date(value);
                  if (isNaN(selectedDate.getTime())) {
                    callback(new Error('日期格式不正确'));
                    return;
                  }
                  
                  const today = new Date();
                  today.setHours(0, 0, 0, 0);
                  
                  if (selectedDate > today) {
                    callback(new Error('取得日期不能晚于今天'));
                  } else {
                    callback();
                  }
                }
              }
            },
            trigger: 'blur'
          }
        ]
      },
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      }
    }
  },
  // 普通用户的组件会被Layout预加载，所以不在created中加载数据
  // 而是在组件被激活时（实际访问时）才加载数据
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.fetchData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'property') {
        this.fetchData()
      }
    }
    websocketService.registerMessageHandler(this.wsHandler)
  },
  activated() {
    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const role = userInfo.role || 'USER'

    // 只有在用户访问该组件时才加载数据
    this.fetchData()
  },
  beforeUnmount() {
    if (this.wsHandler) {
      websocketService.unregisterMessageHandler(this.wsHandler)
    }
  },
  methods: {
    fetchData() {
      this.loading = true
      const params = {
        page: this.pageInfo.pageNum,
        size: this.pageInfo.pageSize,
        ...this.searchForm
      }
      get('/property/list', params).then(response => {
        this.propertyList = response.data.data
        this.pageInfo.total = response.data.total
        this.loading = false
      }).catch(error => {
        console.error('获取房产信息失败:', error)
        this.$message.error('获取房产信息失败')
        this.loading = false
      })
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.fetchData()
    },
    handleReset() {
      // 显式重置搜索表单字段
      this.searchForm = {
        residentName: '',
        propertyType: '',
        usageType: '',
        address: ''
      }
      // 重置页码
      this.pageInfo.pageNum = 1
      // 重新获取数据，重置表格内容
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增房产信息'
      this.propertyForm = {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        propertyType: '',
        address: '',
        area: null,
        value: null,
        propertyCertificate: '',
        acquisitionDate: '',
        usageType: '',
        rooms: null,
        notes: ''
      }
      this.dialogVisible = true
      // 确保表单重置
      this.$nextTick(() => {
        if (this.$refs.propertyForm) {
          this.$refs.propertyForm.clearValidate()
        }
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑房产信息'
      this.propertyForm = { ...row }
      this.dialogVisible = true
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.propertyForm.idCard && this.propertyForm.idCard.trim()) {
        const idCard = this.propertyForm.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.propertyForm.residentName || !this.propertyForm.residentName.trim()) {
          this.$message.warning('请先填写居民姓名')
          return
        }
        
        // 根据身份证号验证居民是否存在
        try {
          this.loading = true
          // 调用获取居民信息的接口
          const residentResponse = await getResidentByIdCard(idCard)
          if (residentResponse && residentResponse.code === 200 && residentResponse.data) {
            const resident = residentResponse.data
            // 验证居民姓名是否匹配
            if (resident.name && resident.name.trim() === this.propertyForm.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.propertyForm.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.propertyForm.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.propertyForm.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    
    async handleSubmit() {
      this.$refs.propertyForm.validate(async (valid) => {
        if (valid) {
          const formData = { ...this.propertyForm }
          
          // 处理日期格式：确保日期为YYYY-MM-DD格式
          const formatDate = (date) => {
            if (!date) return null
            // 如果是Date对象，转换为YYYY-MM-DD格式字符串
            if (date instanceof Date) {
              return date.toISOString().split('T')[0]
            }
            // 如果是字符串，确保格式正确（YYYY-MM-DD）
            else if (typeof date === 'string') {
              // 验证日期格式，如果不符合YYYY-MM-DD格式，尝试转换
              const dateRegex = /^\d{4}-\d{2}-\d{2}$/
              if (!dateRegex.test(date)) {
                const dateObj = new Date(date)
                if (!isNaN(dateObj.getTime())) {
                  return dateObj.toISOString().split('T')[0]
                }
                return null
              }
              return date
            }
            return null
          }
          
          // 格式化取得日期
          formData.acquisitionDate = formatDate(formData.acquisitionDate)
          
          // 验证居民是否存在且匹配
          if (!formData.id && formData.idCard) {
            try {
              // 根据身份证号获取居民ID
              console.log('正在根据身份证号获取居民ID:', formData.idCard)
              const residentResponse = await getResidentByIdCard(formData.idCard)
              if (residentResponse && residentResponse.code === 200 && residentResponse.data) {
                const resident = residentResponse.data
                if (resident.id) {
                  // 验证居民姓名是否匹配
                  if (resident.name && resident.name.trim() === formData.residentName.trim()) {
                    formData.residentId = resident.id
                    console.log('成功获取居民ID:', resident.id)
                  } else {
                    throw new Error('居民姓名与身份证号不匹配，请检查输入信息')
                  }
                } else {
                  throw new Error('未找到对应的居民信息，请检查身份证号是否正确')
                }
              } else {
                throw new Error('获取居民信息失败: ' + (residentResponse ? residentResponse.message : '未知错误'))
              }
            } catch (residentError) {
              console.error('获取居民信息失败:', residentError)
              // 使用弹窗提醒用户未找到该居民
              this.$alert('未找到身份证号为 "' + formData.idCard + '" 的居民信息，或居民姓名与身份证号不匹配。请检查输入信息是否正确或先在居民管理中添加该居民。', '居民信息验证失败', {
                confirmButtonText: '确定',
                type: 'warning',
                center: true
              })
              this.loading = false
              return
            }
          }
          
          if (formData.id) {
            // 编辑
            put('/property/update', formData).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('修改房产信息失败:', error)
              this.$message.error('修改房产信息失败')
            })
          } else {
            // 新增
            post('/property/add', formData).then(response => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('新增房产信息失败:', error)
              this.$message.error('新增房产信息失败')
            })
          }
        }
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('此操作将永久删除该房产信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        const response = await del(`/property/delete/${row.id}`)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        console.error('删除房产信息失败:', error)
        
        // 检查是否为关联数据错误
        if (error.response && error.response.status === 409) {
          // 显示删除错误对话框
          this.deleteErrorInfo = {
            title: '删除失败',
            message: (error.response.data && error.response.data.message) || '该房产信息存在关联数据，无法删除',
            solution: '请先检查并处理相关的关联数据，然后再尝试删除房产信息。'
          }
          
          // 设置关联数据（如果有的话）
          this.deleteRelatedData = (error.response.data && error.response.data.relatedData) || []
          
          // 显示错误对话框
          this.deleteErrorDialogVisible = true
        } else {
          this.$message.error('删除房产信息失败')
        }
      }
    },
    handleSizeChange(val) {
      this.pageInfo.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pageInfo.pageNum = val
      this.fetchData()
    },
    handleDialogClose() {
      this.$refs.propertyForm.resetFields()
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'property', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    getPropertyTypeType(type) {
      const typeMap = {
        '商品房': 'primary',
        '经济适用房': 'success',
        '农村自建房': 'warning',
        '限价房': 'info',
        '公租房': 'danger',
        '廉租房': 'purple',
        '小产权房': 'cyan',
        '别墅': 'orange'
      }
      return typeMap[type] || 'info'
    },
    getUsageTypeType(type) {
      const typeMap = {
        '自住': 'success',
        '出租': 'warning',
        '闲置': 'info'
      }
      return typeMap[type] || 'info'
    }
  }
}
</script>

<style scoped>
.property-management {
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 84px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.management-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.header-title .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.search-bar {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin: 0 0 20px 0;
}

.search-form .el-row {
  margin-bottom: 10px;
}

.search-form .el-row:last-child {
  margin-bottom: 0;
}

.search-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end;
}

.table-container {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.property-table {
  flex: 1;
  width: 100%;
}

.property-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.property-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.property-table :deep(.el-table__row) {
  height: 48px;
}

.property-table :deep(.el-table__cell) {
  padding: 8px 0;
  vertical-align: middle;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  width: 100%;
}

.operation-buttons .el-button {
  margin: 0;
  padding: 6px 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  width: 80px;
  justify-content: center;
}

.pagination-container {
  padding: 20px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #ebeef5;
  min-height: 60px;
  align-items: center;
}

.pagination-container:empty {
  display: none;
}

.property-form {
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.header-buttons .el-button {
  display: flex;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar :deep(.el-form) {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar :deep(.el-form-item) {
    width: 100%;
  }

  .search-buttons {
    justify-content: flex-start;
  }

  .property-table {
    font-size: 12px;
  }

  .table-container {
    padding: 10px;
  }

  .operation-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .operation-buttons .el-button {
    width: 100%;
    justify-content: center;
  }
}
</style>