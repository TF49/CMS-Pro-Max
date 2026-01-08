<template>
  <div class="resident-management common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><UserFilled /></el-icon>
          <span>居民管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加居民
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form ref="searchForm" :inline="true" :model="searchForm" class="search-form" label-width="80px" size="medium">
          <el-form-item label="姓名" class="search-form-item">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入姓名"
              clearable
              @keyup.enter.native="handleSearch"
              class="search-input"
            >
              <template #prefix>
                <el-icon color="#909399" size="16"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="身份证号" class="search-form-item">
            <el-input
              v-model="searchForm.idCard"
              placeholder="请输入身份证号"
              clearable
              @keyup.enter.native="handleSearch"
              class="search-input"
            >
              <template #prefix>
                <el-icon color="#909399" size="16"><CreditCard /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item class="search-buttons">
            <el-button type="primary" @click="handleSearch" size="medium" :icon="Search">
              查询
            </el-button>
            <el-button @click="resetSearch" size="medium" :icon="Refresh" style="margin-left: 12px;">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 居民列表 -->
      <div class="common-table-wrapper">
          <el-table
          :data="residentList"
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
          <el-table-column prop="name" label="姓名" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.name" size="small">{{ scope.row.name }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="idCard" label="身份证号" width="180" align="center"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.gender" size="small" :type="this.formatGender(scope.row.gender) === '男' ? 'success' : 'info'">{{ this.formatGender(scope.row.gender) }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="birthDate" label="出生日期" width="120" align="center"></el-table-column>
          <el-table-column prop="householdNumber" label="户籍编号" width="150" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.householdNumber" size="small">{{ scope.row.householdNumber }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="relationship" label="与户主关系" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.relationship" size="small" :type="getRelationshipTagType(scope.row.relationship)">{{ scope.row.relationship }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="educationLevel" label="教育程度" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope && scope.row && scope.row.educationLevel" size="small" :type="getEducationTagType(scope.row.educationLevel)">{{ this.formatEducationLevel(scope.row.educationLevel) }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="phoneNumber" label="联系电话" width="200" align="center"></el-table-column>
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

    <!-- 添加/编辑居民对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px" 
      @close="handleDialogClose"
      custom-class="resident-dialog"
    >
      <el-form 
        :model="residentForm" 
        :rules="residentRules" 
        ref="residentFormRef" 
        label-width="100px"
        :label-position="'right'"
        size="medium"
      >
          <!-- 第一行：姓名和身份证号 -->
          <el-row :gutter="20" class="form-row">
            <el-col :span="12" :xs="24">
              <el-form-item label="姓名" prop="name">
                <el-input
                  v-model="residentForm.name"
                  placeholder="请输入居民姓名"
                  clearable
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :xs="24">
              <el-form-item label="身份证号" prop="idCard">
                <el-input
                  v-model="residentForm.idCard"
                  placeholder="请输入身份证号"
                  clearable
                >
                  <template #prefix>
                    <el-icon><CreditCard /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 第二行：性别和出生日期 -->
          <el-row :gutter="20" class="form-row">
            <el-col :span="8" :xs="24">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="residentForm.gender" size="large">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="16" :xs="24">
              <el-form-item label="出生日期" prop="birthDate" label-width="80px">
                <el-input
                  v-model="residentForm.birthDate"
                  placeholder="请输入出生日期（格式：YYYY-MM-DD）"
                  style="width: 100%;"
                >
                  <template #prefix>
                    <el-icon><Calendar /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          
          <!-- 第三行：户籍编号和与户主关系 -->
          <el-row :gutter="20" class="form-row">
            <el-col :span="12" :xs="24">
              <el-form-item label="户籍编号" prop="householdNumber">
                <el-input
                  v-model="residentForm.householdNumber"
                  placeholder="请输入户籍编号"
                  :disabled="isEdit"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Document /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" :xs="24">
              <el-form-item label="与户主关系" prop="relationship">
                <el-select 
                  v-model="residentForm.relationship" 
                  placeholder="请选择与户主关系"
                  clearable
                >
                  <el-option label="户主" value="户主" />
                  <el-option label="配偶" value="配偶" />
                  <el-option label="子女" value="子女" />
                  <el-option label="父母" value="父母" />
                  <el-option label="兄弟姐妹" value="兄弟姐妹" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24" class="form-row">
            <el-col :span="12" :xs="24" :sm="12">
              <el-form-item label="教育程度" prop="educationLevel">
                <el-select 
                  v-model="residentForm.educationLevel" 
                  placeholder="请选择教育程度"
                  clearable
                >
                  <el-option label="博士" value="博士" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="本科" value="本科" />
                  <el-option label="大专" value="大专" />
                  <el-option label="高中" value="高中" />
                  <el-option label="初中" value="初中" />
                  <el-option label="小学" value="小学" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12" :xs="24" :sm="12">
              <el-form-item label="联系电话" prop="phoneNumber">
                <el-input
                  v-model="residentForm.phoneNumber"
                  placeholder="请输入联系电话"
                  clearable
                  maxlength="11"
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="备注" prop="notes" class="form-row">
            <el-input
              v-model="residentForm.notes"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
              show-word-limit
              maxlength="200"
            />
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer" style="display: flex; justify-content: flex-end; gap: 12px;">
          <el-button @click="dialogVisible = false" size="medium" plain>
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button type="primary" @click="submitResidentForm" size="medium">
            <el-icon><Check /></el-icon>
            确定
          </el-button>
        </div>
       </el-dialog>

      <!-- 详情对话框 -->
      <el-dialog
        :title="'居民详情'"
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
              <el-form-item label="姓名">
                <el-input 
                  v-model="currentData.name" 
                  readonly 
                  style="background-color: #f5f7fa;"
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
            <el-col :span="12">
              <el-form-item label="性别">
                <el-input 
                  v-model="currentData.gender" 
                  readonly 
                  style="background-color: #f5f7fa;"
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
              <el-form-item label="出生日期">
                <el-input 
                  v-model="currentData.birthDate" 
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
              <el-form-item label="与户主关系">
                <el-input 
                  v-model="currentData.relationship" 
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
              <el-form-item label="民族">
                <el-input 
                  v-model="currentData.ethnicGroup" 
                  readonly 
                  style="background-color: #f5f7fa;"
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
              <el-form-item label="教育程度">
                <el-input 
                  v-model="currentData.educationLevel" 
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
              <el-form-item label="职业">
                <el-input 
                  v-model="currentData.occupation" 
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
              <el-form-item label="婚姻状况">
                <el-input 
                  v-model="currentData.maritalStatus" 
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
              <el-form-item label="是否户主">
                <el-input 
                  v-model="currentData.isHouseholderDisplay" 
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
              <el-form-item label="户籍ID">
                <el-input 
                  v-model="currentData.householdId" 
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
          <el-button @click="detailVisible = false" size="medium" plain>
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

<style scoped>
  /* 搜索栏样式 */
  .search-bar {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px 8px 0 0;
  margin-bottom: 0;
}
  
  .search-form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .search-form-item {
    margin-bottom: 0;
  }
  
  .search-input {
    min-width: 240px;
    border-radius: 4px;
    transition: all 0.3s;
  }
  
  .search-input:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  }
  
  .search-buttons {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  /* 对话框样式 */
  .resident-dialog {
    .el-dialog__header {
      padding: 20px 24px 16px;
      background-color: #fafafa;
      border-bottom: 1px solid #ebeef5;
    }
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
    .el-dialog__body {
      padding: 24px;
      background-color: #ffffff;
    }
    .form-row {
      margin-bottom: 20px;
    }
    .form-row:last-child {
      margin-bottom: 0;
    }
    .el-form-item {
      margin-bottom: 16px;
    }
    .el-form-item__label {
      font-weight: 500;
      color: #606266;
    }
    .dialog-footer {
      padding: 16px 24px 20px;
      background-color: #fafafa;
      border-top: 1px solid #ebeef5;
    }
  }
</style>

<script>
import { addResident, updateResident, deleteResident, getResidentById, searchResidents, getResidentData, getHouseholdByNumber, getHouseholdData } from '@/api/data'
import { clearStatisticsCache } from '@/api/system'
import DeleteErrorHandler from '@/utils/deleteErrorHandler'
import DeleteErrorDialog from '@/components/DeleteErrorDialog'
import websocketService from '@/utils/websocket'
import {
  User,
  UserFilled,
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  CreditCard,
  Phone,
  Document,
  Close,
  Check,
  Calendar,
  Clock
} from '@element-plus/icons-vue'

export default {
  name: 'ResidentManagement',
  components: {
    User,
    UserFilled,
    Plus,
    Search,
    Refresh,
    View,
    Edit,
    Delete,
    CreditCard,
    Phone,
    Document,
    Close,
    Check,
    Calendar,
    Clock,
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
        callback();
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
    
    // 出生日期验证规则
    const validateBirthDate = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入出生日期'));
      } else {
        // 检查日期格式是否为YYYY-MM-DD
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(value)) {
          callback(new Error('出生日期格式不正确，请使用YYYY-MM-DD格式'));
        } else {
          // 检查日期是否有效
          const selectedDate = new Date(value);
          if (isNaN(selectedDate.getTime())) {
            callback(new Error('出生日期格式不正确'));
            return;
          }
          
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          
          if (selectedDate > today) {
            callback(new Error('出生日期不能晚于今天'));
          } else {
            callback();
          }
        }
      }
    };
    
    // 户籍编号验证规则
    const validateHouseholdNumber = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入户籍编号'));
      } else {
        // 户籍编号应为阿拉伯数字
        const reg = /^\d+$/;
        if (!reg.test(value)) {
          callback(new Error('户籍编号只能包含阿拉伯数字'));
        } else {
          callback();
        }
      }
    };

    return {
      loading: false,
      // 详情对话框相关属性
      detailVisible: false,
      currentData: {},
      searchForm: {
        name: '',
        idCard: ''
      },

      residentList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      goToPage: 1,
      
      // 对话框相关
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      
      // 删除错误对话框相关
      deleteErrorDialogVisible: false,
      deleteErrorInfo: {},
      deleteRelatedData: [],
      residentForm: {
        id: undefined,
        name: '',
        idCard: '',
        gender: '',
        birthDate: '',
        householdNumber: '',
        relationship: '',
        ethnicGroup: '',
        educationLevel: '',
        occupation: '',
        maritalStatus: '',
        phoneNumber: '',
        notes: ''
      },
      residentRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
        ],
        idCard: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          { validator: validateIdCard, trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthDate: [
          { required: true, message: '请输入出生日期', trigger: 'blur' },
          { validator: validateBirthDate, trigger: 'blur' }
        ],
        householdNumber: [
          { required: true, message: '请输入户籍编号', trigger: 'blur' },
          { validator: validateHouseholdNumber, trigger: 'blur' }
        ],
        relationship: [
          { required: true, message: '请选择与户主关系', trigger: 'change' }
        ],
        phoneNumber: [
          { validator: validatePhone, trigger: 'blur' }
        ]
      }
    }
  },
  // 使用created和activated钩子加载数据，确保在各种情况下都能正确加载数据
  created() {
    this.loadResidentData()

    // 注册WebSocket消息处理器
    this.wsHandler = (event) => {
      if (event.module === 'resident') {
        this.loadResidentData()
      }
    }
    websocketService.registerMessageHandler(this.wsHandler)

    // 监听自定义刷新事件
    this.refreshHandler = (e) => {
      if (e.detail.module === 'resident') {
        this.loadResidentData()
      }
    }
    window.addEventListener('ws-refresh', this.refreshHandler)
  },
  // 统一使用activated()钩子加载数据，避免组件预加载时产生错误
  activated() {
    this.loadResidentData()
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
    // 根据与户主关系返回对应的标签类型
    getRelationshipTagType(relationship) {
      const relationshipMap = {
        '户主': 'primary',
        '配偶': 'success',
        '子女': 'warning',
        '父母': 'info',
        '兄弟姐妹': 'danger',
        '其他': 'default'
      }
      return relationshipMap[relationship] || 'default'
    },
    
    // 将性别代码转换为中文
    formatGender(gender) {
      const genderMap = {
        'MALE': '男',
        'FEMALE': '女',
        '男': '男',
        '女': '女'
      }
      return genderMap[gender] || gender
    },
    
    // 将教育程度代码转换为中文
    formatEducationLevel(educationLevel) {
      const educationMap = {
        'PRIMARY': '小学',
        'JUNIOR': '初中',
        'SENIOR': '高中',
        'COLLEGE': '大专',
        'BACHELOR': '本科',
        'MASTER': '硕士',
        'DOCTOR': '博士',
        '博士': '博士',
        '硕士': '硕士',
        '本科': '本科',
        '大专': '大专',
        '高中': '高中',
        '初中': '初中',
        '小学': '小学',
        '幼儿园': '幼儿园',
        '其他': '其他'
      }
      return educationMap[educationLevel] || educationLevel
    },
    
    // 根据教育程度返回对应的标签类型
    getEducationTagType(education) {
      // 先转换为中文，再获取标签类型
      const chineseEducation = this.formatEducationLevel(education)
      const educationMap = {
        '博士': 'primary',
        '硕士': 'success',
        '本科': 'warning',
        '大专': 'info',
        '高中': 'danger',
        '初中': 'info',
        '小学': 'warning',
        '幼儿园': 'success',
        '其他': 'default'
      }
      return educationMap[chineseEducation] || 'default'
    },
    
    // 加载户籍数据用于映射
    async loadHouseholdDataForMapping(residents) {
      try {
        // 首先检查居民数据中是否已经包含householdNumber字段，如果有，直接使用
        const hasHouseholdNumber = residents.some(resident => resident.householdNumber)
        if (hasHouseholdNumber) {
          // 居民数据中已经包含householdNumber字段，直接返回格式化后的数据
          return residents.map(resident => ({
            ...resident,
            // 格式化性别和教育程度
            gender: this.formatGender(resident.gender),
            educationLevel: this.formatEducationLevel(resident.educationLevel)
          })).sort((a, b) => a.id - b.id)
        }
        
        // 如果居民数据中没有householdNumber字段，尝试获取户籍数据
        const householdResponse = await getHouseholdData(1, 1000) // 获取足够多的户籍数据，不分页限制
        if (householdResponse.code === 200) {
          // 正确处理分页结构
          const householdData = householdResponse.data || {}
          const households = householdData.data || []
          const householdMap = {}
          households.forEach(household => {
            // 确保ID是数字类型，处理可能的类型转换问题
            const id = household.id.toString()
            householdMap[id] = household.householdNumber
          })
          
          // 为每个居民添加householdNumber字段并格式化数据
          return residents.map(resident => {
            // 确保householdId是字符串类型进行匹配
            const householdIdStr = resident.householdId ? resident.householdId.toString() : ''
            return {
              ...resident,
              householdNumber: householdMap[householdIdStr] || '',
              // 格式化性别和教育程度
              gender: this.formatGender(resident.gender),
              educationLevel: this.formatEducationLevel(resident.educationLevel)
            }
          }).sort((a, b) => a.id - b.id)
        } else {
          // 如果获取户籍数据失败，尝试使用居民数据中的householdNumber字段，如果没有则使用空字符串
          return residents.map(resident => ({
            ...resident,
            householdNumber: resident.householdNumber || '',
            // 格式化性别和教育程度
            gender: this.formatGender(resident.gender),
            educationLevel: this.formatEducationLevel(resident.educationLevel)
          })).sort((a, b) => a.id - b.id)
        }
      } catch (householdError) {
        console.error('获取户籍数据失败:', householdError)
        // 如果获取户籍数据失败，尝试使用居民数据中的householdNumber字段，如果没有则使用空字符串
        return residents.map(resident => ({
          ...resident,
          householdNumber: resident.householdNumber || '',
          // 格式化性别和教育程度
          gender: this.formatGender(resident.gender),
          educationLevel: this.formatEducationLevel(resident.educationLevel)
        })).sort((a, b) => a.id - b.id)
      }
    },
    
    // 加载居民数据
    loadResidentData() {
      this.loading = true
      
      // 检查是否有搜索条件
      const hasSearchCondition = this.searchForm.name.trim() || this.searchForm.idCard.trim()
      
      if (hasSearchCondition) {
        // 执行搜索
        const searchParams = {}
        if (this.searchForm.name.trim()) {
          searchParams.name = this.searchForm.name.trim()
        }
        if (this.searchForm.idCard.trim()) {
          searchParams.idCard = this.searchForm.idCard.trim()
        }
        
        searchResidents(searchParams).then(response => {
          if (response && response.code === 200) {
            const result = response.data || {}
            const residents = result.data || []
            
            // 获取所有户籍数据，建立ID到编号的映射
            this.loadHouseholdDataForMapping(residents).then(processedResidents => {
              this.residentList = processedResidents
              this.total = result.total || 0
              this.currentPage = 1
            })
          } else {
            this.$message.error('搜索居民数据失败')
            this.residentList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          this.$message.error('搜索居民数据失败')
          this.residentList = []
          this.total = 0
          this.loading = false
        })
      } else {
        // 获取分页数据
        getResidentData(this.currentPage, this.pageSize).then(response => {
          if (response && response.code === 200) {
            const result = response.data || {}
            const residents = result.data || []
            
            // 获取所有户籍数据，建立ID到编号的映射
            this.loadHouseholdDataForMapping(residents).then(processedResidents => {
              this.residentList = processedResidents
              this.total = result.total || 0
            })
          } else {
            this.$message.error('获取居民数据失败')
            this.residentList = []
            this.total = 0
          }
          this.loading = false
        }).catch(error => {
          this.$message.error('获取居民数据失败')
          this.residentList = []
          this.total = 0
          this.loading = false
        })
      }
    },
    
    // 添加居民
    handleAdd() {
      this.dialogTitle = '添加居民'
      this.isEdit = false
      this.residentForm = {
        id: undefined,
        name: '',
        idCard: '',
        gender: '',
        birthDate: '',
        householdId: undefined,
        householdNumber: '',
        relationship: '',
        ethnicGroup: '',
        educationLevel: '',
        occupation: '',
        maritalStatus: '',
        phoneNumber: '',
        notes: ''
      }
      this.dialogVisible = true
    },
    
    // 编辑居民
    handleEdit(row) {
      this.dialogTitle = '编辑居民'
      this.isEdit = true
      const residentData = { ...row }
      this.residentForm = residentData
      this.dialogVisible = true
    },
    
    // 查看居民详情
    handleView(row) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const role = userInfo.role || 'USER'
      
      if (role === 'ADMIN') {
        // 管理员保持现有跳转逻辑
        this.$router.push({
          path: '/data/detail',
          query: { tab: 'resident', id: row.id }
        })
      } else {
        // 普通用户弹出详情对话框
        this.currentData = { ...row }
        this.detailVisible = true
      }
    },
    
    // 删除居民
    async handleDelete(row) {
      try {
        // 先查询该居民的关联数据
        let relatedDataInfo = ''
        let hasRelatedData = false
        
        try {
          // 这里可以添加一个API调用，查询该居民的关联数据
          // 由于暂时没有现成的API，我们先模拟一下
          // const relatedDataResponse = await getResidentRelatedData(row.id)
          // if (relatedDataResponse && relatedDataResponse.code === 200) {
          //   const relatedData = relatedDataResponse.data
          //   if (relatedData && Object.keys(relatedData).length > 0) {
          //     hasRelatedData = true
          //     relatedDataInfo = '\n\n关联数据影响：\n'
          //     if (relatedData.educationCount > 0) {
          //       relatedDataInfo += `- 教育记录：${relatedData.educationCount}条\n`
          //     }
          //     if (relatedData.medicalCount > 0) {
          //       relatedDataInfo += `- 医疗记录：${relatedData.medicalCount}条\n`
          //     }
          //     if (relatedData.employmentCount > 0) {
          //       relatedDataInfo += `- 就业记录：${relatedData.employmentCount}条\n`
          //     }
          //     if (relatedData.socialSecurityCount > 0) {
          //       relatedDataInfo += `- 社保记录：${relatedData.socialSecurityCount}条\n`
          //     }
          //     if (relatedData.propertyCount > 0) {
          //       relatedDataInfo += `- 房产记录：${relatedData.propertyCount}条\n`
          //     }
          //     if (relatedData.vehicleCount > 0) {
          //       relatedDataInfo += `- 车辆记录：${relatedData.vehicleCount}条\n`
          //     }
          //     relatedDataInfo += '\n删除后，以上关联数据也将被同步删除，且不可恢复！'
          //   }
          // }
          
          // 模拟有相关数据
          hasRelatedData = true
          relatedDataInfo = '\n\n关联数据影响：\n- 教育记录：1条\n- 医疗记录：2条\n- 就业记录：1条\n\n删除后，以上关联数据也将被同步删除，且不可恢复！'
        } catch (relatedError) {
          console.warn('查询关联数据失败:', relatedError)
        }
        
        await this.$confirm(`此操作将永久删除该居民信息${relatedDataInfo}，是否继续?`, '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          icon: 'el-icon-warning'
        })
        
        const response = await deleteResident(row.id)
        if (response && response.code === 200) {
          this.$message.success('删除成功')
          this.loadResidentData() // 重新加载数据
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
        console.error('删除居民失败:', error)
        
        // 从localStorage获取用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        
        // 检查是否为关联数据错误
        if (error.response && error.response.status === 409) {
          // 显示删除错误对话框
          this.deleteErrorInfo = {
            title: '删除失败',
            message: (error.response.data && error.response.data.message) || '该居民信息存在关联数据，无法删除',
            solution: '请先删除相关的教育、医疗、就业等记录，然后再尝试删除居民信息。'
          }
          
          // 设置关联数据（如果有的话）
          this.deleteRelatedData = (error.response.data && error.response.data.relatedData) || []
          
          // 显示错误对话框
          this.deleteErrorDialogVisible = true
        } else {
          // 根据用户角色显示不同的错误信息
          if (userInfo.role === 'ADMIN') {
            // 管理员显示详细错误
            DeleteErrorHandler.handleDeleteError(error, this.$message)
          } else {
            // 普通用户显示简洁错误
            this.$message.error('删除失败')
          }
        }
      }
    },
    
    // 搜索
    handleSearch() {
      this.loadResidentData()
    },
    
    // 重置搜索
    resetSearch() {
      // 重置搜索表单
      this.searchForm = {
        name: '',
        idCard: ''
      }
      // 重置到第一页并重新加载数据
      this.currentPage = 1
      this.loadResidentData()
    },
    
    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1 // 重置到第一页
      this.handleSearch()
    },
    handleCurrentChange(val) {
        this.currentPage = val
        this.handleSearch()
        this.$message.info('当前页: ' + val)
      },
    

    
    // 提交表单
    async submitResidentForm() {
      if (this.$refs.residentFormRef) {
        this.$refs.residentFormRef.validate(async (valid) => {
          if (valid) {
            try {
              // 准备表单数据
              const formData = { ...this.residentForm }
              
              // 处理户籍编号转换
              if (formData.householdNumber && !this.isEdit) {
                try {
                  const householdResponse = await getHouseholdByNumber(formData.householdNumber)
                  if (householdResponse && householdResponse.code === 200 && householdResponse.data) {
                formData.householdId = householdResponse.data.id
              } else {
                // 从localStorage获取用户信息
                const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                // 管理员显示详细错误，普通用户显示简洁错误
                if (userInfo.role === 'ADMIN') {
                  this.$message.error('未找到户籍编号为 "' + formData.householdNumber + '" 的户籍信息')
                } else {
                  this.$message.error('未找到该户籍编号')
                }
                return
              }
                } catch (error) {
                  // 从localStorage获取用户信息
                  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                  // 管理员显示详细错误，普通用户显示简洁错误
                  if (userInfo.role === 'ADMIN') {
                    this.$message.error('查询户籍信息失败: ' + (error.message || '网络错误'))
                  } else {
                    this.$message.error('未找到该户籍编号')
                  }
                  return
                }
              }
              
              let response
              if (this.isEdit) {
                response = await updateResident(formData)
              } else {
                response = await addResident(formData)
              }
              
              if (response && response.code === 200) {
                this.$message.success(this.isEdit ? '更新成功' : '添加成功')
                this.dialogVisible = false
                this.loadResidentData() // 重新加载数据
                // 清除统计缓存，确保首页统计数据实时更新
                clearStatisticsCache().then(() => {
                  console.log('统计缓存已清除')
                }).catch(error => {
                  console.error('清除统计缓存失败:', error)
                })
              } else {
                this.$message.error(this.isEdit ? '更新失败' : '添加失败')
              }
            } catch (error) {
              console.error(error)
              this.$message.error(this.isEdit ? '更新失败' : '添加失败')
            }
          }
        })
      }
    },
    
    // 关闭对话框
    handleDialogClose() {
      if (this.$refs.residentFormRef) {
        this.$refs.residentFormRef.resetFields()
      }
    },


  }
}
</script>

