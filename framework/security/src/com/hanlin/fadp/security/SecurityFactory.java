
package com.hanlin.fadp.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.http.HttpSession;

import com.hanlin.fadp.base.util.Assert;
import com.hanlin.fadp.base.util.Debug;
import com.hanlin.fadp.base.util.UtilMisc;
import com.hanlin.fadp.base.util.cache.UtilCache;
import com.hanlin.fadp.entity.Delegator;
import com.hanlin.fadp.entity.GenericEntityException;
import com.hanlin.fadp.entity.GenericValue;
import com.hanlin.fadp.entity.condition.EntityCondition;
import com.hanlin.fadp.entity.condition.EntityConditionList;
import com.hanlin.fadp.entity.condition.EntityExpr;
import com.hanlin.fadp.entity.condition.EntityOperator;
import com.hanlin.fadp.entity.util.EntityQuery;
import com.hanlin.fadp.entity.util.EntityUtil;

/**
 * A <code>Security</code> factory.
 */
public final class SecurityFactory {

    public static final String module = SecurityFactory.class.getName();
    // The default implementation stores a Delegator reference, so we will cache by delegator name.
    // The goal is to remove Delegator references in the Security interface, then we can use a singleton
    // and eliminate the cache.
    private static final UtilCache<String, Security> authorizationCache = UtilCache.createUtilCache("security.AuthorizationCache");

    /**
     * Returns a <code>Security</code> instance. The method uses Java's
     * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html"><code>ServiceLoader</code></a>
     * to get a <code>Security</code> instance. If no instance is found, a default implementation is used.
     * The default implementation is based on/backed by the OFBiz entity engine.
     *
     * @param delegator The delegator
     * @return A <code>Security</code> instance
     */
    @SuppressWarnings("deprecation")
    public static Security getInstance(Delegator delegator) throws SecurityConfigurationException {
        Assert.notNull("delegator", delegator);
        Security security = authorizationCache.get(delegator.getDelegatorName());
        if (security == null) {
        	/**
        	 * SPI 实现
        	 */
            Iterator<Security> iterator = ServiceLoader.load(Security.class).iterator();
            if (iterator.hasNext()) {
                security = iterator.next();
            } else {
                security = new OFBizSecurity();
            }
            security.setDelegator(delegator);
            security = authorizationCache.putIfAbsentAndGet(delegator.getDelegatorName(), security);
            if (Debug.verboseOn()) {
                Debug.logVerbose("Security implementation " + security.getClass().getName() + " created for delegator " + delegator.getDelegatorName(), module);
            }
        }
        return security;
    }

    private SecurityFactory() {}

    private static final class OFBizSecurity implements Security {

        private Delegator delegator = null;

        protected static final Map<String, Map<String, String>> simpleRoleEntity = UtilMisc.toMap(
            "ORDERMGR", UtilMisc.<String, String>toMap("name", "OrderRole", "pkey", "orderId"),
            "FACILITY", UtilMisc.<String, String>toMap("name", "FacilityParty", "pkey", "facilityId"),
            "MARKETING", UtilMisc.<String, String>toMap("name", "MarketingCampaignRole", "pkey", "marketingCampaignId"));

        private OFBizSecurity() {}

