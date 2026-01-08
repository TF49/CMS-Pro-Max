<template>
  <div class="user-management-container common-container">
    <el-card class="management-card">
      <div slot="header" class="card-header">
        <div class="header-title">
          <el-icon :size="20"><UserFilled /></el-icon>
          <span>用户管理</span>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </div>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form ref="searchFormRef" :inline="true" :model="searchForm" class="search-form" label-width="80px" size="medium">
          <el-form-item label="用户名" class="search-form-item">
            <el-input
              v-model="searchForm.username"
              placeholder="请输入用户名"
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
                <el-icon color="#909399" size="16"><DocumentCopy /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="状态" class="search-form-item">
            <el-select 
              v-model="searchForm.isActive" 
              placeholder="请选择状态" 
              clearable
              style="width: 150px"
            >
              <el-option label="激活" value="true"></el-option>
              <el-option label="停用" value="false"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="search-buttons">
            <el-button type="primary" @click="handleSearch" size="medium" :icon="Search">
              查询
            </el-button>
            <el-button @click="handleReset" size="medium" :icon="Refresh" style="margin-left: 12px;">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 用户列表 -->
      <div class="common-table-wrapper">
        <el-table
          v-loading="loading"
          :data="userList"
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
          <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
          <el-table-column prop="username" label="用户名" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.username" size="small">{{ scope.row.username }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" width="180" align="center"></el-table-column>
          <el-table-column prop="phone" label="手机号" width="120" align="center"></el-table-column>
          <el-table-column prop="role" label="角色" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.role === 'ADMIN' ? 'success' : 'info'" size="small">
                {{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isActive" label="状态" width="100" align="center">
            <template #default="scope">
              <el-switch 
                v-model="scope.row.isActive" 
                @change="handleStatusChange(scope.row)"
                active-color="#13ce66"
                inactive-color="#ff4d4f"
                size="small"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="householdId" label="户籍ID" width="120" align="center"></el-table-column>
          <el-table-column prop="idCard" label="身份证号" width="180" align="center">
            <template #default="scope">
              <span class="id-card-text">{{ scope.row.idCard || '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <template v-if="scope.row">
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
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          background
        >
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!formData.id">
              <el-input v-model="formData.password" type="password" placeholder="请输入密码"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" prefix-icon="Message"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" prefix-icon="Phone"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="管理员" value="ADMIN"></el-option>
                <el-option label="普通用户" value="USER"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="isActive">
              <el-switch v-model="formData.isActive" active-color="#13ce66" inactive-color="#ff4d4f"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号" prefix-icon="DocumentCopy"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="户籍ID" prop="householdId">
              <el-input v-model="formData.householdId" placeholder="请输入户籍ID" prefix-icon="OfficeBuilding"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看用户对话框 -->
    <el-dialog
      title="用户详情"
      v-model="viewDialogVisible"
      width="500px"
      @close="handleViewDialogClose"
    >
      <el-descriptions :column="1" border :model="viewFormData">
        <el-descriptions-item label="用户名">{{ viewFormData.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ viewFormData.email || '无' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ viewFormData.phone || '无' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ viewFormData.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ viewFormData.isActive ? '激活' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ viewFormData.idCard || '无' }}</el-descriptions-item>
        <el-descriptions-item label="户籍ID">{{ viewFormData.householdId || '未关联' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Plus, Edit, Delete, Search, Phone, RefreshRight, 
  View, User, UserFilled, OfficeBuilding, DocumentCopy, Message, Refresh 
} from '@element-plus/icons-vue'
import { get, post, put, del } from '@/utils/request'

// 页面加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  idCard: '',
  isActive: ''
})

// 用户列表数据
const userList = ref([])

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 对话框相关
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const formRef = ref(null)
const formData = reactive({
  id: null,
  username: '',
  password: '',
  email: '',
  phone: '',
  role: 'USER',
  idCard: '',
  householdId: '',
  isActive: true
})

// 查看表单数据
const viewFormData = reactive({
  username: '',
  email: '',
  phone: '',
  role: 'USER',
  idCard: '',
  householdId: '',
  isActive: true
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 处理行悬停事件
const handleRowHover = (row, column, event) => {
  // 可以添加悬停效果处理
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const response = await get('/api/user/list', params)
    if (response.code === 200) {
      userList.value = response.data && response.data.data ? response.data.data : []
      pagination.total = response.data && response.data.total ? response.data.total : 0
    } else {
      ElMessage.error('获取用户列表失败：' + (response.msg || response.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取用户列表失败：', error)
    ElMessage.error('获取用户列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

// 重置搜索
const handleReset = () => {
  searchForm.username = ''
  searchForm.idCard = ''
  searchForm.isActive = ''
  pagination.currentPage = 1
  loadUserList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

// 当前页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  loadUserList()
}

// 打开添加用户对话框
const handleAdd = () => {
  dialogTitle.value = '添加用户'
  resetForm()
  dialogVisible.value = true
}

// 打开编辑用户对话框
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 打开查看用户对话框
const handleView = (row) => {
  Object.assign(viewFormData, row)
  viewDialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessage.confirm('确定要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await del(`/api/user/${row.id}`)
      if (response.code === 200) {
        ElMessage.success('删除用户成功')
        loadUserList()
      } else {
        ElMessage.error('删除用户失败：' + (response.msg || response.message || '未知错误'))
      }
    } catch (error) {
      console.error('删除用户失败：', error)
      ElMessage.error('删除用户失败：' + error.message)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const response = await put('/api/user/update', row)
    if (response.code !== 200) {
      // 如果更新失败，恢复原状态
      row.isActive = !row.isActive
      ElMessage.error('更新用户状态失败：' + (response.msg || response.message || '未知错误'))
    } else {
      ElMessage.success('更新用户状态成功')
    }
  } catch (error) {
    console.error('更新用户状态失败：', error)
    // 恢复原状态
    row.isActive = !row.isActive
    ElMessage.error('更新用户状态失败：' + error.message)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    let response
    if (formData.id) {
      // 编辑用户
      response = await put('/api/user/update', formData)
    } else {
      // 添加用户
      response = await post('/api/user/add', formData)
    }
    
    if (response.code === 200) {
      ElMessage.success(formData.id ? '编辑用户成功' : '添加用户成功')
      dialogVisible.value = false
      loadUserList()
    } else {
      ElMessage.error((formData.id ? '编辑用户失败：' : '添加用户失败：') + (response.msg || response.message || '未知错误'))
    }
  } catch (error) {
    console.error('提交表单失败：', error)
    ElMessage.error('提交表单失败：' + error.message)
  }
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 关闭查看对话框
const handleViewDialogClose = () => {
  // 重置查看表单数据
  Object.assign(viewFormData, {
    username: '',
    email: '',
    phone: '',
    role: 'USER',
    idCard: '',
    householdId: '',
    isActive: true
  })
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.email = ''
  formData.phone = ''
  formData.role = 'USER'
  formData.idCard = ''
  formData.householdId = ''
  formData.isActive = true
  
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 页面挂载时加载数据
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
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

.common-table {
  flex: 1;
  width: 100%;
  margin: 0;
  border-radius: 0;
}

/* 身份证号样式 */
.id-card-text {
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  color: #606266;
}

/* 对话框样式 */
.dialog-form {
  padding: 10px 0;
}

:deep(.el-row) {
  margin-bottom: 20px;
}
</style>