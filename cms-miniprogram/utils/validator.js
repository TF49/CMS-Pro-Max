/**
 * 表单验证工具类
 */

// 验证规则
const rules = {
    // 手机号验证
    phone: /^1[3-9]\d{9}$/,
    // 身份证号验证 (简单验证)
    idCard: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
    // 邮箱验证
    email: /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/,
    // 整数验证
    integer: /^\d+$/,
    // 密码验证 (6-20位)
    password: /^.{6,20}$/,
    // 用户名验证 (3-20位)
    username: /^.{3,20}$/,
    // 日期格式验证 (YYYY-MM-DD)
    date: /^\d{4}-\d{2}-\d{2}$/
}

export default {
    /**
     * 验证手机号
     * @param {string} value 手机号
     * @returns {boolean}
     */
    phone(value) {
        return rules.phone.test(value)
    },

    /**
     * 验证手机号（别名）
     * @param {string} value 手机号
     * @returns {boolean}
     */
    isMobile(value) {
        return this.phone(value)
    },

    /**
     * 验证身份证号
     * @param {string} value 身份证号
     * @returns {boolean}
     */
    idCard(value) {
        return rules.idCard.test(value)
    },

    /**
     * 验证身份证号（别名）
     * @param {string} value 身份证号
     * @returns {boolean}
     */
    isIdCard(value) {
        return this.idCard(value)
    },

    /**
     * 验证邮箱
     * @param {string} value 邮箱
     * @returns {boolean}
     */
    email(value) {
        return rules.email.test(value)
    },

    /**
     * 验证邮箱（别名）
     * @param {string} value 邮箱
     * @returns {boolean}
     */
    isEmail(value) {
        return this.email(value)
    },

    /**
     * 验证是否为整数
     * @param {string|number} value 
     * @returns {boolean}
     */
    integer(value) {
        return rules.integer.test(value)
    },

    /**
     * 验证是否为整数（别名）
     * @param {string|number} value 
     * @returns {boolean}
     */
    isInteger(value) {
        return this.integer(value)
    },

    /**
     * 验证密码格式
     * @param {string} value 
     * @returns {boolean}
     */
    password(value) {
        return rules.password.test(value)
    },

    /**
     * 验证密码格式（别名）
     * @param {string} value 
     * @returns {boolean}
     */
    isPassword(value) {
        return this.password(value)
    },

    /**
     * 验证用户名格式
     * @param {string} value 
     * @returns {boolean}
     */
    username(value) {
        return rules.username.test(value)
    },

    /**
     * 验证用户名格式（别名）
     * @param {string} value 
     * @returns {boolean}
     */
    isUsername(value) {
        return this.username(value)
    },

    /**
     * 验证日期格式 (YYYY-MM-DD)
     * @param {string} value 日期字符串
     * @returns {boolean}
     */
    date(value) {
        return rules.date.test(value)
    },

    /**
     * 必填项验证
     * @param {any} value 
     * @returns {boolean}
     */
    required(value) {
        if (value === undefined || value === null) return false
        if (typeof value === 'string' && value.trim() === '') return false
        if (Array.isArray(value) && value.length === 0) return false
        return true
    }
}
