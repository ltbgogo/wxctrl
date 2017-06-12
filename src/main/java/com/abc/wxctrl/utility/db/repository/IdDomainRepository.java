package com.abc.wxctrl.utility.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.abc.wxctrl.utility.db.domain.IdDomain;

@NoRepositoryBean
public interface IdDomainRepository<D extends IdDomain> extends JpaRepository<D, String>, JpaSpecificationExecutor<D> {

}




