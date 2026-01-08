<template>
  <div class="employment-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><OfficeBuilding /></el-icon>
          <span>就业管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加就业记录
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
          <el-form-item label="工作单位">
            <el-input
              v-model="searchForm.company"
              placeholder="请输入工作单位"
              clearable
            >
              <template #prefix>
                <el-icon><OfficeBuilding /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="行业">
            <el-select 
              v-model="searchForm.industry" 
              placeholder="请选择行业" 
              clearable
              filterable
              style="width: 180px"
              popper-class="industry-select-popper"
            >
              <template #prefix>
                <el-icon><Briefcase /></el-icon>
              </template>
              <el-option 
                v-for="industry in industryOptions" 
                :key="industry.value" 
                :label="industry.label" 
                :value="industry.value"
                :class="`industry-option industry-${industry.value.replace('/', '-')}`"
              >
                <div class="industry-option-content">
                  <el-icon class="industry-icon">
                    <component :is="industry.icon" />
                  </el-icon>
                  <span>{{ industry.label }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="就业状态">
            <div class="status-with-buttons">
              <el-select 
                v-model="searchForm.employmentStatus" 
                placeholder="请选择就业状态" 
                clearable
                style="width: 150px; margin-right: 20px;"
                popper-class="employment-status-select-popper"
              >
                <template #prefix>
                  <el-icon><TrendCharts /></el-icon>
                </template>
                <el-option 
                  v-for="status in employmentStatusOptions" 
                  :key="status.value" 
                  :label="status.label" 
                  :value="status.value"
                  :class="`status-option status-${status.value}`"
                >
                  <div class="status-option-content">
                    <el-tag :type="status.type" size="small">{{ status.label }}</el-tag>
                  </div>
                </el-option>
              </el-select>
              <el-button type="primary" @click="handleSearch" style="margin-right: 15px;">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
              <el-button @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <!-- 就业信息列表 -->
        <div class="common-table-wrapper">
          <el-table
            :data="employmentList"
            border
            stripe
            v-loading="loading"
            class="common-table employment-table"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: '600',
            textAlign: 'center'
          }"
          :cell-style="{ textAlign: 'center' }"
          row-key="id"
        >
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="residentName" label="居民姓名" width="100">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.residentName"
                size="small">
                {{ scope.row.residentName }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="company" label="工作单位" show-overflow-tooltip></el-table-column>
          <el-table-column prop="position" label="职位" width="120"></el-table-column>
          <el-table-column prop="salary" label="薪资(元)" width="120" align="right">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.salary !== null && scope.row.salary !== undefined && !isNaN(scope.row.salary)">
                {{ parseFloat(scope.row.salary).toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="employmentStatus" label="就业状态" width="100">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.employmentStatus">
                <el-tag :type="getEmploymentStatusType(scope.row.employmentStatus)">
                  {{ scope.row.employmentStatus }}
                </el-tag>
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="industry" label="行业" width="100">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.industry">
                {{ scope.row.industry }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="入职日期" width="120">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.startDate">
                {{ scope.row.startDate }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="contractType" label="合同类型" width="120">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.contractType">
                {{ scope.row.contractType }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <div class="operation-buttons" v-if="scope && scope.row">
                <el-button size="mini" type="primary" plain @click="handleView(scope.row)">
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
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form 
        :model="employmentForm" 
        :rules="employmentRules" 
        ref="employmentForm" 
        label-width="100px"
        class="employment-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input 
                v-model="employmentForm.residentName" 
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
                v-model="employmentForm.idCard" 
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
            <el-form-item label="工作单位" prop="company">
              <el-input 
                v-model="employmentForm.company" 
                placeholder="请输入工作单位"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input 
                v-model="employmentForm.position" 
                placeholder="请输入职位"
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
            <el-form-item label="薪资(元)" prop="salary">
              <el-input-number 
                v-model="employmentForm.salary" 
                :min="0" 
                :precision="2" 
                :step="1000" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="就业状态" prop="employmentStatus">
              <el-select 
                v-model="employmentForm.employmentStatus" 
                placeholder="请选择就业状态" 
                style="width: 100%"
              >
                <el-option label="在职" value="在职"></el-option>
                <el-option label="失业" value="失业"></el-option>
                <el-option label="退休" value="退休"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行业" prop="industry">
              <el-select 
                v-model="employmentForm.industry" 
                placeholder="请选择行业" 
                style="width: 100%"
              >
                <el-option label="IT/互联网" value="IT/互联网"></el-option>
                <el-option label="金融" value="金融"></el-option>
                <el-option label="制造业" value="制造业"></el-option>
                <el-option label="教育" value="教育"></el-option>
                <el-option label="医疗" value="医疗"></el-option>
                <el-option label="服务业" value="服务业"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期" prop="startDate">
              <el-input
                v-model="employmentForm.startDate"
                placeholder="请输入入职日期（格式：YYYY-MM-DD）"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select 
                v-model="employmentForm.contractType" 
                placeholder="请选择合同类型" 
                style="width: 100%"
              >
                <el-option label="固定期限" value="固定期限"></el-option>
                <el-option label="无固定期限" value="无固定期限"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
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
                v-model="employmentForm.notes">
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
      :title="'就业详情'"
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
            <el-form-item label="工作单位">
              <el-input 
                v-model="currentData.company" 
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
            <el-form-item label="职位">
              <el-input 
                v-model="currentData.position" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Briefcase /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪资(元)">
              <el-input 
                v-model="currentData.salary" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Money /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行业">
              <el-input 
                v-model="currentData.industry" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><TrendCharts /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-input 
                v-model="currentData.startDate" 
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
            <el-form-item label="就业状态">
              <el-input 
                v-model="currentData.employmentStatus" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Monitor /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同类型">
              <el-input 
                v-model="currentData.contractType" 
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
import { get, post, put, del } from '@/utils/request'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'
import { Plus, Delete, Search, Refresh, User, OfficeBuilding, Edit, Close, Check, View, Briefcase, TrendCharts, Monitor, Money, Setting, Reading, FirstAidKit, Service, More, CreditCard, Clock } from '@element-plus/icons-vue'

export default {
  name: 'EmploymentManagement',
  components: {
    DeleteErrorDialog,
    Plus,
    Delete,
    Search,
    Refresh,
    User,
    OfficeBuilding,
    Edit,
    Close,
    Check,
    View,
    Briefcase,
    TrendCharts,
    Monitor,
    Money,
    Setting,
    Reading,
    FirstAidKit,
    Service,
    More,
    CreditCard,
    Clock
  },
  data() {
    // 薪资验证规则
    const validateSalary = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入薪资'));
      } else {
        const salary = parseFloat(value);
        if (isNaN(salary) || salary < 0 || salary > 1000000) {
          callback(new Error('薪资应在0-1000000之间'));
        } else {
          callback();
        }
      }
    };
    
    // 工作单位验证规则
    const validateCompany = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入工作单位'));
      } else if (value.trim().length < 2 || value.trim().length > 100) {
        callback(new Error('工作单位长度应在2-100个字符之间'));
      } else {
        callback();
      }
    };
    
    // 职位验证规则
    const validatePosition = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入职位'));
      } else if (value.trim().length < 2 || value.trim().length > 50) {
        callback(new Error('职位长度应在2-50个字符之间'));
      } else {
        callback();
      }
    };
    
    // 入职日期验证规则
    const validateEmploymentDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入入职日期'));
      } else {
        // 检查日期格式是否为YYYY-MM-DD
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(value)) {
          callback(new Error('入职日期格式不正确，请使用YYYY-MM-DD格式'));
        } else {
          // 检查日期是否有效
          const selectedDate = new Date(value);
          if (isNaN(selectedDate.getTime())) {
            callback(new Error('入职日期格式不正确'));
            return;
          }
          
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          
          if (selectedDate > today) {
            callback(new Error('入职日期不能晚于今天'));
          } else {
            callback();
          }
        }
      }
    };
    
    // 行业验证规则
    const validateIndustry = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择行业'));
      } else {
        callback();
      }
    };
    
    // 就业状态验证规则
    const validateEmploymentStatus = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择就业状态'));
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
    
    // 验证居民姓名与身份证号是否匹配
    const validateResidentInfo = (rule, value, callback) => {
      // 这个验证将在表单提交时进行，这里主要是为了保持规则结构
      callback();
    };
    
    return {
      loading: false,
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      searchForm: {
        residentName: '',
        company: '',
        industry: '',
        employmentStatus: ''
      },
      employmentList: [],
      pageInfo: {
        page: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '添加就业信息',
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: '',
      deleteRelatedData: [],
      employmentForm: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        company: '',
        position: '',
        salary: null,
        industry: '',
        startDate: '',
        employmentStatus: '',
        contractType: '',
        notes: ''
      },
      employmentRules: {
        residentName: [
          { required: true, message: '请输入居民姓名', trigger: 'blur' },
          { validator: validateResidentName, trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { validator: validateIdCard, trigger: 'blur' }
        ],
        company: [
          { required: true, message: '请输入工作单位', trigger: 'blur' },
          { validator: validateCompany, trigger: 'blur' }
        ],
        position: [
          { required: true, message: '请输入职位', trigger: 'blur' },
          { validator: validatePosition, trigger: 'blur' }
        ],
        salary: [
          { required: true, message: '请输入薪资', trigger: 'blur' },
          { validator: validateSalary, trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '请选择行业', trigger: 'change' },
          { validator: validateIndustry, trigger: 'change' }
        ],
        startDate: [
          { required: true, message: '请选择入职日期', trigger: 'change' },
          { validator: validateEmploymentDate, trigger: 'change' }
        ],
        employmentStatus: [
          { required: true, message: '请选择就业状态', trigger: 'change' },
          { validator: validateEmploymentStatus, trigger: 'change' }
        ],
        contractType: [
          { required: true, message: '请选择合同类型', trigger: 'change' }
        ]
      },
      industryOptions: [
        { value: 'IT/互联网', label: 'IT/互联网', icon: 'el-icon-monitor' },
        { value: '金融', label: '金融', icon: 'el-icon-money' },
        { value: '医疗', label: '医疗', icon: 'el-icon-first-aid-kit' },
        { value: '教育', label: '教育', icon: 'el-icon-reading' },
        { value: '制造业', label: '制造业', icon: 'el-icon-suitcase' },
        { value: '服务业', label: '服务业', icon: 'el-icon-service' },
        { value: '建筑', label: '建筑', icon: 'el-icon-office-building' },
        { value: '农业', label: '农业', icon: 'el-icon-crop' },
        { value: '其他', label: '其他', icon: 'el-icon-more' }
      ],
      employmentStatusOptions: [
        { value: '在职', label: '在职', type: 'success' },
        { value: '失业', label: '失业', type: 'danger' },
        { value: '退休', label: '退休', type: 'warning' }
      ]
    }
  },
  // 普通用户的组件会被Layout预加载，所以不在created中加载数据
  // 而是在组件被激活时（实际访问时）才加载数据
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.fetchData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'employment') {
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
        page: this.pageInfo.page,
        size: this.pageInfo.size,
        ...this.searchForm
      }
      
      console.log('获取就业信息参数:', params)
      get('/employment/list', params).then(response => {
        console.log('获取就业信息响应:', response)
        if (response && response.data && response.data.data) {
          this.employmentList = response.data.data
          this.pageInfo.total = response.data.total
        } else {
          this.employmentList = []
          this.pageInfo.total = 0
        }
        this.loading = false
      }).catch(error => {
        console.error('获取就业信息失败:', error)
        this.$message.error('获取就业信息失败')
        this.loading = false
      })
    },
    handleSearch() {
      this.pageInfo.page = 1
      this.fetchData()
    },
    handleReset() {
      this.searchForm = {
        residentName: '',
        company: '',
        industry: '',
        employmentStatus: ''
      }
      this.pageInfo.page = 1
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增就业信息'
      // 先关闭对话框，确保完全重置
      this.dialogVisible = false
      
      // 使用$nextTick确保DOM更新完成后再重新打开对话框
      this.$nextTick(() => {
        // 完全重置表单数据
        this.employmentForm = {
          id: null,
          residentId: null,
          residentName: '',
          idCard: '',
          company: '',
          position: '',
          salary: null,
          employmentStatus: '',
          industry: '',
          startDate: '',
          contractType: '',
          notes: ''
        }
        
        // 重新打开对话框
        this.dialogVisible = true
        
        // 确保表单重置
        this.$nextTick(() => {
          if (this.$refs.employmentForm) {
            this.$refs.employmentForm.clearValidate()
            this.$refs.employmentForm.resetFields()
          }
        })
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑就业信息'
      this.employmentForm = { ...row }
      this.dialogVisible = true
      // TODO: 跳转到编辑页面
    },
    // 验证居民姓名
    validateResidentName() {
      if (!this.employmentForm.residentName || !this.employmentForm.residentName.trim()) {
        this.$message.warning('请填写居民姓名')
      }
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.employmentForm.idCard && this.employmentForm.idCard.trim()) {
        const idCard = this.employmentForm.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.employmentForm.residentName || !this.employmentForm.residentName.trim()) {
          this.$message.warning('请先填写居民姓名')
          return
        }
        
        // 根据身份证号验证居民是否存在
        try {
          this.loading = true
          // 这里需要调用获取居民信息的接口
          const residentResponse = await get(`/resident/idCard/${idCard}`)
          if (residentResponse && residentResponse.code === 200 && residentResponse.data) {
            const resident = residentResponse.data
            // 验证居民姓名是否匹配
            if (resident.name && resident.name.trim() === this.employmentForm.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.employmentForm.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.employmentForm.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.employmentForm.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    
    async handleSubmit() {
      this.$refs.employmentForm.validate(async (valid) => {
        if (valid) {
          try {
            this.loading = true
            
            // 准备表单数据
            const formData = { ...this.employmentForm }
            
            // 处理日期格式：参照医疗管理的方式，直接发送字符串格式的日期
            if (formData.startDate) {
              // 如果是Date对象，转换为YYYY-MM-DD格式字符串
              if (formData.startDate instanceof Date) {
                formData.startDate = formData.startDate.toISOString().split('T')[0]
              }
              // 如果是字符串，确保格式正确（YYYY-MM-DD）
              else if (typeof formData.startDate === 'string') {
                // 验证日期格式，如果不符合YYYY-MM-DD格式，尝试转换
                const dateRegex = /^\d{4}-\d{2}-\d{2}$/
                if (!dateRegex.test(formData.startDate)) {
                  const dateObj = new Date(formData.startDate)
                  if (!isNaN(dateObj.getTime())) {
                    formData.startDate = dateObj.toISOString().split('T')[0]
                  }
                }
              }
            }
            
            // 验证居民信息是否存在且匹配
            if (!formData.id && formData.idCard) {
              try {
                console.log('正在根据身份证号获取居民ID:', formData.idCard)
                const residentResponse = await get(`/resident/idCard/${formData.idCard}`)
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
            
            let response
            if (formData.id) {
              // 编辑
              response = await put('/employment/update', formData)
            } else {
              // 新增
              response = await post('/employment/add', formData)
            }
            
            if (response && response.code === 200) {
              this.$message.success(formData.id ? '修改成功' : '新增成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            } else {
              console.error('响应错误:', response)
              this.$message.error(response ? (response.message || (formData.id ? '修改失败' : '新增失败')) : '无响应数据')
            }
          } catch (error) {
            console.error(this.employmentForm.id ? '修改失败:' : '新增失败:', error)
            console.error('错误详情:', error.response || error.message || error)
            let errorMsg = error.message || '未知错误'
            if (error.response && error.response.data && error.response.data.message) {
              errorMsg = error.response.data.message
            }
            this.$message.error(this.employmentForm.id ? '修改失败: ' + errorMsg : '新增失败: ' + errorMsg)
          } finally {
            this.loading = false
          }
        }
      })
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm('此操作将永久删除该就业信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        this.loading = true
        const response = await del(`/employment/delete/${row.id}`)
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
        
        console.error('删除就业信息失败:', error)
        
        // 检查是否为关联数据错误（409状态码）
        if (error.response && error.response.status === 409) {
          // 设置错误信息和关联数据
          this.deleteErrorInfo = error.response.data.message || '删除失败，存在关联数据'
          this.deleteRelatedData = error.response.data.relatedData || []
          // 显示删除错误对话框
          this.deleteErrorDialogVisible = true
        } else {
          // 其他错误使用DeleteErrorHandler处理
          await DeleteErrorHandler.handleDeleteError(error, {
            solution: '请检查该就业信息是否被其他模块引用，或联系系统管理员。',
            relatedData: [],
            onViewRelated: null
          })
        }
      } finally {
        this.loading = false
      }
    },
    
    // 搜索条件检查
    checkSearchConditions() {
      const { residentName, company, industry, employmentStatus } = this.searchForm
      return residentName || company || industry || employmentStatus
    },
    
    // 处理搜索
    handleSearch() {
      if (!this.checkSearchConditions()) {
        this.$message.warning('请至少输入一个搜索条件')
        return
      }
      this.pageInfo.pageNum = 1
      this.fetchData()
    },
    
    // 处理重置
    handleReset() {
      this.searchForm = {
        residentName: '',
        company: '',
        industry: '',
        employmentStatus: ''
      }
      this.pageInfo.pageNum = 1
      this.fetchData()
    },
    
    // 获取就业状态类型
    getEmploymentStatusType(status) {
      const statusMap = {
        '在职': 'success',
        '失业': 'danger',
        '退休': 'warning'
      }
      return statusMap[status] || 'info'
    },
    getEmploymentStatusType(status) {
      const statusMap = {
        '在职': 'success',
        '失业': 'danger',
        '退休': 'warning'
      }
      return statusMap[status] || 'info'
    },
    
    // 格式化薪资
    formatSalary(salary) {
      if (salary === null || salary === undefined || isNaN(salary)) return '-'
      return parseFloat(salary).toFixed(2)
    },
    
    handleSizeChange(val) {
      this.pageInfo.size = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pageInfo.page = val
      this.fetchData()
    },
    handleDialogClose() {
      this.$refs.employmentForm.resetFields()
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'employment', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    }
  }
}
</script>

<style scoped>
.employment-management {
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 84px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 行业选择框样式 */
.industry-option-content {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
}

.industry-icon {
  color: #409EFF;
  font-size: 16px;
}

/* 就业状态选择框样式 */
.status-option-content {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 0;
}

/* 全局选择框样式 */
:deep(.industry-select-popper .el-select-dropdown__item),
:deep(.employment-status-select-popper .el-select-dropdown__item) {
  padding: 8px 16px;
  transition: all 0.3s ease;
}

:deep(.industry-select-popper .el-select-dropdown__item:hover),
:deep(.employment-status-select-popper .el-select-dropdown__item:hover) {
  background-color: #f5f7fa;
  transform: translateX(4px);
}

:deep(.industry-select-popper .el-select-dropdown__item.selected),
:deep(.employment-status-select-popper .el-select-dropdown__item.selected) {
  background-color: #ecf5ff;
  color: #409EFF;
}

/* 选择框前缀图标样式 */
:deep(.el-select .el-input__prefix) {
  display: flex;
  align-items: center;
  padding-left: 8px;
}

:deep(.el-select .el-input__prefix .el-icon) {
  color: #c0c4cc;
  font-size: 14px;
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

.employment-table {
  flex: 1;
  width: 100%;
}

.employment-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.employment-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.employment-table :deep(.el-table__row) {
  height: 48px;
}

.employment-table :deep(.el-table__cell) {
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

.employment-form {
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

  .employment-table {
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