        @Override
        public void clearUserData(GenericValue userLogin) {
            if (userLogin != null) {
                delegator.getCache().remove("UserLoginSecurityGroup", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
            }
        }

        /**
         * 根据 userLoginId, 查找 securityGroup.
         */
        public Iterator<GenericValue> findUserLoginSecurityGroupByUserLoginId(String userLoginId) {
            try {
                List<GenericValue> collection = EntityUtil.filterByDate(EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", userLoginId).cache(true).queryList());
                return collection.iterator();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                return Collections.<GenericValue>emptyList().iterator();
            }
        }

        @Override
        public Delegator getDelegator() {
            return this.delegator;
        }

        /**
         * @param entity 应该翻译成应用, 例如：partymgr, webtools.
         * @param action 允许的操作.
         */
        public boolean hasEntityPermission(String entity, String action, GenericValue userLogin) {
            if (userLogin == null || entity == null || action == null) return false;
            /**
             * 实际的权限.
             */
            String permission = entity.concat(action);
            /**
             * 超级权限.
             */
            String adminPermission = entity.concat("_ADMIN");
            Iterator<GenericValue> iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
            /**
             * 首先判断用户是否有 实际权限，如果没有，那么看有没有超级权限，这个思路是合理的.
             */
            while (iterator.hasNext()) {
                GenericValue userLoginSecurityGroup = iterator.next();
                if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), permission))
                    return true;
                if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), adminPermission))
                    return true;
            }

            return false;
        }

        /**
         * 重载方法.
         */
        public boolean hasEntityPermission(String entity, String action, HttpSession session) {
            if (session == null) {
                return false;
            }
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin == null) {
                return false;
            }
            return hasEntityPermission(entity, action, userLogin);
        }

        /**
         * 是否有某种权限
         */
        public boolean hasPermission(String permission, GenericValue userLogin) {
            if (userLogin == null) {
                return false;
            }
            Iterator<GenericValue> iterator = findUserLoginSecurityGroupByUserLoginId(userLogin.getString("userLoginId"));
            GenericValue userLoginSecurityGroup = null;
            while (iterator.hasNext()) {
                userLoginSecurityGroup = iterator.next();
                /**
                 * 他的这个方法很巧妙, 他是通过用 groupId 和 permission 共同构筑构造 securityGroupPermission
                 */
                if (securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), permission)) return true;
            }
            return false;
        }

        /**
         * 是否有某种权限
         */
        public boolean hasPermission(String permission, HttpSession session) {
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin == null) {
                return false;
            }
            return hasPermission(permission, userLogin);
        }

        /**
         * Like hasEntityPermission above, this checks the specified action, as well as for "_ADMIN" to allow for simplified
         * general administration permission, but also checks action_ROLE and validates the user is a member for the
         * application.
         *
         * @param application The name of the application corresponding to the desired permission.
         * @param action The action on the application corresponding to the desired permission.
         * @param entityName The name of the role entity to use for validation.
         * @param condition EntityCondition used to query the entityName.
         * @param userLogin The userLogin object for user to check against.
         * @return Returns true if the currently logged in userLogin has the specified permission, otherwise returns false.
         */
        /**
         * 
         * @param application 应用名称
         * @param action 操作
         * @param entityName 实体名称
         * @param condition 查询实体的条件
         * @param userLogin 用户 id
         * @return 这个方法表达的意思是：假设有这么一个权限 ORDER_ROLE_VIEW, 通过实体 OrderRole 和订单
         * 关联起来, 那么用户就有权限查看特定订单.
         * 实际中：一个用户拥有查看自己订单的权限, 这是合理的.
         */
        private boolean hasRolePermission(String application, String action, String entityName, EntityCondition condition, GenericValue userLogin) {
            if (userLogin == null) {
                return false;
            }
            // first check the standard permission
            if (hasEntityPermission(application, action, userLogin)) {
                return true;
            }
            // make sure we have what's needed for role security
            if (entityName == null || condition == null) {
                return false;
            }
            // now check the user for the role permission
            if (hasEntityPermission(application + "_ROLE", action, userLogin)) {
                // we have the permission now, we check to make sure we are allowed access
                try {
                    List<GenericValue> roleTest = EntityQuery.use(delegator).from(entityName).where(condition).queryList();
                    if (!roleTest.isEmpty()) {
                        return true;
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems doing role security lookup on entity [" + entityName + "] using [" + condition + "]", module);
                    return false;
                }
            }
            return false;
        }

        /**
         * @param application 应用名称
         * @param action 操作
         * @param primaryKey 主键
         * @param roles 应该是 roleTypeId 的集合
         * @param userLogin 用户实体
         * 限制到了三张表：OrderRole, FacilityParty, MarketingCampaignRole
         */
        public boolean hasRolePermission(String application, String action, String primaryKey, List<String> roles, GenericValue userLogin) {
            if (userLogin == null) {
                return false;
            }
            if (primaryKey.equals("") && roles == null) {
                if (hasEntityPermission(application, action, userLogin)) return true;
                if (hasEntityPermission(application + "_ROLE", action, userLogin)) return true;
            }
            
            String entityName = null;
            EntityCondition condition = null;
            Map<String, String> simpleRoleMap = OFBizSecurity.simpleRoleEntity.get(application);
            if (simpleRoleMap != null && roles != null) {//FIXME: cl 2016-01-6 这里将限制只有 订单, 设备, 市场 这三个模块有权限 ?
                entityName = simpleRoleMap.get("name"); //FIXME: OrderRole, FacilityParty, MarketingCampaignRole
                String pkey = simpleRoleMap.get("pkey");
                if (pkey != null) {
                    List<EntityExpr> expressions = new ArrayList<EntityExpr>();
                    for (String role: roles) {
                        expressions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, role));
                    }
                    EntityConditionList<EntityExpr> exprList = EntityCondition.makeCondition(expressions, EntityOperator.OR);
                    EntityExpr keyExpr = EntityCondition.makeCondition(pkey, primaryKey);
                    EntityExpr partyExpr = EntityCondition.makeCondition("partyId", userLogin.getString("partyId"));
                    condition = EntityCondition.makeCondition(exprList, keyExpr, partyExpr);
                }

            }
            /**
             * 如果说：没有进 if 判断的话, 那么这里，必将返回 false.
             */
            return hasRolePermission(application, action, entityName, condition, userLogin);
        }

        @Override
        public boolean hasRolePermission(String application, String action, String primaryKey, List<String> roles, HttpSession session) {
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            return hasRolePermission(application, action, primaryKey, roles, userLogin);
        }

        @Override
        public boolean hasRolePermission(String application, String action, String primaryKey, String role, GenericValue userLogin) {
            List<String> roles = null;
            if (role != null && !role.equals("")) {
                roles = UtilMisc.toList(role);
            }
            return hasRolePermission(application, action, primaryKey, roles, userLogin);
        }

        @Override
        public boolean hasRolePermission(String application, String action, String primaryKey, String role, HttpSession session) {
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            return hasRolePermission(application, action, primaryKey, role, userLogin);
        }

        /**
         * 返回是否具有某种权限(securityGroupPermission whether exit or not.)
         */
        public boolean securityGroupPermissionExists(String groupId, String permission) {
            try {
                return EntityQuery.use(delegator).from("SecurityGroupPermission").where("groupId", groupId, "permissionId", permission).cache(true).queryOne() != null;
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                return false;
            }
        }

        /**
         * 设置 Delegator
         */
        public void setDelegator(Delegator delegator) {
            if (this.delegator != null) {
                throw new IllegalStateException("This object has been initialized already.");
            }
            Assert.notNull("delegator", delegator);
            this.delegator = delegator;
        }
    }
}
