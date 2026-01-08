<template>
  <div class="medical-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><FirstAidKit /></el-icon>
          <span>医疗管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加医疗记录
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline" label-width="80px">
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
          <el-form-item label="医院名称">
            <el-input 
              v-model="searchForm.hospital" 
              placeholder="请输入医院名称" 
              clearable
            >
              <template #prefix>
                <el-icon><OfficeBuilding /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="search-buttons">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 医疗记录列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="medicalList"
          v-loading="loading"
          stripe
          border
          highlight-current-row
          class="common-table"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: '600',
            textAlign: 'center'
          }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="60" align="center"></el-table-column>
          <el-table-column prop="residentName" label="居民姓名" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.residentName" size="small">{{ scope.row.residentName }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="idCard" label="身份证号" width="180" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="hospital" label="医院" width="250" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="department" label="科室" width="120" align="center"></el-table-column>
          <el-table-column prop="doctor" label="医生" width="100" align="center"></el-table-column>
          <el-table-column prop="diagnosis" label="诊断" width="150" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="lastCheckupDate" label="检查日期" width="100" align="center"></el-table-column>
          <el-table-column prop="insuranceType" label="保险类型" width="120" align="center">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.insuranceType"
                :type="getInsuranceType(scope.row.insuranceType)"
                size="small">
                {{ getInsuranceTypeLabel(scope.row.insuranceType) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <div v-if="scope && scope.row" class="operation-buttons">
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
      <div class="pagination-container common-pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
        >
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 添加/编辑医疗记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="700px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        :model="currentMedical"
        :rules="medicalRules"
        ref="medicalForm"
        label-width="120px"
        class="medical-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input
                v-model="currentMedical.residentName"
                placeholder="请输入居民姓名"
                @blur="validateResidentName"
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
                v-model="currentMedical.idCard"
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
            <el-form-item label="医院" prop="hospital">
              <el-input
                v-model="currentMedical.hospital"
                placeholder="请输入医院名称"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-input
                v-model="currentMedical.department"
                placeholder="请输入科室"
              >
                <template #prefix>
                  <el-icon><School /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="医生" prop="doctor">
              <el-input
                v-model="currentMedical.doctor"
                placeholder="请输入医生姓名"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="诊断" prop="diagnosis">
              <el-input
                v-model="currentMedical.diagnosis"
                placeholder="请输入诊断结果"
              >
                <template #prefix>
                  <el-icon><Edit /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检查日期" prop="lastCheckupDate">
              <el-input
                v-model="currentMedical.lastCheckupDate"
                placeholder="请输入检查日期（格式：YYYY-MM-DD）"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保险类型" prop="insuranceType">
              <el-select 
                v-model="currentMedical.insuranceType" 
                placeholder="请选择保险类型" 
                style="width: 100%;"
              >
                <el-option label="城镇职工医疗保险" value="城镇职工医疗保险"></el-option>
                <el-option label="城乡居民医疗保险" value="城乡居民医疗保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remarks">
          <el-input 
            v-model="currentMedical.remarks" 
            type="textarea" 
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleDialogClose" class="cancel-button">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button type="primary" @click="submitForm" class="save-button">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      :title="'医疗详情'"
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
            <el-form-item label="医院">
              <el-input 
                v-model="currentData.hospital" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科室">
              <el-input 
                v-model="currentData.department" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><FirstAidKit /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="医生">
              <el-input 
                v-model="currentData.doctor" 
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
            <el-form-item label="诊断">
              <el-input 
                v-model="currentData.diagnosis" 
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
            <el-form-item label="检查日期">
              <el-input 
                v-model="currentData.lastCheckupDate" 
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
            <el-form-item label="保险类型">
              <el-input 
                v-model="currentData.insuranceType" 
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
        <el-form-item label="备注">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            v-model="currentData.remarks"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
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
import { searchMedical, getMedicalData, addMedical, updateMedical, deleteMedical, getResidentByIdCard } from '@/api/data'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'
import {
  User, OfficeBuilding, Edit, Calendar, View, Delete, Search,
  Refresh, Plus, Check, Close, Document, School, FirstAidKit, CreditCard, Clock
} from '@element-plus/icons-vue'

export default {
  name: 'MedicalManagement',
  components: {
      User,
      OfficeBuilding,
      Edit,
      Calendar,
      View,
      Delete,
      Search,
      Refresh,
      Plus,
      Check,
      Close,
      Document,
      School,
      FirstAidKit,
      CreditCard,
      Clock,
      DeleteErrorDialog
    },
  data() {
    // 身份证号验证规则
    const validateIdCard = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入身份证号'))
      } else {
        // 18位身份证号验证
        const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        if (!reg.test(value)) {
          callback(new Error('身份证号格式不正确'))
        } else {
          callback()
        }
      }
    }
    
    // 医院名称验证规则
    const validateHospital = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入医院名称'))
      } else if (value.trim().length < 2 || value.trim().length > 50) {
        callback(new Error('医院名称长度应在2-50个字符之间'))
      } else {
        callback()
      }
    }
    
    // 诊断结果验证规则
    const validateDiagnosis = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入诊断结果'))
      } else if (value.trim().length < 2 || value.trim().length > 200) {
        callback(new Error('诊断结果长度应在2-200个字符之间'))
      } else {
        callback()
      }
    }
    
    return {
      loading: false,
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      searchForm: {
        residentName: '',
        hospital: ''
      },
      medicalList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加医疗记录',
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: '',
      deleteRelatedData: [],
      currentMedical: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        hospital: '',
        department: '',
        doctor: '',
        diagnosis: '',
        lastCheckupDate: '',
        insuranceType: '',
        remarks: ''
      },
      medicalRules: {
        idCard: [
          { required: true, validator: validateIdCard, trigger: 'blur' }
        ],
        residentName: [
          { required: true, message: '请先填写居民姓名', trigger: 'blur' },
          { min: 1, max: 50, message: '居民姓名长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        hospital: [
          { required: true, validator: validateHospital, trigger: 'blur' }
        ],
        diagnosis: [
          { required: true, validator: validateDiagnosis, trigger: 'blur' }
        ]
      }
    }
  },
  // 普通用户的组件会被Layout预加载，所以不在created中加载数据
  // 而是在组件被激活时（实际访问时）才加载数据
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.loadMedicalData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'medical') {
        this.loadMedicalData()
      }
    }
    websocketService.registerMessageHandler(this.wsHandler)
  },
  activated() {
    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const role = userInfo.role || 'USER'

    // 只有在用户访问该组件时才加载数据
    this.loadMedicalData()
  },
  beforeUnmount() {
    if (this.wsHandler) {
      websocketService.unregisterMessageHandler(this.wsHandler)
    }
  },
  methods: {
    // 加载医疗数据
    loadMedicalData() {
      this.loading = true
      // 检查是否有搜索条件
      const hasSearchCondition = this.searchForm.residentName.trim() || this.searchForm.hospital.trim()
      
      if (hasSearchCondition) {
        // 执行搜索
        searchMedical({
          residentName: this.searchForm.residentName.trim(),
          hospitalName: this.searchForm.hospital.trim(),
          page: this.currentPage,
          size: this.pageSize
        }).then(response => {
          if (response && response.code === 200 && response.data) {
            this.medicalList = Array.isArray(response.data) ? response.data : (Array.isArray(response.data.data) ? response.data.data : [])
            this.total = response.data.total || 0
          } else {
            this.$message.error('搜索医疗数据失败')
            this.medicalList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          console.error('搜索医疗数据失败:', error)
          this.$message.error('搜索医疗数据失败')
          this.medicalList = []
          this.total = 0
          this.loading = false
        })
      } else {
        // 获取分页数据
        getMedicalData(this.currentPage, this.pageSize).then(response => {
          if (response && response.code === 200 && response.data) {
            this.medicalList = Array.isArray(response.data) ? response.data : (Array.isArray(response.data.data) ? response.data.data : [])
            this.total = response.data.total || 0
          } else {
            this.$message.error('获取医疗数据失败')
            this.medicalList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          console.error('获取医疗数据失败:', error)
          this.$message.error('获取医疗数据失败')
          this.medicalList = []
          this.total = 0
          this.loading = false
        })
      }
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },
    getInsuranceType(type) {
      const typeMap = {
        '城镇职工医疗保险': 'primary',
        '城乡居民医疗保险': 'success',
        'EMPLOYEE': 'primary',
        'RESIDENT': 'success'
      };
      return typeMap[type] || '';
    },
    
    // 保险类型英文转中文
    getInsuranceTypeLabel(type) {
      const typeMap = {
        'EMPLOYEE': '城镇职工医疗保险',
        'RESIDENT': '城乡居民医疗保险',
        // 保留中文映射，兼容旧数据
        '城镇职工医疗保险': '城镇职工医疗保险',
        '城乡居民医疗保险': '城乡居民医疗保险'
      };
      return typeMap[type] || type;
    },
    handleAdd() {
      this.dialogTitle = '添加医疗记录'
      this.currentMedical = {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        hospital: '',
        department: '',
        doctor: '',
        diagnosis: '',
        lastCheckupDate: '',
        insuranceType: '',
        remarks: ''
      }
      this.dialogVisible = true
      // 确保表单重置
      this.$nextTick(() => {
        if (this.$refs.medicalForm) {
          this.$refs.medicalForm.clearValidate()
        }
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑医疗记录'
      this.currentMedical = { ...row }
      this.dialogVisible = true
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'medical', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    handleDelete(row) {
      this.$confirm('此操作将永久删除该医疗记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteMedical(row.id).then(response => {
          if (response && response.code === 200) {
            this.$message.success('删除成功')
            this.loadMedicalData() // 重新加载数据
          } else {
            this.$message.error('删除失败: ' + (response && response.message ? response.message : '未知错误'))
          }
          this.loading = false
        }).catch(error => {
          console.error('删除医疗记录失败:', error)
          // 处理关联数据错误
          if (error.response && error.response.status === 409) {
            // 关联数据错误，显示错误对话框
            this.deleteErrorInfo = error.response.data.message || '该医疗记录存在关联数据，无法删除'
            this.deleteRelatedData = error.response.data.relatedData || []
            this.deleteErrorDialogVisible = true
          } else {
            // 其他错误，使用传统错误提示
            this.$message.error('删除失败')
          }
          this.loading = false
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    handleSearch() {
      this.currentPage = 1
      this.loadMedicalData()
    },
    resetSearch() {
      this.searchForm = {
        residentName: '',
        hospital: ''
      }
      this.loadMedicalData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadMedicalData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadMedicalData()
    },
    // 验证居民姓名
    validateResidentName() {
      if (!this.currentMedical.residentName || !this.currentMedical.residentName.trim()) {
        this.$message.warning('请填写居民姓名')
      }
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.currentMedical.idCard && this.currentMedical.idCard.trim()) {
        const idCard = this.currentMedical.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.currentMedical.residentName || !this.currentMedical.residentName.trim()) {
          this.$message.warning('请先填写居民姓名')
          return
        }
        
        // 根据身份证号验证居民是否存在
        try {
          this.loading = true
          const residentResponse = await getResidentByIdCard(idCard)
          if (residentResponse && residentResponse.code === 200 && residentResponse.data) {
            const resident = residentResponse.data
            // 验证居民姓名是否匹配
            if (resident.name && resident.name.trim() === this.currentMedical.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.currentMedical.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.currentMedical.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.currentMedical.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    async submitForm() {
      this.$refs.medicalForm.validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true
            // 准备表单数据
            const formData = { ...this.currentMedical }
            
            // 如果是添加操作，需要根据身份证号获取residentId并验证居民信息
            if (!formData.id && formData.idCard) {
              try {
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
                  center: true,
                  customClass: 'resident-not-found-alert'
                })
                this.loading = false
                return
              }
            }
            
            // 调试：打印发送的数据
            console.log('=== 开始提交医疗记录表单 ===')
            console.log('操作类型:', formData.id ? '更新' : '添加')
            console.log('发送的数据:', JSON.stringify(formData))
            
            let response
            if (formData.id) {
              response = await updateMedical(formData)
            } else {
              response = await addMedical(formData)
            }
            
            // 调试：打印响应数据
            console.log('响应数据:', JSON.stringify(response))
            
            if (response && response.code === 200) {
              this.$message.success(formData.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.loadMedicalData() // 重新加载数据
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            } else {
              console.error('响应错误:', response)
              this.$message.error(response ? (response.message || (formData.id ? '更新失败' : '添加失败')) : '无响应数据')
            }
          } catch (error) {
            console.error(formData.id ? '更新失败:' : '添加失败:', error)
            console.error('错误详情:', error.response || error.message || error)
            // 使用传统逻辑判断替代可选链操作符
            let errorMsg = error.message || '未知错误'
            if (error.response && error.response.data && error.response.data.message) {
              errorMsg = error.response.data.message
            }
            this.$message.error(formData.id ? '更新失败: ' + errorMsg : '添加失败: ' + errorMsg)
          } finally {
            this.loading = false
          }
        } else {
          console.log('表单验证失败')
          return false
        }
      })
    },
    handleDialogClose() {
      this.dialogVisible = false
      this.$refs.medicalForm.clearValidate()
    }
  }
}
</script>

<style scoped>
.medical-management {
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

.search-bar :deep(.el-form) {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
}

.search-bar :deep(.el-form-item__label) {
  width: auto;
  padding-right: 8px;
}

.search-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.table-container {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.medical-table {
  flex: 1;
  width: 100%;
}

.medical-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.medical-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.medical-table :deep(.el-table__row) {
  height: 48px;
}

.medical-table :deep(.el-table__cell) {
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

.medical-form {
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

  .medical-table {
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