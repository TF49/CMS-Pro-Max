<template>
  <div class="social-security-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><Medal /></el-icon>
          <span>社保管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加社保记录
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :model="searchForm" ref="searchForm" label-width="80px" class="search-form">
          <el-row :gutter="20">
            <el-col :span="6" :xs="24" :sm="12">
              <el-form-item label="居民姓名" prop="residentName">
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
              <el-form-item label="参保状态" prop="insuranceStatus">
                <el-select 
                  v-model="searchForm.insuranceStatus" 
                  placeholder="请选择参保状态" 
                  clearable
                >
                  <el-option label="正常参保" value="正常参保"></el-option>
                  <el-option label="暂停参保" value="暂停参保"></el-option>
                  <el-option label="停止参保" value="停止参保"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6" :xs="24" :sm="12">
              <el-form-item label="养老保险" prop="pensionInsurance">
                <el-select 
                  v-model="searchForm.pensionInsurance" 
                  placeholder="请选择养老保险" 
                  clearable
                >
                  <el-option label="城镇职工养老保险" value="城镇职工养老保险"></el-option>
                  <el-option label="城乡居民养老保险" value="城乡居民养老保险"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6" :xs="24" :sm="12">
              <el-form-item label="医疗保险" prop="medicalInsurance">
                <el-select 
                  v-model="searchForm.medicalInsurance" 
                  placeholder="请选择医疗保险" 
                  clearable
                >
                  <el-option label="城镇职工医疗保险" value="城镇职工医疗保险"></el-option>
                  <el-option label="城乡居民医疗保险" value="城乡居民医疗保险"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24" class="search-buttons">
              <div style="display: flex; gap: 10px; justify-content: flex-end;">
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  查询
                </el-button>
                <el-button @click="handleReset">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 社保信息列表 -->
      <div class="common-table-wrapper">
        <el-table
          :data="socialSecurityList"
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
          <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
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
          <el-table-column prop="pensionInsurance" label="养老保险" width="150"></el-table-column>
          <el-table-column prop="medicalInsurance" label="医疗保险" width="150">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.medicalInsurance"
                :type="getMedicalInsuranceType(scope.row.medicalInsurance)"
                size="small">
                {{ scope.row.medicalInsurance }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="unemploymentInsurance" label="失业保险" width="100"></el-table-column>
          <el-table-column prop="workInjuryInsurance" label="工伤保险" width="100"></el-table-column>
          <el-table-column prop="maternityInsurance" label="生育保险" width="100"></el-table-column>
          <el-table-column prop="insuranceStatus" label="参保状态" width="100">
            <template #default="scope">
              <el-tag 
                v-if="scope && scope.row && scope.row.insuranceStatus"
                :type="getInsuranceStatusType(scope.row.insuranceStatus)"
                size="small">
                {{ scope.row.insuranceStatus }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="paymentAmount" label="缴费金额(元)" width="120" align="right">
            <template #default="scope">
              <span v-if="scope && scope.row && scope.row.paymentAmount !== null && scope.row.paymentAmount !== undefined && !isNaN(scope.row.paymentAmount)">
                {{ parseFloat(scope.row.paymentAmount).toFixed(2) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="参保日期" width="100"></el-table-column>
          <el-table-column prop="endDate" label="停保日期" width="100"></el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <template v-if="scope && scope.row">
                <div class="operation-buttons">
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
        :model="socialSecurityForm" 
        :rules="socialSecurityRules" 
        ref="socialSecurityForm" 
        label-width="120px"
        class="social-security-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="居民姓名" prop="residentName">
              <el-input 
                v-model="socialSecurityForm.residentName" 
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
                v-model="socialSecurityForm.idCard" 
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
            <el-form-item label="参保状态" prop="insuranceStatus">
              <el-select 
                v-model="socialSecurityForm.insuranceStatus" 
                placeholder="请选择参保状态" 
                style="width: 100%"
              >
                <el-option label="正常参保" value="正常参保"></el-option>
                <el-option label="暂停参保" value="暂停参保"></el-option>
                <el-option label="停止参保" value="停止参保"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费金额(元)" prop="paymentAmount">
              <el-input-number 
                v-model="socialSecurityForm.paymentAmount" 
                :min="0" 
                :precision="2" 
                :step="100" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老保险" prop="pensionInsurance">
              <el-select 
                v-model="socialSecurityForm.pensionInsurance" 
                placeholder="请选择养老保险" 
                style="width: 100%"
              >
                <el-option label="城镇职工养老保险" value="城镇职工养老保险"></el-option>
                <el-option label="城乡居民养老保险" value="城乡居民养老保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="医疗保险" prop="medicalInsurance">
              <el-select 
                v-model="socialSecurityForm.medicalInsurance" 
                placeholder="请选择医疗保险" 
                style="width: 100%"
              >
                <el-option label="城镇职工医疗保险" value="城镇职工医疗保险"></el-option>
                <el-option label="城乡居民医疗保险" value="城乡居民医疗保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="失业保险" prop="unemploymentInsurance">
              <el-select 
                v-model="socialSecurityForm.unemploymentInsurance" 
                placeholder="请选择失业保险"
                clearable
              >
                <el-option label="失业保险" value="失业保险"></el-option>
                <el-option label="无失业保险" value="无失业保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工伤保险" prop="workInjuryInsurance">
              <el-select 
                v-model="socialSecurityForm.workInjuryInsurance" 
                placeholder="请选择工伤保险"
                clearable
              >
                <el-option label="工伤保险" value="工伤保险"></el-option>
                <el-option label="无工伤保险" value="无工伤保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生育保险" prop="maternityInsurance">
              <el-select 
                v-model="socialSecurityForm.maternityInsurance" 
                placeholder="请选择生育保险"
                clearable
              >
                <el-option label="生育保险" value="生育保险"></el-option>
                <el-option label="无生育保险" value="无生育保险"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="参保日期" prop="startDate">
              <el-input
                v-model="socialSecurityForm.startDate"
                placeholder="请输入参保日期（格式：YYYY-MM-DD）"
                style="width: 100%"
              >
                <template #prefix>
                  <el-icon><Calendar /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="停保日期" prop="endDate">
              <el-input
                v-model="socialSecurityForm.endDate"
                placeholder="请输入停保日期（格式：YYYY-MM-DD）"
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
                v-model="socialSecurityForm.notes">
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
      :title="'社保详情'"
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
            <el-form-item label="参保状态">
              <el-input 
                v-model="currentData.insuranceStatus" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Medal /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="缴费基数">
              <el-input 
                v-model="currentData.paymentBase" 
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
            <el-form-item label="缴费金额(元)">
              <el-input 
                v-model="currentData.paymentAmount" 
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
            <el-form-item label="养老保险">
              <el-input 
                v-model="currentData.pensionInsurance" 
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
            <el-form-item label="医疗保险">
              <el-input 
                v-model="currentData.medicalInsurance" 
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
            <el-form-item label="失业保险">
              <el-input 
                v-model="currentData.unemploymentInsurance" 
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
            <el-form-item label="工伤保险">
              <el-input 
                v-model="currentData.workInjuryInsurance" 
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
            <el-form-item label="生育保险">
              <el-input 
                v-model="currentData.maternityInsurance" 
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
            <el-form-item label="参保日期">
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
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="停保日期">
              <el-input 
                v-model="currentData.endDate" 
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
import { get, post, put, del } from '@/utils/request'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import { getResidentByName, getResidentByIdCard, getSocialSecurityData } from '@/api/data'
import { clearStatisticsCache } from '@/api/system'
import websocketService from '@/utils/websocket'
import { Plus, Delete, Search, Refresh, User, Edit, Close, Check, View, Medal, Calendar, Document, Clock } from '@element-plus/icons-vue'

export default {
  name: 'SocialSecurityManagement',
  components: {
    DeleteErrorDialog,
    Plus,
    Delete,
    Search,
    Refresh,
    User,
    Edit,
    Close,
    Check,
    View,
    Medal,
    Calendar,
    Document,
    Clock
  },
  data() {
      // 日期验证规则
      const validateSocialSecurityDate = (rule, value, callback) => {
        if (!value) {
          // 日期为空时，验证通过（停保日期可选）
          callback();
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
              callback(new Error('日期不能晚于今天'));
            } else {
              callback();
            }
          }
        }
      };
    
    return {
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      socialSecurityList: [],
      searchForm: {
        residentName: '',
        insuranceStatus: '',
        pensionInsurance: '',
        medicalInsurance: ''
      },
      socialSecurityForm: {
        id: null,
        residentId: null,
        residentName: '',
        idCard: '',
        pensionInsurance: '',
        medicalInsurance: '',
        unemploymentInsurance: '',
        workInjuryInsurance: '',
        maternityInsurance: '',
        insuranceStatus: '',
        paymentAmount: null,
        startDate: '',
        endDate: '',
        notes: ''
      },
      socialSecurityRules: {
        residentName: [
          { required: true, message: '请输入居民姓名', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号码', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号码格式不正确', trigger: 'blur' }
        ],
        insuranceStatus: [
          { required: true, message: '请选择参保状态', trigger: 'change' }
        ],
        pensionInsurance: [
          { required: true, message: '请选择养老保险', trigger: 'change' }
        ],
        medicalInsurance: [
          { required: true, message: '请选择医疗保险', trigger: 'change' }
        ],
        unemploymentInsurance: [
          { required: true, message: '请选择失业保险', trigger: 'change' }
        ],
        workInjuryInsurance: [
          { required: true, message: '请选择工伤保险', trigger: 'change' }
        ],
        maternityInsurance: [
          { required: true, message: '请选择生育保险', trigger: 'change' }
        ],
        paymentAmount: [
          { required: true, message: '请输入缴费金额', trigger: 'change' },
          { type: 'number', min: 0, message: '缴费金额不能为负数', trigger: 'change' }
        ],
        startDate: [
          { required: true, message: '请选择参保日期', trigger: 'change' },
          { validator: validateSocialSecurityDate, trigger: 'change' }
        ],
        endDate: [
          { validator: validateSocialSecurityDate, trigger: 'change', required: false }
        ]
      },
      pageInfo: {
        page: 1,
        size: 10,
        total: 0
      },
      // 删除错误对话框相关属性
      deleteErrorDialogVisible: false,
      deleteErrorInfo: '',
      deleteRelatedData: []
    }
  },
  // 普通用户的组件会被Layout预加载，所以不在created中加载数据
  // 而是在组件被激活时（实际访问时）才加载数据
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.fetchData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'socialSecurity') {
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
        ...this.searchForm,
        page: this.pageInfo.page,
        size: this.pageInfo.size
      }
      get('/social-security/list', params).then(response => {
        this.socialSecurityList = response.data.data
        this.pageInfo.total = response.data.total
        this.loading = false
      }).catch(error => {
        console.error('获取社保信息失败:', error)
        this.$message.error('获取社保信息失败')
        this.loading = false
      })
    },
    handleSearch() {
      this.pageInfo.page = 1
      this.fetchData()
    },
    handleReset() {
      // 显式重置搜索表单字段
      this.searchForm = {
        residentName: '',
        idCard: '',
        insuranceStatus: ''
      }
      // 重置页码
      this.pageInfo.page = 1
      // 重新获取数据，重置表格内容
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增社保信息'
      this.socialSecurityForm = {
        id: undefined,
        residentId: undefined,
        residentName: '',
        idCard: '',
        pensionInsurance: '',
        medicalInsurance: '',
        unemploymentInsurance: '',
        workInjuryInsurance: '',
        maternityInsurance: '',
        insuranceStatus: '',
        paymentAmount: undefined,
        startDate: '',
        endDate: '',
        notes: ''
      }
      this.dialogVisible = true
      // 确保表单重置
      this.$nextTick(() => {
        if (this.$refs.socialSecurityForm) {
          this.$refs.socialSecurityForm.clearValidate()
        }
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑社保信息'
      this.socialSecurityForm = { ...row }
      this.dialogVisible = true
    },
    
    // 处理身份证号输入事件
    async handleIdCardInput() {
      // 当身份证号输入框失去焦点时，验证居民是否存在
      if (this.socialSecurityForm.idCard && this.socialSecurityForm.idCard.trim()) {
        const idCard = this.socialSecurityForm.idCard.trim()
        
        // 验证居民姓名是否已填写
        if (!this.socialSecurityForm.residentName || !this.socialSecurityForm.residentName.trim()) {
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
            if (resident.name && resident.name.trim() === this.socialSecurityForm.residentName.trim()) {
              this.$message.success('居民信息验证通过')
            } else {
              this.$message.error('居民姓名与身份证号不匹配，请检查输入')
              this.socialSecurityForm.residentName = ''
            }
          } else {
            this.$message.error('未找到身份证号为 "' + idCard + '" 的居民信息，请检查身份证号是否正确')
            this.socialSecurityForm.residentName = ''
          }
        } catch (error) {
          console.error('验证居民信息失败:', error)
          this.$message.error('验证居民信息失败，请稍后重试')
          this.socialSecurityForm.residentName = ''
        } finally {
          this.loading = false
        }
      }
    },
    async handleSubmit() {
      this.$refs.socialSecurityForm.validate(async (valid) => {
        if (valid) {
          const formData = { ...this.socialSecurityForm }
          
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
          
          // 格式化参保日期和停保日期
          formData.startDate = formatDate(formData.startDate)
          formData.endDate = formatDate(formData.endDate)
          
          // 验证居民是否存在且匹配
          if (!formData.id && formData.idCard) {
            try {
              // 根据身份证号获取居民ID
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
          
          if (formData.id) {
            // 编辑
            put('/social-security/update', formData).then(response => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('修改社保信息失败:', error)
              this.$message.error('修改社保信息失败')
            })
          } else {
            // 新增
            post('/social-security/add', formData).then(response => {
              this.$message.success('新增成功')
              this.dialogVisible = false
              this.fetchData()
              // 清除统计缓存，确保首页统计数据实时更新
              clearStatisticsCache().then(() => {
                console.log('统计缓存已清除');
              });
            }).catch(error => {
              console.error('新增社保信息失败:', error)
              this.$message.error('新增社保信息失败')
            })
          }
        }
      })
    },
    async handleDelete(row) {
      try {
        this.loading = true
        await this.$confirm('此操作将永久删除该社保信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        await del(`/social-security/delete/${row.id}`)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error === 'cancel') {
          this.$message.info('已取消删除')
        } else if (error.response && error.response.status === 409) {
          // 关联数据错误
          this.deleteErrorInfo = error.response.data.message || '删除失败，存在关联数据'
          this.deleteRelatedData = error.response.data.relatedData || []
          this.deleteErrorDialogVisible = true
        } else {
          console.error('删除社保信息失败:', error)
          DeleteErrorHandler.handleDeleteError(error, '社保信息')
        }
      } finally {
        this.loading = false
      }
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
      this.$refs.socialSecurityForm.resetFields()
    },
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'social-security', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    getMedicalInsuranceType(type) {
      const typeMap = {
        '城镇职工医疗保险': 'primary',
        '城乡居民医疗保险': 'success'
      }
      return typeMap[type] || ''
    },
    
    getInsuranceStatusType(status) {
      const statusMap = {
        '正常参保': 'success',
        '暂停参保': 'warning',
        '停止参保': 'danger'
      }
      return statusMap[status] || ''
    }
  }
}
</script>

<style scoped>
.social-security-management {
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

.social-security-table {
  flex: 1;
  width: 100%;
}

.social-security-table :deep(.el-table__header-wrapper) {
  border-bottom: 1px solid #ebeef5;
}

.social-security-table :deep(.el-table__body-wrapper) {
  flex: 1;
}

.social-security-table :deep(.el-table__row) {
  height: 48px;
}

.social-security-table :deep(.el-table__cell) {
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

.social-security-form {
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

  .social-security-table {
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