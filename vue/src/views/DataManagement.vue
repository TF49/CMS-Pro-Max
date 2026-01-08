<template>
  <div class="data-management">
    <el-card class="data-card">
      <div slot="header" class="card-header">
        <el-icon :size="20"><DataAnalysis /></el-icon>
        <span>数据管理</span>
      </div>
      
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick" class="custom-tabs">
        <el-tab-pane label="数据概览" name="overview">
          <div class="chart-container">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="populationByRegionChart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="ageDistributionChart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="educationLevelChart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="medicalInsuranceChart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="24">
                <el-card class="chart-card">
                  <div ref="populationTrendChart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 新增图表区域 -->
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="employmentChart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="socialSecurityChart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="propertyChart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="chart-card">
                  <div ref="vehicleChart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          </el-tab-pane>
          
          <el-tab-pane label="户籍数据" name="household">
            <div class="table-header">
            <div class="table-title">户籍数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshHouseholdData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="householdData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="householdNumber" label="户籍编号" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.householdNumber">{{ scope.row.householdNumber }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="户籍地址" show-overflow-tooltip align="center"></el-table-column>
            <el-table-column prop="householderName" label="户主姓名" width="100" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.householderName">{{ scope.row.householderName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="populationCount" label="人口数量" width="90" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.populationCount">{{ scope.row.populationCount }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="registrationDate" label="登记日期" width="120" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button size="mini" @click="viewHouseholdDetail(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="居民数据" name="resident">
          <div class="table-header">
            <div class="table-title">居民数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshResidentData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="residentData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="name" label="姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.name">{{ scope.row.name }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="idCard" label="身份证号" width="200"></el-table-column>
            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="scope">
                <span v-if="scope && scope.row && scope.row.gender">
                  <i :class="scope.row.gender === '男' ? 'el-icon-male' : 'el-icon-female'" 
                     :style="{color: scope.row.gender === '男' ? '#409EFF' : '#F56C6C'}"></i>
                  {{ scope.row.gender }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="birthDate" label="出生日期" width="120" align="center"></el-table-column>
            <el-table-column prop="householdNumber" label="户籍编号" width="120"></el-table-column>
            <el-table-column prop="relationship" label="与户主关系" width="120" align="center">
              <template #default="scope">
                <el-tag 
                  v-if="scope && scope.row && scope.row.relationship"
                  :type="getRelationshipType(scope.row.relationship)"
                  size="small">
                  {{ scope.row.relationship }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="educationLevel" label="教育程度" width="120" align="center">
              <template #default="scope">
                <el-tag 
                  v-if="scope && scope.row && scope.row.educationLevel"
                  :type="getEducationType(scope.row.educationLevel)"
                  size="small">
                  {{ scope.row.educationLevel }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-button size="mini" @click="viewResidentDetail(scope.row)" type="primary" plain>
                    <el-icon><View /></el-icon>详情
                  </el-button>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="教育数据" name="education">
          <div class="table-header">
            <div class="table-title">教育数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshEducationData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="educationData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="schoolName" label="学校名称" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="educationLevel" label="教育程度" width="120" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-tag 
                    v-if="scope.row.educationLevel"
                    :type="getEducationLevelType(scope.row.educationLevel)"
                    size="small">
                    {{ getEducationLevelLabel(scope.row.educationLevel) }}
                  </el-tag>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="major" label="专业" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="enrollmentDate" label="入学日期" width="120" align="center"></el-table-column>
            <el-table-column prop="graduationDate" label="毕业日期" width="120" align="center"></el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-tag 
                    v-if="scope.row.status"
                    :type="getStatusType(scope.row.status)"
                    size="small">
                    {{ getStatusLabel(scope.row.status) }}
                  </el-tag>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-button size="mini" @click="viewEducationDetail(scope.row)" type="primary" plain>
                    <el-icon><View /></el-icon>详情
                  </el-button>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="医疗数据" name="medical">
          <div class="table-header">
            <div class="table-title">医疗数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshMedicalData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="medicalData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="hospital" label="医院" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="department" label="科室" width="120" align="center"></el-table-column>
            <el-table-column prop="doctor" label="医生" width="120"></el-table-column>
            <el-table-column prop="diagnosis" label="诊断" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="lastCheckupDate" label="检查日期" width="120" align="center"></el-table-column>
            <el-table-column prop="insuranceType" label="保险类型" width="150" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-tag 
                    v-if="scope.row.insuranceType"
                    :type="getInsuranceType(scope.row.insuranceType)"
                    size="small">
                    {{ scope.row.insuranceType }}
                  </el-tag>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <template v-if="scope && scope.row">
                  <el-button size="mini" @click="viewMedicalDetail(scope.row)" type="primary" plain>
                    <el-icon><View /></el-icon>详情
                  </el-button>
                </template>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="就业数据" name="employment">
          <div class="table-header">
            <div class="table-title">就业数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshEmploymentData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="employmentData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
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
            <el-table-column prop="employmentStatus" label="就业状态" width="100" align="center">
              <template #default="scope">
                <el-tag 
                  v-if="scope && scope.row && scope.row.employmentStatus"
                  :type="getEmploymentStatusType(scope.row.employmentStatus)"
                  size="small">
                  {{ scope.row.employmentStatus }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="industry" label="行业" width="120"></el-table-column>
            <el-table-column prop="startDate" label="入职日期" width="120" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button size="mini" @click="viewEmploymentDetail(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="社保数据" name="social-security">
          <div class="table-header">
            <div class="table-title">社保数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshSocialSecurityData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="socialSecurityData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="pensionInsurance" label="养老保险" show-overflow-tooltip>
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.pensionInsurance">{{ scope.row.pensionInsurance }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="medicalInsurance" label="医疗保险" show-overflow-tooltip>
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.medicalInsurance">{{ scope.row.medicalInsurance }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="insuranceStatus" label="参保状态" width="120" align="center">
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
            <el-table-column prop="startDate" label="参保日期" width="120" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button size="mini" @click="viewSocialSecurityDetail(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="房产数据" name="property">
          <div class="table-header">
            <div class="table-title">房产数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshPropertyData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="propertyData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="propertyType" label="房产类型" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.propertyType">{{ scope.row.propertyType }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="地址" show-overflow-tooltip></el-table-column>
            <el-table-column prop="area" label="面积(㎡)" width="100" align="right">
              <template #default="scope">
                <span v-if="scope && scope.row && scope.row.area !== null && scope.row.area !== undefined && !isNaN(scope.row.area)">
                  {{ parseFloat(scope.row.area).toFixed(2) }}
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
            <el-table-column prop="usageType" label="使用类型" width="100" align="center">
              <template #default="scope">
                <el-tag 
                  v-if="scope && scope.row && scope.row.usageType"
                  :type="getPropertyUsageType(scope.row.usageType)"
                  size="small">
                  {{ scope.row.usageType }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="acquisitionDate" label="取得日期" width="120" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button size="mini" @click="viewPropertyDetail(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="车辆数据" name="vehicle">
          <div class="table-header">
            <div class="table-title">车辆数据列表</div>
            <div class="table-actions">
              <el-button type="primary" @click="refreshVehicleData">刷新数据</el-button>
            </div>
          </div>
          
          <div class="common-table-wrapper">
            <el-table 
              :data="vehicleData" 
              v-loading="loading"
              :header-cell-style="{background:'#f5f7fa', color:'#606266', fontWeight: '600', textAlign: 'center'}"
              :cell-style="{ textAlign: 'center' }"
              border
              stripe
              class="common-table">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="residentName" label="居民姓名" width="120" align="center">
              <template #default="scope">
                <el-tag size="small" v-if="scope && scope.row && scope.row.residentName">{{ scope.row.residentName }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="licensePlate" label="车牌号" width="120"></el-table-column>
            <el-table-column prop="vehicleType" label="车辆类型" width="100"></el-table-column>
            <el-table-column prop="brand" label="品牌" width="100"></el-table-column>
            <el-table-column prop="model" label="型号" width="120"></el-table-column>
            <el-table-column prop="purchaseDate" label="购买日期" width="120" align="center"></el-table-column>
            <el-table-column prop="purchasePrice" label="购买价格(元)" width="120" align="right">
              <template #default="scope">
                <span v-if="scope && scope.row && scope.row.purchasePrice !== null && scope.row.purchasePrice !== undefined && !isNaN(scope.row.purchasePrice)">
                  {{ parseFloat(scope.row.purchasePrice).toFixed(2) }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button size="mini" @click="viewVehicleDetail(scope.row)" type="primary" plain>
                  <el-icon><View /></el-icon>详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <!-- 分页 -->
      <div class="common-pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background>
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 户籍详情对话框 -->
    <el-dialog
      :title="'户籍详情'"
      v-model="householdDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentHousehold" 
        label-width="120px"
        class="household-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentHousehold.id" 
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
                v-model="currentHousehold.householdNumber" 
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
            <el-form-item label="户籍地址">
              <el-input 
                v-model="currentHousehold.address" 
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
            <el-form-item label="户主姓名">
              <el-input 
                v-model="currentHousehold.householderName" 
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
            <el-form-item label="户主身份证号">
              <el-input 
                v-model="currentHousehold.householderIdCard" 
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
            <el-form-item label="联系电话">
              <el-input 
                v-model="currentHousehold.phoneNumber" 
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
                v-model="currentHousehold.populationCount" 
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
                v-model="currentHousehold.householdType" 
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
                v-model="currentHousehold.registrationDate" 
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
                v-model="currentHousehold.createTime" 
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
                v-model="currentHousehold.updateTime" 
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
            v-model="currentHousehold.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="householdDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 居民详情对话框 -->
    <el-dialog
      :title="'居民详情'"
      v-model="residentDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentResident" 
        label-width="120px"
        class="resident-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentResident.id" 
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
                v-model="currentResident.name" 
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
            <el-form-item label="身份证号">
              <el-input 
                v-model="currentResident.idCard" 
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
                v-model="currentResident.gender" 
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
                v-model="currentResident.birthDate" 
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
                v-model="currentResident.householdNumber" 
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
                v-model="currentResident.relationship" 
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
                v-model="currentResident.ethnicGroup" 
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
                v-model="currentResident.educationLevel" 
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
            <el-form-item label="职业">
              <el-input 
                v-model="currentResident.occupation" 
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
            <el-form-item label="婚姻状况">
              <el-input 
                v-model="currentResident.maritalStatus" 
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
                v-model="currentResident.phoneNumber" 
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
                v-model="currentResident.isHouseholderDisplay" 
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
                v-model="currentResident.householdId" 
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
                v-model="currentResident.createTime" 
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
                v-model="currentResident.updateTime" 
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
            v-model="currentResident.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="residentDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 教育详情对话框 -->
    <el-dialog
      :title="'教育详情'"
      v-model="educationDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentEducation" 
        label-width="120px"
        class="education-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentEducation.id" 
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
                v-model="currentEducation.residentId" 
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
                v-model="currentEducation.residentName" 
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
            <el-form-item label="学校名称">
              <el-input 
                v-model="currentEducation.schoolName" 
                readonly 
                style="background-color: #f5f7fa;"
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
            <el-form-item label="教育程度">
              <el-input 
                :model-value="getEducationLevelLabel(currentEducation.educationLevel)" 
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
            <el-form-item label="专业">
              <el-input 
                v-model="currentEducation.major" 
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
            <el-form-item label="入学日期">
              <el-input 
                v-model="currentEducation.enrollmentDate" 
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
            <el-form-item label="毕业日期">
              <el-input 
                v-model="currentEducation.graduationDate" 
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
            <el-form-item label="状态">
              <el-input 
                :model-value="getStatusLabel(currentEducation.status)" 
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
            <el-form-item label="创建时间">
              <el-input 
                v-model="currentEducation.createTime" 
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
                v-model="currentEducation.updateTime" 
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
            v-model="currentEducation.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="educationDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 医疗详情对话框 -->
    <el-dialog
      :title="'医疗详情'"
      v-model="medicalDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentMedical" 
        label-width="120px"
        class="medical-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentMedical.id" 
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
                v-model="currentMedical.residentId" 
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
                v-model="currentMedical.residentName" 
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
                v-model="currentMedical.idCard" 
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
                v-model="currentMedical.hospital" 
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
                v-model="currentMedical.department" 
                readonly 
                style="background-color: #f5f7fa;"
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
            <el-form-item label="医生">
              <el-input 
                v-model="currentMedical.doctor" 
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
        <el-form-item label="诊断">
          <el-input 
            v-model="currentMedical.diagnosis" 
            readonly 
            style="background-color: #f5f7fa;"
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="保险类型">
              <el-input 
                v-model="currentMedical.insuranceType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Shield /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检查日期">
              <el-input 
                v-model="currentMedical.lastCheckupDate" 
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
                v-model="currentMedical.createTime" 
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
                v-model="currentMedical.updateTime" 
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
            v-model="currentMedical.remarks"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="medicalDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 就业详情对话框 -->
    <el-dialog
      :title="'就业详情'"
      v-model="employmentDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentEmployment" 
        label-width="120px"
        class="employment-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentEmployment.id" 
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
                v-model="currentEmployment.residentId" 
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
                v-model="currentEmployment.residentName" 
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
            <el-form-item label="工作单位">
              <el-input 
                v-model="currentEmployment.company" 
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
            <el-form-item label="职位">
              <el-input 
                v-model="currentEmployment.position" 
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
            <el-form-item label="薪资(元)">
              <el-input 
                v-model="currentEmployment.salary" 
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
            <el-form-item label="就业状态">
              <el-input 
                v-model="currentEmployment.employmentStatus" 
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
            <el-form-item label="行业">
              <el-input 
                v-model="currentEmployment.industry" 
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
            <el-form-item label="入职日期">
              <el-input 
                v-model="currentEmployment.startDate" 
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
            <el-form-item label="合同类型">
              <el-input 
                v-model="currentEmployment.contractType" 
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
                v-model="currentEmployment.createTime" 
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
                v-model="currentEmployment.updateTime" 
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
            v-model="currentEmployment.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="employmentDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 社保详情对话框 -->
    <el-dialog
      :title="'社保详情'"
      v-model="socialSecurityDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentSocialSecurity" 
        label-width="120px"
        class="social-security-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentSocialSecurity.id" 
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
                v-model="currentSocialSecurity.residentId" 
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
                v-model="currentSocialSecurity.residentName" 
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
                v-model="currentSocialSecurity.insuranceStatus" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Shield /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老保险">
              <el-input 
                v-model="currentSocialSecurity.pensionInsurance" 
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
                v-model="currentSocialSecurity.medicalInsurance" 
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
                v-model="currentSocialSecurity.unemploymentInsurance" 
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
                v-model="currentSocialSecurity.workInjuryInsurance" 
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
                v-model="currentSocialSecurity.maternityInsurance" 
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
            <el-form-item label="缴费基数">
              <el-input 
                v-model="currentSocialSecurity.paymentBase" 
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
            <el-form-item label="缴费金额(元)">
              <el-input 
                v-model="currentSocialSecurity.paymentAmount" 
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
                v-model="currentSocialSecurity.startDate" 
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
                v-model="currentSocialSecurity.endDate" 
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
                v-model="currentSocialSecurity.createTime" 
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
                v-model="currentSocialSecurity.updateTime" 
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
            v-model="currentSocialSecurity.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="socialSecurityDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 房产详情对话框 -->
    <el-dialog
      :title="'房产详情'"
      v-model="propertyDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentProperty" 
        label-width="120px"
        class="property-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentProperty.id" 
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
                v-model="currentProperty.residentId" 
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
                v-model="currentProperty.residentName" 
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
            <el-form-item label="房产类型">
              <el-input 
                v-model="currentProperty.propertyType" 
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
            <el-form-item label="地址">
              <el-input 
                v-model="currentProperty.address" 
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
            <el-form-item label="面积(㎡)">
              <el-input 
                v-model="currentProperty.area" 
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
            <el-form-item label="估值(元)">
              <el-input 
                v-model="currentProperty.value" 
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
            <el-form-item label="产权证号">
              <el-input 
                v-model="currentProperty.propertyCertificate" 
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
            <el-form-item label="使用类型">
              <el-input 
                v-model="currentProperty.usageType" 
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
                v-model="currentProperty.acquisitionDate" 
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
            <el-form-item label="房间数">
              <el-input 
                v-model="currentProperty.rooms" 
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
            <el-form-item label="创建时间">
              <el-input 
                v-model="currentProperty.createTime" 
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
                v-model="currentProperty.updateTime" 
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
            v-model="currentProperty.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="propertyDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
    
    <!-- 车辆详情对话框 -->
    <el-dialog
      :title="'车辆详情'"
      v-model="vehicleDetailVisible"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentVehicle" 
        label-width="120px"
        class="vehicle-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ID">
              <el-input 
                v-model="currentVehicle.id" 
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
                v-model="currentVehicle.residentId" 
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
                v-model="currentVehicle.residentName" 
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
            <el-form-item label="车牌号">
              <el-input 
                v-model="currentVehicle.licensePlate" 
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
            <el-form-item label="车辆类型">
              <el-input 
                v-model="currentVehicle.vehicleType" 
                readonly 
                style="background-color: #f5f7fa;"
              >
                <template #prefix>
                  <el-icon><Van /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌">
              <el-input 
                v-model="currentVehicle.brand" 
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
            <el-form-item label="型号">
              <el-input 
                v-model="currentVehicle.model" 
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
            <el-form-item label="颜色">
              <el-input 
                v-model="currentVehicle.color" 
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
            <el-form-item label="发动机号">
              <el-input 
                v-model="currentVehicle.engineNumber" 
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
            <el-form-item label="车架号">
              <el-input 
                v-model="currentVehicle.chassisNumber" 
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
            <el-form-item label="购买日期">
              <el-input 
                v-model="currentVehicle.purchaseDate" 
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
            <el-form-item label="购买价格(元)">
              <el-input 
                v-model="currentVehicle.purchasePrice" 
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
            <el-form-item label="保险到期日期">
              <el-input 
                v-model="currentVehicle.insuranceExpiryDate" 
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
            <el-form-item label="年检日期">
              <el-input 
                v-model="currentVehicle.annualInspectionDate" 
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
                v-model="currentVehicle.createTime" 
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
                v-model="currentVehicle.updateTime" 
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
            v-model="currentVehicle.notes"
            readonly
            style="background-color: #f5f7fa;"
          >
          </el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="vehicleDetailVisible = false">
          <el-icon><Close /></el-icon>
          关闭
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { 
  getHouseholdData, 
  getResidentData, 
  getEducationData, 
  getMedicalData,
  getEmploymentData,
  getSocialSecurityData,
  getPropertyData,
  getVehicleData
} from '@/api/data'
import {
  getPopulationByRegion,
  getAgeDistribution,
  getEducationLevel,
  getMedicalInsurance,
  getPopulationTrend,
  getEmploymentStatusDistribution,
  getSocialSecurityCoverage,
  getPropertyTypeDistribution,
  getVehicleBrandDistribution
} from '@/api/statistics'
import { get as getData } from '@/utils/request'
import {
  DataAnalysis,
  UserFilled,
  Avatar,
  School,
  Plus,
  Briefcase,
  Shield,
  OfficeBuilding,
  Van,
  View,
  Document,
  Location,
  CreditCard,
  Phone,
  Clock,
  Close
} from '@element-plus/icons-vue'

export default {
  name: 'DataManagement',
  data() {
    return {
      activeTab: 'overview',
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      routeParamsTimeout: null,
      
      // 详情弹窗可见性状态
      householdDetailVisible: false,
      residentDetailVisible: false,
      educationDetailVisible: false,
      medicalDetailVisible: false,
      employmentDetailVisible: false,
      socialSecurityDetailVisible: false,
      propertyDetailVisible: false,
      vehicleDetailVisible: false,
      
      // 当前查看的详情数据
      currentHousehold: {},
      currentResident: {},
      currentEducation: {},
      currentMedical: {},
      currentEmployment: {},
      currentSocialSecurity: {},
      currentProperty: {},
      currentVehicle: {},
      
      // 户籍数据
      householdData: [],
      
      // 居民数据
      residentData: [],
      
      // 教育数据
      educationData: [],
      
      // 医疗数据
      medicalData: [],
      
      // 就业数据
      employmentData: [],
      
      // 社保数据
      socialSecurityData: [],
      
      // 房产数据
      propertyData: [],
      
      // 车辆数据
      vehicleData: [],
      
      // 图表实例
      populationByRegionChart: null,
      ageDistributionChart: null,
      educationLevelChart: null,
      medicalInsuranceChart: null,
      populationTrendChart: null,
      employmentChart: null,
      socialSecurityChart: null,
      propertyChart: null,
      vehicleChart: null,
      
      // 路由参数处理相关
      routeParamsTimeout: null
    }
  },
  mounted() {
    this.processRouteParams()
    
    // 监听全局事件总线中的关闭所有弹窗事件
    window.eventBus.$on('closeAllModals', this.closeAllModals)
  },
  
  // 添加beforeRouteUpdate来更好地处理路由参数变化
  beforeRouteUpdate(to, from, next) {
    this.processRouteParams()
    next()
  },
  
  beforeDestroy() {
    // 移除事件监听器
    window.eventBus.$off('closeAllModals', this.closeAllModals)
    
    // 清除定时器，防止路由跳转时出现问题
    if (this.routeParamsTimeout) {
      clearTimeout(this.routeParamsTimeout)
    }
    
    // 销毁图表实例，防止内存泄漏
    if (this.populationByRegionChart) {
      this.populationByRegionChart.dispose()
    }
    if (this.ageDistributionChart) {
      this.ageDistributionChart.dispose()
    }
    if (this.educationLevelChart) {
      this.educationLevelChart.dispose()
    }
    if (this.medicalInsuranceChart) {
      this.medicalInsuranceChart.dispose()
    }
    if (this.populationTrendChart) {
      this.populationTrendChart.dispose()
    }
    if (this.employmentChart) {
      this.employmentChart.dispose()
    }
    if (this.socialSecurityChart) {
      this.socialSecurityChart.dispose()
    }
    if (this.propertyChart) {
      this.propertyChart.dispose()
    }
    if (this.vehicleChart) {
      this.vehicleChart.dispose()
    }
  },
  
  methods: {
    // 封装路由参数处理逻辑
    processRouteParams() {
      // 检查路由参数，设置对应的标签页
      const tabParam = this.$route.query.tab
      const idParam = this.$route.query.id
      
      // 首先加载所有数据
      this.loadAllData()
      
      if (tabParam) {
        // 根据传入的tab参数设置激活的标签页
        const validTabs = ['overview', 'household', 'resident', 'education', 'medical', 'employment', 'social-security', 'property', 'vehicle']
        if (validTabs.includes(tabParam)) {
          this.activeTab = tabParam
          // 如果还传入了id参数，查找并显示对应数据的详情
          if (idParam) {
            console.log(`需要查看${tabParam}类型的数据，ID为: ${idParam}`)
            // 清除之前的定时器
            if (this.routeParamsTimeout) {
              clearTimeout(this.routeParamsTimeout)
            }
            // 等待数据加载完成后查找对应记录
            this.routeParamsTimeout = setTimeout(() => {
              this.findAndShowDetail(tabParam, idParam)
            }, 500) // 等待数据加载，可根据实际情况调整延迟
            // 这里可以根据需要实现具体的数据筛选或定位逻辑
          }
        }
      }
      
      this.$nextTick(() => {
        this.initCharts()
      })
    },
    
    // 关闭所有详情弹窗
    closeAllModals() {
      this.householdDetailVisible = false
      this.residentDetailVisible = false
      this.educationDetailVisible = false
      this.medicalDetailVisible = false
      this.employmentDetailVisible = false
      this.socialSecurityDetailVisible = false
      this.propertyDetailVisible = false
      this.vehicleDetailVisible = false
    },
    
    // 加载所有数据
    loadAllData() {
      this.loadHouseholdData()
      this.loadResidentData()
      this.loadEducationData()
      this.loadMedicalData()
      // 加载新增模块数据
      this.loadEmploymentData()
      this.loadSocialSecurityData()
      this.loadPropertyData()
      this.loadVehicleData()
    },
    
    initCharts() {
      this.initPopulationByRegionChart()
      this.initAgeDistributionChart()
      this.initEducationLevelChart()
      this.initMedicalInsuranceChart()
      this.initPopulationTrendChart()
      
      // 初始化新增图表
      this.initEmploymentChart()
      this.initSocialSecurityChart()
      this.initPropertyChart()
      this.initVehicleChart()
      
      // 加载图表数据
      this.loadPopulationByRegionData()
      this.loadAgeDistributionData()
      this.loadEducationLevelData()
      this.loadMedicalInsuranceData()
      this.loadPopulationTrendData()
      
      // 加载新增图表数据
      this.loadEmploymentDataChart()
      this.loadSocialSecurityDataChart()
      this.loadPropertyDataChart()
      this.loadVehicleDataChart()
    },
    
    initPopulationByRegionChart() {
      this.populationByRegionChart = echarts.init(this.$refs.populationByRegionChart)
      const option = {
        title: {
          text: '各地区人口统计',
          textStyle: {
            fontSize: 18,
            fontWeight: 'bold',
            color: '#333'
          },
          left: 'center',
          top: 10
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(128, 179, 246, 0.3)'
            }
          },
          formatter: function (params) {
            return `
              <div style="padding: 8px 12px;">
                <div style="font-weight: bold; margin-bottom: 4px;">${params[0].name}</div>
                <div style="color: #188df0;">
                  <span style="display: inline-block; width: 10px; height: 10px; background: #188df0; border-radius: 50%; margin-right: 6px;"></span>
                  人口数量: <span style="font-weight: bold;">${params[0].value.toLocaleString()}</span> 人
                </div>
              </div>
            `
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#188df0',
          borderWidth: 1,
          borderRadius: 8,
          textStyle: {
            color: '#333'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '1%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisTick: {
            alignWithLabel: true,
            lineStyle: {
              color: '#ccc'
            }
          },
          axisLabel: {
            interval: 0,
            rotate: 0,
            fontSize: 10,
            color: '#666',
            fontWeight: 500,
            formatter: function(value) {
              // 简化地区名称，只显示核心部分
              return value.replace('市', '').replace('省', '').replace('自治区', '')
            },
            interval: 0
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        yAxis: {
          type: 'value',
          boundaryGap: [0, 0.01],
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0',
              type: 'dashed'
            }
          },
          axisLabel: {
            formatter: '{value} 人',
            color: '#666'
          }
        },
        series: [
          {
            name: '人口数量',
            type: 'bar',
            barWidth: '35%',
            data: [],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ]),
              borderRadius: [4, 4, 0, 0],
              borderWidth: 1,
              borderColor: '#188df0'
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2378f7' },
                  { offset: 0.7, color: '#2378f7' },
                  { offset: 1, color: '#83bff6' }
                ]),
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowOffsetY: 2,
                shadowColor: 'rgba(24, 141, 240, 0.3)'
              }
            },
            label: {
              show: true,
              position: 'top',
              align: 'center',
              color: '#188df0',
              fontSize: 12,
              fontWeight: 'bold',
              formatter: '{c}',
              offset: [0, -3]
            },
            animationDelay: function (idx) {
              // 交错动画效果
              return idx * 50;
            },
            animationEasing: 'elasticOut'
          }
        ],
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      }
      this.populationByRegionChart.setOption(option)
      
      // 添加点击事件
      this.populationByRegionChart.on('click', (params) => {
        this.$message({
          message: `您查看了 ${params.name} 的人口数据: ${params.value} 人`,
          type: 'success',
          duration: 2000
        })
      })
    },
    
    initAgeDistributionChart() {
      this.ageDistributionChart = echarts.init(this.$refs.ageDistributionChart)
      const option = {
        title: {
          text: '年龄分布情况',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'middle'
        },
        series: [
          {
            name: '年龄分布',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: []
          }
        ]
      }
      this.ageDistributionChart.setOption(option)
    },
    
    initEducationLevelChart() {
      this.educationLevelChart = echarts.init(this.$refs.educationLevelChart)
      const option = {
        title: {
          text: '教育程度分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['男性', '女性'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: []
        },
        series: [
          {
            name: '男性',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              color: '#409EFF'
            },
            data: []
          },
          {
            name: '女性',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              color: '#F56C6C'
            },
            data: []
          },
          {
            name: 'Total',
            type: 'bar',
            stack: 'total',
            data: [],
            itemStyle: { opacity: 0 },
            label: {
              show: true,
              position: 'right',
              align: 'left',
              color: '#333',
              fontSize: 12,
              fontWeight: 'bold',
              formatter: '{c}'
            }
          }
        ]
      }
      this.educationLevelChart.setOption(option)
    },
    
    initMedicalInsuranceChart() {
      this.medicalInsuranceChart = echarts.init(this.$refs.medicalInsuranceChart)
      const option = {
        title: {
          text: '医疗保险类型分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'horizontal',
          bottom: 10
        },
        series: [
          {
            name: '保险类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: []
          }
        ]
      }
      this.medicalInsuranceChart.setOption(option)
    },
    
    initPopulationTrendChart() {
      this.populationTrendChart = echarts.init(this.$refs.populationTrendChart)
      const option = {
        title: {
          text: '近5年人口增长趋势',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总人口', '男性', '女性'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总人口',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
              ])
            }
          },
          {
            name: '男性',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            lineStyle: {
              type: 'dashed'
            }
          },
          {
            name: '女性',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#F56C6C'
            },
            lineStyle: {
              type: 'dashed'
            }
          }
        ]
      }
      this.populationTrendChart.setOption(option)
    },
    
    loadPopulationByRegionData() {
      getPopulationByRegion().then(response => {
        const data = response.data
        this.populationByRegionChart.setOption({
          xAxis: {
            data: data.regions
          },
          series: [
            {
              name: '人口数量',
              data: data.populations
            }
          ]
        })
      }).catch(error => {
        console.error('获取各地区人口分布数据失败:', error)
        this.$message.error('获取各地区人口分布数据失败')
      })
    },
    
    loadAgeDistributionData() {
      getAgeDistribution().then(response => {
        const data = response.data
        this.ageDistributionChart.setOption({
          series: [
            {
              name: '年龄分布',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取年龄分布数据失败:', error)
        this.$message.error('获取年龄分布数据失败')
      })
    },
    
    loadEducationLevelData() {
      getEducationLevel().then(response => {
        const data = response.data
        // 计算每个教育程度的总数据
        const totalData = data.maleData.map((male, index) => male + data.femaleData[index])
        this.educationLevelChart.setOption({
          yAxis: {
            data: data.levels
          },
          series: [
            {
              name: '男性',
              data: data.maleData
            },
            {
              name: '女性',
              data: data.femaleData
            },
            {
              name: 'Total',
              data: totalData
            }
          ]
        })
      }).catch(error => {
        console.error('获取教育程度分布数据失败:', error)
        this.$message.error('获取教育程度分布数据失败')
      })
    },
    
    loadMedicalInsuranceData() {
      getMedicalInsurance().then(response => {
        const data = response.data
        this.medicalInsuranceChart.setOption({
          series: [
            {
              name: '保险类型',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取医疗保险类型分布数据失败:', error)
        this.$message.error('获取医疗保险类型分布数据失败')
      })
    },
    
    loadPopulationTrendData() {
      getPopulationTrend().then(response => {
        const data = response.data
        this.populationTrendChart.setOption({
          xAxis: {
            data: data.years
          },
          series: [
            {
              name: '总人口',
              data: data.totalData
            },
            {
              name: '男性',
              data: data.maleData
            },
            {
              name: '女性',
              data: data.femaleData
            }
          ]
        })
      }).catch(error => {
        console.error('获取人口增长趋势数据失败:', error)
        this.$message.error('获取人口增长趋势数据失败')
        // 使用模拟数据
        this.populationTrendChart.setOption({
          xAxis: {
            data: ['2020', '2021', '2022', '2023', '2024']
          },
          series: [
            {
              name: '总人口',
              data: [1234, 1280, 1320, 1350, 1375]
            },
            {
              name: '男性',
              data: [620, 645, 665, 680, 692]
            },
            {
              name: '女性',
              data: [614, 635, 655, 670, 683]
            }
          ]
        })
      })
    },
    
    // 初始化就业情况图表
    initEmploymentChart() {
      this.employmentChart = echarts.init(this.$refs.employmentChart)
      const option = {
        title: {
          text: '就业情况统计',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'middle'
        },
        series: [
          {
            name: '就业状态',
            type: 'pie',
            radius: '60%',
            color: ['#409EFF', '#67C23A', '#F56C6C'],
            data: [],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      this.employmentChart.setOption(option)
    },
    
    // 加载就业数据图表
    loadEmploymentDataChart() {
      getEmploymentStatusDistribution().then(response => {
        const data = response.data
        this.employmentChart.setOption({
          series: [
            {
              name: '就业状态',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取就业状态分布数据失败:', error)
        this.$message.error('获取就业状态分布数据失败')
      })
    },
    
    // 初始化社保情况图表
    initSocialSecurityChart() {
      this.socialSecurityChart = echarts.init(this.$refs.socialSecurityChart)
      const option = {
        title: {
          text: '社保参保情况',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['养老保险', '医疗保险', '失业保险', '工伤保险', '生育保险'],
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '参保人数',
            type: 'bar',
            barWidth: '60%',
            data: [],
            itemStyle: {
              color: '#67C23A'
            },
            label: {
              show: true,
              position: 'top',
              align: 'center',
              color: '#333',
              fontSize: 12,
              fontWeight: 'bold',
              formatter: '{c}'
            }
          }
        ]
      }
      this.socialSecurityChart.setOption(option)
    },
    
    // 加载社保数据图表
    loadSocialSecurityDataChart() {
      getSocialSecurityCoverage().then(response => {
        const data = response.data
        this.socialSecurityChart.setOption({
          xAxis: {
            data: data.types
          },
          series: [
            {
              name: '参保人数',
              data: data.insuranceCounts
            }
          ]
        })
      }).catch(error => {
        console.error('获取社保参保情况数据失败:', error)
        this.$message.error('获取社保参保情况数据失败')
      })
    },
    
    // 初始化房产情况图表
    initPropertyChart() {
      this.propertyChart = echarts.init(this.$refs.propertyChart)
      const option = {
        title: {
          text: '房产类型分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'middle'
        },
        series: [
          {
            name: '房产类型',
            type: 'pie',
            radius: ['40%', '70%'],
            data: [],
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        ]
      }
      this.propertyChart.setOption(option)
    },
    
    // 加载房产数据图表
    loadPropertyDataChart() {
      getPropertyTypeDistribution().then(response => {
        const data = response.data
        this.propertyChart.setOption({
          series: [
            {
              name: '房产类型',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取房产类型分布数据失败:', error)
        this.$message.error('获取房产类型分布数据失败')
      })
    },
    
    // 初始化车辆情况图表
    initVehicleChart() {
      this.vehicleChart = echarts.init(this.$refs.vehicleChart)
      const option = {
        title: {
          text: '车辆品牌分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '数量',
            type: 'bar',
            barWidth: '60%',
            data: [],
            itemStyle: {
              color: '#E6A23C'
            },
            label: {
              show: true,
              position: 'top',
              align: 'center',
              color: '#333',
              fontSize: 12,
              fontWeight: 'bold',
              formatter: '{c}'
            }
          }
        ]
      }
      this.vehicleChart.setOption(option)
    },
    
    // 加载车辆数据图表
    loadVehicleDataChart() {
      getVehicleBrandDistribution().then(response => {
        const data = response.data
        // 将后端返回的数据转换为前端图表需要的格式
        const brands = data.map(item => item.name)
        const counts = data.map(item => item.value)
        this.vehicleChart.setOption({
          xAxis: {
            data: brands
          },
          series: [
            {
              name: '数量',
              data: counts
            }
          ]
        })
      }).catch(error => {
        console.error('获取车辆品牌分布数据失败:', error)
        this.$message.error('获取车辆品牌分布数据失败')
      })
    },
    
    // 通用数据加载方法，统一处理数据获取逻辑
    loadData(apiFunc, dataProp, params = [], transformFunc = null) {
      this.loading = true
      return apiFunc(...params).then(response => {
        // 确保数据是数组类型，处理后端返回的完整响应格式
        let data = []
        let total = 0
        
        // 处理Result对象，response是Result对象，结构是 { code: 200, message: 'success', data: {...} }
        if (response.data) {
          // response.data是分页数据对象，结构是 { data: [...], total: 100, page: 1, size: 10 }
          if (Array.isArray(response.data)) {
            // 如果response.data是数组，说明没有分页
            data = response.data
            total = data.length
          } else {
            // 如果response.data是对象，说明有分页
            if (Array.isArray(response.data.data)) {
              // 如果response.data.data是数组，说明是直接返回的数据列表
              data = response.data.data
              total = response.data.total || data.length
            } else if (response.data.data && response.data.data.data) {
              // 如果response.data.data.data是数组，说明是嵌套的分页结构
              data = response.data.data.data
              total = response.data.data.total || 0
            } else if (response.data.data && response.data.data.list) {
              // 兼容其他可能的列表字段名
              data = response.data.data.list
              total = response.data.data.total || 0
            } else if (response.data.list) {
              // 兼容直接在response.data中包含list和total的情况
              data = response.data.list
              total = response.data.total || 0
            }
          }
        } else if (Array.isArray(response)) {
          // 直接返回数组的情况
          data = response
          total = data.length
        }
        
        // 更新total属性
        this.total = total
        // 应用数据转换函数（如果提供）
        this[dataProp] = transformFunc ? transformFunc(data) : data
        this.loading = false
      }).catch(error => {
        const dataName = dataProp.replace('Data', '')
        console.error(`获取${dataName}数据失败:`, error)
        this.$message.error(`获取${dataName}数据失败`)
        this.loading = false
        // 出现错误时使用空数组，避免表格渲染问题
        this[dataProp] = []
        this.total = 0
      })
    },

    loadHouseholdData() {
      // 定义户籍数据转换函数
      const transformHouseholdData = (data) => {
        return data.map(item => {
          return {
            ...item,
            householderName: item.householderName || '未知' // 使用实际的户主姓名或默认值
          }
        })
      }
      // 使用通用数据加载方法
      this.loadData(getHouseholdData, 'householdData', [this.currentPage, this.pageSize], transformHouseholdData)
    },
    
    loadResidentData() {
      // 使用通用数据加载方法
      this.loadData(getResidentData, 'residentData', [this.currentPage, this.pageSize])
    },
    
    loadEducationData() {
      // 使用通用数据加载方法
      this.loadData(getEducationData, 'educationData', [this.currentPage, this.pageSize])
    },
    
    loadMedicalData() {
      // 使用通用数据加载方法
      this.loadData(getMedicalData, 'medicalData', [this.currentPage, this.pageSize])
    },
    
    loadEmploymentData() {
      // 使用通用数据加载方法
      this.loadData(getEmploymentData, 'employmentData', [this.currentPage, this.pageSize])
    },
    
    loadSocialSecurityData() {
      // 使用通用数据加载方法
      this.loadData(getSocialSecurityData, 'socialSecurityData', [this.currentPage, this.pageSize])
    },
    
    loadPropertyData() {
      // 使用通用数据加载方法
      this.loadData(getPropertyData, 'propertyData', [this.currentPage, this.pageSize])
    },
    
    loadVehicleData() {
      // 使用通用数据加载方法
      this.loadData(getVehicleData, 'vehicleData', [this.currentPage, this.pageSize])
    },
    
    refreshHouseholdData() {
      this.loadHouseholdData()
      this.$message.success('户籍数据已刷新')
    },
    
    refreshResidentData() {
      this.loadResidentData()
      this.$message.success('居民数据已刷新')
    },
    
    refreshEducationData() {
      this.loadEducationData()
      this.$message.success('教育数据已刷新')
    },
    
    refreshMedicalData() {
      this.loadMedicalData()
      this.$message.success('医疗数据已刷新')
    },
    
    refreshEmploymentData() {
      this.loadEmploymentData()
      this.$message.success('就业数据已刷新')
    },
    
    refreshSocialSecurityData() {
      this.loadSocialSecurityData()
      this.$message.success('社保数据已刷新')
    },
    
    refreshPropertyData() {
      this.loadPropertyData()
      this.$message.success('房产数据已刷新')
    },
    
    refreshVehicleData() {
      this.loadVehicleData()
      this.$message.success('车辆数据已刷新')
    },
    
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.currentPage = 1
      this.pageSize = 10
      this.loadCurrentTabData()
    },
    
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadCurrentTabData()
    },
    
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadCurrentTabData()
    },
    
    // 根据当前激活的标签页加载对应数据
    loadCurrentTabData() {
      switch(this.activeTab) {
        case 'household':
          this.loadHouseholdData()
          break
        case 'resident':
          this.loadResidentData()
          break
        case 'education':
          this.loadEducationData()
          break
        case 'medical':
          this.loadMedicalData()
          break
        case 'employment':
          this.loadEmploymentData()
          break
        case 'social-security':
          this.loadSocialSecurityData()
          break
        case 'property':
          this.loadPropertyData()
          break
        case 'vehicle':
          this.loadVehicleData()
          break
        default:
          break
      }
    },
    
    viewHouseholdDetail(row) {
      this.currentHousehold = { ...row }
      this.householdDetailVisible = true
    },
    
    viewResidentDetail(row) {
      // 复制行数据并添加显示属性
      this.currentResident = {
        ...row,
        isHouseholderDisplay: row.isHouseholder ? '是' : '否'
      }
      this.residentDetailVisible = true
    },
    
    viewEducationDetail(row) {
      this.currentEducation = { ...row }
      this.educationDetailVisible = true
    },
    
    viewMedicalDetail(row) {
      this.currentMedical = { ...row }
      this.medicalDetailVisible = true
    },
    
    viewEmploymentDetail(row) {
      this.currentEmployment = { ...row }
      this.employmentDetailVisible = true
    },
    
    viewSocialSecurityDetail(row) {
      this.currentSocialSecurity = { ...row }
      this.socialSecurityDetailVisible = true
    },
    
    viewPropertyDetail(row) {
      this.currentProperty = { ...row }
      this.propertyDetailVisible = true
    },
    
    viewVehicleDetail(row) {
      this.currentVehicle = { ...row }
      this.vehicleDetailVisible = true
    },
    
    // 根据tab类型和id查找并显示详情
    findAndShowDetail(tab, id) {
      let targetRow = null
      
      // 根据不同的tab类型在对应的数据集里查找
      switch(tab) {
        case 'household':
          targetRow = this.householdData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewHouseholdDetail(targetRow)
          }
          break
        case 'resident':
          targetRow = this.residentData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewResidentDetail(targetRow)
          }
          break
        case 'education':
          targetRow = this.educationData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewEducationDetail(targetRow)
          }
          break
        case 'medical':
          targetRow = this.medicalData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewMedicalDetail(targetRow)
          }
          break
        case 'employment':
          targetRow = this.employmentData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewEmploymentDetail(targetRow)
          }
          break
        case 'social-security':
          targetRow = this.socialSecurityData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewSocialSecurityDetail(targetRow)
          }
          break
        case 'property':
          targetRow = this.propertyData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewPropertyDetail(targetRow)
          }
          break
        case 'vehicle':
          targetRow = this.vehicleData.find(item => item && item.id === id)
          if (targetRow) {
            this.viewVehicleDetail(targetRow)
          }
          break
      }
      
      if (!targetRow) {
        console.log(`未找到${tab}类型ID为${id}的数据记录`)
      }
    },
    
    getRelationshipType(relationship) {
      const relationMap = {
        '户主': 'primary',
        '配偶': 'success',
        '子女': 'warning',
        '父母': 'info',
        '其他': 'danger'
      }
      return relationMap[relationship] || ''
    },
    
    getEducationType(education) {
      const educationMap = {
        '小学': '',
        '初中': 'info',
        '高中': 'warning',
        '大专': 'success',
        '本科': 'primary',
        '硕士': 'danger',
        '博士': 'danger'
      }
      return educationMap[education] || ''
    },
    
    getEducationLevelType(level) {
      const levelMap = {
        '小学': '',
        '初中': 'info',
        '高中': 'warning',
        '大专': 'success',
        '本科': 'primary',
        '硕士': 'danger',
        '博士': 'danger',
        // 英文映射，兼容新数据
        'PRIMARY': '',
        'JUNIOR': 'info',
        'SENIOR': 'warning',
        'COLLEGE': 'success',
        'BACHELOR': 'primary',
        'MASTER': 'danger',
        'DOCTOR': 'danger'
      }
      return levelMap[level] || ''
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

    getStatusType(status) {
      const statusMap = {
        '在读': 'primary',
        '毕业': 'success',
        '辍学': 'danger',
        '休学': 'warning',
        // 英文映射，兼容新数据
        'STUDYING': 'primary',
        'GRADUATED': 'success',
        'GRADUAT': 'success', // 兼容表格中显示的GRADUAT
        'SUSPENDED': 'warning',
        'DROP_OUT': 'danger'
      }
      return statusMap[status] || ''
    },
    
    // 状态英文转中文
    getStatusLabel(status) {
      const statusMap = {
        'STUDYING': '在读',
        'GRADUATED': '毕业',
        'GRADUAT': '毕业', // 兼容表格中显示的GRADUAT
        'SUSPENDED': '休学',
        'DROP_OUT': '退学',
        // 保留中文映射，兼容旧数据
        '在读': '在读',
        '毕业': '毕业',
        '辍学': '辍学',
        '休学': '休学'
      };
      return statusMap[status] || status;
    },
    
    getInsuranceType(type) {
      const typeMap = {
        '城镇职工医疗保险': 'primary',
        '城乡居民医疗保险': 'success'
      }
      return typeMap[type] || ''
    },
    
    getEmploymentStatusType(status) {
      const statusMap = {
        '在职': 'success',
        '失业': 'danger',
        '退休': 'warning'
      }
      return statusMap[status] || ''
    },
    
    getInsuranceStatusType(status) {
      const statusMap = {
        '正常参保': 'success',
        '暂停参保': 'warning',
        '停止参保': 'danger'
      }
      return statusMap[status] || ''
    },
    
    getPropertyUsageType(type) {
      const typeMap = {
        '自住': 'success',
        '出租': 'warning',
        '闲置': 'info'
      }
      return typeMap[type] || ''
    }
  }
}
</script>

<style scoped>
.data-management {
  padding: 20px;
  height: calc(100vh - 84px); /* 使用height替代min-height确保填满屏幕 */
  overflow-y: auto;
}

.data-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 18px;
  color: #303133;
}

.chart-container {
  padding: 20px 0;
}

.chart-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 300px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  text-align: center;
  width: 100%;
  display: block;
}

.common-pagination {
  margin-top: 20px;
  text-align: center;
}

.el-dialog__header {
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  padding: 15px 20px;
  font-weight: bold;
}

.el-dialog__body {
  padding: 20px;
}

.el-dialog__footer {
  border-top: 1px solid #ebeef5;
  padding: 15px 20px;
}

/* 自定义标签页样式 */
.custom-tabs :deep(.el-tabs__item) {
  font-weight: 500;
  color: #606266;
  transition: all 0.3s ease;
}

.custom-tabs :deep(.el-tabs__item:hover) {
  color: #409EFF;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #409EFF;
  font-weight: bold;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  background-color: #409EFF;
  height: 3px;
  border-radius: 3px;
}

/* 表格样式优化 */
.common-table-wrapper {
  margin-top: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
}

.common-table-wrapper:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
}

.common-table :deep(.el-table__header-wrapper) {
  background-color: #f0f2f5;
}

.common-table :deep(.el-table__header-wrapper th) {
  font-weight: bold;
  color: #303133;
  background-color: #f0f2f5;
  border-bottom: 2px solid #409EFF;
  height: 48px;
  line-height: 48px;
  padding: 0;
}

.common-table :deep(.el-table__body-wrapper tr) {
  transition: all 0.3s ease;
  height: 48px;
}

.common-table :deep(.el-table__body-wrapper tr:hover) {
  background-color: #f5f7fa;
  transform: translateY(0);
}

.common-table :deep(.el-table__body-wrapper .el-table__cell) {
  padding: 12px 8px;
  vertical-align: middle;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s ease;
}

.common-table :deep(.el-table__body-wrapper .el-table__cell:hover) {
  background-color: rgba(64, 158, 255, 0.05);
}

/* 优化标签样式 */
.common-table :deep(.el-tag) {
  margin: 0;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 12px;
  font-weight: 500;
}

/* 优化表格边框 */
.common-table :deep(.el-table__inner-wrapper) {
  border-radius: 6px;
  overflow: hidden;
}

.common-table :deep(.el-table--border .el-table__cell) {
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.common-table :deep(.el-table--border .el-table__header-wrapper th:first-child) {
  border-left: 1px solid #ebeef5;
}

.common-table :deep(.el-table--border .el-table__body-wrapper tr:first-child .el-table__cell) {
  border-top: 1px solid #ebeef5;
}

/* 优化操作按钮 */
.common-table :deep(.el-button) {
  margin: 0 4px;
  padding: 6px 16px;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.common-table :deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 按钮样式优化 */
:deep(.el-button) {
  border-radius: 4px;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 加载样式优化 */
:deep(.el-loading-spinner) {
  font-size: 14px;
}

:deep(.el-loading-text) {
  color: #409EFF;
}
</style>