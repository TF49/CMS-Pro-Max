<template>
  <div class="education-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><Reading /></el-icon>
          <span>教育管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加教育记录
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
          <el-form-item label="学校名称">
            <el-input
              v-model="searchForm.schoolName"
              placeholder="请输入学校名称"
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

      <!-- 教育记录列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="educationList"
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
          <el-table-column prop="schoolName" label="学校名称" width="250" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="educationLevel" label="教育程度" width="100" align="center">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.educationLevel"
                :type="getEducationLevelType(scope.row.educationLevel)"
                size="small">
                {{ getEducationLevelLabel(scope.row.educationLevel) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="major" label="专业" width="200" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="enrollmentDate" label="入学日期" width="120" align="center"></el-table-column>
          <el-table-column prop="graduationDate" label="毕业日期" width="120" align="center"></el-table-column>
          <el-table-column prop="status" label="状态" width="80" align="center">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.status"
                :type="getStatusType(scope.row.status)"
                size="small">
                {{ getStatusLabel(scope.row.status) }}
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
    
    <!-- 添加/编辑教育记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :before-close="handleDialogClose"
      destroy-on-close
      class="common-dialog"
    >
      <el-form
        :model="currentEducation"
        :rules="educationRules"
        ref="educationForm"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input
                v-model="currentEducation.residentName"
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
                v-model="currentEducation.idCard"
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
            <el-form-item label="学校名称" prop="schoolName">
              <el-input
                v-model="currentEducation.schoolName"
                placeholder="请输入学校名称"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教育程度" prop="educationLevel">
              <el-select 
                v-model="currentEducation.educationLevel" 
                placeholder="请选择教育程度" 
                style="width: 100%;"
              >
                <el-option label="小学" value="PRIMARY"></el-option>
                <el-option label="初中" value="JUNIOR"></el-option>
                <el-option label="高中" value="SENIOR"></el-option>
                <el-option label="大专" value="COLLEGE"></el-option>
                <el-option label="本科" value="BACHELOR"></el-option>
                <el-option label="硕士" value="MASTER"></el-option>
                <el-option label="博士" value="DOCTOR"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input
                v-model="currentEducation.major"
                placeholder="请输入专业"
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
            <el-form-item label="入学日期" prop="enrollmentDate">
              <el-input
                v-model="currentEducation.enrollmentDate"
                placeholder="请输入入学日期（格式：YYYY-MM-DD）"
                style="width: 100%;"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业日期" prop="graduationDate">
              <el-input
                v-model="currentEducation.graduationDate"
                placeholder="请输入毕业日期（格式：YYYY-MM-DD）"
                style="width: 100%;"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
              <div class="status-hint" v-if="currentEducation.status === '在读'">
                <el-icon><InfoFilled /></el-icon>
                在读状态可留空毕业日期
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select 
                v-model="currentEducation.status" 
                placeholder="请选择状态" 
                style="width: 100%;"
              >
                <el-option label="在读" value="STUDYING"></el-option>
                <el-option label="毕业" value="GRADUATED"></el-option>
                <el-option label="辍学" value="DROP_OUT"></el-option>
                <el-option label="休学" value="SUSPENDED"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="notes">
          <el-input 
            v-model="currentEducation.notes" 
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
      :title="'教育详情'"
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
            <el-form-item label="学校名称">
              <el-input 
                v-model="currentData.schoolName" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><School /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教育程度">
              <el-input 
                :model-value="getEducationLevelLabel(currentData.educationLevel)" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><GraduationCap /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业">
              <el-input 
                v-model="currentData.major" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Reading /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学日期">
              <el-input 
                v-model="currentData.enrollmentDate" 
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
            <el-form-item label="毕业日期">
              <el-input 
                v-model="currentData.graduationDate" 
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
            <el-form-item label="状态">
              <el-input 
                :model-value="getStatusLabel(currentData.status)" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><InfoFilled /></el-icon>
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

    <!-- 删除错误提示对话框 -->
    <DeleteErrorDialog
      v-model="deleteErrorDialogVisible"
      :error-info="deleteErrorInfo"
      :related-data="deleteRelatedData"
    />
  </div>
</template>

<script>
import { searchEducation, getEducationData, addEducation, updateEducation, deleteEducation, getResidentByIdCard } from '@/api/data'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'
 import { 
   User, OfficeBuilding, Collection, Calendar, View, Edit, Delete, Search, 
   Refresh, Plus, Check, Close, Reading, CreditCard, School, GraduationCap, 
   Clock, MapLocation, InfoFilled 
 } from '@element-plus/icons-vue'
 
 export default {
  name: 'EducationManagement',
  components: {
    User,
    OfficeBuilding,
    Collection,
    Calendar,
    View,
    Edit,
    Delete,
    Search,
    Refresh,
    Plus,
    Check,
    Close,
    Reading,
    CreditCard,
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
    
    // 学校名称验证规则
    const validateSchoolName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入学校名称'));
      } else if (value.trim().length < 2 || value.trim().length > 50) {
        callback(new Error('学校名称长度应在2-50个字符之间'));
      } else {
        callback();
      }
    };
    
    // 居民姓名验证规则
    const validateResidentName = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入居民姓名'));
      } else if (value.trim().length < 2 || value.trim().length > 20) {
        callback(new Error('居民姓名长度应在2-20个字符之间'));
      } else {
        callback();
      }
    };
    
    // 入学日期验证规则
    const validateEnrollmentDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入入学日期'));
      } else {
        // 检查日期格式是否为YYYY-MM-DD
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(value)) {
          callback(new Error('入学日期格式不正确，请使用YYYY-MM-DD格式'));
        } else {
          // 检查日期是否有效
          const selectedDate = new Date(value);
          if (isNaN(selectedDate.getTime())) {
            callback(new Error('入学日期格式不正确'));
            return;
          }
          
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          
          if (selectedDate > today) {
            callback(new Error('入学日期不能晚于今天'));
          } else {
            callback();
          }
        }
      }
    };
    
    // 毕业日期验证规则
    const validateGraduationDate = (rule, value, callback) => {
      // 在读状态下允许为空
      if (this.currentEducation.status === '在读' && !value) {
        callback();
      } else if (!value) {
        callback(new Error('请输入毕业日期'));
      } else {
        // 检查日期格式是否为YYYY-MM-DD
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(value)) {
          callback(new Error('毕业日期格式不正确，请使用YYYY-MM-DD格式'));
        } else {
          // 检查日期是否有效
          const selectedDate = new Date(value);
          if (isNaN(selectedDate.getTime())) {
            callback(new Error('毕业日期格式不正确'));
            return;
          }
          
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          
          if (selectedDate > today) {
            callback(new Error('毕业日期不能晚于今天'));
          } else {
            // 检查毕业日期是否早于入学日期
            if (this.currentEducation.enrollmentDate) {
              const enrollmentDate = new Date(this.currentEducation.enrollmentDate);
              if (selectedDate < enrollmentDate) {
                callback(new Error('毕业日期不能早于入学日期'));
                return;
              }
            }
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
        residentName: '',
        schoolName: ''
      },
      educationList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加教育记录',
      isEdit: false,
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: null,
      deleteRelatedData: null,
      currentEducation: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '', // 添加身份证号字段
        schoolName: '',
        educationLevel: '',
        major: '',
        enrollmentDate: '',
        graduationDate: '',
        status: '在读',
        notes: ''
      },
      educationRules: {
        residentName: [
          { required: true, message: '请输入居民姓名', trigger: 'blur' },
          { validator: validateResidentName, trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { validator: validateIdCard, trigger: 'blur' }
        ],
        schoolName: [
          { required: true, message: '请输入学校名称', trigger: 'blur' },
          { min: 2, max: 100, message: '学校名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        educationLevel: [
          { required: true, message: '请选择学历', trigger: 'change' }
        ],
        enrollmentDate: [
          { required: true, message: '请输入入学日期', trigger: 'blur' },
          { validator: validateEnrollmentDate, trigger: 'blur' }
        ],
        graduationDate: [
          { validator: validateGraduationDate, trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  // 普通用户的组件会被Layout预加载，所以不在created中加载数据
  // 而是在组件被激活时（实际访问时）才加载数据
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.loadEducationData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'education') {
        this.loadEducationData()
      }
    }
    websocketService.registerMessageHandler(this.wsHandler)

    // 监听自定义刷新事件
    this.refreshHandler = (e) => {
      if (e.detail.module === 'education') {
        this.loadEducationData()
      }
    }
    window.addEventListener('ws-refresh', this.refreshHandler)
  },
  activated() {
    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const role = userInfo.role || 'USER'
    
    // 只有在用户访问该组件时才加载数据
    this.loadEducationData()
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
    // 加载教育数据
    loadEducationData() {
      this.loading = true
      // 检查是否有搜索条件
      const hasSearchCondition = this.searchForm.residentName.trim() || this.searchForm.schoolName.trim()
      
      if (hasSearchCondition) {
          // 执行搜索
          searchEducation({
            residentName: this.searchForm.residentName.trim(),
            schoolName: this.searchForm.schoolName.trim(),
            page: this.currentPage,
            size: this.pageSize
          }).then(response => {
            if (response && response.code === 200 && response.data) {
              this.educationList = Array.isArray(response.data) ? response.data : (Array.isArray(response.data.data) ? response.data.data : [])
              this.total = response.data.total || 0
            } else {
              this.$message.error('搜索教育数据失败')
              this.educationList = []
              this.total = 0
            }
            this.loading = false
          }).catch(error => {
            console.error('搜索教育数据失败:', error)
            this.$message.error('搜索教育数据失败')
            this.educationList = []
            this.total = 0
            this.loading = false
          })
        } else {
          // 获取分页数据
          getEducationData(this.currentPage, this.pageSize).then(response => {
            if (response && response.code === 200 && response.data) {
              this.educationList = Array.isArray(response.data) ? response.data : (Array.isArray(response.data.data) ? response.data.data : [])
              this.total = response.data.total || 0
            } else {
              this.$message.error('获取教育数据失败')
              this.educationList = []
              this.total = 0
            }
            this.loading = false
          }).catch(error => {
            console.error('获取教育数据失败:', error)
            this.$message.error('获取教育数据失败')
            this.educationList = []
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
    
    // 验证居民姓名
    validateResidentName(rule, value, callback) {
      if (!value || !value.trim()) {
        callback(new Error('请输入居民姓名'));
      } else if (value.trim().length < 2 || value.trim().length > 20) {
        callback(new Error('姓名长度在 2 到 20 个字符'));
      } else {
        callback();
      }
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.currentEducation.idCard && this.currentEducation.idCard.trim()) {
        const idCard = this.currentEducation.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.currentEducation.residentName || !this.currentEducation.residentName.trim()) {
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
            if (resident.name && resident.name.trim() === this.currentEducation.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.currentEducation.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.currentEducation.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.currentEducation.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    // 教育程度英文转中文
    getEducationLevelLabel(level) {
      const levelMap = {
        'PRIMARY': '小学',
        'JUNIOR': '初中',
        'SENIOR': '高中',
        'COLLEGE': '大专',
        'BACHELOR': '本科',
        'MASTER': '硕士',
        'DOCTOR': '博士',
        // 保留中文映射，兼容旧数据
        '小学': '小学',
        '初中': '初中',
        '高中': '高中',
        '大专': '大专',
        '本科': '本科',
        '硕士': '硕士',
        '博士': '博士'
      };
      return levelMap[level] || level;
    },
    // 教育程度标签类型
    getEducationLevelType(level) {
      const levelMap = {
        'PRIMARY': '',
        'JUNIOR': 'info',
        'SENIOR': 'warning',
        'COLLEGE': 'success',
        'BACHELOR': 'primary',
        'MASTER': 'danger',
        'DOCTOR': 'danger',
        // 保留中文映射，兼容旧数据
        '小学': '',
        '初中': 'info',
        '高中': 'warning',
        '大专': 'success',
        '本科': 'primary',
        '硕士': 'danger',
        '博士': 'danger'
      };
      return levelMap[level] || '';
    },
    // 状态英文转中文
    getStatusLabel(status) {
      const statusMap = {
        'STUDYING': '在读',
        'GRADUATED': '毕业',
        // 保留中文映射，兼容旧数据
        '在读': '在读',
        '毕业': '毕业',
        '辍学': '辍学',
        '休学': '休学'
      };
      return statusMap[status] || status;
    },
    // 状态标签类型
    getStatusType(status) {
      const statusMap = {
        'STUDYING': 'primary',
        'GRADUATED': 'success',
        // 保留中文映射，兼容旧数据
        '在读': 'primary',
        '毕业': 'success',
        '辍学': 'danger',
        '休学': 'warning'
      };
      return statusMap[status] || '';
    },
    // 处理添加教育记录
    handleAdd() {
      this.dialogTitle = '添加教育记录'
      this.isEdit = false
      this.currentEducation = {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        schoolName: '',
        educationLevel: '',
        major: '',
        enrollmentDate: '',
        graduationDate: '',
        status: '在读',
        notes: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑教育记录'
      this.isEdit = true
      this.currentEducation = { ...row }
      // 如果没有身份证号，设置为空字符串
      if (!this.currentEducation.idCard) {
        this.currentEducation.idCard = ''
      }
      this.dialogVisible = true
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'education', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    async handleDelete(row) {
      try {
        this.loading = true
        await this.$confirm('此操作将永久删除该教育记录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteEducation(row.id)
        this.$message.success('删除成功')
        this.loadEducationData() // 重新加载数据
      } catch (error) {
        if (error === 'cancel') {
          this.$message.info('已取消删除')
        } else if (error.response && error.response.status === 409) {
          // 关联数据错误
          this.deleteErrorInfo = error.response.data.message || '删除失败：存在关联数据'
          this.deleteRelatedData = error.response.data.relatedData || []
          this.deleteErrorDialogVisible = true
        } else {
          console.error('删除教育记录失败:', error)
          DeleteErrorHandler.handleDeleteError(error, '教育记录')
        }
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.loadEducationData()
    },
    resetSearch() {
      this.searchForm = {
        residentName: '',
        schoolName: ''
      }
      this.loadEducationData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadEducationData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadEducationData()
    },
    // 提交表单
    async submitForm() {
      this.$refs.educationForm.validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true
            // 准备表单数据
            const formData = { ...this.currentEducation }
            
            // 如果是添加操作，需要根据身份证号获取residentId
            if (!this.isEdit && formData.idCard) {
              try {
                console.log('正在根据身份证号获取居民ID:', formData.idCard)
                const residentResponse = await getResidentByIdCard(formData.idCard)
                if (residentResponse && residentResponse.code === 200 && residentResponse.data) {
                  const resident = residentResponse.data
                  if (resident.id) {
                    formData.residentId = resident.id
                    console.log('成功获取居民ID:', resident.id)
                  } else {
                    throw new Error('未找到对应的居民信息，请检查身份证号是否正确')
                  }
                } else {
                  throw new Error('获取居民信息失败: ' + (residentResponse ? residentResponse.message : '未知错误'))
                }
              } catch (residentError) {
                console.error('获取居民信息失败:', residentError)
                // 使用弹窗提醒用户未找到该居民
                this.$alert('未找到身份证号为 "' + formData.idCard + '" 的居民信息，请检查身份证号是否正确或先在居民管理中添加该居民。', '未找到居民信息', {
                  confirmButtonText: '确定',
                  type: 'warning',
                  center: true,
                  customClass: 'resident-not-found-alert'
                })
                this.loading = false
                return
              }
            }
            
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
            
            // 格式化入学日期和毕业日期
            formData.enrollmentDate = formatDate(formData.enrollmentDate)
            formData.graduationDate = formatDate(formData.graduationDate)
            
            // 调试：打印发送的数据
            console.log('=== 开始提交教育记录表单 ===')
            console.log('操作类型:', this.isEdit ? '更新' : '添加')
            console.log('发送的数据:', JSON.stringify(formData))
            
            let response
            if (this.isEdit) {
              response = await updateEducation(formData)
            } else {
              response = await addEducation(formData)
            }
            
            // 调试：打印响应数据
            console.log('响应数据:', JSON.stringify(response))
            
            if (response && response.code === 200) {
              this.$message.success(this.isEdit ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.loadEducationData() // 重新加载数据
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            } else {
              console.error('响应错误:', response)
              this.$message.error(response ? (response.message || (this.isEdit ? '更新失败' : '添加失败')) : '无响应数据')
            }
          } catch (error) {
              console.error(this.isEdit ? '更新失败:' : '添加失败:', error)
              console.error('错误详情:', error.response || error.message || error)
              // 使用传统逻辑判断替代可选链操作符
              let errorMsg = error.message || '未知错误'
              if (error.response && error.response.data && error.response.data.message) {
                errorMsg = error.response.data.message
              }
              this.$message.error(this.isEdit ? '更新失败: ' + errorMsg : '添加失败: ' + errorMsg)
            } finally {
              console.log('=== 表单提交结束 ===')
              this.loading = false
            }
        } else {
          this.$message.error('表单验证失败，请检查输入信息')
          return false
        }
      })
    },
    // 处理对话框关闭
    handleDialogClose() {
      this.dialogVisible = false
      // 确保educationForm存在再调用resetFields
      if (this.$refs.educationForm) {
        this.$refs.educationForm.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.education-management {
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

.education-table {
  flex: 1;
  width: 100%;
}

.education-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.education-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.education-table :deep(.el-table__row) {
  height: 48px;
}

.education-table :deep(.el-table__cell) {
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

.education-form {
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

/* 日期输入组件样式 */
.date-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.date-input-container {
  display: flex;
  align-items: center;
  gap: 4px;
  position: relative;
}

.date-form-item {
  margin-bottom: 0 !important;
}

.date-input {
  width: 80px;
  text-align: center;
}

.date-input.year {
  width: 90px;
}

.date-input.month {
  width: 60px;
}

.date-input.day {
  width: 60px;
}

/* 错误消息样式 */
.date-error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.date-error .el-icon {
  font-size: 12px;
}

/* 状态提示样式 */
.status-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-hint .el-icon {
  font-size: 12px;
}

/* 按钮图标样式 */
.save-button .el-icon,
.cancel-button .el-icon {
  margin-right: 4px;
}

/* 响应式设计增强 */
@media (max-width: 768px) {
  .date-input-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .date-input-container {
    width: 100%;
    justify-content: flex-start;
  }

  .date-input {
    width: 70px;
  }

  .date-input.year {
    width: 80px;
  }

  .date-input.month,
  .date-input.day {
    width: 50px;
  }
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

  .education-table {
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

/* 居民未找到弹窗样式 */
:deep(.resident-not-found-alert) {
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    border: none;
  }
  
  .el-message-box__header {
    background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
    border-radius: 12px 12px 0 0;
    padding: 16px 20px;
  }
  
  .el-message-box__title {
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .el-message-box__title::before {
    content: "⚠️";
    font-size: 20px;
  }
  
  .el-message-box__content {
    padding: 24px 20px;
    background: #fff;
  }
  
  .el-message-box__message {
    color: #606266;
    font-size: 16px;
    line-height: 1.6;
    text-align: center;
  }
  
  .el-message-box__btns {
    padding: 16px 20px;
    background: #f8f9fa;
    border-radius: 0 0 12px 12px;
    display: flex;
    justify-content: center;
  }
  
  .el-message-box__btns .el-button {
    min-width: 100px;
    border-radius: 6px;
    font-weight: 500;
  }
  
  .el-message-box__btns .el-button--primary {
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    border: none;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  }
  
  .el-message-box__btns .el-button--primary:hover {
    background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }
}
</style>