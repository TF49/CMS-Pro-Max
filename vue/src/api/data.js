import request from '@/utils/request'

// 获取户籍数据
export function getHouseholdData(page = 1, size = 10) {
  return request({
    url: '/household/list',
    method: 'get',
    params: { page, size }
  })
}

// 根据ID获取户籍
export function getHouseholdById(id) {
  return request({
    url: `/household/${id}`,
    method: 'get'
  })
}

// 根据户籍编号获取户籍
export function getHouseholdByNumber(householdNumber) {
  return request({
    url: `/household/number/${householdNumber}`,
    method: 'get'
  })
}

// 搜索户籍数据
export function searchHouseholds(params) {
  return request({
    url: '/household/search',
    method: 'get',
    params
  })
}

// 添加户籍
export function addHousehold(data) {
  return request({
    url: '/household/add',
    method: 'post',
    data
  })
}

// 更新户籍
export function updateHousehold(data) {
  return request({
    url: '/household/update',
    method: 'put',
    data
  })
}

// 删除户籍
export function deleteHousehold(id) {
  return request({
    url: `/household/delete/${id}`,
    method: 'delete'
  })
}

// 获取户籍数据总数
export function getHouseholdCount() {
  return request({
    url: '/household/count',
    method: 'get'
  })
}

// 获取居民数据（支持分页）
export function getResidentData(page = 1, size = 10) {
  return request({
    url: '/resident/list',
    method: 'get',
    params: { page, size }
  })
}

// 根据ID获取居民
export function getResidentById(id) {
  return request({
    url: `/resident/${id}`,
    method: 'get'
  })
}

// 根据身份证号获取居民
export function getResidentByIdCard(idCard) {
  return request({
    url: `/resident/idCard/${idCard}`,
    method: 'get'
  })
}

// 根据姓名获取居民
export function getResidentByName(name) {
  return request({
    url: `/resident/name/${name}`,
    method: 'get'
  })
}

// 根据户籍ID获取居民列表
export function getResidentsByHouseholdId(householdId) {
  return request({
    url: `/resident/household/${householdId}`,
    method: 'get'
  })
}

// 搜索居民数据
export function searchResidents(params) {
  return request({
    url: '/resident/search',
    method: 'get',
    params
  })
}

// 添加居民
export function addResident(data) {
  return request({
    url: '/resident/add',
    method: 'post',
    data
  })
}

// 更新居民
export function updateResident(data) {
  return request({
    url: '/resident/update',
    method: 'put',
    data
  })
}

// 删除居民
export function deleteResident(id) {
  return request({
    url: `/resident/delete/${id}`,
    method: 'delete'
  })
}

// 获取居民数据总数
export function getResidentCount() {
  return request({
    url: '/resident/count',
    method: 'get'
  })
}

// 获取教育数据（支持分页）
export function getEducationData(page = 1, size = 10) {
  return request({
    url: '/education/list',
    method: 'get',
    params: { page, size }
  })
}

// 搜索教育数据
export function searchEducation(params) {
  return request({
    url: '/education/search',
    method: 'get',
    params
  })
}

// 根据ID获取教育数据
export function getEducationById(id) {
  return request({
    url: `/education/${id}`,
    method: 'get'
  })
}

// 添加教育数据
export function addEducation(data) {
  return request({
    url: '/education/add',
    method: 'post',
    data
  })
}

// 更新教育数据
export function updateEducation(data) {
  return request({
    url: '/education/update',
    method: 'put',
    data
  })
}

// 删除教育数据
export function deleteEducation(id) {
  return request({
    url: `/education/delete/${id}`,
    method: 'delete'
  })
}

// 获取教育数据总数
export function getEducationCount() {
  return request({
    url: '/education/count',
    method: 'get'
  })
}

// 获取医疗数据（支持分页）
export function getMedicalData(page = 1, size = 10) {
  return request({
    url: '/medical/list',
    method: 'get',
    params: { page, size }
  })
}

// 搜索医疗数据
export function searchMedical(params) {
  return request({
    url: '/medical/search',
    method: 'get',
    params
  })
}

// 根据ID获取医疗数据
export function getMedicalById(id) {
  return request({
    url: `/medical/${id}`,
    method: 'get'
  })
}

// 添加医疗数据
export function addMedical(data) {
  return request({
    url: '/medical/add',
    method: 'post',
    data
  })
}

// 更新医疗数据
export function updateMedical(data) {
  return request({
    url: '/medical/update',
    method: 'put',
    data
  })
}

// 删除医疗数据
export function deleteMedical(id) {
  return request({
    url: `/medical/delete/${id}`,
    method: 'delete'
  })
}

// 获取医疗数据总数
export function getMedicalCount() {
  return request({
    url: '/medical/count',
    method: 'get'
  })
}

// 获取就业数据（支持分页）
export function getEmploymentData(page = 1, size = 10) {
  return request({
    url: '/employment/list',
    method: 'get',
    params: { page, size }
  })
}