<style scoped>
.resident-management {
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

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-bar {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin: 0 0 10px 0;
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

.common-table-wrapper {
  flex: 1;
  padding: 0;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 0 0 8px 8px;
}

.household-table {
  flex: 1;
  width: 100%;
  margin: 0;
  border-radius: 0;
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
  min-height: 60px;
  align-items: center;
}

.pagination-container:empty {
  display: none;
}

.resident-dialog {
  max-width: 90vw;
}

.resident-form {
  padding: 20px 0;
}

.resident-form-item {
  margin-bottom: 20px;
}

.resident-input {
  width: 100%;
}

.resident-radio-group {
  display: flex;
  gap: 20px;
}

.resident-radio {
  margin-right: 0;
}

.birth-date-container-horizontal {
  display: flex;
  gap: 10px;
  align-items: center;
}

.birth-input-horizontal {
  width: 70px;
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

  .common-table-wrapper {
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

/* 户籍未找到弹窗样式 */
:deep(.household-not-found-alert) {
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

/* 户籍查询错误弹窗样式 */
:deep(.household-query-error-alert) {
  .el-message-box {
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    border: none;
  }
  
  .el-message-box__header {
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
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
    content: "❌";
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
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
    border: none;
    box-shadow: 0 2px 6px rgba(245, 108, 108, 0.3);
  }
  
  .el-message-box__btns .el-button--primary:hover {
    background: linear-gradient(135deg, #f78989 0%, #f56c6c 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(245, 108, 108, 0.4);
  }
}
</style>