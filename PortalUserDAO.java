/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.softpay.pvi.portal.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import mobi.softpay.pvi.portal.model.form.PortalUserForm;
import mobi.softpay.pvi.common.beans.Users;
import mobi.softpay.pvi.common.enumeration.ScopeDataType;
import mobi.softpay.pvi.portal.security.AuthorizedService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class PortalUserDAO extends DAOBase<Users> {
    
    @Autowired
    protected AuthorizedService authorizedService;

    public long checkUserExist(PortalUserForm form){
        Criteria criteria = this.getCriteria();	
        criteria.add(Restrictions.eq("username", form.getUsername()).ignoreCase());
        if(form.getId()!=null) {
            criteria.add(Restrictions.eq("id", form.getId()));
        }
        criteria.setMaxResults(2);
        return criteria.list().size();
    }
    
    public long checkEmailExist(PortalUserForm form){
        Criteria criteria = this.getCriteria();	
        criteria.add(Restrictions.eq("email", form.getEmail()));
        if(form.getId()!=null) {
            criteria.add(Restrictions.eq("id", form.getId()));
        }
        criteria.setMaxResults(2);
        return criteria.list().size();
    }
    
    public long checkMobileExist(PortalUserForm form){
        Criteria criteria = this.getCriteria();	
        criteria.add(Restrictions.eq("mobile", form.getMobile()));
        if(form.getId()!=null) {
            criteria.add(Restrictions.eq("id", form.getId()));
        }
        criteria.setMaxResults(2);
        return criteria.list().size();
    }
    
    public Criteria getCriteriaBySearchForm(PortalUserForm searchForm) {
            Criteria criteria = this.getCriteria();
            criteria.createAlias("agent", "agent", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("agent.company", "company_agent", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("role", "role", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("department", "department", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("department.company", "company_department", JoinType.LEFT_OUTER_JOIN);
            if(!StringUtils.isBlank(searchForm.getTxtSearch())) {
                criteria.add(Restrictions.or(
                    Restrictions.ilike("name", searchForm.getTxtSearch(), MatchMode.ANYWHERE),
                    Restrictions.ilike("email", searchForm.getTxtSearch(), MatchMode.ANYWHERE),
                    Restrictions.ilike("mobile", searchForm.getTxtSearch(), MatchMode.ANYWHERE),
                    Restrictions.ilike("username", searchForm.getTxtSearch(), MatchMode.ANYWHERE)
                ));		
            }
            if(searchForm.getStatus()!=null) {
                criteria.add(Restrictions.eq("status", searchForm.getStatus()));
            }				
            if(searchForm.getUserType()!= null) {
                criteria.add(Restrictions.eq("userType", searchForm.getUserType()));
            }
            if(searchForm.getAgentId()!= null && searchForm.getDepartmentId()!= null) {
                criteria.add(Restrictions.or(
                    Restrictions.eq("agent.id", searchForm.getAgentId()),
                    Restrictions.eq("department.id", searchForm.getDepartmentId())
                ));
            } else {
                if(searchForm.getAgentId()!= null) {
                    criteria.add(Restrictions.eq("agent.id", searchForm.getAgentId()));
                }
                if(searchForm.getDepartmentId()!= null) {
                    criteria.add(Restrictions.eq("department.id", searchForm.getDepartmentId()));
                }
            }
            if(searchForm.getSaleId()!=null) {
                criteria.add(Restrictions.eq("id", searchForm.getSaleId()));
            }
            if(searchForm.getCompanyId()!=null) {
                criteria.add(Restrictions.or(
                    Restrictions.eq("agent.company.id", searchForm.getCompanyId()),
                    Restrictions.eq("department.company.id", searchForm.getCompanyId())
                ));
            }
            
            
            if(authorizedService.getCurrentUser().getRole() != null){
                switch(authorizedService.getCurrentUser().getRole().getScope()) {
                    case GROUP:
                        break;
                    case COMPANY:
                        criteria.add(Restrictions.ne("role.scope", ScopeDataType.GROUP));
                        criteria.add(Restrictions.or(
                            Restrictions.eq("agent.company.id", authorizedService.getCurrentUser().getCompanyId()),
                            Restrictions.eq("department.company.id", authorizedService.getCurrentUser().getCompanyId())
                        ));
                        break;
                    case DEPARTMENT:
                        criteria.add(Restrictions.and(
                            Restrictions.ne("role.scope", ScopeDataType.GROUP),
                            Restrictions.ne("role.scope", ScopeDataType.COMPANY)
                        ));
                        criteria.add(Restrictions.or(
                            Restrictions.eq("agent.id", authorizedService.getCurrentUser().getAgentId()),
                            Restrictions.eq("department.id", authorizedService.getCurrentUser().getDepartmentId())
                        ));
                        break;
                    case PERSON:
                        criteria.add(Restrictions.eq("role.scope", ScopeDataType.PERSON));
                        break;
                    default:
                        break;
                }
            }
            
            if(!StringUtils.isBlank(authorizedService.getCurrentUser().getRoleName())){
                switch(authorizedService.getCurrentUser().getRoleName()) {
                    case "SUPER_ADMIN":
                        break;
                    default:
                        // neu role < SUPER_ADMIN-> khong hien thi SUPER_ADMIN
                        criteria.add(Restrictions.ne("role.code", "SUPER_ADMIN"));
                        break;
                }
            }

            return criteria;
    }


    public List<Users> getListPortalUserBySearchForm(PortalUserForm searchForm){
            Criteria criteria = this.getCriteriaBySearchForm(searchForm);
            this.applySearchFormToCriteria(criteria, searchForm);
            return criteria.list();
    }

    public long countPortalUserBySearchForm(PortalUserForm searchForm){
            Criteria criteria = this.getCriteriaBySearchForm(searchForm);
            return this.countBySearchForm(criteria, searchForm);
    }

    public Users findByUsername(String username) {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("username", username).ignoreCase());
            criteria.setMaxResults(1);
            return (Users) criteria.uniqueResult();
    }

    public Users findByUsernamePass(String username, String pass) {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", pass));
            return (Users) criteria.uniqueResult();
    }

    public Users findByUserLogin(String username) {			
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("username", username));
            return (Users)criteria.uniqueResult();
    }

    public Users findActivationByToken(String token) {
        Criteria criteria = currentSession().createCriteria(Users.class);
        criteria.add(Restrictions.eq("token", token));
        return (Users) criteria.uniqueResult();
    }

    public Users findByToken(String token) {
        Criteria criteria = getCriteria();
        criteria.createAlias("token", "token");
        criteria.add(Restrictions.eq("token.token", token));
        return (Users) criteria.uniqueResult();
    }
}