// 根据ID获取就业数据
export function getEmploymentById(id) {
  return request({
    url: `/employment/${id}`,
    method: 'get'
  })
}

// 根据居民ID获取就业数据
export function getEmploymentsByResidentId(residentId) {
  return request({
    url: `/employment/resident/${residentId}`,
    method: 'get'
  })
}

// 搜索就业数据
export function searchEmployments(params) {
  return request({
    url: '/employment/search',
    method: 'get',
    params
  })
}

// 添加就业数据
export function addEmployment(data) {
  return request({
    url: '/employment/add',
    method: 'post',
    data
  })
}

// 更新就业数据
export function updateEmployment(data) {
  return request({
    url: '/employment/update',
    method: 'put',
    data
  })
}

// 删除就业数据
export function deleteEmployment(id) {
  return request({
    url: `/employment/delete/${id}`,
    method: 'delete'
  })
}

// 获取就业数据总数
export function getEmploymentCount() {
  return request({
    url: '/employment/count',
    method: 'get'
  })
}

// 获取社保数据（支持分页）
export function getSocialSecurityData(page = 1, size = 10) {
  return request({
    url: '/social-security/list',
    method: 'get',
    params: { page, size }
  })
}

// 根据ID获取社保数据
export function getSocialSecurityById(id) {
  return request({
    url: `/social-security/${id}`,
    method: 'get'
  })
}

// 根据居民ID获取社保数据
export function getSocialSecuritiesByResidentId(residentId) {
  return request({
    url: `/social-security/resident/${residentId}`,
    method: 'get'
  })
}

// 搜索社保数据
export function searchSocialSecurities(params) {
  return request({
    url: '/social-security/search',
    method: 'get',
    params
  })
}

// 添加社保数据
export function addSocialSecurity(data) {
  return request({
    url: '/social-security/add',
    method: 'post',
    data
  })
}

// 更新社保数据
export function updateSocialSecurity(data) {
  return request({
    url: '/social-security/update',
    method: 'put',
    data
  })
}

// 删除社保数据
export function deleteSocialSecurity(id) {
  return request({
    url: `/social-security/delete/${id}`,
    method: 'delete'
  })
}

// 获取社保数据总数
export function getSocialSecurityCount() {
  return request({
    url: '/social-security/count',
    method: 'get'
  })
}

// 获取房产数据（支持分页）
export function getPropertyData(page = 1, size = 10) {
  return request({
    url: '/property/list',
    method: 'get',
    params: { page, size }
  })
}

// 根据ID获取房产数据
export function getPropertyById(id) {
  return request({
    url: `/property/${id}`,
    method: 'get'
  })
}

// 根据居民ID获取房产数据
export function getPropertiesByResidentId(residentId) {
  return request({
    url: `/property/resident/${residentId}`,
    method: 'get'
  })
}

// 搜索房产数据
export function searchProperties(params) {
  return request({
    url: '/property/search',
    method: 'get',
    params
  })
}

// 添加房产数据
export function addProperty(data) {
  return request({
    url: '/property/add',
    method: 'post',
    data
  })
}

// 更新房产数据
export function updateProperty(data) {
  return request({
    url: '/property/update',
    method: 'put',
    data
  })
}

// 删除房产数据
export function deleteProperty(id) {
  return request({
    url: `/property/delete/${id}`,
    method: 'delete'
  })
}

// 获取房产数据总数
export function getPropertyCount() {
  return request({
    url: '/property/count',
    method: 'get'
  })
}

// 获取车辆数据（支持分页和搜索）
export function getVehicleData(pageOrParams = 1, size = 10) {
  let params = {};
  if (typeof pageOrParams === 'object') {
    // 接受对象参数的情况，如 { page: 1, size: 10, ...searchParams }
    params = pageOrParams;
  } else {
    // 接受独立参数的情况，如 getVehicleData(page, size)
    params = { page: pageOrParams, size };
  }
  return request({
    url: '/vehicle/list',
    method: 'get',
    params
  })
}

// 根据ID获取车辆数据
export function getVehicleById(id) {
  return request({
    url: `/vehicle/${id}`,
    method: 'get'
  })
}

// 根据居民ID获取车辆数据
export function getVehiclesByResidentId(residentId) {
  return request({
    url: `/vehicle/resident/${residentId}`,
    method: 'get'
  })
}

// 搜索车辆数据
export function searchVehicles(params) {
  return request({
    url: '/vehicle/search',
    method: 'get',
    params
  })
}

// 添加车辆数据
export function addVehicle(data) {
  return request({
    url: '/vehicle/add',
    method: 'post',
    data
  })
}

// 更新车辆数据
export function updateVehicle(data) {
  return request({
    url: '/vehicle/update',
    method: 'put',
    data
  })
}

// 删除车辆数据
export function deleteVehicle(id) {
  return request({
    url: `/vehicle/delete/${id}`,
    method: 'delete'
  })
}

// 获取车辆数据总数
export function getVehicleCount() {
  return request({
    url: '/vehicle/count',
    method: 'get'
  })
}