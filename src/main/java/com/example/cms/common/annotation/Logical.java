package com.example.cms.common.annotation;

/**
 * 逻辑关系枚举
 * 用于定义角色或权限之间的逻辑关系
 */
public enum Logical {
    /**
     * 逻辑或关系，只要满足其中一个条件即可
     */
    OR,
    
    /**
     * 逻辑与关系，必须满足所有条件
     */
    AND
}
