package com.abc.test.domain;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class IdDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "id", length = 48)
	private String id;

	@PrePersist
	protected void prePersist() {
		if (StringUtils.isBlank(this.getId())) {
			this.setId(UUID.randomUUID().toString());
		}
	}
}
