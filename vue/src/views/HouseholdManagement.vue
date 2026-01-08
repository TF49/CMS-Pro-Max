<template>
  <div class="household-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><HomeFilled /></el-icon>
          <span>户籍管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加户籍
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline" label-width="80px">
          <el-form-item label="户籍编号">
            <el-input
              v-model="searchForm.householdNumber"
              placeholder="请输入户籍编号"
              clearable
            >
              <template #prefix>
                <el-icon><Document /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="户主姓名">
            <el-input
              v-model="searchForm.householderName"
              placeholder="请输入户主姓名"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
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

      <!-- 户籍列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="householdList"
          v-loading="loading"
          stripe
          border
          highlight-current-row
          class="household-table common-table"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: '600',
            textAlign: 'center'
          }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
          <el-table-column prop="householdNumber" label="户籍编号" width="150" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.householdNumber" size="small">{{ scope.row.householdNumber }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="householderName" label="户主姓名" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.householderName" size="small">{{ scope.row.householderName }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="householderIdCard" label="身份证号" width="180" align="center"></el-table-column>
          <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="phoneNumber" label="联系电话" width="120" align="center"></el-table-column>
          <el-table-column prop="populationCount" label="人口数量" width="100" align="center">
            <template #default="scope">
              <el-tag size="small" v-if="scope && scope.row && scope.row.populationCount">{{ scope.row.populationCount }} 人</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="registrationDate" label="登记日期" width="120" align="center"></el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <template v-if="scope && scope.row">
                <div class="operation-buttons" style="display: flex; gap: 5px; justify-content: center; width: 100%;">
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
              </template>
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

    <!-- 添加/编辑户籍对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form :model="currentHousehold" :rules="householdRules" ref="householdFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12" :xs="24" :sm="12">
            <el-form-item label="户籍编号" prop="householdNumber">
              <el-input
                v-model="currentHousehold.householdNumber"
                placeholder="请输入户籍编号"
                :disabled="isEdit"
              >
                <template #prefix>
                  <el-icon><Document /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12" :xs="24" :sm="12">
            <el-form-item label="户主姓名" prop="householderName">
              <el-input
                v-model="currentHousehold.householderName"
                placeholder="请输入户主姓名"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="householderIdCard">
              <el-input
                v-model="currentHousehold.householderIdCard"
                placeholder="请输入身份证号"
              >
                <template #prefix>
                  <el-icon><CreditCard /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phoneNumber">
              <el-input
                v-model="currentHousehold.phoneNumber"
                placeholder="请输入联系电话"
              >
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input
            v-model="currentHousehold.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          >
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="人口数量" prop="populationCount">
          <el-input-number
            v-model="currentHousehold.populationCount"
            :min="1"
            :max="20"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="登记日期" prop="registrationDate">
          <el-input
            v-model="currentHousehold.registrationDate"
            placeholder="请输入登记日期（格式：YYYY-MM-DD）"
            style="width: 100%;"
          >
            <template #prefix>
              <el-icon><Calendar /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="currentHousehold.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button type="primary" @click="submitForm">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      :title="'户籍详情'"
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
            <el-form-item label="户籍编号">
              <el-input 
                v-model="currentData.householdNumber" 
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
            <el-form-item label="户主姓名">
              <el-input 
                v-model="currentData.householderName" 
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
            <el-form-item label="户主身份证号">
              <el-input 
                v-model="currentData.householderIdCard" 
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
            <el-form-item label="户籍地址">
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
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input 
                v-model="currentData.phoneNumber" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="人口数量">
              <el-input 
                v-model="currentData.populationCount" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="户籍类型">
              <el-input 
                v-model="currentData.householdType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="登记日期">
              <el-input 
                v-model="currentData.registrationDate" 
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
            v-model="currentData.notes"
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
import { getHouseholdData, searchHouseholds, addHousehold, updateHousehold, deleteHousehold, getResidentsByHouseholdId } from '@/api/data'
import { clearStatisticsCache } from '@/api/system'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import websocketService from '@/utils/websocket'
import {
  HomeFilled,
  Plus,
  Document,
  User,
  UserFilled,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  CreditCard,
  Phone,
  Location,
  OfficeBuilding,
  Calendar,
  Clock,
  Close,
  Check
} from '@element-plus/icons-vue'

export default {
  name: 'HouseholdManagement',
  components: {
    HomeFilled,
    Plus,
    Document,
    User,
    UserFilled,
    Search,
    Refresh,
    View,
    Edit,
    Delete,
    CreditCard,
    Phone,
    Location,
    OfficeBuilding,
    Calendar,
    Clock,
    Close,
    Check,
    DeleteErrorDialog
  },
  data() {
    // 身份证号验证规则
    const validateIdCard = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入身份证号'));
      } else {
        // 18位身份证号验证
        const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        if (!reg.test(value)) {
          callback(new Error('身份证号格式不正确'));
        } else {
          callback();
        }
      }
    };
    
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入联系电话'));
      } else {
        // 手机号验证
        const reg = /^1[3-9]\d{9}$/;
        if (!reg.test(value)) {
          callback(new Error('手机号格式不正确'));
        } else {
          callback();
        }
      }
    };
    
    // 登记日期验证规则
    const validateRegistrationDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入登记日期'));
      } else {
        // 检查日期格式是否为YYYY-MM-DD
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(value)) {
          callback(new Error('登记日期格式不正确，请使用YYYY-MM-DD格式'));
        } else {
          // 检查日期是否有效
          const selectedDate = new Date(value);
          if (isNaN(selectedDate.getTime())) {
            callback(new Error('登记日期格式不正确'));
            return;
          }
          
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          
          if (selectedDate > today) {
            callback(new Error('登记日期不能晚于今天'));
          } else {
            callback();
          }
        }
      }
    };

    return {
      loading: false,
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      searchForm: {
        householdNumber: '',
        householderName: ''
      },
      householdList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加户籍',
      isEdit: false,
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: {
        title: '删除失败',
        message: '',
        solution: ''
      },
      deleteRelatedData: [],
      currentHousehold: {
        id: null,
        householdNumber: '',
        householderName: '',
        householderIdCard: '',
        address: '',
        phoneNumber: '',
        registrationDate: '',
        populationCount: 1,
        notes: ''
      },
      householdRules: {
        householdNumber: [
          { required: true, message: '请输入户籍编号', trigger: 'blur' }
        ],
        householderName: [
          { required: true, message: '请输入户主姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
        ],
        householderIdCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { validator: validateIdCard, trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' },
          { min: 5, max: 100, message: '地址长度应在5-100个字符之间', trigger: 'blur' }
        ],
        phoneNumber: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ],
        populationCount: [
          { required: true, message: '请输入人口数量', trigger: 'blur' }
        ],
        registrationDate: [
          { required: true, message: '请输入登记日期', trigger: 'blur' },
          { validator: validateRegistrationDate, trigger: 'blur' }
        ]
      }
    }
  },
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.loadHouseholdData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'household') {
        this.loadHouseholdData()
      }
    }
    websocketService.registerMessageHandler(this.wsHandler)

    // 监听自定义刷新事件
    this.refreshHandler = (e) => {
      if (e.detail.module === 'household') {
        this.loadHouseholdData()
      }
    }
    window.addEventListener('ws-refresh', this.refreshHandler)
  },
  // 统一使用activated()钩子加载数据，避免组件预加载时产生错误
  activated() {
    this.loadHouseholdData()
  },
  // 组件卸载前清理WebSocket监听器
  beforeUnmount() {
    if (this.wsHandler) {
      websocketService.unregisterMessageHandler(this.wsHandler)
    }
    if (this.refreshHandler) {
      window.removeEventListener('ws-refresh', this.refreshHandler)
    }
  },
  methods: {
    // 加载户籍数据
    loadHouseholdData() {
      this.loading = true
      
      // 检查是否有搜索条件
      const hasSearchCondition = this.searchForm.householdNumber.trim() || this.searchForm.householderName.trim()
      
      if (hasSearchCondition) {
        // 执行搜索
        const searchParams = {}
        if (this.searchForm.householdNumber.trim()) {
          searchParams.householdNumber = this.searchForm.householdNumber.trim()
        }
        if (this.searchForm.householderName.trim()) {
          searchParams.householderName = this.searchForm.householderName.trim()
        }
        
        searchHouseholds(searchParams).then(response => {
          if (response && response.code === 200) {
            this.householdList = response.data || []
            this.total = this.householdList.length
            this.currentPage = 1
          } else {
            this.$message.error('搜索户籍数据失败')
            this.householdList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          this.$message.error('搜索户籍数据失败')
          this.householdList = []
          this.total = 0
          this.loading = false
        })
      } else {
        // 获取分页数据
        getHouseholdData(this.currentPage, this.pageSize).then(response => {
          console.log('Household data response:', response)
          if (response && response.code === 200) {
            const result = response.data || {}
            console.log('Household result:', result)
            this.householdList = result.data || []
            this.total = result.total || 0
            console.log('Household list:', this.householdList)
            console.log('Household total:', this.total)
          } else {
            this.$message.error('获取户籍数据失败')
            console.error('获取户籍数据失败:', response)
            this.householdList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          this.$message.error('获取户籍数据失败')
          console.error('获取户籍数据失败:', error)
          this.householdList = []
          this.total = 0
          this.loading = false
        })
      }
    },
    handleAdd() {
      this.dialogTitle = '添加户籍'
      this.isEdit = false
      this.currentHousehold = {
        id: null,
        householdNumber: '',
        householderName: '',
        householderIdCard: '',
        address: '',
        phoneNumber: '',
        registrationDate: '',
        populationCount: 1,
        notes: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑户籍'
      this.isEdit = true
      const householdData = { ...row }
      this.currentHousehold = householdData
      this.dialogVisible = true
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'household', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    async handleDelete(row) {
      try {
        // 先查询该户籍的关联数据
        let relatedDataInfo = ''
        let hasRelatedData = false
        
        try {
          // 查询该户籍下的居民数量
          const residentsResponse = await getResidentsByHouseholdId(row.id)
          if (residentsResponse && residentsResponse.code === 200) {
            const residents = residentsResponse.data || []
            if (residents.length > 0) {
              hasRelatedData = true
              relatedDataInfo = `

关联数据影响：
- 居民记录：${residents.length}条

删除后，以上关联数据也将被同步删除，且不可恢复！`
            }
          }
        } catch (relatedError) {
          console.warn('查询关联数据失败:', relatedError)
        }
        
        await this.$confirm(`此操作将永久删除该户籍信息${relatedDataInfo}，是否继续?`, '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        const response = await deleteHousehold(row.id)
        if (response && response.code === 200) {
          this.$message.success('删除成功')
          this.loadHouseholdData() // 重新加载数据
          // 清除统计缓存，确保实时更新数据总数
          clearStatisticsCache().then(() => {
            console.log('统计缓存已清除');
          });
        } else {
          // 从localStorage获取用户信息
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          // 管理员显示详细错误，普通用户显示简洁错误
          if (userInfo.role === 'ADMIN') {
            this.$message.error('删除失败: ' + (response.message || '未知错误'))
          } else {
            this.$message.error('删除失败')
          }
        }
      } catch (error) {
        if (error === 'cancel') {
          this.$message.info('已取消删除')
        } else {
          console.error('删除户籍失败:', error)
          
          // 检查是否为关联数据错误（409状态码）
          if (error.response && error.response.status === 409) {
            // 显示删除错误对话框
            this.deleteErrorInfo = {
              title: '删除失败',
              message: (error.response.data && error.response.data.message) || '该户籍信息存在关联数据，无法删除',
              solution: '请先删除或转移该户籍下的所有居民信息，然后再尝试删除户籍。'
            }
            this.deleteErrorDialogVisible = true
          } else {
            // 从localStorage获取用户信息
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
            // 管理员显示详细错误，普通用户显示简洁错误
            if (userInfo.role === 'ADMIN') {
              // 其他错误使用DeleteErrorHandler处理
              await DeleteErrorHandler.handleDeleteError(error, {
                solution: '请先删除或转移该户籍下的所有居民信息，然后再尝试删除户籍。'
              })
            } else {
              this.$message.error('删除失败')
            }
          }
        }
      }
    },
    handleSearch() {
      this.loadHouseholdData()
    },
    resetSearch() {
      this.searchForm = {
        householdNumber: '',
        householderName: ''
      }
      this.loadHouseholdData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadHouseholdData() // 重新加载数据
      this.$message.info('每页条数: ' + val)
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadHouseholdData() // 重新加载数据
      this.$message.info('当前页: ' + val)
    },
    // 提交表单
    async submitForm() {
      this.$refs.householdFormRef.validate(async (valid) => {
          if (valid) {
            try {
              this.loading = true
              // 准备表单数据
              const formData = { ...this.currentHousehold }
            
            let response
            if (this.isEdit) {
              response = await updateHousehold(formData)
            } else {
              response = await addHousehold(formData)
            }
            
            if (response && response.code === 200) {
              this.$message.success(this.isEdit ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.loadHouseholdData() // 重新加载数据
              // 清除统计缓存，确保实时更新数据总数
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            } else {
              this.$message.error(this.isEdit ? '更新失败' : '添加失败')
            }
          } catch (error) {
            this.$message.error(this.isEdit ? '更新失败' : '添加失败')
          } finally {
            this.loading = false
          }
        } else {
          this.$message.error('表单验证失败，请检查输入信息')
        }
      })
    },
    handleDialogClose() {
      if (this.$refs.householdFormRef) {
        this.$refs.householdFormRef.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.household-management {
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

.household-table {
  flex: 1;
  width: 100%;
}

.household-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.household-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.household-table :deep(.el-table__row) {
  height: 48px;
}

.household-table :deep(.el-table__cell) {
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

.household-form {
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

  .household-table {
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