package com.abc.wxctrl.utility;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtraVO {

	@Transient
	private Map<String, Object> extra = new LinkedHashMap<>();
}
