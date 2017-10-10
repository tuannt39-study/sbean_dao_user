/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.softpay.pvi.common.beans;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import mobi.softpay.pvi.common.enumeration.ScopeDataType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import mobi.softpay.pvi.common.enumeration.UserStatusEnum;
import mobi.softpay.pvi.common.enumeration.UserTypeEnum;

@SuppressWarnings("serial")
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "USERS")
public class Users extends BasicTable {

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Column(name = "CREATD_BY")
	private String createdBy;

	@Column(name = "MODIFIED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE", referencedColumnName = "CODE")
	private PortalRole role;

	@Column(name = "USERNAME")
	public String username;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private UserStatusEnum status;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "MOBILE")
	private String mobile;

	@Column(name = "NAME")
	private String name;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "USER_TYPE")
	@Enumerated(EnumType.STRING)
	private UserTypeEnum userType;
    
	@ManyToOne(fetch = FetchType.LAZY)
	private Department department;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent agent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Transaction> listTransaction;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Bill> listBill;

	public UserTypeEnum getUserType() {
		return userType;
	}

	public void setUserType(UserTypeEnum userType) {
		this.userType = userType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Transaction> getListTransaction() {
		return listTransaction;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setListTransaction(List<Transaction> listTransaction) {
		this.listTransaction = listTransaction;
	}

	public List<Bill> getListBill() {
		return listBill;
	}

	public void setListBill(List<Bill> listBill) {
		this.listBill = listBill;
	}

	public PortalRole getRole() {
		return role;
	}

	public void setRole(PortalRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserStatusEnum getStatus() {
		return status;
	}

	public void setStatus(UserStatusEnum status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
