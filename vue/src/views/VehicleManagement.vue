<template>
  <div class="vehicle-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><Van /></el-icon>
          <span>车辆管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加车辆记录
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
              <el-form-item label="车牌号">
                <el-input 
                  v-model="searchForm.licensePlate" 
                  placeholder="请输入车牌号" 
                  clearable
                >
                  <template #prefix>
                    <el-icon><Document /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="车辆类型">
                <el-select 
                  v-model="searchForm.vehicleType" 
                  placeholder="请选择车辆类型" 
                  clearable
                >
                  <el-option label="轿车" value="轿车"></el-option>
                  <el-option label="SUV" value="SUV"></el-option>
                  <el-option label="货车" value="货车"></el-option>
                  <el-option label="客车" value="客车"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="品牌">
                <el-input 
                  v-model="searchForm.brand" 
                  placeholder="请输入品牌" 
                  clearable
                >
                  <template #prefix>
                    <el-icon><Collection /></el-icon>
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

      <!-- 车辆信息列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="vehicleList"
          border
          stripe
          v-loading="loading"
          class="common-table vehicle-table"
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
          <el-table-column prop="licensePlate" label="车牌号" width="120"></el-table-column>
          <el-table-column prop="vehicleType" label="车辆类型" width="100"></el-table-column>
          <el-table-column prop="brand" label="品牌" width="100"></el-table-column>
          <el-table-column prop="model" label="型号" width="120"></el-table-column>
          <el-table-column prop="color" label="颜色" width="80">
            <template #default="scope">
              <el-tag 
                :type="getColorType(scope.row.color)" 
                v-if="scope.row && scope.row.color" 
                size="small"
              >
                {{ scope.row.color }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="purchaseDate" label="购买日期" width="120"></el-table-column>
          <el-table-column prop="purchasePrice" label="购买价格(元)" width="120" align="right">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.purchasePrice !== null && scope.row.purchasePrice !== undefined && !isNaN(scope.row.purchasePrice)">
                {{ parseFloat(scope.row.purchasePrice).toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="insuranceExpiryDate" label="保险到期日期" width="120">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.insuranceExpiryDate">
                {{ scope.row.insuranceExpiryDate }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <div class="operation-buttons" v-if="scope && scope.row">
                <el-button size="small" @click="handleView(scope.row)" type="primary" plain>
                    <el-icon><View /></el-icon>
                    查看
                  </el-button>
                  <el-button size="small" @click="handleEdit(scope.row)" type="success" plain>
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button size="small" type="danger" plain @click="handleDelete(scope.row)">
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
      <div class="pagination-container common-pagination">
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
        :model="vehicleForm" 
        :rules="vehicleRules" 
        ref="vehicleForm" 
        label-width="120px"
        class="vehicle-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input 
                v-model="vehicleForm.residentName" 
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
                v-model="vehicleForm.idCard" 
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
            <el-form-item label="车牌号" prop="licensePlate">
              <el-input 
                v-model="vehicleForm.licensePlate" 
                placeholder="请输入车牌号"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车辆类型" prop="vehicleType">
              <el-select 
                v-model="vehicleForm.vehicleType" 
                placeholder="请选择车辆类型" 
                style="width: 100%"
              >
                <el-option label="轿车" value="轿车"></el-option>
                <el-option label="SUV" value="SUV"></el-option>
                <el-option label="货车" value="货车"></el-option>
                <el-option label="客车" value="客车"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input 
                v-model="vehicleForm.brand" 
                placeholder="请输入品牌"
              >
                <template #prefix>
                  <el-icon><Collection /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input 
                v-model="vehicleForm.model" 
                placeholder="请输入型号"
              >
                <template #prefix>
                  <el-icon><Collection /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="颜色" prop="color">
              <el-input 
                v-model="vehicleForm.color" 
                placeholder="请输入颜色"
              >
                <template #prefix>
                  <el-icon><Collection /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车架号" prop="chassisNumber">
              <el-input 
                v-model="vehicleForm.chassisNumber" 
                placeholder="请输入车架号"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发动机号" prop="engineNumber">
              <el-input 
                v-model="vehicleForm.engineNumber" 
                placeholder="请输入发动机号"
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
            <el-form-item label="购买日期" prop="purchaseDate">
              <el-input
                v-model="vehicleForm.purchaseDate"
                placeholder="请输入购买日期（格式：YYYY-MM-DD）"
                style="width: 100%"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购买价格(元)" prop="purchasePrice">
              <el-input-number 
                v-model="vehicleForm.purchasePrice" 
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
            <el-form-item label="保险到期日期" prop="insuranceExpiryDate">
              <el-input
                v-model="vehicleForm.insuranceExpiryDate"
                placeholder="请输入保险到期日期（格式：YYYY-MM-DD）"
                style="width: 100%"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年检日期" prop="annualInspectionDate">
              <el-input
                v-model="vehicleForm.annualInspectionDate"
                placeholder="请输入年检日期（格式：YYYY-MM-DD）"
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
          <el-col :span="24">
            <el-form-item label="备注" prop="notes">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请输入备注"
                v-model="vehicleForm.notes">
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
      :title="'车辆详情'"
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
            <el-form-item label="车牌号">
              <el-input 
                v-model="currentData.licensePlate" 
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
            <el-form-item label="车辆类型">
              <el-input 
                v-model="currentData.vehicleType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Van /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input 
                v-model="currentData.brand" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Collection /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号">
              <el-input 
                v-model="currentData.model" 
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
            <el-form-item label="颜色">
              <el-input 
                v-model="currentData.color" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Collection /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发动机号">
              <el-input 
                v-model="currentData.engineNumber" 
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
            <el-form-item label="底盘号">
              <el-input 
                v-model="currentData.chassisNumber" 
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
            <el-form-item label="购买日期">
              <el-input 
                v-model="currentData.purchaseDate" 
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
            <el-form-item label="购买价格(元)">
              <el-input 
                v-model="currentData.purchasePrice" 
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
            <el-form-item label="保险到期日期">
              <el-input 
                v-model="currentData.insuranceExpiryDate" 
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
            <el-form-item label="年检日期">
              <el-input 
                v-model="currentData.annualInspectionDate" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
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
        </el-row>
        <el-row :gutter="20">
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

    <!-- 删除错误对话框 -->
    <DeleteErrorDialog
      v-model="deleteErrorDialogVisible"
      :error-info="deleteErrorInfo"
      :related-data="deleteRelatedData"
    />
  </div>
</template>

<script>
import { getVehicleData, searchVehicles, addVehicle, updateVehicle, deleteVehicle, getResidentByIdCard } from '@/api/data'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'
import { Plus, Delete, Search, Refresh, User, Van, Edit, View, Document, Collection, Close, Check, CreditCard, Calendar, Clock } from '@element-plus/icons-vue'

export default {
  name: 'VehicleManagement',
  components: {
    Plus,
    Delete,
    Search,
    Refresh,
    User,
    Van,
    Edit,
    View,
    Document,
    Collection,
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
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: '',
      deleteRelatedData: [],
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      vehicleList: [],
      searchForm: {
        residentName: '',
        licensePlate: '',
        vehicleType: '',
        brand: ''
      },
      vehicleForm: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        licensePlate: '',
        vehicleType: '',
        brand: '',
        model: '',
        color: '',
        engineNumber: '',
        chassisNumber: '',
        purchaseDate: '',
        purchasePrice: null,
        insuranceExpiryDate: '',
        annualInspectionDate: '',
        notes: ''
      },
      vehicleRules: {
        residentName: [
          { required: true, message: '请输入居民姓名', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号码', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号码格式不正确', trigger: 'blur' }
        ],
        licensePlate: [
          { required: true, message: '请输入车牌号', trigger: 'blur' }
        ],
        vehicleType: [
          { required: true, message: '请选择车辆类型', trigger: 'change' }
        ],
        brand: [
          { required: true, message: '请输入品牌', trigger: 'blur' }
        ],
        purchaseDate: [
          { required: true, message: '请输入购买日期', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入购买日期'));
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
                    callback(new Error('购买日期不能晚于今天'));
                  } else {
                    callback();
                  }
                }
              }
            },
            trigger: 'blur'
          }
        ],
        insuranceExpiryDate: [
          {
            validator: (rule, value, callback) => {
              if (value) {
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
                  callback();
                }
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        annualInspectionDate: [
          {
            validator: (rule, value, callback) => {
              if (value) {
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
                  callback();
                }
              } else {
                callback();
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
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.fetchData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'vehicle') {
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
      console.log('[VehicleManagement] fetchData - 请求参数:', params)
      console.log('[VehicleManagement] fetchData - localStorage token:', localStorage.getItem('token') ? '存在' : '不存在')
      
      getVehicleData(params).then(response => {
        console.log('[VehicleManagement] fetchData - 响应数据:', response)
        this.vehicleList = response.data.data
        this.pageInfo.total = response.data.total
        console.log('[VehicleManagement] fetchData - 加载成功，数据条数:', this.vehicleList.length, '总数:', this.pageInfo.total)
        this.loading = false
      }).catch(error => {
        console.error('[VehicleManagement] fetchData - 获取车辆信息失败:', error)
        this.$message.error('获取车辆信息失败:' + (error.message || '未知错误'))
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
        licensePlate: '',
        vehicleType: '',
        brand: ''
      }
      // 重置页码
      this.pageInfo.pageNum = 1
      // 重新获取数据，重置表格内容
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增车辆信息'
      this.vehicleForm = {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        licensePlate: '',
        vehicleType: '',
        brand: '',
        model: '',
        color: '',
        engineNumber: '',
        chassisNumber: '',
        purchaseDate: '',
        purchasePrice: null,
        insuranceExpiryDate: '',
        annualInspectionDate: '',
        notes: ''
      }
      this.dialogVisible = true
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.vehicleForm.idCard && this.vehicleForm.idCard.trim()) {
        const idCard = this.vehicleForm.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.vehicleForm.residentName || !this.vehicleForm.residentName.trim()) {
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
            if (resident.name && resident.name.trim() === this.vehicleForm.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.vehicleForm.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.vehicleForm.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.vehicleForm.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    handleEdit(row) {
      this.dialogTitle = '编辑车辆信息'
      this.vehicleForm = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      this.$refs.vehicleForm.validate(async (valid) => {
        if (valid) {
          const formData = { ...this.vehicleForm }
          
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
          
          // 格式化日期字段
          formData.purchaseDate = formatDate(formData.purchaseDate)
          formData.insuranceExpiryDate = formatDate(formData.insuranceExpiryDate)
          formData.annualInspectionDate = formatDate(formData.annualInspectionDate)
          
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
          
          console.log('提交数据:', formData)
          if (formData.id) {
            // 编辑
            updateVehicle(formData).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('修改车辆信息失败:', error)
              console.error('错误详情:', error.response)
              this.$message.error('修改车辆信息失败: ' + (error.message || '未知错误'))
            })
          } else {
            // 新增
            addVehicle(formData).then(response => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('新增车辆信息失败:', error)
              console.error('错误详情:', error.response)
              this.$message.error('新增车辆信息失败: ' + (error.message || '未知错误'))
            })
          }
        }
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('此操作将永久删除该车辆信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        this.loading = true
        // 使用导入的deleteVehicle函数，确保API路径正确
        const response = await deleteVehicle(row.id)
        if (response && response.code === 200) {
          this.$message.success('删除成功')
          this.fetchData()
        } else {
          this.$message.error(response ? response.message : '删除失败')
        }
      } catch (error) {
        if (error === 'cancel') {
          this.$message.info('已取消删除')
          return
        }
        
        console.error('删除车辆信息失败:', error)
        
        // 检查是否为关联数据错误（409状态码）
        if (error.response && error.response.status === 409) {
          // 设置错误信息和关联数据
          this.deleteErrorInfo = error.response.data.message || '删除失败，存在关联数据'
          this.deleteRelatedData = error.response.data.relatedData || []
          // 显示删除错误对话框
          this.deleteErrorDialogVisible = true
        } else {
          // 其他错误直接显示错误信息
          this.$message.error(error.response ? error.response.data.message : '删除失败')
        }
      } finally {
        this.loading = false
      }
    },
    handleCurrentChange(val) {
      this.pageInfo.pageNum = val
      this.fetchData()
    },
    handleDialogClose() {
      this.vehicleForm = {
        id: null,
        residentName: '',
        licensePlate: '',
        vehicleType: '',
        brand: '',
        model: '',
        color: '',
        engineNumber: '',
        chassisNumber: '',
        purchaseDate: '',
        purchasePrice: '',
        insuranceExpiryDate: '',
        annualInspectionDate: '',
        notes: ''
      }
      this.$refs.vehicleForm.resetFields()
      this.dialogVisible = false
      this.dialogMode = 'add'
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'vehicle', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    getVehicleTypeType(type) {
      const types = {
        '轿车': '轿车',
        'SUV': 'SUV',
        '货车': '货车',
        '客车': '客车',
        '其他': '其他'
      }
      return types[type] || '未知'
    },
    getUsageTypeType(type) {
      const types = {
        'private': '私家车',
        'commercial': '营运车',
        'government': '公务车',
        'other': '其他'
      }
      return types[type] || '未知'
    },
    getColorType(color) {
      const typeMap = {
        '白色': 'info',
        '黑色': 'danger', 
        '银色': 'primary',
        '灰色': 'warning',
        '红色': 'danger',
        '蓝色': 'primary',
        '黄色': 'warning',
        '绿色': 'success',
        '橙色': 'warning',
        '紫色': 'danger',
        '粉色': 'warning'
      }
      return typeMap[color] || 'info'
    }
  }
}
</script>

<style scoped>
.vehicle-management {
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

.vehicle-table {
  flex: 1;
  width: 100%;
}

.vehicle-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.vehicle-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.vehicle-table :deep(.el-table__row) {
  height: 48px;
}

.vehicle-table :deep(.el-table__cell) {
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

.vehicle-form {
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

  .vehicle-table {
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